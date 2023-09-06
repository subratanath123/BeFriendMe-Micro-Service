package net.befriendme.api.graphql.utility;

import net.befriendme.api.graphql.fetcher.AddressListFetcher;
import net.befriendme.api.graphql.fetcher.InterestListFetcher;
import net.befriendme.api.graphql.fetcher.UserDataFetcher;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class UserGraphQlUtility {

    @Autowired
    private UserDataFetcher userDataFetcher;

    @Autowired
    private InterestListFetcher interestListFetcher;

    @Autowired
    private AddressListFetcher addressListFetcher;

    @PostConstruct
    public GraphQL createGraphQlObject() throws IOException {
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        TypeDefinitionRegistry typeDefinitionRegistry = new TypeDefinitionRegistry();
        typeDefinitionRegistry.merge(new SchemaParser().parse(resourceLoader.getResource("classpath:graphql/v1/schemas.graphqls").getInputStream()));
        typeDefinitionRegistry.merge(new SchemaParser().parse(resourceLoader.getResource("classpath:graphql/v1/user.graphqls").getInputStream()));

        RuntimeWiring wiring = buildRunTimeWiring();
        GraphQLSchema schema = new SchemaGenerator().makeExecutableSchema(typeDefinitionRegistry, wiring);
        return GraphQL.newGraphQL(schema).build();
    }

    private RuntimeWiring buildRunTimeWiring() {
        return RuntimeWiring
                .newRuntimeWiring()
                .type("Query", typeWiring -> typeWiring
                        .dataFetcher("user", userDataFetcher))
                .type("User", typeWiring -> typeWiring.dataFetcher("addressList", addressListFetcher)
                        .dataFetcher("interestList", interestListFetcher))
                .build();
    }


}
