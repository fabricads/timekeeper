package br.com.redhat.consulting.model.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PartnerOrganizationDTO  {

    private Integer id;
    private String name;
    private boolean enabled;
    private List<PersonDTO> persons = new ArrayList<>();
    private int numberOfPersons;
    
    private Date registered;
    
    public PartnerOrganizationDTO() { }
    
    public PartnerOrganizationDTO(String name, boolean enabled) {
        this.name = name;
        this.enabled = enabled;
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

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public List<PersonDTO> getPersons2() {
        return persons;
    }

    public void setPersons2(List<PersonDTO> persons) {
        this.persons = persons;
    }
    
    public Date getRegistered() {
        return registered;
    }

    public void setRegistered(Date registered) {
        this.registered = registered;
    }

    public int getNumberOfPersons() {
        return numberOfPersons;
    }

    public void setNumberOfPersons(int numberOfPersons) {
        this.numberOfPersons = numberOfPersons;
    }

    @Override
    public String toString() {
        return "PartnerOrganizationDTO DTO [name=" + name + ", enabled=" + enabled + ", id=" + getId() + "]";
    }
    
    

    
}
