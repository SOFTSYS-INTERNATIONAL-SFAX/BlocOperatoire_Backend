package com.tn.softsys.blocoperatoire;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling

@SpringBootApplication
public class BlocOperatoireApplication {

    public static void main(String[] args) {
        SpringApplication.run(BlocOperatoireApplication.class, args);
    }
}
