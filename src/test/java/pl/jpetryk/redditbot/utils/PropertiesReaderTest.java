package pl.jpetryk.redditbot.utils;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import pl.jpetryk.redditbot.utils.PropertiesReader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.Properties;

/**
 * Created by Jan on 06/12/14.
 */
public class PropertiesReaderTest {

    private static final String KEY = "key";
    private static final String VALUE = "value";
    private static final String FILE_PATH = "filename.properties";

    private PropertiesReader propertiesReader;

    @Before
    public void before() throws Exception {
        preparePropertiesFile();
        propertiesReader = new PropertiesReader(FILE_PATH);
    }

    @After
    public void after() throws Exception {
        deleteFile();
    }

    private void preparePropertiesFile() throws Exception {
        Properties properties = new Properties();
        properties.setProperty(KEY, VALUE);
        OutputStream outputStream = new FileOutputStream(this.getClass().getClassLoader().getResource(".").toURI().getPath() + FILE_PATH);
        properties.store(outputStream, null);
        outputStream.close();
    }

    private void deleteFile() throws Exception {
        File file = new File(this.getClass().getClassLoader().getResource(".").toURI().getPath() + FILE_PATH);
        file.delete();
    }

    @Test
    public void testCreateFile() throws Exception {
        Assert.assertNotNull(new File(FILE_PATH));
    }

    @Test
    public void testDeleteFile() throws Exception {
        deleteFile();
        File file = new File(FILE_PATH);
        Assert.assertFalse(file.exists());
    }

    @Test
    public void testReadValidProperty() throws Exception {
        Assert.assertNotEquals(propertiesReader.getProperty(KEY), PropertiesReader.DEFAULT_VALUE);
        deleteFile();
    }



    @Test
    public void testReadValidFileWithoutGivenPropertyInIt() throws Exception {
        Assert.assertNull(propertiesReader.getProperty("invalidkey"));
    }

}
