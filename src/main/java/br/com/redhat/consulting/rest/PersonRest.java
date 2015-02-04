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
import br.com.redhat.consulting.model.dto.PartnerOrganizationDTO;
import br.com.redhat.consulting.model.dto.PersonDTO;
import br.com.redhat.consulting.model.dto.RoleDTO;
import br.com.redhat.consulting.services.PersonService;
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
    
    @Path("/pms")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @RolesAllowed({"redhat_manager", "admin"})
    public Response listProjectManagers() {
        return list(FIND_PM);
    }
    
    @Path("/list")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @RolesAllowed({"redhat_manager", "admin"})
    public Response listPersons() {
        return list(FIND_ALL);
    }
    
    @Path("/consultants")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @RolesAllowed({"redhat_manager", "admin"})
    public Response listConsultants() {
        return list(FIND_CONSULTANTS);
    }
    
    private Response list(int type) {
        List<Person> persons = null;
        List<PersonDTO> personsDto = null;
        Response.ResponseBuilder response = null;
        try {
            if (type == FIND_PM) 
                persons = personService.findProjectMangers();
            else if (type == FIND_CONSULTANTS)
                persons = personService.findConsultants();
            else if (type == FIND_ALL)
                persons = personService.findPersons();
            
            if (persons.size() == 0) {
                Map<String, Object> responseObj = new HashMap<>();
                responseObj.put("msg", "No project managers found");
                responseObj.put("persons", new ArrayList());
                response = Response.ok(responseObj);
            } else {
                personsDto = new ArrayList<PersonDTO>(persons.size());
                for (Person p: persons) {
                    PersonDTO personDto = new PersonDTO();
                    PartnerOrganization org = p.getPartnerOrganization();
                    PartnerOrganizationDTO orgDto = new PartnerOrganizationDTO();
                    Role role = p.getRole();
                    if (type == FIND_ALL) 
                        personDto.setNumberOfProjects(p.getProjects().size());
                    RoleDTO roleDto = new RoleDTO();
                    BeanUtils.copyProperties(orgDto, org);
                    BeanUtils.copyProperties(roleDto, role);
                    BeanUtils.copyProperties(personDto, p);
                    personDto.setOrganization(orgDto);
                    personDto.setRoleDTO(roleDto);
                    personsDto.add(personDto);
                    
                    // exclude fields from response
                    personDto.setPassword(null);
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
    
    @Path("/types")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @RolesAllowed({"redhat_manager", "admin"})
    public List<Map<String, Object>> personTypes() {
        List<Map<String, Object>> types = new ArrayList<>();
        types.add(PersonType.CONSULTANT_PARTNER.toMap());
        types.add(PersonType.MANAGER_REDHAT.toMap());
        return types;
    }

    @Path("/{pd}")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @RolesAllowed({"redhat_manager", "admin"})
    public Response get(@PathParam("pd") @DefaultValue("-1") int personId) {
        PersonDTO personDto = new PersonDTO();
        Person person = null;
        Response.ResponseBuilder response = null;
        try {
            person = personService.findById(personId);
            if (person == null) {
                Map<String, String> responseObj = new HashMap<>();
                responseObj.put("error", "Person " + personId + " not found.");
                response = Response.status(Response.Status.NOT_FOUND).entity(responseObj);
            } else {
                BeanUtils.copyProperties(personDto, person);
                PartnerOrganization org = person.getPartnerOrganization();
                PartnerOrganizationDTO orgDto = new PartnerOrganizationDTO();
                BeanUtils.copyProperties(orgDto, org);
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
    public Response delete(@PathParam("pd") @DefaultValue("-1") int personId) {
        Response.ResponseBuilder response = null;
        try {
            personService.delete(personId);
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
