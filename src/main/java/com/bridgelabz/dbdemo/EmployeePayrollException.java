package com.bridgelabz.dbdemo;

public class EmployeePayrollException extends Exception {
    public enum ExceptionType {
        WRONG_NAME, NO_DATA_FOUND
    }

    private ExceptionType exceptionType;
    private String message;

    public EmployeePayrollException(String message, ExceptionType exception) {
        this.exceptionType = exception;
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}
