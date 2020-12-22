import framework.service.UserProfileService;
import framework.utils.exceptions.AutomationException;
import framework.utils.initializers.TestInit;
import framework.utils.reportManagement.extent.ExtentTestManager;
import org.testng.annotations.Test;

/* -----------------------------------------------------------------------
   - ** Rest API Testing Framework using RestAssured **
   - Author: Krishan Chawla (krishanchawla1467@gmail.com)
   - Git Repo: https://github.com/krishanchawla/api-testing-rest-assured-java-framework
   ----------------------------------------------------------------------- */
public class TC_GetAllUsersAPI extends TestInit {

    /**
     * Test Case: TC01_GetAllUsers
     * Description: To verify that user is able to get all users in system through API
     *
     * @throws AutomationException
     */
    @Test
    public void TC01_GetAllUsers() throws AutomationException {

        ExtentTestManager.startTest("Get All Users", "To verify that user is able to get all users in system through API");

        UserProfileService
                .init()
                .getUserProfiles();

    }

}
