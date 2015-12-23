package br.com.redhat.consulting.model;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Embeddable
public class PersonTaskId implements Serializable {

    private static final long serialVersionUID = 1L;

    @ManyToOne
    @JoinColumn(name="id_person", nullable=false)
    private Person consultant;
    
    @ManyToOne
    @JoinColumn(name="id_task", nullable=false)
    private Task task;

    public PersonTaskId() { }
    
    public PersonTaskId(Person consultant, Task task) {
        this.consultant = consultant;
        this.task = task;
    }

    public Person getConsultant() {
        return consultant;
    }

    public void setConsultant(Person consultant) {
        this.consultant = consultant;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((consultant == null) ? 0 : consultant.hashCode());
        result = prime * result + ((task == null) ? 0 : task.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PersonTaskId other = (PersonTaskId) obj;
        if (consultant == null) {
            if (other.consultant != null)
                return false;
        } else if (!consultant.equals(other.consultant))
            return false;
        if (task == null) {
            if (other.task != null)
                return false;
        } else if (!task.equals(other.task))
            return false;
        return true;
    }

    
    
}
