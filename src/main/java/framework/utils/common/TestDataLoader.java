package framework.utils.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import framework.utils.exceptions.AutomationException;

import java.io.IOException;
import java.io.InputStream;

/* -----------------------------------------------------------------------
   - ** Rest API Testing Framework using RestAssured **
   - Author: Krishan Chawla (krishanchawla1467@gmail.com)
   - Git Repo: https://github.com/krishanchawla/api-testing-rest-assured-java-framework
   ----------------------------------------------------------------------- */
public final class TestDataLoader {

    private TestDataLoader() {
    }

    /**
     * Loads a JSON array test-data file from the classpath (e.g. "testdata/add-user-negative-cases.json")
     * into an array of the given type, for use as a TestNG @DataProvider source.
     */
    public static <T> T[] loadArray(String classpathResource, Class<T[]> arrayType) throws AutomationException {
        try (InputStream in = TestDataLoader.class.getClassLoader().getResourceAsStream(classpathResource)) {
            if (in == null) {
                throw new AutomationException("Test data file not found on classpath: " + classpathResource);
            }
            return new ObjectMapper().readValue(in, arrayType);
        } catch (IOException e) {
            throw new AutomationException(e);
        }
    }

}
