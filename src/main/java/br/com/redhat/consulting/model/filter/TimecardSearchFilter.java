package br.com.redhat.consulting.model.filter;

import java.util.Date;

import br.com.redhat.consulting.model.Timecard;
import br.com.redhat.consulting.model.PartnerOrganization;

public class TimecardSearchFilter extends Timecard {

    private Date initDate;
    private Date endDate;
    private boolean clausulasJoinPesquisa;
    private boolean onPa;

    public Date getInitDate() {
        return initDate;
    }

    public void setInitDate(Date initDate) {
        this.initDate = initDate;
    }
    
    public void setOnPA(boolean onPa){
    	this.onPa=onPa;
    }
    
    public boolean getOnPA(){
    	return this.onPa;
    }
    

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public boolean isClausulasJoinPesquisa() {
        return clausulasJoinPesquisa;
    }

    public void setClausulasJoinPesquisa(boolean clausulasJoinPesquisa) {
        this.clausulasJoinPesquisa = clausulasJoinPesquisa;
    }

    public boolean getClausulasJoinPesquisa(boolean clausulasJoinPesquisa) {
        return this.clausulasJoinPesquisa;
    }
}
