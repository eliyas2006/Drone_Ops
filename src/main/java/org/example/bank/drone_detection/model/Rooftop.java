package org.example.bank.drone_detection.model;

public class Rooftop {

    private Integer id;
    private String buildingName;
    private String location;
    private int capacity;
    private String status;

    // Default constructor
    public Rooftop() {
    }

    // Constructor
    public Rooftop(Integer id,
                   String buildingName,
                   String location,
                   int capacity,
                   String status) {

        this.id = id;
        this.buildingName = buildingName;
        this.location = location;
        this.capacity = capacity;
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBuildingName() {
        return buildingName;
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}