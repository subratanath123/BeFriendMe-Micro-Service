package net.befriendme.api.graphql.utility;

import net.befriendme.api.graphql.fetcher.BusinessFetcher;
import net.befriendme.api.graphql.fetcher.SubscriptionPackageFetcher;
import net.befriendme.api.graphql.fetcher.UserDataListFetcher;
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
public class BusinessGraphQlUtility {

    @Autowired
    private BusinessFetcher businessFetcher;

    @Autowired
    private SubscriptionPackageFetcher subscriptionPackageFetcher;

    @Autowired
    private UserDataListFetcher userDataListFetcher;

    @PostConstruct
    public GraphQL createGraphQlObject() throws IOException {
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        TypeDefinitionRegistry typeDefinitionRegistry = new TypeDefinitionRegistry();
        typeDefinitionRegistry.merge(new SchemaParser().parse(resourceLoader.getResource("classpath:graphql/v1/schemas.graphqls").getInputStream()));
        typeDefinitionRegistry.merge(new SchemaParser().parse(resourceLoader.getResource("classpath:graphql/v1/business.graphqls").getInputStream()));

        RuntimeWiring wiring = buildRunTimeWiring();
        GraphQLSchema schema = new SchemaGenerator().makeExecutableSchema(typeDefinitionRegistry, wiring);
        return GraphQL.newGraphQL(schema).build();
    }

    private RuntimeWiring buildRunTimeWiring() {
        return RuntimeWiring
                .newRuntimeWiring()
                .type("BusinessQuery", typeWiring -> typeWiring.dataFetcher("business", businessFetcher))
                .type("Business", typeWiring -> typeWiring.dataFetcher("subscriptionPackageList", subscriptionPackageFetcher)
                        .dataFetcher("userLikedList", userDataListFetcher))
                .build();
    }

}
