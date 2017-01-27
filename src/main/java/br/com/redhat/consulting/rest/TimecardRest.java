package br.com.redhat.consulting.rest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Date;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.New;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.text.SimpleDateFormat;

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
import br.com.redhat.consulting.model.dto.ProjectDTO;
import br.com.redhat.consulting.model.dto.TaskDTO;
import br.com.redhat.consulting.model.dto.TimecardDTO;
import br.com.redhat.consulting.model.dto.TimecardEntryDTO;
import br.com.redhat.consulting.services.AlertService;
import br.com.redhat.consulting.services.PersonService;
import br.com.redhat.consulting.services.ProjectService;
import br.com.redhat.consulting.services.TimecardService;
import br.com.redhat.consulting.util.GeneralException;
import br.com.redhat.consulting.util.TimecardEntryDateComparator;
import br.com.redhat.consulting.util.Util;

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
    private  PersonService personService;
    
    @Context
    private HttpServletRequest httpReq;
    
    @Inject
    private AlertService alertService;
    
    @Path("/list-cs")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @RolesAllowed({"partner_consultant","redhat_manager"})
    public Response listTimecardsByCS(@QueryParam("id") Integer consultantId) {
        Response response = null;
        if (consultantId == null) {
            Map<String, Object> responseObj = new HashMap<>();
            responseObj.put("msg", "No timecards found");
            responseObj.put("timecards", new ArrayList());
            response = Response.status(Status.NOT_FOUND).entity(responseObj).build();
        } else {
            response = listTimecards(null, consultantId);
        }
        return response;
    }

    @Path("/list-partner")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @RolesAllowed({"partner_manager"})
    public Response listTimecardsByPartner() {
        Response.ResponseBuilder response = null;
        try{
            PersonDTO loggedUser = Util.loggedUser(httpReq);
            Person person = personService.findById(loggedUser.getId());
            int orgId = person.getPartnerOrganization().getId();
            List<Timecard> timecards = timecardService.findByOrganization(orgId);
            if(timecards!=null){
                List<TimecardDTO> timecardDTOs = new ArrayList<>(timecards.size());
                
                 for (Timecard timecard: timecards) {
                    TimecardDTO tcDto = new TimecardDTO(timecard);
                    ProjectDTO prjDto = new ProjectDTO(timecard.getProject());
                    for (Task task: timecard.getProject().getTasks()) {
                    	prjDto.addTask(new TaskDTO(task));
                    }
                    PersonDTO consultantDto = new PersonDTO(timecard.getConsultant());
                    timecard.getConsultant().nullifyAttributes();
                    tcDto.setConsultantDTO(consultantDto);
                    tcDto.setProjectDTO(prjDto);
                    List<TimecardEntryDTO> tceDtos = new ArrayList<>(timecard.getTimecardEntries().size());
                    for (TimecardEntry tce: timecard.getTimecardEntries()) {
                        TimecardEntryDTO tceDto = new TimecardEntryDTO(tce);
                        tceDtos.add(tceDto);
                    }
                    /**
                    *needs to sort time card entries
                    */
                    Collections.sort(tceDtos, new TimecardEntryDateComparator());
                    tcDto.setFirstDate(tceDtos.get(0).getDay());
                    tcDto.setLastDate(tceDtos.get(tceDtos.size() - 1).getDay());
                    tcDto.setTimecardEntriesDTO(tceDtos);
                    timecardDTOs.add(tcDto);
                }
                response=Response.ok(timecardDTOs);
            }else{
                response=Response.ok(new ArrayList());
            }
        } catch (GeneralException e) {
            LOG.error("Error to find projects.", e);
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("error", e.getMessage());
            response=Response.ok(responseObj);
        }
        return response.build();
    }
    
    
    @Path("/list")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @RolesAllowed({"redhat_manager", "admin"})
    public Response listTimecardsAll(@QueryParam("pm") Integer pmId) {
        return listTimecards(pmId, null);
    }
    
    @Path("/list-pending")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @RolesAllowed({"redhat_manager", "admin"})
    public Response listTimecardsAllPending() {
        return listPending();
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
                /*Map<String, Object> responseObj = new HashMap<>();
                responseObj.put("msg", "No timecards found");
                responseObj.put("timecards", new ArrayList());
                response = Response.status(Status.NOT_FOUND).entity(responseObj);*/
                timecardsDto = new ArrayList<TimecardDTO>(0);
            } else {
                timecardsDto = new ArrayList<TimecardDTO>(timecards.size());
                LOG.info("Total of timecards= "+timecards.size());
                for (Timecard timecard: timecards) {
                    TimecardDTO tcDto = new TimecardDTO(timecard);
                    ProjectDTO prjDto = new ProjectDTO(timecard.getProject());
                    for (Task task: timecard.getProject().getTasks()) {
                    	prjDto.addTask(new TaskDTO(task));
                    }
                    PersonDTO consultantDto = new PersonDTO(timecard.getConsultant());
                    timecard.getConsultant().nullifyAttributes();
                    tcDto.setConsultantDTO(consultantDto);
                    tcDto.setProjectDTO(prjDto);
                    List<TimecardEntryDTO> tceDtos = new ArrayList<>(timecard.getTimecardEntries().size());
                    for (TimecardEntry tce: timecard.getTimecardEntries()) {
                        TimecardEntryDTO tceDto = new TimecardEntryDTO(tce);
                        tceDto.setTaskDTO(new TaskDTO(tce.getTask()));
                        tceDtos.add(tceDto);
                    }
                    /**
                    *needs to sort time card entries
                    */
                    Collections.sort(tceDtos, new TimecardEntryDateComparator());
                    tcDto.setFirstDate(tceDtos.get(0).getDay());
                    tcDto.setLastDate(tceDtos.get(tceDtos.size() - 1).getDay());
                    tcDto.setTimecardEntriesDTO(tceDtos);
                    timecardsDto.add(tcDto);
                }
                //response = Response.ok(timecardsDto);
            }
        } catch (GeneralException e) {
            LOG.error("Error to find projects.", e);
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("error", e.getMessage());
            response = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
        }
        response = Response.ok(timecardsDto);
        return response.build();
    }

    public Response listPending() {
        List<Timecard> timecards = null;
        List<TimecardDTO> timecardsDto = null;
        Response.ResponseBuilder response = null;
       
        try {
            timecards = timecardService.findPending();
           
            if (timecards.size() == 0) {
                timecardsDto = new ArrayList<TimecardDTO>(0);
            } else {
                timecardsDto = new ArrayList<TimecardDTO>(timecards.size());
                LOG.info("Total of timecards= "+timecards.size());
                for (Timecard timecard: timecards) {
                    TimecardDTO tcDto = new TimecardDTO(timecard);
                    ProjectDTO prjDto = new ProjectDTO(timecard.getProject());
                    for (Task task: timecard.getProject().getTasks()) {
                    	prjDto.addTask(new TaskDTO(task));
                    }
                    PersonDTO consultantDto = new PersonDTO(timecard.getConsultant());
                    timecard.getConsultant().nullifyAttributes();
                    tcDto.setConsultantDTO(consultantDto);
                    tcDto.setProjectDTO(prjDto);
                    List<TimecardEntryDTO> tceDtos = new ArrayList<>(timecard.getTimecardEntries().size());
                    for (TimecardEntry tce: timecard.getTimecardEntries()) {
                        TimecardEntryDTO tceDto = new TimecardEntryDTO(tce);
                        tceDto.setTaskDTO(new TaskDTO(tce.getTask()));
                        tceDtos.add(tceDto);
                    }
                    /**
                    *needs to sort time card entries
                    */
                    Collections.sort(tceDtos, new TimecardEntryDateComparator());
                    tcDto.setFirstDate(tceDtos.get(0).getDay());
                    tcDto.setLastDate(tceDtos.get(tceDtos.size() - 1).getDay());
                    tcDto.setTimecardEntriesDTO(tceDtos);
                    timecardsDto.add(tcDto);
                }
            }
        } catch (GeneralException e) {
            LOG.error("Error to find projects.", e);
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("error", e.getMessage());
            response = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
        }
        response = Response.ok(timecardsDto);
        return response.build();
    }
    
    @Path("/{tcId}")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @RolesAllowed({"partner_consultant", "redhat_manager","partner_manager", "admin"})
    public Response getTimecard(@PathParam("tcId") Integer tcId) {

        Timecard timecard = null;
        Response.ResponseBuilder response = null;
        PersonDTO loggedUser = Util.loggedUser(httpReq);
        
        try {
            if (tcId != null) {
                if (loggedUser.isAdminOrProjectManager() || loggedUser.isPartnerManager()) {
                    timecard = timecardService.findByIdAndConsultant(tcId, null);
                } else {
                    timecard = timecardService.findByIdAndConsultant(tcId, loggedUser.getId());
                }
                if (timecard != null) {
                    TimecardDTO tcDto = new TimecardDTO(timecard);
                    ProjectDTO prjDto = new ProjectDTO(timecard.getProject());
    
                    for (Task task: timecard.getProject().getTasks()) {
                        TaskDTO taskDto = new TaskDTO(task);
                        prjDto.addTask(taskDto);
                    }
                    tcDto.setProjectDTO(prjDto);
                    List<TimecardEntryDTO> tceDtos = new ArrayList<>(timecard.getTimecardEntries().size());
                    for (TimecardEntry tce: timecard.getTimecardEntries()) {
                        TimecardEntryDTO tceDto = new TimecardEntryDTO(tce);
                        tceDto.setTaskDTO(new TaskDTO(tce.getTask()));
                        tceDtos.add(tceDto); 
                    }
                    Collections.sort(tceDtos, new TimecardEntryDateComparator());
                    tcDto.setFirstDate(tceDtos.get(0).getDay());
                    tcDto.setLastDate(tceDtos.get(tceDtos.size() - 1).getDay());
                    tcDto.setTimecardEntriesDTO(tceDtos);
                    response = Response.ok(tcDto);
                }
                 
            }
            if (tcId == null || timecard == null) {
                Map<String, Object> responseObj = new HashMap<>();
                responseObj.put("error", "Timecard not found");
                responseObj.put("timecards", new ArrayList());
                response = Response.status(Status.NOT_FOUND).entity(responseObj);
            }
        } catch (GeneralException e) {
            LOG.error("Error to find projects.", e);
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("error", e.getMessage());
            response = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
        }
        return response.build();
    }
    
    @Path("/today")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @RolesAllowed({"partner_consultant"})
    public Response getToday() {
        Response.ResponseBuilder response = null;
        try {
        	Date date = new Date();
            response = Response.status(Response.Status.OK).entity(date);
        } catch (Exception e) {
            LOG.error("Error to insert project.", e);
            Map<String, String> responseObj = new HashMap<String, String>();
            responseObj.put("error", e.getMessage());
            response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseObj);
        }
        return response.build();
    }

    @Path("/count/{projectId}/{fistDate}/{lastDate}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @RolesAllowed({"partner_consultant"})
    public Response countByProjectPeriod(@PathParam("projectId") Integer projectId,@PathParam("fistDate") String fistDate,@PathParam("lastDate") String lastDate) {
        Response.ResponseBuilder response = null;
        try {    
            PersonDTO loggedUser = Util.loggedUser(httpReq);
            response = null;
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date fisrtD = simpleDateFormat.parse(fistDate);
            Date lastD = simpleDateFormat.parse(lastDate);
            Long count = timecardService.countByDate(loggedUser.getId(), projectId, fisrtD, lastD);
            Map<String, Object> responseObj = new HashMap<>();
            responseObj.put("count", count);
            responseObj.put("date", fisrtD);        
            response = Response.status(Response.Status.OK).entity(responseObj);
        } catch (Exception e) {
            LOG.error("Error to insert project.", e);
            Map<String, String> responseObj = new HashMap<String, String>();
            responseObj.put("error", e.getMessage());
            response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseObj);
        }
        return response.build();
    }
    
    @Path("/save")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @POST
    @RolesAllowed({"partner_consultant", "redhat_manager" , "admin"})
    public Response save(TimecardDTO timecardDto) {
        LOG.debug(timecardDto.toString());
        LOG.debug("timecard status: " + timecardDto.getStatus());
        Response.ResponseBuilder response = null;
        PersonDTO loggedUser = Util.loggedUser(httpReq);
        timecardDto.setStatusEnum(TimecardStatusEnum.find(timecardDto.getStatus()));
        try {
            
            if (timecardDto.getId() == null) {
                Timecard timecardEnt = timecardDto.toTimecard();
                Person cs = loggedUser.toPerson();
                timecardEnt.setConsultant(cs);
                Long count = timecardService.countByDate(timecardDto.getConsultant().getId(), timecardDto.getProject().getId(), timecardDto.getFirstDate(), timecardDto.getLastDate());
                if (count > 0) {
                    Map<String, String> responseObj = new HashMap<String, String>();
                    responseObj.put("error", "Cannot save timecard existant with start date "+ timecardDto.getFirstDate() + " and end date "+ timecardDto.getLastDate() + " specified.");
                    response = Response.status(Response.Status.CONFLICT).entity(responseObj);
                } else {
                    Project prj = new Project();
                    prj.setId(timecardDto.getProject().getId());
                    timecardEnt.setProject(prj);
                    for (TimecardEntryDTO tcEntryDto: timecardDto.getTimecardEntriesDTO()) {
                        TimecardEntry tcEntry = tcEntryDto.toTimecardEntry();
//                        BeanUtils.copyProperties(tcEntry, tcEntryDto);
                        timecardEnt.addTimecardEntry(tcEntry);
                        Task task = new Task();
                        task.setId(tcEntryDto.getTaskDTO().getId());
                        tcEntry.setTask(task);
                    }
                    
                    timecardService.persist(timecardEnt);
                    response = Response.ok();
                }
            } else {
                Timecard timecardEnt = timecardDto.toTimecard();
                timecardEnt.setConsultant(loggedUser.toPerson());
                LOG.info("Received project "+timecardDto.getProject().toString());
                Project prj = timecardDto.getProject().toProject();
                LOG.info("Cast of the project "+prj.toString());
                timecardEnt.setProject(prj);
                for (TimecardEntryDTO tcEntryDto: timecardDto.getTimecardEntriesDTO()) {
                    TimecardEntry tcEntry = tcEntryDto.toTimecardEntry();
                    tcEntry.setTask(tcEntryDto.getTaskDTO().toTask());
                    tcEntry.setTimecard(timecardEnt);
                    timecardEnt.addTimecardEntry(tcEntry);
                }
                Timecard dbTimecard = timecardService.findById(timecardDto.getId());
                timecardService.persist(timecardEnt);
                if((timecardEnt.getStatusEnum() == TimecardStatusEnum.SUBMITTED)
                		&& (dbTimecard.getStatusEnum() != TimecardStatusEnum.SUBMITTED)){
                	alertService.alertSubmittedTimecard(dbTimecard);
                }
                response = Response.ok();
            }
        } catch (Exception e) {
            LOG.error("Error to insert project.", e);
            Map<String, String> responseObj = new HashMap<String, String>();
            responseObj.put("error", e.getMessage());
            response = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
        }
        return response.build();
    }
    
    @Path("/delete/{tcId}")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @RolesAllowed({"partner_consultant"})
    public Response delete(@PathParam("tcId") Integer tcId) {
        Response.ResponseBuilder response = null;
        PersonDTO loggedUser = Util.loggedUser(httpReq);
        
        try {
            if (tcId != null) {
                timecardService.delete(tcId, loggedUser.getId());
                response = Response.ok();
            } else  {
                Map<String, Object> responseObj = new HashMap<>();
                responseObj.put("msg", "Timecard not found");
                responseObj.put("timecards", new ArrayList());
                response = Response.status(Status.NOT_FOUND).entity(responseObj);
            }
        } catch (GeneralException e) {
            LOG.error("Error to delete timecard.", e);
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("error", e.getMessage());
            response = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
        }
        return response.build();
    }
    
    @Path("/on-pa/{tcId}")
    @Produces(MediaType.APPLICATION_JSON)
    @POST
    @RolesAllowed({"admin", "redhat_manager"})
    public Response setOnPa(@PathParam("tcId") Integer tcId) {
        Response.ResponseBuilder response = null;
        
        try {
            if (tcId != null) {
                timecardService.setOnPa(tcId);
                response = Response.ok();
            } else  {
                Map<String, Object> responseObj = new HashMap<>();
                responseObj.put("error", "Timecard not found");
                responseObj.put("timecards", new ArrayList());
                response = Response.status(Status.NOT_FOUND).entity(responseObj);
            }
        } catch (GeneralException e) {
            LOG.error("Error to delete timecard.", e);
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("error", e.getMessage());
            response = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
        }
        return response.build();
    }
    
    @Path("/app-rej/{tcId}")
    @Produces(MediaType.APPLICATION_JSON)
    @POST
    @RolesAllowed({"admin", "redhat_manager"})
    public Response approveOrReject(@PathParam("tcId") Integer tcId, @QueryParam("op") Integer op, String commentPM) {
        Response.ResponseBuilder response = null;
        
        try {
            if (tcId != null) {
                if (op == 1) 
                    timecardService.approve(tcId, commentPM);
                else if (op == 2)
                    timecardService.reject(tcId, commentPM);
                response = Response.ok();
            } else  {
                Map<String, Object> responseObj = new HashMap<>();
                responseObj.put("error", "Timecard not found");
                responseObj.put("timecards", new ArrayList());
                response = Response.status(Status.NOT_FOUND).entity(responseObj);
            }
        } catch (GeneralException e) {
            LOG.error("Error to delete timecard.", e);
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("error", e.getMessage());
            response = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
        }
        return response.build();
    }
    

}
