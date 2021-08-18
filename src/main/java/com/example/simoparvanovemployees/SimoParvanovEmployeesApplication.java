package com.example.simoparvanovemployees;

import com.example.simoparvanovemployees.web.HomeController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;

@SpringBootApplication
public class SimoParvanovEmployeesApplication {

    public static void main(String[] args) {
        new File(HomeController.uploadDir).mkdir();
        SpringApplication.run(SimoParvanovEmployeesApplication.class, args);
    }

}
