package com.pixelpet.tamagotchi.model

data class PetState(
    val speciesId: String = "chick",
    val nickname: String = "Gà Bông",
    val stage: Int = 1,          // 1..5
    val xp: Int = 0,
    val hunger: Int = 85,        // 0..100 (100 = no bụng)
    val happiness: Int = 85,     // 0..100
    val energy: Int = 85,        // 0..100
    val cleanliness: Int = 100,  // 0..100
    val poopCount: Int = 0,      // 0..3
    val isSleeping: Boolean = false,
    val isSick: Boolean = false,
    val lastAction: String = "Chào bạn! Chạm nút bên dưới để chăm sóc mình nhé!",
    val lastEffect: String = "IDLE", // IDLE, FEED, CLEAN, PLAY, SLEEP, HEAL, EVOLVE
    val lastUpdateMillis: Long = System.currentTimeMillis(),
    val birthMillis: Long = System.currentTimeMillis()
) {
    val species: PetSpecies
        get() = PetCatalog.findById(speciesId)

    val stageName: String
        get() = species.stageNames.getOrElse(stage - 1) { species.nameVi }

    val xpForNextStage: Int
        get() = when (stage) {
            1 -> 40
            2 -> 80
            3 -> 130
            4 -> 200
            else -> 200
        }

    val ageDays: Int
        get() = (((System.currentTimeMillis() - birthMillis).coerceAtLeast(0L)) / 86_400_000L).toInt() + 1
}
