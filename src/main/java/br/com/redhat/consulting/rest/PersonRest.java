package br.com.redhat.consulting.rest;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.redhat.consulting.config.Authenticated;
import br.com.redhat.consulting.model.PartnerOrganization;
import br.com.redhat.consulting.model.Person;
import br.com.redhat.consulting.model.PersonType;
import br.com.redhat.consulting.model.Role;
import br.com.redhat.consulting.model.dto.ConsultantDTO;
import br.com.redhat.consulting.model.dto.PartnerOrganizationDTO;
import br.com.redhat.consulting.model.dto.PersonDTO;
import br.com.redhat.consulting.model.dto.RoleDTO;
import br.com.redhat.consulting.services.PersonService;
import br.com.redhat.consulting.services.ProjectService;
import br.com.redhat.consulting.util.GeneralException;

@RequestScoped
@Path("/person")
@Authenticated
public class PersonRest {

    private static Logger LOG = LoggerFactory.getLogger(PersonRest.class);
    private static int FIND_PM = 1;
    private static int FIND_ALL= 2;
    private static int FIND_CONSULTANTS = 3;
    
    @Inject
    private PersonService personService;
    
    @Inject
    private ProjectService projectService;
    
    @Path("/pms")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @RolesAllowed({"redhat_manager", "admin"})
    public Response listProjectManagers(@QueryParam("e") @DefaultValue("1") Integer listDisabled) {
        return list(FIND_PM, listDisabled);
    }
    
    @Path("/list")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @RolesAllowed({"redhat_manager", "admin"})
    public Response listPersons(@QueryParam("e") @DefaultValue("1") Integer listDisabled) {
        return list(FIND_ALL, listDisabled);
    }
    
    @Path("/consultants")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @RolesAllowed({"redhat_manager", "admin"})
    public Response listConsultants(@QueryParam("e") @DefaultValue("1") Integer listDisabled) {
        return list(FIND_CONSULTANTS, listDisabled);
    }
    
    private Response list(int type, int listType) {
        List<Person> persons = null;
        List<PersonDTO> personsDto = null;
        Response.ResponseBuilder response = null;
        try {
            int LIST_ENABLED = 1;
            Boolean listOnlyEnabled = null;
            if (listType == LIST_ENABLED)
                listOnlyEnabled = true;
            if (type == FIND_PM) 
                persons = personService.findProjectMangers();
            else if (type == FIND_CONSULTANTS)
                persons = personService.findConsultants();
            else if (type == FIND_ALL) {
                persons = personService.findPersons(listOnlyEnabled);
            }
            
            if (persons.size() == 0) {
                Map<String, Object> responseObj = new HashMap<>();
                responseObj.put("msg", "No project managers found");
                responseObj.put("persons", new ArrayList());
                response = Response.ok(responseObj);
            } else {
                personsDto = new ArrayList<PersonDTO>(persons.size());
                for (Person p: persons) {
                    PersonDTO personDto = new PersonDTO(p);
                    PartnerOrganization org = p.getPartnerOrganization();
                    PartnerOrganizationDTO orgDto = new PartnerOrganizationDTO(org);
                    Role role = p.getRole();
                    if (type == FIND_ALL) 
                        // TODO
//                        personDto.setNumberOfProjects(p.getProjects().size());
                    if (p.getPersonTypeEnum().equals(PersonType.MANAGER_REDHAT)) { 
                        int nrProjects = projectService.countProjectsByPM(p.getId()).intValue();
                        personDto.setNumberOfProjects(nrProjects);
                    }
                    RoleDTO roleDto = new RoleDTO();
                    BeanUtils.copyProperties(roleDto, role);
                    personDto.setOrganization(orgDto);
                    personDto.setRoleDTO(roleDto);
                    // exclude fields from response
                    personDto.setPassword(null);
                    personsDto.add(personDto);
                    
                }
                response = Response.ok(personsDto);
            }
        } catch (GeneralException | IllegalAccessException | InvocationTargetException e) {
            LOG.error("Error to find project managers.", e);
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("error", e.getMessage());
            response = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
        }
        return response.build();
    }
    
    
    
    @Path("/consultant-list")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @RolesAllowed({"redhat_manager", "admin"})
    public Response listConsultants() {
    	List<Person> persons = null;
        List<ConsultantDTO> consultantDTOs = null;
        Response.ResponseBuilder response = null;
        try {
            persons = personService.findConsultants();
            if (persons.size() == 0) {
                Map<String, Object> responseObj = new HashMap<>();
                responseObj.put("msg", "No project managers found");
                responseObj.put("persons", new ArrayList());
                response = Response.ok(responseObj);
            } else {
            	consultantDTOs = new ArrayList<ConsultantDTO>(persons.size());
                for (Person p: persons) {
                	ConsultantDTO consultantDTO = new ConsultantDTO(p);
                	consultantDTO.setPassword(null);
                    consultantDTOs.add(consultantDTO);
                }
                response = Response.ok(consultantDTOs);
            }
        } catch (GeneralException e) {
            LOG.error("Error to find project managers.", e);
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("error", e.getMessage());
            response = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
        }
        return response.build();
    }
    
      
    @Path("/types")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @RolesAllowed({"redhat_manager", "admin"})
    public List<Map<String, Object>> personTypes() {
        List<Map<String, Object>> types = new ArrayList<>();
        types.add(PersonType.CONSULTANT_PARTNER.toMap());
        types.add(PersonType.MANAGER_REDHAT.toMap());
        types.add(PersonType.MANAGER_PARTNER.toMap());
        return types;
    }

