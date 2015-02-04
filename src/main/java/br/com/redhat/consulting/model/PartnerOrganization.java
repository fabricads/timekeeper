package br.com.redhat.consulting.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

@Entity
@Table(name="partner_org")
@Audited
public class PartnerOrganization extends AbstractEntity {

    private static final long serialVersionUID = 1L;
    
    private String name;
    private boolean enabled;
    private List<Person> persons = new ArrayList<>();
    
    private Date registered;
    
    public PartnerOrganization() { }
    
    public PartnerOrganization(String name, boolean enabled) {
        this.name = name;
        this.enabled = enabled;
    }

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id_partner_org")
    public Integer getId() {
        return super.getId();
    }

    @NotNull
    @Column(unique=true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @OneToMany(mappedBy="partnerOrganization")
    public List<Person> getPersons() {
        return persons;
    }

    public void setPersons(List<Person> persons) {
        this.persons = persons;
    }

    public Date getRegistered() {
        return registered;
    }

    public void setRegistered(Date registered) {
        this.registered = registered;
    }

    @Override
    public String toString() {
        return "PartnerOrganization [name=" + name + ", enabled=" + enabled + ", id=" + getId() + "]";
    }
    
    

    
}
