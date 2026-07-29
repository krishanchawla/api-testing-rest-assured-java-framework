import framework.model.apiauth.ProtectedResourceResponse;
import framework.model.apiauth.RevokeResponse;
import framework.model.apiauth.TokenPairResponse;
import framework.service.AuthTokenService;
import framework.utils.common.Randomizer;
import framework.utils.exceptions.AutomationException;
import framework.utils.initializers.TestInit;
import framework.utils.reportManagement.extent.ExtentTestManager;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Covers revoking a session in the playground.krishanchawla.com api-auth scenario:
 * POST /api/scenarios/api-auth/revoke. (R14's "revoke invalidates a refresh token ahead of
 * its natural expiry" is exercised together with R11 in TC_ApiAuthRefresh, per the
 * requirements doc's own note that R14 is "verified via R11".)
 */
public class TC_ApiAuthRevoke extends TestInit {

    private static final String USERNAME = "standard_user";
    private static final String PASSWORD = "Password123!";

    /**
     * R15 - "The revoke endpoint always returns 200 {@code {"ok":true}}, including when
     * given a refresh token that was never valid to begin with (e.g. a bogus string) - it
     * is idempotent/acknowledgment-only rather than validating the token first."
     */
    @Test
    public void TC01_RevokeAlwaysReturnsOkEvenForBogusToken() throws AutomationException {
        ExtentTestManager.startTest("Revoke", "To verify that revoke returns 200 ok:true even for a bogus token that was never valid (R15)");

        String bogusToken = "bogus-token-" + Randomizer.randomNumberWithoutZero(6);
        RevokeResponse response = AuthTokenService.init().revoke(bogusToken);

        assertThat(response.isOk()).isTrue();
    }

    /**
     * R16 - "Revoking a refresh token does not retroactively invalidate the access token
     * that was issued alongside it in the same login/refresh response: a still-live access
     * token from that pair continues to be accepted by /protected (per R5) until it
     * naturally expires (per R8). Access-token validity is self-contained (signature +
     * expiry), independent of refresh-token state."
     */
    @Test
    public void TC02_RevokeDoesNotInvalidateSiblingAccessToken() throws AutomationException {
        ExtentTestManager.startTest("Revoke", "To verify that revoking a refresh token does not invalidate its sibling access token (R16)");

        TokenPairResponse pair = AuthTokenService.init().login(USERNAME, PASSWORD);
        RevokeResponse revokeResponse = AuthTokenService.init().revoke(pair.getRefreshToken());
        assertThat(revokeResponse.isOk()).isTrue();

        ProtectedResourceResponse protectedResponse = AuthTokenService.init().getProtectedResource(pair.getAccessToken());
        assertThat(protectedResponse.getUsername()).isEqualTo(USERNAME);
    }

}
