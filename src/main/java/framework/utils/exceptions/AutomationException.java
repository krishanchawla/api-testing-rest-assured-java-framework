package framework.utils.exceptions;

/* -----------------------------------------------------------------------
   - ** Rest API Testing Framework using RestAssured **
   - Author: Krishan Chawla (krishanchawla1467@gmail.com)
   - Git Repo: https://github.com/krishanchawla/api-testing-rest-assured-java-framework
   ----------------------------------------------------------------------- */
public class AutomationException extends Exception {

    public AutomationException(String message) {
        super(message);
    }

    public AutomationException(Exception ex) {
        super(ex);
    }

}
