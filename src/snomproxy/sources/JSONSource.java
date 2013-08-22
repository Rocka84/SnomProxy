package snomproxy.sources;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import org.json.JSONException;
import org.json.JSONObject;
import snomproxy.SnomProxy;

/**
 *
 * @author Fabian Dillmeier <fabian at dillmeier.de>
 */
public abstract class JSONSource {

	public static JSONObject requestJSON(String url) throws MalformedURLException, IOException {
		SnomProxy.getLogger().log(Level.INFO, url);
		BufferedReader rd = new BufferedReader(new InputStreamReader(new URL(url).openConnection().getInputStream()));
		String line;
		StringBuilder response = new StringBuilder();
		while ((line = rd.readLine()) != null) {
			response.append(line);
			response.append('\r');
		}
		rd.close();
		JSONObject out = new JSONObject(response.toString());
		if (response.toString().isEmpty()) {
			throw new JSONException("no response");
		}
		return out;
	}

}
