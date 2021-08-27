package com.revature.portal.datasource.converters;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.revature.portal.datasource.models.PreReq;
import org.bson.Document;

/**
 *  Converts Mongo Documents to/from PreReqs
 */
public class PreReqConverter {

    public Document convert(PreReq preReq) {
        Document document = new Document();
        document.put("department", preReq.getDepartment());
        document.put("courseNo", preReq.getCourseNo());
        document.put("credits", preReq.getCredits());

        return document;
    }

    public PreReq convert(Document document) {
        PreReq preReq = new PreReq();
        preReq.setDepartment(document.getString("department"));
        preReq.setCourseNo(document.getInteger("courseNo"));
        preReq.setCredits(document.getInteger("credits"));

        return preReq;
    }
}
