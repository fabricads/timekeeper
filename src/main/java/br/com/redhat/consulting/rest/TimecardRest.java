package br.com.redhat.consulting.rest;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
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
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.redhat.consulting.model.Project;
import br.com.redhat.consulting.model.Timecard;
import br.com.redhat.consulting.model.dto.PersonDTO;
import br.com.redhat.consulting.model.dto.TimecardDTO;
import br.com.redhat.consulting.services.PersonService;
import br.com.redhat.consulting.services.TaskService;
import br.com.redhat.consulting.services.TimecardService;
import br.com.redhat.consulting.util.GeneralException;

@RequestScoped
@Path("/timecard")
public class TimecardRest {

    private static Logger LOG = LoggerFactory.getLogger(TimecardRest.class);
    
    @Inject
    private TimecardService timecardService;
    
    @Inject
    private PersonService personService;
    
    @Inject
    private TaskService taskService;
    
    @Inject
    private Validator validator;
    
    @Path("/list")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public Response listTimecards(@QueryParam("by-pm") Integer pmId) {
        List<Timecard> timecards = null;
        List<TimecardDTO> timecardsDto = null;
        Response.ResponseBuilder response = null;
       
        try {
            timecards = timecardService.findAll();
            if (timecards.size() == 0) {
                Map<String, Object> responseObj = new HashMap<>();
                responseObj.put("msg", "No timecards found");
                responseObj.put("timecards", new ArrayList());
                response = Response.ok(responseObj);
            } else {
                timecardsDto = new ArrayList<TimecardDTO>(timecards.size());
                for (Timecard timecard: timecards) {
                    TimecardDTO prjDto = new TimecardDTO();
                    PersonDTO pmDto = new PersonDTO();
                    BeanUtils.copyProperties(prjDto, timecard);
                    timecardsDto.add(prjDto);
                }
                response = Response.ok(timecardsDto);
            }
        } catch (GeneralException | IllegalAccessException | InvocationTargetException e) {
            LOG.error("Error to find projects.", e);
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
    public Response save(TimecardDTO timecardDto) {
        Response.ResponseBuilder builder = null;
        try {
            Timecard timecardEnt = new Timecard();
            
        } catch (ConstraintViolationException e) {
            builder = createViolationResponse("Error to insert project.", e.getConstraintViolations());
        } catch (Exception e) {
            LOG.error("Error to insert project.", e);
            Map<String, String> responseObj = new HashMap<String, String>();
            responseObj.put("error", e.getMessage());
            builder = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
        }
        return builder.build();
    }

    private void validate(Project project) throws ConstraintViolationException, ValidationException {
        // Create a bean validator and check for issues.
        Set<ConstraintViolation<Project>> violations = validator.validate(project);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(violations));
        }

        if (project.getEndDate().before(project.getInitialDate()) ) {
            throw new ValidationException("End date should not be before initial date.");
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
