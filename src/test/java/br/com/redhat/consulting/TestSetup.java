package br.com.redhat.consulting;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.Before;

public class TestSetup {

    WeldContainer weld;

    @Before
    public void setup()   {
        weld = new Weld().initialize();
    }


}
