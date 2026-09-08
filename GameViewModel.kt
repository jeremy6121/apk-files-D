package com.example.cosmic.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cosmic.audio.CosmicAudioEngine
import com.example.cosmic.math.Vector3D
import com.example.cosmic.model.*
import com.example.cosmic.physics.*
import com.example.cosmic.renderer.CameraMode
import com.example.cosmic.renderer.CosmicGLRenderer
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

enum class AppScreen {
    MAIN_MENU,
    BUILDER,
    FLIGHT,
    MISSIONS,
    FLEET,
    CREW,
    TECH_TREE,
    SETTINGS
}

class GameViewModel(application: Application) : AndroidViewModel(application) {

    val glRenderer = CosmicGLRenderer()
    val saveSystem = SaveSystem(application.applicationContext)
    val progressionSystem = ProgressionSystem(application.applicationContext)
    val missionManager = MissionManager(progressionSystem, saveSystem)
    val audioEngine = CosmicAudioEngine(application.applicationContext)
    val crewSystem = CrewSystem(application.applicationContext)
    val fleetManager = FleetManager(application.applicationContext)

    // Navigation state
    private val _currentScreen = MutableStateFlow(AppScreen.MAIN_MENU)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    // Active rocket design in Builder
    private val _activeRocket = MutableStateFlow(StockRockets.createCosmos1())
    val activeRocket: StateFlow<RocketDesign> = _activeRocket.asStateFlow()

    // Currently selected part in Builder
    private val _selectedPart = MutableStateFlow<PlacedPart?>(null)
    val selectedPart: StateFlow<PlacedPart?> = _selectedPart.asStateFlow()

    // Builder category filter
    private val _activeCategory = MutableStateFlow(PartCategory.COMMAND)
    val activeCategory: StateFlow<PartCategory> = _activeCategory.asStateFlow()

    // Undo / Redo history stacks
    private val undoStack = ArrayDeque<List<PlacedPart>>()
    private val redoStack = ArrayDeque<List<PlacedPart>>()

    private val _canUndo = MutableStateFlow(false)
    val canUndo: StateFlow<Boolean> = _canUndo.asStateFlow()

    private val _canRedo = MutableStateFlow(false)
    val canRedo: StateFlow<Boolean> = _canRedo.asStateFlow()

    // Flight state
    private val _flightState = MutableStateFlow<FlightState?>(null)
    val flightState: StateFlow<FlightState?> = _flightState.asStateFlow()

    // Flight controls input
    var touchPitchInput: Float = 0f
    var touchYawInput: Float = 0f

    private val _isPaused = MutableStateFlow(false)
    val isPaused: StateFlow<Boolean> = _isPaused.asStateFlow()

    private val _isOrbitMapActive = MutableStateFlow(false)
    val isOrbitMapActive: StateFlow<Boolean> = _isOrbitMapActive.asStateFlow()

    private var flightJob: Job? = null

    // Saved rockets list
    private val _savedRocketsList = MutableStateFlow<List<String>>(emptyList())
    val savedRocketsList: StateFlow<List<String>> = _savedRocketsList.asStateFlow()

    // Currency balance
    private val _credits = MutableStateFlow(progressionSystem.credits)
    val credits: StateFlow<Int> = _credits.asStateFlow()

    // Notification / Alert message toast
    private val _statusAlert = MutableStateFlow<String?>(null)
    val statusAlert: StateFlow<String?> = _statusAlert.asStateFlow()

    // Crew assigned to next launch
    private val _assignedCrewIds = MutableStateFlow<Set<String>>(emptySet())
    val assignedCrewIds: StateFlow<Set<String>> = _assignedCrewIds.asStateFlow()

    // Target Celestial Body for interplanetary navigation
    private val _targetBody = MutableStateFlow<CelestialBody?>(null)
    val targetBody: StateFlow<CelestialBody?> = _targetBody.asStateFlow()

