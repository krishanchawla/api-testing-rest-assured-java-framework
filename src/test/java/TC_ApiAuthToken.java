import framework.model.apiauth.AuthErrorResponse;
import framework.model.apiauth.LoginRequest;
import framework.model.apiauth.TokenPairResponse;
import framework.service.AuthTokenService;
import framework.utils.common.TestDataLoader;
import framework.utils.exceptions.AutomationException;
import framework.utils.globalConstants.HttpStatus;
import framework.utils.initializers.TestInit;
import framework.utils.reportManagement.extent.ExtentTestManager;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Covers the login step of the playground.krishanchawla.com api-auth scenario:
 * POST /api/scenarios/api-auth/token.
 */
public class TC_ApiAuthToken extends TestInit {

    private static final String USERNAME = "standard_user";
    private static final String PASSWORD = "Password123!";

    /**
     * R1 - "Given valid credentials, the endpoint returns 200 with a fresh accessToken,
     * refreshToken, expiresIn (seconds), and tokenType (Bearer)."
     */
    @Test
    public void TC01_LoginWithValidCredentialsReturnsFreshTokenPair() throws AutomationException {
        ExtentTestManager.startTest("Login", "To verify that valid credentials return a fresh token pair (R1)");

        TokenPairResponse tokenPair = AuthTokenService.init().login(USERNAME, PASSWORD);

        assertThat(tokenPair.getAccessToken()).isNotBlank();
        assertThat(tokenPair.getRefreshToken()).isNotBlank();
        assertThat(tokenPair.getExpiresIn()).isGreaterThan(0);
        assertThat(tokenPair.getTokenType()).isEqualTo("Bearer");
    }

    /**
     * Data source for TC02_LoginNegativeCases, backed by
     * src/test/resources/testdata/apiauth/login-negative-cases.json:
     * <ul>
     *   <li>R2 - "Given an incorrect password for a known username, the endpoint returns
     *   401 with {@code {"error":"invalid_credentials"}}."</li>
     *   <li>R3 - "Given a request body missing the password field, the endpoint responds
     *   the same way as R2 (401 invalid_credentials) - there is no separate 400-level
     *   'missing field' validation path for this endpoint."</li>
     *   <li>R4 - "Given a request body that is not valid JSON, the endpoint returns 400
     *   with {@code {"error":"invalid_body"}}, distinguishing malformed requests from bad
     *   credentials."</li>
     * </ul>
     */
    @DataProvider(name = "loginNegativeCases")
    public Object[][] loginNegativeCases() throws AutomationException {
        LoginNegativeCase[] cases = TestDataLoader.loadArray(
                "testdata/apiauth/login-negative-cases.json", LoginNegativeCase[].class);
        Object[][] rows = new Object[cases.length][1];
        for (int i = 0; i < cases.length; i++) {
            rows[i][0] = cases[i];
        }
        return rows;
    }

    @Test(dataProvider = "loginNegativeCases")
    public void TC02_LoginNegativeCases(LoginNegativeCase testCase) throws AutomationException {
        ExtentTestManager.startTest("Login", "To verify that " + testCase.getDescription());

        Object requestBody = testCase.getRawBody() != null
                ? testCase.getRawBody()
                : new LoginRequest(testCase.getUsername(), testCase.getPassword());

        AuthErrorResponse error = AuthTokenService.init()
                .loginExpectingError(requestBody, HttpStatus.fromCode(testCase.getExpectedStatusCode()));

        assertThat(error.getError()).isEqualTo(testCase.getExpectedError());
    }

}
