/**
 * A single row of {@code testdata/apiauth/protected-negative-cases.json}, backing
 * TC_ApiAuthProtected#TC02_ProtectedResourceNegativeCases (R6, R7). {@code tokenMode} is
 * either {@code NONE} (no Authorization header sent at all) or {@code GARBAGE} (a
 * syntactically invalid bearer token is sent).
 */
public class ProtectedNegativeCase {

    private String caseName;
    private String description;
    private String tokenMode;
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

    public String getTokenMode() {
        return tokenMode;
    }

    public void setTokenMode(String tokenMode) {
        this.tokenMode = tokenMode;
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
