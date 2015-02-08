package br.com.redhat.consulting.config;

import java.io.IOException;

import javax.annotation.Priority;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.redhat.consulting.model.dto.PersonDTO;

@Priority(Priorities.AUTHENTICATION)
public class AuthorizationFilter implements ContainerRequestFilter {

    private static Logger LOG = LoggerFactory.getLogger(AuthorizationFilter.class);

    private static final Response ACCESS_DENIED = Response.status(Response.Status.UNAUTHORIZED).build();
    private static final Response FORBIDDEN = Response.status(Response.Status.FORBIDDEN).build();

    @Context
    private HttpServletRequest httpReq;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        
        HttpSession ss = httpReq.getSession(false);
        PersonDTO user = null;
        if (ss != null) {
            user = (PersonDTO) ss.getAttribute("user");
        }
        if (user == null) {
            LOG.warn("Unauthenticated user. Access denied for " + httpReq.getPathInfo() + " - Source address: " + httpReq.getRemoteAddr());
            requestContext.abortWith(ACCESS_DENIED);
        }
    }


}
