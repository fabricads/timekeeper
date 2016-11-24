package br.com.redhat.consulting.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

@Entity
@Table(name="person")
@Audited
@DynamicUpdate
public class Person extends AbstractEntity {

	private static final long serialVersionUID = 1L;
	
	private Integer oraclePAId;
	private String name;
	private String email;
	private String password;
	private String city;
	private String state;
	private String country;
	private Long telephone1;
	private Long telephone2;
	private PartnerOrganization partnerOrganization;
	
	private Role role;
	
	private List<Timecard> timecards = new ArrayList<>();
	private List<Task> tasks = new ArrayList<>();
	
	// type: consultant partner, redhat manager
	private Integer personType;
	
	private boolean enabled;
	
	private Date registered;
	private Date lastModification;
	
	public Person() {}
	
	public Person(Integer id) {
	    setId(id);
	}
	
    public Person(Integer oraclePAId, String name, String email, String city, String state, PartnerOrganization partnerOrganization) {
        this.oraclePAId = oraclePAId;
        this.name = name;
        this.email = email;
        this.city = city;
        this.state = state;
        this.partnerOrganization = partnerOrganization;
    }

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id_person")
    public Integer getId() {
        return super.getId();
    }

    @Column(name="pa_number")
	public Integer getOraclePAId() {
		return oraclePAId;
	}

	public void setOraclePAId(Integer oraclePAId) {
		this.oraclePAId = oraclePAId;
	}

	@Column
	@NotNull
    public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column
	@NotNull
    public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Column
	@NotNull
    public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	@Column
	@NotNull
    public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	@Column
	@NotNull
    public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	@Column(name="telephone1")
	public Long getTelephone1() {
		return telephone1;
	}

	public void setTelephone1(Long telephone1) {
		this.telephone1 = telephone1;
	}

	@Column(name="telephone2")
	public Long getTelephone2() {
		return telephone2;
	}

	public void setTelephone2(Long telephone2) {
		this.telephone2 = telephone2;
	}

	@Column
	@NotNull
    public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Column(name="registration_date")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getRegistered() {
		return registered;
	}

	public void setRegistered(Date registered) {
		this.registered = registered;
	}

	@Column(name="last_modification")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getLastModification() {
		return lastModification;
	}

	public void setLastModification(Date lastModification) {
		this.lastModification = lastModification;
	}
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="id_org")
    public PartnerOrganization getPartnerOrganization() {
        return partnerOrganization;
    }

    public void setPartnerOrganization(PartnerOrganization partnerOrganization) {
        this.partnerOrganization = partnerOrganization;
    }

    @ManyToOne
    @JoinColumn(name="id_role")
    @Audited(targetAuditMode=RelationTargetAuditMode.NOT_AUDITED)
    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
    
    @Column(name="person_type")
    public Integer getPersonType() {
        return personType;
    }

    public void setPersonType(Integer personType) {
        this.personType = personType;
    }
    
    @Transient
    public PersonType getPersonTypeEnum() {
        return PersonType.find(getPersonType());
    }
    
    @Transient
    public String getPersonTypeEnumDescription() {
        return PersonType.find(getPersonType()).getDescription();
    }
    
    public void setPersonTypeEnum(PersonType _personType) {
        setPersonType(_personType.getId());
    }
    
    @Column(name="enabled")
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    // consultant is the attribute name in the TimecardEntry class
    @OneToMany(mappedBy="consultant")
    public List<Timecard> getTimecards() {
        return timecards;
    }

    public void setTimecards(List<Timecard> timecards) {
        this.timecards = timecards;
    }

    @ManyToMany(mappedBy="consultants")//,fetch=FetchType.EAGER)
    @Audited(targetAuditMode=RelationTargetAuditMode.NOT_AUDITED)
    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }
    
    public void addTask(Task task) {
        this.tasks.add(task);
    }
    public void removeTask(Task task) {
        this.tasks.remove(task);
    }
    
    /**
     *  null some attributes, so when this person is copied to a personDTO some attributes are not sent to the rest responde for security reasons.
     */
    public void nullifyAttributes() {
        setPassword(null);
        setOraclePAId(null);
        getPartnerOrganization().setPersons(null);
        setRole(null);
        setEnabled(false);
        setRegistered(null);
        setLastModification(null);
        setCity(null);
        setCountry(null);
        setTasks(null);
        setTelephone1(null);
        setTelephone2(null);
        setTimecards(null);
    }

    @Override
	public String toString() {
        return "Person [id=" + getId() + ", name=" + name + ", email=" + email + "]";
	}

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        Person other = (Person) obj;
        if (getId() == null) {
            if (other.getId() != null)
                return false;
        } else if (!getId().equals(other.getId()))
            return false;
        return true;
    }
    
    

}
