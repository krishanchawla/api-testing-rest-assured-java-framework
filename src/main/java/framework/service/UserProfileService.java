package framework.service;

import com.fasterxml.jackson.core.type.TypeReference;
import framework.auth.AuthStrategyFactory;
import framework.model.User;
import framework.model.error.ValidationError;
import framework.utils.common.RestUtil;
import framework.utils.exceptions.AutomationException;
import framework.utils.globalConstants.APIEndPoint;
import framework.utils.globalConstants.HttpStatus;
import framework.utils.reportManagement.extent.ExtentTestManager;
import io.restassured.http.ContentType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/* -----------------------------------------------------------------------
   - ** Rest API Testing Framework using RestAssured **
   - Author: Krishan Chawla (krishanchawla1467@gmail.com)
   - Git Repo: https://github.com/krishanchawla/api-testing-rest-assured-java-framework
   ----------------------------------------------------------------------- */
public class UserProfileService {

    private static final String SERVICE_KEY = "user-service";

    private static final String USER_SCHEMA = "schemas/user.schema.json";
    private static final String USER_LIST_SCHEMA = "schemas/user-list.schema.json";
    private static final String VALIDATION_ERROR_SCHEMA = "schemas/validation-error.schema.json";

    private final Logger _logger = LogManager.getLogger(UserProfileService.class);

    private Object responsePayload;
    private boolean isNegativeTest = false;
    private HttpStatus httpStatus = HttpStatus.OK;
    private ContentType responseContentType = ContentType.JSON;

    public static UserProfileService init() {
        return new UserProfileService();
    }

    public UserProfileService isNegativeTest(HttpStatus httpStatus) {
        this.responseContentType = ContentType.JSON;
        this.isNegativeTest = true;
        this.httpStatus = httpStatus;
        return this;
    }

    private RestUtil newRequest() throws AutomationException {
        return RestUtil.init(SERVICE_KEY).auth(AuthStrategyFactory.forService(SERVICE_KEY));
    }

    public UserProfileService getUserProfiles() throws AutomationException {

        ExtentTestManager.step(_logger, "Get User Profiles");

        RestUtil restInstance =
                newRequest()
                        .path(APIEndPoint.USER_PROFILES)
                        .expectedStatusCode(httpStatus)
                        .expectedResponseContentType(responseContentType)
                        .expectedSchema(isNegativeTest ? VALIDATION_ERROR_SCHEMA : USER_LIST_SCHEMA)
                        .get();

        if (!isNegativeTest) {
            responsePayload = restInstance.responseToPojo(new TypeReference<List<User>>() {});
        } else {
            responsePayload = restInstance.responseToPojo(ValidationError.class);
        }

        return this;
    }

    public UserProfileService getUserProfileByID(String userid) throws AutomationException {

        ExtentTestManager.step(_logger, "Get User By ID");

        RestUtil restInstance =
                newRequest()
                        .path(APIEndPoint.USER_PROFILES + "{userid}")
                        .pathParam("userid", userid)
                        .expectedStatusCode(httpStatus)
                        .expectedResponseContentType(responseContentType)
                        .expectedSchema(isNegativeTest ? VALIDATION_ERROR_SCHEMA : USER_LIST_SCHEMA)
                        .get();

        if (!isNegativeTest) {
            responsePayload = restInstance.responseToPojo(new TypeReference<List<User>>() {});
        } else {
            responsePayload = restInstance.responseToPojo(ValidationError.class);
        }

        return this;
    }

    public UserProfileService addUserProfiles(User user) throws AutomationException {

        ExtentTestManager.step(_logger, "Add User Profile");

        RestUtil restInstance =
                newRequest()
                        .path(APIEndPoint.USER_PROFILES + "add")
                        .contentType(ContentType.JSON)
                        .body(user)
                        .expectedStatusCode(httpStatus)
                        .expectedResponseContentType(responseContentType)
                        .expectedSchema(isNegativeTest ? VALIDATION_ERROR_SCHEMA : USER_SCHEMA)
                        .put();

        if (!isNegativeTest) {
            responsePayload = restInstance.responseToPojo(User.class);
        } else {
            responsePayload = restInstance.responseToPojo(ValidationError.class);
        }

        return this;
    }

    public UserProfileService modifyUserProfiles(User user) throws AutomationException {

        ExtentTestManager.step(_logger, "Modify User Profile");

        RestUtil restInstance =
                newRequest()
                        .path(APIEndPoint.USER_PROFILES + "update")
                        .contentType(ContentType.JSON)
                        .body(user)
                        .expectedStatusCode(httpStatus)
                        .expectedResponseContentType(responseContentType)
                        .expectedSchema(isNegativeTest ? VALIDATION_ERROR_SCHEMA : USER_SCHEMA)
                        .post();

        if (!isNegativeTest) {
            responsePayload = restInstance.responseToPojo(User.class);
        } else {
            responsePayload = restInstance.responseToPojo(ValidationError.class);
        }

        return this;
    }

    public UserProfileService deleteUserProfiles(String userid) throws AutomationException {

        ExtentTestManager.step(_logger, "Delete User Profile");

        RestUtil restInstance =
                newRequest()
                        .path(APIEndPoint.USER_PROFILES + "delete/{userid}")
                        .pathParam("userid", userid)
                        .expectedStatusCode(httpStatus)
                        .expectedResponseContentType(responseContentType)
                        .expectedSchema(isNegativeTest ? VALIDATION_ERROR_SCHEMA : USER_SCHEMA)
                        .delete();

        if (!isNegativeTest) {
            responsePayload = restInstance.responseToPojo(User.class);
        } else {
            responsePayload = restInstance.responseToPojo(ValidationError.class);
        }

        return this;
    }

    public Object getResponse() {
        return responsePayload;
    }

}
