/**
 * A single row of {@code testdata/apiauth/refresh-negative-cases.json}, backing
 * TC_ApiAuthRefresh#TC02_RefreshNegativeCases (R10, R11/R14, R12, R13). {@code mode} tells
 * the test which state-setup sequence to run before submitting the resulting token to
 * /refresh: {@code SUPERSEDED} (rotate a token out first), {@code REVOKED} (revoke it
 * first), {@code GARBAGE} (a fixed nonsense string, no setup needed) or {@code WRONG_TYPE}
 * (submit a freshly issued access token instead of a refresh token).
 */
public class RefreshNegativeCase {

    private String caseName;
    private String description;
    private String mode;
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

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
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
