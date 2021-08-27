package com.revature.portal.datasource.util;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.revature.portal.util.exceptions.DataSourceException;
import org.bson.codecs.configuration.CodecRegistry;

import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class MongoClientFactory {

    private MongoClient mongoClient;
    private static final MongoClientFactory mongoClientFactory = new MongoClientFactory();
//    private final Logger logger = LogManager.getLogger(MongoClientFactory.class);

    /**
     * Singleton Factory used to abstract a database connection, and to ensure only one instance is created.
     */
    private MongoClientFactory() {
        Properties appProperties = new Properties();

        try {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            appProperties.load(loader.getResourceAsStream("application.properties"));

            String ipAddress = appProperties.getProperty("ipAddress");
            int port = Integer.parseInt(appProperties.getProperty("port"));
            String dbName = appProperties.getProperty("dbName");
            String username = appProperties.getProperty("username");
            char[] password = appProperties.getProperty("password").toCharArray();

            List<ServerAddress> hosts = Collections.singletonList(new ServerAddress(ipAddress, port));
            MongoCredential credentials = MongoCredential.createScramSha1Credential(username, dbName, password);
            MongoClientSettings settings = MongoClientSettings.builder()
                                                              .applyToClusterSettings(builder -> builder.hosts(hosts))
                                                              .credential(credentials)
                                                              .build();

            this.mongoClient = MongoClients.create(settings);

        } catch (FileNotFoundException fnfe) {
            throw new DataSourceException("Unable to load database properties file.", fnfe);
        } catch(Exception e){
//            logger.error(fnfe.getMessage());
            throw new DataSourceException("Unable to load database properties file.", e);
        }
    }

    public void cleanUp(){
        mongoClient.close();
    }

    public static MongoClientFactory getInstance(){
        return mongoClientFactory;
    }

    public MongoClient getConnection(){
        return mongoClient;
    }

}
