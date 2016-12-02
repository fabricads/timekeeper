package br.com.redhat.consulting.services;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.redhat.consulting.config.TransactionalMode;
import br.com.redhat.consulting.dao.TimecardDao;
import br.com.redhat.consulting.dao.TimecardEntryDao;
import br.com.redhat.consulting.model.Person;
import br.com.redhat.consulting.model.Project;
import br.com.redhat.consulting.model.Timecard;
import br.com.redhat.consulting.model.TimecardEntry;
import br.com.redhat.consulting.model.TimecardStatusEnum;
import br.com.redhat.consulting.model.filter.TimecardSearchFilter;
import br.com.redhat.consulting.util.GeneralException;

public class TimecardService {

    private static Logger LOG = LoggerFactory.getLogger(TimecardService.class);
    
    @Inject
    private TimecardDao timecardDao;
    
    @Inject
    private TimecardEntryDao tcEntryDao;
    
    public List<Timecard> findByConsultant(Integer personId) throws GeneralException {
        TimecardSearchFilter filter = new TimecardSearchFilter();
        Person consultant = new Person();
        consultant.setId(personId);
        filter.setConsultant(consultant);
        filter.setClausulasJoinPesquisa(true);
        //timecardDao.setOrderBy("order by day");
        timecardDao.setDistinct(true);
        //timecardDao.setOrderBy("");
        List<Timecard> res = timecardDao.find(filter);
        return res;
    }
    
    public Timecard findByIdAndConsultant(Integer tcId, Integer consultantId) throws GeneralException {
        TimecardSearchFilter filter = new TimecardSearchFilter();
        filter.setId(tcId);
        Person consultant = new Person();
        consultant.setId(consultantId);
        filter.setConsultant(consultant);
        filter.setClausulasJoinPesquisa(true);
        List<Timecard> res = timecardDao.find(filter);
        Timecard tc = null;
        if (res.size() > 0)
            tc = res.get(0);
        return tc;
    }
    
    public Long countByConsultantAndProject(Integer consultantId, Integer projectId) throws GeneralException {
        Long res = timecardDao.countByConsultantAndProject(projectId, consultantId);
        return res;
    }
    
    public Long countByTask(Integer taskId) throws GeneralException {
        Long res = timecardDao.countByTask(taskId);
        return res;
    }
    
    public Long countByDate(Integer consultantId, Integer projectId, Date initDate, Date endDate) throws GeneralException {
        Long res = timecardDao.countByDate(projectId, consultantId, initDate, endDate);
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
        filter.setClausulasJoinPesquisa(true);
        //timecardDao.setOrderBy("order by ENT2.day");
        //timecardDao.setOrderBy("order by day");
        timecardDao.setDistinct(true);
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
    
    @TransactionalMode
    public void persist(Timecard timecard) throws GeneralException {
        if (timecard.getId() != null) {
            timecardDao.update(timecard);
        } else {
            timecardDao.insert(timecard);
        }
        for (TimecardEntry tcEntry: timecard.getTimecardEntries()) {
            if (tcEntry.getId() != null) {
                tcEntryDao.update(tcEntry);
            } else {
                tcEntry.setTimecard(timecard);
                tcEntryDao.insert(tcEntry);
            }
        }
    }

    public Timecard findById(Integer tcId) {
        return timecardDao.findById(tcId);
    }

    public void delete(Integer tcId) throws GeneralException {
        timecardDao.remove(tcId);
    }
    
    public void delete(Integer tcId, Integer consultantId) throws GeneralException {
        TimecardSearchFilter filter = new TimecardSearchFilter();
        filter.setId(tcId);
        filter.setConsultant(new Person());
        filter.getConsultant().setId(consultantId);
        filter.setClausulasJoinPesquisa(true);
        List<Timecard> res = timecardDao.find(filter);
        if (res.size() > 0) {
            Timecard tc = res.get(0);
            timecardDao.remove(tcId);
        } else {
            LOG.warn("Consultant " +consultantId + " tried to delete timecard ("+ tcId +") assigned to a different consultant");
        }
    }

    public void approve(Integer tcId, String commentPM) throws GeneralException {
        TimecardSearchFilter filter = new TimecardSearchFilter();
        filter.setId(tcId);
        filter.setStatusEnum(TimecardStatusEnum.SUBMITTED);
        List<Timecard> res = timecardDao.find(filter);
        Timecard tc = null;
        if (res.size() > 0) {
            tc = res.get(0);
            tc.setStatusEnum(TimecardStatusEnum.APPROVED);
            tc.setCommentPM(commentPM);
            timecardDao.update(tc);
        }
    }

    public void reject(Integer tcId, String commentPM) throws GeneralException {
        TimecardSearchFilter filter = new TimecardSearchFilter();
        filter.setId(tcId);
        filter.setStatusEnum(TimecardStatusEnum.SUBMITTED);
        List<Timecard> res = timecardDao.find(filter);
        Timecard tc = null;
        if (res.size() > 0) {
            tc = res.get(0);
            tc.setStatusEnum(TimecardStatusEnum.REJECTED);
            tc.setCommentPM(commentPM);
            timecardDao.update(tc);
        }
        
    }
    

}
