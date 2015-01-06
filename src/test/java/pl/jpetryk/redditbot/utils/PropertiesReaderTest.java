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
import java.util.Properties;

/**
 * Created by Jan on 06/12/14.
 */
public class PropertiesReaderTest {

    private static final String KEY = "key";
    private static final String VALUE = "value";
    private static final String FILE_NAME = "filename.properties";
    private static final String FILE_PATH = PropertiesReader.CONFIG_FOLDER + FILE_NAME;

    private PropertiesReader propertiesReader;

    @Before
    public void before() throws IOException {
        preparePropertiesFile();
        propertiesReader = new PropertiesReader(FILE_NAME);
    }

    @After
    public void after(){
        deleteFile();
    }

    private void preparePropertiesFile() throws IOException {
        Properties properties = new Properties();
        properties.setProperty(KEY, VALUE);
        OutputStream outputStream = new FileOutputStream(FILE_PATH);
        properties.store(outputStream, null);
        outputStream.close();
    }

    private void deleteFile() {
        File file = new File(FILE_PATH);
        file.delete();
    }

    @Test
    public void testCreateFile() throws IOException {
        Assert.assertNotNull(new File(FILE_PATH));
    }

    @Test
    public void testDeleteFile() throws IOException {
        deleteFile();
        File file = new File(FILE_PATH);
        Assert.assertFalse(file.exists());
    }

    @Test
    public void testReadValidProperty() throws IOException {
        Assert.assertNotEquals(propertiesReader.getProperty(KEY), PropertiesReader.DEFAULT_VALUE);
        deleteFile();
    }

    @Test
    public void testReadInvalidPropertyFile() {
        try {
            propertiesReader = new PropertiesReader("");
            propertiesReader.getProperty(KEY);
            Assert.fail();
        } catch (RuntimeException e) {
            //file does not exist - exception expected
        }

    }

    @Test
    public void testReadValidFileWithoutGivenPropertyInIt() throws IOException {
        Assert.assertEquals(propertiesReader.getProperty("invalidkey"), PropertiesReader.DEFAULT_VALUE);
    }

}
