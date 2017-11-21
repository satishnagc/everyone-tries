package com.satishnagc.myretail.endpoint

import com.fasterxml.jackson.databind.ObjectMapper
import com.satishnagc.myretail.common.RetailAppTimer
import com.satishnagc.myretail.domain.ProductData
import com.satishnagc.myretail.product.ProductAggregatorService
import com.satishnagc.myretail.product.ProductDataStoreService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus

import javax.servlet.http.HttpServletRequest

import static org.springframework.http.HttpStatus.OK

@Controller("products")
@Slf4j
@RequestMapping(value = "/v1")
class MyRetailController {


    @Autowired
    ProductAggregatorService productAggregatorService

    @Autowired
    ObjectMapper objectMapper

    @Autowired
    ProductDataStoreService productDataStoreService


    @RequestMapping(value = "products/{id}", method = RequestMethod.GET, produces = 'application/json;charset=UTF-8')
    @ResponseStatus(value = OK)
    @ResponseBody
    def getProductDetails(
            @PathVariable(value = "id") String productId , HttpServletRequest httpServletRequest) {

        //TODO Check if id is a number and reject others
        String xRequestID = httpServletRequest.getHeader('X-REQUEST-ID')

        log.info("Received Product GET request for id=${productId}, X-REQUEST-ID: ${xRequestID}")
//        return RetailAppTimer.execute(
//                ['X-REQUEST-ID':xRequestID,'productId':productId,'operation': 'getProductDetails'],
//                {productAggregatorService.getProductDetails(productId)}
//        )


        return productAggregatorService.getProductDetails(productId)
    }


    @RequestMapping(value = "products/{id}", method = RequestMethod.PUT, consumes =  'application/json;charset=UTF-8')
    @ResponseStatus(value = OK)
    @ResponseBody
    def writeProductDetails(
            @PathVariable(value = "id") String productId , @RequestBody def data, HttpServletRequest httpServletRequest) {
        String xRequestID = httpServletRequest.getHeader('X-REQUEST-ID')

        //TODO Check if product id from url path is same as body else throw error

        log.info("Received Product PUT request for id=${productId}, X-REQUEST-ID: ${xRequestID}")

         RetailAppTimer.execute(
                ['X-REQUEST-ID':xRequestID,'productId':productId, 'operation': 'writeProductDetails'],
                 {productDataStoreService.writeProductDetails(objectMapper.readValue(objectMapper.writeValueAsString(data),ProductData))}
         )
    }
}
