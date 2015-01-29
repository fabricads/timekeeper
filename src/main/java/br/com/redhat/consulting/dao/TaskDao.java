package br.com.redhat.consulting.dao;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.persistence.Query;

import br.com.redhat.consulting.config.TransactionalMode;
import br.com.redhat.consulting.model.Task;
import br.com.redhat.consulting.model.filter.TaskSearchFilter;

@RequestScoped
public class TaskDao extends BaseDao<Task, TaskSearchFilter> {

    @TransactionalMode
    public void removeById(List<Integer> tasksToRemove) {
        if (tasksToRemove != null && tasksToRemove.size() > 0) {
            
            try {
                String jql = "delete from Task t where t.id in (";
                for (int i = 0; i < tasksToRemove.size(); i++) {
                    jql += "?" + i;
                    if (i+1 < tasksToRemove.size()) 
                        jql += ",";
                }
                jql += ")";
                Query query = getEntityManager().createQuery(jql);
                for (int i = 0; i < tasksToRemove.size(); i++) {
                    query.setParameter(i, tasksToRemove.get(i));
                }
                int nr = query.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
