package framework.auth;

import io.restassured.builder.RequestSpecBuilder;

/* -----------------------------------------------------------------------
   - ** Rest API Testing Framework using RestAssured **
   - Author: Krishan Chawla (krishanchawla1467@gmail.com)
   - Git Repo: https://github.com/krishanchawla/api-testing-rest-assured-java-framework
   ----------------------------------------------------------------------- */
public class NoAuthStrategy implements AuthStrategy {

    @Override
    public void apply(RequestSpecBuilder requestSpecBuilder) {
        // intentionally a no-op - target API requires no authentication
    }

}
