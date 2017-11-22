package com.satishnagc.myretail.product

import com.satishnagc.myretail.domain.ProductData
import spock.lang.Specification


class ProductAggregatorServiceSpec extends Specification{

    ProductAggregatorService productAggregatorService

    ProductDataStoreService productDataStoreService = Mock()
    ProductService redSkyProductService = Mock()



    def "setup"(){

        productAggregatorService = new ProductAggregatorService(
                productDataStoreService: productDataStoreService,
                redSkyProductService: redSkyProductService
        )
    }


    def "get product details, agg, no productName"(){


        when:
        def result = productAggregatorService.getProductDetails('12345')

        then:
        1 * redSkyProductService.getProductName('12345') >> null
        !result
        0 * _
    }

    def "get product details, agg, no Db data"(){


        when:
        def result = productAggregatorService.getProductDetails('12345')

        then:
        1 * redSkyProductService.getProductName('12345') >> 'testProduct'
        1 * productDataStoreService.getProductDetails(_) >> null
        !result
        0 * _
    }

    def "get product details, agg, b data"(){
        given:

        ProductData productData = Mock()

        when:
        def result = productAggregatorService.getProductDetails('12345')

        then:
        1 * redSkyProductService.getProductName('12345') >> 'testProduct'
        1 * productDataStoreService.getProductDetails(_) >> productData
        result
        0 * _
    }


}
