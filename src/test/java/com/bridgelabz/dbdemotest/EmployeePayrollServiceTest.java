package com.bridgelabz.dbdemotest;

import com.bridgelabz.dbdemo.EmployeePayrollData;
import com.bridgelabz.dbdemo.EmployeePayrollService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class EmployeePayrollServiceTest {
    EmployeePayrollService employeePayrollService;
    List<EmployeePayrollData> employeePayrollList;

    @Test
    public void given3EmployeePayrollInDB_WhenRetrieved_ShouldMatchEmployeeCount(){
        employeePayrollService = new EmployeePayrollService();
        employeePayrollList = employeePayrollService.readEmployeePayrollData(EmployeePayrollService.IOService.DB_IO);
        Assertions.assertEquals(3, employeePayrollList.size());
    }

}
