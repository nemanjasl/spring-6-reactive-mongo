package com.nemanja.reactivemongo.config;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;

import java.util.Collections;

import static java.util.Collections.singletonList;

public class MongoConfig extends AbstractReactiveMongoConfiguration {

    @Bean
    public MongoClient mongoClient() {
        return MongoClients.create();
    }

    @Override
    protected String getDatabaseName() {
        return "sfg";
    }

    //This is ONLY if we are running MongoDB as DockerImg
//    @Override
//    protected void configureClientSettings(MongoClientSettings.Builder builder) {
//        builder.credential(MongoCredential.createCredential("root",
//                        "admin", "example".toCharArray()))
//                .applyToClusterSettings(settings -> {
//                    settings.hosts(singletonList(
//                            new ServerAddress("127.0.0.1", 27017)
//                    ));
//                });
//    }
}
