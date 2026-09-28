package org.example.bank.drone_detection.controller;

import org.example.bank.drone_detection.model.Drone;
import org.example.bank.drone_detection.service.DroneService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class DroneController {

    private final DroneService droneService;

    public DroneController(DroneService droneService) {
        this.droneService = droneService;
    }

    // =========================================================
    // GET ALL DRONES
    // =========================================================
    @GetMapping("/drones")
    public ResponseEntity<List<Drone>> getAllDrones() {

        return ResponseEntity.ok(
                droneService.getAllDrones()
        );
    }

    // =========================================================
    // GET AVAILABLE DRONES
    // =========================================================
    @GetMapping("/drones/available")
    public ResponseEntity<List<Drone>> getAvailableDrones() {

        return ResponseEntity.ok(
                droneService.getAvailableDrones()
        );
    }

    // =========================================================
    // GET DRONE BY ID
    // =========================================================
    @GetMapping("/drones/{id}")
    public ResponseEntity<Drone> getDrone(
            @PathVariable int id) {

        Drone drone = droneService.getDrone(id);

        if (drone == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(drone);
    }

    // =========================================================
    // CREATE DRONE
    // =========================================================
    @PostMapping("/drones")
    public ResponseEntity<Drone> addDrone(
            @RequestBody Drone drone) {

        Drone createdDrone =
                droneService.addDrone(drone);

        return ResponseEntity.ok(createdDrone);
    }

    // =========================================================
    // UPDATE DRONE
    // =========================================================
    @PutMapping("/drones/{id}")
    public ResponseEntity<Drone> updateDrone(
            @PathVariable int id,
            @RequestBody Drone drone) {

        Drone updatedDrone =
                droneService.updateDrone(id, drone);

        if (updatedDrone == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(updatedDrone);
    }

    // =========================================================
    // DELETE DRONE
    // =========================================================
    @DeleteMapping("/drones/{id}")
    public ResponseEntity<String> deleteDrone(
            @PathVariable int id) {

        int result = droneService.deleteDrone(id);

        // ---------------------------------------------------------
        // Drone does not exist
        // ---------------------------------------------------------
        if (result == 0) {

            return ResponseEntity
                    .notFound()
                    .build();
        }

        // ---------------------------------------------------------
        // Drone is referenced by deployment
        // ---------------------------------------------------------
        if (result == -1) {

            return ResponseEntity
                    .status(409)
                    .body(
                            "Drone with ID " + id +
                                    " cannot be deleted because it is used in a deployment"
                    );
        }

        // ---------------------------------------------------------
        // Successfully deleted
        // ---------------------------------------------------------
        return ResponseEntity.ok(
                "Drone with ID " + id +
                        " deleted successfully"
        );
    }

    // =========================================================
    // UPDATE DRONE STATUS
    // =========================================================
    @PutMapping("/drones/{id}/status")
    public ResponseEntity<Drone> updateDroneStatus(
            @PathVariable int id,
            @RequestParam String status) {

        Drone updatedDrone =
                droneService.updateDroneStatus(
                        id,
                        status
                );

        if (updatedDrone == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(updatedDrone);
    }

    // =========================================================
    // UPDATE BATTERY
    // =========================================================
    @PutMapping("/drones/{id}/battery")
    public ResponseEntity<Drone> updateBatteryLevel(
            @PathVariable int id,
            @RequestParam int batteryLevel) {

        Drone updatedDrone =
                droneService.updateBatteryLevel(
                        id,
                        batteryLevel
                );

        if (updatedDrone == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(updatedDrone);
    }
}