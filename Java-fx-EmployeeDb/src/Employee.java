
/**
 * 
 * @author ayamp
 *
 */
public abstract class Employee {
	protected String loginName;
	protected double baseSalary;
	protected String employeeName;
	protected String addedDate;
	protected int employeeId;
	protected int employeeType;
	protected static int nextId = 0;
	
	public Employee() {
		this.loginName = "";
		this.baseSalary = 0;
		this.employeeName = "";
		this.addedDate = "";
		this.employeeId = 0;	
		this.employeeType = 1;
	}
	
	/**
	 * 
	 * @param login login username of employee
	 * @param salary employee salary
	 * @param name employee name
	 */
	public Employee(String login,double salary,String name,String dateAdded,int employeeType) {
		this.loginName = login;
		this.baseSalary = salary;
		this.employeeName = name;	
		this.addedDate = dateAdded;
		this.employeeId = Employee.nextId++;
		this.employeeType = employeeType;
	}
	
	/**
	 * 
	 * @param eId employeeId to be added
	 * @param login login username of emplpoyee
	 * @param salary employee salary
	 * @param name employee name
	 * @param dateAdded date when employee was added
	 */
	
	public Employee(int eId, String login,double salary,String name, String dateAdded,int employeeType) {
		this.loginName = login;
		this.baseSalary = salary;
		this.employeeName = name;	
		this.addedDate = dateAdded;
		this.employeeId = eId;
		this.employeeType = employeeType;
		Employee.nextId = eId + 1;
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
	 * @param name name to set for this employee	 */
	
	public void setName(String name) {
		this.employeeName = name;
	}
	
	
	/**
	 * 
	 * @return returns the base salary of employee
	 */
	
	public double getSalary() {
		return this.baseSalary;
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
		String testData =  String.format("%05d\t%d\t%s\t%.2f\t%s\t%s\n", this.employeeId,this.employeeType, this.loginName, this.baseSalary, this.addedDate, this.employeeName);
		return testData;
	}
	
	public abstract double getPay();
}

