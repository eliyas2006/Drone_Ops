package org.example.bank.drone_detection.model;

import java.time.LocalDateTime;

public class Deployment {

    private Integer id;
    private Integer incidentId;
    private Integer droneId;
    private Integer rooftopId;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;

    public Deployment() {
    }

    public Deployment(Integer id,
                      Integer incidentId,
                      Integer droneId,
                      Integer rooftopId,
                      String status,
                      LocalDateTime createdAt,
                      LocalDateTime completedAt) {

        this.id = id;
        this.incidentId = incidentId;
        this.droneId = droneId;
        this.rooftopId = rooftopId;
        this.status = status;
        this.createdAt = createdAt;
        this.completedAt = completedAt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getIncidentId() {
        return incidentId;
    }

    public void setIncidentId(Integer incidentId) {
        this.incidentId = incidentId;
    }

    public Integer getDroneId() {
        return droneId;
    }

    public void setDroneId(Integer droneId) {
        this.droneId = droneId;
    }

    public Integer getRooftopId() {
        return rooftopId;
    }

    public void setRooftopId(Integer rooftopId) {
        this.rooftopId = rooftopId;
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

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }
}