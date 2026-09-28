package org.example.bank.drone_detection.service;

import org.example.bank.drone_detection.model.Incident;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class IncidentService {

    private final JdbcTemplate jdbcTemplate;

    public IncidentService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // =========================================================
    // GET ALL INCIDENTS
    // =========================================================
    public List<Incident> getAllIncidents() {

        String sql = """
                SELECT id, location, severity,
                       description, status, created_at
                FROM incidents
                ORDER BY created_at DESC
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new Incident(
                        rs.getInt("id"),
                        rs.getString("location"),
                        rs.getString("severity"),
                        rs.getString("description"),
                        rs.getString("status"),
                        rs.getTimestamp("created_at").toLocalDateTime()
                )
        );
    }

    // =========================================================
    // GET INCIDENT BY ID
    // =========================================================
    public Incident getIncident(int id) {

        String sql = """
                SELECT id, location, severity,
                       description, status, created_at
                FROM incidents
                WHERE id = ?
                """;

        List<Incident> incidents = jdbcTemplate.query(
                sql,
                (rs, rowNum) ->
                        new Incident(
                                rs.getInt("id"),
                                rs.getString("location"),
                                rs.getString("severity"),
                                rs.getString("description"),
                                rs.getString("status"),
                                rs.getTimestamp("created_at").toLocalDateTime()
                        ),
                id
        );

        if (incidents.isEmpty()) {
            return null;
        }

        return incidents.get(0);
    }

    // =========================================================
    // CREATE INCIDENT
    // =========================================================
    public Incident addIncident(Incident incident) {

        String sql = """
                INSERT INTO incidents
                (location, severity, description, status, created_at)
                VALUES (?, ?, ?, ?, NOW())
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {

            PreparedStatement ps = connection.prepareStatement(
                    sql,
                    Statement.RETURN_GENERATED_KEYS
            );

            ps.setString(1, incident.getLocation());
            ps.setString(2, incident.getSeverity());
            ps.setString(3, incident.getDescription());
            ps.setString(4, incident.getStatus());

            return ps;

        }, keyHolder);

        Number generatedId = keyHolder.getKey();

        if (generatedId != null) {
            incident.setId(generatedId.intValue());
        }

        return getIncident(incident.getId());
    }

    // =========================================================
    // UPDATE INCIDENT
    // =========================================================
    public Incident updateIncident(int id, Incident incident) {

        String sql = """
                UPDATE incidents
                SET location = ?,
                    severity = ?,
                    description = ?,
                    status = ?
                WHERE id = ?
                """;

        int rowsUpdated = jdbcTemplate.update(
                sql,
                incident.getLocation(),
                incident.getSeverity(),
                incident.getDescription(),
                incident.getStatus(),
                id
        );

        if (rowsUpdated == 0) {
            return null;
        }

        return getIncident(id);
    }

    // =========================================================
    // DELETE INCIDENT
    // =========================================================
    public boolean deleteIncident(int id) {

        String sql = """
                DELETE FROM incidents
                WHERE id = ?
                """;

        int rowsDeleted = jdbcTemplate.update(sql, id);

        return rowsDeleted > 0;
    }

    // =========================================================
    // GET INCIDENTS BY PRIORITY
    // =========================================================
    public List<Incident> getPriorityIncidents() {

        String sql = """
                SELECT id, location, severity,
                       description, status, created_at
                FROM incidents
                WHERE status = 'ACTIVE'
                ORDER BY
                    CASE severity
                        WHEN 'CRITICAL' THEN 1
                        WHEN 'HIGH' THEN 2
                        WHEN 'MEDIUM' THEN 3
                        WHEN 'LOW' THEN 4
                        ELSE 5
                    END,
                    created_at ASC
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new Incident(
                        rs.getInt("id"),
                        rs.getString("location"),
                        rs.getString("severity"),
                        rs.getString("description"),
                        rs.getString("status"),
                        rs.getTimestamp("created_at").toLocalDateTime()
                )
        );
    }

    // =========================================================
    // INCIDENT STATISTICS
    // =========================================================
    public Map<String, Object> getIncidentStats() {

        String sql = """
                SELECT
                    COUNT(*) AS totalIncidents,

                    SUM(
                        CASE
                            WHEN status = 'ACTIVE' THEN 1
                            ELSE 0
                        END
                    ) AS activeIncidents,

                    SUM(
                        CASE
                            WHEN status = 'RESOLVED' THEN 1
                            ELSE 0
                        END
                    ) AS resolvedIncidents,

                    SUM(
                        CASE
                            WHEN severity = 'CRITICAL' THEN 1
                            ELSE 0
                        END
                    ) AS criticalIncidents,

                    SUM(
                        CASE
                            WHEN severity = 'HIGH' THEN 1
                            ELSE 0
                        END
                    ) AS highIncidents,

                    SUM(
                        CASE
                            WHEN severity = 'MEDIUM' THEN 1
                            ELSE 0
                        END
                    ) AS mediumIncidents,

                    SUM(
                        CASE
                            WHEN severity = 'LOW' THEN 1
                            ELSE 0
                        END
                    ) AS lowIncidents

                FROM incidents
                """;

        return jdbcTemplate.queryForObject(
                sql,
                (rs, rowNum) -> {

                    Map<String, Object> stats = new LinkedHashMap<>();

                    stats.put("totalIncidents",
                            rs.getInt("totalIncidents"));

                    stats.put("activeIncidents",
                            rs.getInt("activeIncidents"));

                    stats.put("resolvedIncidents",
                            rs.getInt("resolvedIncidents"));

                    stats.put("criticalIncidents",
                            rs.getInt("criticalIncidents"));

                    stats.put("highIncidents",
                            rs.getInt("highIncidents"));

                    stats.put("mediumIncidents",
                            rs.getInt("mediumIncidents"));

                    stats.put("lowIncidents",
                            rs.getInt("lowIncidents"));

                    return stats;
                }
        );
    }
}