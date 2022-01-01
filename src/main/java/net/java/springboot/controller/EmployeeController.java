package net.java.springboot.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.java.springboot.models.Department;
import net.java.springboot.models.Employee;
import net.java.springboot.projectException.ResourceNotFoundException;
import net.java.springboot.repositories.DepartmentRepository;
import net.java.springboot.repositories.EmployeeRepository;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/v1/")
public class EmployeeController {
	@Autowired
	EmployeeRepository employeeRepository;
	
	@Autowired
	private DepartmentRepository departmentRepository;
	
	//Check a string element is in String Array element
	boolean checkInArr(String [] arr, String testStr) {
		for(String i : arr) {
			if(i.equals(testStr)) return true;
		}
		return false;
	}
	
	private String genderArr[] = new String[] {"male", "female", "other"};
	
	//Get all employees
	@GetMapping("/employees")
	public List<Employee> getAllEmployee() {
		return employeeRepository.findAll();
	}
	
	//Get employee by id, throw an exception if could not find Employee with given id
	@GetMapping("/employees/{id}")
	public ResponseEntity<Employee> getEmployeeById(@PathVariable long id) {
		Employee employee = employeeRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Employee not exist with id: " + id));
		return ResponseEntity.ok(employee);
	}
	
	//Add employee to repository, *Note that this employee don't work for any department, depart attribute will be default: 0
	@PostMapping("/employees")
	public Employee createEmployee(@RequestBody Employee employee) throws IllegalArgumentException {
		if(!checkInArr(genderArr, employee.getGender()))
			throw new IllegalArgumentException("Gender is not valid");
		return employeeRepository.save(employee);
	}
	
	//Add employee to department
	@PutMapping("/addEmployeeToDepart/{departId}/{employeeId}")
	public ResponseEntity<Employee> addEmployee(@PathVariable Long departId,@PathVariable Long employeeId) {
		//Find department with given id, throw an exception if could not find Department with given id
		Department departmentFound = departmentRepository.findById(departId)
				.orElseThrow(() -> new ResourceNotFoundException("Depart not exist with id: " + departId));	
		
		//Find employee with given id, throw an exception if could not find Employee with given id
		Employee employeeFound = employeeRepository.findById(employeeId)
				.orElseThrow(() -> new ResourceNotFoundException("Employee not exist with id: " + employeeId));
		
		if(departmentFound.getBasicSalary() < employeeFound.getSalary())
				throw new ResourceNotFoundException("Employee's salary is more than basic depart's salary");
		
		//Change depart attribute
		employeeFound.setDepart(departmentFound.getId());
		
		//Add to depart
		departmentFound.getEmployee().add(employeeFound);
		departmentRepository.save(departmentFound);
		return ResponseEntity.ok(employeeFound);
	}
	
	//Remove employee from department
	@PutMapping("/removeEmployeeFromDepart/{departId}/{employeeId}")
	public ResponseEntity<Employee> removeEmployee(@PathVariable Long departId,@PathVariable Long employeeId) throws IllegalArgumentException {
		//Find department with given id, throw an exception if could not find Department with given id
		Department departmentFound = departmentRepository.findById(departId)
				.orElseThrow(() -> new ResourceNotFoundException("Depart not exist with id: " + departId));	
		
		//Find employee with given id, throw an exception if could not find Employee with given id
		Employee employeeFound = employeeRepository.findById(employeeId)
				.orElseThrow(() -> new ResourceNotFoundException("Employee not exist with id: " + employeeId));
		
		if(departmentFound.getId() != employeeFound.getDepart()) {
			throw new ResourceNotFoundException("This employee is not in this department");
		}
		
		//The Employee is manager, throw an error 
		if(departmentFound.getManager().getId() == employeeFound.getId()) {
			throw new IllegalArgumentException("This Employee is a Manager");
		}
			
		//Change depart attribute to 0
		employeeFound.setDepart(0);
			
		//Add to depart
		departmentFound.getEmployee().remove(employeeFound);
		departmentRepository.save(departmentFound);
		return ResponseEntity.ok(employeeFound);
	}
	
