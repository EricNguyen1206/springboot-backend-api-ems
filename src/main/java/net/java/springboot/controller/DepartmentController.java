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
import net.java.springboot.projectException.ResourceNotFoundException;
import net.java.springboot.repositories.DepartmentRepository;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/v1/")
public class DepartmentController {
	@Autowired
	private DepartmentRepository departmentRepository;
	
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
		return departmentRepository.save(department);
	}
	
	//Update department with id, throw an exception if could not find Department with given id
	@PutMapping("/departments/{id}")
	public ResponseEntity<Department> updateDepartment(@PathVariable Long id, @RequestBody Department departmentDetails) {
		Department department = departmentRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Department not exist with id: " + id));
			
		department.setBasicSalary(departmentDetails.getBasicSalary());
		department.setEmployee(departmentDetails.getEmployee());
		department.setName(departmentDetails.getName());
			
		Department updatedDepartment = departmentRepository.save(department);
		return ResponseEntity.ok(updatedDepartment);
	}
	
	//Delete Depart, throw an exception if could not find Department with given id
	@DeleteMapping("/departments/{id}")
	public ResponseEntity<Map<String, Boolean>> deleteDepartment(@PathVariable Long id) {
		Department department = departmentRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Department not exist with id: " + id));
			
		departmentRepository.delete(department);
			
		Map<String, Boolean> response = new HashMap<>();
		response.put("deleted Department with id: " + department.getId(), Boolean.TRUE);
		return ResponseEntity.ok(response);
	}
	
	//Delete all Departments
	@DeleteMapping("/deleteAllDepartment")
	public ResponseEntity<Map<String, Boolean>> deleteAllDepartment() {
		departmentRepository.deleteAll();
			
		Map<String, Boolean> response = new HashMap<>();
		response.put("deleted all departments", Boolean.TRUE);
		return ResponseEntity.ok(response);
	}
}