    // Nearest docking target info
    private val _nearestDockTarget = MutableStateFlow<OrbitingVessel?>(null)
    val nearestDockTarget: StateFlow<OrbitingVessel?> = _nearestDockTarget.asStateFlow()

    private val _dockingDistance = MutableStateFlow<Double?>(null)
    val dockingDistance: StateFlow<Double?> = _dockingDistance.asStateFlow()

    private val _isDockingRange = MutableStateFlow(false)
    val isDockingRange: StateFlow<Boolean> = _isDockingRange.asStateFlow()

    init {
        refreshSavedRockets()
        glRenderer.currentRocket = _activeRocket.value
        glRenderer.fleetManager = fleetManager
    }

    fun navigateTo(screen: AppScreen) {
        audioEngine.playButtonClick()
        _currentScreen.value = screen
        when (screen) {
            AppScreen.BUILDER -> {
                stopFlightSimulation()
                audioEngine.stopEngineRoar()
                glRenderer.flightState = null
                glRenderer.camera.setModePreset(CameraMode.BUILDER)
            }
            AppScreen.FLIGHT -> {
                if (_flightState.value == null) {
                    startFlightSimulation()
                }
            }
            else -> {
                stopFlightSimulation()
                audioEngine.stopEngineRoar()
            }
        }
    }

    fun playClickSound() {
        audioEngine.playButtonClick()
    }

    private fun setStatusAlert(message: String) {
        _statusAlert.value = message
        viewModelScope.launch {
            delay(3200)
            if (_statusAlert.value == message) {
                _statusAlert.value = null
            }
        }
    }

    private fun refreshSavedRockets() {
        _savedRocketsList.value = saveSystem.listSavedRockets()
    }

    fun refreshCredits() {
        _credits.value = progressionSystem.credits
    }

    // --- CREW ROSTER ACTIONS ---

    fun toggleAssignCrew(astronautId: String) {
        audioEngine.playButtonClick()
        val capacity = _activeRocket.value.crewCapacity
        val current = _assignedCrewIds.value.toMutableSet()
        if (current.contains(astronautId)) {
            current.remove(astronautId)
            crewSystem.unassignCrew(astronautId)
            setStatusAlert("Astronaut unassigned")
        } else {
            if (current.size >= capacity) {
                setStatusAlert("Rocket crew capacity reached ($capacity astronauts max)")
                return
            }
            current.add(astronautId)
            crewSystem.assignCrew(astronautId, _activeRocket.value.name)
            setStatusAlert("Astronaut assigned to launch")
        }
        _assignedCrewIds.value = current
    }

    fun recruitAstronaut(name: String, role: CrewRole) {
        audioEngine.playButtonClick()
        val cost = 800
        if (progressionSystem.credits >= cost) {
            progressionSystem.spendCredits(cost)
            refreshCredits()
            crewSystem.hireAstronaut(name, role)
            setStatusAlert("Recruited $name as ${role.title} (-$cost C)")
        } else {
            setStatusAlert("Need $cost credits to recruit astronaut")
        }
    }

    // --- INTERPLANETARY TARGETING ---

    fun selectTargetBody(body: CelestialBody?) {
        audioEngine.playButtonClick()
        _targetBody.value = body
        _flightState.value?.targetBody = body
        if (body != null) {
            setStatusAlert("Navigation Target: ${body.name}")
        } else {
            setStatusAlert("Target cleared")
        }
    }

    // --- BUILDER ACTIONS ---

    fun setActiveCategory(cat: PartCategory) {
        audioEngine.playButtonClick()
        _activeCategory.value = cat
    }

    fun selectPart(part: PlacedPart?) {
        _selectedPart.value = part
        glRenderer.selectedPartId = part?.id
    }

    private fun pushUndoSnapshot() {
        val snapshot = _activeRocket.value.parts.map { it.copyPlacedPart() }
        undoStack.addLast(snapshot)
        if (undoStack.size > 25) undoStack.removeFirst()
        redoStack.clear()
        _canUndo.value = true
        _canRedo.value = false
    }

