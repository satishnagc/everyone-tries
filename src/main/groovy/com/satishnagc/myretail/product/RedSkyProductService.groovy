package com.satishnagc.myretail.product

import com.satishnagc.myretail.integration.http.RedSkyHttpClient
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component

@Component
@Slf4j
class RedSkyProductService implements ProductService<Map>{

    @Autowired
    RedSkyHttpClient redSkyHttpClient

    @Override
    ResponseEntity getProductDetails(String productId) {

        try{
            ResponseEntity responseEntity = redSkyHttpClient.getRedSkyProductDetails(productId)
            if(responseEntity?.statusCode == HttpStatus.OK){
                return responseEntity
            }
        }catch (Exception ex){
            log.error("Product details has resulted in Exception", ex)
        }
        null
    }

    @Override
    String getProductName(String productId){

        return getProductDetails(productId)?.body?.product?.item?.product_description?.title
    }
}
