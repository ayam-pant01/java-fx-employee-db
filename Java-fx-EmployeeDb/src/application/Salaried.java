package application;

/**
 * 
 * @author ayamp
 *
 */
public class Salaried extends Employee {	
	/**
	 * Constructor for existing users with password hash
	 * 
	 * @param id
	 * @param login
	 * @param salary
	 * @param name
	 * @param dateAdded
	 * @param employeeType
	 * @param passwordSalt
	 * @param passwordHash
	 */
	public Salaried(int id, String login, double salary, String name, String dateAdded, int employeeType, byte[] salt,
			byte[] hash) {
		super(id, login, salary, name, dateAdded, employeeType,salt,hash);
	}

	/**
	 * Constructor with salt encryption for new users
	 * 
	 * @param login
	 * @param salary
	 * @param name
	 * @param dateAdded
	 * @param employeeType
	 * @param passwordSalt
	 * @param passwordHash
	 */
	public Salaried(String login, double salary, String name, String dateAdded, int employeeType, byte[] salt,
			byte[] hash) {
		super(login, salary, name, dateAdded, employeeType,salt,hash);
	}

	@Override
	public double getPay() {
		double pay = this.getSalary() / 24;
		return pay;
	}

	@Override
	public void setHours(double hours) {
		// Do nothing - Salaried employees don't have an hourly rate or hours worked
	}

	public int getEmployeeId() {
		return super.getId();
	}

	public String toString() {
		return super.toString();
	}
}
