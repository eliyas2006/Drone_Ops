package org.example.bank.drone_detection.service;

import org.example.bank.drone_detection.model.Deployment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeploymentCompletionService {

    private final JdbcTemplate jdbcTemplate;
    private final DeploymentService deploymentService;

    public DeploymentCompletionService(
            JdbcTemplate jdbcTemplate,
            DeploymentService deploymentService) {

        this.jdbcTemplate = jdbcTemplate;
        this.deploymentService = deploymentService;
    }

    // =========================================================
    // COMPLETE DEPLOYMENT
    // =========================================================

    @Transactional
    public Deployment completeDeployment(int deploymentId) {

        // =====================================================
        // 1. CHECK DEPLOYMENT
        // =====================================================

        String checkSql = """
                SELECT id,
                       incident_id,
                       drone_id,
                       rooftop_id,
                       status
                FROM deployments
                WHERE id = ?
                """;

        DeploymentData deployment =
                jdbcTemplate.query(
                                checkSql,
                                (rs, rowNum) -> new DeploymentData(
                                        rs.getInt("id"),
                                        rs.getInt("incident_id"),
                                        rs.getInt("drone_id"),
                                        rs.getInt("rooftop_id"),
                                        rs.getString("status")
                                ),
                                deploymentId
                        )
                        .stream()
                        .findFirst()
                        .orElse(null);

        if (deployment == null) {
            throw new RuntimeException(
                    "Deployment not found"
            );
        }

        // =====================================================
        // 2. CHECK DEPLOYMENT STATUS
        // =====================================================

        if (!"DEPLOYED".equalsIgnoreCase(
                deployment.status())) {

            throw new RuntimeException(
                    "Only a DEPLOYED operation can be completed"
            );
        }

        // =====================================================
        // 3. MARK DEPLOYMENT AS COMPLETED
        // =====================================================

        String updateDeploymentSql = """
                UPDATE deployments
                SET status = 'COMPLETED',
                    completed_at = CURRENT_TIMESTAMP
                WHERE id = ?
                """;

        jdbcTemplate.update(
                updateDeploymentSql,
                deploymentId
        );

        // =====================================================
        // 4. RELEASE DRONE
        // =====================================================

        String updateDroneSql = """
                UPDATE drones
                SET status = 'AVAILABLE'
                WHERE id = ?
                """;

        jdbcTemplate.update(
                updateDroneSql,
                deployment.droneId()
        );

        // =====================================================
        // 5. RELEASE ROOFTOP
        // =====================================================

        String updateRooftopSql = """
                UPDATE rooftops
                SET status = 'AVAILABLE'
                WHERE id = ?
                """;

        jdbcTemplate.update(
                updateRooftopSql,
                deployment.rooftopId()
        );

        // =====================================================
        // 6. RESOLVE INCIDENT
        // =====================================================

        String updateIncidentSql = """
                UPDATE incidents
                SET status = 'RESOLVED'
                WHERE id = ?
                """;

        jdbcTemplate.update(
                updateIncidentSql,
                deployment.incidentId()
        );

        // =====================================================
        // 7. RETURN UPDATED DEPLOYMENT
        // =====================================================

        return deploymentService.getDeployment(
                deploymentId
        );
    }

    // =========================================================
    // INTERNAL DEPLOYMENT DATA
    // =========================================================

    private record DeploymentData(
            int id,
            int incidentId,
            int droneId,
            int rooftopId,
            String status
    ) {
    }
}