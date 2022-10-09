package ru.yandex.practicum.filmorate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FilmorateApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(FilmorateApplication.class, args);
        System.out.println("*".repeat(108) + "\n" + "*".repeat(40) + "\t\tСервер запущен.\t\t"
                + "*".repeat(40) + "\n" + "*".repeat(108));
    }
    
}
