package br.com.redhat.consulting.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.envers.Audited;

@Table(name="timecard_entry")
@Entity
@Audited
public class TimecardEntry extends AbstractEntity {

    private static final long serialVersionUID = 1L;
    
    private Timecard timecard;
    private Date day;
    private Double workedHours;
    private String workDescription;
    private String taskName;
    
    public TimecardEntry() { }
    
    public TimecardEntry(Timecard timecard, Date day, Double workedHours) {
        this.timecard = timecard;
        this.day = day;
        this.workedHours = workedHours;
    }

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id_timecard_entry")
    public Integer getId() {
        return super.getId();
    }

    @Temporal(TemporalType.DATE)
    public Date getDay() {
        return day;
    }

    public void setDay(Date day) {
        this.day = day;
    }

    @Column(name="worked_hours")
    public Double getWorkedHours() {
        return workedHours;
    }

    public void setWorkedHours(Double workedHours) {
        this.workedHours = workedHours;
    }
    
    @ManyToOne
    @JoinColumn(name="id_timecard")
    public Timecard getTimecard() {
        return timecard;
    }

    public void setTimecard(Timecard timecard) {
        this.timecard = timecard;
    }

    @Column(name="work_description")
    public String getWorkDescription() {
        return workDescription;
    }

    public void setWorkDescription(String workDescription) {
        this.workDescription = workDescription;
    }
    

}
