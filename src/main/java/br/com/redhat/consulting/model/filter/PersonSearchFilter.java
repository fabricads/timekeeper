package br.com.redhat.consulting.model.filter;

import br.com.redhat.consulting.model.Person;

public class PersonSearchFilter extends Person {
    
    private static final long serialVersionUID = 1L;
    
    private int[] types;
    
    public void setPersonTypes(int[] _types) {
        this.types = _types;
    }

    public int[] getPersonTypes() {
        return types;
    }
}
