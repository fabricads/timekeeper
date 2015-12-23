package br.com.redhat.consulting.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

/**
 * 
 * Association entity to person and task, to avoid saving/updating the project entity only to associate tasks to consultants. 
 *
 */

@Entity
@Table(name="person_task")
@Audited(targetAuditMode=RelationTargetAuditMode.NOT_AUDITED)
public class PersonTask extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    private Person consultant;
    private Task task;

    public PersonTask() { }
    
    public PersonTask(Person consultant, Task task) {
        this.consultant = consultant;
        this.task = task;
    }

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id_person_task")
    public Integer getId() {
        return super.getId();
    }
    
    @ManyToOne
    @JoinColumn(name="id_person", nullable=false)
    public Person getConsultant() {
        return consultant;
    }

    public void setConsultant(Person consultant) {
        this.consultant = consultant;
    }

    @ManyToOne
    @JoinColumn(name="id_task", nullable=false)
    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

}
