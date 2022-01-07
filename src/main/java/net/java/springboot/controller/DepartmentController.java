package net.java.springboot.controller;

import java.util.ArrayList;
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
public class DepartmentController {
	@Autowired
	private DepartmentRepository departmentRepository;
	
	@Autowired
	private EmployeeRepository employeeRepository;

	//Variables
	double factor = 1.5;
	
	//Get all Departments
	@GetMapping("/departments")
	public List<Department> getAllDepartment() {
		return departmentRepository.findAll();
	}
	
	//Get Department by ID, throw an exception if could not find Department with given id
	@GetMapping("/departments/{id}")
	public ResponseEntity<Department> getDepartmentById(@PathVariable long id) {
		Department department = departmentRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Department not exist with id: " + id));
		return ResponseEntity.ok(department);
	}
	
	//Add department to repository
	@PostMapping("/departments")
	public Department createDepartment(@RequestBody Department department) {
		if(department.getMaxEmployees() <= 0) {
			throw new IllegalArgumentException("maxEmployee must > 0");
		}
		return departmentRepository.save(department);
	}
	
	//Update department with id, throw an exception if could not find Department with given id
	@PutMapping("/departments/{id}")
	public ResponseEntity<Department> updateDepartment(@PathVariable Long id, @RequestBody Department departmentDetails) {
		Department departmentFound = departmentRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Department not exist with id: " + id));
		//Only allow user set Max employee more than old Max employee
		if(departmentDetails.getMaxEmployees() < departmentFound.getMaxEmployees()) {
			throw new IllegalArgumentException("Can not set maxEmployee fewer than old maxEmployee");
		}
		
		departmentFound.setName(departmentDetails.getName());
		departmentFound.setMaxEmployees(departmentDetails.getMaxEmployees());
		
		departmentRepository.save(departmentFound);
		return ResponseEntity.ok(departmentFound);
	}
	
	//Delete Depart, throw an exception if could not find Department with given id
	@DeleteMapping("/departments/{id}")
	public ResponseEntity<Map<String, Boolean>> deleteDepartment(@PathVariable Long id) {
		Department department = departmentRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Department not exist with id: " + id));
		
		List<Employee> employees = department.getEmployee();
		List<Employee> employeesNew = new ArrayList<Employee>();
		//Add employee to Temporary List
		for(int i = 0; i < employees.size(); i++) {
			Employee employeeTemp = new Employee(employees.get(i));
			employeeTemp.setDepart(0);
			employeesNew.add(employeeTemp);
		}
		
		//Add Manager as Staff
		if(department.getManager() != null) {
			Employee employeeTemp2 = new Employee(department.getManager());
			employeeTemp2.setDepart(0);
			employeeTemp2.setToStaff(factor);
			employeesNew.add(employeeTemp2);
		}
		//Add temporary List to repository
		departmentRepository.delete(department);
		for(int i = 0; i < employeesNew.size(); i++) {
			employeeRepository.save(employeesNew.get(i));
		}
			
		Map<String, Boolean> response = new HashMap<>();
		response.put("deleted Department with id: " + department.getId(), Boolean.TRUE);
		return ResponseEntity.ok(response);
	}
	
	//Delete all Departments (Use for testing)
	@DeleteMapping("/deleteAllDepartment")
	public ResponseEntity<Map<String, Boolean>> deleteAllDepartment() {
		departmentRepository.deleteAll();
			
		Map<String, Boolean> response = new HashMap<>();
		response.put("deleted all departments", Boolean.TRUE);
		return ResponseEntity.ok(response);
	}
}