package framework.auth;

import io.restassured.builder.RequestSpecBuilder;

/* -----------------------------------------------------------------------
   - ** Rest API Testing Framework using RestAssured **
   - Author: Krishan Chawla (krishanchawla1467@gmail.com)
   - Git Repo: https://github.com/krishanchawla/api-testing-rest-assured-java-framework
   ----------------------------------------------------------------------- */
public class ApiKeyAuthStrategy implements AuthStrategy {

    private final String headerName;
    private final String apiKey;

    public ApiKeyAuthStrategy(String headerName, String apiKey) {
        this.headerName = headerName;
        this.apiKey = apiKey;
    }

    @Override
    public void apply(RequestSpecBuilder requestSpecBuilder) {
        requestSpecBuilder.addHeader(headerName, apiKey);
    }

}
