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
        return mapBestEmployees(twoEmployeesWithTheLongestTime(employeesList));
    }

    private List<BestEmployees> mapBestEmployees(List<String> result) {
        List<BestEmployees> bestEmployees = new ArrayList<>();

            BestEmployees emp = new BestEmployees();
            emp.setProjectId(result.get(0));
            emp.setEmpIdOne(result.get(1));
            emp.setEmpIdTwo(result.get(2));
            emp.setAllDays(Long.parseLong(result.get(3)) + Long.parseLong(result.get(4)));
            bestEmployees.add(emp);
        return bestEmployees;
    }

    private static List<String> twoEmployeesWithTheLongestTime(List<Employees> employeesList) {
        HashMap<String, HashMap<String, Long>> mapProject = new HashMap<>();
        for (Employees employees : employeesList) {
            long noOfDaysBetween = ChronoUnit.DAYS.between(employees.getStartDate(), employees.getEndDate());
            mapProject.putIfAbsent(employees.getProjectId(), new HashMap<>());
            mapProject.get(employees.getProjectId()).put(employees.getEmpId(), noOfDaysBetween);
        }
        return sortEmployeesDays(mapProject);
    }

    private static List<String> sortEmployeesDays(HashMap<String, HashMap<String, Long>> mapProject) {
        String project;
        String empOneTest = "";
        String empTwoTest = "";
        ;
        long dayEmpOneTest = 0;
        long dayEmpTwoTest = 0;

        String projectFinal = "";
        String empOneFinal = "";
        ;
        String empTwoFinal = "";
        ;
        long dayEmpOneFinal = 0;
        long dayEmpTwoFinal = 0;

        for (Map.Entry<String, HashMap<String, Long>> mapEntry : mapProject.entrySet()) {
            if (mapEntry.getValue().size() == 1) continue;

            project = mapEntry.getKey();
            HashMap<String, Long> mapEmp = mapEntry.getValue();
            Map<String, Long> hm1 = sortByValue(mapEmp);
            int count = 0;
            for (Map.Entry<String, Long> en : hm1.entrySet()) {
                count++;
                if (count == 1) {
                    empOneTest = en.getKey();
                    dayEmpOneTest = en.getValue();
                }
                if (count == 2) {
                    empTwoTest = en.getKey();
                    dayEmpTwoTest = en.getValue();
                    break;
                }
            }
            if (projectFinal.isEmpty()) {
                projectFinal = project;
                empOneFinal = empOneTest;
                empTwoFinal = empTwoTest;
                dayEmpOneFinal = dayEmpOneTest;
                dayEmpTwoFinal = dayEmpTwoTest;
            } else {
                if (dayEmpOneTest + dayEmpTwoTest > dayEmpOneFinal + dayEmpTwoFinal) {
                    projectFinal = project;
                    empOneFinal = empOneTest;
                    empTwoFinal = empTwoTest;
                    dayEmpOneFinal = dayEmpOneTest;
                    dayEmpTwoFinal = dayEmpTwoTest;
                }
            }
        }
        List<String> result = new ArrayList<>();
        result.add(projectFinal);
        result.add(empOneFinal);
        result.add(empTwoFinal);
        result.add(String.valueOf(dayEmpOneFinal));
        result.add(String.valueOf(dayEmpTwoFinal));

        return result;
    }

    private static HashMap<String, Long> sortByValue(HashMap<String, Long> hm) {
        List<Map.Entry<String, Long>> list =
                new LinkedList<Map.Entry<String, Long>>(hm.entrySet());

        Collections.sort(list, new Comparator<Map.Entry<String, Long>>() {
            public int compare(Map.Entry<String, Long> o1,
                               Map.Entry<String, Long> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        HashMap<String, Long> temp = new LinkedHashMap<String, Long>();
        for (Map.Entry<String, Long> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
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
