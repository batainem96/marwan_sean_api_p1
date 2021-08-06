package com.revature.schoolDatabase.repositories;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.revature.schoolDatabase.models.Person;
import com.revature.schoolDatabase.util.MongoClientFactory;
import com.revature.schoolDatabase.util.exceptions.DataSourceException;
import org.bson.Document;

public class UserRepository implements CrudRepository<Person> {

    public Person findUserByCredentials(String username, String password) {
        try {
            MongoClient mongoClient = MongoClientFactory.getInstance().getConnection();
            MongoDatabase bookstoreDatabase = mongoClient.getDatabase("bookstore");
            MongoCollection<Document> usersCollection = bookstoreDatabase.getCollection("users");
            Document queryDoc = new Document("username", username).append("password", password);
            Document authUserDoc = usersCollection.find(queryDoc).first();

            if (authUserDoc == null) {
                return null;
            }

            ObjectMapper mapper = new ObjectMapper();
            Person authUser = mapper.readValue(authUserDoc.toJson(), Person.class);
            authUser.setId(authUserDoc.get("_id").toString());
            return authUser;

        } catch (JsonMappingException jme) {
            jme.printStackTrace(); // TODO log this to a file
            throw new DataSourceException("An exception occurred while mapping the document.", jme);
        } catch (Exception e) {
            e.printStackTrace(); // TODO log this to a file
            throw new DataSourceException("An unexpected exception occurred.", e);
        }

    }

    @Override
    public Person findById(int id) {
        return null;
    }

    @Override
    public Person save(Person newPerson) {
        try {
            MongoClient mongoClient = MongoClientFactory.getInstance().getConnection();

            MongoDatabase bookstoreDb = mongoClient.getDatabase("bookstore");
            MongoCollection<Document> usersCollection = bookstoreDb.getCollection("users");
            Document newPersonDoc = new Document("firstName", newPerson.getFirstName())
                    .append("lastName", newPerson.getLastName())
                    .append("username", newPerson.getUsername())
                    .append("password", newPerson.getPassword())
                    .append("userType", newPerson.getUserType());

            usersCollection.insertOne(newPersonDoc);
            newPerson.setId(newPersonDoc.get("_id").toString());

            return newPerson;

        } catch (Exception e) {
            e.printStackTrace(); // TODO log this to a file
            throw new DataSourceException("An unexpected exception occurred.", e);
        }
    }

    @Override
    public boolean update(Person updatedResource) {
        return false;
    }

    @Override
    public boolean deleteById(int id) {
        return false;
    }
}
