package org.example.bank.drone_detection.model;

import java.time.LocalDateTime;

public class Incident {

    private Integer id;
    private String location;
    private String severity;
    private String description;
    private String status;
    private LocalDateTime createdAt;

    // Default constructor
    public Incident() {
    }

    // Constructor
    public Incident(Integer id,
                    String location,
                    String severity,
                    String description,
                    String status,
                    LocalDateTime createdAt) {

        this.id = id;
        this.location = location;
        this.severity = severity;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}