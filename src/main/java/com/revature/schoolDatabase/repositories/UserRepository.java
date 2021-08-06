package com.revature.schoolDatabase.repositories;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.revature.schoolDatabase.models.Person;
import com.revature.schoolDatabase.util.MongoClientFactory;
import com.revature.schoolDatabase.util.exceptions.DataSourceException;
import org.bson.Document;

public class UserRepository implements CrudRepository<Person> {


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
//                    .append("email", newPerson.getEmail())
                    .append("username", newPerson.getUsername())
                    .append("password", newPerson.getPassword());

            usersCollection.insertOne(newPersonDoc);
            newPerson.setId(newPersonDoc.get("_id").toString());

            return newPerson;

        } catch (Exception e) {
            e.printStackTrace(); // TODO log this to a file
//            throw new DataSourceException("An unexpected exception occurred.", e);
        }
        return null;
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
