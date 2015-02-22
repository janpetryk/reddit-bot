package pl.jpetryk.redditbot.connectors;

import com.google.common.io.BaseEncoding;
import org.junit.Assert;
import org.junit.Test;
import pl.jpetryk.redditbot.utils.PropertiesReader;

import java.io.*;
import java.net.URL;

/**
 * Created by Jan on 22/02/15.
 */
public class ImgurConnectorITCase {

    PropertiesReader propertiesReader = new PropertiesReader("resources/bot.properties");

    private String imageToCopyUrl = "http://i.imgur.com/mIuKLpu.jpg";
    ImgurConnectorInterface imgurConnector;

    @Test
    public void testUploadImg() throws Exception {
        imgurConnector = new ImgurConnector(propertiesReader.getProperty("imgur-client-id"),
                propertiesReader.getProperty("imgur-client-secret"));
        String reuploadedImageUrl = imgurConnector.reuploadImage(imageToCopyUrl);
        Assert.assertEquals(getBase64ImageValue(imageToCopyUrl), getBase64ImageValue(reuploadedImageUrl));
    }

    private String getBase64ImageValue(String imageUrl) throws Exception {
        URL url = new URL(imageUrl);
        InputStream in = new BufferedInputStream(url.openStream());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int n = 0;
        while (-1!=(n=in.read(buf)))
        {
            out.write(buf, 0, n);
        }
        out.close();
        in.close();
        byte[] response = out.toByteArray();
        return BaseEncoding.base64().encode(response);
    }
}
