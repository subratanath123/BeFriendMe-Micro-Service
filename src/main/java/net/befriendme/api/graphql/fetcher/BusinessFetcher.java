package net.befriendme.api.graphql.fetcher;

import net.befriendme.api.mongo.dao.BusinessDao;
import net.befriendme.entity.business.Business;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class BusinessFetcher implements DataFetcher<Business> {

    @Autowired
    private BusinessDao businessDao;

    @Override
    public Business get(DataFetchingEnvironment environment) throws Exception {
        Map args = environment.getArguments();
        return businessDao.findById(String.valueOf(args.get("id"))).orElse(null);
    }
}
