package br.com.redhat.consulting.model;

import java.util.HashMap;
import java.util.Map;

public enum PersonType {

    CONSULTANT_PARTNER  (1, "Partner Consultant"),
    CONSULTANT_REDHAT   (2, "Red Hat Consultant"),
    MANAGER_PARTNER     (3, "Partner Manager"),
    MANAGER_REDHAT      (4, "Red Hat Manager");
    
    private int id;
    private String description;
    
    private PersonType(int id, String description) {
        this.id = id;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }
    
    public static PersonType find(int _id) {
        PersonType[] sts = values();
        for (int i = 0; i < sts.length; i++) {
            if (_id == sts[i].getId())
                return sts[i];
        }
        return null;
    }
    
    public Map<String, Object> toMap() {
        Map<String, Object> map1 = new HashMap<>(1);
        map1.put("id", getId());
        map1.put("name", getDescription());
        return map1;
    }
    
    
}
