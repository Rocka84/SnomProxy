package snomproxy.sources;

import snomproxy.data.Data;

/**
 * Ruft Daten von einem Server/WebService ab
 * 
 * @author Fabian Dillmeier <fabian at dillmeier.de>
 */
public interface DataSource {

    public abstract Data request(String request);

}