    fun undo() {
        if (undoStack.isEmpty()) return
        audioEngine.playButtonClick()
        val currentSnapshot = _activeRocket.value.parts.map { it.copyPlacedPart() }
        redoStack.addLast(currentSnapshot)

        val previous = undoStack.removeLast()
        val rocket = _activeRocket.value
        rocket.parts.clear()
        rocket.parts.addAll(previous)
        _activeRocket.value = rocket.clone()
        _selectedPart.value = null
        glRenderer.selectedPartId = null
        glRenderer.currentRocket = _activeRocket.value

        _canUndo.value = undoStack.isNotEmpty()
        _canRedo.value = true
        setStatusAlert("Undo")
    }

    fun redo() {
        if (redoStack.isEmpty()) return
        audioEngine.playButtonClick()
        val currentSnapshot = _activeRocket.value.parts.map { it.copyPlacedPart() }
        undoStack.addLast(currentSnapshot)

        val next = redoStack.removeLast()
        val rocket = _activeRocket.value
        rocket.parts.clear()
        rocket.parts.addAll(next)
        _activeRocket.value = rocket.clone()
        _selectedPart.value = null
        glRenderer.selectedPartId = null
        glRenderer.currentRocket = _activeRocket.value

        _canUndo.value = true
        _canRedo.value = redoStack.isNotEmpty()
        setStatusAlert("Redo")
    }

    fun addPartToAssembly(type: PartType) {
        audioEngine.playButtonClick()
        if (!progressionSystem.isPartUnlocked(type)) {
            setStatusAlert("Locked part: Unlock in Tech Tree first!")
            return
        }

        pushUndoSnapshot()
        val current = _activeRocket.value

        var spawnY = 0f
        if (current.parts.isNotEmpty()) {
            val topPart = current.parts.maxByOrNull { it.topY }
            if (topPart != null) {
                spawnY = topPart.topY + type.height / 2f
            }
        }

        val newPart = PlacedPart(
            type = type,
            x = 0f,
            y = spawnY,
            z = 0f,
            stageIndex = current.parts.maxOfOrNull { it.stageIndex } ?: 0
        )

        current.parts.add(newPart)
        _activeRocket.value = current.clone()
        glRenderer.currentRocket = _activeRocket.value
        selectPart(newPart)
        setStatusAlert("Added ${type.displayName}")
    }

    fun removeSelectedPart() {
        val selected = _selectedPart.value ?: return
        audioEngine.playButtonClick()
        pushUndoSnapshot()
        val current = _activeRocket.value
        current.parts.removeAll { it.id == selected.id }
        _selectedPart.value = null
        glRenderer.selectedPartId = null
        _activeRocket.value = current.clone()
        glRenderer.currentRocket = _activeRocket.value
        setStatusAlert("Part removed")
    }

    fun duplicateSelectedPart() {
        val selected = _selectedPart.value ?: return
        audioEngine.playButtonClick()
        pushUndoSnapshot()
        val current = _activeRocket.value
        val cloned = selected.copyPlacedPart()
        cloned.x += 1.0f
        current.parts.add(cloned)
        _activeRocket.value = current.clone()
        glRenderer.currentRocket = _activeRocket.value
        selectPart(cloned)
        setStatusAlert("Duplicated part")
    }

    fun moveSelectedPart(dx: Float, dy: Float, dz: Float) {
        val selected = _selectedPart.value ?: return
        selected.x += dx
        selected.y += dy
        selected.z += dz
        _activeRocket.value = _activeRocket.value.clone()
        glRenderer.currentRocket = _activeRocket.value
    }

    fun snapSelectedPart() {
        val selected = _selectedPart.value ?: return
        audioEngine.playButtonClick()
        pushUndoSnapshot()
        val snapped = _activeRocket.value.autoSnapPart(selected)
        if (snapped) {
            _activeRocket.value = _activeRocket.value.clone()
            glRenderer.currentRocket = _activeRocket.value
            setStatusAlert("Snapped to nearest attachment point")
        } else {
            setStatusAlert("No nearby attachment point")
        }
    }

