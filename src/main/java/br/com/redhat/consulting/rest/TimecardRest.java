package br.com.redhat.consulting.rest;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.security.RolesAllowed;
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
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.redhat.consulting.config.Authenticated;
import br.com.redhat.consulting.model.Person;
import br.com.redhat.consulting.model.Project;
import br.com.redhat.consulting.model.Task;
import br.com.redhat.consulting.model.Timecard;
import br.com.redhat.consulting.model.TimecardEntry;
import br.com.redhat.consulting.model.TimecardStatusEnum;
import br.com.redhat.consulting.model.dto.PersonDTO;
import br.com.redhat.consulting.model.dto.TimecardDTO;
import br.com.redhat.consulting.model.dto.TimecardEntryDTO;
import br.com.redhat.consulting.services.PersonService;
import br.com.redhat.consulting.services.ProjectService;
import br.com.redhat.consulting.services.TaskService;
import br.com.redhat.consulting.services.TimecardService;
import br.com.redhat.consulting.util.GeneralException;

@RequestScoped
@Path("/timecard")
@Authenticated
public class TimecardRest {

    private static Logger LOG = LoggerFactory.getLogger(TimecardRest.class);
    
    @Inject
    private TimecardService timecardService;
    
    @Inject
    private ProjectService projectService;
    
    @Inject
    private PersonService personService;
    
    @Inject
    private TaskService taskService;
    
    @Inject
    private Validator validator;

    @Path("/list-cs")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @RolesAllowed({"partner_consultant"})
    public Response listTimecardsByCS(@QueryParam("id") Integer consultantId) {
        return listTimecards(null, consultantId);
    }
    
    
    @Path("/list")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @RolesAllowed({"redhat_manager", "admin"})
    public Response listTimecardsAll(@QueryParam("pm") Integer pmId) {
        return listTimecards(pmId, null);
    }
    
    public Response listTimecards(Integer pmId, Integer consultantId) {
        List<Timecard> timecards = null;
        List<TimecardDTO> timecardsDto = null;
        Response.ResponseBuilder response = null;
       
        try {
            if (consultantId != null) {
                timecards = timecardService.findByConsultant(consultantId);
            } else {
                timecards = timecardService.findAll();
            }
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
    
    @Path("/list/project/{prjId}")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public Response listTimecardsByProject(@PathParam("prjId") Integer prjId) {
        List<Timecard> timecards = null;
        List<TimecardDTO> timecardsDto = null;
        Response.ResponseBuilder response = null;
        
        try {
            timecards = timecardService.findByProject(prjId);
            if (timecards.size() == 0) {
                Map<String, Object> responseObj = new HashMap<>();
                responseObj.put("msg", "No timecards found");
                responseObj.put("timecards", new ArrayList());
                response = Response.ok(responseObj);
            } else {
                timecardsDto = new ArrayList<TimecardDTO>(timecards.size());
                for (Timecard timecard: timecards) {
                    TimecardDTO timecardDto = new TimecardDTO();
                    PersonDTO pmDto = new PersonDTO();
                    BeanUtils.copyProperties(timecardDto, timecard);
                    timecardsDto.add(timecardDto);
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
        LOG.debug(timecardDto.toString());
        Response.ResponseBuilder response = null;
        try {
            timecardDto.setStatusEnum(TimecardStatusEnum.IN_PROGRESS);
            
            Timecard timecardEnt = timecardDto.toTimecard();
            Long count = timecardService.countByDate(timecardDto.getConsultant().getId(), timecardDto.getProject().getId(), timecardDto.getInitDate(), timecardDto.getEndDate());
            if (count > 0) {
                Map<String, String> responseObj = new HashMap<String, String>();
                responseObj.put("error", "Timecard existant with start date "+ timecardDto.getInitDate() + " and end date "+ timecardDto.getEndDate() + " specified.");
                response = Response.status(Response.Status.CONFLICT).entity(responseObj);
            } else {
            
                // validate if the logged user is equals to the consultant 
                Person consultant = new Person();
                consultant.setId(timecardDto.getConsultant().getId());
                timecardEnt.setConsultant(consultant);
                
                Project prj = new Project();
                prj.setId(timecardDto.getProject().getId());
                timecardEnt.setProject(prj);
                
                List<Date> dates = new ArrayList<>();
                for (TimecardEntryDTO tcEntryDto: timecardDto.getTimecardEntries()) {
                    dates.add(tcEntryDto.getDay());
                    TimecardEntry tcEntry = new TimecardEntry();
                    BeanUtils.copyProperties(tcEntry, tcEntryDto);
                    timecardEnt.addTimecardEntry(tcEntry);
                    Task task = new Task();
                    task.setId(tcEntryDto.getTaskDTO().getId());
                    tcEntry.setTask(task);
                }
                
                timecardService.persist(timecardEnt);
                response = Response.ok();
            }
        } catch (ConstraintViolationException e) {
            response = createViolationResponse("Error to insert project.", e.getConstraintViolations());
        } catch (Exception e) {
            LOG.error("Error to insert project.", e);
            Map<String, String> responseObj = new HashMap<String, String>();
            responseObj.put("error", e.getMessage());
            response = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
        }
        return response.build();
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
