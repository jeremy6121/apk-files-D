package com.example.cosmic.model

object StockRockets {

    /**
     * Cosmos 1 (Orbiter): 2-stage orbital launch vehicle with aerodynamic nose cone and stabilizer fins.
     */
    fun createCosmos1(): RocketDesign {
        val parts = mutableListOf(
            // Payload / Crew Module
            PlacedPart(
                type = PartType.COMMAND_CAPSULE,
                x = 0f, y = 3.8f, z = 0f,
                stageIndex = 1
            ),
            PlacedPart(
                type = PartType.PARACHUTE,
                x = 0f, y = 4.6f, z = 0f,
                stageIndex = 1
            ),
            // Upper Orbital Stage
            PlacedPart(
                type = PartType.TANK_SMALL,
                x = 0f, y = 2.4f, z = 0f,
                stageIndex = 1
            ),
            PlacedPart(
                type = PartType.ENGINE_SPARK,
                x = 0f, y = 1.2f, z = 0f,
                stageIndex = 1
            ),
            // Interstage Decoupler
            PlacedPart(
                type = PartType.DECOUPLER,
                x = 0f, y = 0.5f, z = 0f,
                stageIndex = 1
            ),
            // Main Booster Stage
            PlacedPart(
                type = PartType.TANK_MEDIUM,
                x = 0f, y = -1.1f, z = 0f,
                stageIndex = 0
            ),
            PlacedPart(
                type = PartType.ENGINE_VECTOR,
                x = 0f, y = -3.1f, z = 0f,
                stageIndex = 0
            ),
            // 4 Stabilizer Fins
            PlacedPart(
                type = PartType.AERO_FIN,
                x = 0.9f, y = -2.5f, z = 0f,
                rotationYDeg = 0f,
                stageIndex = 0
            ),
            PlacedPart(
                type = PartType.AERO_FIN,
                x = -0.9f, y = -2.5f, z = 0f,
                rotationYDeg = 180f,
                stageIndex = 0
            ),
            PlacedPart(
                type = PartType.AERO_FIN,
                x = 0f, y = -2.5f, z = 0.9f,
                rotationYDeg = 90f,
                stageIndex = 0
            ),
            PlacedPart(
                type = PartType.AERO_FIN,
                x = 0f, y = -2.5f, z = -0.9f,
                rotationYDeg = 270f,
                stageIndex = 0
            )
        )
        return RocketDesign(name = "Cosmos 1 (Orbiter)", parts = parts)
    }

    /**
     * Lunar Explorer: 3-stage mission vehicle designed for Lunara landing and sample return.
     */
    fun createLunarExplorer(): RocketDesign {
        val parts = mutableListOf(
            // Stage 2: Lunar Descent & Ascent Lander
            PlacedPart(
                type = PartType.COMMAND_CAPSULE,
                x = 0f, y = 6.8f, z = 0f,
                stageIndex = 2
            ),
            PlacedPart(
                type = PartType.TANK_SMALL,
                x = 0f, y = 5.4f, z = 0f,
                stageIndex = 2
            ),
            PlacedPart(
                type = PartType.ENGINE_SPARK,
                x = 0f, y = 4.2f, z = 0f,
                stageIndex = 2
            ),
            PlacedPart(
                type = PartType.LANDING_LEGS,
                x = 0.85f, y = 4.6f, z = 0f,
                rotationYDeg = 0f,
                stageIndex = 2
            ),
            PlacedPart(
                type = PartType.LANDING_LEGS,
                x = -0.85f, y = 4.6f, z = 0f,
                rotationYDeg = 180f,
                stageIndex = 2
            ),
            PlacedPart(
                type = PartType.SOLAR_PANEL,
                x = 0f, y = 5.2f, z = 0.85f,
                rotationYDeg = 90f,
                stageIndex = 2
            ),
            PlacedPart(
                type = PartType.SOLAR_PANEL,
                x = 0f, y = 5.2f, z = -0.85f,
                rotationYDeg = 270f,
                stageIndex = 2
            ),

            // Interstage Decoupler
            PlacedPart(
                type = PartType.DECOUPLER,
                x = 0f, y = 3.6f, z = 0f,
                stageIndex = 2
            ),

            // Stage 1: Trans-Lunar Injection & Orbital Insertion
            PlacedPart(
                type = PartType.TANK_MEDIUM,
                x = 0f, y = 2.0f, z = 0f,
                stageIndex = 1
            ),
            PlacedPart(
                type = PartType.ENGINE_AEROSPIKE,
                x = 0f, y = 0.1f, z = 0f,
                stageIndex = 1
            ),

            // Lower Interstage Decoupler
            PlacedPart(
                type = PartType.DECOUPLER,
                x = 0f, y = -0.6f, z = 0f,
                stageIndex = 1
            ),

            // Stage 0: Main Lifter Core
            PlacedPart(
                type = PartType.TANK_LARGE,
                x = 0f, y = -2.8f, z = 0f,
                stageIndex = 0
            ),
            PlacedPart(
                type = PartType.ENGINE_VECTOR,
                x = 0f, y = -5.4f, z = 0f,
                stageIndex = 0
            ),
            PlacedPart(
                type = PartType.AERO_FIN,
                x = 1.1f, y = -4.5f, z = 0f,
                stageIndex = 0
            ),
            PlacedPart(
                type = PartType.AERO_FIN,
                x = -1.1f, y = -4.5f, z = 0f,
                rotationYDeg = 180f,
                stageIndex = 0
            )
        )
        return RocketDesign(name = "Lunar Explorer", parts = parts)
    }

