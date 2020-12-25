package framework.service;

import com.fasterxml.jackson.core.type.TypeReference;
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

    private final Logger _logger = LogManager.getLogger(UserProfileService.class);

    private User requestPayload;
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

    public UserProfileService getUserProfiles() throws AutomationException {

        ExtentTestManager.step(_logger, "Get User Profiles");

        RestUtil restInstance =
                RestUtil.init()
                        .path(APIEndPoint.USER_PROFILES)
                        .expectedStatusCode(httpStatus)
                        .expectedResponseContentType(responseContentType)
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
                RestUtil.init()
                        .path(APIEndPoint.USER_PROFILES + "{userid}")
                        .pathParam("userid", userid)
                        .expectedStatusCode(httpStatus)
                        .expectedResponseContentType(responseContentType)
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
        requestPayload = user;

        RestUtil restInstance =
                RestUtil.init()
                        .path(APIEndPoint.USER_PROFILES + "add")
                        .contentType(ContentType.JSON)
                        .body(user)
                        .expectedStatusCode(httpStatus)
                        .expectedResponseContentType(responseContentType)
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
        requestPayload = user;

        RestUtil restInstance =
                RestUtil.init()
                        .path(APIEndPoint.USER_PROFILES + "update")
                        .contentType(ContentType.JSON)
                        .body(user)
                        .expectedStatusCode(httpStatus)
                        .expectedResponseContentType(responseContentType)
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
                RestUtil.init()
                        .path(APIEndPoint.USER_PROFILES + "delete/{userid}")
                        .pathParam("userid", userid)
                        .expectedStatusCode(httpStatus)
                        .expectedResponseContentType(responseContentType)
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
