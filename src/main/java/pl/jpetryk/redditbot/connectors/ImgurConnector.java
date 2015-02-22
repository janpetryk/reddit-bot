package pl.jpetryk.redditbot.connectors;

import com.squareup.okhttp.OkHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by Jan on 22/02/15.
 */
public class ImgurConnector implements ImgurConnectorInterface {

    private static final String IMAGE_UPLOAD_URL = "https://api.imgur.com/3/image";

    private String clientID;
    private String clientSecret;

    public ImgurConnector(String clientID, String clientSecret) {
        this.clientID = clientID;
        this.clientSecret = clientSecret;
    }

    @Override
    public String reuploadImage(String imageURL) throws IOException, JSONException {
        URL url = new URL(IMAGE_UPLOAD_URL);

        String data = URLEncoder.encode("image", "UTF-8") + "="
                + URLEncoder.encode(imageURL, "UTF-8");
        HttpURLConnection conn = getHttpURLConnection(url);
        conn.connect();
        OutputStreamWriter outputWriter = new OutputStreamWriter(conn.getOutputStream());
        outputWriter.write(data);
        outputWriter.flush();

        JSONObject json = new JSONObject(readResponse(conn));
        outputWriter.close();
        return json.getJSONObject("data").getString("link");
    }

    private String readResponse(HttpURLConnection conn) throws IOException {
        BufferedReader rd = new BufferedReader(
                new InputStreamReader(conn.getInputStream()));
        StringBuilder stb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            stb.append(line).append("\n");
        }
        rd.close();
        return stb.toString();
    }

    private HttpURLConnection getHttpURLConnection(URL url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setConnectTimeout(15000);
        conn.setReadTimeout(15000);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", "Client-ID " + clientID);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type",
                "application/x-www-form-urlencoded");
        return conn;
    }
}
