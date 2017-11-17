package com.satishnagc.myretail.product

import org.springframework.http.ResponseEntity


interface ProductService<T> {
    ResponseEntity<T> getProductDetails(String productId)
    String getProductName(String productId)

}