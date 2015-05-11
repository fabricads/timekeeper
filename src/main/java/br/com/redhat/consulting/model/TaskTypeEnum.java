package br.com.redhat.consulting.model;

public enum TaskTypeEnum {

    LABOR   (1, "Labor"),
    EXPENSE (2, "Expense");
    
    private int id;
    private String description;
    
    private TaskTypeEnum(int id, String description) {
        this.id = id;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }
    
    public static TaskTypeEnum find(int _id) {
        TaskTypeEnum[] sts = values();
        for (int i = 0; i < sts.length; i++) {
            if (_id == sts[i].getId())
                return sts[i];
        }
        return null;
    }

}
