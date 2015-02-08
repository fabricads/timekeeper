package br.com.redhat.consulting.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import br.com.redhat.consulting.model.dto.PersonDTO;

public class Util {

    public static PersonDTO getPerson(HttpServletRequest req) {
        PersonDTO p = null;
        if (req != null) {
            HttpSession ss = req.getSession(false);
            if (ss != null) {
                p = (PersonDTO) ss.getAttribute("user");
            }
        }
        return p;
    }
    
}
