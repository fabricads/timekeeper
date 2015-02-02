package br.com.redhat.consulting.dao;

import javax.enterprise.context.RequestScoped;

import br.com.redhat.consulting.model.TimecardEntry;
import br.com.redhat.consulting.model.filter.TimecardEntrySearchFilter;

@RequestScoped
public class TimecardEntryDao extends BaseDao<TimecardEntry, TimecardEntrySearchFilter> {

}
