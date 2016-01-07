package br.com.redhat.consulting.util;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.Response;

import org.apache.commons.codec.binary.Base64;

import br.com.redhat.consulting.model.dto.PersonDTO;

public class Util {

    public static PersonDTO loggedUser(HttpServletRequest req) {
        PersonDTO p = null;
        if (req != null) {
            HttpSession ss = req.getSession(false);
            if (ss != null) {
                p = (PersonDTO) ss.getAttribute("user");
            }
        }
        return p;
    }
    
    public static String hash(String clearText) {
        MessageDigest md = null;
        byte[] bytes = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
            bytes = clearText.getBytes(("UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        md.update(bytes);
        byte[] digest = md.digest();
        String encoded = Base64.encodeBase64String(digest);
        return encoded;
    }

    
    public static Map<String, String> jsonMessageResponse(String key, String message) {
        Map<String, String> responseObj = new HashMap<String, String>();
        responseObj.put(key, message);
        return responseObj; 
    }
    
}
