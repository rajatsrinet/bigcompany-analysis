package com.bigcompany.analysis.service;

import com.bigcompany.analysis.dto.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class CompanyAnalyzerImplTest {
    @InjectMocks
    private CompanyAnalyzerImpl companyAnalyzer;

    @Spy
    private CompanyAnalyzerImpl companyAnalyzerSpy;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testReadEmployeesFromCSV() throws IOException {
        String filePath = "src/test/resources/employees.csv";
        Map<String, Employee> employees = companyAnalyzer.readEmployeesFromCSVFile(filePath);
        assertEquals(7, employees.size());
    }

    @Test
    public void testBuildHierarchy() throws IOException {
        String filePath = "src/test/resources/employees.csv";
        Map<String, Employee> employees = companyAnalyzer.readEmployeesFromCSVFile(filePath);
        companyAnalyzer.buildSubordinateLink(employees);
        Employee ceo = employees.get("123");
        assertEquals(4, ceo.getSubordinates().size());
    }

    @Test
    public void testCalculateAverageSalary() {
        Employee emp1 = new Employee("1", "Emp", "One", "2", 100000);
        Employee emp2 = new Employee("2", "Emp", "Two", "3", 60000);
        List<Employee> subordinates = List.of(emp1, emp2);
        double averageSalary = companyAnalyzer.calculateAverageSalary(subordinates);
        assertEquals(80000, averageSalary);
    }

    @Test
    public void testCheckManagerSalaries() throws IOException {
        String filePath = "src/test/resources/employees.csv";
        Map<String, Employee> employees = companyAnalyzerSpy.readEmployeesFromCSVFile(filePath);
        companyAnalyzerSpy.buildSubordinateLink(employees);
        companyAnalyzerSpy.checkManagerSalaries(employees);
        Mockito.verify(companyAnalyzerSpy, Mockito.times(3))
                .calculateAverageSalary(Mockito.anyList());
    }

    @Test
    public void testCheckReportingLines() throws IOException {
        String filePath = "src/test/resources/employees.csv";
        Map<String, Employee> employees = companyAnalyzerSpy.readEmployeesFromCSVFile(filePath);
        companyAnalyzerSpy.buildSubordinateLink(employees);
        companyAnalyzerSpy.checkReportingLines(employees);
        Mockito.verify(companyAnalyzerSpy, Mockito.times(7))
                .countManagers(Mockito.any(), Mockito.anyMap());
    }
}