    fun rotateSelectedPart(angleDeltaDeg: Float) {
        val selected = _selectedPart.value ?: return
        audioEngine.playButtonClick()
        selected.rotationYDeg = (selected.rotationYDeg + angleDeltaDeg) % 360f
        _activeRocket.value = _activeRocket.value.clone()
        glRenderer.currentRocket = _activeRocket.value
    }

    fun setSelectedPartStage(newStage: Int) {
        val selected = _selectedPart.value ?: return
        pushUndoSnapshot()
        selected.stageIndex = newStage.coerceAtLeast(0)
        _activeRocket.value = _activeRocket.value.clone()
    }

    fun movePartStage(part: PlacedPart, delta: Int) {
        pushUndoSnapshot()
        part.stageIndex = (part.stageIndex + delta).coerceAtLeast(0)
        _activeRocket.value = _activeRocket.value.clone()
    }

    fun loadDesign(design: RocketDesign) {
        audioEngine.playButtonClick()
        pushUndoSnapshot()
        _activeRocket.value = design.clone()
        _selectedPart.value = null
        glRenderer.selectedPartId = null
        glRenderer.currentRocket = _activeRocket.value
        setStatusAlert("Loaded ${design.name}")
    }

    fun saveActiveRocket(customName: String) {
        audioEngine.playButtonClick()
        val trimmed = customName.trim().ifEmpty { "Cosmic Rocket" }
        val current = _activeRocket.value
        val toSave = current.copy(name = trimmed)
        _activeRocket.value = toSave
        saveSystem.saveRocket(toSave)
        refreshSavedRockets()
        setStatusAlert("Saved rocket as $trimmed")
    }

    fun deleteSavedRocket(name: String) {
        audioEngine.playButtonClick()
        saveSystem.deleteRocket(name)
        refreshSavedRockets()
        setStatusAlert("Deleted $name")
    }

    fun clearRocketAssembly() {
        audioEngine.playButtonClick()
        pushUndoSnapshot()
        val current = _activeRocket.value
        current.parts.clear()
        _activeRocket.value = current.clone()
        _selectedPart.value = null
        glRenderer.selectedPartId = null
        glRenderer.currentRocket = _activeRocket.value
        setStatusAlert("Hangar cleared")
    }

    // --- PROGRESSION & TECH TREE ACTIONS ---

    fun unlockPart(type: PartType): Boolean {
        audioEngine.playButtonClick()
        val success = progressionSystem.unlockPart(type)
        if (success) {
            refreshCredits()
            audioEngine.playMissionSuccess()
            setStatusAlert("Unlocked ${type.displayName}!")
        } else {
            setStatusAlert("Need ${type.cost} Credits (You have ${_credits.value})")
        }
        return success
    }

    fun upgradePart(type: PartType): Boolean {
        audioEngine.playButtonClick()
        val success = progressionSystem.upgradePart(type)
        if (success) {
            refreshCredits()
            setStatusAlert("Upgraded ${type.displayName}!")
        } else {
            setStatusAlert("Insufficient credits for upgrade")
        }
        return success
    }

    // --- FLIGHT SIMULATION ACTIONS ---

