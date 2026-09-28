package org.example.bank.drone_detection.controller;

import org.example.bank.drone_detection.model.Rooftop;
import org.example.bank.drone_detection.service.RooftopService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class RooftopController {

    private final RooftopService rooftopService;

    public RooftopController(RooftopService rooftopService) {
        this.rooftopService = rooftopService;
    }

    // =========================================================
    // GET ALL ROOFTOPS
    // =========================================================
    @GetMapping("/rooftops")
    public ResponseEntity<List<Rooftop>> getAllRooftops() {

        return ResponseEntity.ok(
                rooftopService.getAllRooftops()
        );
    }

    // =========================================================
    // GET AVAILABLE ROOFTOPS
    // =========================================================
    @GetMapping("/rooftops/available")
    public ResponseEntity<List<Rooftop>> getAvailableRooftops() {

        return ResponseEntity.ok(
                rooftopService.getAvailableRooftops()
        );
    }

    // =========================================================
    // GET ROOFTOP BY ID
    // =========================================================
    @GetMapping("/rooftops/{id}")
    public ResponseEntity<Rooftop> getRooftop(
            @PathVariable int id) {

        Rooftop rooftop =
                rooftopService.getRooftop(id);

        if (rooftop == null) {
            return ResponseEntity
                    .notFound()
                    .build();
        }

        return ResponseEntity.ok(rooftop);
    }

    // =========================================================
    // CREATE ROOFTOP
    // =========================================================
    @PostMapping("/rooftops")
    public ResponseEntity<Rooftop> addRooftop(
            @RequestBody Rooftop rooftop) {

        Rooftop createdRooftop =
                rooftopService.addRooftop(rooftop);

        return ResponseEntity.ok(createdRooftop);
    }

    // =========================================================
    // UPDATE ROOFTOP
    // =========================================================
    @PutMapping("/rooftops/{id}")
    public ResponseEntity<Rooftop> updateRooftop(
            @PathVariable int id,
            @RequestBody Rooftop rooftop) {

        Rooftop updatedRooftop =
                rooftopService.updateRooftop(
                        id,
                        rooftop
                );

        if (updatedRooftop == null) {
            return ResponseEntity
                    .notFound()
                    .build();
        }

        return ResponseEntity.ok(updatedRooftop);
    }

    // =========================================================
    // DELETE ROOFTOP
    // =========================================================
    @DeleteMapping("/rooftops/{id}")
    public ResponseEntity<String> deleteRooftop(
            @PathVariable int id) {

        boolean deleted =
                rooftopService.deleteRooftop(id);

        if (!deleted) {
            return ResponseEntity
                    .notFound()
                    .build();
        }

        return ResponseEntity.ok(
                "Rooftop with ID " + id +
                        " deleted successfully"
        );
    }

    // =========================================================
    // UPDATE ROOFTOP STATUS
    // =========================================================
    @PutMapping("/rooftops/{id}/status")
    public ResponseEntity<Rooftop> updateRooftopStatus(
            @PathVariable int id,
            @RequestParam String status) {

        Rooftop updatedRooftop =
                rooftopService.updateRooftopStatus(
                        id,
                        status
                );

        if (updatedRooftop == null) {
            return ResponseEntity
                    .notFound()
                    .build();
        }

        return ResponseEntity.ok(updatedRooftop);
    }

    // =========================================================
    // UPDATE ROOFTOP CAPACITY
    // =========================================================
    @PutMapping("/rooftops/{id}/capacity")
    public ResponseEntity<Rooftop> updateRooftopCapacity(
            @PathVariable int id,
            @RequestParam int capacity) {

        Rooftop updatedRooftop =
                rooftopService.updateRooftopCapacity(
                        id,
                        capacity
                );

        if (updatedRooftop == null) {
            return ResponseEntity
                    .notFound()
                    .build();
        }

        return ResponseEntity.ok(updatedRooftop);
    }
}