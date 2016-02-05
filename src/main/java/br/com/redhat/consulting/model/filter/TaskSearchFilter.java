package br.com.redhat.consulting.model.filter;

import br.com.redhat.consulting.model.Task;

public class TaskSearchFilter extends Task {

    private boolean joinConsultants;

    public boolean isJoinConsultants() {
        return joinConsultants;
    }

    public void setJoinConsultants(boolean joinConsultants) {
        this.joinConsultants = joinConsultants;
    }
    
}
