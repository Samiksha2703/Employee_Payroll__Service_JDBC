package com.bridgelabz.dbdemotest;

import com.bridgelabz.dbdemo.EmployeePayrollData;
import com.bridgelabz.dbdemo.EmployeePayrollException;
import com.bridgelabz.dbdemo.EmployeePayrollService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class EmployeePayrollServiceTest {
    EmployeePayrollService employeePayrollService;
    List<EmployeePayrollData> employeePayrollList;

    @Test
    public void given3EmployeePayrollInDB_WhenRetrieved_ShouldMatchEmployeeCount() {
        employeePayrollService = new EmployeePayrollService();
        employeePayrollList = employeePayrollService.readEmployeePayrollData(EmployeePayrollService.IOService.DB_IO);
        Assertions.assertEquals(7, employeePayrollList.size());
    }

    @Test
    public void givenNewSalaryForEmployee_WhenUpdated_ShouldMatch() {
        employeePayrollService = new EmployeePayrollService();
        employeePayrollList = employeePayrollService.readEmployeePayrollData(EmployeePayrollService.IOService.DB_IO);
        employeePayrollService.updateEmployeeSalary("Terisa", 3000000.0);
        boolean result = employeePayrollService.checkEmployeePayrollInSyncWithDB("Terisa");
        Assertions.assertTrue(result);
    }

    @Test
    public void givenNewSalaryForEmployee_WhenUpdated_ShouldSyncWithDB() throws EmployeePayrollException {
        employeePayrollService = new EmployeePayrollService();
        employeePayrollList = employeePayrollService.readEmployeePayrollData(EmployeePayrollService.IOService.DB_IO);
        boolean result;
        try {
            employeePayrollService.updateEmployeeSalaryWithPreparedStatement("Terisa", 3000000.0);
            result = employeePayrollService.checkEmployeePayrollInSyncWithDB("Terisa");
        } catch (EmployeePayrollException e) {
            throw new EmployeePayrollException("Wrong given Name", EmployeePayrollException.ExceptionType.WRONG_NAME);
        }
        Assertions.assertTrue(result);
    }

    @Test
    public void givenEmployeeName_WhenRetrieveSalary_ShouldMatch() {
        employeePayrollService = new EmployeePayrollService();
        employeePayrollList = employeePayrollService.readEmployeePayrollData(EmployeePayrollService.IOService.DB_IO);
        List<EmployeePayrollData> employeePayrollData = employeePayrollService.getEmployeeSalary("Bill", 1000000.0);
        Assertions.assertEquals(1000000.0, employeePayrollData.get(0).salary);
    }

    @Test
    public void givenDateRange_WhenRetrievedEmployee_ShouldReturnEmpCount() throws EmployeePayrollException {
        employeePayrollService = new EmployeePayrollService();
        employeePayrollList = employeePayrollService.readEmployeePayrollData(EmployeePayrollService.IOService.DB_IO);
        LocalDate startDate = LocalDate.of(2018, 01, 01);
        LocalDate endDate = LocalDate.now();
        List<EmployeePayrollData> employeePayrollData = employeePayrollService.readEmployeePayrollDataForDateRange(startDate, endDate);
        Assertions.assertEquals(7, employeePayrollList.size());
    }

    @Test
    public void givenPayrollData_WhenAverageSalaryRetrieveByGender_ShouldReturnProperValue() {
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        employeePayrollService.readEmployeePayrollData(EmployeePayrollService.IOService.DB_IO);
        Map<String, Double> averageSalaryByGender = employeePayrollService.averageSalaryByGender();
        Assertions.assertTrue(averageSalaryByGender.get("M").equals(2400000.0) && averageSalaryByGender.get("F").equals(3000000.0));
    }

    @Test
    public void givenNewEmployee_WhenAddedUsingER_ShouldSyncWithDB() {
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        employeePayrollService.readEmployeePayrollData(EmployeePayrollService.IOService.DB_IO);
        ArrayList<String> depts = new ArrayList<>();
        depts.add("Sales");
        depts.add("Marketing");
        employeePayrollService.addEmployeeAndPayrollData("Mark", 200000.00, LocalDate.now(), "M", depts);
        boolean isSynced = employeePayrollService.checkEmployeePayrollInSyncWithDB("Mark");
        Assertions.assertTrue(isSynced);
    }

    @Test
    public void givenEmployeeId_WhenDeletedUsing_ShouldSyncWithDB() {
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        employeePayrollList = employeePayrollService.readEmployeePayrollData(EmployeePayrollService.IOService.DB_IO);
        employeePayrollService.removeEmployee(3);
        Assertions.assertEquals(7, employeePayrollList.size());
    }

    @Test
    public void given6Employees_WhenAdded_Should_ShouldMatchEmpEntries() {
        EmployeePayrollData[] arrayOfEmps = {
                new EmployeePayrollData(0, "Jeff Bezos", "M", 100000.0, LocalDate.now()),
                new EmployeePayrollData(0, "Bill Gates", "M", 200000.0, LocalDate.now()),
                new EmployeePayrollData(0, "Mark Zuckerberg", "M", 300000.0, LocalDate.now()),
                new EmployeePayrollData(0, "Sunder", "M", 600000.0, LocalDate.now()),
                new EmployeePayrollData(0, "Mukesh", "M", 1000000.0, LocalDate.now()),
                new EmployeePayrollData(0, "Anil", "M", 200000.0, LocalDate.now()),
        };
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        employeePayrollService.readEmployeePayrollData(EmployeePayrollService.IOService.DB_IO);
        Instant start = Instant.now();
        employeePayrollService.addEmployeeAndPayroll(Arrays.asList(arrayOfEmps));
        Instant end = Instant.now();
        System.out.println("Duration without Thread: " + Duration.between(start, end));
    }
}
