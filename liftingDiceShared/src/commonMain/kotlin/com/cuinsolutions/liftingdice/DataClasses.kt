package com.cuinsolutions.liftingdice

import kotlinx.serialization.Serializable

@Serializable
data class MuscleGroup(val name: String, val id: Int)

@Serializable
data class EquipmentSetting(val name: String, val id: Int)

@Serializable
data class Exercise(val name: String, val id: Int, val muscleGroupIds: List<Int>, val equipmentIds: List<Int>)