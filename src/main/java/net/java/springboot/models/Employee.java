package net.java.springboot.models;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "Employee")
public class Employee {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Column(name = "first_name")
	protected String firstName;
	
	@Column(name = "last_name")
	protected String lastName;
	
	@Column(name = "gender")
	protected String gender;
	
	@Column(name = "email", unique = true)
	protected String email;
	
	@Column(name = "phone", unique = true)
	protected String phone;
	
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern = "dd/mm/yyyy")
	protected Date birthDay;
	
	@Column(name = "role")
	protected String role = "Staff";
	
	@Column(name = "salary")
	protected double salary;
	
	@Column(name = "depart")
	protected long depart = 0;
	
	//Default Constructor
	public Employee() {
		super();
	}
	
	//Constructor
	public Employee(String firstName, String lastName, String gender, String email, String phone, Date birthDay,
			double salary) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.gender = gender;		
		this.email = email;
		this.phone = phone;
		this.birthDay = birthDay;
		this.salary = salary;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public Date getBirthDay() {
		return birthDay;
	}

	public void setBirthDay(Date birthDay) {
		this.birthDay = birthDay;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public double getSalary() {
		return salary;
	}

	public void setSalary(double salary) {
		this.salary = salary;
	}

	public long getDepart() {
		return depart;
	}

	public void setDepart(long depart) {
		this.depart = depart;
	}

	@Override
	public String toString() {
		return "Employee [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", gender=" + gender
				+ ", email=" + email + ", phone=" + phone + ", birthDay=" + birthDay + ", role=" + role + ", salary="
				+ salary + ", depart=" + depart + "]";
	}	
}
