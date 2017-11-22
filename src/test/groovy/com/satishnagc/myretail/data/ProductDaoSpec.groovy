package com.satishnagc.myretail.data

import com.datastax.driver.core.BoundStatement
import com.datastax.driver.core.PreparedStatement
import com.datastax.driver.core.ResultSet
import com.datastax.driver.core.Row
import com.datastax.driver.core.Session
import com.datastax.driver.core.Statement
import com.fasterxml.jackson.databind.ObjectMapper
import com.satishnagc.myretail.domain.ProductData
import spock.lang.Specification


class ProductDaoSpec extends Specification{
    Session cassandraSession = Mock()

    PreparedStatement selectQuery = Mock()
    PreparedStatement insertProductDataQuery = Mock()

    BoundStatement boundStatement

    ObjectMapper objectMapper = new ObjectMapper()

    ProductDao productDao = new ProductDao(
            cassandraSession: cassandraSession,
            objectMapper: objectMapper
    )


    def "setup"(){

        boundStatement = GroovyMock(global: true)

        1 * cassandraSession.prepare({ Statement select ->
            select.toString() == 'SELECT * ' +
                    'FROM satishnagc_product.product_by_id_Name ' +
                    'WHERE productId=? ' +
                    'AND productName=?;'
        }) >> selectQuery

        1 * cassandraSession.prepare({ Statement insert ->
            insert.toString() == 'INSERT INTO ' +
                    'satishnagc_product.product_by_id_Name (productId,productName,data,timestamp) ' +
                    'VALUES (?,?,?,?);'
        }) >> insertProductDataQuery

        productDao.postConfig()
    }


    def 'getBY Product ID and Product Name'() {
        ResultSet resultSet = Mock()
        Row row = Mock()

        ProductData productData = new ProductData(
                productId: '12345',
                productName: 'Dummy Product Name'
        )

        when:
        ProductData result = productDao.selectFirstRecord(productData)

        then:
        1 * new BoundStatement(selectQuery) >> boundStatement
        1 * boundStatement.bind('12345', 'Dummy Product Name')
        1 * cassandraSession.execute(boundStatement) >> resultSet
        1 * resultSet.all() >> [row]
        1 * row.getString('data') >> '{"value":15.34,"currency_code":"USD"}'
        1 * row.getString('productId') >> 12345
        1 * row.getString('productName') >> 'Dummy Product Name'
        1 * row.getDate('timestamp') >> new Date()
        result?.current_price?.value == 15.34f
        (result?.current_price?.currency_code)?.equalsIgnoreCase('USD')
        ((result?.productId))?.equalsIgnoreCase('12345')
        ((result?.productName))?.equalsIgnoreCase('Dummy Product Name')
        1 * boundStatement.preparedStatement()

        0 * _
    }


    def "check the multiple records select"(){

        ResultSet resultSet = Mock()
        Row row1 = Mock()
        Row row2 = Mock()

        ProductData productData = new ProductData(
                productId: '12345',
                productName: 'Dummy Product Name'
        )

        when:
        ProductData result = productDao.selectFirstRecord(productData)

        then:
        1 * new BoundStatement(selectQuery) >> boundStatement
        1 * boundStatement.bind('12345', 'Dummy Product Name')
        1 * cassandraSession.execute(boundStatement) >> resultSet
        1 * resultSet.all() >> [row1, row2]

        1 * row1.getString('data') >> '{"value":15.34,"currency_code":"USD"}'
        1 * row1.getString('productId') >> 12345
        1 * row1.getString('productName') >> 'Dummy Product Name'
        1 * row1.getDate('timestamp') >> new Date()

        1 * row2.getString('productId') >> 123456
        1 * row2.getString('productName') >> 'Dummy Product Name 2'

        1 * row2.getString('data') >> '{"value":16.34,"currency_code":"GBP"}'
        1 * row2.getDate('timestamp') >> new Date()

        result?.current_price?.value == 15.34f
        (result?.current_price?.currency_code)?.equalsIgnoreCase('USD')
        ((result?.productId))?.equalsIgnoreCase('12345')
        ((result?.productName))?.equalsIgnoreCase('Dummy Product Name')
        1 * boundStatement.preparedStatement()

        0 * _
    }


    def "check the no records select"(){

        ResultSet resultSet = Mock()
        Row row1 = Mock()
        Row row2 = Mock()

        ProductData productData = new ProductData(
                productId: '12345',
                productName: 'Dummy Product Name'
        )

        when:
        ProductData result = productDao.selectFirstRecord(productData)

        then:
        1 * new BoundStatement(selectQuery) >> boundStatement
        1 * boundStatement.bind('12345', 'Dummy Product Name')
        1 * cassandraSession.execute(boundStatement) >> resultSet
        1 * resultSet.all() >> []


        1 * boundStatement.preparedStatement()
        !result
        0 * _
    }


    def "Save product data"() {
        Date timestamp = new Date()
        ProductData productData = new ProductData(productId: '123456', productName: 'product name is dummy',timeStamp: timestamp,current_price: ['value':12.12,'currency_code':'GBP'])


        when:
        productDao.insert(productData)

        then:
        1 * new BoundStatement(insertProductDataQuery) >> boundStatement
        1 * boundStatement.bind('123456', 'product name is dummy',objectMapper.writeValueAsString(productData?.current_price),_)
        1 * cassandraSession.execute(boundStatement)
        1 * boundStatement.preparedStatement() >> insertProductDataQuery
    }
}
