package br.com.redhat.consulting.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Contact {

    private String name;
    private Long telephone;
    
    public Contact() {}
    
    public Contact(String name, Long telephone) {
        this.name = name;
        this.telephone = telephone;
    }
    
    @Column(name="name", nullable=false)
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    @Column(name="telephone", nullable=false)
    public Long getTelephone() {
        return telephone;
    }
    
    public void setTelephone(Long telephone) {
        this.telephone = telephone;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((telephone == null) ? 0 : telephone.hashCode());
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
        Contact other = (Contact) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (telephone == null) {
            if (other.telephone != null)
                return false;
        } else if (!telephone.equals(other.telephone))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Contact [name=" + name + "]";
    }
    
}
