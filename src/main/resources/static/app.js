/* =========================================================
   DRONEOPS FRONTEND
========================================================= */

const API_BASE = "";

let incidents = [];
let drones = [];
let rooftops = [];
let deployments = [];

let currentEditingIncident = null;


/* =========================================================.
   INITIALIZATION
========================================================= */

document.addEventListener("DOMContentLoaded", () => {

    setupNavigation();

    setupButtons();

    setupIncidentFilters();

    setupIncidentModal();

    loadAllData();

});


/* =========================================================
   API HELPER
========================================================= */

async function apiRequest(url, options = {}) {

    const response = await fetch(
        `${API_BASE}${url}`,
        {
            ...options,
            headers: {
                "Content-Type": "application/json",
                ...(options.headers || {})
            }
        }
    );

    if (!response.ok) {

        let message = "Request failed";

        try {

            message = await response.text();

        } catch (error) {

            console.error(error);

        }

        throw new Error(message);

    }

    const contentType =
        response.headers.get("content-type");

    if (
        contentType &&
        contentType.includes("application/json")
    ) {

        return await response.json();

    }

    return await response.text();

}


/* =========================================================
   LOAD ALL DATA
========================================================= */

async function loadAllData() {

    setConnection(false);

    try {

        const results = await Promise.all([

            apiRequest("/incidents"),

            apiRequest("/drones"),

            apiRequest("/rooftops"),

            apiRequest("/deployments")

        ]);

        incidents = results[0] || [];

        drones = results[1] || [];

        rooftops = results[2] || [];

        deployments = results[3] || [];

        setConnection(true);

        updateDashboard();

        renderIncidents();

        renderDrones();

        renderRooftops();

        renderDeployments();

    } catch (error) {

        console.error("API error:", error);

        setConnection(false);

        showToast(
            "Unable to connect to Spring Boot backend"
        );

    }

}


/* =========================================================
   CONNECTION STATUS
========================================================= */

function setConnection(online) {

    const dot =
        document.getElementById("connectionDot");

    const text =
        document.getElementById("connectionText");

    if (online) {

        dot.classList.add("online");

        text.textContent = "Connected";

    } else {

        dot.classList.remove("online");

        text.textContent = "Connecting...";

    }

}


/* =========================================================
   NAVIGATION
========================================================= */

function setupNavigation() {

    document.querySelectorAll(
        "[data-page]"
    ).forEach(button => {

        button.addEventListener(
            "click",
            () => {

                const page =
                    button.dataset.page;

                navigateTo(page);

            }
        );

    });

}


function navigateTo(page) {

    document.querySelectorAll(
        ".page"
    ).forEach(section => {

        section.classList.add("hidden");

    });

    const target =
        document.getElementById(
            `${page}Page`
        );

    if (target) {

        target.classList.remove("hidden");

    }

    document.querySelectorAll(
        ".nav-item"
    ).forEach(item => {

        item.classList.remove("active");

        if (
            item.dataset.page === page
        ) {

            item.classList.add("active");

        }

    });

}


/* =========================================================
   BUTTONS
========================================================= */

function setupButtons() {

    document
        .getElementById("refreshButton")
        .addEventListener(
            "click",
            loadAllData
        );


    document
        .getElementById("incidentRefreshButton")
        .addEventListener(
            "click",
            loadAllData
        );


    document
        .getElementById("dashboardReportButton")
        .addEventListener(
            "click",
            openCreateIncidentModal
        );


    document
        .getElementById("reportIncidentButton")
        .addEventListener(
            "click",
            openCreateIncidentModal
        );

}


/* =========================================================
   DASHBOARD
========================================================= */

function updateDashboard() {

    const activeIncidents =
        incidents.filter(
            incident =>
                incident.status === "ACTIVE"
        );

    const availableDrones =
        drones.filter(
            drone =>
                drone.status === "AVAILABLE"
        );

    const availableRooftops =
        rooftops.filter(
            rooftop =>
                rooftop.status === "AVAILABLE"
        );

    document.getElementById(
        "dashboardIncidents"
    ).textContent =
        activeIncidents.length;

    document.getElementById(
        "dashboardDrones"
    ).textContent =
        availableDrones.length;

    document.getElementById(
        "dashboardRooftops"
    ).textContent =
        availableRooftops.length;

    document.getElementById(
        "dashboardDeployments"
    ).textContent =
        deployments.length;

    document.getElementById(
        "sidebarIncidentCount"
    ).textContent =
        activeIncidents.length;


    renderDashboardIncidents(
        activeIncidents
    );

    renderDashboardDrones(
        availableDrones
    );

    renderDashboardDeployments();

}


/* =========================================================
   DASHBOARD INCIDENTS
========================================================= */

