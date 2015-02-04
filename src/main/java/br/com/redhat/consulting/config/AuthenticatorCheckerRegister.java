package br.com.redhat.consulting.config;

import java.lang.reflect.Method;

import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * The annotation Authenticated is used to mark the classes or methods of Rest endpoints, where the request must be authenticated.
 * Resteasy doesn't provide this feature, but it provides Authorization.
 * 
 * @author claudio
 *
 */
@Provider
public class AuthenticatorCheckerRegister implements DynamicFeature {

    private static Logger LOG = LoggerFactory.getLogger(AuthenticatorCheckerRegister.class);
    
    @Override
    public void configure(ResourceInfo resourceInfo, FeatureContext configurable) {
        final Class declaring = resourceInfo.getResourceClass();
        final Method method = resourceInfo.getResourceMethod();

        if (declaring == null || method == null)
            return;

        Authenticated authenticated = (Authenticated) declaring.getAnnotation(Authenticated.class);
        Authenticated methodAuthenticated = method.getAnnotation(Authenticated.class);

        if (methodAuthenticated != null || authenticated != null) {
            LOG.debug("register class " + declaring.getSimpleName() + ", method: " + method.getName());
            AuthorizationFilter filter = new AuthorizationFilter();
            configurable.register(filter);
        }
    }
}
