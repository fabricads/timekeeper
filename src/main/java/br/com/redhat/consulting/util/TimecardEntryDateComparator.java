package br.com.redhat.consulting.util;

import java.util.Comparator;

import br.com.redhat.consulting.model.dto.TimecardEntryDTO;

public class TimecardEntryDateComparator implements Comparator<TimecardEntryDTO>  {

    @Override
    public int compare(TimecardEntryDTO tce1, TimecardEntryDTO tce2) {
        return tce1.getDay().compareTo(tce2.getDay());
    }


}
