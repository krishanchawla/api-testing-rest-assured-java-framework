import framework.model.User;
import framework.service.UserProfileService;
import framework.utils.common.Randomizer;
import framework.utils.common.TestDataLoader;
import framework.utils.exceptions.AutomationException;
import framework.utils.globalConstants.HttpStatus;
import framework.utils.initializers.TestInit;
import framework.utils.reportManagement.extent.ExtentTestManager;
import org.testng.annotations.DataProvider;
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
     * Data source for TC03_AddUserProfileNegativeCases, backed by
     * src/test/resources/testdata/add-user-negative-cases.json - add a new
     * row to that file to cover another negative case without touching this class.
     */
    @DataProvider(name = "addUserNegativeCases")
    public Object[][] addUserNegativeCases() throws AutomationException {
        AddUserNegativeCase[] cases = TestDataLoader.loadArray(
                "testdata/add-user-negative-cases.json", AddUserNegativeCase[].class);

        Object[][] rows = new Object[cases.length][1];
        for (int i = 0; i < cases.length; i++) {
            rows[i][0] = cases[i];
        }
        return rows;
    }

    /**
     * Test Case: TC03_AddUserProfileNegativeCases
     * Test Type: Negative
     * Description: Table-driven negative cases for adding a user profile (see addUserNegativeCases data provider)
     *
     * @throws AutomationException
     */
    @Test(dataProvider = "addUserNegativeCases")
    public void TC03_AddUserProfileNegativeCases(AddUserNegativeCase testCase) throws AutomationException {

        ExtentTestManager.startTest("Add User Profile", "To verify that " + testCase.getDescription());

        User userToCreate = new User(Randomizer.randomNumberWithoutZero(5));
        if (testCase.getUserIdOverride() != null) {
            userToCreate.setUserid(testCase.getUserIdOverride());
        }
        if (testCase.getStatusOverride() != null) {
            userToCreate.setStatus(testCase.getStatusOverride());
        }

        UserProfileService
                .init()
                .isNegativeTest(HttpStatus.fromCode(testCase.getExpectedStatusCode()))
                .addUserProfiles(userToCreate);

    }

}
