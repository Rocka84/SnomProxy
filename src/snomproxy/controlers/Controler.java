package snomproxy.controlers;

/**
 * Sendet Steuerbefehle an ein Gerät
 * 
 * @author Fabian Dillmeier <fabian at dillmeier.de>
 */
public interface Controler {

    public String getTarget();

    public void showUrl(String url);

    public void dial(String sip_id);
    
    public void hangUp();

    public void keyStroke(String key);

    public void sendDtmfTones(String tones);
}
