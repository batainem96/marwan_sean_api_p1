package com.revature.portal.datasource.codecs;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mongodb.MongoClientSettings;
import com.revature.portal.datasource.converters.PreReqConverter;
import com.revature.portal.datasource.models.Course;
import com.revature.portal.datasource.models.MeetingTime;
import com.revature.portal.datasource.models.PreReq;
import org.bson.BsonReader;
import org.bson.BsonValue;
import org.bson.BsonWriter;
import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.CollectibleCodec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistry;

import java.util.ArrayList;

public class CourseCodec implements CollectibleCodec<Course> {

    private final CodecRegistry registry;
    private final Codec<Document> documentCodec;
    private final PreReqConverter preReqConverter;

    public CourseCodec() {
        this.registry = MongoClientSettings.getDefaultCodecRegistry();
        this.documentCodec = this.registry.get(Document.class);
        this.preReqConverter = new PreReqConverter();
    }

    public CourseCodec(Codec<Document> documentCodec) {
        this.registry = MongoClientSettings.getDefaultCodecRegistry();
        this.documentCodec = documentCodec;
        this.preReqConverter = new PreReqConverter();
    }

    public CourseCodec(CodecRegistry registry) {
        this.registry = registry;
        this.documentCodec = this.registry.get(Document.class);
        this.preReqConverter = new PreReqConverter();
    }

    @Override
    public Course generateIdIfAbsentFromDocument(Course course) {
        return null;
    }

    @Override
    public boolean documentHasId(Course course) {
        return false;
    }

    @Override
    public BsonValue getDocumentId(Course course) {
        return null;
    }

    @Override
    public Course decode(BsonReader bsonReader, DecoderContext decoderContext) {
        Document document = documentCodec.decode(bsonReader, decoderContext);

        Course course = new Course();

        course.setId(document.getObjectId("_id").toString());
        course.setTitle(document.getString("title"));
        course.setDepartment(document.getString("department"));
        course.setDeptShort(document.getString("deptShort"));
        course.setCourseNo(document.getInteger("courseNo"));
        course.setSectionNo(document.getInteger("sectionNo"));

        return course;
    }

    @Override
    public void encode(BsonWriter bsonWriter, Course course, EncoderContext encoderContext) {
        Document document = new Document();
        document.put("_id", course.getId());
        document.put("title", course.getTitle());
        document.put("department", course.getDepartment());
        document.put("deptShort", course.getDeptShort());
        document.put("courseNo", course.getCourseNo());
        document.put("sectionNo", course.getSectionNo());
        document.put("prerequisites", course.getPrerequisites());
        document.put("instructor", course.getInstructor());
        document.put("credits", course.getCredits());
        document.put("totalSeats", course.getTotalSeats());
        document.put("openSeats", course.getOpenSeats());
//        document.put("meetingTimes", course.getMeetingTimes());
        document.put("description", course.getDescription());

        documentCodec.encode(bsonWriter, document, encoderContext);
    }

    @Override
    public Class<Course> getEncoderClass() {
        return Course.class;
    }
}
