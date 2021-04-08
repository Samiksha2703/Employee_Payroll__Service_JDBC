package com.bridgelabz.dbdemo;

public class EmployeePayrollException extends Exception {
public enum ExceptionType{
    WRONG_NAME
}
    private ExceptionType exceptionType;
    private String message;

    public EmployeePayrollException(String message,ExceptionType exception) {
        this.exceptionType=exception;
        this.message=message;
    }

    public ExceptionType getExceptionType(){
        return this.exceptionType;
    }

    public String getMessage(){
        return this.message;
    }
}
