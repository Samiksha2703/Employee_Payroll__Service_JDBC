package com.bridgelabz.dbdemo;

import java.time.LocalDate;
import java.util.ArrayList;
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

    public void addEmployeeAndPayrollData(String name, Double salary, LocalDate startDate, String gender, ArrayList<String> department) {
        employeePayrollList.add(
                employeePayrollDBService.addEmployeePayroll(name, salary, startDate, gender, department));
    }

    public void removeEmployee(int empId) {
        employeePayrollDBService.removeEmployeeFromDB(empId);
    }


    public void addEmployeeAndPayroll(List<EmployeePayrollData> employeePayrollDataList) {
        employeePayrollDataList.forEach(employeePayrollData -> {
            this.addEmployeeAndPayrollData(employeePayrollData.name, employeePayrollData.salary,
                    employeePayrollData.startDate, employeePayrollData.gender);
        });
    }

    private void addEmployeeAndPayrollData(String name, double salary, LocalDate startDate, String gender) {
        employeePayrollList.add(employeePayrollDBService.addEmployeePayrollIntoDB(name, salary, startDate, gender));
    }

    public int countEntries() {
        return employeePayrollList.size();
    }
}
