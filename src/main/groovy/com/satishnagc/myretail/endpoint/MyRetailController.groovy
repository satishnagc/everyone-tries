package com.satishnagc.myretail.endpoint

import com.satishnagc.myretail.common.RetailAppTimer
import com.satishnagc.myretail.product.ProductService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
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
    ProductService redSkyProductService


    @RequestMapping(value = "products/{id}", method = RequestMethod.GET, produces = 'application/json;charset=UTF-8')
    @ResponseStatus(value = OK)
    @ResponseBody
    def getProductDetails(
            @PathVariable(value = "id") String productId , HttpServletRequest httpServletRequest) {
        Map response
        String xRequestID = httpServletRequest.getHeader('X-REQUEST-ID')

        log.info("Received Product request for id=${productId}, X-REQUEST-ID: ${xRequestID}")
        return RetailAppTimer.execute(['X-REQUEST-ID':xRequestID,'productId':productId],
                {redSkyProductService.getProductName(productId)})
    }
}
