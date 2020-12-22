package framework.utils.propertiesManagement;

import framework.utils.exceptions.AutomationException;
import framework.utils.globalConstants.PathConfig;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Properties;

/* -----------------------------------------------------------------------
   - ** Rest API Testing Framework using RestAssured **
   - Author: Krishan Chawla (krishanchawla1467@gmail.com)
   - Git Repo: https://github.com/krishanchawla/api-testing-rest-assured-java-framework
   ----------------------------------------------------------------------- */
public final class TestProperties {

    private static final TestProperties TEST_PROPERTIES = new TestProperties();
    private static Properties properties;

    public static final TestProperties init() {
        TestProperties testProp = TEST_PROPERTIES;
        testProp.getInstance();
        return testProp;
    }

    private void getInstance() {
        if (properties == null) {
            properties = new Properties();
            System.out.println("Loading Properties");
            try {
                FileInputStream inputStream = new FileInputStream(PathConfig.getPropertiesTest());
                properties.load(inputStream);
            } catch (FileNotFoundException ex) {

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getProperty(String propertyName) throws AutomationException {

        Object value = properties.get(propertyName);
        if (value != null) {
            return properties.get(propertyName).toString();
        } else {
            String errorLog = MessageFormat.format("Error occurred while getting {0} Property from MlsTestProperties. This could be due to no such property available in MlsTestProperties.properties file.", propertyName);
            throw new AutomationException(errorLog);
        }
    }

    public boolean getBooleanProperty(String propertyName) {
        return Boolean.parseBoolean(properties.getProperty(propertyName));
    }

    public Integer getIntegerProperty(String propertyName) {
        return Integer.parseInt(properties.getProperty(propertyName));
    }

}
