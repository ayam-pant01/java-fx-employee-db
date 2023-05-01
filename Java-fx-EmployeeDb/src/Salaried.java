public class Salaried extends Employee {
	public Salaried(int id,String login, double salary, String name, String dateAdded,int employeeType) {
        super(id, login, salary, name, dateAdded,employeeType);
    }
	public Salaried(String login, double salary, String name, String dateAdded,int employeeType) {
        super(login, salary, name, dateAdded,employeeType);
    }
    
    @Override
    public double getPay() {
        double pay = this.getSalary() / 24;
        return pay;
    }
    
    public String toString() {
//        return "Salaried Employee: " + super.toString();
        return super.toString();
    }
}
