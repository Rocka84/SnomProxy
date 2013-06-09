package snomproxy.controlers.snom;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import snomproxy.controlers.Controler;

/**
 * Sendet Steuerbefehle an ein SnomIPPhone
 *
 * @author Fabian Dillmeier <fabian at dillmeier.de>
 */
public class SnomControler implements Controler {
    private String target;
    private final static Logger logger = Logger.getLogger("snomproxy.controler");

    public SnomControler(String target) {
        this.target = target;
    }

    @Override
    public void showUrl(String url) {
        try {
            logger.log(Level.INFO,"Pushing url: ".concat(url));
            SnomControler.sendHttpRequest("http://"+this.getTarget()+"/minibrowser.htm?url="+URLEncoder.encode(url,"UTF-8") );
        } catch (UnsupportedEncodingException ex) {
        }
    }

    @Override
    public void dial(String sip_id) {
        logger.log(Level.INFO,"Dialing: ".concat(sip_id));
        SnomControler.sendHttpRequest("http://".concat(this.getTarget()).concat("/command.htm?number=").concat(sip_id));
    }

    @Override
    public void hangUp() {
        logger.log(Level.INFO,"Hanging Up");
        SnomControler.sendHttpRequest("http://".concat(this.getTarget()).concat("/command.htm?RELEASE_ALL_CALLS"));
    }
    
    @Override
    public void keyStroke(String key) {
        logger.log(Level.INFO,"Sending Key: ".concat(key));
        SnomControler.sendHttpRequest("http://"+this.getTarget()+"/command.htm?key="+key);
    }

    @Override
    public void sendDtmfTones(String tones) {
        logger.log(Level.INFO,"Sending dtmf tones: ".concat(tones));
        SnomControler.sendHttpRequest("http://"+this.getTarget()+"/command.htm?key_dtmf="+tones);
    }

    public static void sendHttpRequest(String url) {
        logger.log(Level.INFO,"Send Request: ".concat(url));
        HttpURLConnection.setFollowRedirects(false);
        try {
            URL _url = new URL(url);
            URLConnection conn = _url.openConnection();
            conn.connect();
        } catch (MalformedURLException e) {
            logger.log(Level.SEVERE,"Malformed URL !");
        } catch (IOException e) {
            logger.log(Level.WARNING,"Target not reached");
        }
    }

    @Override
    public String getTarget() {
        return this.target;
    }
}
