package com.satishnagc.myretail.product

import com.satishnagc.myretail.domain.ProductData


interface ProductDataStoreService {
    void writeProductDetails(ProductData productData)
    ProductData getProductDetails(ProductData productData)
}