    /**
     * Titan Heavy: Massive multi-booster rocket with side-attached strap-on tanks and satellite probe payload.
     */
    fun createTitanHeavy(): RocketDesign {
        val parts = mutableListOf(
            // Satellite Payload
            PlacedPart(
                type = PartType.PROBE_CORE,
                x = 0f, y = 6.4f, z = 0f,
                stageIndex = 2
            ),
            PlacedPart(
                type = PartType.BATTERY_PACK,
                x = 0f, y = 5.8f, z = 0f,
                stageIndex = 2
            ),
            PlacedPart(
                type = PartType.SOLAR_PANEL,
                x = 0.65f, y = 6.2f, z = 0f,
                rotationYDeg = 0f,
                stageIndex = 2
            ),
            PlacedPart(
                type = PartType.SOLAR_PANEL,
                x = -0.65f, y = 6.2f, z = 0f,
                rotationYDeg = 180f,
                stageIndex = 2
            ),
            PlacedPart(
                type = PartType.ENGINE_ION,
                x = 0f, y = 5.0f, z = 0f,
                stageIndex = 2
            ),
            PlacedPart(
                type = PartType.DECOUPLER,
                x = 0f, y = 4.4f, z = 0f,
                stageIndex = 2
            ),

            // Upper Insertion Core
            PlacedPart(
                type = PartType.TANK_MEDIUM,
                x = 0f, y = 2.8f, z = 0f,
                stageIndex = 1
            ),
            PlacedPart(
                type = PartType.ENGINE_AEROSPIKE,
                x = 0f, y = 0.9f, z = 0f,
                stageIndex = 1
            ),
            PlacedPart(
                type = PartType.DECOUPLER,
                x = 0f, y = 0.2f, z = 0f,
                stageIndex = 1
            ),

            // Central Heavy Booster
            PlacedPart(
                type = PartType.TANK_LARGE,
                x = 0f, y = -2.0f, z = 0f,
                stageIndex = 0
            ),
            PlacedPart(
                type = PartType.ENGINE_VECTOR,
                x = 0f, y = -4.6f, z = 0f,
                stageIndex = 0
            ),

            // Strap-on Side Booster Left
            PlacedPart(
                type = PartType.DECOUPLER_RADIAL,
                x = -1.4f, y = -1.8f, z = 0f,
                stageIndex = 0
            ),
            PlacedPart(
                type = PartType.TANK_RADIAL,
                x = -1.7f, y = -1.8f, z = 0f,
                stageIndex = 0
            ),
            PlacedPart(
                type = PartType.ENGINE_VECTOR,
                x = -1.7f, y = -3.8f, z = 0f,
                stageIndex = 0
            ),

            // Strap-on Side Booster Right
            PlacedPart(
                type = PartType.DECOUPLER_RADIAL,
                x = 1.4f, y = -1.8f, z = 0f,
                stageIndex = 0
            ),
            PlacedPart(
                type = PartType.TANK_RADIAL,
                x = 1.7f, y = -1.8f, z = 0f,
                stageIndex = 0
            ),
            PlacedPart(
                type = PartType.ENGINE_VECTOR,
                x = 1.7f, y = -3.8f, z = 0f,
                stageIndex = 0
            )
        )
        return RocketDesign(name = "Titan Heavy Lifter", parts = parts)
    }

