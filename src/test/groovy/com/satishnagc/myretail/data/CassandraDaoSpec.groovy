package com.satishnagc.myretail.data

import com.datastax.driver.core.BoundStatement
import com.datastax.driver.core.Cluster
import com.datastax.driver.core.Session
import spock.lang.Specification


class CassandraDaoSpec extends Specification{

    Session session = Mock()
    Cluster cluster = Mock()
    CassandraDao testDao = new TestDao(cassandraSession: session)

    private class TestDao extends CassandraDao {}

    def "execute"() {
        given:
        BoundStatement boundStatement = Mock()

        when:
        testDao.execute(boundStatement)

        then:
        1 * boundStatement.preparedStatement()
        1 * session.execute(boundStatement)
        0 *_

    }
}
