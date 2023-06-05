package com.IncomeTax.controller;

import com.IncomeTax.models.Employee;
import com.IncomeTax.models.EmployeeTaxDetails;
import com.IncomeTax.service.EmployeeService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employees")
@Validated
public class EmployeeController {

    private final EmployeeService employeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping
    public ResponseEntity<Employee> storeEmployeeDetails(@RequestBody @Valid Employee employee) {
        Employee savedEmployee = employeeService.saveEmployee(employee);
        return new ResponseEntity<>(savedEmployee, HttpStatus.CREATED);
    }

    @GetMapping("/tax-deduction")
    public ResponseEntity<List<EmployeeTaxDetails>> getEmployeeTaxDeduction() {
        List<EmployeeTaxDetails> taxDetails = employeeService.getEmployeeTaxDeduction();
        return new ResponseEntity<>(taxDetails, HttpStatus.OK);
    }
}
