import framework.model.User;
import framework.service.UserProfileService;
import framework.utils.common.Randomizer;
import framework.utils.exceptions.AutomationException;
import framework.utils.globalConstants.HttpStatus;
import framework.utils.initializers.TestInit;
import framework.utils.reportManagement.extent.ExtentTestManager;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/* -----------------------------------------------------------------------
   - ** Rest API Testing Framework using RestAssured **
   - Author: Krishan Chawla (krishanchawla1467@gmail.com)
   - Git Repo: https://github.com/krishanchawla/api-testing-rest-assured-java-framework
   ----------------------------------------------------------------------- */
public class TC_AddUserAPI extends TestInit {

    /**
     * Test Case: TC02_AddUserProfile
     * Test Type: Positive
     * Description: To verify that user is able to add new user in system through API
     *
     * @throws AutomationException
     */
    @Test
    public void TC02_AddUserProfile() throws AutomationException {

        ExtentTestManager.startTest("Add User Profile", "To verify that user is able to add new users in system through API");

        User userToCreate = new User(Randomizer.randomNumberWithoutZero(5));
        User createdUser = (User) UserProfileService
                .init()
                .addUserProfiles(userToCreate)
                .getResponse();

        assertThat(createdUser).usingRecursiveComparison().ignoringFields("id").isEqualTo(userToCreate);

    }

    /**
     * Test Case: TC03_AddUserProfileExistingUserId
     * Test Type: Negative
     * Description: To verify that user is not able to add new user in system through API if provided userID already exists in system
     *
     * @throws AutomationException
     */
    @Test
    public void TC03_AddUserProfileExistingUserId() throws AutomationException {

        ExtentTestManager.startTest("Add User Profile", "To verify that user is not able to add new user in system through API if provided userID already exists in system");

        User userToCreate = new User(Randomizer.randomNumberWithoutZero(5));
        userToCreate.setUserid("KRISHAN001");

        UserProfileService
                .init()
                .isNegativeTest(HttpStatus.INTERNAL_SERVER_ERROR)
                .addUserProfiles(userToCreate);

    }

    /**
     * Test Case: TC04_AddUserProfileAlphabeticStatus
     * Test Type: Negative
     * Description: To verify that user is not able to add new user in system through API if provided status is not numeric
     *
     * @throws AutomationException
     */
    @Test
    public void TC04_AddUserProfileAlphabeticStatus() throws AutomationException {

        ExtentTestManager.startTest("Add User Profile", "To verify that user is not able to add new user in system through API if provided status is not numeric");

        User userToCreate = new User(Randomizer.randomNumberWithoutZero(5));
        userToCreate.setStatus("STATUS");

        UserProfileService
                .init()
                .isNegativeTest(HttpStatus.BAD_REQUEST)
                .addUserProfiles(userToCreate);

    }

}
