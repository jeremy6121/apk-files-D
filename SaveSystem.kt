package com.example.cosmic.model

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject

enum class GraphicsQuality(val label: String, val maxParticles: Int, val trajectorySamples: Int) {
    LOW("Low", 150, 48),
    MEDIUM("Medium", 350, 72),
    HIGH("High", 650, 108)
}

class SaveSystem(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("cosmic_saved_rockets", Context.MODE_PRIVATE)

    // --- ROCKET STORAGE ---
    fun saveRocket(design: RocketDesign) {
        val json = serializeRocket(design)
        prefs.edit().putString("rocket_${design.name}", json).apply()

        // Also add to saved list index
        val list = getSavedRocketNames().toMutableSet()
        list.add(design.name)
        prefs.edit().putStringSet("saved_rocket_names", list).apply()
    }

    fun loadRocket(name: String): RocketDesign? {
        val json = prefs.getString("rocket_$name", null) ?: return null
        return deserializeRocket(json)
    }

    fun getSavedRocketNames(): List<String> {
        val names = prefs.getStringSet("saved_rocket_names", emptySet()) ?: emptySet()
        return names.toList().sorted()
    }

    fun deleteRocket(name: String) {
        prefs.edit().remove("rocket_$name").apply()
        val list = getSavedRocketNames().toMutableSet()
        list.remove(name)
        prefs.edit().putStringSet("saved_rocket_names", list).apply()
    }

    // --- SETTINGS STORAGE ---
    var graphicsQuality: GraphicsQuality
        get() {
            val name = prefs.getString("graphics_quality", GraphicsQuality.MEDIUM.name) ?: GraphicsQuality.MEDIUM.name
            return try {
                GraphicsQuality.valueOf(name)
            } catch (e: Exception) {
                GraphicsQuality.MEDIUM
            }
        }
        set(value) = prefs.edit().putString("graphics_quality", value.name).apply()

    var soundEnabled: Boolean
        get() = prefs.getBoolean("sound_enabled", true)
        set(value) = prefs.edit().putBoolean("sound_enabled", value).apply()

    var particlesEnabled: Boolean
        get() = prefs.getBoolean("particles_enabled", true)
        set(value) = prefs.edit().putBoolean("particles_enabled", value).apply()

    // --- MISSIONS STORAGE ---
    fun getCompletedMissionIds(): Set<String> {
        return prefs.getStringSet("completed_missions", emptySet()) ?: emptySet()
    }

    fun markMissionCompleted(missionId: String) {
        val set = getCompletedMissionIds().toMutableSet()
        set.add(missionId)
        prefs.edit().putStringSet("completed_missions", set).apply()
    }

    // --- SERIALIZATION ---
    private fun serializeRocket(design: RocketDesign): String {
        val root = JSONObject()
        root.put("name", design.name)
        val partsArray = JSONArray()

        for (part in design.parts) {
            val pObj = JSONObject()
            pObj.put("id", part.id)
            pObj.put("typeName", part.type.name)
            pObj.put("x", part.x.toDouble())
            pObj.put("y", part.y.toDouble())
            pObj.put("z", part.z.toDouble())
            pObj.put("rotationYDeg", part.rotationYDeg.toDouble())
            pObj.put("stageIndex", part.stageIndex)
            pObj.put("fuelRemaining", part.fuelRemaining)
            pObj.put("isDeployed", part.isDeployed)
            partsArray.put(pObj)
        }
        root.put("parts", partsArray)
        return root.toString()
    }

    private fun deserializeRocket(jsonStr: String): RocketDesign? {
        return try {
            val root = JSONObject(jsonStr)
            val name = root.optString("name", "Custom Rocket")
            val partsArray = root.getJSONArray("parts")
            val parts = mutableListOf<PlacedPart>()

            for (i in 0 until partsArray.length()) {
                val pObj = partsArray.getJSONObject(i)
                val typeName = pObj.getString("typeName")
                val type = try {
                    PartType.valueOf(typeName)
                } catch (e: Exception) {
                    continue
                }

                val part = PlacedPart(
                    id = pObj.optString("id"),
                    type = type,
                    x = pObj.optDouble("x", 0.0).toFloat(),
                    y = pObj.optDouble("y", 0.0).toFloat(),
                    z = pObj.optDouble("z", 0.0).toFloat(),
                    rotationYDeg = pObj.optDouble("rotationYDeg", 0.0).toFloat(),
                    stageIndex = pObj.optInt("stageIndex", 0),
                    fuelRemaining = pObj.optDouble("fuelRemaining", type.fuelCapacity),
                    isDeployed = pObj.optBoolean("isDeployed", false)
                )
                parts.add(part)
            }
            RocketDesign(name = name, parts = parts)
        } catch (e: Exception) {
            null
        }
    }
}
