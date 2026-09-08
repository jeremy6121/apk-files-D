package com.example.cosmic.model

import java.util.UUID
import kotlin.math.abs
import kotlin.math.ln
import kotlin.math.sqrt

enum class PartCategory(val title: String) {
    COMMAND("Command & Probes"),
    FUEL("Fuel Tanks"),
    ENGINE("Engines & Thrusters"),
    AERO("Aerodynamics"),
    UTILITY("Utility & Staging"),
    POWER_RCS("Power & RCS"),
    STRUCTURAL("Structural")
}

enum class MeshShape {
    CYLINDER,
    CONE,
    TRUNCATED_CONE,
    BOX,
    FIN,
    LANDING_LEG,
    PANEL,
    RCS_NOZZLE,
    TRUSS,
    PARACHUTE_CANOPY
}

enum class AttachType {
    STACK,  // Top or bottom in-line stack
    RADIAL  // Surface mounted on side
}

data class AttachmentPoint(
    val localX: Float,
    val localY: Float,
    val localZ: Float,
    val attachType: AttachType = AttachType.STACK
)

enum class PartType(
    val displayName: String,
    val category: PartCategory,
    val dryMass: Double, // in kg
    val fuelCapacity: Double, // in kg
    val thrust: Double, // in Newtons
    val isp: Double, // specific impulse in seconds
    val dragCoeff: Double,
    val height: Float, // in meters
    val radius: Float, // in meters
    val shape: MeshShape,
    val primaryColorHex: Long,
    val accentColorHex: Long,
    val cost: Int, // in Cosmic Credits
    val techTier: Int, // 1: Starter, 2: Advanced, 3: Deep Space
    val unlockedByDefault: Boolean,
    val description: String
) {
    // --- COMMAND & PROBES ---
    COMMAND_CAPSULE(
        displayName = "Command Pod Mk1",
        category = PartCategory.COMMAND,
        dryMass = 800.0,
        fuelCapacity = 0.0,
        thrust = 0.0,
        isp = 0.0,
        dragCoeff = 0.25,
        height = 1.3f,
        radius = 0.8f,
        shape = MeshShape.TRUNCATED_CONE,
        primaryColorHex = 0xFFECEFF1,
        accentColorHex = 0xFF29B6F6,
        cost = 1200,
        techTier = 1,
        unlockedByDefault = true,
        description = "Manned crew capsule with built-in reaction wheels and flight avionics."
    ),
    PROBE_CORE(
        displayName = "Avionics Probe Core",
        category = PartCategory.COMMAND,
        dryMass = 180.0,
        fuelCapacity = 0.0,
        thrust = 0.0,
        isp = 0.0,
        dragCoeff = 0.2,
        height = 0.55f,
        radius = 0.55f,
        shape = MeshShape.CYLINDER,
        primaryColorHex = 0xFFB0BEC5,
        accentColorHex = 0xFFFFB300,
        cost = 900,
        techTier = 1,
        unlockedByDefault = true,
        description = "Lightweight autonomous guidance computer for unmanned satellite probes."
    ),

    // --- FUEL TANKS ---
    TANK_SMALL(
        displayName = "Hydrolox Tank S",
        category = PartCategory.FUEL,
        dryMass = 180.0,
        fuelCapacity = 900.0,
        thrust = 0.0,
        isp = 0.0,
        dragCoeff = 0.3,
        height = 1.5f,
        radius = 0.8f,
        shape = MeshShape.CYLINDER,
        primaryColorHex = 0xFFEEEEEE,
        accentColorHex = 0xFFE53935,
        cost = 450,
        techTier = 1,
        unlockedByDefault = true,
        description = "Compact pressurized fuel tank for upper orbital insertion stages."
    ),
    TANK_MEDIUM(
        displayName = "Hydrolox Tank M",
        category = PartCategory.FUEL,
        dryMass = 450.0,
        fuelCapacity = 2400.0,
        thrust = 0.0,
        isp = 0.0,
        dragCoeff = 0.3,
        height = 2.8f,
        radius = 0.8f,
        shape = MeshShape.CYLINDER,
        primaryColorHex = 0xFFFAFAFA,
        accentColorHex = 0xFFE53935,
        cost = 950,
        techTier = 1,
        unlockedByDefault = true,
        description = "Standard core booster fuel tank with balanced propellant capacity."
    ),
    TANK_LARGE(
        displayName = "Titan Heavy Tank",
        category = PartCategory.FUEL,
        dryMass = 950.0,
        fuelCapacity = 5200.0,
        thrust = 0.0,
        isp = 0.0,
        dragCoeff = 0.32,
        height = 4.2f,
        radius = 1.0f,
        shape = MeshShape.CYLINDER,
        primaryColorHex = 0xFFE0E0E0,
        accentColorHex = 0xFF1E88E5,
        cost = 1800,
        techTier = 2,
        unlockedByDefault = false,
        description = "Heavy-lift main stage fuel tank for deep-space interplanetary missions."
    ),
    TANK_RADIAL(
        displayName = "Radial Booster Tank",
        category = PartCategory.FUEL,
        dryMass = 380.0,
        fuelCapacity = 1900.0,
        thrust = 0.0,
        isp = 0.0,
        dragCoeff = 0.22,
        height = 3.2f,
        radius = 0.65f,
        shape = MeshShape.CYLINDER,
        primaryColorHex = 0xFFECEFF1,
        accentColorHex = 0xFFFF7043,
        cost = 800,
        techTier = 2,
        unlockedByDefault = false,
        description = "Streamlined side-mount propellant tank equipped with aerodynamic nose cap."
    ),

    // --- ENGINES & THRUSTERS ---
    ENGINE_VECTOR(
        displayName = "Titan Lifter Engine",
        category = PartCategory.ENGINE,
        dryMass = 550.0,
        fuelCapacity = 0.0,
        thrust = 185_000.0, // 185 kN
        isp = 295.0,
        dragCoeff = 0.4,
        height = 1.2f,
        radius = 0.75f,
        shape = MeshShape.CONE,
        primaryColorHex = 0xFF37474F,
        accentColorHex = 0xFFFF7043,
        cost = 1600,
        techTier = 1,
        unlockedByDefault = true,
        description = "High-thrust sea level booster engine with gimbal thrust vectoring."
    ),
    ENGINE_SPARK(
        displayName = "Spark Vacuum Engine",
        category = PartCategory.ENGINE,
        dryMass = 220.0,
        fuelCapacity = 0.0,
        thrust = 60_000.0, // 60 kN
        isp = 370.0,
        dragCoeff = 0.35,
        height = 0.95f,
        radius = 0.65f,
        shape = MeshShape.CONE,
        primaryColorHex = 0xFF455A64,
        accentColorHex = 0xFF42A5F5,
        cost = 1100,
        techTier = 1,
        unlockedByDefault = true,
        description = "High-efficiency vacuum engine optimized for orbital burns and lunar transfers."
    ),
    ENGINE_AEROSPIKE(
        displayName = "Nova Aerospike",
        category = PartCategory.ENGINE,
        dryMass = 380.0,
        fuelCapacity = 0.0,
        thrust = 110_000.0, // 110 kN
        isp = 335.0,
        dragCoeff = 0.32,
        height = 1.0f,
        radius = 0.7f,
        shape = MeshShape.CONE,
        primaryColorHex = 0xFF263238,
        accentColorHex = 0xFFAB47BC,
        cost = 2100,
        techTier = 2,
        unlockedByDefault = false,
        description = "Altitude-compensating aerospike maintaining steady efficiency across all atmospheric regimes."
    ),
    ENGINE_ION(
        displayName = "Photon Ion Thruster",
        category = PartCategory.ENGINE,
        dryMass = 120.0,
        fuelCapacity = 0.0,
        thrust = 15_000.0, // 15 kN
        isp = 1750.0, // Ultra high Isp
        dragCoeff = 0.25,
        height = 0.7f,
        radius = 0.5f,
        shape = MeshShape.CONE,
        primaryColorHex = 0xFF1A237E,
        accentColorHex = 0xFF00E5FF,
        cost = 3200,
        techTier = 3,
        unlockedByDefault = false,
        description = "Advanced xenon electric propulsion providing phenomenal specific impulse for probes."
    ),
    ENGINE_BOOSTER(
        displayName = "Solid Rocket Booster",
        category = PartCategory.ENGINE,
        dryMass = 290.0,
        fuelCapacity = 1400.0, // Internal solid fuel
        thrust = 145_000.0, // 145 kN
        isp = 260.0,
        dragCoeff = 0.28,
        height = 2.9f,
        radius = 0.6f,
        shape = MeshShape.CYLINDER,
        primaryColorHex = 0xFFF5F5F5,
        accentColorHex = 0xFFD84315,
        cost = 750,
        techTier = 1,
        unlockedByDefault = true,
        description = "Pre-packed solid propellant strap-on booster providing massive takeoff thrust."
    ),

    // --- AERODYNAMICS ---
    NOSE_CONE(
        displayName = "Aero Nose Cone",
        category = PartCategory.AERO,
        dryMass = 90.0,
        fuelCapacity = 0.0,
        thrust = 0.0,
        isp = 0.0,
        dragCoeff = 0.08,
        height = 1.4f,
        radius = 0.8f,
        shape = MeshShape.CONE,
        primaryColorHex = 0xFFECEFF1,
        accentColorHex = 0xFFD32F2F,
        cost = 250,
        techTier = 1,
        unlockedByDefault = true,
        description = "Sharply profiled aerodynamic fairing cutting atmospheric friction during ascent."
    ),
    NOSE_CONE_BLUNT(
        displayName = "Thermal Shield Cone",
        category = PartCategory.AERO,
        dryMass = 140.0,
        fuelCapacity = 0.0,
        thrust = 0.0,
        isp = 0.0,
        dragCoeff = 0.16,
        height = 1.0f,
        radius = 0.8f,
        shape = MeshShape.TRUNCATED_CONE,
        primaryColorHex = 0xFF3E2723,
        accentColorHex = 0xFFFF6F00,
        cost = 600,
        techTier = 2,
        unlockedByDefault = false,
        description = "Ablative re-entry nose cone protecting payload against extreme hypersonic compression heat."
    ),
    AERO_FIN(
        displayName = "Stabilizer Fin",
        category = PartCategory.AERO,
        dryMass = 35.0,
        fuelCapacity = 0.0,
        thrust = 0.0,
        isp = 0.0,
        dragCoeff = 0.05,
        height = 1.1f,
        radius = 0.9f,
        shape = MeshShape.FIN,
        primaryColorHex = 0xFFCFD8DC,
        accentColorHex = 0xFFE53935,
        cost = 180,
        techTier = 1,
        unlockedByDefault = true,
        description = "Aerodynamic winglet providing passive self-centering stability in dense atmosphere."
    ),
    CANARD_FIN(
        displayName = "Steerable Canard",
        category = PartCategory.AERO,
        dryMass = 45.0,
        fuelCapacity = 0.0,
        thrust = 0.0,
        isp = 0.0,
        dragCoeff = 0.06,
        height = 0.8f,
        radius = 0.7f,
        shape = MeshShape.FIN,
        primaryColorHex = 0xFF90A4AE,
        accentColorHex = 0xFF0288D1,
        cost = 380,
        techTier = 2,
        unlockedByDefault = false,
        description = "Active trim canard providing rapid pitch control response during high dynamic pressure."
    ),

    // --- UTILITY & STAGING ---
    DECOUPLER(
        displayName = "Stack Decoupler",
        category = PartCategory.UTILITY,
        dryMass = 80.0,
        fuelCapacity = 0.0,
        thrust = 0.0,
        isp = 0.0,
        dragCoeff = 0.2,
        height = 0.35f,
        radius = 0.82f,
        shape = MeshShape.CYLINDER,
        primaryColorHex = 0xFF455A64,
        accentColorHex = 0xFFFFEB3B,
        cost = 400,
        techTier = 1,
        unlockedByDefault = true,
        description = "In-line explosive staging ring discarding spent booster stages with pneumatic impulse."
    ),
    DECOUPLER_RADIAL(
        displayName = "Radial Decoupler",
        category = PartCategory.UTILITY,
        dryMass = 65.0,
        fuelCapacity = 0.0,
        thrust = 0.0,
        isp = 0.0,
        dragCoeff = 0.15,
        height = 0.6f,
        radius = 0.4f,
        shape = MeshShape.BOX,
        primaryColorHex = 0xFF546E7A,
        accentColorHex = 0xFFFF9800,
        cost = 450,
        techTier = 2,
        unlockedByDefault = false,
        description = "Surface-mounted explosive clamp releasing strap-on side boosters safely outward."
    ),
    LANDING_LEGS(
        displayName = "Lunar Landing Struts",
        category = PartCategory.UTILITY,
        dryMass = 110.0,
        fuelCapacity = 0.0,
        thrust = 0.0,
        isp = 0.0,
        dragCoeff = 0.25,
        height = 1.2f,
        radius = 0.85f,
        shape = MeshShape.LANDING_LEG,
        primaryColorHex = 0xFF78909C,
        accentColorHex = 0xFF66BB6A,
        cost = 550,
        techTier = 1,
        unlockedByDefault = true,
        description = "Deployable hydraulic shock struts engineered for soft touchdown on planetary terrain."
    ),
    LANDING_LEGS_HEAVY(
        displayName = "Heavy Planetary Legs",
        category = PartCategory.UTILITY,
        dryMass = 220.0,
        fuelCapacity = 0.0,
        thrust = 0.0,
        isp = 0.0,
        dragCoeff = 0.3,
        height = 1.7f,
        radius = 1.1f,
        shape = MeshShape.LANDING_LEG,
        primaryColorHex = 0xFF37474F,
        accentColorHex = 0xFF43A047,
        cost = 950,
        techTier = 2,
        unlockedByDefault = false,
        description = "Reinforced wide-stance landing legs capable of absorbing high-mass planetary impacts."
    ),
    PARACHUTE(
        displayName = "Main Parachute",
        category = PartCategory.UTILITY,
        dryMass = 95.0,
        fuelCapacity = 0.0,
        thrust = 0.0,
        isp = 0.0,
        dragCoeff = 0.15,
        height = 0.4f,
        radius = 0.65f,
        shape = MeshShape.CONE,
        primaryColorHex = 0xFFECEFF1,
        accentColorHex = 0xFFFB8C00,
        cost = 350,
        techTier = 1,
        unlockedByDefault = true,
        description = "Full-size atmospheric descent parachute slowing capsules to gentle touchdown speeds."
    ),
    PARACHUTE_DROGUE(
        displayName = "Drogue Parachute",
        category = PartCategory.UTILITY,
        dryMass = 50.0,
        fuelCapacity = 0.0,
        thrust = 0.0,
        isp = 0.0,
        dragCoeff = 0.12,
        height = 0.35f,
        radius = 0.5f,
        shape = MeshShape.CONE,
        primaryColorHex = 0xFFFAFAFA,
        accentColorHex = 0xFF00ACC1,
        cost = 420,
        techTier = 2,
        unlockedByDefault = false,
        description = "High-speed drogue chute deployed at supersonic speeds to stabilize chaotic re-entry spin."
    ),

    // --- POWER & RCS ---
    SOLAR_PANEL(
        displayName = "Deployable Solar Array",
        category = PartCategory.POWER_RCS,
        dryMass = 40.0,
        fuelCapacity = 0.0,
        thrust = 0.0,
        isp = 0.0,
        dragCoeff = 0.1,
        height = 1.4f,
        radius = 0.45f,
        shape = MeshShape.PANEL,
        primaryColorHex = 0xFF0D47A1,
        accentColorHex = 0xFF00E5FF,
        cost = 650,
        techTier = 2,
        unlockedByDefault = false,
        description = "High-efficiency photovoltaic solar panels providing continuous electrical power."
    ),
    BATTERY_PACK(
        displayName = "Li-Ion Energy Cell",
        category = PartCategory.POWER_RCS,
        dryMass = 60.0,
        fuelCapacity = 0.0,
        thrust = 0.0,
        isp = 0.0,
        dragCoeff = 0.12,
        height = 0.4f,
        radius = 0.65f,
        shape = MeshShape.CYLINDER,
        primaryColorHex = 0xFF37474F,
        accentColorHex = 0xFF76FF03,
        cost = 400,
        techTier = 2,
        unlockedByDefault = false,
        description = "High-capacity rechargeable battery bank sustaining systems through eclipse shadow."
    ),
    RCS_BLOCK(
        displayName = "4-Way RCS Thruster",
        category = PartCategory.POWER_RCS,
        dryMass = 25.0,
        fuelCapacity = 0.0,
        thrust = 4_000.0, // 4 kN
        isp = 240.0,
        dragCoeff = 0.05,
        height = 0.3f,
        radius = 0.3f,
        shape = MeshShape.RCS_NOZZLE,
        primaryColorHex = 0xFF455A64,
        accentColorHex = 0xFFECEFF1,
        cost = 500,
        techTier = 2,
        unlockedByDefault = false,
        description = "Cold-gas reaction control thruster block granting precise translational & docking control."
    ),

    // --- STRUCTURAL ---
    STRUCTURAL_TRUSS(
        displayName = "Girder Truss Adapter",
        category = PartCategory.STRUCTURAL,
        dryMass = 50.0,
        fuelCapacity = 0.0,
        thrust = 0.0,
        isp = 0.0,
        dragCoeff = 0.18,
        height = 1.2f,
        radius = 0.7f,
        shape = MeshShape.TRUSS,
        primaryColorHex = 0xFF78909C,
        accentColorHex = 0xFF9E9E9E,
        cost = 200,
        techTier = 1,
        unlockedByDefault = true,
        description = "Lightweight hollow alloy truss section for payload separation and structural offset."
    ),

    // --- V3 SPACE STATIONS, DOCKING & TELEMETRY ---
    DOCKING_PORT(
        displayName = "Orbital Docking Clamp",
        category = PartCategory.UTILITY,
        dryMass = 120.0,
        fuelCapacity = 0.0,
        thrust = 0.0,
        isp = 0.0,
        dragCoeff = 0.22,
        height = 0.4f,
        radius = 0.75f,
        shape = MeshShape.CYLINDER,
        primaryColorHex = 0xFFECEFF1,
        accentColorHex = 0xFF00E676,
        cost = 750,
        techTier = 2,
        unlockedByDefault = false,
        description = "Magnetic and mechanical orbital docking ring for connecting space station modules and visiting spacecraft."
    ),
    STATION_HABITAT(
        displayName = "Station Crew Habitat",
        category = PartCategory.COMMAND,
        dryMass = 1200.0,
        fuelCapacity = 0.0,
        thrust = 0.0,
        isp = 0.0,
        dragCoeff = 0.35,
        height = 2.4f,
        radius = 0.95f,
        shape = MeshShape.CYLINDER,
        primaryColorHex = 0xFFFAFAFA,
        accentColorHex = 0xFF29B6F6,
        cost = 1800,
        techTier = 2,
        unlockedByDefault = false,
        description = "Pressurized orbital living quarters accommodating up to 3 crew astronauts in microgravity comfort."
    ),
    STATION_SCIENCE_LAB(
        displayName = "Orbital Science Lab",
        category = PartCategory.COMMAND,
        dryMass = 1400.0,
        fuelCapacity = 0.0,
        thrust = 0.0,
        isp = 0.0,
        dragCoeff = 0.35,
        height = 2.6f,
        radius = 0.95f,
        shape = MeshShape.CYLINDER,
        primaryColorHex = 0xFFECEFF1,
        accentColorHex = 0xFFAB47BC,
        cost = 2400,
        techTier = 2,
        unlockedByDefault = false,
        description = "Equipped with microgravity spectroscopy and sample processing units yielding research credits."
    ),
    SOLAR_ARRAY_HEAVY(
        displayName = "Station Solar Truss Wing",
        category = PartCategory.POWER_RCS,
        dryMass = 95.0,
        fuelCapacity = 0.0,
        thrust = 0.0,
        isp = 0.0,
        dragCoeff = 0.2,
        height = 2.2f,
        radius = 0.6f,
        shape = MeshShape.PANEL,
        primaryColorHex = 0xFF0D47A1,
        accentColorHex = 0xFFFFD600,
        cost = 1200,
        techTier = 2,
        unlockedByDefault = false,
        description = "Large dual-wing photovoltaic array generating massive electrical current for space stations."
    ),
    BATTERY_BANK_HEAVY(
        displayName = "Station Storage Battery",
        category = PartCategory.POWER_RCS,
        dryMass = 130.0,
        fuelCapacity = 0.0,
        thrust = 0.0,
        isp = 0.0,
        dragCoeff = 0.18,
        height = 0.6f,
        radius = 0.8f,
        shape = MeshShape.CYLINDER,
        primaryColorHex = 0xFF37474F,
        accentColorHex = 0xFF00E676,
        cost = 850,
        techTier = 2,
        unlockedByDefault = false,
        description = "Industrial-grade energy storage bank powering space station life support during orbital night."
    ),
    HEAT_SHIELD_HEAVY(
        displayName = "Planetary Heat Shield",
        category = PartCategory.AERO,
        dryMass = 240.0,
        fuelCapacity = 0.0,
        thrust = 0.0,
        isp = 0.0,
        dragCoeff = 0.45,
        height = 0.45f,
        radius = 1.0f,
        shape = MeshShape.TRUNCATED_CONE,
        primaryColorHex = 0xFF212121,
        accentColorHex = 0xFFFF5722,
        cost = 800,
        techTier = 2,
        unlockedByDefault = false,
        description = "Heavy ablative thermal shield dissipating extreme atmospheric entry heat at Pyros, Novaria, or Aeris."
    ),
    ANTENNA_DISH(
        displayName = "High-Gain Comms Dish",
        category = PartCategory.POWER_RCS,
        dryMass = 35.0,
        fuelCapacity = 0.0,
        thrust = 0.0,
        isp = 0.0,
        dragCoeff = 0.08,
        height = 0.9f,
        radius = 0.55f,
        shape = MeshShape.CONE,
        primaryColorHex = 0xFFCFD8DC,
        accentColorHex = 0xFF0288D1,
        cost = 550,
        techTier = 2,
        unlockedByDefault = false,
        description = "Steerable parabolic communication dish maintaining constant telemetry across interplanetary distances."
    );

    val fuelConsumptionRate: Double
        get() = if (isp > 0 && thrust > 0) thrust / (isp * 9.80665) else 0.0

    /**
     * Standard attachment points in local part coordinates.
     */
    val attachmentPoints: List<AttachmentPoint>
        get() {
            val halfH = height / 2f
            return when (category) {
                PartCategory.AERO -> {
                    if (shape == MeshShape.FIN) {
                        listOf(AttachmentPoint(0f, 0f, 0f, AttachType.RADIAL))
                    } else if (this == HEAT_SHIELD_HEAVY) {
                        listOf(
                            AttachmentPoint(0f, halfH, 0f, AttachType.STACK),
                            AttachmentPoint(0f, -halfH, 0f, AttachType.STACK)
                        )
                    } else {
                        // Nose cone has bottom stack attach
                        listOf(AttachmentPoint(0f, -halfH, 0f, AttachType.STACK))
                    }
                }
                PartCategory.ENGINE -> {
                    // Engine has top stack attach
                    listOf(AttachmentPoint(0f, halfH, 0f, AttachType.STACK))
                }
                PartCategory.UTILITY -> {
                    if (shape == MeshShape.LANDING_LEG) {
                        listOf(AttachmentPoint(0f, 0f, 0f, AttachType.RADIAL))
                    } else if (shape == MeshShape.BOX) {
                        listOf(AttachmentPoint(0f, 0f, 0f, AttachType.RADIAL))
                    } else {
                        // Decoupler / Parachute
                        listOf(
                            AttachmentPoint(0f, halfH, 0f, AttachType.STACK),
                            AttachmentPoint(0f, -halfH, 0f, AttachType.STACK)
                        )
                    }
                }
                PartCategory.POWER_RCS -> {
                    listOf(AttachmentPoint(0f, 0f, 0f, AttachType.RADIAL))
                }
                else -> {
                    // Standard Stack parts (Tanks, Command Pods, Trusses)
                    listOf(
                        AttachmentPoint(0f, halfH, 0f, AttachType.STACK),
                        AttachmentPoint(0f, -halfH, 0f, AttachType.STACK),
                        AttachmentPoint(radius, 0f, 0f, AttachType.RADIAL),
                        AttachmentPoint(-radius, 0f, 0f, AttachType.RADIAL),
                        AttachmentPoint(0f, 0f, radius, AttachType.RADIAL),
                        AttachmentPoint(0f, 0f, -radius, AttachType.RADIAL)
                    )
                }
            }
        }
}

