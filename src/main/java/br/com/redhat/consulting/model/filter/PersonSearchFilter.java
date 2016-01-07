package br.com.redhat.consulting.model.filter;

import java.util.List;

import br.com.redhat.consulting.model.Person;

public class PersonSearchFilter extends Person {

    private int[] types;
    private String partialName;
    private List<Integer> personIds;
    private String joinClause;

    public void setPersonTypes(int[] _types) {
        this.types = _types;
    }

    public int[] getPersonTypes() {
        return types;
    }

    public String getPartialName() {
        return partialName;
    }

    public void setPartialName(String partialName) {
        this.partialName = partialName;
    }

    public List<Integer> getPersonIds() {
        return personIds;
    }

    public void setPersonIds(List<Integer> personIds) {
        this.personIds = personIds;
    }

    public String getJoinClause() {
        return joinClause;
    }

    public void setJoinClause(String joinClause) {
        this.joinClause = joinClause;
    }


}
