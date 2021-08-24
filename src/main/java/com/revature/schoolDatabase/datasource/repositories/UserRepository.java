package com.revature.schoolDatabase.datasource.repositories;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.DuplicateKeyException;
import com.mongodb.MongoWriteException;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.InsertOneOptions;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
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
     * This is a service function to abstract away some of the logic of mapping database info to a class.
     *      Once a database query has returned a document, this function reads the user type in the document
     *      and maps the data to either a Student or Faculty object.
     *
     * @param authUserDoc - A MongoDB document containing the result of the last query.
     */
    public User mapUser(Document authUserDoc) {
        User authUser;
        // Retrieves the value of the userType field in the database
        String userType = authUserDoc.get("userType").toString();
        try {
            switch (userType) {
                case "student":
                    authUser = mapper.readValue(authUserDoc.toJson(), Student.class);
                    break;
                case "faculty":
                case "pendingFaculty":  // TODO Fully implement pendingFaculty
                    authUser = mapper.readValue(authUserDoc.toJson(), Faculty.class);
                    break;
                default:
                    Throwable cause = new Throwable("Invalid user type");
                    throw new DataSourceException("An exception occurred while mapping the document.", cause);
            }
        } catch (JsonMappingException jme) {
            throw new DataSourceException("An exception occurred while mapping the document.", jme);
        } catch (Exception e) {
            throw new DataSourceException("An unexpected exception occurred while mapping the document.", e);
        }

        authUser.setId(authUserDoc.get("_id").toString());
        return authUser;
    }

    /**
     * Finds and retrieves all users currently stored in database.
     */
    public List<User> retrieveUsers() {
        List<User> users = new ArrayList<>();
        try {
            // Store all documents into a findIterable object
            MongoCursor<Document> cursor = usersCollection.find().iterator();
            while (cursor.hasNext()) {
                Document curUser = cursor.next();
                User newUser = mapUser(curUser);
                users.add(newUser);
            }
            cursor.close();
            return users;

        } catch (Exception e) {
            throw new DataSourceException("An unexpected exception occurred.", e);
        }
    }

    /**
     * Find user in database given an id
     *
     * @param id
     * @return New user if an entry was found, otherwise null.
     */
    public User findById(String id) {
        try {
            Document queryDoc = new Document("_id", new ObjectId(id));
            Document authUserDoc = usersCollection.find(queryDoc).first();

            if (authUserDoc == null)
                return null;
            User authUser = mapUser(authUserDoc);
            return authUser;

        } catch (DataSourceException dse) {
            throw new DataSourceException(dse.getLocalizedMessage(), dse);
        } catch (Exception e) {
            throw new DataSourceException("An unexpected exception occurred.", e);
        }

    }

    /**
     * Finds user in database given a username
     *
     * @param username
     * @return New user if an entry was found, otherwise null.
     */
    public User findUserByCredentials(String username) {
        try {
            Document queryDoc = new Document("username", username);
            Document authUserDoc = usersCollection.find(queryDoc).first();

            if (authUserDoc == null)
                return null;
            User authUser = mapUser(authUserDoc);
            return authUser;

        } catch (DataSourceException dse) {
            throw new DataSourceException(dse.getLocalizedMessage(), dse);
        } catch (Exception e) {
            throw new DataSourceException("An unexpected exception occurred.", e);
        }

    }

    /**
     * Search the database for an entry with the given username and password combination
     *
     * @param username
     * @param password
     * @return New user if an entry was found, otherwise null.
     */
    public User findUserByCredentials(String username, String password) {
        try {
            Document queryDoc = new Document("username", username).append("password", password);
            Document authUserDoc = usersCollection.find(queryDoc).first();

            if (authUserDoc == null)
                return null;
            User authUser = mapUser(authUserDoc);
            return authUser;

        } catch (DataSourceException dse) {
            throw new DataSourceException(dse.getLocalizedMessage(), dse);
        } catch (Exception e) {
            throw new DataSourceException("An unexpected exception occurred.", e);
        }
    }

    /**
     * Find user in database given a first and last name.
     *
     * @param firstName, lastName
     * @return New user if an entry was found, otherwise null.
     */
    public User findUserByName(String firstName, String lastName) {
        try {
            Document queryDoc = new Document("firstName", firstName).append("lastName", lastName);
            Document authUserDoc = usersCollection.find(queryDoc).first();

            if (authUserDoc == null)
                return null;
            User authUser = mapUser(authUserDoc);
            return authUser;

        } catch (DataSourceException dse) {
            throw new DataSourceException(dse.getLocalizedMessage(), dse);
        } catch (Exception e) {
            throw new DataSourceException("An unexpected exception occurred.", e);
        }
    }

    /**
     * Find user in database given an email.
     *
     * @param email
     * @return New user if an entry was found, otherwise null.
     */
    public User findUserByEmail(String email) {
        try {
            Document queryDoc = new Document("email", email);
            Document authUserDoc = usersCollection.find(queryDoc).first();

            if (authUserDoc == null)
                return null;
            User authUser = mapUser(authUserDoc);
            return authUser;

        } catch (DataSourceException dse) {
            throw new DataSourceException(dse.getLocalizedMessage(), dse);
        } catch (Exception e) {
            throw new DataSourceException("An unexpected exception occurred.", e);
        }
    }

    /**
     *  Persists new user info to the database. The service layer already checks for duplicate entries
     *      before calling save(), therefore an exception is not expected to be thrown. If an exception is
     *      still thrown anyway, pass it up the chain to be handled later.
     */
    public User save(User newUser) {
        try {
            // Convert Person to BasicDBObject
            String userJson = mapper.writeValueAsString(newUser);
            Document userDoc = Document.parse(userJson);

            // Address write concern
            InsertOneResult insertOneResult = usersCollection.insertOne(userDoc);
            newUser.setId(userDoc.get("_id").toString());

            if (insertOneResult.wasAcknowledged())
                return newUser;
            else throw new InvalidRequestException("User already exists!");

        } catch (MongoWriteException we) {
            throw new InvalidRequestException("User already exists!");
        } catch (Exception e) {
            throw new DataSourceException("An unexpected exception occurred.", e);
        }
    }

    /**
     * Searches the database for an entry matching the given user's id, and replaces the entry with the new
     *      user.
     *
     * @param updatedUser
     * @return
     */
    public boolean update(User updatedUser) {
        try {
            // Convert Person to BasicDBObject
            String userJson = mapper.writeValueAsString(updatedUser);
            Document userDoc = Document.parse(userJson);
            UpdateResult result = usersCollection.replaceOne(eq(("_id"), new ObjectId(updatedUser.getId())), userDoc);

            return result.wasAcknowledged();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Searches the database for an entry with the given id and, if it exists, deletes it.
     *
     * @param id
     * @return
     */
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

    /**
     * Searches the database for an entry with the given username and, if it exists, deletes it.
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
}
