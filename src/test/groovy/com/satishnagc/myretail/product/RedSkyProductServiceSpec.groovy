package com.satishnagc.myretail.product

import com.satishnagc.myretail.exception.MyRetailRetryEnabledException
import com.satishnagc.myretail.integration.http.RedSkyHttpClient
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.client.HttpServerErrorException
import spock.lang.Specification


class RedSkyProductServiceSpec extends  Specification{

    RedSkyProductService redSkyProductService
    RedSkyHttpClient redSkyHttpClient = Mock()

    def "setup"(){
        redSkyProductService = new RedSkyProductService(
                redSkyHttpClient:redSkyHttpClient
        )
    }


    def "get product details, retry enabled,httpServer, general exception"(){

        when:
        def result = redSkyProductService.getProductDetails('1234')

        then:
        1 * redSkyHttpClient.getRedSkyProductDetails('1234') >> {throw exceptionType}
        !resultExpected
        0 * _
        noExceptionThrown()


        where:
        exceptionType                                                  |    resultExpected
        new MyRetailRetryEnabledException()                            |null
        new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR) |null
        new Exception()                                                |null

    }

    def "get product details, no result with non 200"(){

        given:

        ResponseEntity responseEntity = new ResponseEntity(HttpStatus.ALREADY_REPORTED)
        when:
        def result = redSkyProductService.getProductDetails('1234')

        then:
        1 * redSkyHttpClient.getRedSkyProductDetails('1234') >> responseEntity
        !result
        0 * _
    }

    def "get product details, no result with 200"(){

        given:

        ResponseEntity responseEntity = new ResponseEntity(HttpStatus.OK)
        when:
        def result = redSkyProductService.getProductDetails('1234')

        then:
        1 * redSkyHttpClient.getRedSkyProductDetails('1234') >> responseEntity
        result?.statusCode == HttpStatus.OK
        !result?.body
        0 * _
    }


    def "get product name, no data"(){
        given:

        ResponseEntity responseEntity = new ResponseEntity(HttpStatus.OK)
        when:
        def result = redSkyProductService.getProductName('1234')

        then:
        1 * redSkyHttpClient.getRedSkyProductDetails('1234') >> responseEntity
        !result
        0 * _
        noExceptionThrown()

    }

    def "get product name, no data, exception"(){
        when:
        def result = redSkyProductService.getProductName('1234')

        then:
        1 * redSkyHttpClient.getRedSkyProductDetails('1234') >> {throw new Exception()}
        !result
        0 * _
        noExceptionThrown()

    }



    def "get product name,  data, no product name"(){

        given:

        ResponseEntity responseEntity = new ResponseEntity(['product':['item':['product_deacription':['almost':'almostName']]]],
                HttpStatus.OK
        )


        when:
        def result = redSkyProductService.getProductName('1234')

        then:
        1 * redSkyHttpClient.getRedSkyProductDetails('1234') >> responseEntity
        !result
        0 * _
        noExceptionThrown()

    }

    def "get product name,  data,  product name"(){

        given:

        ResponseEntity responseEntity = new ResponseEntity(['product':['item':['product_description':['title':'you got it']]]],
                HttpStatus.OK
        )


        when:
        def result = redSkyProductService.getProductName('1234')

        then:
        1 * redSkyHttpClient.getRedSkyProductDetails('1234') >> responseEntity
        result?.equalsIgnoreCase('you got it')
        0 * _
        noExceptionThrown()

    }

}
