package br.com.redhat.consulting.rest;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.redhat.consulting.config.Authenticated;
import br.com.redhat.consulting.model.Person;
import br.com.redhat.consulting.model.dto.PersonDTO;
import br.com.redhat.consulting.services.PersonService;
import br.com.redhat.consulting.util.Util;

@RequestScoped
@Path("/profile")
@RolesAllowed({"redhat_manager", "admin", "partner_consultant","partner_manager"})
@Authenticated
public class ProfileRest {

    private static Logger LOG = LoggerFactory.getLogger(ProfileRest.class);
    
    @Inject
    private PersonService personService;
    
    @Path("/{pd}")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public Response get(@PathParam("pd") int personId, @Context HttpServletRequest req) {
        Person person = null;
        Response.ResponseBuilder response = null;
        try {
            // check if the request id is the logged user.
            PersonDTO loggedUser = Util.loggedUser(req);
            if (loggedUser.getId().intValue() != personId) {
                Map<String, String> responseObj = new HashMap<>();
                responseObj.put("error", "Person " + personId + " not found.");
                response = Response.status(Response.Status.NOT_FOUND).entity(responseObj);
                LOG.warn(loggedUser + " trying to locate other user id " + personId);
            } else {
                person = personService.findById(personId);
                if (person == null) {
                    Map<String, String> responseObj = new HashMap<>();
                    responseObj.put("error", "Person " + personId + " not found.");
                    response = Response.status(Response.Status.NOT_FOUND).entity(responseObj);
                } else {
                    // exclude fields from response
                    person.setPersonType(null);
                    person.setOraclePAId(null);
                    person.setLastModification(null);
                    person.setPartnerOrganization(null);
                    person.setTasks(null);
                    person.setRegistered(null);
                    person.setRole(null);
                    person.setTimecards(null);
                    person.setPassword(null);
                    
                    PersonDTO personDto = new PersonDTO(person);
                    response = Response.ok(personDto);
                }
            }
        } catch (Exception e) {
            LOG.error("Error to find person.", e);
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("error", e.getMessage());
            response = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
        }
        return response.build();
    }
    
    @Path("/save")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @POST
    public Response savePerson(PersonDTO personDto, @Context HttpServletRequest req) {
        Response.ResponseBuilder builder = null;
        try {
            if (personDto != null && StringUtils.isBlank(personDto.getName())) {
                Map<String, String> responseObj = new HashMap<String, String>();
                responseObj.put("error", "Person name must not be empty.");
                builder = Response.status(Response.Status.CONFLICT).entity(responseObj);
                return builder.build();
            }
            PersonDTO loggedUser = Util.loggedUser(req);
            if (!loggedUser.getId().equals(personDto.getId())) {
                Map<String, String> responseObj = new HashMap<>();
                responseObj.put("error", "Person " + personDto.getId() + " not found.");
                builder = Response.status(Response.Status.NOT_FOUND).entity(responseObj);
                LOG.warn(loggedUser + " trying to save on top of other user id " + personDto.getId());
            } else {
                Person personEnt = personService.findByName(personDto.getName());
                if (personEnt != null && (personEnt.getId() != personDto.getId())) {
                    Map<String, String> responseObj = new HashMap<String, String>();
                    responseObj.put("error", "Person with duplicated name: " + personDto.getName());
                    builder = Response.status(Response.Status.CONFLICT).entity(responseObj);
                } else {
                    personEnt = personService.findById(personDto.getId());
                    personEnt.setName(personDto.getName());
                    personEnt.setEmail(personDto.getEmail());
                    personEnt.setPassword(personDto.getPassword());
                    personEnt.setCity(personDto.getCity());
                    personEnt.setState(personDto.getState());
                    personEnt.setCountry(personDto.getCountry());
                    personEnt.setTelephone1(personDto.getTelephone1());
                    personEnt.setTelephone2(personDto.getTelephone2());
                    
                    personService.persist(personEnt);
                    builder = Response.ok(personDto);
                }
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
