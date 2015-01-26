package br.com.redhat.consulting.model;

import java.io.Serializable;

public interface IEntity  extends Serializable{

    Integer getId();
    void setId(Integer id);

}
