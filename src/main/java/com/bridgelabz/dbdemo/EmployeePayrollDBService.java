package com.bridgelabz.dbdemo;

import jdk.nashorn.internal.ir.WhileNode;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmployeePayrollDBService {

    private static EmployeePayrollDBService employeePayrollDBService;
    private PreparedStatement employeePayrollDataStatement;
    private PreparedStatement prepareStatement;
    private PreparedStatement updateEmployeeSalary;
    private PreparedStatement employeeSalary;

    private EmployeePayrollDBService() {

    }

    public static EmployeePayrollDBService getInstance() {
        if (employeePayrollDBService == null)
            employeePayrollDBService = new EmployeePayrollDBService();
        return employeePayrollDBService;
    }

    private Connection getConnection() throws SQLException {
        String jdbcURL = "jdbc:mysql://localhost:3306/payroll_service1?useSSL=false";
        String userName = "root";
        String password = "Welcome@01";
        Connection connection;
        System.out.println("Connecting to database:" + jdbcURL);
        connection = DriverManager.getConnection(jdbcURL, userName, password);
        System.out.println("Connection is successful!" + connection);
        return connection;
    }

    public List<EmployeePayrollData> readData() {
        String sql = "SELECT * FROM employee_payroll; ";
        return this.getEmployeePayrollDataUsingDB(sql);
    }

    public int updateEmployeeData(String name, Double salary) {
        return this.updateEmployeeDataUsingStatement(name, salary);
    }

    private int updateEmployeeDataUsingStatement(String name, Double salary) {
        String sql = String.format("update employee_payroll set salary = %.2f where name = '%s';", salary, name);
        try (Connection connection = this.getConnection()) {
            Statement statement = connection.createStatement();
            return statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int updateEmployeeDataUsingPreparedStatement(String name, Double salary) throws EmployeePayrollException {
        String sql = "update employee_payroll set salary = ? where name = ?";
        if (this.updateEmployeeSalary == null)
            updateEmployeeSalary = this.prepareStatementForEmployeeData(sql);
        try {
            updateEmployeeSalary.setString(2, name);
            updateEmployeeSalary.setDouble(1, salary);
            return updateEmployeeSalary.executeUpdate();
        } catch (SQLException e) {
            throw new EmployeePayrollException("Wrong given Name", EmployeePayrollException.ExceptionType.WRONG_NAME);
        }
    }

    public List<EmployeePayrollData> getEmployeePayrollData(String name) {
        List<EmployeePayrollData> employeePayrollList = null;
        String sql = "SELECT * FROM employee_payroll WHERE name = ?";
        if (this.employeePayrollDataStatement == null)
            employeePayrollDataStatement = this.prepareStatementForEmployeeData(sql);
        try {
            employeePayrollDataStatement.setString(1, name);
            ResultSet resultSet = employeePayrollDataStatement.executeQuery();
            employeePayrollList = this.getEmployeePayrollData(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employeePayrollList;
    }

    public List<EmployeePayrollData> getSalary(String name, Double salary) {
        List<EmployeePayrollData> employeePayrollList = null;
        String sql = "SELECT * FROM employee_payroll WHERE name = ? AND salary = ?";
        if (this.employeeSalary == null)
            employeeSalary = this.prepareStatementForEmployeeData(sql);
        try {
            employeeSalary.setString(1, name);
            employeeSalary.setDouble(2, salary);
            ResultSet resultSet = employeeSalary.executeQuery();
            employeePayrollList = this.getEmployeePayrollData(resultSet);
            return employeePayrollList;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<EmployeePayrollData> getEmployeePayrollData(ResultSet resultSet) {
        List<EmployeePayrollData> employeePayrollList = new ArrayList<>();
        try {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                double salary = resultSet.getDouble("salary");
                LocalDate startDate = resultSet.getDate("start").toLocalDate();
                employeePayrollList.add(new EmployeePayrollData(id, name, salary, startDate));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employeePayrollList;
    }

    private PreparedStatement prepareStatementForEmployeeData(String sql) {
        try {
            Connection connection = this.getConnection();
            prepareStatement = connection.prepareStatement(sql);
            return prepareStatement;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<EmployeePayrollData> getEmployeePayrollDataForDateRange(LocalDate startDate, LocalDate endDate) {
        String sql = String.format("SELECT * FROM employee_payroll WHERE START BETWEEN '%s' AND '%s';", Date.valueOf(startDate), Date.valueOf(endDate));
        return this.getEmployeePayrollDataUsingDB(sql);
    }

    private List<EmployeePayrollData> getEmployeePayrollDataUsingDB(String sql) {
        List<EmployeePayrollData> employeePayrollList = new ArrayList<>();
        try (Connection connection = this.getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            employeePayrollList = this.getEmployeePayrollData(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employeePayrollList;
    }

    public Map<String, Double> getAverageSalaryByGender() {
        String sql = "SELECT gender, AVG(salary) as avg_salary FROM employee_payroll GROUP BY gender;";
        Map<String, Double> genderAverageSalaryMap = new HashMap<>();
        try (Connection connection = this.getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                String gender = resultSet.getString("gender");
                double salary = resultSet.getDouble("avg_salary");
                genderAverageSalaryMap.put(gender, salary);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return genderAverageSalaryMap;
    }

    public EmployeePayrollData addEmployeePayrollUC7(String name, double salary, LocalDate start, String gender) {
        int employeeID = -1;
        EmployeePayrollData employeePayrollData = null;
        String sql = String.format("INSERT INTO employee_payroll (name, gender, salary, start)" +
                "VALUES ('%s', '%s', '%s', '%s')", name, gender, salary, Date.valueOf(start));
        try (Connection connection = this.getConnection()) {
            Statement statement = connection.createStatement();
            int rowAffected = statement.executeUpdate(sql, statement.RETURN_GENERATED_KEYS);
            if (rowAffected == 1) {
                ResultSet resultSet = statement.getGeneratedKeys();
                if (resultSet.next()) employeeID = resultSet.getInt(1);
            }
            employeePayrollData = new EmployeePayrollData(employeeID, name, salary, start);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employeePayrollData;
    }

    public EmployeePayrollData addEmployeePayroll(String name, double salary, LocalDate start, String gender) {
        int employeeID = -1;
        Connection connection = null;
        EmployeePayrollData employeePayrollData = null;
        try {
            connection = this.getConnection();
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try (Statement statement = connection.createStatement()) {
            String sql = String.format("INSERT INTO employee_payroll (name, gender, salary, start)" +
                    "VALUES ('%s', '%s', '%s', '%s')", name, gender, salary, Date.valueOf(start));
            int rowAffected = statement.executeUpdate(sql, statement.RETURN_GENERATED_KEYS);
            if (rowAffected == 1) {
                ResultSet resultSet = statement.getGeneratedKeys();
                if (resultSet.next()) employeeID = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                connection.rollback();
                return employeePayrollData;
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        try (Statement statement = connection.createStatement()) {
            double deduction = salary * 0.2;
            double taxablePay = salary - deduction;
            double tax = taxablePay * 0.1;
            double netPay = salary - tax;
            String sql = String.format("INSERT INTO payroll_details" +
                    "(employee_id, basic_pay, deduction, taxable_pay, taxa, net_pay) VALUES" +
                    "(%s, %s, %s, %s, %s, %s)", employeeID, salary, deduction, taxablePay, tax, netPay);
            int rowAffected = statement.executeUpdate(sql);
            if (rowAffected == 1) {
                employeePayrollData = new EmployeePayrollData(employeeID, name, salary, start);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                connection.rollback();
                return employeePayrollData;
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        try {
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }
        return employeePayrollData;
    }
}