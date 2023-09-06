package net.befriendme.api.graphql.controller;

import net.befriendme.api.graphql.utility.UserGraphQlUtility;
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
@Tag(name = "UserGraphQl", description = "GraphQl Query for User Details retrieval")
@SecurityRequirement(name = "security_auth")
public class GraphQlUserController {

    private GraphQL graphQL;

    @Autowired
    private UserGraphQlUtility userGraphQlUtility;

    @PostConstruct
    public void UserController() throws IOException {
        graphQL = userGraphQlUtility.createGraphQlObject();
    }

    @Operation(
            summary = "Retrieve User Details",
            description = "Retrieve on demand properties for an User",
            tags = {"UserGraphQl"}
    )
    @PostMapping(value = "/user")
    public ResponseEntity query(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "GraphQL Query")
            @RequestBody String query) {
        ExecutionResult result = graphQL.execute(query);
        return ResponseEntity.ok(result.getData());
    }


}
