package pl.jpetryk.redditbot.model;

import net.dean.jraw.models.LoggedInAccount;

/**
 * Created by Jan on 19/12/14.
 */
public class JrawLoggedInUserAdapter implements RedditLoggedInAccountInterface {

    private LoggedInAccount loggedInAccount;

    public JrawLoggedInUserAdapter(LoggedInAccount loggedInAccount){
        this.loggedInAccount = loggedInAccount;
    }
}
