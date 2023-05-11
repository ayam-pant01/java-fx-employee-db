package application;

import java.util.Scanner;

public class Hourly extends Employee {
    private double hourlyRate;
    private double hoursWorked;
    
    public void addHours(double hours) {
        this.hoursWorked += hours;
    }
    
    public Hourly(int id,String login,String name, String dateAdded,double hourlyRate,int employeeType) {
        super(id,login, hourlyRate, name, dateAdded,employeeType);
        this.hourlyRate = hourlyRate;
        this.hoursWorked = 0;
    }
    
    public Hourly(String login,String name, String dateAdded,double hourlyRate,int employeeType) {
    	super(login, hourlyRate, name, dateAdded,employeeType);
        this.hourlyRate = hourlyRate;
        this.hoursWorked = 0;
    }
    @Override
    public void setHours(double hours) {
        this.hoursWorked = hours;
    }
    
    public void resetHours() {
        this.hoursWorked = 0;
    }
    

//    @Override
//    public double getPay() {
//        double pay = this.hoursWorked * this.hourlyRate;
//        return pay;
//    }
    
    @Override
    public double getPay() {
//        Scanner input = new Scanner(System.in);
//        System.out.print("Enter the number of hours worked during this pay period: ");
        double pay = this.hoursWorked * this.hourlyRate;
        return pay;
    }
    
    public String toString() {
//        return "Hourly Employee: " + super.toString();
        return super.toString();
    }
}

