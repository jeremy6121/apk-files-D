package com.example.cosmic.model

import android.content.Context
import android.content.SharedPreferences

data class PartUpgrade(
    val partName: String,
    val level: Int = 1,
    val thrustBonusPercent: Float = 0f,
    val massReductionPercent: Float = 0f,
    val ispBonusPercent: Float = 0f
)

class ProgressionSystem(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("cosmic_progression", Context.MODE_PRIVATE)

    var credits: Int
        get() = prefs.getInt("player_credits", 2500) // 2500 starter credits
        set(value) = prefs.edit().putInt("player_credits", value.coerceAtLeast(0)).apply()

    fun addCredits(amount: Int) {
        credits += amount
    }

    fun canAfford(cost: Int): Boolean = credits >= cost

    fun isPartUnlocked(partType: PartType): Boolean {
        if (partType.unlockedByDefault) return true
        val unlockedSet = getUnlockedPartNames()
        return unlockedSet.contains(partType.name)
    }

    fun unlockPart(partType: PartType): Boolean {
        if (isPartUnlocked(partType)) return true
        if (credits < partType.cost) return false

        credits -= partType.cost
        val set = getUnlockedPartNames().toMutableSet()
        set.add(partType.name)
        prefs.edit().putStringSet("unlocked_parts", set).apply()
        return true
    }

    fun getUnlockedPartNames(): Set<String> {
        val saved = prefs.getStringSet("unlocked_parts", emptySet()) ?: emptySet()
        val set = saved.toMutableSet()
        // Include default parts
        PartType.entries.filter { it.unlockedByDefault }.forEach { set.add(it.name) }
        return set
    }

    fun getPartUpgradeLevel(partName: String): Int {
        return prefs.getInt("upgrade_$partName", 1)
    }

    fun upgradePart(partType: PartType): Boolean {
        val currentLevel = getPartUpgradeLevel(partType.name)
        if (currentLevel >= 3) return false
        val cost = (partType.cost * 0.75 * currentLevel).toInt()
        if (credits < cost) return false

        credits -= cost
        prefs.edit().putInt("upgrade_${partType.name}", currentLevel + 1).apply()
        return true
    }
}
