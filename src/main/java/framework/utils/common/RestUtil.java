package framework.utils.common;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import framework.utils.exceptions.AutomationException;
import framework.utils.globalConstants.HttpStatus;
import framework.utils.logManagement.APIResponseFilter;
import framework.utils.propertiesManagement.TestProperties;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.EncoderConfig;
import io.restassured.http.ContentType;
import io.restassured.http.Cookie;
import io.restassured.http.Cookies;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.io.IOException;
import java.util.Map;

import static io.restassured.RestAssured.given;

/* -----------------------------------------------------------------------
   - ** Rest API Testing Framework using RestAssured **
   - Author: Krishan Chawla (krishanchawla1467@gmail.com)
   - Git Repo: https://github.com/krishanchawla/api-testing-rest-assured-java-framework
   ----------------------------------------------------------------------- */
public class RestUtil {

    private RequestSpecBuilder requestSpecBuilder;
    private RequestSpecification requestSpecification;
    private Response apiResponse;

    private HttpStatus expectedStatusCode = HttpStatus.OK;
    private String expectedResponseContentType;

    /**
     * Returns a new object of RestUtil class
     *
     * @return this
     * @throws AutomationException
     */
    public static RestUtil init() throws AutomationException {
        return new RestUtil();
    }

    /**
     * RestUtil Default Constructor
     *
     * @throws AutomationException
     */
    public RestUtil() throws AutomationException {
        initializeRequestSpec();
    }

    /**
     * Initializes Request Specifications including the Base URI Path from test.properties
     *
     * @throws AutomationException
     */
    private void initializeRequestSpec() throws AutomationException {

        EncoderConfig encoderconfig = new EncoderConfig();
        requestSpecBuilder = new RequestSpecBuilder();

        /* ----- H E A D E R S ----- */
        requestSpecBuilder.setBaseUri(TestProperties.init().getProperty("app.url"));
        requestSpecBuilder.setConfig(RestAssured.config().encoderConfig(encoderconfig.appendDefaultContentCharsetToContentTypeIfUndefined(false)));

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
     * Hits the Pre-Defined Request Specification as PUT Request
     * <p>
     * On successful response, method validates:
     * -   Status Code against the Status Code provided in Request Specification
     * -   Content Type against the Content Type provided in Request Specification
     *
     * @return this
     */
    public RestUtil put() {
        requestSpecification = requestSpecBuilder.build();
        apiResponse =
                given()
                        .log().all()
                        .filter(new APIResponseFilter())
                        .spec(requestSpecification)
                        .when()
                        .put()
                        .then()
                        .assertThat()
                        .statusCode(expectedStatusCode.getCode())
                        .contentType(expectedResponseContentType)
                        .and()
                        .extract()
                        .response();

        return this;
    }

    /**
     * Hits the Pre-Defined Request Specification as DELETE Request
     * <p>
     * On successful response, method validates:
     * -   Status Code against the Status Code provided in Request Specification
     * -   Content Type against the Content Type provided in Request Specification
     *
     * @return this
     */
    public RestUtil delete() {
        requestSpecification = requestSpecBuilder.build();
        apiResponse =
                given()
                        .log().all()
                        .filter(new APIResponseFilter())
                        .spec(requestSpecification)
                        .when()
                        .delete()
                        .then()
                        .assertThat()
                        .statusCode(expectedStatusCode.getCode())
                        .contentType(expectedResponseContentType)
                        .and()
                        .extract()
                        .response();

        return this;
    }

    /**
     * Hits the Pre-Defined Request Specification as POST Request
     * <p>
     * On successful response, method validates:
     * -   Status Code against the Status Code provided in Request Specification
     * -   Content Type against the Content Type provided in Request Specification
     *
     * @return this
     */
    public RestUtil post() {
        requestSpecification = requestSpecBuilder.build();
        apiResponse =
                given()
                        .log().all()
                        .filter(new APIResponseFilter())
                        .spec(requestSpecification)
                        .when()
                        .post()
                        .then()
                        .assertThat()
                        .statusCode(expectedStatusCode.getCode())
                        .contentType(expectedResponseContentType)
                        .and()
                        .extract()
                        .response();

        return this;
    }

    /**
     * Hits the Pre-Defined Request Specification as GET Request
     * <p>
     * On successful response, method validates:
     * -   Status Code against the Status Code provided in Request Specification
     * -   Content Type against the Content Type provided in Request Specification
     *
     * @return this
     */
    public RestUtil get() {
        requestSpecification = requestSpecBuilder.build();
        apiResponse =
                given()
                        .log().all()
                        .filter(new APIResponseFilter())
                        .spec(requestSpecification)
                        .when()
                        .get()
                        .then()
                        .assertThat()
                        .statusCode(expectedStatusCode.getCode())
                        .contentType(expectedResponseContentType)
                        .and()
                        .extract()
                        .response();

        return this;
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
    public <T> T responseToPojo(TypeReference type) throws AutomationException {
        try {
            return (T) new ObjectMapper().enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY).readValue(getApiResponseAsString(), type);
        } catch (IOException ioex) {
            throw new AutomationException(ioex);
        }
    }

}
