package com.satishnagc.myretail.config

import groovy.util.logging.Slf4j
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.PropertyPlaceholderAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.web.support.SpringBootServletInitializer
import org.springframework.context.ApplicationContext

@Slf4j
@SpringBootApplication(
        scanBasePackages = [
                'com.satishnagc.myretail'

        ],
        exclude = [PropertyPlaceholderAutoConfiguration.class, JacksonAutoConfiguration.class]
)
class MyRetailApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(MyRetailApplication.class);
    }

    static void main(String[] args) {
        log.info('initializing spring application')
        ApplicationContext context = SpringApplication.run(MyRetailApplication.class, args)

        System.out.println("Beans provided by Spring Boot:")

        String[] beanNames = context.getBeanDefinitionNames()
        Arrays.sort(beanNames)
        for (String beanName : beanNames) {
            System.out.println(beanName)
        }
    }
}
