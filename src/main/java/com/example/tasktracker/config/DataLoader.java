package com.example.tasktracker.config;

import com.example.tasktracker.model.*;
import com.example.tasktracker.repository.ProjectRepository;
import com.example.tasktracker.repository.TaskRepository;
import com.example.tasktracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final PasswordEncoder passwordEncoder;

    private static final Random RANDOM = new Random();

    private static final int NUM_MANAGERS = 2;
    private static final int NUM_USERS = 5;
    private static final int PROJECTS_PER_MANAGER = 3;
    private static final int TASKS_PER_PROJECT_MIN = 2;
    private static final int TASKS_PER_PROJECT_MAX = 5;

    @Override
    @Transactional 
    public void run(String... args) throws Exception {
        if (userRepository.count() > 0) {
            log.info("Database already contains data. Skipping initial data load.");
            return;
        }

        log.info("Loading initial sample data...");

        List<User> managers = createAndSaveManagers();
        List<User> users = createAndSaveUsers();
        List<Project> projects = createAndSaveProjects(managers);
        createAndSaveTasks(projects, users);

        log.info("Sample data loading complete.");
    }

    private List<User> createAndSaveManagers() {
        List<User> managers = new ArrayList<>();
        String hashedPassword = passwordEncoder.encode("password123");

        User admin = new User("admin@example.com", hashedPassword, Role.ADMIN);
        managers.add(admin);

        for (int i = 1; i <= NUM_MANAGERS; i++) {
            managers.add(new User("manager" + i + "@example.com", hashedPassword, Role.MANAGER));
        }
        return userRepository.saveAll(managers);
    }

    private List<User> createAndSaveUsers() {
        List<User> users = new ArrayList<>();
        String hashedPassword = passwordEncoder.encode("password123");
        List<String> userRoles = List.of("dev", "qa", "designer", "analyst", "scrum");

        for (int i = 0; i < NUM_USERS; i++) {
            String roleName = userRoles.get(i % userRoles.size());
            users.add(new User(roleName + (i+1) + "@example.com", hashedPassword, Role.USER));
        }
        return userRepository.saveAll(users);
    }

    private List<Project> createAndSaveProjects(List<User> managers) {
        List<Project> projects = new ArrayList<>();
        List<String> projectNouns = List.of("Phoenix", "Odyssey", "Titan", "Nova", "Orion");
        List<String> projectTypes = List.of("Initiative", "Platform", "Overhaul", "Migration", "Framework");

        List<User> actualManagers = managers.stream().filter(u -> u.getRole() == Role.MANAGER).toList();

        for (User manager : actualManagers) {
            for (int i = 0; i < PROJECTS_PER_MANAGER; i++) {
                Project project = new Project();
                String name = "Project " + projectNouns.get(RANDOM.nextInt(projectNouns.size()))
                        + " " + projectTypes.get(RANDOM.nextInt(projectTypes.size()));
                project.setName(name);
                project.setDescription("This is the description for " + name);
                project.setOwner(manager);
                projects.add(project);
            }
        }
        return projectRepository.saveAll(projects);
    }

    private void createAndSaveTasks(List<Project> projects, List<User> users) {
        List<Task> tasks = new ArrayList<>();
        List<String> verbs = List.of("Implement", "Design", "Refactor", "Test", "Deploy", "Document");
        List<String> nouns = List.of("Login Page", "Database Schema", "API Gateway", "CI/CD Pipeline", "User Guide");

        for (Project project : projects) {
            int numTasks = RANDOM.nextInt(TASKS_PER_PROJECT_MAX - TASKS_PER_PROJECT_MIN + 1) + TASKS_PER_PROJECT_MIN;
            for (int i = 0; i < numTasks; i++) {
                Task task = new Task();
                task.setTitle(verbs.get(RANDOM.nextInt(verbs.size())) + " the " + nouns.get(RANDOM.nextInt(nouns.size())));
                task.setDescription("Detailed description for this task goes here.");
                task.setStatus(TaskStatus.values()[RANDOM.nextInt(TaskStatus.values().length)]);
                task.setPriority(TaskPriority.values()[RANDOM.nextInt(TaskPriority.values().length)]);
                task.setDueDate(LocalDate.now().plusDays(RANDOM.nextInt(30) - 7)); 
                task.setProject(project);
                task.setAssignedUser(users.get(RANDOM.nextInt(users.size()))); 
                tasks.add(task);
            }
        }
        taskRepository.saveAll(tasks);
    }
}