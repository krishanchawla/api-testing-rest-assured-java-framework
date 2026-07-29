import framework.model.apiauth.AuthErrorResponse;
import framework.model.apiauth.TokenPairResponse;
import framework.service.AuthTokenService;
import framework.utils.exceptions.AutomationException;
import framework.utils.globalConstants.HttpStatus;
import framework.utils.initializers.TestInit;
import framework.utils.reportManagement.extent.ExtentTestManager;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Deliberately kept out of TC_ApiAuthProtected and registered in its own TestNG
 * {@code <test>} block (see testng.xml) because R8 requires waiting out the sandbox's
 * short (20s) access-token TTL via a real Thread.sleep. Running that sleep inside the main
 * parallel="methods" thread-count="5" pool would either hold a shared slot idle for 20+
 * seconds (slowing the whole suite down for no reason) or - if scheduled alongside other
 * timing-sensitive assertions - make results flaky under contention. Isolating it into a
 * single-threaded, sequential test block avoids both without affecting the fast tests.
 */
public class TC_ApiAuthProtectedExpiry extends TestInit {

    private static final String USERNAME = "standard_user";
    private static final String PASSWORD = "Password123!";

    /**
     * R8 - "Given an access token whose expiresIn window has elapsed, the endpoint returns
     * 401 with {@code {"error":"expired_token"}}, distinct from both R6 and R7."
     */
    @Test
    public void TC01_ExpiredAccessTokenIsRejected() throws AutomationException, InterruptedException {
        ExtentTestManager.startTest("Protected Resource", "To verify that an expired access token is rejected with 401 expired_token (R8)");

        TokenPairResponse tokenPair = AuthTokenService.init().login(USERNAME, PASSWORD);
        Thread.sleep((tokenPair.getExpiresIn() + 1) * 1000L);

        AuthErrorResponse error = AuthTokenService.init()
                .getProtectedResourceExpectingError(tokenPair.getAccessToken(), HttpStatus.UNAUTHORIZED);

        assertThat(error.getError()).isEqualTo("expired_token");
    }

}
