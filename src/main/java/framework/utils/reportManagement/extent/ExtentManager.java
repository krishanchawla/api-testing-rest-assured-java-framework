package framework.utils.reportManagement.extent;

import com.aventstack.extentreports.AnalysisStrategy;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import framework.utils.globalConstants.PathConfig;

import java.io.File;

/* -----------------------------------------------------------------------
   - ** Rest API Testing Framework using RestAssured **
   - Author: Krishan Chawla (krishanchawla1467@gmail.com)
   - Git Repo: https://github.com/krishanchawla/api-testing-rest-assured-java-framework
   ----------------------------------------------------------------------- */
public class ExtentManager {

    private static ExtentReports extent;
    private static String reportFileName = "ExecutionReport_" + System.getProperty("current.date") + ".html";
    private static String fileSeperator = System.getProperty("file.separator");
    private static String reportFilepath = PathConfig.getReportsPath();
    private static String reportFileLocation = reportFilepath + fileSeperator + reportFileName;


    public static ExtentReports getInstance() {
        if (extent == null)
            createInstance();
        return extent;
    }

    public static ExtentReports createInstance() {

        extent = new ExtentReports();
        extent.setAnalysisStrategy(AnalysisStrategy.CLASS);

        String fileName = getReportPath(reportFilepath);

        ExtentSparkReporter htmlReporter = new ExtentSparkReporter(fileName);
        htmlReporter.config().setTheme(Theme.STANDARD);
        htmlReporter.config().setDocumentTitle(reportFileName);
        htmlReporter.config().setEncoding("utf-8");
        htmlReporter.config().setReportName(reportFileName);
        htmlReporter.config().setTimeStampFormat("EEEE, MMMM dd, yyyy, hh:mm a '('zzz')'");

        extent.attachReporter(htmlReporter);

        return extent;
    }

    private static String getReportPath(String path) {
        File testDirectory = new File(path);
        if (!testDirectory.exists()) {
            if (testDirectory.mkdir()) {
                return reportFileLocation;
            } else {
                return System.getProperty("user.dir");
            }
        } else {
            //System.out.println("Directory already exists: " + path);
        }
        return reportFileLocation;
    }

}
