package framework.utils.reportManagement.extent;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.model.Media;
import org.apache.logging.log4j.Logger;

import java.text.MessageFormat;

/* -----------------------------------------------------------------------
   - ** Rest API Testing Framework using RestAssured **
   - Author: Krishan Chawla (krishanchawla1467@gmail.com)
   - Git Repo: https://github.com/krishanchawla/api-testing-rest-assured-java-framework
   ----------------------------------------------------------------------- */
public class ExtentTestManager {

    static ExtentReports extent = ExtentManager.getInstance();

    public static synchronized ExtentTest getTest() {
        return ExtentReporter
                .get((int) (Thread.currentThread().getId()));
    }

    public static synchronized ExtentTest startTest(String module, String testCase, String... params) {
        ExtentTest extentModule = ExtentModuleManager.getModule(module);
        String testCaseDescription = MessageFormat.format(testCase, params);
        ExtentTest test = extentModule.createNode(testCaseDescription);
        ExtentReporter.set((int) (Thread.currentThread().getId()), test);

        return test;
    }

    public static synchronized void endTest() {
        extent.flush();
    }

    public static synchronized void log(Logger logger, Status status, Object logObject) {
        ExtentTest test = getTest();

        if (logObject instanceof String) {
            test.log(status, (String) logObject);
            logger.info((String) logObject);
        } else if (logObject instanceof Throwable) {
            test.log(status, MarkupHelper.createCodeBlock(((Throwable) logObject).getMessage()));
            logger.info(((Throwable) logObject).getStackTrace().toString());
        } else if (logObject instanceof Media) {
            test.log(status, (Media) logObject);
        }

    }

    public static synchronized void step(Logger logger, String log) {
        ExtentTest test = getTest();
        test.log(Status.INFO, MarkupHelper.createLabel("Performing -> " + log, ExtentColor.INDIGO));
    }

}
