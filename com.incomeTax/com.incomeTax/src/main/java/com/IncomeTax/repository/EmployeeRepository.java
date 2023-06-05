package com.IncomeTax.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.IncomeTax.models.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}

