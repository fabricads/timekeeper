package br.com.redhat.consulting.config;

import java.io.Serializable;

import javax.annotation.Resource;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.transaction.Status;
import javax.transaction.UserTransaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Interceptor
@TransactionalMode
public class TransactionalModeInterceptor implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private static Logger LOG = LoggerFactory.getLogger(TransactionalModeInterceptor.class);

    @Resource
    private UserTransaction utx;

    @AroundInvoke
    public Object runTxMode(InvocationContext ic) throws Throwable {
        boolean startedTransaction = false;
        Object ret = null;
        if (utx != null) {
            if (utx.getStatus() != Status.STATUS_ACTIVE) {
//                LOG.debug("Transaction begin: " + utx);
                utx.begin();
                startedTransaction = true;
            }

            try {
                ret = ic.proceed();

                if (startedTransaction) {
//                    LOG.debug("Transaction commit: " + utx);
                    utx.commit();
                }
            } catch (Throwable t) {
                LOG.error("Error continuing business application or in transaction commit. Prepare to rollback", t);
                if (startedTransaction) {
//                    LOG.debug("Transaction rollback: " + utx);
                    utx.rollback();
                }
                throw t;
            }
        } else {
            LOG.error("UserTransaction is null");
        }
        return ret;
    }
}