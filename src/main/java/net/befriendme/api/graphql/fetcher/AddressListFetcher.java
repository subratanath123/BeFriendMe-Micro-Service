package net.befriendme.api.graphql.fetcher;

import net.befriendme.entity.user.Address;
import net.befriendme.entity.user.User;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AddressListFetcher implements DataFetcher<List<Address>> {

    @Override
    public List<Address> get(DataFetchingEnvironment environment) throws Exception {
        User user = environment.getSource();
        List<Address> addressList = new ArrayList<>();

        if (user != null) {
            addressList = user.getAddressList();
        }

        return addressList;
    }
}
