package br.com.redhat.consulting.model.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.JoinColumn;

public class ProjectDTO  {

    private static final long serialVersionUID = 1L;

    private Integer id;
    private String name;
    private String description;
    
    // oracle PA number
    private Integer paNumber;
    
    // Red Hat project manager
    private PersonDTO projectManager;
    private List<PersonDTO> consultants = new ArrayList<>();
    private boolean enabled;
    private Boolean usePMSubstitute;

    private Date initialDate;
    private Date endDate;
    
    private Date registered;
    private Date lastModification;
    
    private List<TimecardDTO> timecards = new ArrayList<>();
    private List<String> tasks = new ArrayList<>();
    
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

    public Integer getPaNumber() {
        return paNumber;
    }

    public void setPaNumber(Integer paNumber) {
        this.paNumber = paNumber;
    }

    public PersonDTO getProjectManagerDTO() {
        return projectManager;
    }

    public void setProjectManagerDTO(PersonDTO projectManager) {
        this.projectManager = projectManager;
    }

    public List<PersonDTO> getConsultants() {
        return consultants;
    }

    public void setConsultantsDTO(List<PersonDTO> consultants) {
        this.consultants = consultants;
    }
    
    public void addConsultant(PersonDTO consultant) {
        consultants.add(consultant);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean active) {
        this.enabled = active;
    }

    public Date getRegistered() {
        return registered;
    }

    public void setRegistered(Date registered) {
        this.registered = registered;
    }

    public Date getLastModification() {
        return lastModification;
    }

    public void setLastModification(Date lastModification) {
        this.lastModification = lastModification;
    }

    public List<TimecardDTO> getTimecards() {
        return timecards;
    }

    public void setTimecardsDTO(List<TimecardDTO> timecards) {
        this.timecards = timecards;
    }

    public Date getInitialDate() {
        return initialDate;
    }

    public void setInitialDate(Date initialDate) {
        this.initialDate = initialDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Boolean isUsePMSubstitute() {
        return usePMSubstitute;
    }

    public void setUsePMSubstitute(Boolean usePMSubstitute) {
        this.usePMSubstitute = usePMSubstitute;
    }

    public List<String> getTasks() {
        return tasks;
    }

    public void setTasks(List<String> tasks) {
        this.tasks = tasks;
    }

    @Override
    public String toString() {
        return "ProjectDTO [id=" + getId() + ", name=" + name + ", paNumber=" + paNumber + ", pm=" + projectManager + "]";
    }
    
    

    
}
