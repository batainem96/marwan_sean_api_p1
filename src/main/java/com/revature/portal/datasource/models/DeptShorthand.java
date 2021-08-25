package com.revature.portal.datasource.models;

import java.util.HashMap;
import java.util.Map;

public class DeptShorthand {
    public static Map<String, String> deptToShort = new HashMap<String, String>() {{
       put("Computer Science", "COSC");
       put("Mathematics", "MATH");
       put("English", "ENGL");
       put("History", "HIST");
       put("Biology", "BIOL");
       put("Chemistry", "CHEM");
       put("Psychology", "PSYC");
       put("Physics", "PHYS");

       put("TestDepartment", "TEST");
    }};
}
