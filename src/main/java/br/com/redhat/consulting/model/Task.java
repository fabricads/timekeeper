package br.com.redhat.consulting.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

@Entity
@Table(name="task")
@Audited
public class Task extends AbstractEntity {

    private String name;
    private Project project;
    private Integer taskType;
    
    private List<Person> consultants = new ArrayList<>();
    
    public Task() { }
    
    public Task(String name) {
        this.name = name;
    }

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id_task")
    public Integer getId() {
        return super.getId();
    }

    @NotNull
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ManyToOne
    @JoinColumn(name="id_project")
    @NotNull
    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }
    
    @Transient
    public TaskTypeEnum getTaskTypeEnum() {
        return TaskTypeEnum.find(getTaskType());
    }
    
    @Transient
    public String getTaskTypeEnumDescription() {
        return TaskTypeEnum.find(getTaskType()).getDescription();
    }

    @NotNull
    @Column(name="task_type")
    public Integer getTaskType() {
        return taskType;
    }

    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
    }
    
    @ManyToMany
    @JoinTable(name="person_task", joinColumns=@JoinColumn(name="id_task"), inverseJoinColumns=@JoinColumn(name="id_person"))
    public List<Person> getConsultants() {
        return consultants;
    }

    public void setConsultants(List<Person> consultants) {
        this.consultants = consultants;
    }
    
    public void addConsultant(Person consultant) {
        this.consultants.add(consultant);
        consultant.addTask(this);
    }

    @Override
    public String toString() {
        return "Task [id=" + getId() + ", name=" + name + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        Person other = (Person) obj;
        if (getId() == null) {
            if (other.getId() != null)
                return false;
        } else if (!getId().equals(other.getId()))
            return false;
        return true;
    }
    
    
}
