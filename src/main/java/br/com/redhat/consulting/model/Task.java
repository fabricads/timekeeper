package br.com.redhat.consulting.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

@Entity
@Table(name="task")
@Audited
public class Task extends AbstractEntity {

    private static final long serialVersionUID = 1L;
    
    private String name;
    private Project project;
    private Integer taskType;
    
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

    public Integer getTaskType() {
        return taskType;
    }

    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
    }

    @Override
    public String toString() {
        return "Task [id=" + getId() + ", name=" + name + "]";
    }

}
