package br.com.redhat.consulting.model.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.redhat.consulting.model.Timecard;
import br.com.redhat.consulting.model.TimecardStatusEnum;

public class TimecardDTO  {

    private static final long serialVersionUID = 1L;

    private Integer id;
    private ProjectDTO project;
    private PersonDTO consultant;
    private Integer status;
    private String commentConsultant;
    private String commentPM;
    private List<TimecardEntryDTO> timecardEntries = new ArrayList<>();
    private Date initDate;
    private Date endDate;
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public List<TimecardEntryDTO> getTimecardEntries() {
        return timecardEntries;
    }

    public void setTimecardEntries(List<TimecardEntryDTO> timecardEntries) {
        this.timecardEntries = timecardEntries;
    }
    
    public void addTimecardEntry(TimecardEntryDTO tce) {
        timecardEntries.add(tce);
    }

    public Date getInitDate() {
        return initDate;
    }

    public void setInitDate(Date initDate) {
        this.initDate = initDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return "TimecardEntryDTO [id=" + getId() + ", project=" + project + ", consultant=" + consultant + "]";
    }
    
    public Timecard toTimecard() {
        Timecard tc = new Timecard();
        tc.setCommentConsultant(commentConsultant);
        tc.setCommentPM(commentPM);
        tc.setId(id);
        tc.setStatus(status);
        return tc;
    }
    
}
