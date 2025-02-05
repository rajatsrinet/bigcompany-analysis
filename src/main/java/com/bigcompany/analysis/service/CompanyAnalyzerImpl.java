package com.bigcompany.analysis.service;

import com.bigcompany.analysis.dto.Employee;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

@Service
@Slf4j
public class CompanyAnalyzerImpl implements CompanyAnalyzer {

    @Override
    public Map<String, Employee> readEmployeesFromCSVFile(String filePath) throws IOException {
        Map<String, Employee> employees = new HashMap<>();
        BufferedReader br = new BufferedReader(new FileReader(filePath));
        String line;
        // Skip first line
        br.readLine();
        while ((line = br.readLine()) != null) {
            String[] values = line.split(",");
            String id = values[0];
            String firstName = values[1];
            String lastName = values[2];
            double salary = Double.parseDouble(values[3]);
            String managerId = values.length > 4 ? values[4] : null;
            Employee employee = new Employee(id, firstName, lastName, managerId, salary);
            employees.put(id, employee);
        }
        log.info(new ObjectMapper().writeValueAsString(employees));
        return employees;
    }

    @Override
    public void buildSubordinateLink(Map<String, Employee> employees) throws JsonProcessingException {
        employees.values().stream()
                .filter(employee -> employee.getManagerId() != null && !employee.getManagerId().isEmpty())
                .forEach(employee -> {
                    Employee manager = employees.get(employee.getManagerId());
                    if (Objects.nonNull(manager)) {
                        manager.addSubordinate(employee);
                    }
                });
        log.info(new ObjectMapper().writeValueAsString(employees));
    }

    @Override
    public double calculateAverageSalary(List<Employee> subordinates) {
        if (CollectionUtils.isEmpty(subordinates)) {
            return 0;
        }
        double totalSalary = subordinates.stream().mapToDouble(Employee::getSalary).sum();
        return totalSalary / subordinates.size();
    }

    @Override
    public void checkManagerSalaries(Map<String, Employee> employees) {
        employees.values().stream()
                .filter(manager -> !manager.getSubordinates().isEmpty())
                .forEach(manager -> {
                    List<Employee> subordinates = manager.getSubordinates();
                    double averageSalary = calculateAverageSalary(subordinates);
                    double minSalary = averageSalary * 1.2;
                    double maxSalary = averageSalary * 1.5;
                    if (manager.getSalary() < minSalary) {
                        double difference = minSalary - manager.getSalary();
                        log.info("Manager " + manager.getFirstName() + " " + manager.getLastName()
                                + " earns less than they should by " + difference);
                    } else if (manager.getSalary() > maxSalary) {
                        double difference = manager.getSalary() - maxSalary;
                        log.info("Manager " + manager.getFirstName() + " " + manager.getLastName()
                                + " earns more than they should by " + difference);
                    }
                });
    }

    @Override
    public void checkReportingLines(Map<String, Employee> employees) {
        for (Employee employee : employees.values()) {
            int managerCount = countManagers(employee, employees);
            if (managerCount > 2) {
                int excessManagers = managerCount - 2;
                log.info("Employee " + employee.getFirstName() + " " + employee.getLastName()
                        + " has a reporting line that is too long by " + excessManagers + " managers.");
            } else {
                log.info("Employee " + employee.getFirstName() + " " + employee.getLastName()
                        + " has a reporting line that number " + managerCount + " managers.");
            }
        }
    }

    private int countManagers(Employee employee, Map<String, Employee> employees) {
        int count = 0;
        String managerId = employee.getManagerId();
        while (managerId != null) {
            count++;
            Employee manager = employees.get(managerId);
            if (manager == null) {
                break;
            }
            managerId = manager.getManagerId();
        }
        return count;
    }
}
