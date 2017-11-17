package com.satishnagc.myretail.common

import groovy.util.logging.Slf4j

@Slf4j
class RetailAppTimer {

    static <T> T execute(Map logValues, Closure closure) {
        Exception thrown = null
        T result = null

        long start = System.nanoTime()
        try {
            result = closure.call()
        } catch (e) {
            thrown = e
            throw e
        } finally {

            long duration = System.nanoTime() - start
            Long millis = Math.round(duration / 1000000l)
            Long micros = Math.round(duration / 1000l)

            String logStatement = buildLogStatment(logValues, millis, micros, thrown)

            log.info(logStatement)
        }
        result
    }

    private static String buildLogStatment(Map logValues, long millis, long micros, Exception e) {
        StringBuilder builder = new StringBuilder("Retail App Timed Execution Complete; elapsedTimeInMs=${millis}; elapsedTimeInMicros=${micros}; error=${e};")
        logValues.each {logKey, value ->
            builder.append(" ${logKey}=${value};")
        }
        builder.toString()
    }
}
