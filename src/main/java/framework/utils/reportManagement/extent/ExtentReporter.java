package framework.utils.reportManagement.extent;

import com.aventstack.extentreports.ExtentTest;

/* -----------------------------------------------------------------------
   - ** Rest API Testing Framework using RestAssured **
   - Author: Krishan Chawla (krishanchawla1467@gmail.com)
   - Git Repo: https://github.com/krishanchawla/api-testing-rest-assured-java-framework
   -----------------------------------------------------------------------
   Holds the current ExtentTest per thread. Using a ThreadLocal instead of a
   shared map keyed by thread id avoids any need for synchronization and is
   correct under TestNG's parallel="methods" execution.
   ----------------------------------------------------------------------- */
public class ExtentReporter {

    private static final ThreadLocal<ExtentTest> CURRENT_TEST = new ThreadLocal<>();

    public static ExtentTest get() {
        return CURRENT_TEST.get();
    }

    public static void set(ExtentTest test) {
        CURRENT_TEST.set(test);
    }

}
