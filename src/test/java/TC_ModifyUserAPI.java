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
public class TC_ModifyUserAPI extends TestInit {

    /**
     * Test Case: TC07_ModifyUserProfile
     * Test Type: Positive
     * Description: To verify that user is able to modify existing user in system through API
     *
     * @throws AutomationException
     */
    @Test
    public void TC07_ModifyUserProfile() throws AutomationException {

        ExtentTestManager.startTest("Modify User Profile", "To verify that user is able to modify existing user in system through API");

        List<User> userList = (List<User>) UserProfileService
                .init()
                .getUserProfiles()
                .getResponse();

        User userToModify = userList.get(1);
        userToModify.setFirstname("ModifiedFirstName" + Randomizer.randomNumeric(5));

        User createdUser = (User) UserProfileService
                .init()
                .modifyUserProfiles(userToModify)
                .getResponse();

        assertThat(createdUser).usingRecursiveComparison().isEqualTo(userToModify);

    }

    /**
     * Test Case: TC08_ModifyUserProfile
     * Test Type: Negative
     * Description: To verify that user is able to modify user in system if userid provided does not exist through API
     *
     * @throws AutomationException
     */
    @Test
    public void TC08_ModifyUserProfile() throws AutomationException {

        ExtentTestManager.startTest("Modify User Profile", "To verify that user is able to modify user in system if userid provided does not exist through API");

        List<User> userList = (List<User>) UserProfileService
                .init()
                .getUserProfiles()
                .getResponse();

        User userToModify = userList.get(1);
        userToModify.setUserid("SampleInvalidUserID");

        UserProfileService
                .init()
                .isNegativeTest(HttpStatus.INTERNAL_SERVER_ERROR)
                .modifyUserProfiles(userToModify);

    }

}
