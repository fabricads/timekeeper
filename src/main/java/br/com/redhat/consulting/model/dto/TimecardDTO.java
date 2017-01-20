package br.com.redhat.consulting.model.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import br.com.redhat.consulting.model.Timecard;
import br.com.redhat.consulting.model.TimecardStatusEnum;

@JsonIgnoreProperties(ignoreUnknown=true)
public class TimecardDTO  {

    private Integer id;
    private ProjectDTO project;
    private PersonDTO consultant;
    private Integer status;
    private String commentConsultant;
    private String commentPM;
    private List<TimecardEntryDTO> timecardEntries = new ArrayList<>();
    private Date firstDate;
    private Date lastDate;
    private boolean onPA;
    

    public TimecardDTO() {}
    
    public TimecardDTO(Timecard timecard) {
        this.id = timecard.getId();
        this.status = timecard.getStatus();
        this.commentConsultant = timecard.getCommentConsultant();
        this.commentPM = timecard.getCommentPM();
        this.onPA =timecard.isOnPA();
    }
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    
    public boolean isOnPA() {
        return onPA;
    }

    public void setOnPA(boolean onPA) {
        this.onPA = onPA;
    } 
    
    public ProjectDTO getProject() {
        return project;
    }

    public void setProjectDTO(ProjectDTO project) {
        this.project = project;
    }

    public PersonDTO getConsultant() {
        return consultant;
    }

    public void setConsultantDTO(PersonDTO consultant) {
        this.consultant = consultant;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public TimecardStatusEnum getStatusEnum() {
        return TimecardStatusEnum.find(getStatus());
    }
    
    public String getStatusDescription() {
        return getStatusEnum().getDescription();
    }
    
    @JsonIgnore
    public void setStatusEnum(TimecardStatusEnum projectStatusEnum) {
        setStatus(projectStatusEnum.getId());
    }
    
    public String getCommentConsultant() {
        return commentConsultant;
    }

    public void setCommentConsultant(String commentConsultant) {
        this.commentConsultant = commentConsultant;
    }

    public String getCommentPM() {
        return commentPM;
    }

    public void setCommentPM(String commentPM) {
        this.commentPM = commentPM;
    }

    public List<TimecardEntryDTO> getTimecardEntriesDTO() {
        return timecardEntries;
    }

    public void setTimecardEntriesDTO(List<TimecardEntryDTO> timecardEntries) {
        this.timecardEntries = timecardEntries;
    }
    
    public void addTimecardEntry(TimecardEntryDTO tce) {
        timecardEntries.add(tce);
    }

    public Date getFirstDate() {
        return firstDate;
    }

    public void setFirstDate(Date firstDate) {
        this.firstDate = firstDate;
    }

    public Date getLastDate() {
        return lastDate;
    }

    public void setLastDate(Date lastDate) {
        this.lastDate = lastDate;
    }

    @Override
    public String toString() {
        return "TimecardEntryDTO [id=" + getId() + ", project=" + project + ", consultant=" + consultant + ", firstDate=" + firstDate+ ", lastDate=" + lastDate + "]";
    }
    
    public Timecard toTimecard() {
        Timecard tc = new Timecard();
        tc.setCommentConsultant(commentConsultant);
        tc.setCommentPM(commentPM);
        tc.setId(id);
        tc.setStatus(status);
        tc.setOnPA(onPA);
        return tc;
    }
    
}
