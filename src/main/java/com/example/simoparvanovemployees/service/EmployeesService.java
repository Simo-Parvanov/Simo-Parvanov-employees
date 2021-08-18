package com.example.simoparvanovemployees.service;

import com.example.simoparvanovemployees.model.BestEmployees;

import java.util.List;

public interface EmployeesService {
  List<BestEmployees> getBestEmployees(String path);
}
