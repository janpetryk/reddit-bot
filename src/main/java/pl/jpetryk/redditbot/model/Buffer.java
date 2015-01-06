package pl.jpetryk.redditbot.model;

import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Jan on 06/01/15.
 */
public class Buffer<T> {

    private static final Long ITEM_LIVE_TIME_IN_MILLIS = 3600000L;

    private static final Logger logger = Logger.getLogger(Buffer.class);

    private Map<T, Long> map;

    private int itemsAdded;

    public Buffer() {
        map = new HashMap<>();
        itemsAdded = 0;
    }

    public void add(T t) {
        map.put(t, System.currentTimeMillis());
        itemsAdded++;
    }

    public int itemsAdded() {
        return itemsAdded;
    }

    public void invalidateOldItems() {
        Iterator<Map.Entry<T, Long>> iterator = map.entrySet().iterator();
        int itemsDeleted = 0;
        while (iterator.hasNext()) {
            Map.Entry<T, Long> entry = iterator.next();
            if (System.currentTimeMillis() - entry.getValue() > ITEM_LIVE_TIME_IN_MILLIS) {
                iterator.remove();
                itemsDeleted++;
            }
        }
        if (itemsDeleted > 0) {
            logger.info("Removed " + Integer.toString(itemsDeleted) + " items from buffer.");
        }
    }

    public boolean contains(T t) {
        return map.keySet().contains(t);
    }
}