	//Set an employee to manager
	@PutMapping("/setToManager/{departId}/{employeeId}")
	public ResponseEntity<Employee> setToManager(@PathVariable Long departId,@PathVariable Long employeeId) throws IllegalArgumentException{
		//Find department with given id, throw an exception if could not find Department with given id
		Department departmentFound = departmentRepository.findById(departId)
				.orElseThrow(() -> new ResourceNotFoundException("Depart not exist with id: " + departId));	
		
		//Find employee with given id, throw an exception if could not find Employee with given id
		Employee employeeFound = employeeRepository.findById(employeeId)
				.orElseThrow(() -> new ResourceNotFoundException("Employee not exist with id: " + employeeId));
		
		//If this department already have a manager or this employee is not in the department
		if(departmentFound.getManager() != null || employeeFound.getDepart() != departmentFound.getId()) {
			throw new IllegalArgumentException("Can't set this employee to manager");
		}
			
		//Set to manager
		employeeFound.setRole("Manager");
		employeeFound.setSalary(employeeFound.getSalary() * 1.5);
		departmentFound.setManager(employeeFound);
		departmentRepository.save(departmentFound);
		return ResponseEntity.ok(employeeFound);
	}
	
	//Set a manager to employee
	@PutMapping("/setToStaff/{departId}/{employeeId}")
	public ResponseEntity<Employee> setToStaff(@PathVariable Long departId,@PathVariable Long employeeId) throws IllegalArgumentException {
		//Find department with given id, throw an exception if could not find Department with given id
		Department departmentFound = departmentRepository.findById(departId)
			.orElseThrow(() -> new ResourceNotFoundException("Depart not exist with id: " + departId));	
			
		//Find employee with given id, throw an exception if could not find Employee with given id
		Employee employeeFound = employeeRepository.findById(employeeId)
			.orElseThrow(() -> new ResourceNotFoundException("Employee not exist with id: " + employeeId));
			
		//If this employee is not manager
		if(departmentFound.getManager().getId() != employeeFound.getId()) {
			throw new IllegalArgumentException("This employee is not manager");
		}
				
		//Set to manager
		employeeFound.setRole("Staff");
		employeeFound.setSalary(employeeFound.getSalary() / 1.5);
		departmentFound.setManager(null);
		departmentRepository.save(departmentFound);
		return ResponseEntity.ok(employeeFound);
	}
	
	//Update employee, throw an exception if could not find Employee with given id
	@PutMapping("/employees/{id}")
	public ResponseEntity<Employee> updateEmployee(@PathVariable Long id, @RequestBody Employee employeeDetails) throws IllegalArgumentException {
		Employee employee = employeeRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Employee not exist with id: " + id));
			
		employee.setFirstName(employeeDetails.getFirstName());
		employee.setLastName(employeeDetails.getLastName());
		employee.setBirthDay(employeeDetails.getBirthDay());
		employee.setEmail(employeeDetails.getEmail());
		employee.setGender(employeeDetails.getGender());
		employee.setPhone(employeeDetails.getPhone());
		employee.setSalary(employeeDetails.getSalary());
		
		if(!employee.getRole().equals(employee.getRole())) {
			throw new IllegalArgumentException("This employee's role has been changed");
		}
			
		Employee updatedEmployee = employeeRepository.save(employee);
		return ResponseEntity.ok(updatedEmployee);
	}
	
	//Delete an employee, throw an exception if could not find Employee with given id
	@DeleteMapping("/employees/{id}")
	public ResponseEntity<Map<String, Boolean>> deleteEmployee(@PathVariable Long id) {
		Employee employee = employeeRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Employee not exist with id: " + id));
		employeeRepository.delete(employee);
		
		Map<String, Boolean> response = new HashMap<>();
		response.put("deleted employee with id: " + employee.getId(), Boolean.TRUE);
		return ResponseEntity.ok(response);
	}
	
	//Delete all employee in repository
	@DeleteMapping("/employees")
	public ResponseEntity<Map<String, Boolean>> deleteAllEmployee() {
		employeeRepository.deleteAll();
		
		Map<String, Boolean> response = new HashMap<>();
		response.put("deleted all employees", Boolean.TRUE);
		return ResponseEntity.ok(response);
	}
}