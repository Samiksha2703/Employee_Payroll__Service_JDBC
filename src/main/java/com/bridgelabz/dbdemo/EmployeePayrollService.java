package com.bridgelabz.dbdemo;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class EmployeePayrollService {

    public enum IOService {DB_IO}

    private List<EmployeePayrollData> employeePayrollList;
    private final EmployeePayrollDBService employeePayrollDBService;

    public EmployeePayrollService() {
        employeePayrollDBService = EmployeePayrollDBService.getInstance();
    }

    public EmployeePayrollService(List<EmployeePayrollData> employeePayrollList) {
        this();
        this.employeePayrollList = employeePayrollList;
    }

    public List<EmployeePayrollData> readEmployeePayrollData(IOService ioService) {
        if (ioService.equals(IOService.DB_IO))
            this.employeePayrollList = employeePayrollDBService.readData();
        return this.employeePayrollList;
    }

    public boolean checkEmployeePayrollInSyncWithDB(String name) {
        List<EmployeePayrollData> employeePayrollDataList = employeePayrollDBService.getEmployeePayrollData(name);
        return employeePayrollDataList.get(0).equals(getEmployeePayrollData(name));
    }

    public void updateEmployeeSalary(String name, Double salary) {
        int result = employeePayrollDBService.updateEmployeeData(name, salary);
        if (result == 0) return;
        EmployeePayrollData employeePayrollData = this.getEmployeePayrollData(name);
        if (employeePayrollData != null)
            employeePayrollData.salary = salary;
    }

    public void updateEmployeeSalaryWithPreparedStatement(String name, Double salary) throws EmployeePayrollException {
        int result = employeePayrollDBService.updateEmployeeDataUsingPreparedStatement(name, salary);
        if (result == 0) return;
        EmployeePayrollData employeePayrollData = this.getEmployeePayrollData(name);
        if (employeePayrollData != null)
            employeePayrollData.salary = salary;
    }

    private EmployeePayrollData getEmployeePayrollData(String name) {
        return this.employeePayrollList.stream().filter(employeePayrollDataItem -> employeePayrollDataItem.name.equals(name)).findFirst().orElse(null);
    }

    public List<EmployeePayrollData> getEmployeeSalary(String name, Double salary) {
        List<EmployeePayrollData> employeePayrollData = employeePayrollDBService.getSalary(name, salary);
        return employeePayrollData;
    }

    public List<EmployeePayrollData> readEmployeePayrollDataForDateRange(LocalDate startDate, LocalDate endDate) {
        return employeePayrollDBService.getEmployeePayrollDataForDateRange(startDate, endDate);
    }

    public Map<String, Double> averageSalaryByGender() {
        return employeePayrollDBService.getAverageSalaryByGender();
    }
}
