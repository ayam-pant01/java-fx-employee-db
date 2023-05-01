
/**
 * 
 * @author ayamp
 *
 */
public class EmployeeOld {
	private String loginName;
	private double baseSalary;
	private String employeeName;
	private String addedDate;
	private int employeeId;
	private static int nextId = 0;
	
	public EmployeeOld() {
		this.loginName = "";
		this.baseSalary = 0;
		this.employeeName = "";
		this.addedDate = "";
		this.employeeId = 0;		
	}
	
	/**
	 * 
	 * @param login login username of employee
	 * @param salary employee salary
	 * @param name employee name
	 */
	public EmployeeOld(String login,double salary,String name,String dateAdded) {
		this.loginName = login;
		this.baseSalary = salary;
		this.employeeName = name;	
		this.addedDate = dateAdded;
		this.employeeId = EmployeeOld.nextId++;
	}
	
	/**
	 * 
	 * @param eId employeeId to be added
	 * @param login login username of emplpoyee
	 * @param salary employee salary
	 * @param name employee name
	 * @param dateAdded date when employee was added
	 */
	
	public EmployeeOld(int eId, String login,double salary,String name, String dateAdded) {
		this.loginName = login;
		this.baseSalary = salary;
		this.employeeName = name;	
		this.addedDate = dateAdded;
		this.employeeId = eId;
		EmployeeOld.nextId = eId + 1;
	}
	
	/**
	 * 
	 * @param salary salary to set for the employee
	 */
	
	public void setSalary(double salary) {
		this.baseSalary = salary;
	}
	
	/**
	 * 
	 * @return login name of employee
	 */
	
	public String getLoginName() {
		return this.loginName;
	}
	
	/**
	 * 
	 * @return Name of employee
	 */
	public String getName() {
		return this.employeeName;
	}
	
	/**
	 * 
	 * @return employee id
	 */
	
	public int getId() {
		return this.employeeId;
	}
	
	/**
	 * l Returns string representation of the object
	 * 
	 * @return a string representation of employees attribute
	 */
	@Override
	public String toString() {
		String testData =  String.format("%05d\t%s\t%.2f\t%s\t%s\n", this.employeeId, this.loginName, this.baseSalary, this.addedDate, this.employeeName);
		return testData;
	}
}

