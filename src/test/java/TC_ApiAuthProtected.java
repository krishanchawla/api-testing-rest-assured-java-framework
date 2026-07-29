import framework.model.apiauth.AuthErrorResponse;
import framework.model.apiauth.ProtectedResourceResponse;
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
 * Covers the fast (non-expiry) paths of accessing the protected resource in the
 * playground.krishanchawla.com api-auth scenario: GET /api/scenarios/api-auth/protected.
 * R8 (expired token) needs to wait out the token TTL and is intentionally kept out of this
 * class - see TC_ApiAuthProtectedExpiry, isolated into its own TestNG test block.
 */
public class TC_ApiAuthProtected extends TestInit {

    private static final String USERNAME = "standard_user";
    private static final String PASSWORD = "Password123!";
    private static final String GARBAGE_TOKEN = "not-a-real-token";

    /**
     * R5 - "Given a valid, unexpired access token in the Authorization: Bearer &lt;token&gt;
     * header, the endpoint returns 200 with a message and the requesting username."
     */
    @Test
    public void TC01_AccessProtectedResourceWithValidToken() throws AutomationException {
        ExtentTestManager.startTest("Protected Resource", "To verify that a valid access token grants access to the protected resource (R5)");

        TokenPairResponse tokenPair = AuthTokenService.init().login(USERNAME, PASSWORD);
        ProtectedResourceResponse response = AuthTokenService.init().getProtectedResource(tokenPair.getAccessToken());

        assertThat(response.getUsername()).isEqualTo(USERNAME);
        assertThat(response.getMessage()).isNotBlank();
    }

    /**
     * Data source for TC02_ProtectedResourceNegativeCases, backed by
     * src/test/resources/testdata/apiauth/protected-negative-cases.json:
     * <ul>
     *   <li>R6 - "Given no Authorization header at all, the endpoint returns 401 with
     *   {@code {"error":"missing_token"}}."</li>
     *   <li>R7 - "Given a syntactically invalid/garbage bearer token, the endpoint returns
     *   401 with {@code {"error":"invalid_token"}}, distinct from the missing-header case
     *   (R6)."</li>
     * </ul>
     */
    @DataProvider(name = "protectedNegativeCases")
    public Object[][] protectedNegativeCases() throws AutomationException {
        ProtectedNegativeCase[] cases = TestDataLoader.loadArray(
                "testdata/apiauth/protected-negative-cases.json", ProtectedNegativeCase[].class);
        Object[][] rows = new Object[cases.length][1];
        for (int i = 0; i < cases.length; i++) {
            rows[i][0] = cases[i];
        }
        return rows;
    }

    @Test(dataProvider = "protectedNegativeCases")
    public void TC02_ProtectedResourceNegativeCases(ProtectedNegativeCase testCase) throws AutomationException {
        ExtentTestManager.startTest("Protected Resource", "To verify that " + testCase.getDescription());

        String bearerToken = "GARBAGE".equals(testCase.getTokenMode()) ? GARBAGE_TOKEN : null;

        AuthErrorResponse error = AuthTokenService.init()
                .getProtectedResourceExpectingError(bearerToken, HttpStatus.fromCode(testCase.getExpectedStatusCode()));

        assertThat(error.getError()).isEqualTo(testCase.getExpectedError());
    }

}
