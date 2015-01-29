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

import br.com.redhat.consulting.model.PartnerOrganization;
import br.com.redhat.consulting.model.Person;
import br.com.redhat.consulting.model.Project;
import br.com.redhat.consulting.model.Role;
import br.com.redhat.consulting.model.dto.PartnerOrganizationDTO;
import br.com.redhat.consulting.model.dto.PersonDTO;
import br.com.redhat.consulting.model.dto.ProjectDTO;
import br.com.redhat.consulting.model.dto.RoleDTO;
import br.com.redhat.consulting.services.PersonService;
import br.com.redhat.consulting.services.ProjectService;
import br.com.redhat.consulting.util.GeneralException;

@RequestScoped
@Path("/project")
public class ProjectRest {

    private static Logger LOG = LoggerFactory.getLogger(ProjectRest.class);
    
    @Inject
    private ProjectService projectService;
    
    @Inject
    private PersonService personService;
    
    @Inject
    private Validator validator;
    
    @Path("/list")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public Response listProjects(@QueryParam("by-pm") Integer pmId, @QueryParam("by-cs") Integer consultantId) {
        List<Project> projects = null;
        List<ProjectDTO> projectsDto = null;
        Response.ResponseBuilder response = null;
       
        try {
            if (pmId != null)
                projects = projectService.findByPM(pmId);
            else if (consultantId != null) 
                projects = projectService.findByConsultant(consultantId);
            else 
                projects = projectService.findAll();
            if (projects.size() == 0) {
                Map<String, Object> responseObj = new HashMap<>();
                responseObj.put("msg", "No projects found");
                responseObj.put("projects", new ArrayList());
                response = Response.ok(responseObj);
            } else {
                projectsDto = new ArrayList<ProjectDTO>(projects.size());
                for (Project project: projects) {
                    ProjectDTO prjDto = new ProjectDTO();
                    BeanUtils.copyProperties(prjDto, project);
                    projectsDto.add(prjDto);
                }
                response = Response.ok(projectsDto);
            }
        } catch (GeneralException | IllegalAccessException | InvocationTargetException e) {
            LOG.error("Error to find projects.", e);
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("error", e.getMessage());
            response = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
        }
        return response.build();
    }
    
    @Path("/{pr}")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public Response get(@PathParam("pr") @DefaultValue("-1") int projectId) {
        ProjectDTO projectDto = new ProjectDTO();
        Project projectEnt = null;
        Response.ResponseBuilder response = null;
        try {
            projectEnt = projectService.findByIdWithConsultants(projectId);
            if (projectEnt == null) {
                Map<String, String> responseObj = new HashMap<>();
                responseObj.put("error", "Project " + projectId + " not found.");
                response = Response.status(Response.Status.NOT_FOUND).entity(responseObj);
            } else {
                Person pm = projectEnt.getProjectManager();
                pm.nullifyAttributes();
                
                PersonDTO pmDto = new PersonDTO();
                BeanUtils.copyProperties(pmDto, pm);
                BeanUtils.copyProperties(projectDto, projectEnt);
                projectDto.setProjectManagerDTO(pmDto);
                for (Person consultant: projectEnt.getConsultants()) {
                    consultant.nullifyAttributes();
                    PersonDTO consultantDto = new PersonDTO();
                    PartnerOrganizationDTO orgDto = new PartnerOrganizationDTO();
                    BeanUtils.copyProperties(consultantDto, consultant);
                    BeanUtils.copyProperties(orgDto, consultant.getPartnerOrganization());
                    consultantDto.setOrganization(orgDto);
                    projectDto.addConsultant(consultantDto);
                    
                }
                response = Response.ok(projectDto);
            }
        } catch (Exception e) {
            LOG.error("Error to find project.", e);
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
    public Response save(ProjectDTO projectDto) {
        Response.ResponseBuilder builder = null;
        try {
            if (projectDto != null && StringUtils.isBlank(projectDto.getName())) {
                Map<String, String> responseObj = new HashMap<String, String>();
                responseObj.put("error", "Project name must not be empty.");
                builder = Response.status(Response.Status.CONFLICT).entity(responseObj);
                return builder.build();
            }
            Project projectEnt = projectService.findByName(projectDto.getName());
            if (projectEnt != null && (projectEnt.getId() != projectDto.getId())) {
                Map<String, String> responseObj = new HashMap<String, String>();
                responseObj.put("error", "Project with duplicated name: " + projectDto.getName());
                builder = Response.status(Response.Status.CONFLICT).entity(responseObj);
            } else {
                projectEnt = new Project();
                Person pm = personService.findById(projectDto.getProjectManagerDTO().getId());
                List<Integer> consultantsId = new ArrayList<>(projectDto.getConsultants().size());
                for (PersonDTO pdto: projectDto.getConsultants()) {
                    consultantsId.add(pdto.getId());
                }
                List<Person> consultants = personService.findPersonsById(consultantsId);
                projectDto.setConsultantsDTO(null);
                BeanUtils.copyProperties(projectEnt, projectDto);
                projectEnt.setProjectManager(pm);
                projectEnt.setConsultants(consultants);
                projectService.persist(projectEnt);
                builder = Response.ok(projectDto);
            }
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

    @Path("/{pd}/disable")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public Response disable(@PathParam("pd") @DefaultValue("-1") int projectId) {
        Response.ResponseBuilder response = null;
        try {
            projectService.disable(projectId);
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
    public Response enable(@PathParam("pd") @DefaultValue("-1") int projectId) {
        Response.ResponseBuilder response = null;
        try {
            projectService.enable(projectId);
            response = Response.ok();
        } catch (GeneralException e) {
            LOG.error("Error to insert organization.", e);
            Map<String, String> responseObj = new HashMap<String, String>();
            responseObj.put("error", e.getMessage());
            response = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
        }
        return response.build();
    }
    
    @Path("/{pd}/delete")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public Response delete(@PathParam("pd") @DefaultValue("-1") int projectId) {
        Response.ResponseBuilder response = null;
        try {
            projectService.delete(projectId);
            response = Response.ok();
        } catch (GeneralException e) {
            LOG.error("Error to insert organization.", e);
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
