package com.bigcompany.analysis.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Employee {
    private String id;
    private String firstName;
    private String lastName;
    private String managerId;
    private double salary;
    private List<Employee> subordinates;

    public Employee(String id, String firstName, String lastName, String managerId, double salary) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.managerId = managerId;
        this.salary = salary;
        this.subordinates = new ArrayList<>();
    }

    public void addSubordinate(Employee subordinate) {
        this.subordinates.add(subordinate);
    }
}