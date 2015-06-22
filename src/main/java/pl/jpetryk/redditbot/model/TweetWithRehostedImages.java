package pl.jpetryk.redditbot.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

import java.util.List;

/**
 * Created by Jan on 04/03/15.
 */
public class TweetWithRehostedImages extends Tweet {


    private Multimap<String, RehostedImageEntity> rehostedImageEntityMultimap;

    public TweetWithRehostedImages(Tweet tweet, Multimap<String, RehostedImageEntity> rehostedImageEntities) {
        super(tweet);
        this.rehostedImageEntityMultimap = ImmutableMultimap.copyOf(rehostedImageEntities);
    }

    public Multimap<String, RehostedImageEntity> getRehostedImageEntities() {
        return rehostedImageEntityMultimap;
    }
}
