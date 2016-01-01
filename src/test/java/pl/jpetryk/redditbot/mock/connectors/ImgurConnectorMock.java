package pl.jpetryk.redditbot.mock.connectors;

import pl.jpetryk.redditbot.connectors.ImgurConnectorInterface;
import pl.jpetryk.redditbot.exceptions.ImgurException;

/**
 * Created by Jan on 2015-12-30.
 */
public class ImgurConnectorMock implements ImgurConnectorInterface{


    @Override
    public String reuploadImage(String imageURL) throws ImgurException {
        return imageURL;
    }
}
