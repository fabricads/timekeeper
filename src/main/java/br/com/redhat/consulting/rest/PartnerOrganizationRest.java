package br.com.redhat.consulting.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.redhat.consulting.config.Authenticated;
import br.com.redhat.consulting.model.PartnerOrganization;
import br.com.redhat.consulting.model.dto.PartnerOrganizationDTO;
import br.com.redhat.consulting.services.PartnerOrganizationService;
import br.com.redhat.consulting.util.GeneralException;

@RequestScoped
@Path("/organization")
@RolesAllowed({"redhat_manager", "admin"})
@Authenticated
public class PartnerOrganizationRest {

    private static Logger LOG = LoggerFactory.getLogger(PartnerOrganizationRest.class);
    
    @Inject
    private PartnerOrganizationService orgService;
    
    @Path("/list")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public Response listOrgs(@QueryParam("e") @DefaultValue("1") Integer listType) {
        List<PartnerOrganization> orgs = null;
        List<PartnerOrganizationDTO> orgsDto = null;
        Response.ResponseBuilder response = null;
       
        try {
            int LIST_ENABLED = 1;
            Boolean listOnlyEnabled = null;
            if (listType == LIST_ENABLED)
                listOnlyEnabled = true;
            orgs = orgService.findOrganizations(listOnlyEnabled);
            if (orgs.size() == 0) {
                Map<String, Object> responseObj = new HashMap<>();
                responseObj.put("msg", "No organizations found");
                responseObj.put("orgs", new ArrayList());
                response = Response.ok(responseObj);
            } else {
                orgsDto = new ArrayList<PartnerOrganizationDTO>(orgs.size());
                for (PartnerOrganization org: orgs) {
                    PartnerOrganizationDTO _orgDto = new PartnerOrganizationDTO(org);
                    _orgDto.setNumberOfPersons(org.getPersons().size());
                    orgsDto.add(_orgDto);
                }
                response = Response.ok(orgsDto);
            }
        } catch (GeneralException e) {
            LOG.error("Error to find organization.", e);
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("error", e.getMessage());
            response = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
        }
        return response.build();
    }
    
    @Path("/{pd}")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public Response get(@PathParam("pd") @DefaultValue("-1") int orgId) {
        PartnerOrganization org = null;
        Response.ResponseBuilder response = null;
        try {
            org = orgService.findById(orgId);
            if (org == null) {
                Map<String, String> responseObj = new HashMap<>();
                responseObj.put("error", "Organization " + orgId + " not found.");
                response = Response.status(Response.Status.NOT_FOUND).entity(responseObj);
            } else {
                PartnerOrganizationDTO _orgDto = new PartnerOrganizationDTO(org);
                _orgDto.setContacts(org.getContacts());
                response = Response.ok(_orgDto);
            }
        } catch (Exception e) {
            LOG.error("Error to find organization.", e);
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("error", e.getMessage());
            response = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
        }
        return response.build();
    }

    
    @Path("/{pd}/disable")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public Response disable(@PathParam("pd") @DefaultValue("-1") int orgId) {
        Response.ResponseBuilder response = null;
        try {
            orgService.disable(orgId);
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
    public Response enable(@PathParam("pd") @DefaultValue("-1") int orgId) {
        Response.ResponseBuilder response = null;
        try {
            orgService.enable(orgId);
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
    public Response delete(@PathParam("pd") @DefaultValue("-1") int orgId) {
        Response.ResponseBuilder response = null;
        try {
            orgService.delete(orgId);
            response = Response.ok();
        } catch (GeneralException e) {
            LOG.error("Error to insert organization.", e);
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
    public Response saveOrg(PartnerOrganizationDTO orgDto) {
        Response.ResponseBuilder builder = null;
        try {
            PartnerOrganization orgEnt = orgService.findByName(orgDto.getName());
            if (orgEnt != null && orgEnt.getId() != orgDto.getId()) {
                Map<String, String> responseObj = new HashMap<String, String>();
                responseObj.put("error", "Organization with duplicated name: " + orgDto.getName());
                builder = Response.status(Response.Status.CONFLICT).entity(responseObj);
            } else {
                PartnerOrganization org = orgDto.toPartnerOrganization();
                org.setContacts(orgDto.getContacts());
                orgService.persist(org);
                builder = Response.ok(org);
            }
        } catch (Exception e) {
            LOG.error("Error to insert organization.", e);
            Map<String, String> responseObj = new HashMap<String, String>();
            String msg = e.getMessage();
            if (e.getCause() != null) 
                msg = e.getCause().getMessage();
            responseObj.put("error", msg);
            builder = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
        }
        return builder.build();
    }
    
}
