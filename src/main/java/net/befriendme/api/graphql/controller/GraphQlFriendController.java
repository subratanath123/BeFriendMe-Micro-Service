package net.befriendme.api.graphql.controller;

import net.befriendme.api.graphql.utility.FriendGraphQlUtility;
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
@Tag(name = "UserGraphQl", description = "GraphQl Query for Friend retrieval")
@SecurityRequirement(name = "security_auth")
public class GraphQlFriendController {

    private GraphQL graphQL;

    @Autowired
    private FriendGraphQlUtility friendGraphQlUtility;

    @PostConstruct
    public void GraphQlFriendController() throws IOException {
        graphQL = friendGraphQlUtility.createGraphQlObject();
    }

    @Operation(
            summary = "Retrieve Friendship Details",
            description = "Retrieve on demand properties for an User Friendship",
            tags = {"FriendGraphQl"}
    )
    @PostMapping(value = "/friend")
    public ResponseEntity query(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "GraphQL Query")
            @RequestBody String query) {
        ExecutionResult result = graphQL.execute(query);
        return ResponseEntity.ok(result.getData());
    }


}
