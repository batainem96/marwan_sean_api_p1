package com.revature.schoolDatabase.repositories;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Projections;
import com.revature.schoolDatabase.models.Faculty;
import com.revature.schoolDatabase.models.Person;
import com.revature.schoolDatabase.models.Student;
import com.revature.schoolDatabase.util.MongoClientFactory;
import com.revature.schoolDatabase.util.exceptions.DataSourceException;
import org.bson.Document;

public class UserRepository implements CrudRepository<Person> {

    public Person findUserByCredentials(String username, String password) {
        try {
            MongoClient mongoClient = MongoClientFactory.getInstance().getConnection();
            MongoDatabase schoolDatabase = mongoClient.getDatabase("p0");
            MongoCollection<Document> usersCollection = schoolDatabase.getCollection("users");
            Document queryDoc = new Document("username", username).append("password", password);
            Document authUserDoc = usersCollection.find(queryDoc).first();

            if (authUserDoc == null) {
                return null;
            }

            ObjectMapper mapper = new ObjectMapper();
            Person authUser;
            String userType = usersCollection.find(new BasicDBObject("username", username)).projection(Projections.fields(Projections.include("userType"), Projections.excludeId())).first().getString("userType");
            System.out.println(userType);
            switch (userType) {
                case "student":
                    authUser = mapper.readValue(authUserDoc.toJson(), Student.class);
                    break;
                case "faculty":
                    authUser = mapper.readValue(authUserDoc.toJson(), Faculty.class);
                    break;
                default:
                    System.out.println("Invalid user type");
                    return null;
            }

            authUser.setId(authUserDoc.get("_id").toString());
            System.out.println(authUser);
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

            MongoDatabase schoolDatabase = mongoClient.getDatabase("bookstore");
            MongoCollection<Document> usersCollection = schoolDatabase.getCollection("users");
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
