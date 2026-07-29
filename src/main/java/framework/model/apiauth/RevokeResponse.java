package framework.model.apiauth;

/* -----------------------------------------------------------------------
   - ** Rest API Testing Framework using RestAssured **
   - Author: Krishan Chawla (krishanchawla1467@gmail.com)
   - Git Repo: https://github.com/krishanchawla/api-testing-rest-assured-java-framework
   -----------------------------------------------------------------------
   200 response shape returned by POST /api/scenarios/api-auth/revoke -
   always {"ok": true}, even for a token that was never valid (R15).
   ----------------------------------------------------------------------- */
public class RevokeResponse {

    private boolean ok;

    public boolean isOk() {
        return ok;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }

}
