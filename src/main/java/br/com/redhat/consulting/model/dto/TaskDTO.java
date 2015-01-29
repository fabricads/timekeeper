package br.com.redhat.consulting.model.dto;


public class TaskDTO  {

    private static final long serialVersionUID = 1L;
    
    private Integer id;
    private String name;
    
    public TaskDTO() { }
    
    public TaskDTO(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Task [id=" + getId() + ", name=" + name + "]";
    }

}
