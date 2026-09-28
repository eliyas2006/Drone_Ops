package org.example.bank.drone_detection.service;

import org.example.bank.drone_detection.model.Deployment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AutoDeploymentService {

    private final JdbcTemplate jdbcTemplate;
    private final DeploymentService deploymentService;

    public AutoDeploymentService(
            JdbcTemplate jdbcTemplate,
            DeploymentService deploymentService) {

        this.jdbcTemplate = jdbcTemplate;
        this.deploymentService = deploymentService;
    }

    // =========================================================
    // AUTOMATIC SMART DEPLOYMENT
    // =========================================================

    @Transactional
    public Deployment automaticallyDeploy(int incidentId) {

        // =====================================================
        // 1. FIND INCIDENT
        // =====================================================

        String incidentSql = """
                SELECT id,
                       location,
                       severity,
                       status
                FROM incidents
                WHERE id = ?
                """;

        List<IncidentData> incidents = jdbcTemplate.query(
                incidentSql,
                (rs, rowNum) -> new IncidentData(
                        rs.getInt("id"),
                        rs.getString("location"),
                        rs.getString("severity"),
                        rs.getString("status")
                ),
                incidentId
        );

        if (incidents.isEmpty()) {
            throw new RuntimeException(
                    "Incident not found"
            );
        }

        IncidentData incident = incidents.get(0);

        // =====================================================
        // 2. INCIDENT MUST BE ACTIVE
        // =====================================================

        if (!"ACTIVE".equalsIgnoreCase(incident.status())) {

            throw new RuntimeException(
                    "Incident is not active"
            );
        }

        // =====================================================
        // 3. PREVENT DUPLICATE DEPLOYMENT
        // =====================================================

        String existingDeploymentSql = """
                SELECT COUNT(*)
                FROM deployments
                WHERE incident_id = ?
                  AND status = 'DEPLOYED'
                """;

        Integer existingDeployments =
                jdbcTemplate.queryForObject(
                        existingDeploymentSql,
                        Integer.class,
                        incidentId
                );

        if (existingDeployments != null &&
                existingDeployments > 0) {

            throw new RuntimeException(
                    "Incident already has an active deployment"
            );
        }

        // =====================================================
        // 4. DETERMINE DRONE REQUIREMENTS
        // =====================================================

        int minimumPayload;
        int minimumBattery;

        String severity =
                incident.severity().toUpperCase();

        switch (severity) {

            case "CRITICAL":
                minimumPayload = 12;
                minimumBattery = 50;
                break;

            case "HIGH":
                minimumPayload = 10;
                minimumBattery = 40;
                break;

            case "MEDIUM":
                minimumPayload = 8;
                minimumBattery = 30;
                break;

            default:
                throw new RuntimeException(
                        "Unsupported incident severity: "
                                + incident.severity()
                );
        }

        // =====================================================
        // 5. FIND AVAILABLE ROOFTOP
        // =====================================================

        String rooftopSql = """
                SELECT id
                FROM rooftops
                WHERE location = ?
                  AND status = 'AVAILABLE'
                ORDER BY id
                LIMIT 1
                """;

        List<Integer> rooftops = jdbcTemplate.query(
                rooftopSql,
                (rs, rowNum) ->
                        rs.getInt("id"),
                incident.location()
        );

        if (rooftops.isEmpty()) {

            throw new RuntimeException(
                    "No available rooftop found near incident location"
            );
        }

        int rooftopId = rooftops.get(0);

        // =====================================================
        // 6. SMART DRONE SELECTION
        // =====================================================

        String droneSql = """
                SELECT id
                FROM drones
                WHERE status = 'AVAILABLE'
                  AND payload_capacity >= ?
                  AND battery_level >= ?
                ORDER BY battery_level DESC,
                         payload_capacity DESC
                LIMIT 1
                """;

        List<Integer> drones = jdbcTemplate.query(
                droneSql,
                (rs, rowNum) ->
                        rs.getInt("id"),
                minimumPayload,
                minimumBattery
        );

        if (drones.isEmpty()) {

            throw new RuntimeException(
                    "No suitable drone available for "
                            + severity
                            + " incident"
            );
        }

        int droneId = drones.get(0);

        // =====================================================
        // 7. CREATE DEPLOYMENT
        // =====================================================

        Deployment deployment =
                new Deployment();

        deployment.setIncidentId(incidentId);
        deployment.setDroneId(droneId);
        deployment.setRooftopId(rooftopId);
        deployment.setStatus("DEPLOYED");

        Deployment createdDeployment =
                deploymentService.addDeployment(
                        deployment
                );

        // =====================================================
        // 8. UPDATE DRONE STATUS
        // =====================================================

        String updateDroneSql = """
                UPDATE drones
                SET status = 'DEPLOYED'
                WHERE id = ?
                """;

        jdbcTemplate.update(
                updateDroneSql,
                droneId
        );

        // =====================================================
        // 9. UPDATE ROOFTOP STATUS
        // =====================================================

        String updateRooftopSql = """
                UPDATE rooftops
                SET status = 'OCCUPIED'
                WHERE id = ?
                """;

        jdbcTemplate.update(
                updateRooftopSql,
                rooftopId
        );

        return createdDeployment;
    }

    // =========================================================
    // INCIDENT DATA
    // =========================================================

    private record IncidentData(
            int id,
            String location,
            String severity,
            String status
    ) {
    }
}