function renderDashboardIncidents(data) {

    const container =
        document.getElementById(
            "dashboardIncidentList"
        );

    if (!data.length) {

        container.innerHTML = `
            <div class="empty-state">

                <div class="empty-state-icon">
                    ✅
                </div>

                <div class="empty-state-title">
                    No active emergencies
                </div>

                <div class="empty-state-text">
                    All emergency situations are currently under control.
                </div>

            </div>
        `;

        return;

    }

    container.innerHTML =
        data
            .slice(0, 5)
            .map(incident => `

                <div class="incident-row">

                    <div>

                        <strong>
                            ${escapeHtml(
                incident.location
            )}
                        </strong>

                        <div class="small-text">
                            ${escapeHtml(
                incident.description
            )}
                        </div>

                    </div>

                    ${severityBadge(
                incident.severity
            )}

                </div>

            `)
            .join("");

}


/* =========================================================
   DASHBOARD DRONES
========================================================= */

function renderDashboardDrones(data) {

    const container =
        document.getElementById(
            "dashboardDroneList"
        );

    if (!data.length) {

        container.innerHTML = `
            <div class="empty-state">

                <div class="empty-state-icon">
                    🚁
                </div>

                <div class="empty-state-title">
                    No drones available
                </div>

                <div class="empty-state-text">
                    All drones are currently deployed.
                </div>

            </div>
        `;

        return;

    }

    container.innerHTML =
        data
            .slice(0, 5)
            .map(drone => `

                <div class="incident-row">

                    <div>

                        <strong>
                            ${escapeHtml(
                drone.droneCode
            )}
                        </strong>

                        <div class="small-text">
                            Payload:
                            ${drone.payloadCapacity}
                        </div>

                    </div>

                    <span class="badge badge-resolved">

                        ${drone.batteryLevel}% Battery

                    </span>

                </div>

            `)
            .join("");

}


/* =========================================================
   DASHBOARD DEPLOYMENTS
========================================================= */

function renderDashboardDeployments() {

    const tbody =
        document.getElementById(
            "dashboardDeploymentTable"
        );

    const data =
        deployments.slice(0, 5);

    if (!data.length) {

        tbody.innerHTML = `
            <tr>

                <td colspan="5">

                    <div class="empty-state">
                        No deployments recorded.
                    </div>

                </td>

            </tr>
        `;

        return;

    }

    tbody.innerHTML =
        data.map(deployment => `

            <tr>

                <td>
                    #${deployment.id}
                </td>

                <td>
                    Incident #${deployment.incidentId}
                </td>

                <td>
                    Drone #${deployment.droneId}
                </td>

                <td>
                    Rooftop #${deployment.rooftopId}
                </td>

                <td>
                    ${statusBadge(
            deployment.status
        )}
                </td>

            </tr>

        `).join("");

}


/* =========================================================
   INCIDENTS
========================================================= */

function renderIncidents() {

    const search =
        document
            .getElementById("incidentSearch")
            .value
            .toLowerCase();

    const severity =
        document
            .getElementById("severityFilter")
            .value;

    const status =
        document
            .getElementById("statusFilter")
            .value;


    const filtered =
        incidents.filter(incident => {

            const matchesSearch =
                incident.location
                    ?.toLowerCase()
                    .includes(search)
                ||
                incident.description
                    ?.toLowerCase()
                    .includes(search);

            const matchesSeverity =
                severity === "ALL"
                ||
                incident.severity === severity;

            const matchesStatus =
                status === "ALL"
                ||
                incident.status === status;

            return (
                matchesSearch &&
                matchesSeverity &&
                matchesStatus
            );

        });


    updateIncidentSummary();


    const tbody =
        document.getElementById(
            "incidentTableBody"
        );


    if (!filtered.length) {

        tbody.innerHTML = `

            <tr>

                <td colspan="7">

                    <div class="empty-state">

                        <div class="empty-state-icon">
                            🔍
                        </div>

                        <div class="empty-state-title">
                            No incidents found
                        </div>

                        <div class="empty-state-text">
                            Try changing your search or filters.
                        </div>

                    </div>

                </td>

            </tr>

        `;

        return;

    }


    tbody.innerHTML =
        filtered.map(incident => `

            <tr>

                <td>
                    #${incident.id}
                </td>

                <td>
                    <strong>
                        ${escapeHtml(
            incident.location
        )}
                    </strong>
                </td>

                <td>
                    ${severityBadge(
            incident.severity
        )}
                </td>

                <td
                    style="
                        max-width:240px;
                        overflow:hidden;
                        text-overflow:ellipsis;
                    "
                >
                    ${escapeHtml(
            incident.description
        )}
                </td>

                <td>
                    ${statusBadge(
            incident.status
        )}
                </td>

                <td>
                    ${formatDate(
            incident.createdAt
        )}
                </td>

                <td>

                    <div class="action-group">

                        <button
                            class="action-button"
                            title="Edit"
                            onclick="editIncident(${incident.id})">

                            ✏️

                        </button>

                        <button
                            class="action-button"
                            title="Delete"
                            onclick="deleteIncident(${incident.id})">

                            🗑️

                        </button>

                    </div>

                </td>

            </tr>

        `).join("");

}


