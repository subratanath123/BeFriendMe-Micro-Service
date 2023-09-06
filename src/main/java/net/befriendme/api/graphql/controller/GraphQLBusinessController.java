package net.befriendme.api.graphql.controller;

import net.befriendme.api.graphql.utility.BusinessGraphQlUtility;
import graphql.ExecutionResult;
import graphql.GraphQL;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@Tag(name = "BusinessGraphQl", description = "GraphQl Query for Business Profile Details retrieval")
@SecurityRequirement(name = "security_auth")
public class GraphQLBusinessController {

    private GraphQL graphQL;

    @Autowired
    private BusinessGraphQlUtility businessGraphQlUtility;

    @PostConstruct
    public void BusinessController() throws IOException {
        graphQL = businessGraphQlUtility.createGraphQlObject();
    }

    @Operation(
            summary = "Retrieve Business Details",
            description = "Retrieve on demand properties for an Business",
            tags = {"BusinessGraphQl"}
    )
    @PostMapping(value = "/business")
    public ResponseEntity query(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "GraphQL Query")
            @RequestBody String query) {
        ExecutionResult result = graphQL.execute(query);
        return ResponseEntity.ok(result.getData());
    }


}
