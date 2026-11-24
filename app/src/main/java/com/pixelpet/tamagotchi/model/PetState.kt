package com.pixelpet.tamagotchi.model

data class PetState(
    val hasCompletedOnboarding: Boolean = false,
    val speciesId: String = "chick",
    val nickname: String = "Gà Bông",
    val stage: Int = 1,
    val stageEnteredMillis: Long = System.currentTimeMillis(),
    val nextPoopAtMillis: Long = System.currentTimeMillis() + 30L * 60_000L,
    val hunger: Int = 80,
    val happiness: Int = 80,
    val energy: Int = 90,
    val cleanliness: Int = 100,
    val weightGrams: Int = 5,
    val careMistakes: Int = 0,
    val poopCount: Int = 0,
    val isSleeping: Boolean = false,
    val isSick: Boolean = false,
    val soundEnabled: Boolean = true,
    // Nông trại & Lò bánh (Stardew Valley loop)
    val gold: Int = 60,
    val farmingXp: Int = 0,
    val plots: List<FarmPlot> = List(6) { FarmPlot(index = it) },
    val cropBag: Map<String, Int> = mapOf("wheat" to 2),
    val dishBag: Map<String, Int> = mapOf("bread" to 1),
    val lastAction: String = "Trứng đang rung nhẹ... 5 phút nữa sẽ nở!",
    val lastEffect: String = "IDLE",
    val lastUpdateMillis: Long = System.currentTimeMillis(),
    val birthMillis: Long = System.currentTimeMillis()
) {
    val species: PetSpecies
        get() = PetCatalog.findById(speciesId)

    val stageName: String
        get() = species.stageNames.getOrElse(stage - 1) { species.nameVi }

    val farmingLevel: Int
        get() = (farmingXp / 80) + 1

    val totalDishes: Int
        get() = dishBag.values.sum()

    fun readyCropsCount(now: Long = System.currentTimeMillis()): Int =
        plots.count { it.isReady(now) }

    val hungerHearts: Int
        get() = ((hunger + 24) / 25).coerceIn(0, 4)

    val happinessHearts: Int
        get() = ((happiness + 24) / 25).coerceIn(0, 4)

    val requiredStageDurationMillis: Long
        get() = when (stage) {
            1 -> 5L * 60_000L
            2 -> 65L * 60_000L
            3 -> 48L * 60L * 60_000L
            4 -> 96L * 60L * 60_000L
            else -> 0L
        }

    fun remainingEvolutionMillis(now: Long = System.currentTimeMillis()): Long {
        if (stage >= 5) return 0L
        val elapsed = (now - stageEnteredMillis).coerceAtLeast(0L)
        return (requiredStageDurationMillis - elapsed).coerceAtLeast(0L)
    }

    fun formattedEvolutionCountdown(now: Long = System.currentTimeMillis()): String {
        if (stage >= 5) return "MAX Cấp 5"
        val rem = remainingEvolutionMillis(now)
        if (rem <= 0L) {
            return if (stage == 4 && careMistakes > 2) "Cần giảm lỗi" else "Sắp tiến hóa!"
        }
        val totalMinutes = (rem / 60_000L).coerceAtLeast(1L)
        val days = totalMinutes / (60L * 24L)
        val hours = (totalMinutes % (60L * 24L)) / 60L
        val mins = totalMinutes % 60L
        return when {
            days > 0 -> "${days}n ${hours}g"
            hours > 0 -> "${hours}g ${mins}p"
            else -> "${mins}p"
        }
    }
}
