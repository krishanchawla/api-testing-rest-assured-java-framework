package framework.service;

import framework.auth.AuthStrategyFactory;
import framework.model.apiauth.AuthErrorResponse;
import framework.model.apiauth.LoginRequest;
import framework.model.apiauth.ProtectedResourceResponse;
import framework.model.apiauth.RefreshTokenRequest;
import framework.model.apiauth.RevokeResponse;
import framework.model.apiauth.TokenPairResponse;
import framework.utils.common.RestUtil;
import framework.utils.exceptions.AutomationException;
import framework.utils.globalConstants.APIEndPoint;
import framework.utils.globalConstants.HttpStatus;
import framework.utils.reportManagement.extent.ExtentTestManager;
import io.restassured.http.ContentType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;

/* -----------------------------------------------------------------------
   - ** Rest API Testing Framework using RestAssured **
   - Author: Krishan Chawla (krishanchawla1467@gmail.com)
   - Git Repo: https://github.com/krishanchawla/api-testing-rest-assured-java-framework
   -----------------------------------------------------------------------
   Service class for the playground.krishanchawla.com "api-auth" scenario:
   a bearer-token lifecycle (login -> protected resource -> refresh
   rotation -> revoke). Unlike a statically-configured bearer service, the
   token here is the thing under test - it is minted and rotated within a
   single test method - so it is never wired through
   AuthStrategyFactory("bearer"). authType=none still gets applied via
   .auth(...) for consistency with every other service class, and each
   operation that needs a bearer token attaches it per-call via
   RestUtil.headers(Map.of("Authorization", "Bearer " + token)) using a
   token captured from a prior response earlier in the same test.
   ----------------------------------------------------------------------- */
public class AuthTokenService {

    private static final String SERVICE_KEY = "api-auth";
    private static final String TOKEN_PAIR_SCHEMA = "schemas/apiauth/token-pair.schema.json";
    private static final String PROTECTED_SCHEMA = "schemas/apiauth/protected-success.schema.json";
    private static final String ERROR_SCHEMA = "schemas/apiauth/error.schema.json";
    private static final String REVOKE_SCHEMA = "schemas/apiauth/revoke.schema.json";

    private final Logger _logger = LogManager.getLogger(AuthTokenService.class);

    private Object responsePayload;

    public static AuthTokenService init() {
        return new AuthTokenService();
    }

    private RestUtil newRequest() throws AutomationException {
        return RestUtil.init(SERVICE_KEY).auth(AuthStrategyFactory.forService(SERVICE_KEY));
    }

    /**
     * POST /api/scenarios/api-auth/token with valid credentials - the R1 happy path.
     * Returns the fresh accessToken/refreshToken pair.
     */
    public TokenPairResponse login(String username, String password) throws AutomationException {
        ExtentTestManager.step(_logger, "Login with credentials for user " + username);
        TokenPairResponse tokenPair = newRequest()
                .path(APIEndPoint.API_AUTH_TOKEN)
                .contentType(ContentType.JSON)
                .body(new LoginRequest(username, password))
                .expectedStatusCode(HttpStatus.OK)
                .expectedResponseContentType(ContentType.JSON)
                .expectedSchema(TOKEN_PAIR_SCHEMA)
                .post()
                .responseToPojo(TokenPairResponse.class);
        responsePayload = tokenPair;
        return tokenPair;
    }

    /**
     * POST /api/scenarios/api-auth/token expecting a rejection - covers R2 (wrong
     * password), R3 (missing password field) and R4 (non-JSON body). requestBody may be a
     * {@link LoginRequest} (whose password may be left null so Jackson omits the field
     * entirely, for R3) or a raw String for R4's malformed-body case.
     */
    public AuthErrorResponse loginExpectingError(Object requestBody, HttpStatus expectedStatus) throws AutomationException {
        ExtentTestManager.step(_logger, "Login expecting rejection: " + expectedStatus.getCode());
        AuthErrorResponse error = newRequest()
                .path(APIEndPoint.API_AUTH_TOKEN)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .expectedStatusCode(expectedStatus)
                .expectedResponseContentType(ContentType.JSON)
                .expectedSchema(ERROR_SCHEMA)
                .post()
                .responseToPojo(AuthErrorResponse.class);
        responsePayload = error;
        return error;
    }

