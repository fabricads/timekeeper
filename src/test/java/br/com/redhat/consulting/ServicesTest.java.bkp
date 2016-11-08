package br.com.redhat.consulting;


import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.jglue.cdiunit.ActivatedAlternatives;
import org.jglue.cdiunit.CdiRunner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.redhat.consulting.dao.PersonDao;
import br.com.redhat.consulting.model.Person;
import br.com.redhat.consulting.model.filter.PersonSearchFilter;
import br.com.redhat.consulting.util.GeneralException;

@RunWith(CdiRunner.class)
@ActivatedAlternatives(EntityManagerTestProducers.class)
public class ServicesTest  {

    static Logger LOG = LoggerFactory.getLogger(ServicesTest.class);
    
    @Inject
    PersonDao personDao;

    @Test
    public void findById() {
        Person p1 = personDao.findById(1);
        Assert.assertEquals("Email nao e claudio@claudius.com.br", "claudio11@claudius.com.br", p1.getEmail());
    }
    
    @Test
    public void findByFilter() {
        PersonSearchFilter p1 = new PersonSearchFilter();
        p1.setEmail("claudio@claudius.com.br");
        List<Person> l = Collections.emptyList();
        try {
            l = personDao.find(p1);
        } catch (GeneralException e) {
            e.printStackTrace();
        }
        Assert.assertNotEquals("Deve conter resultados na pesquisa", 0, l.size());
        if (l.size() > 0) {
            Person p = l.get(0);
            Assert.assertEquals("Email nao e claudio@claudius.com.br", "claudio11@claudius.com.br", p.getEmail());
        }
        
        p1 = new PersonSearchFilter();
        p1.setName("claudio");
        try {
            l = personDao.find(p1);
        } catch (GeneralException e) {
            e.printStackTrace();
        }
        Assert.assertNotEquals("Deve conter resultados na pesquisa", 0, l.size());
        
        if (l.size() > 0) {
            Person p = l.get(0);
            Assert.assertEquals("Email nao e claudio@claudius.com.br", "claudio11@claudius.com.br", p.getEmail());
        }
    }
    

}
