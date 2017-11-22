package com.satishnagc.myretail.product

import com.satishnagc.myretail.data.ProductDao
import com.satishnagc.myretail.domain.ProductData
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Slf4j
@Service
class CassandraProductService implements ProductDataStoreService{


    @Autowired
    ProductDao productDao


    @Override
    void writeProductDetails(ProductData productData) {
        productDao.insert(productData)
    }

    @Override
    ProductData getProductDetails(ProductData productData) {
        try{
            productDao.selectFirstRecord(productData)
        }catch (Exception ex){
            log.error("Cassandra has resulted in an exception", ex)
        }
    }
}
