package br.com.redhat.consulting.model.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.redhat.consulting.model.PersonType;

public class PersonDTO  {

	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private String oraclePAId;
	private String name;
	private String email;
	private String password;
	private String city;
	private String state;
	private String country;
	private String telephone1;
	private String telephone2;
	private PartnerOrganizationDTO organization;
	
	private RoleDTO role;
	
	private List<TimecardDTO> timecards = new ArrayList<>();
	private List<ProjectDTO> projects = new ArrayList<>();
	
	// type: consultant partner, redhat manager
	private Integer personType;
	
	private boolean enabled;
	
	private Date registered;
	private Date lastModification;
	
	public PersonDTO() {}
	
    public PersonDTO(String oraclePAId, String name, String email, String city, String state, PartnerOrganizationDTO partnerOrganization) {
        super();
        this.oraclePAId = oraclePAId;
        this.name = name;
        this.email = email;
        this.city = city;
        this.state = state;
        this.organization = partnerOrganization;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

	public String getOraclePAId() {
		return oraclePAId;
	}

	public void setOraclePAId(String oraclePAId) {
		this.oraclePAId = oraclePAId;
	}

    public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

    public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

    public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

    public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

    public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getTelephone1() {
		return telephone1;
	}

	public void setTelephone1(String telephone1) {
		this.telephone1 = telephone1;
	}

	public String getTelephone2() {
		return telephone2;
	}

	public void setTelephone2(String telephone2) {
		this.telephone2 = telephone2;
	}

    public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Date getRegistered() {
		return registered;
	}

	public void setRegistered(Date registered) {
		this.registered = registered;
	}

	public Date getLastModification() {
		return lastModification;
	}

	public void setLastModification(Date lastModification) {
		this.lastModification = lastModification;
	}
	
    public PartnerOrganizationDTO getOrganization() {
        return organization;
    }

    public void setOrganization(PartnerOrganizationDTO partnerOrganization) {
        this.organization = partnerOrganization;
    }

    public RoleDTO getRole() {
        return role;
    }

    public void setRoleDTO(RoleDTO role) {
        this.role = role;
    }
    
    public Integer getPersonType() {
        return personType;
    }

    public void setPersonType(Integer personType) {
        this.personType = personType;
    }
    
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    // consultant is the attribute name in the TimecardEntryDTO class
    public List<TimecardDTO> getTimecards() {
        return timecards;
    }

    public void setTimecardsDTO(List<TimecardDTO> timecards) {
        this.timecards = timecards;
    }

    public List<ProjectDTO> getProjects() {
        return projects;
    }

    public void setProjectsDTO(List<ProjectDTO> projects) {
        this.projects = projects;
    }
    
    public void addProject(ProjectDTO project) {
        projects.add(project);
    }

    @Override
	public String toString() {
		return "PersonDTO [oraclePAId=" + oraclePAId + ", name=" + name
				+ ", email=" + email + ", id=" + getId() + "]";
	}

    
    
	
}
