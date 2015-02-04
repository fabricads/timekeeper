package br.com.redhat.consulting.model.dto;

import java.util.List;

public class RoleDTO  {

    private static final long serialVersionUID = 1L;
    
    private Integer id;
    private String name;
    private String shortName;
    private String description;
    private List<PersonDTO> persons;
    
    public RoleDTO() { }
    
    public RoleDTO(String name, String shortName) {
        this.name = name;
        this.shortName = shortName;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<PersonDTO> getPersons() {
        return persons;
    }

    public void setPersonsDTO(List<PersonDTO> persons) {
        this.persons = persons;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    @Override
    public String toString() {
        return "RoleDTO [name=" + name + ", id=" + getId() + "]";
    }
    
}
