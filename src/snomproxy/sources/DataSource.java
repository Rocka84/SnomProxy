package snomproxy.sources;

import snomproxy.xml.snom.SnomDocument;

/**
 * Ruft Daten von einem Server/WebService ab und liefert sie als XmlDocument
 * zurück
 * 
 * @author Fabian Dillmeier <fabian at dillmeier.de>
 */
public interface DataSource {

    public abstract SnomDocument request(String data);

}
