package com.satishnagc.myretail.integration.http

import com.satishnagc.myretail.exception.MyRetailRetryEnabledException
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.HttpServerErrorException
import org.springframework.web.client.RestTemplate

@Component
@Slf4j
class RedSkyHttpClient {

    @Autowired
    RestTemplate myRetailRestTemplate


    /**
     * @input : productId in String
     * @Output : ResponseEntity with Map as body
     * @throws : MyRetailRetryEnabledException after maxAttempts have been exhausted.
     *          HttpServerErrorException with status Code if they are part of list which does not benefit from retry.
     *
     * */


    @Retryable( value = MyRetailRetryEnabledException,
            maxAttempts = 2
    )
    ResponseEntity<Map> getRedSkyProductDetails(String productId){

        ResponseEntity<Map> responseEntity = null
        String xRequest='myRetail-request_ID'
        String url ="http://redsky.target.com/v2/pdp/tcin/${productId}?excludes=taxonomy,price,promotion,bulk_ship,rating_and_review_reviews,rating_and_review_statistics,question_answer_statistics"
        try {
            responseEntity = myRetailRestTemplate.exchange(url, HttpMethod.GET,  new HttpEntity(getHeaders(xRequest))
                    , Map)
        } catch (HttpClientErrorException e) {
            if ([HttpStatus.NOT_FOUND, HttpStatus.GONE].contains(e.statusCode)) {
                log.error("RedSkyHttpClient:: responded with status:${e.statusCode.value()} url=$url, X-REQUEST-ID=$xRequest")
                throw new HttpServerErrorException(e?.statusCode,"RedSkyHttpClient Rest Call Failed with Client Exception.")
            }
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR,"RedSkyHttpClient Rest Call Failed with Client Exception.")

        } catch (Exception e) {
            log.error("RedSkyHttpClient::RestTemplate threw an exception url=$url X-REQUEST-ID=$xRequest", e.message)
            throw new MyRetailRetryEnabledException("RedSkyHttpClient Rest Call Failed.Retry enabled, failed after max attempts")
        }
        responseEntity
    }

    protected HttpHeaders getHeaders(String xRequest){
        HttpHeaders headers = new HttpHeaders()
        headers.set("X-REQUEST-ID", xRequest)
        headers
    }
}