/* =========================================================
   INCIDENT SUMMARY
========================================================= */

function updateIncidentSummary() {

    const critical =
        incidents.filter(
            i => i.severity === "CRITICAL"
        ).length;

    const active =
        incidents.filter(
            i => i.status === "ACTIVE"
        ).length;

    const resolved =
        incidents.filter(
            i => i.status === "RESOLVED"
        ).length;


    document.getElementById(
        "totalIncidentCount"
    ).textContent =
        incidents.length;

    document.getElementById(
        "criticalIncidentCount"
    ).textContent =
        critical;

    document.getElementById(
        "activeIncidentCount"
    ).textContent =
        active;

    document.getElementById(
        "resolvedIncidentCount"
    ).textContent =
        resolved;

}


/* =========================================================
   INCIDENT FILTERS
========================================================= */

function setupIncidentFilters() {

    document
        .getElementById("incidentSearch")
        .addEventListener(
            "input",
            renderIncidents
        );

    document
        .getElementById("severityFilter")
        .addEventListener(
            "change",
            renderIncidents
        );

    document
        .getElementById("statusFilter")
        .addEventListener(
            "change",
            renderIncidents
        );

}


/* =========================================================
   INCIDENT MODAL
========================================================= */

function setupIncidentModal() {

    document
        .getElementById("closeModal")
        .addEventListener(
            "click",
            closeIncidentModal
        );

    document
        .getElementById("cancelModal")
        .addEventListener(
            "click",
            closeIncidentModal
        );

    document
        .getElementById("incidentForm")
        .addEventListener(
            "submit",
            saveIncident
        );

}


/* =========================================================
   CREATE INCIDENT
========================================================= */

function openCreateIncidentModal() {

    currentEditingIncident = null;

    document.getElementById(
        "modalTitle"
    ).textContent =
        "Report Emergency";

    document.getElementById(
        "incidentId"
    ).value = "";

    document.getElementById(
        "incidentLocation"
    ).value = "";

    document.getElementById(
        "incidentSeverity"
    ).value = "";

    document.getElementById(
        "incidentDescription"
    ).value = "";

    document.getElementById(
        "incidentStatus"
    ).value = "ACTIVE";

    document
        .getElementById("incidentModal")
        .classList.remove("hidden");

}


/* =========================================================
   EDIT INCIDENT
========================================================= */

function editIncident(id) {

    const incident =
        incidents.find(
            item => item.id === id
        );

    if (!incident) return;

    currentEditingIncident = id;

    document.getElementById(
        "modalTitle"
    ).textContent =
        "Update Incident";

    document.getElementById(
        "incidentId"
    ).value =
        incident.id;

    document.getElementById(
        "incidentLocation"
    ).value =
        incident.location;

    document.getElementById(
        "incidentSeverity"
    ).value =
        incident.severity;

    document.getElementById(
        "incidentDescription"
    ).value =
        incident.description;

    document.getElementById(
        "incidentStatus"
    ).value =
        incident.status;

    document
        .getElementById("incidentModal")
        .classList.remove("hidden");

}


/* =========================================================
   SAVE INCIDENT
========================================================= */

async function saveIncident(event) {

    event.preventDefault();

    const id =
        document.getElementById(
            "incidentId"
        ).value;


    const incident = {

        location:
            document.getElementById(
                "incidentLocation"
            ).value.trim(),

        severity:
        document.getElementById(
            "incidentSeverity"
        ).value,

        description:
            document.getElementById(
                "incidentDescription"
            ).value.trim(),

        status:
        document.getElementById(
            "incidentStatus"
        ).value

    };


    try {

        if (id) {

            await apiRequest(
                `/incidents/${id}`,
                {
                    method: "PUT",
                    body: JSON.stringify(incident)
                }
            );

            showToast(
                "Incident updated successfully"
            );

        } else {

            await apiRequest(
                "/incidents",
                {
                    method: "POST",
                    body: JSON.stringify(incident)
                }
            );

            showToast(
                "Emergency reported successfully"
            );

        }

        closeIncidentModal();

        await loadAllData();

        navigateTo("incidents");

    } catch (error) {

        console.error(error);

        showToast(
            "Failed to save incident"
        );

    }

}


/* =========================================================
   DELETE INCIDENT
========================================================= */

async function deleteIncident(id) {

    const confirmed =
        confirm(
            `Are you sure you want to delete Incident #${id}?`
        );

    if (!confirmed) return;


    try {

        await apiRequest(
            `/incidents/${id}`,
            {
                method: "DELETE"
            }
        );

        showToast(
            "Incident deleted successfully"
        );

        await loadAllData();

    } catch (error) {

        console.error(error);

        showToast(
            "Unable to delete incident"
        );

    }

}


