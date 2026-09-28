package org.example.bank.drone_detection.service;

import org.example.bank.drone_detection.model.Drone;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Service
public class DroneService {

    private final JdbcTemplate jdbcTemplate;

    public DroneService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // =========================================================
    // GET ALL DRONES
    // =========================================================
    public List<Drone> getAllDrones() {

        String sql = """
                SELECT id,
                       drone_code,
                       payload_capacity,
                       battery_level,
                       status
                FROM drones
                ORDER BY id
                """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> new Drone(
                        rs.getInt("id"),
                        rs.getString("drone_code"),
                        rs.getDouble("payload_capacity"),
                        rs.getInt("battery_level"),
                        rs.getString("status")
                )
        );
    }

    // =========================================================
    // GET AVAILABLE DRONES
    // =========================================================
    public List<Drone> getAvailableDrones() {

        String sql = """
                SELECT id,
                       drone_code,
                       payload_capacity,
                       battery_level,
                       status
                FROM drones
                WHERE status = ?
                ORDER BY battery_level DESC
                """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> new Drone(
                        rs.getInt("id"),
                        rs.getString("drone_code"),
                        rs.getDouble("payload_capacity"),
                        rs.getInt("battery_level"),
                        rs.getString("status")
                ),
                "AVAILABLE"
        );
    }

    // =========================================================
    // GET DRONE BY ID
    // =========================================================
    public Drone getDrone(int id) {

        String sql = """
                SELECT id,
                       drone_code,
                       payload_capacity,
                       battery_level,
                       status
                FROM drones
                WHERE id = ?
                """;

        List<Drone> drones = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> new Drone(
                        rs.getInt("id"),
                        rs.getString("drone_code"),
                        rs.getDouble("payload_capacity"),
                        rs.getInt("battery_level"),
                        rs.getString("status")
                ),
                id
        );

        if (drones.isEmpty()) {
            return null;
        }

        return drones.get(0);
    }

    // =========================================================
    // CREATE DRONE
    // =========================================================
    public Drone addDrone(Drone drone) {

        String sql = """
                INSERT INTO drones
                (
                    drone_code,
                    payload_capacity,
                    battery_level,
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

            ps.setString(1, drone.getDroneCode());
            ps.setDouble(2, drone.getPayloadCapacity());
            ps.setInt(3, drone.getBatteryLevel());
            ps.setString(4, drone.getStatus());

            return ps;

        }, keyHolder);

        Number generatedId = keyHolder.getKey();

        if (generatedId != null) {
            drone.setId(generatedId.intValue());
        }

        return getDrone(drone.getId());
    }

    // =========================================================
    // UPDATE DRONE
    // =========================================================
    public Drone updateDrone(
            int id,
            Drone drone) {

        String sql = """
                UPDATE drones
                SET drone_code = ?,
                    payload_capacity = ?,
                    battery_level = ?,
                    status = ?
                WHERE id = ?
                """;

        int rowsUpdated = jdbcTemplate.update(
                sql,
                drone.getDroneCode(),
                drone.getPayloadCapacity(),
                drone.getBatteryLevel(),
                drone.getStatus(),
                id
        );

        if (rowsUpdated == 0) {
            return null;
        }

        return getDrone(id);
    }

    // =========================================================
    // DELETE DRONE
    //
    // RETURN VALUES:
    //  1  = deleted successfully
    //  0  = drone not found
    // -1  = drone is used in deployment
    // =========================================================
    public int deleteDrone(int id) {

        // ---------------------------------------------------------
        // STEP 1: Check whether drone exists
        // ---------------------------------------------------------
        Drone drone = getDrone(id);

        if (drone == null) {
            return 0;
        }

        // ---------------------------------------------------------
        // STEP 2: Check whether drone is referenced by deployment
        // ---------------------------------------------------------
        String deploymentCheckSql = """
                SELECT COUNT(*)
                FROM deployments
                WHERE drone_id = ?
                """;

        Integer deploymentCount = jdbcTemplate.queryForObject(
                deploymentCheckSql,
                Integer.class,
                id
        );

        if (deploymentCount != null && deploymentCount > 0) {
            return -1;
        }

        // ---------------------------------------------------------
        // STEP 3: Delete drone
        // ---------------------------------------------------------
        String deleteSql = """
                DELETE FROM drones
                WHERE id = ?
                """;

        int rowsDeleted = jdbcTemplate.update(
                deleteSql,
                id
        );

        return rowsDeleted > 0 ? 1 : 0;
    }

    // =========================================================
    // UPDATE DRONE STATUS
    // =========================================================
    public Drone updateDroneStatus(
            int id,
            String status) {

        String sql = """
                UPDATE drones
                SET status = ?
                WHERE id = ?
                """;

        int rowsUpdated = jdbcTemplate.update(
                sql,
                status,
                id
        );

        if (rowsUpdated == 0) {
            return null;
        }

        return getDrone(id);
    }

    // =========================================================
    // UPDATE BATTERY LEVEL
    // =========================================================
    public Drone updateBatteryLevel(
            int id,
            int batteryLevel) {

        String sql = """
                UPDATE drones
                SET battery_level = ?
                WHERE id = ?
                """;

        int rowsUpdated = jdbcTemplate.update(
                sql,
                batteryLevel,
                id
        );

        if (rowsUpdated == 0) {
            return null;
        }

        return getDrone(id);
    }
}