    /**
     * Gateway Station Core: Launch vehicle carrying a modular space station core with docking clamp and solar truss.
     */
    fun createStationCore(): RocketDesign {
        val parts = mutableListOf(
            // Station Docking Clamp
            PlacedPart(type = PartType.DOCKING_PORT, x = 0f, y = 5.2f, z = 0f, stageIndex = 1),
            // Station Crew Habitat
            PlacedPart(type = PartType.STATION_HABITAT, x = 0f, y = 3.8f, z = 0f, stageIndex = 1),
            // Station Science Lab
            PlacedPart(type = PartType.STATION_SCIENCE_LAB, x = 0f, y = 1.3f, z = 0f, stageIndex = 1),
            // Station Battery Bank
            PlacedPart(type = PartType.BATTERY_BANK_HEAVY, x = 0f, y = -0.3f, z = 0f, stageIndex = 1),
            // Solar Wings Left and Right
            PlacedPart(type = PartType.SOLAR_ARRAY_HEAVY, x = 1.5f, y = 1.3f, z = 0f, stageIndex = 1, isDeployed = true),
            PlacedPart(type = PartType.SOLAR_ARRAY_HEAVY, x = -1.5f, y = 1.3f, z = 0f, stageIndex = 1, isDeployed = true),
            // Comms Dish
            PlacedPart(type = PartType.ANTENNA_DISH, x = 0f, y = 3.8f, z = 1.1f, stageIndex = 1),
            // Interstage Decoupler
            PlacedPart(type = PartType.DECOUPLER, x = 0f, y = -0.9f, z = 0f, stageIndex = 1),
            // Heavy Lift Booster Stage
            PlacedPart(type = PartType.TANK_LARGE, x = 0f, y = -3.2f, z = 0f, stageIndex = 0),
            PlacedPart(type = PartType.ENGINE_VECTOR, x = 0f, y = -5.8f, z = 0f, stageIndex = 0),
            // Aerodynamic Fins
            PlacedPart(type = PartType.AERO_FIN, x = 1.1f, y = -5.2f, z = 0f, stageIndex = 0),
            PlacedPart(type = PartType.AERO_FIN, x = -1.1f, y = -5.2f, z = 0f, rotationYDeg = 180f, stageIndex = 0),
            PlacedPart(type = PartType.AERO_FIN, x = 0f, y = -5.2f, z = 1.1f, rotationYDeg = 90f, stageIndex = 0),
            PlacedPart(type = PartType.AERO_FIN, x = 0f, y = -5.2f, z = -1.1f, rotationYDeg = 270f, stageIndex = 0)
        )
        return RocketDesign(name = "Orbital Station Core", parts = parts)
    }

