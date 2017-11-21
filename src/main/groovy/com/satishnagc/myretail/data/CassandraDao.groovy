package com.satishnagc.myretail.data

import com.datastax.driver.core.BoundStatement
import com.datastax.driver.core.ResultSet
import com.datastax.driver.core.Session
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PreDestroy

@Slf4j
@Component
abstract class CassandraDao {

    @Autowired
    Session cassandraSession

    protected ResultSet execute(BoundStatement boundStatement) {
        ResultSet resultSet = null
        Exception thrown = null

        long start = System.nanoTime()
        try {
            resultSet = cassandraSession.execute(boundStatement)
        } catch (e) {
            thrown = e
            throw e
        } finally {
            long duration = System.nanoTime() - start
            Long millis = Math.round(duration / 1000000l)
            Long micros = Math.round(duration / 1000l)

            String logStatement = "Cassandra Query Complete; elapsedTime=${millis}; elapsedMicros=${micros}; error=${thrown}; " +
                    "query=${boundStatement?.preparedStatement()?.getQueryString()}"
            log.debug(logStatement)
        }
        resultSet
    }

    @PreDestroy
    void shutdown() {
        log.info('Shutting down the cassandra cluster')
        cassandraSession?.close()
        cassandraSession?.cluster?.close()
    }
}
