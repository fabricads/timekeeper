package br.com.redhat.consulting.model.dto;

import br.com.redhat.consulting.model.Person;


public class ConsultantDTO  {

	private Integer id;
	private Integer oraclePAId;
	private String name;
	private String email;
	private String password;
	private String city;
	private String state;
	private String country;
	private Long telephone1;
	private Long telephone2;
	
	public ConsultantDTO() {}
	
	public ConsultantDTO(Person p) {
	    id = p.getId();
	    oraclePAId = p.getOraclePAId();
	    name = p.getName();
	    email = p.getEmail();
	    password = p.getPassword();
	    city = p.getCity();
	    state = p.getState();
	    country = p.getCountry();
	    telephone1 = p.getTelephone1();
	    telephone2 = p.getTelephone2();
	}
	
    public ConsultantDTO(Integer oraclePAId, String name, String email, String city, String state) {
        this.oraclePAId = oraclePAId;
        this.name = name;
        this.email = email;
        this.city = city;
        this.state = state;

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

	public Integer getOraclePAId() {
		return oraclePAId;
	}

	public void setOraclePAId(Integer oraclePAId) {
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

	public Long getTelephone1() {
		return telephone1;
	}

	public void setTelephone1(Long telephone1) {
		this.telephone1 = telephone1;
	}

	public Long getTelephone2() {
		return telephone2;
	}

	public void setTelephone2(Long telephone2) {
		this.telephone2 = telephone2;
	}

    public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	
	
}
