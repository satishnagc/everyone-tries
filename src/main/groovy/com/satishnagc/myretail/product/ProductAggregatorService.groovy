package com.satishnagc.myretail.product

import com.satishnagc.myretail.common.RetailAppTimer
import com.satishnagc.myretail.domain.ProductData
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Slf4j
@Service
class ProductAggregatorService {

    @Autowired
    ProductService redSkyProductService

    @Autowired
    ProductDataStoreService productDataStoreService


    def getProductDetails(String productId){

        String productName =  RetailAppTimer.execute(
                ['productId':productId, 'operation': 'getProductName'],{redSkyProductService.getProductName(productId)})

        log.info("ProductId =${productId}; returned productName=${productName}")

        //TODO in case of time, introduce a stale flag, query from only id and send response only from cassandra
        //TODO can implement gpars pool with async call in the above case

        if(productName){
            return RetailAppTimer.execute(
                    ['productId':productId, 'operation': 'getProductFromCassandra'],
                    { productDataStoreService.getProductDetails(new ProductData(
                    productId: productId,
                    productName: productName))}
            )
        }
    }
}