    @Path("/{pd}")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @RolesAllowed({"redhat_manager", "admin"})
    public Response get(@PathParam("pd") @DefaultValue("-1") int personId) {
        Person person = null;
        Response.ResponseBuilder response = null;
        try {
            person = personService.findById(personId);
            if (person == null) {
                Map<String, String> responseObj = new HashMap<>();
                responseObj.put("error", "Person " + personId + " not found.");
                response = Response.status(Response.Status.NOT_FOUND).entity(responseObj);
            } else {
                PersonDTO personDto = new PersonDTO(person);
                PartnerOrganization org = person.getPartnerOrganization();
                PartnerOrganizationDTO orgDto = new PartnerOrganizationDTO(org);
                personDto.setOrganization(orgDto);
                Role role = person.getRole();
                RoleDTO roleDto = new RoleDTO();
                BeanUtils.copyProperties(roleDto, role);
                personDto.setRoleDTO(roleDto);
                personDto.setPassword(null);
                response = Response.ok(personDto);
            }
        } catch (Exception e) {
            LOG.error("Error to find person.", e);
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("error", e.getMessage());
            response = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
        }
        return response.build();
    }
    
    @Path("/{pd}/disable")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @RolesAllowed({"redhat_manager", "admin"})
    public Response disable(@PathParam("pd") @DefaultValue("-1") int personId) {
        Response.ResponseBuilder response = null;
        try {
            personService.disable(personId);
            response = Response.ok();
        } catch (GeneralException e) {
            LOG.error("Error to disable organization.", e);
            Map<String, String> responseObj = new HashMap<String, String>();
            responseObj.put("error", e.getMessage());
            response = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
        }
        return response.build();
    }
    
    @Path("/{pd}/enable")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @RolesAllowed({"redhat_manager", "admin"})
    public Response enable(@PathParam("pd") @DefaultValue("-1") int personId) {
        Response.ResponseBuilder response = null;
        try {
            personService.enable(personId);
            response = Response.ok();
        } catch (GeneralException e) {
            LOG.error("Error to disable organization.", e);
            Map<String, String> responseObj = new HashMap<String, String>();
            responseObj.put("error", e.getMessage());
            response = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
        }
        return response.build();
    }
    
    @Path("/{pd}/delete")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @RolesAllowed({"redhat_manager", "admin"})
    public Response remove(@PathParam("pd") @DefaultValue("-1") int personId) {
        Response.ResponseBuilder response = null;
        try {
            personService.remove(personId);
            response = Response.ok();
        } catch (GeneralException e) {
            LOG.error("Error to disable organization.", e);
            Map<String, String> responseObj = new HashMap<String, String>();
            responseObj.put("error", e.getMessage());
            response = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
        }
        return response.build();
    }
    

    
    @Path("/save")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @POST
    @RolesAllowed({"redhat_manager", "admin"})
    public Response savePerson(PersonDTO personDto) {
        Response.ResponseBuilder builder = null;
        try {
            if (personDto != null && StringUtils.isBlank(personDto.getName())) {
                Map<String, String> responseObj = new HashMap<String, String>();
                responseObj.put("error", "Person name must not be empty.");
                builder = Response.status(Response.Status.CONFLICT).entity(responseObj);
                return builder.build();
            }
            Person personEnt = personService.findByName(personDto.getName());
            if (personEnt != null && (personEnt.getId() != personDto.getId())) {
                Map<String, String> responseObj = new HashMap<String, String>();
                responseObj.put("error", "Person with duplicated name: " + personDto.getName());
                builder = Response.status(Response.Status.CONFLICT).entity(responseObj);
            } else {
                Person person = personDto.toPerson();
                PartnerOrganization org = new PartnerOrganization();
                Role role = new Role();
                BeanUtils.copyProperties(org, personDto.getOrganization());
                BeanUtils.copyProperties(role, personDto.getRole());
                person.setRole(role);
                person.setPartnerOrganization(org);
                personService.persist(person);
                builder = Response.ok(personDto);
            }
        } catch (ConstraintViolationException e) {
            builder = createViolationResponse("Error to insert person.", e.getConstraintViolations());
        } catch (Exception e) {
            LOG.error("Error to insert person.", e);
            Map<String, String> responseObj = new HashMap<String, String>();
            responseObj.put("error", e.getMessage());
            builder = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
        }
        return builder.build();
    }

    private Response.ResponseBuilder createViolationResponse(String msg, Set<ConstraintViolation<?>> violations) {
        LOG.info("Validation completed for Person: " + msg + " . " + violations.size() + " violations found: ");
        Map<String, String> responseObj = new HashMap<String, String>();
        for (ConstraintViolation<?> violation : violations) {
            responseObj.put("error", "Field " + violation.getPropertyPath().toString() + ": " + violation.getMessage());
        }
        return Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
    }    
}
