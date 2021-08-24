package com.example.simoparvanovemployees.service.impl;

import com.example.simoparvanovemployees.model.BestEmployees;
import com.example.simoparvanovemployees.model.Employees;
import com.example.simoparvanovemployees.service.EmployeesService;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class EmployeesServiceImpl implements EmployeesService {
    public static String[] variations = new String[2];
    public static Set<String> setAllElements = new HashSet<>();
    public static Set<String> setMomentElements = new HashSet<>();
    public static List<String> elements = new ArrayList<>();
    public static List<String> uniqueCouples = new ArrayList<>();
    private static final String[] formats = {
            "yyyy-MM-dd'T'HH:mm:ss'Z'", "yyyy-MM-dd'T'HH:mm:ssZ",
            "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            "yyyy-MM-dd'T'HH:mm:ss.SSSZ", "yyyy-MM-dd HH:mm:ss",
            "MM/dd/yyyy HH:mm:ss", "MM/dd/yyyy'T'HH:mm:ss.SSS'Z'",
            "MM/dd/yyyy'T'HH:mm:ss.SSSZ", "MM/dd/yyyy'T'HH:mm:ss.SSS",
            "MM/dd/yyyy'T'HH:mm:ssZ", "MM/dd/yyyy'T'HH:mm:ss",
            "yyyy:MM:dd HH:mm:ss", "yyyy-MM-dd",
            "MM/dd/yyyy", "dd/MM/yyyy",
            "dd-MMM-yyyy", "EEE, d MMM yyyy",
            "dd-M-yyyy hh:mm:ss a", "dd/MM/yyyy HH:mm:ss"};

    @Override
    public List<BestEmployees> getBestEmployees(String path) {
        List<Employees> employeesList = new ArrayList<>();
        FileReader file;
        try {
            file = new FileReader(path);
            BufferedReader bf = new BufferedReader(file);
            try {
                String temp;
                while ((temp = bf.readLine()) != null) {
                    String replaceComma = temp.replace(",", "");
                    String[] date = replaceComma.split("[\\s+]");
                    String empID = date[0];
                    String projectID = date[1];
                    String dateFrom = date[2];
                    String dateTo = date[3];
                    setAllElements.add(empID);

                    Employees employees = new Employees(
                            empID,
                            projectID,
                            dateFormatter(dateFrom),
                            dateFormatter(dateTo));

                    employeesList.add(employees);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
        elements.addAll(setAllElements);
        return getBestEmployees(employeesList);
    }

    private List<BestEmployees> getBestEmployees(List<Employees> employeesList) {
        HashMap<String, HashMap<String, Long>> mapBestEmployees = getMapPyProject(employeesList);
        List<BestEmployees> bestEmployees = new ArrayList<>();
        for (Map.Entry<String, HashMap<String, Long>> entry : mapBestEmployees.entrySet()) {
            String[] split = entry.getKey().split("@");
            String emp1 = split[0];
            String emp2 = split[1];
            for (Map.Entry<String, Long> employee : entry.getValue().entrySet()) {

                BestEmployees emp = new BestEmployees();
                emp.setProjectId(employee.getKey());
                emp.setEmpIdOne(emp1);
                emp.setEmpIdTwo(emp2);
                emp.setAllDays(employee.getValue());
                bestEmployees.add(emp);
            }
        }
        return bestEmployees;
    }

    private static HashMap<String, HashMap<String, Long>> getMapPyProject(List<Employees> employeesList) {
        HashMap<String, HashMap<String, Long>> mapProject = new HashMap<>();
        comb(0, 0);
        for (Employees employees : employeesList) {
            long noOfDaysBetween = ChronoUnit.DAYS.between(employees.getStartDate(), employees.getEndDate());
            mapProject.putIfAbsent(employees.getProjectId(), new HashMap<>());
            mapProject.get(employees.getProjectId()).put(employees.getEmpId(), noOfDaysBetween);
        }
        return getMapByUniqueCouplesEmployees(mapProject);
    }

    private static HashMap<String, HashMap<String, Long>> getMapByUniqueCouplesEmployees(HashMap<String, HashMap<String, Long>> mapProject) {
        HashMap<String, HashMap<String, Long>> mapProjectByEmployees = new HashMap<>();
        HashMap<String, Long> mapEmployeesForOneProject = new HashMap<>();

        for (Map.Entry<String, HashMap<String, Long>> entry : mapProject.entrySet()) {
            HashMap<String, Long> mapEmp = entry.getValue();
            for (Map.Entry<String, Long> entryEmployee : mapEmp.entrySet()) {
                setMomentElements.add(entryEmployee.getKey());
                mapEmployeesForOneProject.put(entryEmployee.getKey(), entryEmployee.getValue());
            }
            extracted(mapProjectByEmployees, mapEmployeesForOneProject, entry);
            mapEmployeesForOneProject.clear();
        }
        return selectTheBestCouple(mapProjectByEmployees);
    }

    private static void extracted(HashMap<String, HashMap<String, Long>> mapProjectByEmployees,
                                  HashMap<String, Long> mapEmployeesForOneProject, Map.Entry<String,
            HashMap<String, Long>> entry) {
        elements.clear();
        uniqueCouples.clear();
        elements.addAll(setMomentElements);
        comb(0, 0);
        setMomentElements.clear();
        for (String uniqueCouple : uniqueCouples) {
            String[] split = uniqueCouple.split("@");
            String emp1 = split[0];
            String emp2 = split[1];
            long valEmp1 = mapEmployeesForOneProject.get(emp1);
            long valEmp2 = mapEmployeesForOneProject.get(emp2);
            if (!mapProjectByEmployees.containsKey(uniqueCouple)) {
                mapProjectByEmployees.put(uniqueCouple, new HashMap<>());
                mapProjectByEmployees.get(uniqueCouple).put(entry.getKey(), 0L);
            }
            mapProjectByEmployees.get(uniqueCouple).putIfAbsent(entry.getKey(), 0L);
            mapProjectByEmployees.get(uniqueCouple).put(entry.getKey(),
                    mapProjectByEmployees.get(uniqueCouple).get(entry.getKey()) + valEmp1 + valEmp2);
        }
    }

    private static HashMap<String, HashMap<String, Long>> selectTheBestCouple(HashMap<String, HashMap<String, Long>> mapProject) {
        HashMap<String, HashMap<String, Long>> finalMap = new HashMap<>();
        String kay = "";
        long sum = 0;
        for (Map.Entry<String, HashMap<String, Long>> entry : mapProject.entrySet()) {
            if (entry.getValue().size() > 1) {
                long a = entry.getValue().values().stream().reduce(0L, Long::sum);
                if (a > sum) {
                    kay = entry.getKey();
                    sum = a;
                }
            }
        }
        finalMap.put(kay, mapProject.get(kay));
        return finalMap;
    }

    public static void comb(int index, int start) {
        if (index >= variations.length) {
            uniqueCouples.add(variations[0] + "@" + variations[1]);
        } else {
            for (int i = start; i < elements.size(); i++) {
                variations[index] = elements.get(i);
                comb(index + 1, i + 1);
            }
        }
    }

    private static LocalDate dateFormatter(String dateFrom) {
        if (!dateFrom.equals("null")) {
            for (String parse : formats) {
                SimpleDateFormat sdf = new SimpleDateFormat(parse);
                try {
                    sdf.parse(dateFrom);
                    Date date1 = new SimpleDateFormat(parse).parse(dateFrom);

                    LocalDate ld = date1.toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate();
                    return ld;
                } catch (ParseException ignored) {
                }
            }
        }
        return LocalDate.now();
    }
}
