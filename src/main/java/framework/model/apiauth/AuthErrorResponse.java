package framework.model.apiauth;

/* -----------------------------------------------------------------------
   - ** Rest API Testing Framework using RestAssured **
   - Author: Krishan Chawla (krishanchawla1467@gmail.com)
   - Git Repo: https://github.com/krishanchawla/api-testing-rest-assured-java-framework
   -----------------------------------------------------------------------
   Common error response shape used across every negative path in the
   api-auth scenario: /token (R2/R3/R4), /protected (R6/R7/R8) and
   /refresh (R10/R11/R12/R13) all return {"error": "<code>"[, "message": "..."]}.
   ----------------------------------------------------------------------- */
public class AuthErrorResponse {

    private String error;
    private String message;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
