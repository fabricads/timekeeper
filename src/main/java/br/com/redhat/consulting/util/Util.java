package br.com.redhat.consulting.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import br.com.redhat.consulting.model.Person;

public class Util {

    public static Person getPerson(HttpServletRequest req) {
        Person p = null;
        if (req != null) {
            HttpSession ss = req.getSession(false);
            if (ss != null) {
                p = (Person) ss.getAttribute("user");
            }
        }
        return p;
    }
    
}
