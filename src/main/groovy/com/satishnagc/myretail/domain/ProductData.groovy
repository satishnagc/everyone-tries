package com.satishnagc.myretail.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import groovy.transform.ToString

@ToString
class ProductData implements Serializable{

    @JsonProperty(value = 'id')
    String productId

    @JsonProperty(value = 'name')
    String productName

    @JsonIgnore
    String timeStamp


    PriceDetail current_price
}
