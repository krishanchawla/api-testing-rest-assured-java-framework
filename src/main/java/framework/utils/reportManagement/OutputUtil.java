package framework.utils.reportManagement;

import framework.utils.globalConstants.PathConfig;

import java.io.File;

/* -----------------------------------------------------------------------
   - ** Rest API Testing Framework using RestAssured **
   - Author: Krishan Chawla (krishanchawla1467@gmail.com)
   - Git Repo: https://github.com/krishanchawla/api-testing-rest-assured-java-framework
   ----------------------------------------------------------------------- */
public class OutputUtil {

    public static void createOutputDirectory() {
        String timestamp = System.getProperty("current.date");
        String outputDirName = "Output_" + timestamp;

        /** Output Directory Structure Definition **/
        PathConfig.setOutputDirName(outputDirName);
        PathConfig.setOutputDir("./Output/" + outputDirName);
        PathConfig.setReportsPath(PathConfig.getOutputDir() + File.separator + "Reports" + File.separator);
        PathConfig.setApplogsPath(PathConfig.getOutputDir() + File.separator + "Applogs" + File.separator);

        new File(PathConfig.getOutputDir()).mkdir();
        new File(PathConfig.getApplogsPath()).mkdir();
        new File(PathConfig.getReportsPath()).mkdir();
    }

}
