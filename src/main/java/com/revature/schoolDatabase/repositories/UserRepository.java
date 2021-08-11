package com.revature.schoolDatabase.repositories;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.revature.schoolDatabase.models.Faculty;
import com.revature.schoolDatabase.models.Person;
import com.revature.schoolDatabase.models.Student;
import com.revature.schoolDatabase.util.MongoClientFactory;
import com.revature.schoolDatabase.util.exceptions.DataSourceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

// TODO Handle exceptions more extensively
public class UserRepository implements CrudRepository<Person> {
    // Variables
    private final MongoCollection<Document> usersCollection;
    private final ObjectMapper mapper;
    private final Logger logger = LogManager.getLogger(UserRepository.class);

    // Constructor
    public UserRepository(ObjectMapper mapper) {
        this.mapper = mapper;
        MongoClient mongoClient = MongoClientFactory.getInstance().getConnection();
        MongoDatabase schoolDatabase = mongoClient.getDatabase("p0");
        this.usersCollection = schoolDatabase.getCollection("users");
    }

    // Methods
    /**
     * Find all users currently stored in database
     */
    public List<Person> retrieveUsers() {
        List<Person> users = new ArrayList<>();
        try {
            // Store all documents into a findIterable object
            MongoCursor<Document> cursor = usersCollection.find().iterator();
            while (cursor.hasNext()) {
                Document curUser = cursor.next();
                Person newUser;
                if (curUser.get("userType").toString().equals("faculty") || curUser.get("userType").toString().equals("pendingFaculty"))
                    newUser = mapper.readValue((curUser).toJson(), Faculty.class);
                else newUser = mapper.readValue((curUser).toJson(), Student.class);
                newUser.setId(curUser.get("_id").toString());
                users.add(newUser);
            }
            cursor.close();

            return users;

        } catch (JsonMappingException jme) {
            logger.error(jme.getMessage());
            throw new DataSourceException("An exception occurred while mapping the document.", jme);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new DataSourceException("An unexpected exception occurred.", e);
        }
    }

    /**
     * Find user in database given a username
     *
     * @param username
     * @return
     */
    public Person findUserByCredentials(String username) {
        try {
            Document queryDoc = new Document("username", username);
            Document authUserDoc = usersCollection.find(queryDoc).first();

            if (authUserDoc == null)
                return null;

            Person authUser;
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
            logger.error(jme.getMessage());
            throw new DataSourceException("An exception occurred while mapping the document.", jme);
        } catch (Exception e) {
            logger.error(e.getMessage());
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
    public Person findUserByCredentials(String username, String password) {
        try {
            Document queryDoc = new Document("username", username).append("password", password);
            Document authUserDoc = usersCollection.find(queryDoc).first();

            if (authUserDoc == null)
                return null;

            Person authUser;
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
            logger.error(jme.getMessage());
            throw new DataSourceException("An exception occurred while mapping the document.", jme);
        } catch (Exception e) {
            logger.error(e.getMessage());
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
            logger.error(e.getMessage());
            throw new DataSourceException("An unexpected exception occurred.", e);
        }
    }

    /**
     * Overridden User CRUD operations
     *
     *  id = Unique Object ID given by the Mongo Database
     */
    @Override
    public Person save(Person newPerson) {
        try {
            // Convert Person to BasicDBObject
            String userJson = mapper.writeValueAsString(newPerson);
            Document userDoc = Document.parse(userJson);

            usersCollection.insertOne(userDoc);
            newPerson.setId(userDoc.get("_id").toString());

            return newPerson;

        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new DataSourceException("An unexpected exception occurred.", e);
        }
    }

    @Override
    public Person findById(String id) {
        try {
            Document queryDoc = new Document("_id", new ObjectId(id));
            Document authUserDoc = usersCollection.find(queryDoc).first();

            if (authUserDoc == null)
                return null;

            Person authUser;
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
            logger.error(jme.getMessage());
            throw new DataSourceException("An exception occurred while mapping the document.", jme);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new DataSourceException("An unexpected exception occurred.", e);
        }

    }

    @Override
    public boolean update(Person updatedPerson) {
        try {
            // Convert Person to BasicDBObject
            String userJson = mapper.writeValueAsString(updatedPerson);
            Document userDoc = Document.parse(userJson);
            UpdateResult result = usersCollection.replaceOne(eq(("_id"), new ObjectId(updatedPerson.getId())), userDoc);

            return result.wasAcknowledged();

        } catch (JsonMappingException jme) {
            jme.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean deleteById(String id) {
        try {
            Document queryDoc = new Document("_id", new ObjectId(id));

            // delete user
            DeleteResult result = usersCollection.deleteOne(queryDoc);
            return result.wasAcknowledged();
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new DataSourceException("An unexpected exception occurred.", e);
        }
    }
}
