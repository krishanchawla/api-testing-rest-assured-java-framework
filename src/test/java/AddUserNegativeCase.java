/* -----------------------------------------------------------------------
   - ** Rest API Testing Framework using RestAssured **
   - Test data POJO for TC_AddUserAPI's data-driven negative cases
   ----------------------------------------------------------------------- */
public class AddUserNegativeCase {

    private String caseName;
    private String description;
    private String userIdOverride;
    private String statusOverride;
    private int expectedStatusCode;

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

    public String getUserIdOverride() {
        return userIdOverride;
    }

    public void setUserIdOverride(String userIdOverride) {
        this.userIdOverride = userIdOverride;
    }

    public String getStatusOverride() {
        return statusOverride;
    }

    public void setStatusOverride(String statusOverride) {
        this.statusOverride = statusOverride;
    }

    public int getExpectedStatusCode() {
        return expectedStatusCode;
    }

    public void setExpectedStatusCode(int expectedStatusCode) {
        this.expectedStatusCode = expectedStatusCode;
    }

    /** TestNG uses toString() to label each data-provider row in the report. */
    @Override
    public String toString() {
        return caseName;
    }

}
