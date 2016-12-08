package br.com.redhat.consulting.model;

public enum RoleEnum {

    ADMIN               (1, "admin", "Admin"),
    PARTNER_CONSULTANT  (2, "partner_consultant", "Partner Consultant"),
    REDHAT_MANAGER      (3, "redhat_manager", "Red Hat Manager"),
    PARTNER_MANAGER      (4, "partner_manager", "PartnerManager");
    
    private int id;
    private String shortName;
    private String description;
    
    private RoleEnum(int id, String shortName, String description) {
        this.id = id;
        this.shortName = shortName;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }
    
    public String getShortName() {
        return shortName;
    }

    public static RoleEnum find(int _id) {
        RoleEnum[] sts = values();
        for (int i = 0; i < sts.length; i++) {
            if (_id == sts[i].getId())
                return sts[i];
        }
        return null;
    }
    
    public static RoleEnum findByShortname(String shortName) {
        RoleEnum[] sts = values();
        for (int i = 0; i < sts.length; i++) {
            if (shortName.equals(sts[i].getShortName())) 
                return sts[i];
        }
        return null;
    }
    
}
