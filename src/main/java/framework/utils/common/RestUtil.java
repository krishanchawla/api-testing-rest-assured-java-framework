package framework.utils.common;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import framework.auth.AuthStrategy;
import framework.config.EnvironmentConfig;
import framework.utils.exceptions.AutomationException;
import framework.utils.globalConstants.HttpStatus;
import framework.utils.logManagement.APIResponseFilter;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.config.EncoderConfig;
import io.restassured.config.HttpClientConfig;
import io.restassured.http.ContentType;
import io.restassured.http.Cookie;
import io.restassured.http.Cookies;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import java.io.IOException;
import java.util.Map;
import java.util.function.Supplier;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

/* -----------------------------------------------------------------------
   - ** Rest API Testing Framework using RestAssured **
   - Author: Krishan Chawla (krishanchawla1467@gmail.com)
   - Git Repo: https://github.com/krishanchawla/api-testing-rest-assured-java-framework
   ----------------------------------------------------------------------- */
public class RestUtil {

    private final RequestSpecBuilder requestSpecBuilder;
    private RequestSpecification requestSpecification;
    private Response apiResponse;

    private HttpStatus expectedStatusCode = HttpStatus.OK;
    private String expectedResponseContentType;
    private String expectedSchemaClasspath;

    private final int retryMaxAttempts;
    private final long retryBackoffMs;

    /**
     * Returns a new object of RestUtil class bound to the given service.
     * The service's base URL, timeouts and retry policy are resolved from
     * EnvironmentConfig (config/common.properties + config/&lt;env&gt;.properties).
     *
     * @param serviceKey key used to look up "service.&lt;serviceKey&gt;.baseUrl" etc.
     * @return this
     * @throws AutomationException if required config is missing
     */
    public static RestUtil init(String serviceKey) throws AutomationException {
        return new RestUtil(serviceKey);
    }

    private RestUtil(String serviceKey) throws AutomationException {
        EnvironmentConfig config = EnvironmentConfig.init();

        int connectTimeoutMs = config.getIntProperty("http.connectTimeoutMs", 5000);
        int socketTimeoutMs = config.getIntProperty("http.socketTimeoutMs", 10000);
        this.retryMaxAttempts = config.getIntProperty("http.retry.maxAttempts", 1);
        this.retryBackoffMs = config.getIntProperty("http.retry.backoffMs", 0);

        EncoderConfig encoderConfig = new EncoderConfig().appendDefaultContentCharsetToContentTypeIfUndefined(false);
        HttpClientConfig httpClientConfig = HttpClientConfig.httpClientConfig()
                .setParam("http.connection.timeout", connectTimeoutMs)
                .setParam("http.socket.timeout", socketTimeoutMs);

        requestSpecBuilder = new RequestSpecBuilder();
        requestSpecBuilder.setBaseUri(config.getProperty("service." + serviceKey + ".baseUrl"));
        requestSpecBuilder.setConfig(RestAssured.config().encoderConfig(encoderConfig).httpClient(httpClientConfig));
    }

    /**
     * Applies an authentication strategy (API key, bearer token, basic, OAuth2 client-credentials, ...)
     * to this request.
     *
     * @param authStrategy the strategy to apply
     * @return this
     */
    public RestUtil auth(AuthStrategy authStrategy) {
        authStrategy.apply(requestSpecBuilder);
        return this;
    }

    /**
     * Defines API Endpoint Path to Request Specification
     *
     * @param path
     * @return this
     */
    public RestUtil path(String path) {
        requestSpecBuilder.setBasePath(path);
        return this;
    }

    /**
     * Defines Path Parameters to Request Specification
     *
     * @param key
     * @param value
     * @return this
     */
    public RestUtil pathParam(String key, String value) {
        requestSpecBuilder.addPathParam(key, value);
        return this;
    }

    /**
     * Defines Query Parameters to Request Specification
     *
     * @param key
     * @param value
     * @return this
     */
    public RestUtil queryParam(String key, String value) {
        requestSpecBuilder.addQueryParam(key, value);
        return this;
    }

