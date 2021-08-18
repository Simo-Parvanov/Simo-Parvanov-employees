package com.example.simoparvanovemployees.web;

import com.example.simoparvanovemployees.service.EmployeesService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class HomeController {
    private final EmployeesService employeesService;
    public static String uploadDir = System.getProperty("user.dir")+"/uploads";

    public HomeController(EmployeesService employeesService) {
        this.employeesService = employeesService;
    }


    @RequestMapping("/")
    public String getHome(Model model){
        return "home";
    }

    @RequestMapping("/upload")
    public String uploadFile(Model model, @RequestPart("files")MultipartFile[] files){
        StringBuilder fileName = new StringBuilder();
        for (MultipartFile file : files) {
            Path fileNameAndPath = Paths.get(uploadDir, file.getOriginalFilename());
            fileName.append(file.getOriginalFilename());
            try {
                Files.write(fileNameAndPath, file.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        model.addAttribute("bestEmployees", employeesService.getBestEmployees(uploadDir + "/" + fileName.toString()));
        return "table";
    }
}
