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
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.apache.commons.beanutils.BeanUtils;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import br.com.redhat.consulting.model.dto.PersonDTO;

@Entity
@Table(name="person")
@Audited
public class Person extends AbstractEntity {

	private static final long serialVersionUID = 1L;
	
	private String oraclePAId;
	private String name;
	private String email;
	private String password;
	private String city;
	private String state;
	private String country;
	private String telephone1;
	private String telephone2;
	private PartnerOrganization partnerOrganization;
	
	private Role role;
	
	private List<Timecard> timecards = new ArrayList<>();
	private List<Project> projects = new ArrayList<>();
	
	// type: consultant partner, redhat manager
	private Integer personType;
	
	private boolean enabled;
	
	private Date registered;
	private Date lastModification;
	
	public Person() {}
	
    public Person(String oraclePAId, String name, String email, String city, String state, PartnerOrganization partnerOrganization) {
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

    @Column(name="oracle_pa_id")
	public String getOraclePAId() {
		return oraclePAId;
	}

	public void setOraclePAId(String oraclePAId) {
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

	@Column
	public String getTelephone1() {
		return telephone1;
	}

	public void setTelephone1(String telephone1) {
		this.telephone1 = telephone1;
	}

	@Column
	public String getTelephone2() {
		return telephone2;
	}

	public void setTelephone2(String telephone2) {
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

	@Column
	public Date getRegistered() {
		return registered;
	}

	public void setRegistered(Date registered) {
		this.registered = registered;
	}

	@Column(name="last_modification")
	public Date getLastModification() {
		return lastModification;
	}

	public void setLastModification(Date lastModification) {
		this.lastModification = lastModification;
	}
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="id_partner_org")
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
    
    @Column
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

    @ManyToMany(mappedBy="consultants")
    public List<Project> getProjects() {
        return projects;
    }

    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }
    
    public void addProject(Project project) {
        projects.add(project);
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
        setProjects(null);
        setTelephone1(null);
        setTelephone2(null);
        setTimecards(null);
    }

    @Override
	public String toString() {
        return "Person [id=" + getId() + ", name=" + name + ", email=" + email + "]";
	}

}
