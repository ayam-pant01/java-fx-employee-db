package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.text.SimpleDateFormat;

/**
 * 
 * @author ayamp
 * @version 03/11/2023 This class creates a file employee data that includes the
 *          boss information on startup and then allow user to add employees as
 *          they go
 */
public class Payroll {

	private static ArrayList<Employee> employeeList;
	private static Employee currentUser;
	private static Scanner inputSc = new Scanner(System.in);
	public static final String EMOLOYEE_LIST = "employeeList.txt";
	public static final String EMOLOYEE_LIST_OBJECT_FILE = "employeeListObjFile.txt";
	public static final String TERMINATED_EMPLOYEE_LIST = "terminatedEmployeeList.txt";
	public static final String PAYROLL_FILE = "payroll.txt";
	private static ArrayList<Employee> terminatedEmployeeList;

	/**
	 * Payroll constructor when the first this program is run, it will create a new
	 * employee boss as the file do not exist
	 */
	public Payroll() {
		employeeList = new ArrayList<>();
		terminatedEmployeeList = new ArrayList<>();
		currentUser = null;
		try {
			Scanner fileSc = new Scanner(new File(EMOLOYEE_LIST));
			while (fileSc.hasNextLine()) {
				String line = fileSc.nextLine();
				String[] parts = line.split("\t");
				int id = Integer.parseInt(parts[0]);
				int type = Integer.parseInt(parts[1]);
				String loginName = parts[2];
				double salary = Double.parseDouble(parts[3]);
				String currDate = parts[4];
				String name = parts[5];
				Employee newEmployee;
				if (type == 1) {
					newEmployee = new Salaried(id, loginName, salary, name, currDate, type);
				} else {
					newEmployee = new Hourly(id, loginName, name, currDate, salary, type);
				}
				employeeList.add(newEmployee);
			}
			fileSc.close();

		} catch (FileNotFoundException e) {
			System.out.println(
					"Employee list file not found. \n Please enter information to add information of the Boss. \n");
			createBoss();
			saveChanges();
		} finally {

			try {
				Scanner fileScTerminated = new Scanner(new File(TERMINATED_EMPLOYEE_LIST));
				while (fileScTerminated.hasNextLine()) {
					String line = fileScTerminated.nextLine();
					String[] parts = line.split("\t");
					int id = Integer.parseInt(parts[0]);
					int type = Integer.parseInt(parts[1]);
					String loginName = parts[2];
					double salary = Double.parseDouble(parts[3]);
					String currDate = parts[4];
					String name = parts[5];
					Employee terminatedEmp;
					if (type == 1) {
						terminatedEmp = new Salaried(id, loginName, salary, name, currDate, type);
					} else {
						terminatedEmp = new Hourly(id, loginName, name, currDate, salary, type);
					}
					terminatedEmployeeList.add(terminatedEmp);
				}
				fileScTerminated.close();

			} catch (FileNotFoundException e) {
				System.out.println("Terminated employee list not found!");
			}

		}

	}

	public static void saveChanges() {
		Payroll.writeEmployeeListtoFile(employeeList, EMOLOYEE_LIST);
		Payroll.writeEmployeeListtoFile(terminatedEmployeeList, TERMINATED_EMPLOYEE_LIST);
	}

	private static void createBoss() {
		System.out.println("Please enter information to employee. \n");
		String loginName = "ayamp";
		String name = "Ayam Pant";
		Date curDate = new Date();
		int empTypeChoice = 1;
		Employee newEmployee = new Salaried(loginName, 50000.00, name, convertDateToString(curDate), empTypeChoice);
		employeeList.add(newEmployee);
		currentUser = newEmployee;
	}

	/**
	 * method to pay employee
	 */

	public static void payEmployees() {
		try (PrintWriter writer = new PrintWriter(PAYROLL_FILE)) {
			writer.println("Payroll Report - " + convertDateToString(new Date()));
			writer.printf("%-10s %-20s %10s\n", "ID", "Name", "Pay");
			double totalPay = 0;
			for (Employee employee : employeeList) {
				double pay = employee.getPay();
				writer.printf("%-10d %-20s %10.2f\n", employee.getId(), employee.getName(), pay);
				totalPay += pay;
			}
			writer.printf("Total Pay: $%.2f\n", totalPay);
		} catch (FileNotFoundException e) {
			System.out.println("Error writing to file: " + e.getMessage());
		}
		// displayMenu();
	}

	/**
	 * 
	 * @param listOfEmployee uses the list to write to a file
	 */
	private static void writeEmployeeListtoFile(ArrayList<Employee> listOfEmployee, String filename) {
		try {
			PrintWriter pw = new PrintWriter(new File(filename));
			for (Employee employee : listOfEmployee) {
				pw.print(employee.toString());
			}
			pw.close();
		} catch (FileNotFoundException e) {
			System.out.println("File not found.");
		}
	}

	/**
	 * 
	 * @param date date to be converted
	 * @return string value of converted date to mm dd yyyy format
	 */
	public static String convertDateToString(Date date) {
		SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");
		String formattedDate = formatter.format(date);
		return formattedDate;
	}

	public ArrayList<Employee> getEmployeeList() {
		return employeeList;
	}
	
	public ArrayList<Employee> getTerminatedEmployeeList() {
		return terminatedEmployeeList;
	}


	public Employee getCurrentUser() {
		return currentUser;
	}

	public void addEmployee(Employee emp) {
		if (emp != null) {
			employeeList.add(emp);
			saveChanges();
		}
	}

	public void removeEmployee(Employee emp) {
		if (emp != null) {
			employeeList.remove(emp);
			saveChanges();
		}
	}

	public void addTerminatedEmployee(Employee emp) {
		if (emp != null) {
			terminatedEmployeeList.add(emp);
			saveChanges();
		}
	}

	public static Employee findEmployeeById(int id) {
		for (Employee e : employeeList) {
			if (e.getId() == id) {
				return e;
			}
		}
		return null;
	}

}
