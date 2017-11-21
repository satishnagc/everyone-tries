package com.satishnagc.myretail.product

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

        String productName = redSkyProductService.getProductName(productId)

        return productDataStoreService.getProductDetails(new ProductData(
                productId: productId,
                productName: productName)
        )

    }
}
