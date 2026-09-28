package org.example.bank.drone_detection.controller;

import org.example.bank.drone_detection.model.Incident;
import org.example.bank.drone_detection.service.IncidentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class IncidentController {

    private final IncidentService incidentService;

    public IncidentController(IncidentService incidentService) {
        this.incidentService = incidentService;
    }

    // =========================================================
    // GET ALL INCIDENTS
    // =========================================================
    @GetMapping("/incidents")
    public ResponseEntity<List<Incident>> getAllIncidents() {

        return ResponseEntity.ok(
                incidentService.getAllIncidents()
        );
    }

    // =========================================================
    // GET INCIDENT BY ID
    // =========================================================
    @GetMapping("/incidents/{id}")
    public ResponseEntity<Incident> getIncident(
            @PathVariable int id) {

        Incident incident =
                incidentService.getIncident(id);

        if (incident == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(incident);
    }

    // =========================================================
    // CREATE INCIDENT
    // =========================================================
    @PostMapping("/incidents")
    public ResponseEntity<Incident> addIncident(
            @RequestBody Incident incident) {

        Incident createdIncident =
                incidentService.addIncident(incident);

        return ResponseEntity.ok(createdIncident);
    }

    // =========================================================
    // UPDATE INCIDENT
    // =========================================================
    @PutMapping("/incidents/{id}")
    public ResponseEntity<Incident> updateIncident(
            @PathVariable int id,
            @RequestBody Incident incident) {

        Incident updatedIncident =
                incidentService.updateIncident(id, incident);

        if (updatedIncident == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(updatedIncident);
    }

    // =========================================================
    // DELETE INCIDENT
    // =========================================================
    @DeleteMapping("/incidents/{id}")
    public ResponseEntity<String> deleteIncident(
            @PathVariable int id) {

        boolean deleted =
                incidentService.deleteIncident(id);

        if (!deleted) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(
                "Incident with ID " + id +
                        " deleted successfully"
        );
    }

    // =========================================================
    // GET INCIDENTS BY PRIORITY
    // =========================================================
    @GetMapping("/incidents/priority")
    public ResponseEntity<List<Incident>> getPriorityIncidents() {

        return ResponseEntity.ok(
                incidentService.getPriorityIncidents()
        );
    }

    // =========================================================
    // GET INCIDENT STATISTICS
    // =========================================================
    @GetMapping("/incidents/stats")
    public ResponseEntity<Map<String, Object>> getIncidentStats() {

        return ResponseEntity.ok(
                incidentService.getIncidentStats()
        );
    }
}