    /**
     * Reusable Falcon: Vertical Takeoff & Vertical Landing (VTVL) booster equipped with landing legs and canard steering.
     */
    fun createReusableFalcon(): RocketDesign {
        val parts = mutableListOf(
            PlacedPart(type = PartType.PROBE_CORE, x = 0f, y = 4.2f, z = 0f, stageIndex = 0),
            PlacedPart(type = PartType.NOSE_CONE, x = 0f, y = 5.2f, z = 0f, stageIndex = 0),
            // Aerodynamic Grid Steering Canards
            PlacedPart(type = PartType.CANARD_FIN, x = 0.85f, y = 3.8f, z = 0f, stageIndex = 0),
            PlacedPart(type = PartType.CANARD_FIN, x = -0.85f, y = 3.8f, z = 0f, rotationYDeg = 180f, stageIndex = 0),
            PlacedPart(type = PartType.CANARD_FIN, x = 0f, y = 3.8f, z = 0.85f, rotationYDeg = 90f, stageIndex = 0),
            PlacedPart(type = PartType.CANARD_FIN, x = 0f, y = 3.8f, z = -0.85f, rotationYDeg = 270f, stageIndex = 0),
            // Propellant Core
            PlacedPart(type = PartType.TANK_LARGE, x = 0f, y = 1.2f, z = 0f, stageIndex = 0),
            PlacedPart(type = PartType.TANK_MEDIUM, x = 0f, y = -2.3f, z = 0f, stageIndex = 0),
            // Heavy Landing Legs for recovery
            PlacedPart(type = PartType.LANDING_LEGS_HEAVY, x = 1.2f, y = -3.2f, z = 0f, stageIndex = 0),
            PlacedPart(type = PartType.LANDING_LEGS_HEAVY, x = -1.2f, y = -3.2f, z = 0f, rotationYDeg = 180f, stageIndex = 0),
            PlacedPart(type = PartType.LANDING_LEGS_HEAVY, x = 0f, y = -3.2f, z = 1.2f, rotationYDeg = 90f, stageIndex = 0),
            PlacedPart(type = PartType.LANDING_LEGS_HEAVY, x = 0f, y = -3.2f, z = -1.2f, rotationYDeg = 270f, stageIndex = 0),
            // Vector Throttle Engine
            PlacedPart(type = PartType.ENGINE_VECTOR, x = 0f, y = -4.5f, z = 0f, stageIndex = 0)
        )
        return RocketDesign(name = "Reusable Falcon Booster", parts = parts)
    }

    /**
     * Pyros Deep-Space Cruiser: Multi-stage interplanetary spacecraft with planetary heat shield and ion propulsion.
     */
    fun createPyrosExplorer(): RocketDesign {
        val parts = mutableListOf(
            PlacedPart(type = PartType.HEAT_SHIELD_HEAVY, x = 0f, y = 6.4f, z = 0f, stageIndex = 2),
            PlacedPart(type = PartType.COMMAND_CAPSULE, x = 0f, y = 5.6f, z = 0f, stageIndex = 2),
            PlacedPart(type = PartType.TANK_SMALL, x = 0f, y = 4.2f, z = 0f, stageIndex = 2),
            PlacedPart(type = PartType.ENGINE_ION, x = 0f, y = 3.2f, z = 0f, stageIndex = 2),
            PlacedPart(type = PartType.SOLAR_ARRAY_HEAVY, x = 1.3f, y = 4.2f, z = 0f, stageIndex = 2, isDeployed = true),
            PlacedPart(type = PartType.SOLAR_ARRAY_HEAVY, x = -1.3f, y = 4.2f, z = 0f, stageIndex = 2, isDeployed = true),
            PlacedPart(type = PartType.ANTENNA_DISH, x = 0f, y = 5.6f, z = 0.9f, stageIndex = 2),
            PlacedPart(type = PartType.DECOUPLER, x = 0f, y = 2.6f, z = 0f, stageIndex = 2),
            // Interplanetary Injection Stage
            PlacedPart(type = PartType.TANK_LARGE, x = 0f, y = 0.2f, z = 0f, stageIndex = 1),
            PlacedPart(type = PartType.ENGINE_AEROSPIKE, x = 0f, y = -2.4f, z = 0f, stageIndex = 1),
            PlacedPart(type = PartType.DECOUPLER, x = 0f, y = -3.1f, z = 0f, stageIndex = 1),
            // First Stage Heavy Booster
            PlacedPart(type = PartType.TANK_LARGE, x = 0f, y = -5.4f, z = 0f, stageIndex = 0),
            PlacedPart(type = PartType.ENGINE_VECTOR, x = 0f, y = -8.0f, z = 0f, stageIndex = 0),
            PlacedPart(type = PartType.AERO_FIN, x = 1.1f, y = -7.4f, z = 0f, stageIndex = 0),
            PlacedPart(type = PartType.AERO_FIN, x = -1.1f, y = -7.4f, z = 0f, rotationYDeg = 180f, stageIndex = 0)
        )
        return RocketDesign(name = "Pyros Deep Space Cruiser", parts = parts)
    }

    val allStockDesigns: List<RocketDesign>
        get() = listOf(
            createCosmos1(),
            createLunarExplorer(),
            createTitanHeavy(),
            createStationCore(),
            createReusableFalcon(),
            createPyrosExplorer()
        )
}
