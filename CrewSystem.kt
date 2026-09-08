package com.example.cosmic.model

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

enum class CrewRole(val title: String, val perkDescription: String) {
    PILOT("Chief Pilot", "Improves SAS stability and steering precision by +25%"),
    ENGINEER("Flight Engineer", "Optimizes engine propellant burn and reinforces landing gear"),
    SCIENTIST("Mission Specialist", "Yields +35% bonus scientific credits on orbital deployments")
}

data class Astronaut(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val role: CrewRole,
    var missionsFlown: Int = 0,
    var landingsCompleted: Int = 0,
    var experienceXp: Int = 0,
    var isAssigned: Boolean = false
) {
    val level: Int
        get() = when {
            experienceXp >= 1000 -> 5
            experienceXp >= 500 -> 4
            experienceXp >= 250 -> 3
            experienceXp >= 100 -> 2
            else -> 1
        }

    fun awardMissionExperience(wasLanding: Boolean, isInterplanetary: Boolean) {
        missionsFlown += 1
        if (wasLanding) landingsCompleted += 1
        val xpGain = 50 + (if (wasLanding) 100 else 0) + (if (isInterplanetary) 150 else 0)
        experienceXp += xpGain
    }
}

class CrewManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("cosmic_crew_roster", Context.MODE_PRIVATE)

    val roster: MutableList<Astronaut> = mutableListOf()

    init {
        loadRoster()
    }

    private fun loadRoster() {
        val json = prefs.getString("crew_roster", null)
        if (json != null) {
            try {
                val array = JSONArray(json)
                roster.clear()
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    val role = try {
                        CrewRole.valueOf(obj.getString("role"))
                    } catch (e: Exception) {
                        CrewRole.PILOT
                    }
                    roster.add(
                        Astronaut(
                            id = obj.getString("id"),
                            name = obj.getString("name"),
                            role = role,
                            missionsFlown = obj.optInt("missionsFlown", 0),
                            landingsCompleted = obj.optInt("landingsCompleted", 0),
                            experienceXp = obj.optInt("experienceXp", 0),
                            isAssigned = obj.optBoolean("isAssigned", false)
                        )
                    )
                }
            } catch (e: Exception) {
                initDefaultRoster()
            }
        } else {
            initDefaultRoster()
        }
    }

    private fun initDefaultRoster() {
        roster.clear()
        roster.add(Astronaut(name = "Commander Nova Sterling", role = CrewRole.PILOT, missionsFlown = 3, experienceXp = 180))
        roster.add(Astronaut(name = "Dr. Orion Vance", role = CrewRole.SCIENTIST, missionsFlown = 2, experienceXp = 120))
        roster.add(Astronaut(name = "Chief Engineer Vega Sparks", role = CrewRole.ENGINEER, missionsFlown = 2, experienceXp = 110))
        roster.add(Astronaut(name = "Pilot Lyra Hayes", role = CrewRole.PILOT, missionsFlown = 1, experienceXp = 60))
        roster.add(Astronaut(name = "Cadet Jaxen Cole", role = CrewRole.ENGINEER, missionsFlown = 0, experienceXp = 0))
        saveRoster()
    }

    fun saveRoster() {
        val array = JSONArray()
        for (astro in roster) {
            val obj = JSONObject()
            obj.put("id", astro.id)
            obj.put("name", astro.name)
            obj.put("role", astro.role.name)
            obj.put("missionsFlown", astro.missionsFlown)
            obj.put("landingsCompleted", astro.landingsCompleted)
            obj.put("experienceXp", astro.experienceXp)
            obj.put("isAssigned", astro.isAssigned)
            array.put(obj)
        }
        prefs.edit().putString("crew_roster", array.toString()).apply()
    }

    fun hireAstronaut(name: String, role: CrewRole): Astronaut {
        val astro = Astronaut(name = name, role = role)
        roster.add(astro)
        saveRoster()
        return astro
    }

    fun dismissAstronaut(id: String) {
        roster.removeAll { it.id == id }
        saveRoster()
    }

    fun assignCrewMember(id: String, assigned: Boolean) {
        val astro = roster.find { it.id == id } ?: return
        astro.isAssigned = assigned
        saveRoster()
    }
}
