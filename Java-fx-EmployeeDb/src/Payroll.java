import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
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
	private static String menu = "Payroll Menu\n\t" + "1. Log In" + "\n\t" + "2. Enter employees\n\t"
			+ "3. List Employees \n\t" + "4. Change Employee Data \n\t" + "5. Terminate an Employee\n\t"
			+ "6. Pay Employees\n\t" + "7. List Terminated Employees\n\t" + "8. Save Changes\n\t" + "0. Exit System";
	private static Employee currentUser;
	private static Scanner inputSc = new Scanner(System.in);
	public static final String EMOLOYEE_LIST = "employeeList.txt";
	public static final String EMOLOYEE_LIST_OBJECT_FILE = "employeeListObjFile.txt";
	public static final String TERMINATED_EMPLOYEE_LIST = "terminatedEmployeeList.txt";

//Part 2
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
			createEmployee(true);
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

	/**
	 * this method is used to display menu to the user This system doesn't not allow
	 * to do anything without logging in. So user has to login first before
	 * accessing menu options.
	 */
	public static void displayMenu() {
		if (currentUser == null) {
			System.out.println("Please login before using the system.\n");
			login();
		}
		try {
			int choice = -1;
			do {
				System.out.println(Payroll.menu);
				System.out.println("Choose from the above options.");
				try {
					choice = inputSc.nextInt();
					if (choice < 0 || choice > 8) {
						String junk = inputSc.nextLine();
						System.out.printf("Bad input.:  %s %s\n", choice, junk);
					}
				} catch (Exception e) {
					String junk = inputSc.nextLine();
					System.out.printf("Input needs to be a number:  %s\n", junk);
				}
			} while (choice < 0 || choice > 8);

			switch (choice) {
			case 0:
				exitSystem();
				break;
			case 1:
				login();
				break;
			case 2:
				createEmployee(false);
				break;
			case 3:
				listEmployees();
				break;
			case 4:
				changeEmployeeData();
				break;
			case 5:
				terminateEmployee();
				break;
			case 6:
				payEmployees();
				break;
			case 7:
				listTerminatedEmployees();
				break;
			case 8:
				saveChanges();
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void saveChanges() {
		Payroll.writeEmployeeListtoFile(employeeList, EMOLOYEE_LIST);
		Payroll.writeEmployeeListtoFile(terminatedEmployeeList, TERMINATED_EMPLOYEE_LIST);
//		saveEmployees(employeeList,EMOLOYEE_LIST_OBJECT_FILE);
		//writeEmployeeListToFileAsObject(employeeList,EMOLOYEE_LIST_OBJECT_FILE);
		System.out.print("Successfully Saved!\n");
		displayMenu();
	}
	
	private static void saveEmployees(ArrayList<Employee> listOfEmployee,String fileName) {
	    try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(fileName))) {
	    	if (objectOutputStream != null && listOfEmployee.size() > 0) {
                objectOutputStream.writeObject(listOfEmployee);
            }
	    } catch (IOException e) {
	        System.err.println("Error saving employees: " + e.getMessage());
	    }
	}
	
	private static void writeEmployeeListToFileAsObject(ArrayList<Employee> listOfEmployee, String filename) {
        try {
            FileOutputStream fileOut = new FileOutputStream(filename);
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(listOfEmployee);
            objectOut.close();
            System.out.println("Employee list written to file: " + filename);
        } catch (IOException e) {
            System.out.println("Error writing to file.");
        }
    }
	
//	  private void saveEmployees(ArrayList<Employee> listOfEmployee,String fileName) throws IOException {
//	        ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(fileName));
//	        try {
////	            ArrayList<Employee> employeesList;
//				if (objectOutputStream != null && listOfEmployee.size() > 0) {
//	                objectOutputStream.writeObject(listOfEmployee);
//	            }
//	        } finally {
//	            objectOutputStream.close();
//	        }
//
//	    }

	/**
	 * login method
	 */
	private static void login() {
		System.out.print("Enter your login name: ");
		String loginName = inputSc.next();
		boolean userFound = false;

		for (Employee employee : Payroll.employeeList) {
			if (employee.getLoginName().equals(loginName)) {
				currentUser = employee;
				System.out.println("Login Successfull!!!\n");
				System.out.println("Logged in as " + currentUser.getName() + ". \n");
				userFound = true;
			}
		}
		if (!userFound)
			System.out.println("User not found.\n");
		displayMenu();
	}

	/**
	 * Method to add new employeee
	 */
	private static void createEmployee(boolean isBoss) {
		System.out.println("Please enter information to employee. \n");
		String loginName = "";
		boolean isUnique = false;
		while (!isUnique) {
			loginName = promptForString("Enter Log on Name:");
			isUnique = true;
			for (Employee e : employeeList) {
				if (e.getLoginName().equals(loginName)) {
					System.out.println("Login name already exists. Please enter a different login name.");
					isUnique = false;
					break;
				}
			}
		}
		String name = promptForString("Enter employee name: ");
		Date curDate = new Date();
		int empTypeChoice = 0;
		do {

			System.out.println("Enter (1 or 2) type of employee from below:");
			System.out.println("1. " + "Salaried");
			System.out.println("2. " + "Hourly");
			try {
				empTypeChoice = inputSc.nextInt();
				if (empTypeChoice < 1 || empTypeChoice > 2) {
					String junk = inputSc.nextLine();
					System.out.printf("Bad input.:  %s %s\n", empTypeChoice, junk);
				}
			} catch (Exception e) {
				String junk = inputSc.nextLine();
				System.out.printf("Input needs to be a number:  %s\n", junk);
			}
		} while (empTypeChoice < 1 || empTypeChoice > 2);

		Employee newEmployee;
		if (empTypeChoice == 1) {
			double salary = promptForDouble("Enter employee salary: ");
			newEmployee = new Salaried(loginName, salary, name, convertDateToString(curDate), empTypeChoice);
		} else {
			double hourlyRate = promptForDouble("Enter Hourly Rate of employee:");
			newEmployee = new Hourly(loginName, name, convertDateToString(curDate), hourlyRate, empTypeChoice);
		}
		employeeList.add(newEmployee);
		System.out.print("New Employee " + name + " Added!\n");
		if (isBoss) {
			currentUser = newEmployee;
		} else {
			displayMenu();
		}
	}

	/**
	 * method to list employees
	 */
	private static void listEmployees() {
		if (currentUser.getId() == 0) {
			for (Employee employee : employeeList) {
				System.out.println(employee.toString());
			}
		} else {
			for (Employee employee : employeeList) {
				if (employee.getLoginName().equals(currentUser.getLoginName())) {
					System.out.println(employee.toString());
				}
			}
		}
		displayMenu();
	}

	/**
	 * method to list terminated employees
	 */
	private static void listTerminatedEmployees() {
		if (currentUser.getId() == 0) {
			for (Employee employee : terminatedEmployeeList) {
				System.out.println(employee.toString());
			}
		} else {
			for (Employee employee : terminatedEmployeeList) {
				if (employee.getLoginName().equals(currentUser.getLoginName())) {
					System.out.println(employee.toString());
				}
			}
		}
	}

	/**
	 * method to change employee data
	 */

	private static void changeEmployeeData() {
		if (currentUser.getId() == 0) { // only the boss can change employee data
			int id = promptForInt("Enter the ID number of the employee you want to change:");
			Employee employee = findEmployeeById(id);
			if (employee == null) {
				System.out.println("Employee not found.\n");
			} else {
				System.out.println("Enter the new data for " + employee.getName() + ":");
				String name = promptForString("Name (press Enter to keep current value):");
				if (!name.isEmpty()) {
					employee.setName(name);
				}
				double salary = promptForDouble("Salary (press Enter to keep current value):");
				if (salary > 0) {
					employee.setSalary(salary);
				}
				System.out.println("Employee data updated.");
				// displayMenu();
			}
		} else {
			System.out.println("You are not authorized to change employee data.");
		}
		displayMenu();
	}

	/**
	 * method to terminate employee
	 */
	private static void terminateEmployee() {
		int id = promptForInt("Enter employee ID:");
		Employee employee = findEmployeeById(id);
		boolean showMenu = true;
		if (employee == null) {
			System.out.println("Employee not found.");
		} else if (currentUser.getId() != 0 && currentUser.getId() != employee.getId()) {
			// Current user is not the boss and not the employee to be terminated
			System.out.println("You are not authorized to terminate this employee.");
		} else {
			System.out.println("Terminating employee " + employee.getName());
			employeeList.remove(employee);
			terminatedEmployeeList.add(employee);
			if (currentUser == employee) {
				System.out.println("Currently logged in user just quit. Please Re-login to continue.");
				login();
			}
		}
		if (showMenu)
			displayMenu();
	}

	/**
	 * method to pay employee
	 */

	private static void payEmployees() {
		try (PrintWriter writer = new PrintWriter("payroll.txt")) {
			writer.println("Payroll Report - " + convertDateToString(new Date()));
			System.out.println("Payroll Report - " + convertDateToString(new Date()));
			System.out.printf("%-10s %-20s %10s\n", "ID", "Name", "Pay");
			writer.printf("%-10s %-20s %10s\n", "ID", "Name", "Pay");
			double totalPay = 0;
			for (Employee employee : employeeList) {
				double pay = employee.getPay();
				System.out.printf("%-10d %-20s %10.2f\n", employee.getId(), employee.getName(), pay);
				writer.printf("%-10d %-20s %10.2f\n", employee.getId(), employee.getName(), pay);
				totalPay += pay;
			}
			System.out.printf("Total Pay: $%.2f\n", totalPay);
			writer.printf("Total Pay: $%.2f\n", totalPay);
		} catch (FileNotFoundException e) {
			System.out.println("Error writing to file: " + e.getMessage());
		}
		displayMenu();
	}

	/**
	 * method to exit system
	 */
	private static void exitSystem() {
		Payroll.writeEmployeeListtoFile(employeeList, EMOLOYEE_LIST);
		System.out.print("Exiting program!");
		System.exit(0);
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
	private static String convertDateToString(Date date) {
		SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");
		String formattedDate = formatter.format(date);
		return formattedDate;
	}

//	public static Employee findEmployeeById(int id, ArrayList<Employee> employees) {
	public static Employee findEmployeeById(int id) {
		for (Employee e : employeeList) {
			if (e.getId() == id) {
				return e;
			}
		}
		return null;
	}

	public static int promptForInt(String message) {
		Scanner scanner = new Scanner(System.in);
		int input;
		while (true) {
			System.out.print(message);
			try {
				input = Integer.parseInt(scanner.nextLine());
				break;
			} catch (NumberFormatException e) {
				System.out.println("Invalid input. Please enter an integer.");
			}
		}
//		scanner.close();
		return input;
	}

	public static String promptForString(String message) {
		Scanner scanner = new Scanner(System.in);
		System.out.print(message + " ");
//		scanner.close();
		return scanner.nextLine();
	}

	public static double promptForDouble(String prompt) {
		Scanner scanner = new Scanner(System.in);
		double value = 0.0;
		boolean isValid = false;

		do {
			System.out.print(prompt);
			if (scanner.hasNextDouble()) {
				value = scanner.nextDouble();
				isValid = true;
			} else {
				System.out.println("Invalid input. Please enter a valid number.");
				scanner.next(); // clear the invalid input from the scanner
			}
		} while (!isValid);

//		scanner.close();
		return value;
	}

}
