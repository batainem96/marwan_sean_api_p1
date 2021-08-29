package com.revature.portal.datasource.repositories;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import com.revature.portal.datasource.models.*;
import com.revature.portal.datasource.util.MongoClientFactory;
import com.revature.portal.util.exceptions.DataSourceException;
import com.revature.portal.util.exceptions.InvalidRequestException;
import com.revature.portal.util.exceptions.ResourcePersistenceException;
import com.revature.portal.web.dtos.UserDTO;
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
    public UserDTO mapUser(Document authUserDoc) {
        UserDTO user;
        // Retrieves the value of the userType field in the database
        String role = authUserDoc.get("role").toString();
        try {
            user = mapper.readValue(authUserDoc.toJson(), UserDTO.class);

        } catch (JsonMappingException jme) {
            throw new DataSourceException("An exception occurred while mapping the document.", jme);

        } catch (Exception e) {
            throw new DataSourceException("An unexpected exception occurred while mapping the document.", e);

        }

        user.setId(authUserDoc.get("_id").toString());
        return user;
    }

    /**
     * Finds and retrieves all users currently stored in database.
     */
    public List<UserDTO> retrieveUsers() {
        List<UserDTO> users = new ArrayList<>();
        try {
            // Store all documents into a findIterable object
            MongoCursor<Document> cursor = usersCollection.find().iterator();
            while (cursor.hasNext()) {
                Document curUser = cursor.next();
                UserDTO newUser = mapUser(curUser);
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
    public UserDTO findById(String id) {
        try {
            Document queryDoc = new Document("_id", new ObjectId(id));
            Document authUserDoc = usersCollection.find(queryDoc).first();

            if (authUserDoc == null)
                return null;
            UserDTO authUser = mapUser(authUserDoc);
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
    public UserDTO findUserByUsername(String username) {
        try {
            Document queryDoc = new Document("username", username);
            Document authUserDoc = usersCollection.find(queryDoc).first();

            if (authUserDoc == null)
                return null;
            UserDTO authUser = mapUser(authUserDoc);
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
    public UserDTO findUserByCredentials(String username, String password) {
        try {
            Document queryDoc = new Document("username", username).append("password", password);
            Document authUserDoc = usersCollection.find(queryDoc).first();

            if (authUserDoc == null)
                return null;
            UserDTO authUser = mapUser(authUserDoc);
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
    public UserDTO findUserByName(String firstName, String lastName) {
        try {
            Document queryDoc = new Document("firstName", firstName).append("lastName", lastName);
            Document authUserDoc = usersCollection.find(queryDoc).first();

            if (authUserDoc == null)
                return null;
            UserDTO authUser = mapUser(authUserDoc);
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
    public UserDTO findUserByEmail(String email) {
        try {
            Document queryDoc = new Document("email", email);
            Document authUserDoc = usersCollection.find(queryDoc).first();

            if (authUserDoc == null)
                return null;
            UserDTO authUser = mapUser(authUserDoc);
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
    public UserDTO replace(User updatedUser) {
        try {
            // Convert Person to BasicDBObject
            String userJson = mapper.writeValueAsString(updatedUser);
//            Document userDoc = Document.parse(userJson);
            Document fieldsDoc = new Document();

            if(updatedUser.getFirstName() != null) fieldsDoc.append("firstName", updatedUser.getFirstName());
            if(updatedUser.getLastName() != null) fieldsDoc.append("lastName", updatedUser.getLastName());
            if(updatedUser.getEmail() != null) fieldsDoc.append("email", updatedUser.getEmail());
            if(updatedUser.getUsername() != null) fieldsDoc.append("username", updatedUser.getUsername());
            if(updatedUser.getPassword() != null) fieldsDoc.append("password", updatedUser.getPassword());
            if(updatedUser.getSchedule() != null) {

                List<Document> courseListDoc = new ArrayList<>();

                for (Course course : updatedUser.getSchedule()) {

                    List<Document> meetingTimesDoc = new ArrayList<>();
                    for(MeetingTime meet : course.getMeetingTimes()) {
                        meetingTimesDoc.add(new Document("day", meet.getDay())
                                        .append("startTime", meet.getStartTime())
                                        .append("endTime", meet.getEndTime())
                                        .append("classType", meet.getClassType()));
                    }

                    List<Document> prerequisitesDoc = new ArrayList<>();
                    for (PreReq preReq : course.getPrerequisites()) {
                        prerequisitesDoc.add(new Document("department", preReq.getDepartment())
                                        .append("courseNo", preReq.getCourseNo())
                                        .append("credits", preReq.getCredits()));
                    }

                    courseListDoc.add(new Document("_id", new ObjectId(course.getId()))
                            .append("title", course.getTitle())
                            .append("department", course.getDepartment())
                            .append("deptShort", course.getDeptShort())
                            .append("courseNo", course.getCourseNo())
                            .append("sectionNo", course.getSectionNo())
                            .append("instructor", course.getInstructor())
                            .append("credits", course.getCredits())
                            .append("totalSeats", course.getTotalSeats())
                            .append("openSeats", course.getOpenSeats())
                            .append("description", course.getDescription())
                            .append("meetingTimes", meetingTimesDoc)
                            .append("prerequisites", prerequisitesDoc));
                }

                fieldsDoc.append("schedule", courseListDoc);
            }


            Document updateDoc = new Document("$set", fieldsDoc);

            System.out.println(fieldsDoc);

            Document result = usersCollection.findOneAndUpdate(eq(("_id"), new ObjectId(updatedUser.getId())), updateDoc, new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER));
            UserDTO updatedUserDTO = mapper.readValue(result.toJson(), UserDTO.class);
            updatedUserDTO.setId(result.get("_id").toString());

            //return result.wasAcknowledged();
            return updatedUserDTO;

        } catch (Exception e) {
            e.printStackTrace();
            throw new ResourcePersistenceException("Failed to update user!");
        }
    }

    public UserDTO update(User user) {
        try {
            // Convert User to BasicDBObject
            String id = user.getId();
            user.setId(null);
            String userJson = mapper.writeValueAsString(user);
            Document updateFields = Document.parse(userJson);

            BasicDBObject setQuery = new BasicDBObject();
            setQuery.append("$set", updateFields);

            usersCollection.updateOne(eq(("_id"), new ObjectId(id)), setQuery);
            Document userDoc = usersCollection.find(eq(("_id"), new ObjectId(id))).first();
            UserDTO newUser = mapper.readValue(userDoc.toJson(), UserDTO.class);
            newUser.setId(userDoc.get("_id").toString());

            return newUser;

        } catch (Exception e) {
            e.printStackTrace();
            return new UserDTO(user);
        }
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

    public boolean deleteCourseByIdFromUsers(String id) {
        try {

            if(id == null || id.trim().equals("")) {
                throw new InvalidRequestException("Invalid Course ID given.");
            }

            Document queryDoc = new Document("schedule.id", id);
            Document pullDoc = new Document("schedule",
                    new Document("id", id));

            usersCollection.updateMany(queryDoc,
                    new Document("$pull",
                    pullDoc));

            System.out.println("Course (ID): " + id + " successfully removed from students.");
            return true;
        } catch (Exception e) {
            throw new ResourcePersistenceException("Failed to remove courses from users' schedules.");
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
