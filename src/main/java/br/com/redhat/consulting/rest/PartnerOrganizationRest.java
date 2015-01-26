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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.redhat.consulting.model.PartnerOrganization;
import br.com.redhat.consulting.model.Person;
import br.com.redhat.consulting.model.dto.PartnerOrganizationDTO;
import br.com.redhat.consulting.services.PartnerOrganizationService;
import br.com.redhat.consulting.util.GeneralException;

@RequestScoped
@Path("/organization")
public class PartnerOrganizationRest {

    private static Logger LOG = LoggerFactory.getLogger(PartnerOrganizationRest.class);
    
    @Inject
    private PartnerOrganizationService orgService;
    
    @Inject
    private Validator validator;
    
    @Path("/list")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public List<PartnerOrganizationDTO> listOrgs(@QueryParam("e") Boolean enabled) {
        List<PartnerOrganization> orgs = null;
        List<PartnerOrganizationDTO> orgsDto = null;
        try {
            orgs = orgService.findOrganizations(enabled);
            orgsDto = new ArrayList<PartnerOrganizationDTO>(orgs.size());
            for (PartnerOrganization org: orgs) {
                PartnerOrganizationDTO _orgDto = new PartnerOrganizationDTO();
                BeanUtils.copyProperties(_orgDto, org);
                orgsDto.add(_orgDto);
            }
        } catch (GeneralException | IllegalAccessException | InvocationTargetException e) {
            String msg = "Error searching for organizations.";
            LOG.error(msg, e);
        }
        return orgsDto;
    }
    
    @Path("/{pd}")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public PartnerOrganization get(@PathParam("pd") @DefaultValue("-1") int orgId) {
        PartnerOrganization org = null;
        try {
            org = orgService.findById(orgId);
        } catch (GeneralException e) {
            LOG.error("Error to insert organization.", e);
            Map<String, String> responseObj = new HashMap<String, String>();
            responseObj.put("error", e.getMessage());
//            builder = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
        }
        return org;
    }
    
    @Path("/{pd}/disable")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public PartnerOrganization disable(@PathParam("pd") @DefaultValue("-1") int orgId) {
        PartnerOrganization org = null;
        try {
            orgService.disable(orgId);
        } catch (GeneralException e) {
            LOG.error("Error to insert organization.", e);
            Map<String, String> responseObj = new HashMap<String, String>();
            responseObj.put("error", e.getMessage());
//            builder = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
        }
        return org;
    }
    
    @Path("/{pd}/enable")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public PartnerOrganization enable(@PathParam("pd") @DefaultValue("-1") int orgId) {
        PartnerOrganization org = null;
        try {
            orgService.enable(orgId);
        } catch (GeneralException e) {
            LOG.error("Error to insert organization.", e);
            Map<String, String> responseObj = new HashMap<String, String>();
            responseObj.put("error", e.getMessage());
//            builder = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
        }
        return org;
    }
    
    @Path("/{pd}/delete")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public PartnerOrganization delete(@PathParam("pd") @DefaultValue("-1") int orgId) {
        PartnerOrganization org = null;
        try {
            orgService.delete(orgId);
        } catch (GeneralException e) {
            LOG.error("Error to insert organization.", e);
            Map<String, String> responseObj = new HashMap<String, String>();
            responseObj.put("error", e.getMessage());
//            builder = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
        }
        return org;
    }

    
    @Path("/save")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @POST
    public Response saveOrg(PartnerOrganization org) {

        Response.ResponseBuilder builder = null;
        try {
            validate(org);
            orgService.persist(org);
            builder = Response.ok();
        } catch (ConstraintViolationException ce) {
            LOG.error("Error to insert organization.", ce);
            builder = createViolationResponse(org.getName(), ce.getConstraintViolations());
        } catch (ValidationException e) {
            LOG.error("Error to insert organization.", e);
            Map<String, String> responseObj = new HashMap<String, String>();
            responseObj.put("error", e.getMessage());
            builder = Response.status(Response.Status.CONFLICT).entity(responseObj);
        } catch (Exception e) {
            LOG.error("Error to insert organization.", e);
            Map<String, String> responseObj = new HashMap<String, String>();
            responseObj.put("error", e.getMessage());
            builder = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
        }

        return builder.build();
    }

    
    private void validate(PartnerOrganization org) throws ConstraintViolationException, ValidationException {
        // Create a bean validator and check for issues.
        Set<ConstraintViolation<PartnerOrganization>> violations = validator.validate(org);

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
