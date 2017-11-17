package com.satishnagc.myretail.product

import com.satishnagc.myretail.integration.http.RedSkyHttpClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component

@Component
class RedSkyProductService implements ProductService<Map>{

    @Autowired
    RedSkyHttpClient redSkyHttpClient

    @Override
    ResponseEntity getProductDetails(String productId) {
        redSkyHttpClient.getRedSkyProductDetails(productId)
    }

    @Override
    String getProductName(String productId){

        return getProductDetails(productId)?.body?.product?.item?.product_description?.title
    }
}
