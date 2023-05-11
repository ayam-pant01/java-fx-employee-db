package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
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
			    byte[] salt = Base64.getDecoder().decode(parts[6]); // read in salt from file
			    byte[] hash = Base64.getDecoder().decode(parts[7]); // read in hash from file
				Employee newEmployee;
				if (type == 1) {
					newEmployee = new Salaried(id, loginName, salary, name, currDate, type,salt,hash);
				} else {
					newEmployee = new Hourly(id, loginName, name, currDate, salary, type,salt,hash);
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
						terminatedEmp = new Salaried(id, loginName, salary, name, currDate, type,null,null);
					} else {
						terminatedEmp = new Hourly(id, loginName, name, currDate, salary, type,null,null);
					}
					terminatedEmployeeList.add(terminatedEmp);
				}
				fileScTerminated.close();

			} catch (FileNotFoundException e) {
				System.out.println("Terminated employee list not found!");
			}

		}

	}

	public static Employee login(String loginName, String password) {
	    for (Employee employee : employeeList) {
	        if (employee.getLoginName().equals(loginName)) {
	            byte[] storedHash = employee.getPasswordHash();
	            byte[] salt = employee.getPasswordSalt();
	            try {
	                byte[] enteredHash = computeHash(password, salt);
	                if (Arrays.equals(enteredHash, storedHash)) {
	                    currentUser = employee;
	                    return currentUser;
	                } else {
	                    System.out.println("Incorrect password.");
	                    return null;
	                }
	            } catch (NoSuchAlgorithmException e) {
	                System.out.println("Error: " + e.getMessage());
	                return null;
	            }
	        }
	    }
	    System.out.println("User not found.");
	    return null;
	}
	
	public static void saveChanges() {
		Payroll.writeEmployeeListtoFile(employeeList, EMOLOYEE_LIST);
		Payroll.writeEmployeeListtoFile(terminatedEmployeeList, TERMINATED_EMPLOYEE_LIST);
	}

	private static void createBoss() {
		try {
			System.out.println("Please enter information to employee. \n");
			String loginName = "ayamp";
			String name = "Ayam Pant";
			Date curDate = new Date();
			int empTypeChoice = 1;
			byte[] randomSalt = getRandomSalt();
			byte[] hash = computeHash("ayamp", randomSalt);
			Employee newEmployee = new Salaried(loginName, 50000.00, name, convertDateToString(curDate), empTypeChoice,randomSalt,hash);
			employeeList.add(newEmployee);
			currentUser = newEmployee;
		} catch (NoSuchAlgorithmException e) {
			System.out.println("Failed to create boss due to hashing error: " + e.getMessage());
		}
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

	public static byte[] computeHash(String password, byte[] salt) throws NoSuchAlgorithmException {
		byte[] passwordBytes = password.getBytes();
		byte[] saltedPasswordBytes = new byte[salt.length + passwordBytes.length];
		System.arraycopy(salt, 0, saltedPasswordBytes, 0, salt.length);
		System.arraycopy(passwordBytes, 0, saltedPasswordBytes, salt.length, passwordBytes.length);

		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		digest.update(saltedPasswordBytes);
		byte[] hash = digest.digest();

		byte[] saltedHash = new byte[salt.length + hash.length];
		System.arraycopy(salt, 0, saltedHash, 0, salt.length);
		System.arraycopy(hash, 0, saltedHash, salt.length, hash.length);

		return saltedHash;
	}

	public static byte[] getRandomSalt() {
		SecureRandom randomSalt = new SecureRandom();
		byte[] salt = new byte[16];
		randomSalt.nextBytes(salt);
		return salt;
	}

}
