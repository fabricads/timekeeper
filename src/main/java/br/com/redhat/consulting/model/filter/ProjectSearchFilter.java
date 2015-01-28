package br.com.redhat.consulting.model.filter;

import br.com.redhat.consulting.model.Project;

public class ProjectSearchFilter extends Project {

    private String partialName;

    public String getPartialName() {
        return partialName;
    }

    public void setPartialName(String partialName) {
        this.partialName = partialName;
    }
    
}
