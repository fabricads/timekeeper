package br.com.redhat.consulting.model.filter;

import br.com.redhat.consulting.model.PartnerOrganization;

public class PartnerOrganizationSearchFilter extends PartnerOrganization {

    private String partialName;
    private boolean joinPersons;
    private boolean joinContacts;

    public String getPartialName() {
        return partialName;
    }

    public void setPartialName(String partialName) {
        this.partialName = partialName;
    }

    public boolean isJoinPersons() {
        return joinPersons;
    }

    public void setJoinPersons(boolean joinPersons) {
        this.joinPersons = joinPersons;
    }

    public boolean isJoinContacts() {
        return joinContacts;
    }

    public void setJoinContacts(boolean joinContacts) {
        this.joinContacts = joinContacts;
    }

}
