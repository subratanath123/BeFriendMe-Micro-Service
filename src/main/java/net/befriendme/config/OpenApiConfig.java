package net.befriendme.config;

import net.befriendme.entity.idp.AppleIdpConfig;
import net.befriendme.entity.idp.BeFriendMeIdpConfig;
import net.befriendme.entity.idp.GoogleIdpConfig;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.Scopes;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@SecurityScheme(
//        name = "security_auth",
//        type = SecuritySchemeType.OAUTH2,
//        flows = @OAuthFlows(authorizationCode =
//        @OAuthFlow(
//                authorizationUrl = "https://accounts.google.com/o/oauth2/auth",
//                tokenUrl = "https://oauth2.googleapis.com/token",
//                scopes = {
//                        @OAuthScope(name = "profile", description = "Profile Information"),
//                        @OAuthScope(name = "email", description = "Email Information"),
//                        @OAuthScope(name = "openid", description = "OpenId Information"),
//                }
//        )))

@Configuration
public class OpenApiConfig {

        @Autowired
        private BeFriendMeIdpConfig brFriendMeIdpConfig;

        @Autowired
        private GoogleIdpConfig googleIdpConfig;

        @Autowired
        private AppleIdpConfig appleIdpConfig;

        @Bean
        public OpenAPI customOpenAPI() {
                OAuthFlow oAuthFlow = new OAuthFlow()
                        .tokenUrl(googleIdpConfig.getTokenUrl())
                        .authorizationUrl(googleIdpConfig.getAuthUrl())
                        .scopes(new Scopes()
                                .addString("profile", "profile")
                                .addString("email", "email")
                                .addString("openid", "openid"));

                SecurityScheme oauth2 = new SecurityScheme()
                        .flows(new OAuthFlows().authorizationCode(oAuthFlow))
                        .type(SecurityScheme.Type.OAUTH2)
                        .scheme("oauth2");

                oauth2.addExtension("x-tokenName", "id_token");

                Components components = new Components()
                        .addSecuritySchemes("security_auth", oauth2);

                return new OpenAPI()
                        .components(components)
                        .info(new Info()
                                .title("Be Friend Me Service")
                                .version("1.0")
                                .description("Be Friend Me Swagger APi Documentation"));
        }
}