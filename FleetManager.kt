package com.example.cosmic.model

import android.content.Context
import android.content.SharedPreferences
import com.example.cosmic.math.Vector3D
import com.example.cosmic.physics.FlightState
import com.example.cosmic.physics.SolarSystem
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

enum class VesselType(val label: String) {
    ROCKET("Active Spacecraft"),
    SPACE_STATION("Orbital Station"),
    SATELLITE_COMM("Communication Relay"),
    SATELLITE_SCIENCE("Orbital Science Probe"),
    SATELLITE_NAV("Constellation NavSat"),
    SURFACE_LANDER("Surface Outpost")
}

data class OrbitingVessel(
    val id: String = UUID.randomUUID().toString(),
    var name: String,
    val type: VesselType,
    var primaryBodyName: String = "Novaria",
    var position: Vector3D,
    var velocity: Vector3D,
    var design: RocketDesign,
    val crewIds: MutableList<String> = mutableListOf(),
    var isDocked: Boolean = false,
    var dockedTargetId: String? = null
) {
    val partCount: Int get() = design.parts.size
    val totalMass: Double get() = design.totalMass()
}

class FleetManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("cosmic_fleet_state", Context.MODE_PRIVATE)

    val vessels: MutableList<OrbitingVessel> = mutableListOf()

    init {
        loadFleet()
    }

    private fun loadFleet() {
        val json = prefs.getString("fleet_vessels", null)
        if (json != null) {
            try {
                val array = JSONArray(json)
                vessels.clear()
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    val id = obj.getString("id")
                    val name = obj.getString("name")
                    val type = try {
                        VesselType.valueOf(obj.getString("type"))
                    } catch (e: Exception) {
                        VesselType.SPACE_STATION
                    }
                    val bodyName = obj.optString("primaryBodyName", "Novaria")
                    val px = obj.optDouble("posX", 0.0)
                    val py = obj.optDouble("posY", 0.0)
                    val pz = obj.optDouble("posZ", 0.0)
                    val vx = obj.optDouble("velX", 0.0)
                    val vy = obj.optDouble("velY", 0.0)
                    val vz = obj.optDouble("velZ", 0.0)

                    // Parts
                    val partsArray = obj.getJSONArray("parts")
                    val parts = mutableListOf<PlacedPart>()
                    for (p in 0 until partsArray.length()) {
                        val pObj = partsArray.getJSONObject(p)
                        val partType = try {
                            PartType.valueOf(pObj.getString("typeName"))
                        } catch (e: Exception) {
                            continue
                        }
                        parts.add(
                            PlacedPart(
                                id = pObj.optString("id", UUID.randomUUID().toString()),
                                type = partType,
                                x = pObj.optDouble("x", 0.0).toFloat(),
                                y = pObj.optDouble("y", 0.0).toFloat(),
                                z = pObj.optDouble("z", 0.0).toFloat(),
                                rotationYDeg = pObj.optDouble("rotY", 0.0).toFloat(),
                                stageIndex = pObj.optInt("stage", 0),
                                fuelRemaining = pObj.optDouble("fuel", partType.fuelCapacity),
                                isDeployed = pObj.optBoolean("deployed", false)
                            )
                        )
                    }

                    val crewArray = obj.optJSONArray("crewIds")
                    val crew = mutableListOf<String>()
                    if (crewArray != null) {
                        for (c in 0 until crewArray.length()) {
                            crew.add(crewArray.getString(c))
                        }
                    }

                    vessels.add(
                        OrbitingVessel(
                            id = id,
                            name = name,
                            type = type,
                            primaryBodyName = bodyName,
                            position = Vector3D(px, py, pz),
                            velocity = Vector3D(vx, vy, vz),
                            design = RocketDesign(name = name, parts = parts),
                            crewIds = crew
                        )
                    )
                }
            } catch (e: Exception) {
                initStarterFleet()
            }
        } else {
            initStarterFleet()
        }
    }

    private fun initStarterFleet() {
        vessels.clear()
        // Default starter orbiting space station: "Novaria Gateway Station"
        val gatewayParts = mutableListOf(
            PlacedPart(type = PartType.COMMAND_CAPSULE, y = 3.0f),
            PlacedPart(type = PartType.TANK_MEDIUM, y = 1.0f),
            PlacedPart(type = PartType.STRUCTURAL_TRUSS, y = -1.2f),
            PlacedPart(type = PartType.SOLAR_PANEL, x = 1.5f, y = 1.0f, isDeployed = true),
            PlacedPart(type = PartType.SOLAR_PANEL, x = -1.5f, y = 1.0f, isDeployed = true),
            PlacedPart(type = PartType.BATTERY_PACK, x = 0f, y = -1.2f, z = 0.9f)
        )
        vessels.add(
            OrbitingVessel(
                name = "Gateway Space Station",
                type = VesselType.SPACE_STATION,
                primaryBodyName = "Novaria",
                position = Vector3D(350_000.0, 0.0, 0.0),
                velocity = Vector3D(0.0, 0.0, 1928.0),
                design = RocketDesign(name = "Gateway Space Station", parts = gatewayParts)
            )
        )

        // Starter communications satellite in high orbit
        val satParts = mutableListOf(
            PlacedPart(type = PartType.PROBE_CORE, y = 0f),
            PlacedPart(type = PartType.SOLAR_PANEL, x = 1.0f, isDeployed = true),
            PlacedPart(type = PartType.SOLAR_PANEL, x = -1.0f, isDeployed = true),
            PlacedPart(type = PartType.BATTERY_PACK, z = 0.6f)
        )
        vessels.add(
            OrbitingVessel(
                name = "AeroComm-1 Satellite",
                type = VesselType.SATELLITE_COMM,
                primaryBodyName = "Novaria",
                position = Vector3D(0.0, 0.0, 600_000.0),
                velocity = Vector3D(-1472.0, 0.0, 0.0),
                design = RocketDesign(name = "AeroComm-1", parts = satParts)
            )
        )
        saveFleet()
    }

    fun saveFleet() {
        val array = JSONArray()
        for (vessel in vessels) {
            val obj = JSONObject()
            obj.put("id", vessel.id)
            obj.put("name", vessel.name)
            obj.put("type", vessel.type.name)
            obj.put("primaryBodyName", vessel.primaryBodyName)
            obj.put("posX", vessel.position.x)
            obj.put("posY", vessel.position.y)
            obj.put("posZ", vessel.position.z)
            obj.put("velX", vessel.velocity.x)
            obj.put("velY", vessel.velocity.y)
            obj.put("velZ", vessel.velocity.z)

            val pArray = JSONArray()
            for (p in vessel.design.parts) {
                val pObj = JSONObject()
                pObj.put("id", p.id)
                pObj.put("typeName", p.type.name)
                pObj.put("x", p.x.toDouble())
                pObj.put("y", p.y.toDouble())
                pObj.put("z", p.z.toDouble())
                pObj.put("rotY", p.rotationYDeg.toDouble())
                pObj.put("stage", p.stageIndex)
                pObj.put("fuel", p.fuelRemaining)
                pObj.put("deployed", p.isDeployed)
                pArray.put(pObj)
            }
            obj.put("parts", pArray)

            val cArray = JSONArray()
            for (c in vessel.crewIds) {
                cArray.put(c)
            }
            obj.put("crewIds", cArray)
            array.put(obj)
        }
        prefs.edit().putString("fleet_vessels", array.toString()).apply()
    }

    fun registerDeployedVessel(flight: FlightState, name: String, type: VesselType): OrbitingVessel {
        val activeParts = flight.rocket.parts.filter { !it.isDecoupled }.map { it.copyPlacedPart() }
        val newVessel = OrbitingVessel(
            name = name,
            type = type,
            primaryBodyName = flight.primaryBody.name,
            position = flight.position,
            velocity = flight.velocity,
            design = RocketDesign(name = name, parts = activeParts.toMutableList())
        )
        vessels.add(newVessel)
        saveFleet()
        return newVessel
    }

    fun removeVessel(id: String) {
        vessels.removeAll { it.id == id }
        saveFleet()
    }

    /**
     * Check if active flight is close enough to any orbiting vessel for docking clamp.
     */
    fun findDockingTarget(flight: FlightState, maxDistanceMeters: Double = 60.0): OrbitingVessel? {
        val bodyName = flight.primaryBody.name
        return vessels.find { vessel ->
            vessel.primaryBodyName == bodyName &&
                vessel.position.distanceTo(flight.position) <= maxDistanceMeters
        }
    }

    /**
     * Executes in-space docking: merges target vessel modules onto the active craft.
     */
    fun dockWithVessel(flight: FlightState, target: OrbitingVessel): Boolean {
        val relDist = flight.position.distanceTo(target.position)
        val relSpeed = (flight.velocity - target.velocity).length()

        // Docking clamp requires distance < 50m and relative speed < 2.5 m/s
        if (relDist > 65.0 || relSpeed > 3.0) return false

        // Offset target parts to avoid collision overlap
        val highestY = flight.rocket.parts.maxOfOrNull { it.y } ?: 0f
        val lowestTargetY = target.design.parts.minOfOrNull { it.y } ?: 0f
        val yOffset = highestY - lowestTargetY + 2.0f

        for (part in target.design.parts) {
            val merged = part.copyPlacedPart()
            merged.y += yOffset
            flight.rocket.parts.add(merged)
        }

        // Remove docked station from separate fleet list since it is now part of the active craft
        vessels.remove(target)
        saveFleet()
        return true
    }

    /**
     * Equalizes fuel across all propellant tanks in the combined spacecraft/station.
     */
    fun balanceStationFuel(flight: FlightState) {
        val activeTanks = flight.rocket.parts.filter { !it.isDecoupled && (it.type.category == PartCategory.FUEL || it.type.fuelCapacity > 0) }
        if (activeTanks.isEmpty()) return
        val totalCapacity = activeTanks.sumOf { it.type.fuelCapacity }
        val totalFuel = activeTanks.sumOf { it.fuelRemaining }
        val ratio = (totalFuel / totalCapacity).coerceIn(0.0, 1.0)
        for (tank in activeTanks) {
            tank.fuelRemaining = tank.type.fuelCapacity * ratio
        }
    }
}
