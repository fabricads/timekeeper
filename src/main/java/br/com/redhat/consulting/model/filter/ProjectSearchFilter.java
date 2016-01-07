package br.com.redhat.consulting.model.filter;

import java.util.HashSet;
import java.util.Set;

import br.com.redhat.consulting.model.Person;
import br.com.redhat.consulting.model.Project;

public class ProjectSearchFilter extends Project {

    private String partialName;
    private Set<Person> consultants = new HashSet<>();
    private boolean joinTasks;
    private boolean joinConsultants;

    public String getPartialName() {
        return partialName;
    }

    public void setPartialName(String partialName) {
        this.partialName = partialName;
    }

    public Set<Person> getConsultants() {
        return consultants;
    }

    public void addConsultant(Person consultant) {
        this.consultants.add(consultant);
    }

    public boolean isJoinTasks() {
        return joinTasks;
    }

    public void setJoinTasks(boolean joinTasks) {
        this.joinTasks = joinTasks;
    }

    public boolean isJoinConsultants() {
        return joinConsultants;
    }

    public void setJoinConsultants(boolean joinConsultants) {
        this.joinConsultants = joinConsultants;
    }

}
