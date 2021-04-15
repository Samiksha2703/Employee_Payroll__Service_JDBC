package com.bridgelabz.dbdemotest;

import com.bridgelabz.dbdemo.EmployeePayrollData;
import com.bridgelabz.dbdemo.EmployeePayrollService;
import com.google.gson.Gson;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.time.LocalDate;
import java.util.Arrays;

public class RestAssuredTest {

    @Before
    public void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 3000;
    }

    public EmployeePayrollData[] getEmployeeList() {
        Response response = RestAssured.get("/employee_payroll");
        System.out.println("Employee Payroll Entries in JSONSever:\n" + response.asString());
        EmployeePayrollData[] arrayOfEmployees = new Gson().fromJson(response.asString(), EmployeePayrollData[].class);
        return arrayOfEmployees;
    }


    private Response addEmployeeToJsonServer(EmployeePayrollData employeePayrollData) {
        String empJson = new Gson().toJson(employeePayrollData);
        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/json");
        request.body(empJson);
        return request.post("/employee_payroll");
    }

    @Test
    public void givenEmployeeDataInJsonServer_WhenRetrieved_ShouldMatchTheCount() {
        EmployeePayrollData[] arrayOfEmployees = getEmployeeList();
        EmployeePayrollService employeePayrollService;
        employeePayrollService = new EmployeePayrollService(Arrays.asList(arrayOfEmployees));
        long entries = employeePayrollService.countEntries(EmployeePayrollService.IOService.REST_IO);
        Assertions.assertEquals(2, entries);
    }

    @Test
    public void givenNewEmployee_WhenAdded_ShouldMatch201ResponseAndCount() {
        EmployeePayrollData[] arrayOfEmployees = getEmployeeList();
        EmployeePayrollService employeePayrollService;
        employeePayrollService = new EmployeePayrollService(Arrays.asList(arrayOfEmployees));
        EmployeePayrollData employeePayrollData = new EmployeePayrollData(0, "Mark Zuckerberg", "M",
                300000.0, LocalDate.now());
        System.out.println("Data :" +employeePayrollData);
        Response response = addEmployeeToJsonServer(employeePayrollData);
        int statusCode = response.getStatusCode();
        Assertions.assertEquals(201, statusCode);

        employeePayrollData = new Gson().fromJson(response.asString(), EmployeePayrollData.class);
        employeePayrollService.addEmployeeAndPayroll(employeePayrollData, EmployeePayrollService.IOService.REST_IO);
        long entries = employeePayrollService.countEntries(EmployeePayrollService.IOService.REST_IO);
        Assertions.assertEquals(3, entries);
    }

    @Test
    public void givenListOfNewEmployee_WhenAdded_ShouldMatch201ResponseAndCount() {
        EmployeePayrollData[] arrayOfEmployees = getEmployeeList();
        EmployeePayrollService employeePayrollService;
        employeePayrollService = new EmployeePayrollService(Arrays.asList(arrayOfEmployees));
        EmployeePayrollData[] arrayOfEmployeePayroll = {
                new EmployeePayrollData(0, "Sundar", "M", 500000.0, LocalDate.now()),
                new EmployeePayrollData(0, "Mukesh", "M", 1000000.0, LocalDate.now()),
                new EmployeePayrollData(0, "Anil", "M", 200000.0, LocalDate.now())
        };
        for (EmployeePayrollData employeePayrollData : arrayOfEmployeePayroll) {
            Response response = addEmployeeToJsonServer(employeePayrollData);
            int statusCode = response.getStatusCode();
            Assertions.assertEquals(201, statusCode);

            employeePayrollData = new Gson().fromJson(response.asString(), EmployeePayrollData.class);
            employeePayrollService.addEmployeeAndPayroll(employeePayrollData, EmployeePayrollService.IOService.REST_IO);
        }
        long entries = employeePayrollService.countEntries(EmployeePayrollService.IOService.REST_IO);
        Assertions.assertEquals(6, entries);
    }

    @Test
    public void givenNewSalaryForEmployee_WhenUpdated_ShouldMatch() {
        EmployeePayrollData[] arrayOfEmployees = getEmployeeList();
        EmployeePayrollService employeePayrollService;
        employeePayrollService = new EmployeePayrollService(Arrays.asList(arrayOfEmployees));
        employeePayrollService.updateEmployeeSalary("Bill Gates", 3000000.00, EmployeePayrollService.IOService.REST_IO);
        EmployeePayrollData employeePayrollData = employeePayrollService.getEmployeePayrollData("Jeff Bezos");
        System.out.println("data : "+employeePayrollData);
        String empJson = new Gson().toJson(employeePayrollData);
        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/json");
        request.body(empJson);
        Response response = request.put("/employee_payroll/"+employeePayrollData.id);
        int statusCode = response.getStatusCode();
        Assertions.assertEquals(200, statusCode);
    }


    @Test
    public void givenEmployeeToDelete_WhenDeleted_ShouldMatch200ResponseAndCount() {
        EmployeePayrollData[] arrayOfEmployees = getEmployeeList();
        EmployeePayrollService employeePayrollService;
        employeePayrollService = new EmployeePayrollService(Arrays.asList(arrayOfEmployees));

        EmployeePayrollData employeePayrollData = employeePayrollService.getEmployeePayrollData("Anil");
        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/json");
        Response response = request.delete("/employee_payroll/"+employeePayrollData.id);
        int statusCode = response.getStatusCode();
        Assertions.assertEquals(200, statusCode);

        employeePayrollService.deleteEmployeePayroll(employeePayrollData.name, EmployeePayrollService.IOService.REST_IO);
        long entries = employeePayrollService.countEntries(EmployeePayrollService.IOService.REST_IO);
        Assertions.assertEquals(5, entries);
    }
}
