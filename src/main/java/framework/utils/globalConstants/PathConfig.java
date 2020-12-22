package framework.utils.globalConstants;

/* -----------------------------------------------------------------------
   - ** Rest API Testing Framework using RestAssured **
   - Author: Krishan Chawla (krishanchawla1467@gmail.com)
   - Git Repo: https://github.com/krishanchawla/api-testing-rest-assured-java-framework
   ----------------------------------------------------------------------- */
public class PathConfig {

    private final static String PROPERTIES_TEST = "./src/main/resources/test.properties";
    private final static String PROPERTIES_LOG4J = "./src/test/resources/log4j2.xml";

    private static String OUTPUT_DIR_NAME = "";
    private static String OUTPUT_DIR = "";
    private static String REPORTS_PATH = "";
    private static String APPLOGS_PATH = "";

    public static String getLog4JPropertiesPath() {
        return PROPERTIES_LOG4J;
    }

    public static String getOutputDirName() {
        return OUTPUT_DIR_NAME;
    }

    public static void setOutputDirName(String outputDirName) {
        OUTPUT_DIR_NAME = outputDirName;
    }

    public static String getOutputDir() {
        return OUTPUT_DIR;
    }

    public static void setOutputDir(String outputDir) {
        OUTPUT_DIR = outputDir;
    }

    public static String getReportsPath() {
        return REPORTS_PATH;
    }

    public static void setReportsPath(String reportsPath) {
        REPORTS_PATH = reportsPath;
    }

    public static String getApplogsPath() {
        return APPLOGS_PATH;
    }

    public static void setApplogsPath(String applogsPath) {
        APPLOGS_PATH = applogsPath;
    }

    public static String getPropertiesTest() {
        return PROPERTIES_TEST;
    }

    public static String getPropertiesLog4j() {
        return PROPERTIES_LOG4J;
    }
}
