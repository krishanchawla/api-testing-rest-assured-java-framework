package framework.auth;

import io.restassured.authentication.PreemptiveBasicAuthScheme;
import io.restassured.builder.RequestSpecBuilder;

/* -----------------------------------------------------------------------
   - ** Rest API Testing Framework using RestAssured **
   - Author: Krishan Chawla (krishanchawla1467@gmail.com)
   - Git Repo: https://github.com/krishanchawla/api-testing-rest-assured-java-framework
   ----------------------------------------------------------------------- */
public class BasicAuthStrategy implements AuthStrategy {

    private final String username;
    private final String password;

    public BasicAuthStrategy(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public void apply(RequestSpecBuilder requestSpecBuilder) {
        PreemptiveBasicAuthScheme scheme = new PreemptiveBasicAuthScheme();
        scheme.setUserName(username);
        scheme.setPassword(password);
        requestSpecBuilder.setAuth(scheme);
    }

}
