package br.com.redhat.consulting.model.dto;

import java.util.List;

public class RoleDTO  {

    private static final long serialVersionUID = 1L;
    
    private Integer id;
    private String name;
    private String description;
    private List<PersonDTO> persons;
    
    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<PersonDTO> getPersons() {
        return persons;
    }

    public void setPersons(List<PersonDTO> persons) {
        this.persons = persons;
    }

    @Override
    public String toString() {
        return "RoleDTO [name=" + name + ", id=" + getId() + "]";
    }
    
}
