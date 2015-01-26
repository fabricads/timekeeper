package br.com.redhat.consulting.model;

public enum TimecardStatusEnum {

    APPROVED     (1, "Approved"),
    REJECTED     (2, "Rejected"),
    IN_PROGRESS  (3, "In progress"),
    SUBMITTED    (4, "Submitted");
    
    private int id;
    private String description;
    
    private TimecardStatusEnum(int id, String description) {
        this.id = id;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }
    
    public static TimecardStatusEnum find(int _id) {
        TimecardStatusEnum[] sts = values();
        for (int i = 0; i < sts.length; i++) {
            if (_id == sts[i].getId())
                return sts[i];
        }
        return null;
    }

}
