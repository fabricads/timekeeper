package br.com.redhat.consulting.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.validation.Validator;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.redhat.consulting.model.Person;
import br.com.redhat.consulting.model.PersonType;
import br.com.redhat.consulting.services.PersonService;
import br.com.redhat.consulting.util.GeneralException;

@RequestScoped
@Path("/person")
public class PersonRest {

    private static Logger LOG = LoggerFactory.getLogger(PersonRest.class);
    
    @Inject
    private Validator validator;
    
    @Inject
    private PersonService personService;
    
    @Path("/pms")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public List<Person> listProjectManagers() {
        List<Person> pms = null;
        try {
            pms = personService.findProjectMangers();
        } catch (GeneralException e) {
            String msg = "Error searching for project managers.";
            LOG.error(msg, e);
        }
        return pms;
    }
    
    @Path("/list")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public List<Person> listPersons() {
        List<Person> persons = null;
        try {
            persons = personService.findPersons();
//            for (Person p: persons) {
//                p.getPartnerOrganization().getPersons();
//            }
        } catch (GeneralException e) {
            String msg = "Error searching for persons.";
            LOG.error(msg, e);
        }
        return persons;
    }
    
    @Path("/consultants")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public List<Person> listConsultants() {
        List<Person> persons = null;
        try {
            persons = personService.findConsultants();
//            for (Person p: persons) {
//                p.getPartnerOrganization().getPersons();
//            }
        } catch (GeneralException e) {
            String msg = "Error searching for consultants.";
            LOG.error(msg, e);
        }
        return persons;
    }
    
    
    @Path("/types")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public List<Map<String, String>> personTypes() {
        List<Map<String, String>> types = new ArrayList<>();
        types.add(PersonType.CONSULTANT_PARTNER.toMap());
        types.add(PersonType.MANAGER_REDHAT.toMap());
        return types;
    }

    @Path("/{pd}")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public Person get(@PathParam("pd") @DefaultValue("-1") int personId) {
        Person person = null;
        try {
            person = personService.findById(personId);
            if (person != null) {
                person.getProjects();
                person.getPartnerOrganization();
                person.getRole();
            }
        } catch (Exception e) {
            LOG.error("Error to get personId="+personId, e);
            Map<String, String> responseObj = new HashMap<String, String>();
            responseObj.put("error", e.getMessage());
//            builder = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
        }
        return person;
    }
    
    @Path("/{pd}/disable")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public Person disable(@PathParam("pd") @DefaultValue("-1") int personId) {
        Person person = null;
        try {
            personService.disable(personId);
        } catch (GeneralException e) {
            LOG.error("Error to insert person.", e);
            Map<String, String> responseObj = new HashMap<String, String>();
            responseObj.put("error", e.getMessage());
//            builder = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
        }
        return person;
    }
    
    @Path("/{pd}/enable")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public Person enable(@PathParam("pd") @DefaultValue("-1") int personId) {
        Person person = null;
        try {
            personService.enable(personId);
        } catch (GeneralException e) {
            LOG.error("Error to insert person.", e);
            Map<String, String> responseObj = new HashMap<String, String>();
            responseObj.put("error", e.getMessage());
//            builder = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
        }
        return person;
    }
    
    @Path("/{pd}/delete")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public Person delete(@PathParam("pd") @DefaultValue("-1") int personId) {
        Person person = null;
        try {
            personService.delete(personId);
        } catch (GeneralException e) {
            LOG.error("Error to insert person.", e);
            Map<String, String> responseObj = new HashMap<String, String>();
            responseObj.put("error", e.getMessage());
//            builder = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
        }
        return person;
    }

    
    @Path("/save")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @POST
    public Response savePerson(Person person) {

        Response.ResponseBuilder builder = null;
        try {
            validate(person);
            personService.persist(person);
            builder = Response.ok();
        } catch (ConstraintViolationException ce) {
            LOG.error("Error to insert person.", ce);
            builder = createViolationResponse(person.getName(), ce.getConstraintViolations());
        } catch (ValidationException e) {
            LOG.error("Error to insert person.", e);
            Map<String, String> responseObj = new HashMap<String, String>();
            responseObj.put("error", e.getMessage());
            builder = Response.status(Response.Status.CONFLICT).entity(responseObj);
        } catch (Exception e) {
            LOG.error("Error to insert person.", e);
            Map<String, String> responseObj = new HashMap<String, String>();
            responseObj.put("error", e.getMessage());
            builder = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
        }

        return builder.build();
    }

    
    private void validate(Person person) throws ConstraintViolationException, ValidationException {
        // Create a bean validator and check for issues.
        Set<ConstraintViolation<Person>> violations = validator.validate(person);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(violations));
        }

    }

    /**
     * Creates a JAX-RS "Bad Request" response including a map of all violation fields, and their message. This can then be used
     * by clients to show violations.
     *
     * @param violations A set of violations that needs to be reported
     * @return JAX-RS response containing all violations
     */
    private Response.ResponseBuilder createViolationResponse(String msg, Set<ConstraintViolation<?>> violations) {
        LOG.info("Validation completed for Pedido: " + msg + " . " + violations.size() + " violations found: ");

        Map<String, String> responseObj = new HashMap<String, String>();

        for (ConstraintViolation<?> violation : violations) {
            responseObj.put(violation.getPropertyPath().toString(), violation.getMessage());
        }

        return Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
    }

    
}
