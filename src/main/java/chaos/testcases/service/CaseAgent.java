package chaos.testcases.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.Executors;

@SpringBootApplication
public class CaseAgent {
    public static void main(String[] args) {
        SpringApplication.run(CaseAgent.class, args);
    }
}
