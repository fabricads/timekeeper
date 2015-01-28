package br.com.redhat.consulting.services;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import br.com.redhat.consulting.dao.PersonDao;
import br.com.redhat.consulting.model.Person;
import br.com.redhat.consulting.model.PersonType;
import br.com.redhat.consulting.model.dto.PersonDTO;
import br.com.redhat.consulting.model.filter.PersonSearchFilter;
import br.com.redhat.consulting.util.GeneralException;

public class PersonService {
    
    @Inject
    private PersonDao personDao;
    
    public void insert(Person person) throws GeneralException {
        Date today = new Date();
        person.setRegistered(today);
        person.setLastModification(today);
        personDao.insert(person);
    }
    
    public List<Person> findProjectMangers() throws GeneralException {
        List<Person> res = null;
        PersonSearchFilter filter = new PersonSearchFilter();
        filter.setEnabled(true);
        filter.setPersonTypeEnum(PersonType.MANAGER_REDHAT);
        res = personDao.find(filter);
        return res;
    }
    
    public List<Person> findPersons() throws GeneralException {
        List<Person> res = null;
        PersonSearchFilter filter = new PersonSearchFilter();
        filter.setPersonTypes(new int[]{PersonType.CONSULTANT_PARTNER.getId(), PersonType.MANAGER_REDHAT.getId()});
        personDao.setFetchCollection(new String[]{"projects"});
        res = personDao.find(filter);
        return res;
    }
    
    public List<Person> findConsultants() throws GeneralException {
        List<Person> res = null;
        PersonSearchFilter filter = new PersonSearchFilter();
        filter.setPersonTypeEnum(PersonType.CONSULTANT_PARTNER);
        filter.setEnabled(true);
        personDao.setFetchCollection(new String[]{"projects"});
        res = personDao.find(filter);
        return res;
    }
    
    public Person findById(Integer id)  {
        Person person = personDao.findById(id);
        return person;
    }
    
    public void disable(Integer personId) throws GeneralException {
        Person person = findById(personId);
        person.setEnabled(false);
        personDao.update(person);
    }
    
    public void enable(Integer personId) throws GeneralException {
        Person person = findById(personId);
        person.setEnabled(true);
        personDao.update(person);
    }
    
    public void delete(Integer personId) throws GeneralException {
        personDao.remove(personId);
    }
    
    public void persist(Person person) throws GeneralException {
        Date today = new Date();
        if (person.getId() != null) {
            person.setLastModification(today);
            personDao.update(person);
        } else {
            person.setRegistered(today);
            person.setLastModification(today);
            personDao.insert(person);
        }
    }

    public Person findByName(String name) throws GeneralException {
        List<Person> res = null;
        PersonSearchFilter filter = new PersonSearchFilter();
        filter.setName(name);
        res = personDao.find(filter);
        Person person = null;
        if (res.size() > 0)
            person = res.get(0);
        return person;
    }

    public List<Person> findPersonsById(List<Integer> consultantsId) throws GeneralException {
        List<Person> res = null;
        PersonSearchFilter filter = new PersonSearchFilter();
        filter.setPersonIds(consultantsId);
        filter.setEnabled(true);
        personDao.setFetchCollection(new String[]{"projects"});
        res = personDao.find(filter);
        return res;
    }

}
