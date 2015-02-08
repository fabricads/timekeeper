package br.com.redhat.consulting.util;

import javax.ws.rs.ForbiddenException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class RestExceptionMapper implements ExceptionMapper<Throwable> {

    private static Logger LOG = LoggerFactory.getLogger(RestExceptionMapper.class);
    
    @Override
    public Response toResponse(Throwable exception) {

        Response.ResponseBuilder response = null;
        if (exception instanceof ForbiddenException) {
            response = Response.status(Status.FORBIDDEN);
        } else {
            response = Response.status(Status.INTERNAL_SERVER_ERROR);
            LOG.warn("", exception);
            
        }
        return response.build();
    }

}
