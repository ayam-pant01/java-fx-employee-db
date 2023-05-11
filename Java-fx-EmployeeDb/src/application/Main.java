package application;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class Main extends Application {
	public static Payroll start = new Payroll();
	public static ArrayList<Employee> empList = start.getEmployeeList();
	public static ArrayList<Employee> terminatedEmpList = start.getTerminatedEmployeeList();
	public static Employee currentUser = start.getCurrentUser();

	@Override
	public void start(Stage primaryStage) {
		if (currentUser == null) {
			showLoginPage(primaryStage);
		} else {
			showMainMenu(primaryStage);
		}

	}

	private static void showPayEmployeesScreen(Stage primaryStage) {

		// create table view to display employee data

		TableView<Employee> payEmpTable = new TableView<>();
		payEmpTable.setEditable(false);
		double totalPay = 0;
		for (Employee employee : empList) {
			if (employee.getType() == 2) {
				TextInputDialog dialog = new TextInputDialog();
				dialog.setTitle("Enter " + employee.getName() + " total hours");
				dialog.setHeaderText(null);
				dialog.setContentText("Please enter hours:");

				Optional<String> result = dialog.showAndWait();

				if (result.isPresent()) {
					try {
						double number = Double.parseDouble(result.get());
						employee.setHours(number);
					} catch (NumberFormatException e) {
						// Handle invalid input
					}
				}
			}
			totalPay += employee.getPay();
		}
		// create table columns for employee data
		TableColumn<Employee, Integer> idColumn = new TableColumn<>("ID");
		idColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
		TableColumn<Employee, String> nameColumn = new TableColumn<>("Name");
		nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
		TableColumn<Employee, Double> payColumn = new TableColumn<>("Pay");
		payColumn.setCellValueFactory(cellData -> Bindings
				.createObjectBinding(() -> Double.valueOf(String.format("%.2f", cellData.getValue().getPay()))));

		// add columns to table view
		payEmpTable.getColumns().addAll(idColumn, nameColumn, payColumn);
		payEmpTable.getItems().addAll(empList);
		for (Employee employee : empList) {
			totalPay += employee.getPay();
		}
		Label totalPayLabel = new Label("Total Pay: $" + Double.valueOf(String.format("%.2f", totalPay)));
		// create VBox to hold the table view
		Button backButton = new Button("Back");
		backButton.setOnAction(e -> {
			showMainMenu(primaryStage);
		});

		VBox vBox = new VBox(10);
		vBox.setAlignment(Pos.CENTER);
		vBox.setPadding(new Insets(10));
		vBox.getChildren().add(payEmpTable);
		vBox.getChildren().add(totalPayLabel);
		vBox.getChildren().add(backButton);

		// create scene and set it on the primary stage
		Scene scene = new Scene(new BorderPane(vBox), 600, 400);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Payroll Report - " + Payroll.convertDateToString(new Date()));
		// show the primary stage
		primaryStage.show();
	}

	private static void showEmployeeSelectionForm(Stage primaryStage, Boolean isTerminate) {
		Label idLabel = new Label("Employee ID:");
		TextField idTextField = new TextField();
		idTextField.setMaxWidth(200);
		Button submitButton = new Button("Submit");
		Button cancelButton = new Button("Cancel");

		// Layout UI elements
		VBox root = new VBox();
		root.setAlignment(Pos.CENTER);
		root.setSpacing(10);
		root.setPadding(new Insets(10));
		root.getChildren().addAll(idLabel, idTextField);

		HBox buttonBox = new HBox();
		buttonBox.setAlignment(Pos.CENTER);
		buttonBox.setSpacing(10);
		buttonBox.getChildren().addAll(submitButton, cancelButton);

		root.getChildren().add(buttonBox);

		// Set up event handler for submit button
		submitButton.setOnAction(event -> {
			if (idTextField.getText().isEmpty()) {
				Alert alert = new Alert(AlertType.ERROR, "Enter employeeid to process.");
				alert.showAndWait();
				return;
			}
			int id = Integer.parseInt(idTextField.getText());
			Employee employee = Payroll.findEmployeeById(id);
			if (employee == null) {
				Alert alert = new Alert(AlertType.ERROR, "Employee with the provided ID not found.");
				alert.showAndWait();
				return;
			} else {
				if (isTerminate) {
					if (currentUser.getId() != 0 && currentUser.getId() != employee.getId()) {
						Alert alert = new Alert(AlertType.ERROR, "You are not authorized to terminate this employee.");
						alert.showAndWait();
						return;
					}
					start.removeEmployee(employee);
					start.addTerminatedEmployee(employee);
					if (currentUser == employee) {
						Alert alert = new Alert(AlertType.INFORMATION,
								"Currently logged in user just quit. Please Re-login to continue.");
						alert.showAndWait();
						showLoginPage(primaryStage);
						return;
					} else {
						Alert alert = new Alert(AlertType.INFORMATION,
								"Existing employee " + employee.getName() + " terminated successfully!");
						alert.showAndWait();
						showMainMenu(primaryStage);
						return;
					}
				} else {
					if (currentUser.getId() != 0 && currentUser.getId() != employee.getId()) {
						Alert alert = new Alert(AlertType.ERROR,
								"You are not authorized to change this employee data.");
						alert.showAndWait();
						return;
					}
					showEnterEmployeesForm(primaryStage, employee);
				}
			}
		});

		// Set up event handler for cancel button
		cancelButton.setOnAction(event -> {
			showMainMenu(primaryStage);
		});

		Scene scene = new Scene(root, 400, 400);
		primaryStage.setScene(scene);
		if (isTerminate) {
			primaryStage.setTitle("Enter Employee Information to terminate");
		} else {
			primaryStage.setTitle("Enter Employee Information to change");
		}
		primaryStage.show();
	}

	private static void showEmployeeList(Stage primaryStage, Boolean isTerminatedList) {
		// create table view to display employee data
		TableView<Employee> employeeTable = new TableView<>();
		employeeTable.setEditable(false);

		// create table columns for employee data
		TableColumn<Employee, Integer> idColumn = new TableColumn<>("ID");
		idColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
		TableColumn<Employee, String> typeColumn = new TableColumn<>("Type");
		typeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTypeName()));
		TableColumn<Employee, String> loginNameColumn = new TableColumn<>("Login Name");
		loginNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLoginName()));
		TableColumn<Employee, String> addedDateColumn = new TableColumn<>("Added Date");
		addedDateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAddedDate()));
		TableColumn<Employee, String> nameColumn = new TableColumn<>("Name");
		nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
		TableColumn<Employee, Double> salaryColumn = new TableColumn<>("Base Salary");
		salaryColumn
				.setCellValueFactory(cellData -> Bindings.createObjectBinding(() -> cellData.getValue().getSalary()));

		// add columns to table view
		employeeTable.getColumns().addAll(idColumn, typeColumn, loginNameColumn, addedDateColumn, nameColumn,
				salaryColumn);

		if (isTerminatedList) {
			employeeTable.getItems().addAll(terminatedEmpList);
		} else {
			System.out.print("ComesHere");
			if (currentUser.getId() != 0) {
				List<Employee> filteredList = empList.stream().filter(emp -> emp.getId() == currentUser.getId())
						.collect(Collectors.toList());
				employeeTable.getItems().addAll(filteredList);
			} else {
				employeeTable.getItems().addAll(empList);
			}
		}

		// create VBox to hold the table view
		Button backButton = new Button("Back");
		backButton.setOnAction(e -> {
			showMainMenu(primaryStage);
		});

		VBox vBox = new VBox(10);
		vBox.setAlignment(Pos.CENTER);
		vBox.setPadding(new Insets(10));
		vBox.getChildren().add(employeeTable);
		vBox.getChildren().add(backButton);

		// create scene and set it on the primary stage
		Scene scene = new Scene(new BorderPane(vBox), 600, 400);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Employee List");
		if (isTerminatedList) {
			primaryStage.setTitle("Terminated Employee List");
		}

		// show the primary stage
		primaryStage.show();
	}

	private static void showEnterEmployeesForm(Stage primaryStage, Employee selectedEmployee) {
		// create GridPane to hold form controls
		GridPane gridPane = new GridPane();
		gridPane.setAlignment(Pos.CENTER);
		gridPane.setHgap(10);
		gridPane.setVgap(10);
		gridPane.setPadding(new Insets(10));

		// create form controls
		Label titleLabel = new Label("Enter Employee Information");
		titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
		GridPane.setHalignment(titleLabel, HPos.CENTER);
		GridPane.setColumnSpan(titleLabel, 2);

		Label loginNameLabel = new Label("Login Name:");
		TextField loginNameTextField = new TextField();

		Label nameLabel = new Label("Name:");
		TextField nameTextField = new TextField();

		Label empTypeLabel = new Label("Employee Type:");
		RadioButton salariedRadioButton = new RadioButton("Salaried");
		RadioButton hourlyRadioButton = new RadioButton("Hourly");
		ToggleGroup empTypeToggleGroup = new ToggleGroup();
		salariedRadioButton.setToggleGroup(empTypeToggleGroup);
		hourlyRadioButton.setToggleGroup(empTypeToggleGroup);

		Label salaryLabel = new Label("Salary:");
		TextField salaryTextField = new TextField();

		Label hourlyRateLabel = new Label("Hourly Rate:");
		TextField hourlyRateTextField = new TextField();

		// hide hourly rate field by default
		hourlyRateLabel.setVisible(false);
		hourlyRateTextField.setVisible(false);

		// add event listener to toggle hourly rate field visibility based on employee
		// type
		empTypeToggleGroup.selectedToggleProperty().addListener((obsVal, oldVal, newVal) -> {
			if (newVal == salariedRadioButton) {
				salaryLabel.setVisible(true);
				salaryTextField.setVisible(true);
				hourlyRateLabel.setVisible(false);
				hourlyRateTextField.setVisible(false);
			} else if (newVal == hourlyRadioButton) {
				hourlyRateLabel.setVisible(true);
				hourlyRateTextField.setVisible(true);
				salaryLabel.setVisible(false);
				salaryTextField.setVisible(false);
			}
		});

		Button submitButton = new Button("Submit");

		// set up event handler for submit button
		submitButton.setOnAction(e -> {
			String loginName = loginNameTextField.getText();
			boolean isUnique = true;
			for (Employee emp : empList) {
				if (selectedEmployee == null) {
					if (emp.getLoginName().equals(loginName)) {
						isUnique = false;
						break;
					}
				} else {

					if (emp.getLoginName().equals(loginName) && emp.getId() != selectedEmployee.getId()) {
						isUnique = false;
						break;
					}
				}
			}
			if (!isUnique) {
				Alert alert = new Alert(AlertType.ERROR,
						"Login name already exists. Please enter a different login name.");
				alert.showAndWait();
				return;
			}
			String name = nameTextField.getText();
			if (empTypeToggleGroup.getSelectedToggle() == null) {
				Alert alert = new Alert(AlertType.ERROR, "Please select a type of employee.");
				alert.showAndWait();
				return;
			}
			int empTypeChoice = empTypeToggleGroup.getSelectedToggle() == salariedRadioButton ? 1 : 2;
			double salary = 0.0;
			double hourlyRate = 0.0;
			try {
				if (empTypeChoice == 1) {
					salary = Double.parseDouble(salaryTextField.getText());
				} else {
					hourlyRate = Double.parseDouble(hourlyRateTextField.getText());
				}
			} catch (NumberFormatException ex) {
				Alert alert = new Alert(AlertType.ERROR, "Please enter a valid number for salary/hourly rate.");
				alert.showAndWait();
				return;
			}
			if (selectedEmployee == null) {
				Date curDate = new Date();
				Employee newEmployee;
				if (empTypeChoice == 1) {
					newEmployee = new Salaried(loginName, salary, name, Payroll.convertDateToString(curDate),
							empTypeChoice);
				} else {
					newEmployee = new Hourly(loginName, name, Payroll.convertDateToString(curDate), hourlyRate,
							empTypeChoice);
				}
				start.addEmployee(newEmployee);
				Alert alert = new Alert(AlertType.INFORMATION, "New employee " + name + " added!");
				alert.showAndWait();
				showMainMenu(primaryStage);
			} else {
				Employee existingEmp = Payroll.findEmployeeById(selectedEmployee.getId());
				Employee updatedEmployee;
				if (empTypeChoice == 1) {
					updatedEmployee = new Salaried(selectedEmployee.getId(), loginName, salary, name,
							selectedEmployee.getAddedDate(), empTypeChoice);
				} else {
					updatedEmployee = new Hourly(selectedEmployee.getId(), loginName, name,
							selectedEmployee.getAddedDate(), hourlyRate, empTypeChoice);
				}
				start.removeEmployee(existingEmp);
				start.addEmployee(updatedEmployee);
				Payroll.saveChanges();
				Alert alert = new Alert(AlertType.INFORMATION, "Existing employee " + name + " updated!");
				alert.showAndWait();
				showMainMenu(primaryStage);
			}

		});

		if (selectedEmployee != null) {

			nameTextField.setText(selectedEmployee.getName());
			loginNameTextField.setText(selectedEmployee.getLoginName());
			if (selectedEmployee.getType() == 1) {

				salaryTextField.setText(String.valueOf(selectedEmployee.getSalary()));
				salariedRadioButton.setSelected(true);
				hourlyRadioButton.setSelected(false);
			} else {
				hourlyRateTextField.setText(String.valueOf(selectedEmployee.getSalary()));
				hourlyRadioButton.setSelected(true);
				salariedRadioButton.setSelected(false);
			}
		}

		Button cancelButton = new Button("Cancel");
		cancelButton.setOnAction(e -> {
			showMainMenu(primaryStage);
		});

		// add form controls to gridPane
		gridPane.add(titleLabel, 0, 0);
		gridPane.add(loginNameLabel, 0, 1);
		gridPane.add(loginNameTextField, 1, 1);
		gridPane.add(nameLabel, 0, 2);
		gridPane.add(nameTextField, 1, 2);
		gridPane.add(empTypeLabel, 0, 3);
		gridPane.add(salariedRadioButton, 1, 3);
		gridPane.add(hourlyRadioButton, 1, 4);
		gridPane.add(salaryLabel, 0, 5);
		gridPane.add(salaryTextField, 1, 5);
		gridPane.add(hourlyRateLabel, 0, 6);
		gridPane.add(hourlyRateTextField, 1, 6);
		gridPane.add(cancelButton, 0, 7);
		gridPane.add(submitButton, 1, 7);

		// create scene and set it on the primary stage
		Scene scene = new Scene(new BorderPane(gridPane), 400, 400);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Enter Employee Information");

		// show the primary stage
		primaryStage.show();
	}

	private static void showMainMenu(Stage primaryStage) {
		// create VBox to hold the main menu
		VBox vBox = new VBox(10);
		vBox.setAlignment(Pos.CENTER);
		vBox.setPadding(new Insets(10));

		// create menu labels and buttons
		Label menuLabel = new Label("Payroll Menu");
		Button enterEmployeesButton = new Button("Enter Employees");
		enterEmployeesButton.setOnAction(e -> {
			showEnterEmployeesForm(primaryStage, null);
		});
		Button listEmployeesButton = new Button("List Employees");
		listEmployeesButton.setOnAction(e -> {
			showEmployeeList(primaryStage, false);
		});
		Button changeEmployeeDataButton = new Button("Change Employee Data");
		changeEmployeeDataButton.setOnAction(e -> {
			showEmployeeSelectionForm(primaryStage, false);
		});
		Button terminateEmployeeButton = new Button("Terminate Employee");
		terminateEmployeeButton.setOnAction(e -> {
			showEmployeeSelectionForm(primaryStage, true);
		});
		Button payEmployeesButton = new Button("Pay Employees");
		payEmployeesButton.setOnAction(e -> {
			showPayEmployeesScreen(primaryStage);
			Payroll.payEmployees();
		});
		Button listTerminatedEmployeesButton = new Button("List Terminated Employees");
		listTerminatedEmployeesButton.setOnAction(e -> {
			showEmployeeList(primaryStage, true);
		});

		Button logOutButton = new Button("Log Out");
		logOutButton.setOnAction(e -> {
			// handle exit system button click here
			currentUser = null;
			showLoginPage(primaryStage);
		});
		Button exitSystemButton = new Button("Exit System");
		exitSystemButton.setOnAction(e -> {
			// handle exit system button click here
			primaryStage.close();
		});

		// add menu labels and buttons to the VBox
		vBox.getChildren().addAll(menuLabel, enterEmployeesButton, listEmployeesButton, changeEmployeeDataButton,
				terminateEmployeeButton, payEmployeesButton, listTerminatedEmployeesButton, logOutButton,
				exitSystemButton);

		if (currentUser.getId() != 0) {
			enterEmployeesButton.setVisible(false);
			payEmployeesButton.setVisible(false);
			listTerminatedEmployeesButton.setVisible(false);
		}
		// create scene and set it on the primary stage
		Scene scene = new Scene(new BorderPane(vBox), 400, 400);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Payroll Menu");

		// show the primary stage
		primaryStage.show();
	}

	private static void showLoginPage(Stage primaryStage) {
		Label idLabel = new Label("Employee Database:");
		// create VBox to hold the login form
		VBox vBox = new VBox(10);
		vBox.setAlignment(Pos.CENTER);
		vBox.setPadding(new Insets(10));
		vBox.getChildren().add(idLabel);

		// create login name label and field
		TextField loginNameField = new TextField();
		loginNameField.setPromptText("Login Name:");
		loginNameField.setMaxWidth(200);
		vBox.getChildren().add(loginNameField);

		// create password label and field
		PasswordField passwordField = new PasswordField();
		passwordField.setPromptText("Password:");
		passwordField.setMaxWidth(200);
		vBox.getChildren().add(passwordField);

		// create login button
		Button loginButton = new Button("Login");
		loginButton.setOnAction(e -> {
			// handle login button click here
			String loginName = loginNameField.getText();
			String password = passwordField.getText();
			System.out.println("Login name: " + loginName);
			System.out.println("Password: " + password);
			boolean userFound = false;
			for (Employee employee : empList) {
				if (employee.getLoginName().equals(loginName)) {
					currentUser = employee;
					userFound = true;
					System.out.println("Login Successfull!!!\n");
					System.out.println("Logged in as " + currentUser.getName() + ". \n");
					break;
				}
			}

			if (!userFound) {
				Alert alert = new Alert(AlertType.ERROR, "Login Credentians doesn't match.");
				alert.showAndWait();
				return;
			}
			showMainMenu(primaryStage);
		});
		vBox.getChildren().add(loginButton);

		// create scene and set it on the primary stage
		Scene scene = new Scene(new BorderPane(vBox), 400, 400);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Login Page");

		// show the primary stage
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
