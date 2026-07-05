package framework.auth;

import io.restassured.builder.RequestSpecBuilder;

/* -----------------------------------------------------------------------
   - ** Rest API Testing Framework using RestAssured **
   - Author: Krishan Chawla (krishanchawla1467@gmail.com)
   - Git Repo: https://github.com/krishanchawla/api-testing-rest-assured-java-framework
   ----------------------------------------------------------------------- */
public interface AuthStrategy {

    /**
     * Applies whatever headers/scheme this strategy requires to the request
     * being built. Implementations that need a token (OAuth2, refreshable
     * bearer tokens) are responsible for fetching/caching it themselves.
     */
    void apply(RequestSpecBuilder requestSpecBuilder);

}
