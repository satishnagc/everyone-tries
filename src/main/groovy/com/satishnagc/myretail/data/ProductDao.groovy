package com.satishnagc.myretail.data

import com.datastax.driver.core.BoundStatement
import com.datastax.driver.core.PreparedStatement
import com.datastax.driver.core.ResultSet
import com.datastax.driver.core.Row
import com.datastax.driver.core.querybuilder.QueryBuilder
import com.fasterxml.jackson.databind.ObjectMapper
import com.satishnagc.myretail.common.MyRetailConstants
import com.satishnagc.myretail.domain.PriceDetail
import com.satishnagc.myretail.domain.ProductData
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

import static com.datastax.driver.core.querybuilder.QueryBuilder.bindMarker
import static com.datastax.driver.core.querybuilder.QueryBuilder.eq

@Component
@Slf4j
class ProductDao extends CassandraDao{

    static private PreparedStatement selectQuery
    static private PreparedStatement insertQuery


    @Autowired
    ObjectMapper objectMapper


    @PostConstruct
    void postConfig() {

        String keyspace = MyRetailConstants.MYRETAIL_KEYSPACE

        selectQuery = cassandraSession.prepare(QueryBuilder.select()
                .from(keyspace.toString(), MyRetailConstants.TBL_PRODUCT_BY_ID_NAME)
                .where(eq(MyRetailConstants.COL_PRODUCT_ID, bindMarker()))
                .and(eq(MyRetailConstants.COL_PRODUCT_NAME, bindMarker()))
        )


        insertQuery = cassandraSession.prepare(QueryBuilder.insertInto(keyspace.toString(), MyRetailConstants.TBL_PRODUCT_BY_ID_NAME)
                .value(MyRetailConstants.COL_PRODUCT_ID, bindMarker())
                .value(MyRetailConstants.COL_PRODUCT_NAME, bindMarker())
                .value(MyRetailConstants.COL_DATA, bindMarker())
                .value(MyRetailConstants.COL_TIMESTAMP, bindMarker())
        )

    }

    ProductData selectFirstRecord(ProductData productData){
        select(productData)?.first()
    }

    List<ProductData> select(ProductData productData) {
        BoundStatement statement = new BoundStatement(selectQuery)
        statement.bind(productData.productId, productData.productName)
        executeAndGetProductData(statement)
    }

    private List<ProductData> executeAndGetProductData(BoundStatement boundStatement) {
        ResultSet resultSet = execute(boundStatement)

        resultSet.all().collect { Row row->
            ProductData productData = new ProductData()

            productData.productId = row.getString(MyRetailConstants.COL_PRODUCT_ID)
            productData.productName = row.getString(MyRetailConstants.COL_PRODUCT_NAME)
            productData.timeStamp = row.getDate(MyRetailConstants.COL_TIMESTAMP)
            productData.current_price = objectMapper.readValue(row.getString(MyRetailConstants.COL_DATA), PriceDetail)
            productData
        }
    }


    void insert(ProductData productData) {
        BoundStatement boundStatement = new BoundStatement(insertQuery)
        boundStatement.bind(productData.productId,
                productData.productName,
                objectMapper.writeValueAsString(productData.current_price),
                new Date()
        )
        execute(boundStatement)
    }

}
