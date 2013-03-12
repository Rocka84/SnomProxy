package snomproxy.sources;

import snomproxy.SnomProxy;
import snomproxy.xml.snom.SnomDocument;
import snomproxy.xml.snom.SnomIPPhoneText;

/**
 * Ruft Daten vom blau direkt WebService ab und liefert sie als BlauDocument
 * zurück
 *
 * @author Fabian Dillmeier <fabian at dillmeier.de>
 */
public class BlauDataSource implements DataSource{

    @Override
    public SnomDocument request(String data) {
        
        SnomDocument out=new SnomIPPhoneText("blau data source","Noch nicht implementiert.");
        out.addSoftKeyItem("index","Index", SnomProxy.getServer().getAddressString().concat("/"));
        
        /**
         * 
         * Hier den Webservice ansprechen
         * und ein SnomDocument, bevorzugt ein SnomIPPhoneDirectory
         * 
         */
        
        return out;
    }
      
}
