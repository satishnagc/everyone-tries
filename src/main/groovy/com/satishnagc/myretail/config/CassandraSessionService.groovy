package com.satishnagc.myretail.config

import com.datastax.driver.core.Cluster
import com.datastax.driver.core.Session
import groovy.util.logging.Slf4j

import java.util.concurrent.ConcurrentHashMap

@Slf4j
class CassandraSessionService {

    Cluster cluster

    Map<String, Session> sessions = new ConcurrentHashMap<>()

    Session getSession(final String keyspace) {
        Session session = sessions.get(keyspace)
        if (!session) {
            session = getCluster().connect(keyspace)
            sessions.put(keyspace, session)
        } else if (session.isClosed()) {
            log.error("session for keyspace " + keyspace + " is closed, creating a new keyspace.")
            // if any prepared statements are cached for this session, memory leak issue
            session = getCluster().connect(keyspace)
            sessions.put(keyspace, session)
        }

        session
    }

    private Cluster getCluster() {
        if (!cluster) {
            log.error("cluster not set.");
            throw new IllegalArgumentException("cluster not defined.")
        }
        cluster
    }

    void setCluster(final Cluster cluster) {
        if (!cluster) {
            throw new IllegalArgumentException("cluster cannot be null.")
        }
        this.cluster = cluster
    }
}