    /**
     * Defines Content Type Header to Request Specification
     *
     * @param contentType
     * @return this
     */
    public RestUtil contentType(ContentType contentType) {
        requestSpecBuilder.setContentType(contentType);
        return this;
    }

    /**
     * Defines Headers to Request Specification
     *
     * @param headers
     * @return this
     */
    public RestUtil headers(Map<String, String> headers) {
        requestSpecBuilder.addHeaders(headers);
        return this;
    }

    /**
     * Defines Cookies to Request Specification
     *
     * @param cookies
     * @return this
     */
    public RestUtil cookies(Map<String, String> cookies) {
        requestSpecBuilder.addCookies(cookies);
        return this;
    }

    /**
     * Defines Cookies to Request Specification
     *
     * @param cookies
     * @return this
     */
    public RestUtil cookies(Cookies cookies) {
        requestSpecBuilder.addCookies(cookies);
        return this;
    }

    /**
     * Defines Cookie to Request Specification
     *
     * @param cookie
     * @return this
     */
    public RestUtil cookie(Cookie cookie) {
        requestSpecBuilder.addCookie(cookie);
        return this;
    }

    /**
     * Defines Body to Request Specification
     *
     * @param body
     * @return this
     */
    public RestUtil body(Object body) {
        requestSpecBuilder.setBody(body);
        return this;
    }

    /**
     * Defines the Expected Status Code following successful api execution for validation
     *
     * @param expectedStatusCode
     * @return this
     */
    public RestUtil expectedStatusCode(HttpStatus expectedStatusCode) {
        this.expectedStatusCode = expectedStatusCode;
        return this;
    }

    /**
     * Defines the Expected Response Content Type following successful api execution for validation
     *
     * @param contentType
     * @return this
     */
    public RestUtil expectedResponseContentType(ContentType contentType) {
        this.expectedResponseContentType = contentType.toString();
        return this;
    }

    /**
     * Defines the Expected Response Content Type following successful api execution for validation
     *
     * @param contentType
     * @return this
     */
    public RestUtil expectedResponseContentType(String contentType) {
        this.expectedResponseContentType = contentType;
        return this;
    }

    /**
     * Defines a JSON schema (classpath resource, e.g. "schemas/user.schema.json") that the
     * response body must satisfy in addition to the status code / content type checks.
     *
     * @param classpathSchemaPath classpath-relative path to the JSON schema file
     * @return this
     */
    public RestUtil expectedSchema(String classpathSchemaPath) {
        this.expectedSchemaClasspath = classpathSchemaPath;
        return this;
    }

    /**
     * Hits the Pre-Defined Request Specification as PUT Request
     * <p>
     * On successful response, method validates:
     * -   Status Code against the Status Code provided in Request Specification
     * -   Content Type against the Content Type provided in Request Specification
     * -   JSON Schema, if one was provided via expectedSchema()
     *
     * @return this
     */
    public RestUtil put() {
        requestSpecification = requestSpecBuilder.build();
        apiResponse = executeWithRetry(() -> given()
                .log().all()
                .filter(new APIResponseFilter())
                .spec(requestSpecification)
                .when()
                .put()
                .then()
                .spec(expectedResponseSpec())
                .extract()
                .response());

        return this;
    }

    /**
     * Hits the Pre-Defined Request Specification as DELETE Request
     * <p>
     * On successful response, method validates:
     * -   Status Code against the Status Code provided in Request Specification
     * -   Content Type against the Content Type provided in Request Specification
     * -   JSON Schema, if one was provided via expectedSchema()
     *
     * @return this
     */
    public RestUtil delete() {
        requestSpecification = requestSpecBuilder.build();
        apiResponse = executeWithRetry(() -> given()
                .log().all()
                .filter(new APIResponseFilter())
                .spec(requestSpecification)
                .when()
                .delete()
                .then()
                .spec(expectedResponseSpec())
                .extract()
                .response());

        return this;
    }

