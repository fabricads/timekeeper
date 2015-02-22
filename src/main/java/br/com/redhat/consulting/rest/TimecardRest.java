package br.com.redhat.consulting.rest;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
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
import br.com.redhat.consulting.model.dto.ProjectDTO;
import br.com.redhat.consulting.model.dto.TaskDTO;
import br.com.redhat.consulting.model.dto.TimecardDTO;
import br.com.redhat.consulting.model.dto.TimecardEntryDTO;
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
    
    @Context
    private HttpServletRequest httpReq;
    
    @Path("/list-cs")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @RolesAllowed({"partner_consultant"})
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
                response = Response.status(Status.NOT_FOUND).entity(responseObj);
            } else {
                timecardsDto = new ArrayList<TimecardDTO>(timecards.size());
                for (Timecard timecard: timecards) {
                    TimecardDTO tcDto = new TimecardDTO(timecard);
//                    BeanUtils.copyProperties(tcDto, timecard);
                    ProjectDTO prjDto = new ProjectDTO(timecard.getProject());
//                    BeanUtils.copyProperties(prjDto, timecard.getProject());
                    PersonDTO consultantDto = new PersonDTO(timecard.getConsultant());
                    timecard.getConsultant().nullifyAttributes();
//                    BeanUtils.copyProperties(consultantDto, timecard.getConsultant());
                    tcDto.setConsultantDTO(consultantDto);
                    tcDto.setProjectDTO(prjDto);
                    List<TimecardEntryDTO> tceDtos = new ArrayList<>(timecard.getTimecardEntries().size());
                    for (TimecardEntry tce: timecard.getTimecardEntries()) {
                        TimecardEntryDTO tceDto = new TimecardEntryDTO(tce);
//                        BeanUtils.copyProperties(tceDto, tce);
                        tceDtos.add(tceDto);
                    }
                    tcDto.setFirstDate(tceDtos.get(0).getDay());
                    tcDto.setLastDate(tceDtos.get(tceDtos.size() - 1).getDay());
                    tcDto.setTimecardEntriesDTO(tceDtos);
                    timecardsDto.add(tcDto);
                }
                response = Response.ok(timecardsDto);
            }
        } catch (GeneralException e) {
            LOG.error("Error to find projects.", e);
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("error", e.getMessage());
            response = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
        }
        return response.build();
    }
    
    @Path("/{tcId}")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @RolesAllowed({"partner_consultant", "redhat_manager", "admin"})
    public Response getTimecard(@PathParam("tcId") Integer tcId) {
        Timecard timecard = null;
        Response.ResponseBuilder response = null;
        PersonDTO loggedUser = Util.loggedUser(httpReq);
        
        try {
            if (tcId != null) {
                if (loggedUser.isAdminOrProjectManager()) {
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
    
    @Path("/save")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @POST
    @RolesAllowed({"partner_consultant"})
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
            } else {
                Timecard timecardEnt = timecardDto.toTimecard();
                timecardEnt.setConsultant(loggedUser.toPerson());
                Project prj = timecardDto.getProject().toProject();
                timecardEnt.setProject(prj);
                for (TimecardEntryDTO tcEntryDto: timecardDto.getTimecardEntriesDTO()) {
                    TimecardEntry tcEntry = tcEntryDto.toTimecardEntry();
                    tcEntry.setTask(tcEntryDto.getTaskDTO().toTask());
                    tcEntry.setTimecard(timecardEnt);
                    timecardEnt.addTimecardEntry(tcEntry);
                }
                timecardService.persist(timecardEnt);
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
