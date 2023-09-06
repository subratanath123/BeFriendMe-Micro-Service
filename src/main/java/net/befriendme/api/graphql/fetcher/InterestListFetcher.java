package net.befriendme.api.graphql.fetcher;

import net.befriendme.entity.user.Interest;
import net.befriendme.entity.user.User;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class InterestListFetcher implements DataFetcher<List<Interest>> {

    @Override
    public List<Interest> get(DataFetchingEnvironment environment) throws Exception {
        User user = environment.getSource();
        List<Interest> interestList = new ArrayList<>();

        if (user != null) {
            interestList = user.getInterestList();
        }

        return interestList;
    }
}
