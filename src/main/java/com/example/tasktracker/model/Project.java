package com.example.tasktracker.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "projects")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY) 
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createDate;

    @UpdateTimestamp
    private LocalDateTime updateDate;
}