package br.com.redhat.consulting.rest;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.redhat.consulting.config.Authenticated;
import br.com.redhat.consulting.model.Role;
import br.com.redhat.consulting.services.RoleService;
import br.com.redhat.consulting.util.GeneralException;

@RequestScoped
@Path("/role")
@RolesAllowed({"redhat_manager", "admin"})
@Authenticated
public class RoleRest {

    private static Logger LOG = LoggerFactory.getLogger(RoleRest.class);
    
    @Inject
    private RoleService roleService;
    
    @Path("/list")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public List<Role> listRoles() {
        List<Role> roles = null;
        try {
            roles = roleService.findAllRoles();
        } catch (GeneralException e) {
            String msg = "Error searching for all roles.";
            LOG.error(msg, e);
        }
        return roles;
    }
    
    
    
}
