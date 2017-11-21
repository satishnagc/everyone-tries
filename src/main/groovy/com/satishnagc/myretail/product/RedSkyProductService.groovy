package com.satishnagc.myretail.product

import com.satishnagc.myretail.integration.http.RedSkyHttpClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component

@Component
class RedSkyProductService implements ProductService<Map>{

    @Autowired
    RedSkyHttpClient redSkyHttpClient

    @Override
    ResponseEntity getProductDetails(String productId) {
        ResponseEntity responseEntity = redSkyHttpClient.getRedSkyProductDetails(productId)
        if(responseEntity?.statusCode == HttpStatus.OK){
            return responseEntity
        }
        return null
    }

    @Override
    String getProductName(String productId){

        return getProductDetails(productId)?.body?.product?.item?.product_description?.title
    }
}
