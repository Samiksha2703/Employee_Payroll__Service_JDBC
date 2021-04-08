package com.bridgelabz.dbdemotest;

import com.bridgelabz.dbdemo.EmployeePayrollData;
import com.bridgelabz.dbdemo.EmployeePayrollException;
import com.bridgelabz.dbdemo.EmployeePayrollService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

public class EmployeePayrollServiceTest {
    EmployeePayrollService employeePayrollService;
    List<EmployeePayrollData> employeePayrollList;

    @Test
    public void given3EmployeePayrollInDB_WhenRetrieved_ShouldMatchEmployeeCount() {
        employeePayrollService = new EmployeePayrollService();
        employeePayrollList = employeePayrollService.readEmployeePayrollData(EmployeePayrollService.IOService.DB_IO);
        Assertions.assertEquals(3, employeePayrollList.size());
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
    public void givenNewSalaryForEmployee_WhenUpdated_ShouldSyncWithDB() {
        employeePayrollService = new EmployeePayrollService();
        employeePayrollList = employeePayrollService.readEmployeePayrollData(EmployeePayrollService.IOService.DB_IO);
        try {
            employeePayrollService.updateEmployeeSalaryWithPreparedStatement("Terisa", 3000000.0);
        } catch (EmployeePayrollException e) {
            e.printStackTrace();
        }
        boolean result = employeePayrollService.checkEmployeePayrollInSyncWithDB("Terisa");
        Assertions.assertTrue(result);
    }
}
