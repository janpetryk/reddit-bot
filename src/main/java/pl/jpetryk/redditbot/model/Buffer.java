package pl.jpetryk.redditbot.model;

import com.google.common.collect.EvictingQueue;
import org.apache.log4j.Logger;

import java.util.Queue;

/**
 * Created by Jan on 06/01/15.
 */
public class Buffer<T> {

    private Queue<T> queue;

    private int itemsAdded;

    public Buffer(int size) {
        queue = EvictingQueue.create(size);
        itemsAdded = 0;
    }

    public void add(T t) {
        queue.add(t);
        itemsAdded++;
    }

    public int itemsAdded() {
        return itemsAdded;
    }

    public boolean contains(T t) {
        return queue.contains(t);
    }
}
