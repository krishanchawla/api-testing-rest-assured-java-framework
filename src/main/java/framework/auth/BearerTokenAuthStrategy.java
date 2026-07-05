package framework.auth;

import io.restassured.builder.RequestSpecBuilder;

/* -----------------------------------------------------------------------
   - ** Rest API Testing Framework using RestAssured **
   - Author: Krishan Chawla (krishanchawla1467@gmail.com)
   - Git Repo: https://github.com/krishanchawla/api-testing-rest-assured-java-framework
   ----------------------------------------------------------------------- */
public class BearerTokenAuthStrategy implements AuthStrategy {

    private final String token;

    public BearerTokenAuthStrategy(String token) {
        this.token = token;
    }

    @Override
    public void apply(RequestSpecBuilder requestSpecBuilder) {
        requestSpecBuilder.addHeader("Authorization", "Bearer " + token);
    }

}
