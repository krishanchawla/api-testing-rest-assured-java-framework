package framework.utils.reportManagement.extent;

import com.aventstack.extentreports.ExtentTest;

import java.util.HashMap;
import java.util.Map;

/* -----------------------------------------------------------------------
   - ** Rest API Testing Framework using RestAssured **
   - Author: Krishan Chawla (krishanchawla1467@gmail.com)
   - Git Repo: https://github.com/krishanchawla/api-testing-rest-assured-java-framework
   ----------------------------------------------------------------------- */
public class ExtentReporter {

    private static Map<Integer, ExtentTest> reporterMap = new HashMap<Integer, ExtentTest>();

    public static ExtentTest get(Integer integer) {
        return reporterMap.get(integer);
    }

    public static void set(Integer integer, ExtentTest test) {
        reporterMap.put(integer, test);
    }

}