    fun startFlightSimulation() {
        val rocket = _activeRocket.value
        if (rocket.parts.isEmpty()) {
            setStatusAlert("Cannot launch: Rocket has no parts!")
            navigateTo(AppScreen.BUILDER)
            return
        }

        val state = PhysicsEngine.setupLaunchPad(rocket)
        state.targetBody = _targetBody.value
        state.assignedCrewIds.addAll(_assignedCrewIds.value)
        _flightState.value = state
        glRenderer.flightState = state
        glRenderer.isOrbitMapView = false
        _isOrbitMapActive.value = false
        glRenderer.camera.setModePreset(CameraMode.FLIGHT_FOLLOW)
        _isPaused.value = false

        flightJob?.cancel()
        flightJob = viewModelScope.launch {
            var lastTime = System.nanoTime()
            while (isActive) {
                val now = System.nanoTime()
                val dt = ((now - lastTime) / 1_000_000_000.0).coerceIn(0.001, 0.05)
                lastTime = now

                if (!_isPaused.value) {
                    val currentFlight = _flightState.value
                    if (currentFlight != null) {
                        PhysicsEngine.update(
                            state = currentFlight,
                            dtRaw = dt,
                            touchPitchInput = touchPitchInput,
                            touchYawInput = touchYawInput
                        )

                        // Update audio engine throttle & sound
                        val inAtmo = currentFlight.altitude < currentFlight.primaryBody.atmosphereHeight
                        audioEngine.updateThrottle(currentFlight.throttle, inAtmo)

                        // Check proximity to orbital fleet for docking
                        updateProximityDocking(currentFlight)

                        // Missions evaluation
                        missionManager.evaluateFlight(currentFlight, dt) { completedMission ->
                            refreshCredits()
                            audioEngine.playMissionSuccess()
                            setStatusAlert("MISSION SUCCESS: ${completedMission.title} (+${completedMission.rewardCredits} C)!")
                        }
                    }
                }
                delay(16) // ~60 FPS
            }
        }
    }

    private fun updateProximityDocking(flight: FlightState) {
        val vessels = fleetManager.vessels
        if (vessels.isEmpty() || flight.altitude < flight.primaryBody.atmosphereHeight) {
            _isDockingRange.value = false
            _dockingDistance.value = null
            _nearestDockTarget.value = null
            flight.isDockingAligned = false
            return
        }

        val nearest = vessels.minByOrNull { it.position.distanceTo(flight.position) }
        if (nearest != null) {
            val dist = nearest.position.distanceTo(flight.position)
            _nearestDockTarget.value = nearest
            _dockingDistance.value = dist
            flight.dockingTargetName = nearest.name
            flight.dockingDistanceMeters = dist

            val canDockRange = dist < 70.0
            val relSpeed = (flight.velocity - nearest.velocity).length()
            val hasPort = flight.rocket.hasDockingPort

            _isDockingRange.value = canDockRange && relSpeed < 4.0 && hasPort
            flight.isDockingAligned = _isDockingRange.value
        }
    }

    fun attemptDocking() {
        val flight = _flightState.value ?: return
        val target = _nearestDockTarget.value ?: return
        audioEngine.playButtonClick()

        val success = fleetManager.attemptDock(flight, target)
        if (success) {
            audioEngine.playDockingLatch()
            missionManager.completeDockingMission {
                refreshCredits()
                audioEngine.playMissionSuccess()
                setStatusAlert("MISSION SUCCESS: Docking Complete (+${it.rewardCredits} C)!")
            }
            setStatusAlert("DOCKING SUCCESSFUL with ${target.name}!")
        } else {
            setStatusAlert("Docking failed: Keep distance < 65m & relative speed < 3.0 m/s")
        }
    }

    fun convertFlightToStation(customName: String, vesselType: VesselType) {
        val flight = _flightState.value ?: return
        if (flight.status != FlightStatus.ORBIT && flight.status != FlightStatus.SPACE) {
            setStatusAlert("Cannot station: Must be in stable orbit!")
            return
        }
        audioEngine.playButtonClick()
        val orbitInfo = OrbitalMath.computeOrbit(flight.position - flight.primaryBody.position, flight.velocity, flight.primaryBody)
        fleetManager.addVessel(
            name = customName.trim().ifEmpty { "Orbital Station Alpha" },
            vesselType = vesselType,
            primaryBody = flight.primaryBody,
            orbit = orbitInfo,
            position = flight.position,
            velocity = flight.velocity,
            design = flight.rocket,
            assignedCrew = flight.assignedCrewIds.toList()
        )
        audioEngine.playMissionSuccess()
        setStatusAlert("Registered ${vesselType.displayName}: $customName to Fleet!")
        navigateTo(AppScreen.FLEET)
    }

