/**
 * A single row of {@code testdata/apiauth/login-negative-cases.json}, backing
 * TC_ApiAuthToken#TC02_LoginNegativeCases (R2, R3, R4). When {@code rawBody} is set the test
 * sends it verbatim as a non-JSON body (R4); otherwise it builds a LoginRequest from
 * username/password, where a null password is omitted entirely by Jackson (R3).
 */
public class LoginNegativeCase {

    private String caseName;
    private String description;
    private String username;
    private String password;
    private String rawBody;
    private int expectedStatusCode;
    private String expectedError;

    public String getCaseName() {
        return caseName;
    }

    public void setCaseName(String caseName) {
        this.caseName = caseName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getRawBody() {
        return rawBody;
    }

    public void setRawBody(String rawBody) {
        this.rawBody = rawBody;
    }

    public int getExpectedStatusCode() {
        return expectedStatusCode;
    }

    public void setExpectedStatusCode(int expectedStatusCode) {
        this.expectedStatusCode = expectedStatusCode;
    }

    public String getExpectedError() {
        return expectedError;
    }

    public void setExpectedError(String expectedError) {
        this.expectedError = expectedError;
    }

    /** TestNG uses toString() to label each data-provider row in the report. */
    @Override
    public String toString() {
        return caseName;
    }

}
