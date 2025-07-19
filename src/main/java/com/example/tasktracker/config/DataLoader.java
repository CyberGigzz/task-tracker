package com.example.tasktracker.config;

import com.example.tasktracker.model.*;
import com.example.tasktracker.repository.ProjectRepository;
import com.example.tasktracker.repository.TaskRepository;
import com.example.tasktracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            System.out.println("Loading initial data into the database...");
            loadData();
            System.out.println("Data loading complete.");
        } else {
            System.out.println("Database already contains data. Skipping initial load.");
        }
    }

    private void loadData() {
        User admin = new User("admin@example.com", passwordEncoder.encode("password123"), Role.ADMIN);
        User manager1 = new User("manager.one@example.com", passwordEncoder.encode("password123"), Role.MANAGER);
        User manager2 = new User("manager.two@example.com", passwordEncoder.encode("password123"), Role.MANAGER);
        User devUser = new User("user.dev@example.com", passwordEncoder.encode("password123"), Role.USER);
        User qaUser = new User("user.qa@example.com", passwordEncoder.encode("password123"), Role.USER);

        userRepository.saveAll(List.of(admin, manager1, manager2, devUser, qaUser));

        Project project1 = new Project();
        project1.setName("E-commerce Platform");
        project1.setDescription("Development of a new online shopping website.");
        project1.setOwner(manager1);

        Project project2 = new Project();
        project2.setName("Mobile Banking App");
        project2.setDescription("Creating a native mobile application for banking services.");
        project2.setOwner(manager1);
        
        Project project3 = new Project();
        project3.setName("Data Analytics Dashboard");
        project3.setDescription("A new dashboard for visualizing company metrics.");
        project3.setOwner(manager2);

        projectRepository.saveAll(List.of(project1, project2, project3));

        Task task1 = new Task();
        task1.setTitle("Design User Interface Mockups");
        task1.setDescription("Create wireframes and mockups in Figma.");
        task1.setStatus(TaskStatus.IN_PROGRESS);
        task1.setPriority(TaskPriority.HIGH);
        task1.setDueDate(LocalDate.now().plusWeeks(2));
        task1.setProject(project1);
        task1.setAssignedUser(devUser);

        Task task2 = new Task();
        task2.setTitle("Develop Authentication API");
        task2.setDescription("Implement user login and registration endpoints.");
        task2.setStatus(TaskStatus.TODO);
        task2.setPriority(TaskPriority.HIGH);
        task2.setDueDate(LocalDate.now().plusWeeks(3));
        task2.setProject(project1);
        task2.setAssignedUser(devUser);
        
        Task task3 = new Task();
        task3.setTitle("Write API tests for checkout");
        task3.setDescription("Create integration tests for the payment flow.");
        task3.setStatus(TaskStatus.TODO);
        task3.setPriority(TaskPriority.MEDIUM);
        task3.setDueDate(LocalDate.now().plusMonths(1));
        task3.setProject(project1);
        task3.setAssignedUser(qaUser);
        
        Task task4 = new Task();
        task4.setTitle("Implement Biometric Login");
        task4.setDescription("Add support for fingerprint and Face ID.");
        task4.setStatus(TaskStatus.TODO);
        task4.setPriority(TaskPriority.HIGH);
        task4.setDueDate(LocalDate.now().plusWeeks(4));
        task4.setProject(project2);
        task4.setAssignedUser(devUser);

        taskRepository.saveAll(List.of(task1, task2, task3, task4));
    }
}