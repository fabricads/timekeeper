package br.com.redhat.consulting.model.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import br.com.redhat.consulting.model.Project;

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

    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    @JsonDeserialize(using = CustomJsonDateDeserializer.class)
    private Date initialDate;
    
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    @JsonDeserialize(using = CustomJsonDateDeserializer.class)
    private Date endDate;
    
    private Date registered;
    private Date lastModification;
    
    private List<TimecardDTO> timecards = new ArrayList<>();
    private List<TaskDTO> tasks = new ArrayList<>();
    private List<Integer> tasksToRemove = new ArrayList<>();
    private Date lastFilledDay;
    private int qtyConsultants;
    
    public ProjectDTO() {}
    
    public ProjectDTO(Project prj) {
        this.id = prj.getId();
        this.name = prj.getName();
        this.description = prj.getDescription();
        this.paNumber = prj.getPaNumber();
        this.enabled = prj.isEnabled();
        this.usePMSubstitute = prj.isUsePMSubstitute();
        this.initialDate = prj.getInitialDate();
        this.endDate = prj.getEndDate();
        this.registered = prj.getRegistered();
        this.lastModification = prj.getLastModification();
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
    
    public void addTimecardDTO(TimecardDTO dto) {
        this.timecards.add(dto);
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

    public List<TaskDTO> getTasksDTO() {
        return tasks;
    }

    public void setTasksDTO(List<TaskDTO> tasks) {
        this.tasks = tasks;
    }
    
    public void addTask(TaskDTO task) {
        this.tasks.add(task);
    }

    public List<Integer> getTasksToRemove() {
        return tasksToRemove;
    }

    public void setTasksToRemove(List<Integer> tasksToRemove) {
        this.tasksToRemove = tasksToRemove;
    }
    
    public Date getLastFilledDay() {
        return lastFilledDay;
    }

    public void setLastFilledDay(Date lastFilledDay) {
        this.lastFilledDay = lastFilledDay;
    }
    
    public Project toProject() {
        Project prj = new Project();
        prj.setId(id);
        prj.setName(name);
        prj.setDescription(description);
        prj.setPaNumber(paNumber);
        prj.setEnabled(enabled);
        prj.setUsePMSubstitute(usePMSubstitute);
        prj.setInitialDate(initialDate);
        prj.setEndDate(endDate);
        prj.setRegistered(registered);
        prj.setLastModification(lastModification);
        return prj;
    }

    public int getQtyConsultants() {
        return qtyConsultants;
    }

    public void setQtyConsultants(int qtyConsultants) {
        this.qtyConsultants = qtyConsultants;
    }

    @Override
    public String toString() {
        return "ProjectDTO [id=" + getId() + ", name=" + name + ", paNumber=" + paNumber + ", pm=" + projectManager + "]";
    }
    
}
