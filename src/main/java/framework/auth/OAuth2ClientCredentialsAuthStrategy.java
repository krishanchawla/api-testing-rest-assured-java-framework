package framework.auth;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;

import java.time.Instant;

import static io.restassured.RestAssured.given;

/* -----------------------------------------------------------------------
   - ** Rest API Testing Framework using RestAssured **
   - Author: Krishan Chawla (krishanchawla1467@gmail.com)
   - Git Repo: https://github.com/krishanchawla/api-testing-rest-assured-java-framework
   -----------------------------------------------------------------------
   Fetches an access token via the OAuth2 client-credentials grant and
   caches it until shortly before it expires. Deliberately does not go
   through RestUtil (the token endpoint is a different service/host and
   needs none of RestUtil's response assertions).
   ----------------------------------------------------------------------- */
public class OAuth2ClientCredentialsAuthStrategy implements AuthStrategy {

    /** Refresh this many seconds before the token's reported expiry to avoid races. */
    private static final int EXPIRY_SAFETY_MARGIN_SECONDS = 30;

    private final String tokenUrl;
    private final String clientId;
    private final String clientSecret;
    private final String scope;

    private volatile String cachedToken;
    private volatile Instant cachedTokenExpiry = Instant.MIN;

    public OAuth2ClientCredentialsAuthStrategy(String tokenUrl, String clientId, String clientSecret, String scope) {
        this.tokenUrl = tokenUrl;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.scope = scope;
    }

    @Override
    public void apply(RequestSpecBuilder requestSpecBuilder) {
        requestSpecBuilder.addHeader("Authorization", "Bearer " + validToken());
    }

    private synchronized String validToken() {
        if (cachedToken == null || Instant.now().isAfter(cachedTokenExpiry)) {
            fetchAndCacheToken();
        }
        return cachedToken;
    }

    private void fetchAndCacheToken() {
        Response response =
                given()
                        .contentType("application/x-www-form-urlencoded")
                        .formParam("grant_type", "client_credentials")
                        .formParam("client_id", clientId)
                        .formParam("client_secret", clientSecret)
                        .formParam("scope", scope)
                        .when()
                        .post(tokenUrl)
                        .then()
                        .statusCode(200)
                        .extract()
                        .response();

        cachedToken = response.jsonPath().getString("access_token");
        int expiresInSeconds = response.jsonPath().getInt("expires_in");
        cachedTokenExpiry = Instant.now().plusSeconds(Math.max(0, expiresInSeconds - EXPIRY_SAFETY_MARGIN_SECONDS));
    }

}