data class PlacedPart(
    val id: String = UUID.randomUUID().toString(),
    val type: PartType,
    var x: Float = 0f,
    var y: Float = 0f, // vertical stacking offset in meters
    var z: Float = 0f,
    var rotationYDeg: Float = 0f,
    var stageIndex: Int = 0,
    var fuelRemaining: Double = type.fuelCapacity,
    var isDecoupled: Boolean = false,
    var isDeployed: Boolean = false
) {
    fun dryMass(): Double = type.dryMass
    fun currentMass(): Double = type.dryMass + fuelRemaining
    fun isFuelEmpty(): Boolean = type.fuelCapacity > 0 && fuelRemaining <= 0.01

    val topY: Float get() = y + type.height / 2f
    val bottomY: Float get() = y - type.height / 2f

    fun copyPlacedPart(): PlacedPart = PlacedPart(
        id = UUID.randomUUID().toString(),
        type = type,
        x = x,
        y = y,
        z = z,
        rotationYDeg = rotationYDeg,
        stageIndex = stageIndex,
        fuelRemaining = fuelRemaining,
        isDecoupled = isDecoupled,
        isDeployed = isDeployed
    )
}

data class RocketDesign(
    var name: String = "Cosmic Explorer",
    val parts: MutableList<PlacedPart> = mutableListOf()
) {
    fun totalMass(): Double = parts.filter { !it.isDecoupled }.sumOf { it.currentMass() }
    fun totalDryMass(): Double = parts.filter { !it.isDecoupled }.sumOf { it.dryMass() }
    fun totalFuelRemaining(): Double = parts.filter { !it.isDecoupled }.sumOf { it.fuelRemaining }
    fun totalFuelCapacity(): Double = parts.filter { !it.isDecoupled }.sumOf { it.type.fuelCapacity }
    fun totalCost(): Int = parts.sumOf { it.type.cost }

    val crewCapacity: Int
        get() = parts.filter { !it.isDecoupled }.sumOf {
            when (it.type) {
                PartType.COMMAND_CAPSULE -> 1
                PartType.STATION_HABITAT -> 3
                else -> 0
            }
        }

    val hasDockingPort: Boolean
        get() = parts.any { !it.isDecoupled && it.type == PartType.DOCKING_PORT }

    fun thrustForStage(stage: Int): Double {
        return parts.filter { !it.isDecoupled && it.stageIndex == stage }
            .sumOf { it.type.thrust }
    }

    fun twr(stage: Int = 0, g: Double = 9.81): Double {
        val mass = totalMass()
        if (mass <= 0.0) return 0.0
        val thrust = thrustForStage(stage)
        return thrust / (mass * g)
    }

    fun totalEstimatedDeltaV(): Double {
        val stages = parts.filter { !it.isDecoupled }.map { it.stageIndex }.distinct().sorted()
        var totalDv = 0.0

        for (stage in stages) {
            val stageParts = parts.filter { !it.isDecoupled && it.stageIndex >= stage }
            val stageEngines = stageParts.filter { it.type.category == PartCategory.ENGINE && it.stageIndex == stage }
            val stageTanks = stageParts.filter { (it.type.category == PartCategory.FUEL || it.type.fuelCapacity > 0) && it.stageIndex == stage }

            if (stageEngines.isEmpty() || stageTanks.isEmpty()) continue

            val avgIsp = stageEngines.map { it.type.isp }.average()
            val stageFuel = stageTanks.sumOf { it.fuelRemaining }
            val m0 = stageParts.sumOf { it.currentMass() }
            val mf = (m0 - stageFuel).coerceAtLeast(10.0)

            if (m0 > mf && avgIsp > 0) {
                val stageDv = avgIsp * 9.80665 * ln(m0 / mf)
                totalDv += stageDv
            }
        }
        return if (totalDv > 0) totalDv else {
            val engines = parts.filter { it.type.category == PartCategory.ENGINE }
            if (engines.isEmpty()) return 0.0
            val avgIsp = engines.map { it.type.isp }.average()
            val m0 = totalMass()
            val mf = totalDryMass().coerceAtLeast(10.0)
            avgIsp * 9.80665 * ln(m0 / mf)
        }
    }

    fun getStagesCount(): Int {
        if (parts.isEmpty()) return 1
        return (parts.maxOfOrNull { it.stageIndex } ?: 0) + 1
    }

    /**
     * Automatically snaps a target part to the most suitable existing part's attachment point.
     * Returns true if snapped.
     */
    fun autoSnapPart(target: PlacedPart, snapThreshold: Float = 1.4f): Boolean {
        val otherParts = parts.filter { it.id != target.id }
        if (otherParts.isEmpty()) {
            target.x = 0f
            target.z = 0f
            return true
        }

        // For Radial parts (Fins, Landing Legs, Radial Decouplers, Solar Panels, RCS)
        if (target.type.category in listOf(PartCategory.AERO, PartCategory.POWER_RCS) ||
            target.type in listOf(PartType.LANDING_LEGS, PartType.LANDING_LEGS_HEAVY, PartType.DECOUPLER_RADIAL, PartType.TANK_RADIAL)) {
            // Find closest main vertical column part (x~0, z~0)
            val coreParts = otherParts.filter { abs(it.x) < 0.2f && abs(it.z) < 0.2f }
            if (coreParts.isNotEmpty()) {
                val nearestCore = coreParts.minByOrNull { abs(it.y - target.y) } ?: coreParts.first()
                val attachR = nearestCore.type.radius + target.type.radius * 0.4f

                // Align to closest quadrant (0, 90, 180, 270 deg)
                val angleRad = Math.toRadians(target.rotationYDeg.toDouble())
                target.x = (attachR * Math.cos(angleRad)).toFloat()
                target.z = (attachR * Math.sin(angleRad)).toFloat()
                return true
            }
        }

        // For Stack parts (Tanks, Engines, Capsules, Nose Cones, Stack Decouplers, Trusses)
        // Find nearest part in vertical stack
        val stackParts = otherParts.filter { abs(it.x - target.x) < 0.5f && abs(it.z - target.z) < 0.5f }
        if (stackParts.isNotEmpty()) {
            val nearest = stackParts.minByOrNull { abs(it.y - target.y) } ?: stackParts.first()

            // Snap above or below
            if (target.y >= nearest.y) {
                // Stack ABOVE nearest
                target.y = nearest.topY + target.type.height / 2f
            } else {
                // Stack BELOW nearest
                target.y = nearest.bottomY - target.type.height / 2f
            }
            target.x = nearest.x
            target.z = nearest.z
            return true
        }

        return false
    }

    fun clone(): RocketDesign {
        return RocketDesign(
            name = name,
            parts = parts.map { it.copyPlacedPart() }.toMutableList()
        )
    }
}