    fun recoverFlight() {
        val flight = _flightState.value ?: return
        if (!flight.isRecoverable) {
            setStatusAlert("Vehicle must be landed on Novaria to recover!")
            return
        }
        audioEngine.playButtonClick()
        val refund = flight.calculateRecoveryRefund()
        progressionSystem.addCredits(refund)
        refreshCredits()

        // Award crew experience
        for (crewId in flight.assignedCrewIds) {
            crewSystem.recordMissionCompleted(crewId)
        }

        audioEngine.playMissionSuccess()
        setStatusAlert("Craft Recovered! +$refund Credits refunded for intact booster!")
        stopFlightSimulation()
        _flightState.value = null
        glRenderer.flightState = null
        navigateTo(AppScreen.BUILDER)
    }

    fun stopFlightSimulation() {
        flightJob?.cancel()
        flightJob = null
        audioEngine.stopEngineRoar()
    }

    fun launchCountdown() {
        val flight = _flightState.value ?: return
        if (flight.status == FlightStatus.PRE_LAUNCH) {
            audioEngine.playCountdownBeep()
            flight.status = FlightStatus.COUNTDOWN
            flight.countdownRemaining = 3.0f
            setStatusAlert("T-Minus 3 seconds to Ignition!")
        }
    }

    fun setThrottle(value: Float) {
        val flight = _flightState.value ?: return
        flight.throttle = value.coerceIn(0f, 1f)
    }

    fun triggerStage() {
        val flight = _flightState.value ?: return
        if (flight.status == FlightStatus.PRE_LAUNCH) {
            launchCountdown()
            return
        }
        val enginesIgnited = PhysicsEngine.executeStage(flight)
        audioEngine.playStaging()
        glRenderer.particles.emitStagingBurst(flight.position)
        val stageNum = flight.activeStage
        if (enginesIgnited) {
            setStatusAlert("Stage $stageNum Decoupled & Engines Ignited!")
        } else {
            setStatusAlert("Stage $stageNum Separated")
        }
    }

    fun setTimeWarp(warp: Int) {
        val flight = _flightState.value ?: return
        audioEngine.playButtonClick()
        if (warp > 2 && flight.altitude < flight.primaryBody.atmosphereHeight && flight.speed > 200.0) {
            setStatusAlert("High atmospheric drag: Max warp 2x")
            flight.timeWarp = 2
            return
        }
        flight.timeWarp = warp
        setStatusAlert("Time Warp ${warp}x")
    }

    fun setSasMode(mode: SasMode) {
        val flight = _flightState.value ?: return
        audioEngine.playButtonClick()
        flight.sasMode = mode
        setStatusAlert("Autopilot: ${mode.title}")
    }

    fun toggleLandingGear() {
        val flight = _flightState.value ?: return
        audioEngine.playButtonClick()
        flight.landingGearDeployed = !flight.landingGearDeployed
        setStatusAlert(if (flight.landingGearDeployed) "Landing Struts Deployed" else "Landing Struts Retracted")
    }

    fun toggleParachute() {
        val flight = _flightState.value ?: return
        audioEngine.playButtonClick()
        flight.parachuteDeployed = !flight.parachuteDeployed
        setStatusAlert(if (flight.parachuteDeployed) "Parachute Deployed!" else "Parachute Cut")
    }

    fun toggleOrbitMap() {
        audioEngine.playButtonClick()
        val active = !_isOrbitMapActive.value
        _isOrbitMapActive.value = active
        glRenderer.isOrbitMapView = active
        if (active) {
            glRenderer.camera.setModePreset(CameraMode.ORBIT_MAP)
        } else {
            glRenderer.camera.setModePreset(CameraMode.FLIGHT_FOLLOW)
        }
    }

    fun togglePause() {
        audioEngine.playButtonClick()
        _isPaused.value = !_isPaused.value
    }

    fun restartFlight() {
        audioEngine.playButtonClick()
        startFlightSimulation()
    }

    override fun onCleared() {
        super.onCleared()
        stopFlightSimulation()
        audioEngine.release()
    }
}
