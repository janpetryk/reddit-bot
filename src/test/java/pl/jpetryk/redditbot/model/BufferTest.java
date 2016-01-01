package pl.jpetryk.redditbot.model;

import org.junit.Assert;
import org.junit.Test;
import pl.jpetryk.redditbot.model.Buffer;

/**
 * Created by Jan on 07/01/15.
 */
public class BufferTest {

    @Test
    public void testBufferContainsOnlyLastElement() {
        String firstElement = "asdasdasdads1";
        String secondElement = "asdwwsa2";
        String thirdElement="Asdasdasdadsa3";
        Buffer<String> buffer = new Buffer(2);
        buffer.add(firstElement);
        Assert.assertTrue(buffer.contains(firstElement));
        buffer.add(secondElement);
        Assert.assertTrue(buffer.contains(secondElement));
        buffer.add(thirdElement);
        Assert.assertTrue(buffer.contains(secondElement));
        Assert.assertTrue(buffer.contains(thirdElement));
        Assert.assertFalse(buffer.contains(firstElement));

    }
}
