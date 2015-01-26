package br.com.redhat.consulting.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import br.com.redhat.consulting.model.Person;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class PartnerOrganizationJsonSerializer extends JsonSerializer<List<Person>>{

    @Override
    public void serialize(List<Person> persons, JsonGenerator jsonGen, SerializerProvider provider) throws IOException, JsonProcessingException {
        List<Person> simplePersons = new ArrayList<>();
//        for (Person _person: persons) {
//            simplePersons.add(new SimplePerson(_person.getName()));
//        }
        jsonGen.writeObject(simplePersons);
    }

}
