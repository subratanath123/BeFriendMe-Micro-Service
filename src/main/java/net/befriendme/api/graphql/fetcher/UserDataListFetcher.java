package net.befriendme.api.graphql.fetcher;

import net.befriendme.entity.business.Business;
import net.befriendme.entity.user.User;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.Collections.emptyList;

@Component
public class UserDataListFetcher implements DataFetcher<List<User>> {

    @Override
    public List<User> get(DataFetchingEnvironment environment) throws Exception {

        if (environment.getSource() instanceof Business) {
            return getBusinessLikerList(environment);

        } else {
            return emptyList();
        }
    }

    private List<User> getBusinessLikerList(DataFetchingEnvironment environment) {
        Business business = environment.getSource();
        return business.getUserLikedList();
    }
}
