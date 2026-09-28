package org.example.bank.drone_detection.service;

import org.example.bank.drone_detection.model.Deployment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;

@Service
public class DeploymentService {

    private final JdbcTemplate jdbcTemplate;

    public DeploymentService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // =========================================================
    // GET ALL DEPLOYMENTS
    // =========================================================

    public List<Deployment> getAllDeployments() {

        String sql = """
                SELECT id,
                       incident_id,
                       drone_id,
                       rooftop_id,
                       status,
                       created_at,
                       completed_at
                FROM deployments
                ORDER BY created_at DESC
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new Deployment(
                        rs.getInt("id"),
                        rs.getInt("incident_id"),
                        rs.getInt("drone_id"),
                        rs.getInt("rooftop_id"),
                        rs.getString("status"),
                        rs.getTimestamp("created_at")
                                .toLocalDateTime(),
                        rs.getTimestamp("completed_at") != null
                                ? rs.getTimestamp("completed_at")
                                .toLocalDateTime()
                                : null
                )
        );
    }

    // =========================================================
    // GET DEPLOYMENT BY ID
    // =========================================================

    public Deployment getDeployment(int id) {

        String sql = """
                SELECT id,
                       incident_id,
                       drone_id,
                       rooftop_id,
                       status,
                       created_at,
                       completed_at
                FROM deployments
                WHERE id = ?
                """;

        List<Deployment> deployments = jdbcTemplate.query(
                sql,
                (rs, rowNum) ->
                        new Deployment(
                                rs.getInt("id"),
                                rs.getInt("incident_id"),
                                rs.getInt("drone_id"),
                                rs.getInt("rooftop_id"),
                                rs.getString("status"),
                                rs.getTimestamp("created_at")
                                        .toLocalDateTime(),
                                rs.getTimestamp("completed_at") != null
                                        ? rs.getTimestamp("completed_at")
                                        .toLocalDateTime()
                                        : null
                        ),
                id
        );

        if (deployments.isEmpty()) {
            return null;
        }

        return deployments.get(0);
    }

    // =========================================================
    // CREATE DEPLOYMENT
    // =========================================================

    public Deployment addDeployment(Deployment deployment) {

        String sql = """
                INSERT INTO deployments
                (incident_id, drone_id, rooftop_id, status, created_at)
                VALUES (?, ?, ?, ?, NOW())
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {

            PreparedStatement ps = connection.prepareStatement(
                    sql,
                    Statement.RETURN_GENERATED_KEYS
            );

            ps.setInt(1, deployment.getIncidentId());
            ps.setInt(2, deployment.getDroneId());
            ps.setInt(3, deployment.getRooftopId());
            ps.setString(4, deployment.getStatus());

            return ps;

        }, keyHolder);

        Number generatedId = keyHolder.getKey();

        if (generatedId != null) {
            deployment.setId(generatedId.intValue());
        }

        return getDeployment(deployment.getId());
    }

    // =========================================================
    // UPDATE DEPLOYMENT
    // =========================================================

    public Deployment updateDeployment(
            int id,
            Deployment deployment) {

        String sql = """
                UPDATE deployments
                SET incident_id = ?,
                    drone_id = ?,
                    rooftop_id = ?,
                    status = ?,
                    completed_at = ?
                WHERE id = ?
                """;

        Timestamp completedTimestamp =
                deployment.getCompletedAt() != null
                        ? Timestamp.valueOf(
                        deployment.getCompletedAt())
                        : null;

        int rowsUpdated = jdbcTemplate.update(
                sql,
                deployment.getIncidentId(),
                deployment.getDroneId(),
                deployment.getRooftopId(),
                deployment.getStatus(),
                completedTimestamp,
                id
        );

        if (rowsUpdated == 0) {
            return null;
        }

        return getDeployment(id);
    }

    // =========================================================
    // DELETE DEPLOYMENT
    // =========================================================

    public boolean deleteDeployment(int id) {

        String sql = """
                DELETE FROM deployments
                WHERE id = ?
                """;

        int rowsDeleted = jdbcTemplate.update(sql, id);

        return rowsDeleted > 0;
    }
}
