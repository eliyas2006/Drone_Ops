    package org.example.bank.drone_detection.controller;

    import org.example.bank.drone_detection.model.Deployment;
    import org.example.bank.drone_detection.service.AutoDeploymentService;
    import org.example.bank.drone_detection.service.DeploymentCompletionService;
    import org.example.bank.drone_detection.service.DeploymentService;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;

    import java.util.List;

    @RestController
    public class DeploymentController {

        private final DeploymentService deploymentService;
        private final AutoDeploymentService autoDeploymentService;
        private final DeploymentCompletionService
                deploymentCompletionService;

        public DeploymentController(
                DeploymentService deploymentService,
                AutoDeploymentService autoDeploymentService,
                DeploymentCompletionService deploymentCompletionService) {

            this.deploymentService = deploymentService;
            this.autoDeploymentService = autoDeploymentService;
            this.deploymentCompletionService =
                    deploymentCompletionService;
        }

        // =========================================================
        // GET ALL DEPLOYMENTS
        // =========================================================

        @GetMapping("/deployments")
        public ResponseEntity<List<Deployment>> getAllDeployments() {

            return ResponseEntity.ok(
                    deploymentService.getAllDeployments()
            );
        }

        // =========================================================
        // GET DEPLOYMENT BY ID
        // =========================================================

        @GetMapping("/deployments/{id}")
        public ResponseEntity<Deployment> getDeployment(
                @PathVariable int id) {

            Deployment deployment =
                    deploymentService.getDeployment(id);

            if (deployment == null) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(deployment);
        }

        // =========================================================
        // CREATE DEPLOYMENT
        // =========================================================

        @PostMapping("/deployments")
        public ResponseEntity<Deployment> addDeployment(
                @RequestBody Deployment deployment) {

            Deployment createdDeployment =
                    deploymentService.addDeployment(
                            deployment
                    );

            return ResponseEntity.ok(createdDeployment);
        }

        // =========================================================
        // UPDATE DEPLOYMENT
        // =========================================================

        @PutMapping("/deployments/{id}")
        public ResponseEntity<Deployment> updateDeployment(
                @PathVariable int id,
                @RequestBody Deployment deployment) {

            Deployment updatedDeployment =
                    deploymentService.updateDeployment(
                            id,
                            deployment
                    );

            if (updatedDeployment == null) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(updatedDeployment);
        }

        // =========================================================
        // DELETE DEPLOYMENT
        // =========================================================

        @DeleteMapping("/deployments/{id}")
        public ResponseEntity<String> deleteDeployment(
                @PathVariable int id) {

            boolean deleted =
                    deploymentService.deleteDeployment(id);

            if (!deleted) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(
                    "Deployment with ID " + id +
                            " deleted successfully"
            );
        }

        // =========================================================
        // AUTOMATIC DEPLOYMENT
        // =========================================================

        @PostMapping("/deployments/auto/{incidentId}")
        public ResponseEntity<?> automaticallyDeploy(
                @PathVariable int incidentId) {

            try {

                Deployment deployment =
                        autoDeploymentService
                                .automaticallyDeploy(
                                        incidentId
                                );

                return ResponseEntity.ok(deployment);

            } catch (RuntimeException e) {

                return ResponseEntity
                        .badRequest()
                        .body(e.getMessage());
            }
        }

        // =========================================================
        // COMPLETE DEPLOYMENT
        // =========================================================

        @PutMapping("/deployments/{id}/complete")
        public ResponseEntity<?> completeDeployment(
                @PathVariable int id) {

            try {

                Deployment deployment =
                        deploymentCompletionService
                                .completeDeployment(id);

                return ResponseEntity.ok(deployment);

            } catch (RuntimeException e) {

                return ResponseEntity
                        .badRequest()
                        .body(e.getMessage());
            }
        }
    }