    /**
     * Hits the Pre-Defined Request Specification as POST Request
     * <p>
     * On successful response, method validates:
     * -   Status Code against the Status Code provided in Request Specification
     * -   Content Type against the Content Type provided in Request Specification
     * -   JSON Schema, if one was provided via expectedSchema()
     *
     * @return this
     */
    public RestUtil post() {
        requestSpecification = requestSpecBuilder.build();
        apiResponse = executeWithRetry(() -> given()
                .log().all()
                .filter(new APIResponseFilter())
                .spec(requestSpecification)
                .when()
                .post()
                .then()
                .spec(expectedResponseSpec())
                .extract()
                .response());

        return this;
    }

    /**
     * Hits the Pre-Defined Request Specification as GET Request
     * <p>
     * On successful response, method validates:
     * -   Status Code against the Status Code provided in Request Specification
     * -   Content Type against the Content Type provided in Request Specification
     * -   JSON Schema, if one was provided via expectedSchema()
     *
     * @return this
     */
    public RestUtil get() {
        requestSpecification = requestSpecBuilder.build();
        apiResponse = executeWithRetry(() -> given()
                .log().all()
                .filter(new APIResponseFilter())
                .spec(requestSpecification)
                .when()
                .get()
                .then()
                .spec(expectedResponseSpec())
                .extract()
                .response());

        return this;
    }

    private ResponseSpecification expectedResponseSpec() {
        ResponseSpecBuilder responseSpecBuilder = new ResponseSpecBuilder()
                .expectStatusCode(expectedStatusCode.getCode())
                .expectContentType(expectedResponseContentType);

        if (expectedSchemaClasspath != null) {
            responseSpecBuilder.expectBody(matchesJsonSchemaInClasspath(expectedSchemaClasspath));
        }

        return responseSpecBuilder.build();
    }

    /**
     * Retries the given HTTP call on transient network failures (connect/socket timeout)
     * using the configured http.retry.maxAttempts / http.retry.backoffMs. Assertion
     * failures (wrong status code, schema mismatch, ...) are never retried - they are
     * genuine test failures, not flakiness.
     */
    private Response executeWithRetry(Supplier<Response> requestExecutor) {
        RuntimeException lastFailure = null;

        for (int attempt = 1; attempt <= Math.max(1, retryMaxAttempts); attempt++) {
            try {
                return requestExecutor.get();
            } catch (RuntimeException ex) {
                if (!isTransientNetworkFailure(ex) || attempt == retryMaxAttempts) {
                    throw ex;
                }
                lastFailure = ex;
                sleepQuietly(retryBackoffMs * attempt);
            }
        }

        throw lastFailure;
    }

    private static boolean isTransientNetworkFailure(Throwable ex) {
        for (Throwable cause = ex; cause != null; cause = cause.getCause()) {
            // covers java.net.SocketTimeoutException and org.apache.http.conn.ConnectTimeoutException,
            // both of which extend InterruptedIOException rather than each other
            if (cause instanceof java.io.InterruptedIOException || cause instanceof java.net.ConnectException) {
                return true;
            }
        }
        return false;
    }

    private static void sleepQuietly(long millis) {
        if (millis <= 0) {
            return;
        }
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Returns the apiResponse Object
     *
     * @return apiResponse
     */
    public Response response() {
        return apiResponse;
    }

    /**
     * Returns the apiResponse Object as String
     *
     * @return apiResponse
     */
    public String getApiResponseAsString() {
        return apiResponse.asString();
    }

    /**
     * Converts the Response Object into the provided Class Type
     *
     * @param type
     * @param <T>
     * @return
     * @throws AutomationException
     */
    public <T> T responseToPojo(Class<T> type) throws AutomationException {
        try {
            return new ObjectMapper().enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY).readValue(getApiResponseAsString(), type);
        } catch (IOException ioex) {
            throw new AutomationException("Response Received did not match the expected Response Format POJO: " + type.getName() + ioex);
        }
    }

    /**
     * Converts the Response Object into the provided Class Type
     *
     * @param type
     * @param <T>
     * @return
     * @throws AutomationException
     */
    @SuppressWarnings("unchecked")
    public <T> T responseToPojo(TypeReference type) throws AutomationException {
        try {
            return (T) new ObjectMapper().enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY).readValue(getApiResponseAsString(), type);
        } catch (IOException ioex) {
            throw new AutomationException(ioex);
        }
    }

}
