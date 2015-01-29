package br.com.redhat.consulting.services;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import br.com.redhat.consulting.dao.TimecardDao;
import br.com.redhat.consulting.model.Person;
import br.com.redhat.consulting.model.Project;
import br.com.redhat.consulting.model.Timecard;
import br.com.redhat.consulting.model.filter.TimecardSearchFilter;
import br.com.redhat.consulting.util.GeneralException;

public class TimecardService {
    
    @Inject
    private TimecardDao timecardDao;
    
    public List<Timecard> findByConsultant(Integer personId) throws GeneralException {
        TimecardSearchFilter filter = new TimecardSearchFilter();
        Person consultant = new Person();
        consultant.setId(personId);
        filter.setConsultant(consultant);
        List<Timecard> res = timecardDao.find(filter);
        return res;
    }
    
    public List<Timecard> findByStatus(Integer statusId) throws GeneralException {
        TimecardSearchFilter filter = new TimecardSearchFilter();
        filter.setStatus(statusId);
        List<Timecard> res = timecardDao.find(filter);
        return res;
    }
    
    public List<Timecard> findAll() throws GeneralException {
        TimecardSearchFilter filter = new TimecardSearchFilter();
        List<Timecard> res = timecardDao.find(filter);
        return res;
    }
    
    public List<Timecard> findByProject(Integer prjId) throws GeneralException {
        TimecardSearchFilter filter = new TimecardSearchFilter();
        Project project = new Project();
        project.setId(prjId);
        filter.setProject(project);
        List<Timecard> res = timecardDao.find(filter);
        return res;
    }
    
    public void persist(Timecard timecard) throws GeneralException {
        Date today = new Date();
        if (timecard.getId() != null) {
            timecardDao.update(timecard);
        } else {
            timecardDao.insert(timecard);
        }
    }

    private Timecard findById(Integer projectId) {
        return timecardDao.findById(projectId);
    }

    public void delete(Integer projectId) throws GeneralException {
        timecardDao.remove(projectId);
    }


}
