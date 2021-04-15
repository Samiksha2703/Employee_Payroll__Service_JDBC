package com.bridgelabz.dbdemo;

import java.time.LocalDate;
import java.util.Objects;

public class EmployeePayrollData {
    public String gender = "";
    public int id;
    public static Double salary;
    public String name;
    public LocalDate startDate;

    public EmployeePayrollData(Integer id, String name, Double salary) {
        this.id = id;
        this.name = name;
        this.salary = salary;
        this.startDate = null;
    }

    public EmployeePayrollData(Integer id, String name, Double salary, LocalDate startDate) {
        this(id, name, salary);
        this.startDate = startDate;
    }

    public EmployeePayrollData(Integer id, String name, String gender, Double salary, LocalDate startDate) {
        this.id = id;
        this.name = name;
        this.salary = salary;
        this.startDate = startDate;
        this.gender = gender;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, gender, salary, startDate);
    }

    @Override
    public String toString() {
        return '{'+"id=" + id +
                ", salary=" + salary +
                ", name='" + name + '\'' +
                ", startDate=" + startDate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmployeePayrollData that = (EmployeePayrollData) o;
        return id == that.id && Double.compare(that.salary, salary) == 0 && Objects.equals(name, that.name);
    }
}
