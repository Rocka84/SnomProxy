package snomproxy.sources;

import snomproxy.xml.snom.SnomDocument;

/**
 * Ruft Daten vom blau direkt WebService ab und liefert sie als BlauDocument
 * zurück
 *
 * @author Fabian Dillmeier <fabian at dillmeier.de>
 */
public class BlauDataSource implements DataSource{

    @Override
    public SnomDocument request(String data) {
        
        /**
         * 
         * Hier den Webservice ansprechen
         * 
         */
        
        
        return null;//new SnomDocument();
    }
      
}