    /**
     * GET /api/scenarios/api-auth/protected with a valid bearer token - the R5 happy path.
     */
    public ProtectedResourceResponse getProtectedResource(String accessToken) throws AutomationException {
        ExtentTestManager.step(_logger, "Access protected resource with bearer token");
        ProtectedResourceResponse response = newRequest()
                .path(APIEndPoint.API_AUTH_PROTECTED)
                .headers(Collections.singletonMap("Authorization", "Bearer " + accessToken))
                .expectedStatusCode(HttpStatus.OK)
                .expectedResponseContentType(ContentType.JSON)
                .expectedSchema(PROTECTED_SCHEMA)
                .get()
                .responseToPojo(ProtectedResourceResponse.class);
        responsePayload = response;
        return response;
    }

    /**
     * GET /api/scenarios/api-auth/protected expecting a rejection - covers R6 (no
     * Authorization header at all, pass accessToken == null), R7 (garbage token) and R8
     * (expired token).
     */
    public AuthErrorResponse getProtectedResourceExpectingError(String accessToken, HttpStatus expectedStatus) throws AutomationException {
        ExtentTestManager.step(_logger, "Access protected resource expecting rejection: " + expectedStatus.getCode());
        RestUtil request = newRequest().path(APIEndPoint.API_AUTH_PROTECTED);
        if (accessToken != null) {
            request = request.headers(Collections.singletonMap("Authorization", "Bearer " + accessToken));
        }
        AuthErrorResponse error = request
                .expectedStatusCode(expectedStatus)
                .expectedResponseContentType(ContentType.JSON)
                .expectedSchema(ERROR_SCHEMA)
                .get()
                .responseToPojo(AuthErrorResponse.class);
        responsePayload = error;
        return error;
    }

    /**
     * POST /api/scenarios/api-auth/refresh with a valid refresh token - the R9 happy path
     * (rotation). Returns the brand-new accessToken/refreshToken pair.
     */
    public TokenPairResponse refresh(String refreshToken) throws AutomationException {
        ExtentTestManager.step(_logger, "Refresh session with refresh token");
        TokenPairResponse tokenPair = newRequest()
                .path(APIEndPoint.API_AUTH_REFRESH)
                .contentType(ContentType.JSON)
                .body(new RefreshTokenRequest(refreshToken))
                .expectedStatusCode(HttpStatus.OK)
                .expectedResponseContentType(ContentType.JSON)
                .expectedSchema(TOKEN_PAIR_SCHEMA)
                .post()
                .responseToPojo(TokenPairResponse.class);
        responsePayload = tokenPair;
        return tokenPair;
    }

    /**
     * POST /api/scenarios/api-auth/refresh expecting a rejection - covers R10 (a
     * just-superseded refresh token), R11 (an explicitly revoked refresh token), R12
     * (garbage refresh token) and R13 (an access token submitted where a refresh token is
     * expected).
     */
    public AuthErrorResponse refreshExpectingError(String refreshToken, HttpStatus expectedStatus) throws AutomationException {
        ExtentTestManager.step(_logger, "Refresh session expecting rejection: " + expectedStatus.getCode());
        AuthErrorResponse error = newRequest()
                .path(APIEndPoint.API_AUTH_REFRESH)
                .contentType(ContentType.JSON)
                .body(new RefreshTokenRequest(refreshToken))
                .expectedStatusCode(expectedStatus)
                .expectedResponseContentType(ContentType.JSON)
                .expectedSchema(ERROR_SCHEMA)
                .post()
                .responseToPojo(AuthErrorResponse.class);
        responsePayload = error;
        return error;
    }

    /**
     * POST /api/scenarios/api-auth/revoke - always returns 200 {"ok":true}, even for a
     * refresh token that was never valid (R15), used to drive R11/R14/R16's state setup.
     */
    public RevokeResponse revoke(String refreshToken) throws AutomationException {
        ExtentTestManager.step(_logger, "Revoke refresh token");
        RevokeResponse response = newRequest()
                .path(APIEndPoint.API_AUTH_REVOKE)
                .contentType(ContentType.JSON)
                .body(new RefreshTokenRequest(refreshToken))
                .expectedStatusCode(HttpStatus.OK)
                .expectedResponseContentType(ContentType.JSON)
                .expectedSchema(REVOKE_SCHEMA)
                .post()
                .responseToPojo(RevokeResponse.class);
        responsePayload = response;
        return response;
    }

    public Object getResponse() {
        return responsePayload;
    }

}
