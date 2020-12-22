package framework.utils.logManagement;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.Markup;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import framework.utils.reportManagement.extent.ExtentTestManager;
import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.filter.log.LogDetail;
import io.restassured.internal.print.RequestPrinter;
import io.restassured.internal.print.ResponsePrinter;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import org.apache.commons.io.output.WriterOutputStream;

import java.io.PrintStream;
import java.io.StringWriter;
import java.util.HashSet;

/* -----------------------------------------------------------------------
   - ** Rest API Testing Framework using RestAssured **
   - Author: Krishan Chawla (krishanchawla1467@gmail.com)
   - Git Repo: https://github.com/krishanchawla/api-testing-rest-assured-java-framework
   ----------------------------------------------------------------------- */
public class APIResponseFilter implements Filter {

    @Override
    public Response filter(FilterableRequestSpecification requestSpec, FilterableResponseSpecification responseSpec, FilterContext ctx) {
        Response response = ctx.next(requestSpec, responseSpec);
        String responseStr = ResponsePrinter.print(response,
                response,
                new PrintStream(new WriterOutputStream(new StringWriter())),
                LogDetail.ALL,
                true,
                new HashSet<>());

        String requestStr = RequestPrinter.print(requestSpec,
                requestSpec.getMethod(),
                requestSpec.getURI(),
                LogDetail.ALL,
                new HashSet<>(),
                new PrintStream(new WriterOutputStream(new StringWriter())),
                true);

        Markup m = MarkupHelper.createCodeBlock(requestStr, responseStr);
        ExtentTestManager.getTest().log(Status.INFO, m);
        return response;
    }

}