/* =========================================================
   CLOSE MODAL
========================================================= */

function closeIncidentModal() {

    document
        .getElementById("incidentModal")
        .classList.add("hidden");

}


/* =========================================================
   DRONES
========================================================= */

function renderDrones() {

    const tbody =
        document.getElementById(
            "droneTableBody"
        );

    if (!drones.length) {

        tbody.innerHTML = `
            <tr>
                <td colspan="5">
                    <div class="empty-state">
                        No drones found.
                    </div>
                </td>
            </tr>
        `;

        return;

    }

    tbody.innerHTML =
        drones.map(drone => `

            <tr>

                <td>
                    #${drone.id}
                </td>

                <td>
                    <strong>
                        ${escapeHtml(
            drone.droneCode
        )}
                    </strong>
                </td>

                <td>
                    ${drone.payloadCapacity}
                </td>

                <td>
                    ${drone.batteryLevel}%
                </td>

                <td>
                    ${statusBadge(
            drone.status
        )}
                </td>

            </tr>

        `).join("");

}


/* =========================================================
   ROOFTOPS
========================================================= */

function renderRooftops() {

    const tbody =
        document.getElementById(
            "rooftopTableBody"
        );

    if (!rooftops.length) {

        tbody.innerHTML = `
            <tr>
                <td colspan="5">
                    <div class="empty-state">
                        No rooftops found.
                    </div>
                </td>
            </tr>
        `;

        return;

    }

    tbody.innerHTML =
        rooftops.map(rooftop => `

            <tr>

                <td>
                    #${rooftop.id}
                </td>

                <td>
                    <strong>
                        ${escapeHtml(
            rooftop.buildingName
        )}
                    </strong>
                </td>

                <td>
                    ${escapeHtml(
            rooftop.location
        )}
                </td>

                <td>
                    ${rooftop.capacity}
                </td>

                <td>
                    ${statusBadge(
            rooftop.status
        )}
                </td>

            </tr>

        `).join("");

}


/* =========================================================
   DEPLOYMENTS
========================================================= */

function renderDeployments() {

    const tbody =
        document.getElementById(
            "deploymentTableBody"
        );

    if (!deployments.length) {

        tbody.innerHTML = `
            <tr>
                <td colspan="6">
                    <div class="empty-state">
                        No deployments found.
                    </div>
                </td>
            </tr>
        `;

        return;

    }

    tbody.innerHTML =
        deployments.map(deployment => `

            <tr>

                <td>
                    #${deployment.id}
                </td>

                <td>
                    Incident #${deployment.incidentId}
                </td>

                <td>
                    Drone #${deployment.droneId}
                </td>

                <td>
                    Rooftop #${deployment.rooftopId}
                </td>

                <td>
                    ${statusBadge(
            deployment.status
        )}
                </td>

                <td>
                    ${formatDate(
            deployment.createdAt
        )}
                </td>

            </tr>

        `).join("");

}


/* =========================================================
   BADGES
========================================================= */

function severityBadge(severity) {

    const value =
        severity || "UNKNOWN";

    const className =
        value.toLowerCase();

    return `
        <span class="badge badge-${className}">
            ${value}
        </span>
    `;

}


function statusBadge(status) {

    const value =
        status || "UNKNOWN";

    const className =
        value
            .toLowerCase()
            .replace("_", "-");

    return `
        <span class="badge badge-${className}">
            ${value}
        </span>
    `;

}


/* =========================================================
   DATE FORMAT
========================================================= */

function formatDate(value) {

    if (!value) return "-";

    const date =
        new Date(value);

    if (Number.isNaN(
        date.getTime()
    )) {

        return value;

    }

    return date.toLocaleString(
        "en-IN",
        {
            day: "2-digit",
            month: "short",
            year: "numeric",
            hour: "2-digit",
            minute: "2-digit"
        }
    );

}


/* =========================================================
   HTML SAFETY
========================================================= */

function escapeHtml(value) {

    if (value === null ||
        value === undefined) {

        return "";

    }

    return String(value)
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");

}


/* =========================================================
   TOAST
========================================================= */

let toastTimer;

function showToast(message) {

    const toast =
        document.getElementById(
            "toast"
        );

    document.getElementById(
        "toastMessage"
    ).textContent =
        message;

    toast.classList.add("show");

    clearTimeout(toastTimer);

    toastTimer =
        setTimeout(
            () => {

                toast.classList.remove(
                    "show"
                );

            },
            3000
        );

}


/* =========================================================
   GLOBAL FUNCTIONS
========================================================= */

window.editIncident =
    editIncident;

window.deleteIncident =
    deleteIncident;