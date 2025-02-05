package com.bigcompany.analysis.service;

import com.bigcompany.analysis.dto.Employee;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface CompanyAnalyzer {
    Map<String, Employee> readEmployeesFromCSVFile(String filePath) throws IOException;

    void buildSubordinateLink(Map<String, Employee> employees) throws JsonProcessingException;

    double calculateAverageSalary(List<Employee> subordinates);

    void checkManagerSalaries(Map<String, Employee> employees);

    void checkReportingLines(Map<String, Employee> employees);
}
