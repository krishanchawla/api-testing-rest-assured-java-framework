import framework.service.UserProfileService;
import framework.utils.exceptions.AutomationException;
import framework.utils.globalConstants.HttpStatus;
import framework.utils.initializers.TestInit;
import framework.utils.reportManagement.extent.ExtentTestManager;
import org.testng.annotations.Test;

/* -----------------------------------------------------------------------
   - ** Rest API Testing Framework using RestAssured **
   - Author: Krishan Chawla (krishanchawla1467@gmail.com)
   - Git Repo: https://github.com/krishanchawla/api-testing-rest-assured-java-framework
   ----------------------------------------------------------------------- */
public class TC_GetUserByID extends TestInit {

    /**
     * Test Case: TC05_GetUserByID
     * Description: To verify that user is able to get user details through API
     *
     * @throws AutomationException
     */
    @Test
    public void TC05_GetUserByID() throws AutomationException {

        ExtentTestManager.startTest("Get User By ID", "To verify that user is able to get user details through API if userid is provided");

        UserProfileService
                .init()
                .getUserProfileByID("KRISHAN001");

    }

    /**
     * Test Case: TC06_GetUserByIDInvalidUserID
     * Test Type: Negative
     * Description: To verify that user is not able to get user details through API if userid provided does not exist
     *
     * @throws AutomationException
     */
    @Test
    public void TC06_GetUserByIDInvalidUserID() throws AutomationException {

        ExtentTestManager.startTest("Get User By ID", "To verify that user is not able to get user details through API if userid provided does not exist");

        UserProfileService
                .init()
                .isNegativeTest(HttpStatus.INTERNAL_SERVER_ERROR)
                .getUserProfileByID("INVALIDTESTID");

    }

}
