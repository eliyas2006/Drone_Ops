package org.example.bank.drone_detection.service;

import org.example.bank.drone_detection.model.Rooftop;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Service
public class RooftopService {

    private final JdbcTemplate jdbcTemplate;

    public RooftopService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // =========================================================
    // GET ALL ROOFTOPS
    // =========================================================
    public List<Rooftop> getAllRooftops() {

        String sql = """
                SELECT id,
                       building_name,
                       location,
                       capacity,
                       status
                FROM rooftops
                ORDER BY id
                """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> new Rooftop(
                        rs.getInt("id"),
                        rs.getString("building_name"),
                        rs.getString("location"),
                        rs.getInt("capacity"),
                        rs.getString("status")
                )
        );
    }

    // =========================================================
    // GET AVAILABLE ROOFTOPS
    // =========================================================
    public List<Rooftop> getAvailableRooftops() {

        String sql = """
                SELECT id,
                       building_name,
                       location,
                       capacity,
                       status
                FROM rooftops
                WHERE status = ?
                ORDER BY capacity DESC
                """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> new Rooftop(
                        rs.getInt("id"),
                        rs.getString("building_name"),
                        rs.getString("location"),
                        rs.getInt("capacity"),
                        rs.getString("status")
                ),
                "AVAILABLE"
        );
    }

    // =========================================================
    // GET ROOFTOP BY ID
    // =========================================================
    public Rooftop getRooftop(int id) {

        String sql = """
                SELECT id,
                       building_name,
                       location,
                       capacity,
                       status
                FROM rooftops
                WHERE id = ?
                """;

        List<Rooftop> rooftops = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> new Rooftop(
                        rs.getInt("id"),
                        rs.getString("building_name"),
                        rs.getString("location"),
                        rs.getInt("capacity"),
                        rs.getString("status")
                ),
                id
        );

        if (rooftops.isEmpty()) {
            return null;
        }

        return rooftops.get(0);
    }

    // =========================================================
    // CREATE ROOFTOP
    // =========================================================
    public Rooftop addRooftop(Rooftop rooftop) {

        String sql = """
                INSERT INTO rooftops
                (
                    building_name,
                    location,
                    capacity,
                    status
                )
                VALUES (?, ?, ?, ?)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {

            PreparedStatement ps =
                    connection.prepareStatement(
                            sql,
                            Statement.RETURN_GENERATED_KEYS
                    );

            ps.setString(1, rooftop.getBuildingName());
            ps.setString(2, rooftop.getLocation());
            ps.setInt(3, rooftop.getCapacity());
            ps.setString(4, rooftop.getStatus());

            return ps;

        }, keyHolder);

        Number generatedId = keyHolder.getKey();

        if (generatedId != null) {
            rooftop.setId(generatedId.intValue());
        }

        return getRooftop(rooftop.getId());
    }

    // =========================================================
    // UPDATE ROOFTOP
    // =========================================================
    public Rooftop updateRooftop(
            int id,
            Rooftop rooftop) {

        String sql = """
                UPDATE rooftops
                SET building_name = ?,
                    location = ?,
                    capacity = ?,
                    status = ?
                WHERE id = ?
                """;

        int rowsUpdated = jdbcTemplate.update(
                sql,
                rooftop.getBuildingName(),
                rooftop.getLocation(),
                rooftop.getCapacity(),
                rooftop.getStatus(),
                id
        );

        if (rowsUpdated == 0) {
            return null;
        }

        return getRooftop(id);
    }

    // =========================================================
    // DELETE ROOFTOP
    // =========================================================
    public boolean deleteRooftop(int id) {

        String sql = """
                DELETE FROM rooftops
                WHERE id = ?
                """;

        int rowsDeleted =
                jdbcTemplate.update(sql, id);

        return rowsDeleted > 0;
    }

    // =========================================================
    // UPDATE ROOFTOP STATUS
    // =========================================================
    public Rooftop updateRooftopStatus(
            int id,
            String status) {

        String sql = """
                UPDATE rooftops
                SET status = ?
                WHERE id = ?
                """;

        int rowsUpdated =
                jdbcTemplate.update(
                        sql,
                        status,
                        id
                );

        if (rowsUpdated == 0) {
            return null;
        }

        return getRooftop(id);
    }

    // =========================================================
    // UPDATE ROOFTOP CAPACITY
    // =========================================================
    public Rooftop updateRooftopCapacity(
            int id,
            int capacity) {

        String sql = """
                UPDATE rooftops
                SET capacity = ?
                WHERE id = ?
                """;

        int rowsUpdated =
                jdbcTemplate.update(
                        sql,
                        capacity,
                        id
                );

        if (rowsUpdated == 0) {
            return null;
        }

        return getRooftop(id);
    }
}