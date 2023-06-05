package com.IncomeTax.service;

import com.IncomeTax.models.Employee;
import com.IncomeTax.models.EmployeeTaxDetails;
import com.IncomeTax.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public Employee saveEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    public List<EmployeeTaxDetails> getEmployeeTaxDeduction() {
        List<Employee> employees = employeeRepository.findAll();
        List<EmployeeTaxDetails> taxDetails = calculateTaxDeduction(employees);
        return taxDetails;
    }

    private List<EmployeeTaxDetails> calculateTaxDeduction(List<Employee> employees) {
        List<EmployeeTaxDetails> taxDetailsList = new ArrayList<>();

        for (Employee employee : employees) {
            LocalDate doj = employee.getDoj();
            double salary = employee.getSalary();
            int joiningMonth = doj.getMonthValue();
            int joiningYear = doj.getYear();

            LocalDate financialYearStart = LocalDate.of(joiningYear, 4, 1);
            LocalDate financialYearEnd = LocalDate.of(joiningYear + 1, 3, 31);

            double totalSalary = calculateTotalSalary(financialYearStart, financialYearEnd, joiningMonth, salary);
            double taxAmount = calculateTaxAmount(totalSalary);
            double cessAmount = calculateCessAmount(totalSalary);

            EmployeeTaxDetails taxDetails = new EmployeeTaxDetails();
            taxDetails.setEmployeeCode(employee.getEmployeeId());
            taxDetails.setFirstName(employee.getFirstName());
            taxDetails.setLastName(employee.getLastName());
            taxDetails.setYearlySalary(totalSalary);
            taxDetails.setTaxAmount(taxAmount);
            taxDetails.setCessAmount(cessAmount);

            taxDetailsList.add(taxDetails);
        }

        return taxDetailsList;
    }

    private double calculateTotalSalary(LocalDate financialYearStart, LocalDate financialYearEnd, int joiningMonth, double salary) {
        LocalDate proratedStart = joiningMonth > 3 ? financialYearStart.withYear(financialYearStart.getYear() + 1) : financialYearStart;
        LocalDate proratedEnd = financialYearEnd;
        if (joiningMonth < 4) {
            proratedEnd = joiningMonth == 1 ? financialYearEnd.minusYears(1).minusDays(1) : financialYearEnd.minusMonths(1);
        }

        int monthsInFinancialYear = proratedStart.getMonthValue() <= proratedEnd.getMonthValue() ?
                proratedStart.until(proratedEnd).getMonths() + 1 :
                proratedStart.until(proratedEnd).getMonths() + 12 + 1;

        return salary * monthsInFinancialYear;
    }

    private double calculateTaxAmount(double yearlySalary) {
        if (yearlySalary <= 250000) {
            return 0;
        } else if (yearlySalary <= 500000) {
            return (yearlySalary - 250000) * 0.05;
        } else if (yearlySalary <= 1000000) {
            return 12500 + (yearlySalary - 500000) * 0.1;
        } else {
            return 12500 + 50000 + (yearlySalary - 1000000) * 0.2;
        }
    }

    private double calculateCessAmount(double yearlySalary) {
        if (yearlySalary > 2500000) {
            return (yearlySalary - 2500000) * 0.02;
        }
        return 0;
    }
}
