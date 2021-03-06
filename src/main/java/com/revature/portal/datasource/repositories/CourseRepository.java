package com.revature.portal.datasource.repositories;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.client.*;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.revature.portal.datasource.models.Course;
import com.revature.portal.datasource.models.PreReq;
import com.revature.portal.datasource.util.MongoClientFactory;
import com.revature.portal.util.exceptions.DataSourceException;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class CourseRepository {
    // Variables
    private final MongoCollection<Document> courseCollection;
    private final ObjectMapper mapper;

    // Constructors
    public CourseRepository(ObjectMapper mapper) {
        this.mapper = mapper;
        MongoClient mongoClient = MongoClientFactory.getInstance().getConnection();
        MongoDatabase schoolDatabase = mongoClient.getDatabase("p1");
        this.courseCollection = schoolDatabase.getCollection("courses");
    }

    // Other Methods
    /**
     * Returns an arrayList of courses from the database
     *
     * @return
     */
    public List<Course> retrieveCourses() {
        List<Course> courseList = new ArrayList<>();
        try {
            // Store all documents into a findIterable object
            MongoCursor<Document> cursor = courseCollection.find().iterator();
            while (cursor.hasNext()) {
                Document curCourse = cursor.next();
                Course newCourse = mapper.readValue((curCourse).toJson(), Course.class);
                newCourse.setId(curCourse.get("_id").toString());
                courseList.add(newCourse);
            }
            cursor.close();

            return courseList;

        } catch (JsonMappingException jme) {
            throw new DataSourceException("An exception occurred while mapping the document.", jme);
        } catch (Exception e) {
            throw new DataSourceException("An unexpected exception occurred.", e);
        }
    }

    /**
     * Retrieves list of courses from database with given user as the instructor
     *
     * @return
     */
    public List<Course> retrieveInstructorCourses(String firstName, String lastName) {
        List<Course> courseList = new ArrayList<>();
        try {
            // Run two finds:
            //  1. All matching courses of type firstname lastname
            //  2. All matching courses of type lastname, firstname
            String find1 = firstName.concat(" ").concat(lastName);
            String find2 = lastName.concat(", ").concat(firstName);

            // Store all documents into a findIterable object
            Document fields = new Document("$eq", find1);
            MongoCursor<Document> cursor = courseCollection.find(new Document("instructor", fields)).iterator();
            while (cursor.hasNext()) {
                Document curCourse = cursor.next();
                Course newCourse = mapper.readValue((curCourse).toJson(), Course.class);
                newCourse.setId(curCourse.get("_id").toString());
                courseList.add(newCourse);
            }

            // Store all documents into a findIterable object
            fields = new Document("$eq", find2);
            cursor = courseCollection.find(new Document("instructor", fields)).iterator();
            while (cursor.hasNext()) {
                Document curCourse = cursor.next();
                Course newCourse = mapper.readValue((curCourse).toJson(), Course.class);
                newCourse.setId(curCourse.get("_id").toString());
                courseList.add(newCourse);
            }
            cursor.close();

            return courseList;

        } catch (JsonMappingException jme) {
            throw new DataSourceException("An exception occurred while mapping the document.", jme);
        } catch (Exception e) {
            throw new DataSourceException("An unexpected exception occurred.", e);
        }
    }

    public List<Course> retrieveOpenCourses() {
        List<Course> courseList = new ArrayList<>();
        try {
            // Store all documents into a findIterable object
            Document fields = new Document("$gt", 0);
            MongoCursor<Document> cursor = courseCollection.find(new Document("openSeats", fields)).iterator();

            while (cursor.hasNext()) {
                Document curCourse = cursor.next();
                Course newCourse = mapper.readValue((curCourse).toJson(), Course.class);
                newCourse.setId(curCourse.get("_id").toString());
                courseList.add(newCourse);
            }
            cursor.close();

            return courseList;

        } catch (JsonMappingException jme) {
            throw new DataSourceException("An exception occurred while mapping the document.", jme);
        } catch (Exception e) {
            throw new DataSourceException("An unexpected exception occurred.", e);
        }
    }

    /**
     * Finds course in database according to given credentials
     *
     * @param dept
     * @param courseNo
     * @param sectionNo
     * @return
     */
    public Course findByCredentials(String dept, int courseNo, int sectionNo) {
        try {
            System.out.println("Searching for " + dept + " " + courseNo + "-" + sectionNo + "...");
            Document queryDoc = new Document("deptShort", dept).append("courseNo", courseNo)
                                        .append("sectionNo", sectionNo);
            Document authCourseDoc = courseCollection.find(queryDoc).first();

            if (authCourseDoc == null)
                return null;

            Course newCourse = mapper.readValue(authCourseDoc.toJson(), Course.class);
            newCourse.setId(authCourseDoc.get("_id").toString());

            return newCourse;

        } catch (JsonMappingException jme) {
            throw new DataSourceException("An exception occurred while mapping the document.", jme);
        } catch (Exception e) {
            throw new DataSourceException("An unexpected exception occurred.", e);
        }
    }

    public boolean deleteByCredentials(String dept, int courseNo, int sectionNo) {
        try {
            Document queryDoc = new Document("deptShort", dept)
                                        .append("courseNo", courseNo)
                                        .append("sectionNo", sectionNo);

            // delete course
            DeleteResult result = courseCollection.deleteOne(queryDoc);
            return result.wasAcknowledged();
        } catch (Exception e) {
            throw new DataSourceException("An unexpected exception occurred.", e);
        }
    }

    /**
     * Overridden Course CRUD operations
     *
     * @param id = Unique Object ID given by the Mongo Database
     */

    public Course findById(String id) {
        try {
            Document queryDoc = new Document("_id", new ObjectId(id));
            Document authCourseDoc = courseCollection.find(queryDoc).first();

            if (authCourseDoc == null)
                return null;

            Course newCourse = mapper.readValue(authCourseDoc.toJson(), Course.class);
            newCourse.setId(authCourseDoc.get("_id").toString());

            return newCourse;

        } catch (Exception e) {
            e.printStackTrace(); // --- debug
            throw new DataSourceException("An unexpected exception occurred while trying to find course.", e);
        }
    }

    public Course save(Course newCourse) {
        try {
            String courseJson = mapper.writeValueAsString(newCourse);
            Document courseDoc = Document.parse(courseJson);
            courseCollection.insertOne(courseDoc);
            newCourse.setId(courseDoc.get("_id").toString());

            return newCourse;

        } catch (Exception e) {
            e.printStackTrace(); // --- debug
            throw new DataSourceException("An unexpected exception occurred while saving new course.", e);
        }
    }

    public boolean replace(Course updatedCourse) {
        try {
            // Convert Course to BasicDBObject
            String courseJson = mapper.writeValueAsString(updatedCourse);
            Document courseDoc = Document.parse(courseJson);
            UpdateResult result = courseCollection.replaceOne(eq(("_id"), new ObjectId(updatedCourse.getId())), courseDoc);

            return result.wasAcknowledged();

        } catch (JsonMappingException jme) {
//            throw new DataSourceException("An error occurred while mapping the object.", jme);
        } catch (Exception e) {

        }
        return false;
    }

    /**
     *  The update method takes in a course, expected to be partially valid; Fields intended to be updated will
     *  have valid information, otherwise, they will be null/-1. The valid information is then built into a query
     *  object, which is appended to an update query of the form: findOneAndUpdate(eq(("_id"), id), $set: (field: updatedFieldValue)).
     *
     * @param course
     * @return The newly updated course retrieved from the database.
     */
    public Course update(Course course) {
        try {
            // Convert Course to BasicDBObject
            String id = course.getId();
            course.setId(null);
            String courseJson = mapper.writeValueAsString(course);
            Document updateFields = Document.parse(courseJson);

            System.out.println(updateFields);

            BasicDBObject setQuery = new BasicDBObject();
            setQuery.append("$set", updateFields);

            Document courseDoc = courseCollection.findOneAndUpdate(eq(("_id"), new ObjectId(id)), setQuery);
            Course newCourse = mapper.readValue(courseDoc.toJson(), Course.class);
            newCourse.setId(courseDoc.get("_id").toString());

            return newCourse;

        } catch (Exception e) {
            e.printStackTrace();
            return course;
        }
    }

    /**
     *  Removes a course from the database matching the given id. If no course is found matching the id, return false.
     *
     * @param id
     * @return
     */
    public boolean deleteById(String id) {
        try {
            Document queryDoc = new Document("_id", new ObjectId(id));

            // delete course
            Document result = courseCollection.findOneAndDelete(queryDoc);

            // Return true if course was found (and deleted), otherwise return false
            if (result != null) return true;
            else return false;
        } catch (Exception e) {
            throw new DataSourceException("An unexpected exception occurred.", e);
        }
    }
}
