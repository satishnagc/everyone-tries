package com.satishnagc.myretail.integration.http

import com.satishnagc.myretail.exception.MyRetailRetryEnabledException
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.HttpServerErrorException
import org.springframework.web.client.RestTemplate
import spock.lang.Specification


class RedSkyHttpClientSpec extends Specification{

    RedSkyHttpClient redSkyHttpClient

    RestTemplate restMockTemplate = Mock(RestTemplate)

    def "setup"(){
        redSkyHttpClient = new RedSkyHttpClient(
                myRetailRestTemplate: restMockTemplate
        )
    }

    def "get product details, exception scenarios, dont retry exceptions"(){

        when:
        redSkyHttpClient.getRedSkyProductDetails(productId)

        then:
        1 * restMockTemplate.exchange(_,HttpMethod.GET,_,Map) >> { throw new HttpClientErrorException(HttpStatus.NOT_FOUND)}
        thrown(exceptionThrown)
        0 * _

        where:
        productId               | resultExpected                                            |   exceptionThrown
//        '123456'                |{throw new Exception()}                                    |   MyRetailRetryEnabledException
//        '123456'                |{throw new HttpClientErrorException()}                     |   MyRetailRetryEnabledException
        '123456'                |{ throw new HttpClientErrorException(HttpStatus.NOT_FOUND)} |   HttpServerErrorException


    }

    def "get product details, exception scenarios, retry exceptions"(){

        when:
        redSkyHttpClient.getRedSkyProductDetails(productId)

        then:
        1 * restMockTemplate.exchange(_,HttpMethod.GET,_,Map) >> { throw new Exception()}
        thrown(exceptionThrown)
        0 * _

        where:
        productId               | resultExpected                                            |   exceptionThrown
        '123456'                |{throw new Exception()}                                    |   MyRetailRetryEnabledException

    }
    def "get product details, exception scenarios, retry exceptions with 4XX"(){

        when:
        redSkyHttpClient.getRedSkyProductDetails(productId)

        then:
        1 * restMockTemplate.exchange(_,HttpMethod.GET,
                { HttpEntity it ->
                    assert it.headers.get('X-REQUEST-ID') == ['myRetail-request_ID']
                    true
                }
                ,Map) >> { throw new HttpClientErrorException()}
        thrown(MyRetailRetryEnabledException)
        0 * _

        where:
        productId                                |   exceptionThrown
        '123456'                                 |   MyRetailRetryEnabledException

    }

    def "get product details, success"(){

        given:
        ResponseEntity mockResponse = new ResponseEntity(['Key': 'Value'],HttpStatus.OK)

        when:
        ResponseEntity responseEntity = redSkyHttpClient.getRedSkyProductDetails('12345')

        then:
        1 * restMockTemplate.exchange(_,HttpMethod.GET,
                { HttpEntity it ->
                    assert it.headers.get('X-REQUEST-ID') == ['myRetail-request_ID']
                    true
                }
                ,Map) >> mockResponse

        responseEntity.statusCode == HttpStatus.OK
        (responseEntity.body?.Key as String)?.equalsIgnoreCase('Value')
        0 * _


    }

}
