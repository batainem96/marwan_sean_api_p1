package com.revature.schoolDatabase.repositories;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.*;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.revature.schoolDatabase.models.Course;
import com.revature.schoolDatabase.util.MongoClientFactory;
import com.revature.schoolDatabase.util.exceptions.DataSourceException;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class CourseRepository implements CrudRepository<Course>{

    /**
     * Returns an arrayList of courses from the database
     *
     * @return
     */
    public List<Course> retrieveCourses() {
        List<Course> courseList = new ArrayList<>();
        try {
            MongoClient mongoClient = MongoClientFactory.getInstance().getConnection();
            MongoDatabase schoolDatabase = mongoClient.getDatabase("p0");
            MongoCollection<Document> courseCollection = schoolDatabase.getCollection("courses");
            ObjectMapper mapper = new ObjectMapper();
            
            // Store all documents into a findIterable object
            MongoCursor<Document> cursor = courseCollection.find().iterator();
            while (cursor.hasNext()) {
                Document curCourse = cursor.next();
                Course newCourse = mapper.readValue((curCourse).toJson(), Course.class);
                newCourse.setId(curCourse.get("_id").toString());
                courseList.add(newCourse);
            }

            return courseList;

        } catch (JsonMappingException jme) {
            jme.printStackTrace(); // TODO log this to a file
            throw new DataSourceException("An exception occurred while mapping the document.", jme);
        } catch (Exception e) {
            e.printStackTrace(); // TODO log this to a file
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
    public Course findById(String dept, int courseNo, int sectionNo) {
        try {
            System.out.println("Searching for " + dept + " " + courseNo + "-" + sectionNo + "...");
            MongoClient mongoClient = MongoClientFactory.getInstance().getConnection();
            MongoDatabase schoolDatabase = mongoClient.getDatabase("p0");
            MongoCollection<Document> courseCollection = schoolDatabase.getCollection("courses");
            Document queryDoc = new Document("deptShort", dept).append("courseNo", courseNo)
                                        .append("sectionNo", sectionNo);
            Document authCourseDoc = courseCollection.find(queryDoc).first();

            if (authCourseDoc == null)
                return null;

            ObjectMapper mapper = new ObjectMapper();
            Course newCourse = mapper.readValue(authCourseDoc.toJson(), Course.class);
            newCourse.setId(authCourseDoc.get("_id").toString());

            return newCourse;

        } catch (JsonMappingException jme) {
            jme.printStackTrace(); // TODO log this to a file
            throw new DataSourceException("An exception occurred while mapping the document.", jme);
        } catch (Exception e) {
            e.printStackTrace(); // TODO log this to a file
            throw new DataSourceException("An unexpected exception occurred.", e);
        }
    }

    public boolean deleteById(String dept, int courseNo, int sectionNo) {
        try {
            MongoClient mongoClient = MongoClientFactory.getInstance().getConnection();
            MongoDatabase schoolDatabase = mongoClient.getDatabase("p0");
            MongoCollection<Document> courseCollection = schoolDatabase.getCollection("courses");
            Document queryDoc = new Document("deptShort", dept)
                                        .append("courseNo", courseNo)
                                        .append("sectionNo", sectionNo);

            // delete course
            DeleteResult result = courseCollection.deleteOne(queryDoc);
            return result.wasAcknowledged();
        } catch (Exception e) {
            e.printStackTrace(); // TODO log this to a file
            throw new DataSourceException("An unexpected exception occurred.", e);
        }
    }

    /**
     * Overridden Course CRUD operations
     *
     * @param id = Unique Object ID given by the Mongo Database
     */

    @Override
    public Course findById(int id) {
        try {
            MongoClient mongoClient = MongoClientFactory.getInstance().getConnection();

            MongoDatabase schoolDatabase = mongoClient.getDatabase("p0");
            MongoCollection<Document> courseCollection = schoolDatabase.getCollection("courses");
            Document queryDoc = new Document("_id", new ObjectId(String.valueOf(id)));
            Document authCourseDoc = courseCollection.find(queryDoc).first();

            if (authCourseDoc == null)
                return null;

            ObjectMapper mapper = new ObjectMapper();
            Course newCourse = mapper.readValue(authCourseDoc.toJson(), Course.class);
            newCourse.setId(authCourseDoc.get("_id").toString());

            return newCourse;

        } catch (Exception e) {
            e.printStackTrace(); // TODO log this to a file
            throw new DataSourceException("An unexpected exception occurred.", e);
        }
    }

    @Override
    public Course save(Course newCourse) {
        try {
            MongoClient mongoClient = MongoClientFactory.getInstance().getConnection();

            MongoDatabase schoolDatabase = mongoClient.getDatabase("p0");
            MongoCollection<Document> courseCollection = schoolDatabase.getCollection("courses");
            Document newCourseDoc = new Document("title", newCourse.getTitle())
                    .append("department", newCourse.getDepartment())
                    .append("courseNo", newCourse.getCourseNo())
                    .append("sectionNo", newCourse.getSectionNo())
                    .append("instructor", newCourse.getInstructor());

            courseCollection.insertOne(newCourseDoc);
            newCourse.setId(newCourseDoc.get("_id").toString());

            return newCourse;

        } catch (Exception e) {
            e.printStackTrace(); // TODO log this to a file
            throw new DataSourceException("An unexpected exception occurred.", e);
        }
    }

    @Override
    public boolean update(Course updatedCourse) {
        try {
            MongoClient mongoClient = MongoClientFactory.getInstance().getConnection();
            MongoDatabase schoolDatabase = mongoClient.getDatabase("p0");
            MongoCollection<Document> courseCollection = schoolDatabase.getCollection("courses");

            // Convert Course to BasicDBObject
            ObjectMapper mapper = new ObjectMapper();
            String courseJson = mapper.writeValueAsString(updatedCourse);
            Document courseDoc = Document.parse(courseJson);
            courseCollection.findOneAndReplace(eq(("_id"), new ObjectId(updatedCourse.getId())), courseDoc);

            return true;

        } catch (JsonMappingException jme) {
            jme.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean deleteById(int id) {
        try {
            MongoClient mongoClient = MongoClientFactory.getInstance().getConnection();
            MongoDatabase schoolDatabase = mongoClient.getDatabase("p0");
            MongoCollection<Document> courseCollection = schoolDatabase.getCollection("courses");
            Document queryDoc = new Document("_id", new ObjectId(String.valueOf(id)));

            // delete course
            DeleteResult result = courseCollection.deleteOne(queryDoc);
            return result.wasAcknowledged();
        } catch (Exception e) {
            e.printStackTrace(); // TODO log this to a file
            throw new DataSourceException("An unexpected exception occurred.", e);
        }
    }
}
