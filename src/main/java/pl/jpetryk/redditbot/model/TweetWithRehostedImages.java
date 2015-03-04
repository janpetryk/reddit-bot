package pl.jpetryk.redditbot.model;

import com.google.common.collect.ImmutableList;

import java.util.List;

/**
 * Created by Jan on 04/03/15.
 */
public class TweetWithRehostedImages extends Tweet {


    private List<RehostedImageEntity> rehostedImageEntityList;

    public TweetWithRehostedImages(Tweet tweet, List<RehostedImageEntity> rehostedImageEntities) {
        super(tweet);
        this.rehostedImageEntityList = ImmutableList.copyOf(rehostedImageEntities);
    }

    public List<RehostedImageEntity> getRehostedImageEntityList() {
        return rehostedImageEntityList;
    }
}
