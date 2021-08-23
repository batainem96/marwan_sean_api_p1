package com.revature.schoolDatabase.datasource.repositories;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.DuplicateKeyException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.revature.schoolDatabase.datasource.models.Faculty;
import com.revature.schoolDatabase.datasource.models.User;
import com.revature.schoolDatabase.datasource.models.Student;
import com.revature.schoolDatabase.datasource.util.MongoClientFactory;
import com.revature.schoolDatabase.util.exceptions.DataSourceException;
import com.revature.schoolDatabase.util.exceptions.InvalidRequestException;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

// TODO Handle exceptions more extensively
public class UserRepository {
    // Variables
    private final MongoCollection<Document> usersCollection;
    private final ObjectMapper mapper;

    // Constructor
    public UserRepository(ObjectMapper mapper) {
        this.mapper = mapper;
        MongoClient mongoClient = MongoClientFactory.getInstance().getConnection();
        MongoDatabase schoolDatabase = mongoClient.getDatabase("p1");
        this.usersCollection = schoolDatabase.getCollection("users");
    }

    // Methods
    /**
     * Find all users currently stored in database
     */
    public List<User> retrieveUsers() {
        List<User> users = new ArrayList<>();
        try {
            // Store all documents into a findIterable object
            MongoCursor<Document> cursor = usersCollection.find().iterator();
            while (cursor.hasNext()) {
                Document curUser = cursor.next();
                User newUser;
                if (curUser.get("userType").toString().equals("faculty") || curUser.get("userType").toString().equals("pendingFaculty"))
                    newUser = mapper.readValue((curUser).toJson(), Faculty.class);
                else newUser = mapper.readValue((curUser).toJson(), Student.class);
                newUser.setId(curUser.get("_id").toString());
                users.add(newUser);
            }
            cursor.close();

            return users;

        } catch (JsonMappingException jme) {
            throw new DataSourceException("An exception occurred while mapping the document.", jme);
        } catch (Exception e) {
            throw new DataSourceException("An unexpected exception occurred.", e);
        }
    }

    /**
     * Find user in database given a username
     *
     * @param username
     * @return
     */
    public User findUserByCredentials(String username) {
        try {
            Document queryDoc = new Document("username", username);
            Document authUserDoc = usersCollection.find(queryDoc).first();

            if (authUserDoc == null)
                return null;

            User authUser;
            // Retrieves the value of the userType field in the database
            String userType = authUserDoc.get("userType").toString();
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
            return authUser;

        } catch (JsonMappingException jme) {
            throw new DataSourceException("An exception occurred while mapping the document.", jme);
        } catch (Exception e) {
            throw new DataSourceException("An unexpected exception occurred.", e);
        }

    }

    /**
     * Search the database for an entry with the given username and password combination
     *
     * @param username
     * @param password
     * @return
     */
    public User findUserByCredentials(String username, String password) {
        try {
            Document queryDoc = new Document("username", username).append("password", password);
            Document authUserDoc = usersCollection.find(queryDoc).first();

            if (authUserDoc == null)
                return null;

            User authUser;
            // Retrieves the value of the userType field in the database
            String userType = authUserDoc.get("userType").toString();
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
            return authUser;

        } catch (JsonMappingException jme) {
            throw new DataSourceException("An exception occurred while mapping the document.", jme);
        } catch (Exception e) {
            throw new DataSourceException("An unexpected exception occurred.", e);
        }

    }

    /**
     * Search the database for an entry with the given username and, if it exists, delete it.
     *
     * @param username
     * @return
     */
    public boolean deleteUserByCredentials(String username) {
        try {
            MongoClient mongoClient = MongoClientFactory.getInstance().getConnection();

            MongoDatabase schoolDatabase = mongoClient.getDatabase("p0");
            MongoCollection<Document> usersCollection = schoolDatabase.getCollection("users");

            // delete user
            usersCollection.deleteOne(Filters.eq("username", username));

            return true;

        } catch (Exception e) {
            throw new DataSourceException("An unexpected exception occurred.", e);
        }
    }

    /**
     *  id = Unique Object ID given by the Mongo Database
     */
    public User save(User newUser) {
        try {
            // Convert Person to BasicDBObject
            String userJson = mapper.writeValueAsString(newUser);
            Document userDoc = Document.parse(userJson);

            usersCollection.insertOne(userDoc);
            newUser.setId(userDoc.get("_id").toString());

            return newUser;
        } catch (DuplicateKeyException dke) {
            // TODO Discern which keys are invalid
            throw new InvalidRequestException("User already exists!");
        } catch (Exception e) {
            throw new DataSourceException("An unexpected exception occurred.", e);
        }
    }

    public User findById(String id) {
        try {
            Document queryDoc = new Document("_id", new ObjectId(id));
            Document authUserDoc = usersCollection.find(queryDoc).first();

            if (authUserDoc == null)
                return null;

            User authUser;
            // Retrieves the value of the userType field in the database
            String userType = authUserDoc.get("userType").toString();
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
            return authUser;

        } catch (JsonMappingException jme) {
            throw new DataSourceException("An exception occurred while mapping the document.", jme);
        } catch (Exception e) {
            throw new DataSourceException("An unexpected exception occurred.", e);
        }

    }

    public boolean update(User updatedUser) {
        try {
            // Convert Person to BasicDBObject
            String userJson = mapper.writeValueAsString(updatedUser);
            Document userDoc = Document.parse(userJson);
            UpdateResult result = usersCollection.replaceOne(eq(("_id"), new ObjectId(updatedUser.getId())), userDoc);

            return result.wasAcknowledged();

        } catch (JsonMappingException jme) {
            jme.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteById(String id) {
        try {
            Document queryDoc = new Document("_id", new ObjectId(id));

            // delete user
            DeleteResult result = usersCollection.deleteOne(queryDoc);
            return result.wasAcknowledged();
        } catch (Exception e) {
            throw new DataSourceException("An unexpected exception occurred.", e);
        }
    }
}
