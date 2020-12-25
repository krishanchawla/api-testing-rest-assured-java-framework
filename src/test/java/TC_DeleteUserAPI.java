import framework.model.User;
import framework.service.UserProfileService;
import framework.utils.common.Randomizer;
import framework.utils.exceptions.AutomationException;
import framework.utils.globalConstants.HttpStatus;
import framework.utils.initializers.TestInit;
import framework.utils.reportManagement.extent.ExtentTestManager;
import org.testng.annotations.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/* -----------------------------------------------------------------------
   - ** Rest API Testing Framework using RestAssured **
   - Author: Krishan Chawla (krishanchawla1467@gmail.com)
   - Git Repo: https://github.com/krishanchawla/api-testing-rest-assured-java-framework
   ----------------------------------------------------------------------- */
public class TC_DeleteUserAPI extends TestInit {

    /**
     * Test Case: TC09_DeleteUserProfile
     * Test Type: Positive
     * Description: To verify that user is able to delete existing user in system through API
     *
     * @throws AutomationException
     */
    @Test
    public void TC09_DeleteUserProfile() throws AutomationException {

        ExtentTestManager.startTest("Delete User Profile", "To verify that user is able to delete existing user in system through API");

        User createdUser = (User) UserProfileService
                .init()
                .addUserProfiles(new User(Randomizer.randomNumberWithoutZero(5)))
                .getResponse();

        UserProfileService
                .init()
                .deleteUserProfiles(createdUser.getUserid());

    }

    /**
     * Test Case: TC10_DeleteUserProfile
     * Test Type: Negative
     * Description: To verify that user is able to delete user in system if userid provided does not exist through API
     *
     * @throws AutomationException
     */
    @Test
    public void TC10_DeleteUserProfile() throws AutomationException {

        ExtentTestManager.startTest("Delete User Profile", "To verify that user is able to delete user in system if userid provided does not exist through API");

        List<User> userList = (List<User>) UserProfileService
                .init()
                .getUserProfiles()
                .getResponse();

        User userToDelete = userList.get(1);
        userToDelete.setUserid("SampleInvalidUserID");

        UserProfileService
                .init()
                .isNegativeTest(HttpStatus.INTERNAL_SERVER_ERROR)
                .modifyUserProfiles(userToDelete);

    }

}
