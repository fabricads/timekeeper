package br.com.redhat.consulting.model.filter;

import br.com.redhat.consulting.model.PartnerOrganization;

public class PartnerOrganizationSearchFilter extends PartnerOrganization {
    
    private String partialName;

    public String getPartialName() {
        return partialName;
    }

    public void setPartialName(String partialName) {
        this.partialName = partialName;
    }

}
