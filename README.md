# REST API Test Automation Framework

![TestCase-Design](https://user-images.githubusercontent.com/28475979/102895822-69bbf280-448b-11eb-99ae-196813593faa.PNG)
This Test Automation Framework is created using Java + TestNG + RestAssured + ExtentReports, which can be used across different api based applications. 
In this approach, the endeavor is to wrap the common requirements of API's for specific application within the wrapper class in order to test the API's without spending any extra efforts on common requirements. 
With this framework in place, whenever we need to automate API's, we would not need to start from scratch, and begin with developing the test cases straight away.

# Pre-Requisites
* IntelliJ or Eclipse
* Java JDK-1.8 or higher
* Apache Maven 3 or higher

# Reporting
![ExtentReport](https://user-images.githubusercontent.com/28475979/102895706-31b4af80-448b-11eb-94d1-c1e6ef8220b2.PNG)
* The framework supports Extent Report for Reporting purpose. On execution, new directory is created under Ouput folder where all artifacts related to the execution resides. The report gives the details information on the Request & Response for each API executed.
