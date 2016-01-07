package br.com.redhat.consulting.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

@Entity
@Table(name="organization")
@Audited
@DynamicUpdate
public class PartnerOrganization extends AbstractEntity {

    private static final long serialVersionUID = 1L;
    
    private String name;
    private Boolean enabled;
    private List<Person> persons = new ArrayList<>();
    private List<Contact> contacts = new ArrayList<>();
    
    private Date registered;
    
    public PartnerOrganization() { }
    
    public PartnerOrganization(String name, boolean enabled) {
        this.name = name;
        this.enabled = enabled;
    }

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id_org")
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

    public Boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    @OneToMany(mappedBy="partnerOrganization")
    public List<Person> getPersons() {
        return persons;
    }

    public void setPersons(List<Person> persons) {
        this.persons = persons;
    }

    @Column(name="registration_date")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getRegistered() {
        return registered;
    }

    public void setRegistered(Date registered) {
        this.registered = registered;
    }

    @ElementCollection
    @CollectionTable(name="org_contact", joinColumns=@JoinColumn(name="id_org"))
    @NotAudited
    @OrderColumn(name="id_contact")
    public List<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }
    
    public void addContact(Contact contact) {
        this.contacts.add(contact);
    }

    @Override
    public String toString() {
        return "PartnerOrganization [name=" + name + ", enabled=" + enabled + ", id=" + getId() + "]";
    }
    
    

    
}
