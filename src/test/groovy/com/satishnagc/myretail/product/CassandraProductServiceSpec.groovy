package com.satishnagc.myretail.product

import com.satishnagc.myretail.data.ProductDao
import com.satishnagc.myretail.domain.ProductData
import spock.lang.Specification


class CassandraProductServiceSpec extends Specification{

    CassandraProductService cassandraProductService

    ProductDao productDao = Mock()

    def "setup"(){
        cassandraProductService = new CassandraProductService(
                productDao: productDao
        )
    }


    def "insert method"(){

        given:

        ProductData productData = Mock()
        when:
        cassandraProductService.writeProductDetails(productData)

        then:
        1 * productDao.insert(productData)
        0 * _
    }


    def "select product data, exception"(){

        given:

        ProductData productData = Mock()
        when:
        def result = cassandraProductService.getProductDetails(productData)

        then:
        1 * productDao.selectFirstRecord(productData) >> {throw new Exception()}
        0 * _

        !result
    }

    def "select product data, success"(){

        given:

        ProductData productData = Mock()
        when:
        def result = cassandraProductService.getProductDetails(productData)

        then:
        1 * productDao.selectFirstRecord(productData) >> productData
        0 * _

        result
    }
}
