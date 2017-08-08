package br.com.redhat.consulting.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.ejb.criteria.predicate.BooleanExpressionPredicate;
import org.hibernate.envers.Audited;

@Table(name="timecard")
@Entity
@Audited
@DynamicUpdate
public class Timecard extends AbstractEntity {

    private static final long serialVersionUID = 1L;
    
    private Project project;
    private Person consultant;
    private Integer status;
    private String commentConsultant;
    private String commentPM;
    private boolean onPA;
    private List<TimecardEntry> timecardEntries = new ArrayList<>();
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id_timecard")
    public Integer getId() {
        return super.getId();
    }

    @ManyToOne
    @JoinColumn(name="id_project")
    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }
    	
    @Column(name = "on_pa", columnDefinition = "boolean default false", nullable = false)
    public boolean isOnPA() {
        return onPA;
    }

    public void setOnPA(boolean onPA) {
        this.onPA = onPA;
    } 
    	
    @ManyToOne
    @JoinColumn(name="id_consultant")
    public Person getConsultant() {
        return consultant;
    }

    public void setConsultant(Person consultant) {
        this.consultant = consultant;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Transient
    public TimecardStatusEnum getStatusEnum() {
        return TimecardStatusEnum.find(getStatus());
    }
    
    @Transient
    public String getStatusDescription() {
        return getStatusEnum().getDescription();
    }
    
    public void setStatusEnum(TimecardStatusEnum projectStatusEnum) {
        setStatus(projectStatusEnum.getId());
    }
    
    @Column(name="comment_consultant")
    public String getCommentConsultant() {
        return commentConsultant;
    }

    public void setCommentConsultant(String commentConsultant) {
        this.commentConsultant = commentConsultant;
    }

    @Column(name="comment_pm")
    public String getCommentPM() {
        return commentPM;
    }

    public void setCommentPM(String commentPM) {
        this.commentPM = commentPM;
    }

    @OneToMany(mappedBy="timecard", cascade=CascadeType.ALL)
    public List<TimecardEntry> getTimecardEntries() {
        return timecardEntries;
    }

    public void setTimecardEntries(List<TimecardEntry> timecardEntries) {
        this.timecardEntries = timecardEntries;
    }
    
    public void addTimecardEntry(TimecardEntry tce) {
        timecardEntries.add(tce);
    }

    @Override
    public String toString() {
        return "TimecardEntry [id=" + getId() + ", project=" + project + ", consultant=" + consultant + "]";
    }
}