package framework.model.apiauth;

import com.fasterxml.jackson.annotation.JsonInclude;

/* -----------------------------------------------------------------------
   - ** Rest API Testing Framework using RestAssured **
   - Author: Krishan Chawla (krishanchawla1467@gmail.com)
   - Git Repo: https://github.com/krishanchawla/api-testing-rest-assured-java-framework
   -----------------------------------------------------------------------
   Request body for POST /api/scenarios/api-auth/token. @JsonInclude(NON_NULL)
   is deliberate: it lets tests build R3's "password field missing entirely"
   case (as opposed to an empty-string password) by simply leaving password
   null - Jackson omits the key rather than serializing it as null.
   ----------------------------------------------------------------------- */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginRequest {

    private String username;
    private String password;

    public LoginRequest() {
    }

    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
