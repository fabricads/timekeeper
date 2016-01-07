package br.com.redhat.consulting;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import br.com.redhat.consulting.model.PartnerOrganization;
import br.com.redhat.consulting.model.Person;
import br.com.redhat.consulting.model.PersonType;
import br.com.redhat.consulting.model.Project;
import br.com.redhat.consulting.model.Timecard;
import br.com.redhat.consulting.model.TimecardEntry;
import br.com.redhat.consulting.model.TimecardStatusEnum;

public class EntityTest  {

    EntityManager em;  

    @Before
    public void setup() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("partner_timekeeper_test");
        em = emf.createEntityManager();
    }

    @Test
    public void createPartnerOrg() {
        Date today = new Date();
        
        PartnerOrganization par = new PartnerOrganization();
        par.setEnabled(true);
        par.setName("Target SO");
        par.setRegistered(today);
        
        PartnerOrganization par2 = new PartnerOrganization();
        par2.setEnabled(true);
        par2.setName("Red Hat");
        par2.setRegistered(today);
        
        Person pm = new Person(723737, "Camila Mantila 2", "camila2@redhat.com", "Rio de Janeiro", "RJ", par2);
        pm.setEnabled(false);
        pm.setCountry("Brasil");
        pm.setLastModification(today);
        pm.setRegistered(today);
        pm.setPassword("admin123");
        pm.setPersonTypeEnum(PersonType.MANAGER_REDHAT);
        pm.setTelephone1(2182828282L);
        
        Person consultant = new Person(9876, "Claudio Miranda 2", "claudio2@redhat.com", "Brasilia", "DF", par);
        consultant.setEnabled(false);
        consultant.setCountry("Brasil");
        consultant.setLastModification(today);
        consultant.setRegistered(today);
        consultant.setPassword("admin123");
        consultant.setPersonTypeEnum(PersonType.CONSULTANT_REDHAT);
        consultant.setTelephone1(6191249387L);
        
        Project prj = new Project();
        prj.setEnabled(true);
        prj.setDescription("TV Anhanguera - HealtCHeck - Goiania");
        prj.setName("TV Anhanguera - HealthCheck");
        prj.setPaNumber(837373);
        prj.setProjectManager(pm);
        prj.setRegistered(today);
        DateTime dtInit = new DateTime(2015, 01, 10, 0, 0);
        DateTime dtEnd = new DateTime(2015, 01, 20, 0, 0);
        prj.setInitialDate(dtInit.toDate());
        prj.setEndDate(dtEnd.toDate());
        prj.setLastModification(today);
//        pm.addProject(prj);
//        prj.addConsultant(pm);

        Timecard tc = new Timecard();
        tc.setProject(prj);
        tc.setConsultant(consultant);
        tc.setStatus(TimecardStatusEnum.IN_PROGRESS.getId());
        
        DateTime dt1 = new DateTime(2015, 01, 10, 0, 0);
        DateTime dt2 = new DateTime(2015, 01, 11, 0, 0);
        DateTime dt3 = new DateTime(2015, 01, 12, 0, 0);
        TimecardEntry tce1 = new TimecardEntry(tc, dt1.toDate(), 8.0);
        TimecardEntry tce2 = new TimecardEntry(tc, dt2.toDate(), 7.0);
        TimecardEntry tce3 = new TimecardEntry(tc, dt3.toDate(), 8.5);
        tc.addTimecardEntry(tce1);
        tc.addTimecardEntry(tce2);
        tc.addTimecardEntry(tce3);
        
        em.getTransaction().begin();
        
        em.persist(par);
        em.persist(par2);
        em.persist(consultant);
        em.persist(pm);
        em.persist(prj);
        em.persist(tc);
        em.persist(tce1);
        em.persist(tce2);
        em.persist(tce3);
        
        
        em.getTransaction().commit();
        
        dt3 = new DateTime(2015, 01, 13, 0, 0);
        tce3 = new TimecardEntry(tc, dt3.toDate(), 9.0);
        tc.addTimecardEntry(tce3);
        tc.setStatusEnum(TimecardStatusEnum.SUBMITTED);
        tc.setCommentPM("Aprovado, obrigado");
        

        em.getTransaction().begin();
        em.persist(tce3);
        em.merge(tc);
        em.getTransaction().commit();
        
        
        
    }
    

}
