import framework.model.apiauth.AuthErrorResponse;
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
 * Covers session refresh/rotation in the playground.krishanchawla.com api-auth scenario:
 * POST /api/scenarios/api-auth/refresh.
 */
public class TC_ApiAuthRefresh extends TestInit {

    private static final String USERNAME = "standard_user";
    private static final String PASSWORD = "Password123!";

    /**
     * R9 - "Given a valid, non-expired, non-revoked refresh token, the endpoint returns 200
     * with a brand-new accessToken/refreshToken pair (rotation), not a reused access
     * token."
     */
    @Test
    public void TC01_RefreshWithValidTokenRotatesPair() throws AutomationException {
        ExtentTestManager.startTest("Refresh", "To verify that a valid refresh token returns a freshly rotated token pair (R9)");

        TokenPairResponse original = AuthTokenService.init().login(USERNAME, PASSWORD);
        TokenPairResponse rotated = AuthTokenService.init().refresh(original.getRefreshToken());

        assertThat(rotated.getAccessToken()).isNotBlank().isNotEqualTo(original.getAccessToken());
        assertThat(rotated.getRefreshToken()).isNotBlank().isNotEqualTo(original.getRefreshToken());
        assertThat(rotated.getTokenType()).isEqualTo("Bearer");
    }

    /**
     * Data source for TC02_RefreshNegativeCases, backed by
     * src/test/resources/testdata/apiauth/refresh-negative-cases.json. Each row's "mode"
     * drives a state-transition setup the test performs before submitting the resulting
     * token to /refresh:
     * <ul>
     *   <li>R10 - "Once a refresh token has been used to mint a new pair (R9), that same
     *   (now-superseded) refresh token is immediately invalid for further use: retrying it
     *   returns 401 with {@code {"error":"revoked_token"}}. Refresh tokens are
     *   single-use."</li>
     *   <li>R11 - "Given a refresh token that was explicitly invalidated via the revoke
     *   endpoint, the refresh endpoint returns 401 with {@code {"error":"revoked_token"}} -
     *   the same error code as the rotation case in R10." This same row also exercises R14
     *   - "revoking [a refresh token] invalidates it for future use immediately, ahead of
     *   its natural expiry" - since the requirements doc itself notes R14 is "verified via
     *   R11".</li>
     *   <li>R12 - "Given a syntactically invalid/garbage refresh token, the endpoint
     *   returns 401 with {@code {"error":"invalid_token"}}."</li>
     *   <li>R13 - "Given an access token submitted where a refresh token is expected, the
     *   endpoint rejects it with 401 and {@code {"error":"wrong_type"}} rather than
     *   treating it as an unrecognized/invalid token - the two token kinds are
     *   distinguished server-side."</li>
     * </ul>
     */
    @DataProvider(name = "refreshNegativeCases")
    public Object[][] refreshNegativeCases() throws AutomationException {
        RefreshNegativeCase[] cases = TestDataLoader.loadArray(
                "testdata/apiauth/refresh-negative-cases.json", RefreshNegativeCase[].class);
        Object[][] rows = new Object[cases.length][1];
        for (int i = 0; i < cases.length; i++) {
            rows[i][0] = cases[i];
        }
        return rows;
    }

    @Test(dataProvider = "refreshNegativeCases")
    public void TC02_RefreshNegativeCases(RefreshNegativeCase testCase) throws AutomationException {
        ExtentTestManager.startTest("Refresh", "To verify that " + testCase.getDescription());

        String refreshTokenToSubmit;
        switch (testCase.getMode()) {
            case "SUPERSEDED": {
                TokenPairResponse original = AuthTokenService.init().login(USERNAME, PASSWORD);
                AuthTokenService.init().refresh(original.getRefreshToken());
                refreshTokenToSubmit = original.getRefreshToken();
                break;
            }
            case "REVOKED": {
                TokenPairResponse pair = AuthTokenService.init().login(USERNAME, PASSWORD);
                AuthTokenService.init().revoke(pair.getRefreshToken());
                refreshTokenToSubmit = pair.getRefreshToken();
                break;
            }
            case "GARBAGE":
                refreshTokenToSubmit = "garbage.not.a.jwt";
                break;
            case "WRONG_TYPE": {
                TokenPairResponse pair = AuthTokenService.init().login(USERNAME, PASSWORD);
                refreshTokenToSubmit = pair.getAccessToken();
                break;
            }
            default:
                throw new AutomationException("Unknown refresh negative-case mode: " + testCase.getMode());
        }

        AuthErrorResponse error = AuthTokenService.init()
                .refreshExpectingError(refreshTokenToSubmit, HttpStatus.fromCode(testCase.getExpectedStatusCode()));

        assertThat(error.getError()).isEqualTo(testCase.getExpectedError());
    }

}
