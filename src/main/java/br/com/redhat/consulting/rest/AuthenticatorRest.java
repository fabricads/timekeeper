package br.com.redhat.consulting.rest;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.security.auth.Subject;
import javax.security.auth.login.LoginException;
import javax.security.jacc.PolicyContext;
import javax.security.jacc.PolicyContextException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
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
import org.jboss.security.SimpleGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.redhat.consulting.model.Person;
import br.com.redhat.consulting.model.RoleEnum;
import br.com.redhat.consulting.model.dto.PersonDTO;
import br.com.redhat.consulting.model.dto.RoleDTO;
import br.com.redhat.consulting.services.EmailService;
import br.com.redhat.consulting.services.ForgotPassword;
import br.com.redhat.consulting.services.PersonService;
import br.com.redhat.consulting.util.GeneralException;

@RequestScoped
@Path("/auth")
public class AuthenticatorRest {

    private static Logger LOG = LoggerFactory.getLogger(AuthenticatorRest.class);

    @Inject 
    private PersonService personService;
    
    @Inject
    private ForgotPassword forgotPassword;
    
    @Context 
    private HttpServletRequest req;
    
    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(PersonDTO user) throws LoginException {
        Principal userPrincipal = req.getUserPrincipal();
        Response.ResponseBuilder response = null;
        if (userPrincipal == null) {
            try {
                HttpSession ss = req.getSession(true);
                LOG.debug("created http session: " + ss.getId());
                req.login(user.getEmail(), user.getPassword());
                user.setPassword(null);
                Person p = personService.findByEmail(user.getEmail());
                user.setId(p.getId());
                user.setName(p.getName());
                List<String> roles = retrieveRoles();
                RoleEnum roleEnum = RoleEnum.findByShortname(roles.get(0));
                RoleDTO role = new RoleDTO(roleEnum.getDescription(), roleEnum.getShortName());
                role.setId(roleEnum.getId());
                user.setRoleDTO(role);
                ss.setAttribute("user", user);
                response = Response.ok(user);
            } catch (ServletException | GeneralException e) {
                LOG.error("Error authenticate " + user.getEmail(), e);
                Map<String, String> responseObj = new HashMap<>();
                responseObj.put("error", e.getMessage());
                response = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
            }
        } else {
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("msg", "User already authenticated");
            response = Response.ok(responseObj);
        }

        return response.build();
    }
    
    @POST
    @Path("/reset")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response reset(PersonDTO user)  {
        Response.ResponseBuilder response = null;
        if (user != null && StringUtils.isNotBlank(user.getHash())) {
            Person p = forgotPassword.check(user.getHash());
            if (p != null) {
                p.setPassword(user.getPassword());
                try {
                    personService.persist(p);
                    response = Response.ok();                    
                } catch (GeneralException e) {
                    LOG.error("Error reset password " + user.getEmail(), e);
                    Map<String, String> responseObj = new HashMap<>();
                    responseObj.put("error", e.getMessage());
                    response = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
                }
            } else {
                String msg = user.getHash() + "not found ";
                LOG.error(msg);
                Map<String, String> responseObj = new HashMap<>();
                responseObj.put("error", msg);
                response = Response.status(Response.Status.NOT_FOUND).entity(responseObj);
            }
        } else {
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("error", "hash is required");
            response = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
        }
        return response.build();
    }
    
    @GET
    @Path("/forgot/{email}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response forgotPassword(@PathParam("email") String email) throws LoginException {
        Response.ResponseBuilder response = null;
        if (StringUtils.isNotBlank(email)) {
            try {
                Person ps = personService.findByEmail(email);
                if (ps != null) {
                    forgotPassword.requestPasswordReset(req.getRemoteAddr(), ps);
                    response = Response.ok();
                } else {
                    String msg = "E-mail " + email + " not found or disabled."; 
                    LOG.warn(msg);
                    Map<String, String> responseObj = new HashMap<>();
                    responseObj.put("error", msg);
                    response = Response.status(Response.Status.NOT_FOUND).entity(responseObj);
                }
            } catch (GeneralException e) {
                LOG.error(email + "not found " , e);
                Map<String, String> responseObj = new HashMap<>();
                responseObj.put("error", e.getMessage());
                response = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
            }
        } else {
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("error", "e-mail is required");
            response = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
        }
        
        return response.build();
    }
    
    @GET
    @Path("/check/{hash}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkHash(@PathParam("hash") String hash) throws LoginException {
        Response.ResponseBuilder response = null;
        if (StringUtils.isNotBlank(hash)) {
            Person p = forgotPassword.check(hash);
            if (p != null) {
                p.nullifyAttributes();
                PersonDTO psDto = new PersonDTO(p);
                response = Response.ok(psDto);                    
            } else {
                String msg = "Error processing password reset token.";
                LOG.error(msg);
                Map<String, String> responseObj = new HashMap<>();
                responseObj.put("error", msg);
                response = Response.status(Response.Status.NOT_FOUND).entity(responseObj);
            }
        } else {
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("error", "hash is required");
            response = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
        }
        return response.build();
    }
    
    private List<String> retrieveRoles() {
        List<String> roles = new ArrayList<String>();
        try {
            Subject subject = (Subject) PolicyContext.getContext("javax.security.auth.Subject.container");
            Set<SimpleGroup> _prs = subject.getPrincipals(SimpleGroup.class);
            for (Principal pri: _prs) {
                if ("Roles".equals(pri.getName())) {
                    LOG.debug("principal: "  + pri);
                    SimpleGroup group = (SimpleGroup) pri;
                    Enumeration<Principal> _pri2 = group.members();
                    while (_pri2.hasMoreElements()) {
                        Principal _role = _pri2.nextElement();
                        roles.add(_role.getName());
                    }
                }
            }
        } catch (PolicyContextException e) {
            LOG.error("Erro no login", e);

        }
        return roles;
    }

    @GET
    @Path("/logout")
    @Produces(MediaType.APPLICATION_JSON)
    public void logout(@Context HttpServletRequest req) throws LoginException {
        Principal _principal = req.getUserPrincipal();
        HttpSession ss = req.getSession(false);
        if(_principal != null){
            LOG.debug("logout principal: " + _principal);
            try {
                req.logout();
                if (ss != null) {
                    LOG.debug("logout current http session: " + ss.getId());
                    LOG.debug("logout user     : " + ss.getAttribute("user"));
                    ss.invalidate();
                }
            } catch (ServletException e) {
                LOG.error("Erro no logout", e);
            }
        }
    }
    
}
