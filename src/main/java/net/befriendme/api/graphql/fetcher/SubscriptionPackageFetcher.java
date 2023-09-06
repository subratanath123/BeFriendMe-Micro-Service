package net.befriendme.api.graphql.fetcher;

import net.befriendme.entity.business.Business;
import net.befriendme.entity.business.SubscriptionPackage;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class SubscriptionPackageFetcher implements DataFetcher<List<SubscriptionPackage>> {

    @Override
    public List<SubscriptionPackage> get(DataFetchingEnvironment environment) throws Exception {
        Business business = environment.getSource();
        List<SubscriptionPackage> subscriptionPackageList = new ArrayList<>();

        if (business != null) {
            subscriptionPackageList = business.getSubscriptionPackageList();
        }

        return subscriptionPackageList;
    }
}
