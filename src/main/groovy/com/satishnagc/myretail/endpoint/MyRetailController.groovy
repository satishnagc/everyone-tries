package com.satishnagc.myretail.endpoint

import com.fasterxml.jackson.databind.ObjectMapper
import com.satishnagc.myretail.common.RetailAppTimer
import com.satishnagc.myretail.domain.ProductData
import com.satishnagc.myretail.product.ProductAggregatorService
import com.satishnagc.myretail.product.ProductDataStoreService
import groovy.util.logging.Slf4j
import org.apache.commons.codec.binary.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.text.ParseException

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
            @PathVariable(value = "id") String productId , HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {

        String xRequestID = httpServletRequest.getHeader('X-REQUEST-ID')

        try{
            Integer.parseInt(productId)
        }catch(NumberFormatException ex){
            httpServletResponse.sendError(HttpServletResponse.SC_BAD_REQUEST,"ProductId is invalid.")
            httpServletResponse.getWriter().close()
            return
        }

        log.info("Received Product GET request for id=${productId}, X-REQUEST-ID: ${xRequestID}")

        ProductData response = productAggregatorService.getProductDetails(productId)

        if(response){
            return response
        }else {
            httpServletResponse.sendError(HttpServletResponse.SC_BAD_REQUEST,"No data found for the given ProductId.")
            httpServletResponse.getWriter().close()
        }
    }


    @RequestMapping(value = "products/{id}", method = RequestMethod.PUT, consumes =  'application/json;charset=UTF-8')
    @ResponseStatus(value = OK)
    @ResponseBody
    def writeProductDetails(
            @PathVariable(value = "id") String productId , @RequestBody def data, HttpServletRequest httpServletRequest,HttpServletResponse httpServletResponse) {
        String xRequestID = httpServletRequest.getHeader('X-REQUEST-ID')
        ProductData productData
        try{

            productData = objectMapper.readValue(objectMapper.writeValueAsString(data),ProductData)
            Integer.parseInt(productData?.productId)

            if(!(productData?.productId?.equalsIgnoreCase(productId))){
                httpServletResponse.sendError(HttpServletResponse.SC_BAD_REQUEST,"ProductId is invalid.")
                httpServletResponse.getWriter().close()
                return
            }

        }catch(Exception ex){
            httpServletResponse.sendError(HttpServletResponse.SC_BAD_REQUEST,"Data is invalid.")
            httpServletResponse.getWriter().close()
            return
        }

        log.info("Received Product PUT request for id=${productId}, X-REQUEST-ID: ${xRequestID}")

         RetailAppTimer.execute(
                ['X-REQUEST-ID':xRequestID,'productId':productId, 'operation': 'writeProductDetails'],
                 {productDataStoreService.writeProductDetails(productData)}
         )

        "Successfully inserted productId=${productId} in the datastore."
    }
}
