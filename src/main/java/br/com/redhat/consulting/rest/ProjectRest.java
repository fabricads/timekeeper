package br.com.redhat.consulting.rest;

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

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.redhat.consulting.model.Person;
import br.com.redhat.consulting.model.Project;
import br.com.redhat.consulting.services.ProjectService;
import br.com.redhat.consulting.util.GeneralException;

@RequestScoped
@Path("/project")
public class ProjectRest {

    private static Logger LOG = LoggerFactory.getLogger(ProjectRest.class);
    
    @Inject
    private ProjectService projectService;
    
    @Inject
    private Validator validator;
    
    @Path("/list")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public List<Project> listProjects(@QueryParam("by-pm") String pm) {
        List<Project> projects = null;
        try {
            if (StringUtils.isNotBlank(pm))
                projects = projectService.findByPM(pm);
            else 
                projects = projectService.findAll();
        } catch (GeneralException e) {
            String msg = "Error searching for projects.";
            LOG.error(msg, e);
        }
        return projects;
    }
    
    @Path("/count-by-pm")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public Integer countProjectsByPM(@QueryParam("pmId") Integer pmId) {
        Integer count = 0;
        try {
            count = projectService.countProjectsByPM(pmId);
        } catch (GeneralException e) {
            String msg = "Error searching for projects.";
            LOG.error(msg, e);
        }
        return count;
    }
    
    @Path("/{pr}")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public Project get(@PathParam("pr") @DefaultValue("-1") int projectId) {
        Project project = null;
        try {
            project = projectService.findById(projectId);
            Person p = project.getProjectManager();
            p.setPassword(null);
//            p.setPersonType(null);
            p.setOraclePAId(null);
            p.setPartnerOrganization(null);
            p.setRole(null);
            p.setEnabled(false);
            p.setRegistered(null);
            p.setLastModification(null);
        } catch (Exception e) {
            LOG.error("Error to get project id="+projectId, e);
            Map<String, String> responseObj = new HashMap<String, String>();
            responseObj.put("error", e.getMessage());
//            builder = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
        }
        return project;
    }

    
    @Path("/save")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @POST
    public Response savePedido(Project project) {

        Response.ResponseBuilder builder = null;
        try {
            validate(project);
            projectService.persist(project);
            builder = Response.ok();
        } catch (ConstraintViolationException ce) {
            ce.printStackTrace();
            builder = createViolationResponse(project.getName(), ce.getConstraintViolations());
        } catch (ValidationException e) {
            e.printStackTrace();
            Map<String, String> responseObj = new HashMap<String, String>();
            responseObj.put("email", "Email taken");
            builder = Response.status(Response.Status.CONFLICT).entity(responseObj);
        } catch (Exception e) {
            e.printStackTrace();
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
