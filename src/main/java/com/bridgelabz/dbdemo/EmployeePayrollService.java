package com.bridgelabz.dbdemo;

import java.util.List;

public class EmployeePayrollService {
    public enum IOService {CONSOLE_IO, FILE_IO, DB_IO, REST_IO}

    private List<EmployeePayrollData> employeePayrollDataList;
    private EmployeePayrollService employeePayrollService;
    public EmployeePayrollService() {

    }

    public List<EmployeePayrollData> readEmployeePayrollData(IOService ioService){
        if(ioService.equals(IOService.DB_IO))
            this.employeePayrollDataList = new EmployeePayrollDBService().readData();
        return this.employeePayrollDataList;
    }
}
