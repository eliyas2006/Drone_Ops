package org.example.bank.drone_detection.model;

public class Drone {

    private Integer id;
    private String droneCode;
    private double payloadCapacity;
    private int batteryLevel;
    private String status;

    // Default constructor
    public Drone() {
    }

    // Constructor used when reading from database
    public Drone(Integer id,
                 String droneCode,
                 double payloadCapacity,
                 int batteryLevel,
                 String status) {

        this.id = id;
        this.droneCode = droneCode;
        this.payloadCapacity = payloadCapacity;
        this.batteryLevel = batteryLevel;
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDroneCode() {
        return droneCode;
    }

    public void setDroneCode(String droneCode) {
        this.droneCode = droneCode;
    }

    public double getPayloadCapacity() {
        return payloadCapacity;
    }

    public void setPayloadCapacity(double payloadCapacity) {
        this.payloadCapacity = payloadCapacity;
    }

    public int getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(int batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}