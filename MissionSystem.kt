package com.example.cosmic.model

import com.example.cosmic.physics.FlightState
import com.example.cosmic.physics.FlightStatus
import com.example.cosmic.physics.OrbitalMath
import com.example.cosmic.physics.SolarSystem

data class Mission(
    val id: String,
    val title: String,
    val description: String,
    val rewardCredits: Int,
    val targetBadge: String,
    val category: String = "Exploration",
    var isCompleted: Boolean = false
)

class MissionManager(
    private val progression: ProgressionSystem? = null,
    private val saveSystem: SaveSystem? = null
) {
    val missions = mutableListOf(
        Mission(
            id = "m1_suborbital",
            title = "Suborbital Leap",
            description = "Ascend above 15,000 meters altitude and break through the lower cloud layers.",
            rewardCredits = 300,
            targetBadge = "15km",
            category = "Atmospheric"
        ),
        Mission(
            id = "m2_karman",
            title = "Breach the Karman Line",
            description = "Cross 72,000 meters into vacuum space where atmosphere fades completely.",
            rewardCredits = 600,
            targetBadge = "Vacuum",
            category = "Atmospheric"
        ),
        Mission(
            id = "m3_orbit",
            title = "Orbital Insertion",
            description = "Achieve a closed circular or elliptical orbit around Novaria with periapsis > 75,000 meters.",
            rewardCredits = 1200,
            targetBadge = "Orbit",
            category = "Orbital"
        ),
        Mission(
            id = "m4_satellite",
            title = "Orbital Satellite Deployment",
            description = "Place an unmanned probe equipped with Solar Panels and Battery into a stable orbit.",
            rewardCredits = 1500,
            targetBadge = "Satellite",
            category = "Satellites"
        ),
        Mission(
            id = "m5_engine_test",
            title = "High-Thrust Engine Test",
            description = "Ignite the Titan Lifter or Vector engine at 100% full throttle for at least 10 seconds.",
            rewardCredits = 500,
            targetBadge = "Burn",
            category = "Propulsion"
        ),
        Mission(
            id = "m6_lunar_transfer",
            title = "Lunar Gravity Well",
            description = "Perform a translunar injection burn and cross into the Sphere of Influence of Moon Lunara.",
            rewardCredits = 2200,
            targetBadge = "Lunara SOI",
            category = "Lunar"
        ),
        Mission(
            id = "m7_lunar_landing",
            title = "Touchdown on Lunara",
            description = "Execute a precision powered descent and land softly on the cratered surface of Moon Lunara.",
            rewardCredits = 3500,
            targetBadge = "Moon Pioneer",
            category = "Lunar"
        ),
        Mission(
            id = "m8_safe_return",
            title = "Atmospheric Re-entry & Recovery",
            description = "Survive atmospheric re-entry heat and touch down safely on Novaria using recovery parachutes.",
            rewardCredits = 1800,
            targetBadge = "Splashdown",
            category = "Recovery"
        ),
        // --- V3 EXPANSION MISSIONS ---
        Mission(
            id = "m9_docking",
            title = "Orbital Rendezvous & Docking",
            description = "Perform precision orbital maneuvering and engage docking clamps with another spacecraft or station in orbit.",
            rewardCredits = 2800,
            targetBadge = "Docked",
            category = "Stations"
        ),
        Mission(
            id = "m10_station_expansion",
            title = "Station Module Deployment",
            description = "Deploy a space station core equipped with a Crew Habitat module or Science Lab into orbit.",
            rewardCredits = 3200,
            targetBadge = "Station Core",
            category = "Stations"
        ),
        Mission(
            id = "m11_booster_recovery",
            title = "Reusable Booster Precision Landing",
            description = "Perform a controlled retro-burn and land a booster stage vertically with landing legs deployed on Novaria.",
            rewardCredits = 2500,
            targetBadge = "Booster Reuse",
            category = "Reusable"
        ),
        Mission(
            id = "m12_pyros_flyby",
            title = "Pyros Solar Encounter",
            description = "Navigate an interplanetary trajectory across deep space and enter the Sphere of Influence of inner world Pyros.",
            rewardCredits = 4500,
            targetBadge = "Pyros SOI",
            category = "Interplanetary"
        ),
        Mission(
            id = "m13_pyros_touchdown",
            title = "Volcanic Touchdown on Pyros",
            description = "Descend through Pyros's amber atmosphere and touchdown softly onto the scorched volcanic plains.",
            rewardCredits = 6500,
            targetBadge = "Pyros Surface",
            category = "Interplanetary"
        ),
        Mission(
            id = "m14_aeris_rings",
            title = "Journey to the Rings of Aeris",
            description = "Fly an interplanetary probe to ringed azure planet Aeris or its icy moon Boreas.",
            rewardCredits = 5500,
            targetBadge = "Aeris Rings",
            category = "Interplanetary"
        ),
        Mission(
            id = "m15_glacies_frontier",
            title = "Outer Frontier at Glacies",
            description = "Reach the edge of the Helios system and capture into orbit around distant frozen world Glacies.",
            rewardCredits = 7500,
            targetBadge = "Deep Frontier",
            category = "Interplanetary"
        ),
        Mission(
            id = "m16_crew_expedition",
            title = "Manned Crew Expedition",
            description = "Launch an astronaut crew on a manned orbital flight and safely return them to home base.",
            rewardCredits = 3000,
            targetBadge = "Crew Flown",
            category = "Crew"
        )
    )

    init {
        // Load completed missions from persistent save
        saveSystem?.let { save ->
            val completed = save.getCompletedMissionIds()
            missions.forEach { m ->
                if (completed.contains(m.id)) {
                    m.isCompleted = true
                }
            }
        }
    }

    private var engineFullThrottleTimer = 0.0

    fun evaluateFlight(state: FlightState, dt: Double, onMissionCompleted: ((Mission) -> Unit)? = null) {
        val alt = state.altitude
        val primary = state.primaryBody

        // Mission 1: 15 km altitude
        if (alt >= 15_000.0) {
            completeMission("m1_suborbital", onMissionCompleted)
        }

        // Mission 2: > 72 km space
        if (alt >= 72_000.0 && primary == SolarSystem.PLANET_NOVARIA) {
            completeMission("m2_karman", onMissionCompleted)
        }

        // Mission 3: Stable orbit
        if (primary == SolarSystem.PLANET_NOVARIA && alt > 72_000.0) {
            val orbit = OrbitalMath.computeOrbit(state.position, state.velocity, primary)
            if (orbit.isStableOrbit && orbit.periapsisAltitude > 75_000.0) {
                completeMission("m3_orbit", onMissionCompleted)

                // Mission 4: Satellite check (has solar panel & probe or battery)
                val hasSolar = state.rocket.parts.any { it.type == PartType.SOLAR_PANEL || it.type == PartType.SOLAR_ARRAY_HEAVY }
                val hasProbeOrBattery = state.rocket.parts.any {
                    it.type == PartType.PROBE_CORE || it.type == PartType.BATTERY_PACK || it.type == PartType.BATTERY_BANK_HEAVY
                }
                if (hasSolar && hasProbeOrBattery) {
                    completeMission("m4_satellite", onMissionCompleted)
                }

                // Mission 10: Station Module Deployment check
                val hasHabitatOrLab = state.rocket.parts.any {
                    it.type == PartType.STATION_HABITAT || it.type == PartType.STATION_SCIENCE_LAB
                }
                if (hasHabitatOrLab && hasSolar) {
                    completeMission("m10_station_expansion", onMissionCompleted)
                }
            }
        }

        // Mission 5: Engine burn test
        if (state.throttle >= 0.98f && state.rocket.parts.any { it.type == PartType.ENGINE_VECTOR && !it.isDecoupled && it.stageIndex == state.activeStage }) {
            engineFullThrottleTimer += dt
            if (engineFullThrottleTimer >= 10.0) {
                completeMission("m5_engine_test", onMissionCompleted)
            }
        } else {
            engineFullThrottleTimer = 0.0
        }

        // Mission 6: Lunar SOI
        if (primary == SolarSystem.MOON_LUNARA) {
            completeMission("m6_lunar_transfer", onMissionCompleted)
        }

        // Mission 7: Lunar landing
        if (primary == SolarSystem.MOON_LUNARA && state.status == FlightStatus.LANDED) {
            completeMission("m7_lunar_landing", onMissionCompleted)
        }

        // Mission 8: Safe atmospheric return
        if (primary == SolarSystem.PLANET_NOVARIA && state.status == FlightStatus.LANDED && state.parachuteDeployed) {
            completeMission("m8_safe_return", onMissionCompleted)
        }

        // Mission 11: Reusable Booster Precision Landing (with legs, vertical, on Novaria)
        if (primary == SolarSystem.PLANET_NOVARIA && state.status == FlightStatus.LANDED && state.landingGearDeployed) {
            completeMission("m11_booster_recovery", onMissionCompleted)
        }

        // Mission 12: Pyros SOI
        if (primary == SolarSystem.PLANET_PYROS || primary == SolarSystem.MOON_ASTRA) {
            completeMission("m12_pyros_flyby", onMissionCompleted)
        }

        // Mission 13: Pyros landing
        if (primary == SolarSystem.PLANET_PYROS && state.status == FlightStatus.LANDED) {
            completeMission("m13_pyros_touchdown", onMissionCompleted)
        }

        // Mission 14: Aeris SOI / rings
        if (primary == SolarSystem.PLANET_AERIS || primary == SolarSystem.MOON_BOREAS) {
            completeMission("m14_aeris_rings", onMissionCompleted)
        }

        // Mission 15: Glacies SOI
        if (primary == SolarSystem.PLANET_GLACIES || primary == SolarSystem.MOON_NIX) {
            completeMission("m15_glacies_frontier", onMissionCompleted)
        }

        // Mission 16: Manned crew expedition (has crew and safely in orbit or returned)
        if (state.assignedCrewIds.isNotEmpty() && (state.status == FlightStatus.ORBIT || (state.status == FlightStatus.LANDED && state.metSeconds > 60.0))) {
            completeMission("m16_crew_expedition", onMissionCompleted)
        }
    }

    fun completeDockingMission(onMissionCompleted: ((Mission) -> Unit)? = null) {
        completeMission("m9_docking", onMissionCompleted)
    }

    private fun completeMission(id: String, callback: ((Mission) -> Unit)?) {
        val mission = missions.find { it.id == id } ?: return
        if (!mission.isCompleted) {
            mission.isCompleted = true
            progression?.addCredits(mission.rewardCredits)
            saveSystem?.markMissionCompleted(mission.id)
            callback?.invoke(mission)
        }
    }
}
