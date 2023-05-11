package application;

/**
 * 
 * @author ayamp
 *
 */
public class Hourly extends Employee {
	private double hourlyRate;
	private double hoursWorked;
	
	/**
	 * Constructor with password and hash
	 * @param id           existing user id
	 * @param login        existing user login name
	 * @param name         existing user name
	 * @param dateAdded    existing user date added
	 * @param hourlyRate   existing user hourly rate
	 * @param employeeType existing user type
	 */
	public Hourly(int id, String login, String name, String dateAdded, double hourlyRate, int employeeType, byte[] salt,
			byte[] hash) {
		super(id, login, hourlyRate, name, dateAdded, employeeType,salt,hash);
		this.hourlyRate = hourlyRate;
		this.hoursWorked = 0;
	}

	/**
	 * constructor with password hash
	 * 
	 * @param login        new user login
	 * @param name         new user name
	 * @param dateAdded    new user date added
	 * @param hourlyRate   new user hourly rate
	 * @param employeeType new user type
	 */
	public Hourly(String login, String name, String dateAdded, double hourlyRate, int employeeType, byte[] salt,
			byte[] hash) {
		super(login, hourlyRate, name, dateAdded, employeeType, salt, hash);
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

	@Override
	public double getPay() {
		double pay = this.hoursWorked * this.hourlyRate;
		return pay;
	}

	public String toString() {
		return super.toString();
	}
}
