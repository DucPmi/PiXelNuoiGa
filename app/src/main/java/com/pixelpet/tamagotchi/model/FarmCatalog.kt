package com.pixelpet.tamagotchi.model

data class CropDef(
    val id: String,
    val nameVi: String,
    val emoji: String,
    val seedCost: Int,
    val sellPrice: Int,
    val growSeconds: Long,
    val farmXp: Int
)

data class RecipeDef(
    val id: String,
    val nameVi: String,
    val emoji: String,
    val ingredients: Map<String, Int>,
    val hungerGain: Int,
    val happinessGain: Int,
    val energyGain: Int,
    val sellPrice: Int,
    val curesMistake: Boolean = false
)

data class FarmPlot(
    val index: Int,
    val cropId: String? = null,
    val plantedAtMillis: Long = 0L,
    val watered: Boolean = false
) {
    fun crop(): CropDef? = cropId?.let { FarmCatalog.findCrop(it) }

    fun effectiveGrowMillis(): Long {
        val c = crop() ?: return 0L
        val base = c.growSeconds * 1000L
        return if (watered) (base * 70L) / 100L else base
    }

    fun remainingMillis(now: Long): Long {
        if (cropId == null) return 0L
        val elapsed = (now - plantedAtMillis).coerceAtLeast(0L)
        return (effectiveGrowMillis() - elapsed).coerceAtLeast(0L)
    }

    fun isReady(now: Long): Boolean = cropId != null && remainingMillis(now) <= 0L

    fun progressFraction(now: Long): Float {
        val total = effectiveGrowMillis().coerceAtLeast(1L)
        val elapsed = (now - plantedAtMillis).coerceAtLeast(0L)
        return (elapsed.toFloat() / total.toFloat()).coerceIn(0f, 1f)
    }
}

object FarmCatalog {
    val crops: List<CropDef> = listOf(
        CropDef("wheat", "Lúa Mì", "🌾", seedCost = 10, sellPrice = 20, growSeconds = 120L, farmXp = 10),
        CropDef("carrot", "Cà Rốt", "🥕", seedCost = 15, sellPrice = 32, growSeconds = 180L, farmXp = 15),
        CropDef("corn", "Ngô Ngọt", "🌽", seedCost = 20, sellPrice = 42, growSeconds = 240L, farmXp = 20),
        CropDef("strawberry", "Dâu Tây", "🍓", seedCost = 25, sellPrice = 55, growSeconds = 300L, farmXp = 25),
        CropDef("blueberry", "Việt Quất", "🫐", seedCost = 30, sellPrice = 68, growSeconds = 360L, farmXp = 30),
        CropDef("pumpkin", "Bí Ngô", "🎃", seedCost = 35, sellPrice = 82, growSeconds = 420L, farmXp = 35)
    )

    val recipes: List<RecipeDef> = listOf(
        RecipeDef(
            id = "bread",
            nameVi = "Bánh Mì Nóng",
            emoji = "🍞",
            ingredients = mapOf("wheat" to 2),
            hungerGain = 30,
            happinessGain = 10,
            energyGain = 15,
            sellPrice = 55
        ),
        RecipeDef(
            id = "popcorn",
            nameVi = "Bắp Rang Bơ",
            emoji = "🍿",
            ingredients = mapOf("corn" to 1),
            hungerGain = 22,
            happinessGain = 28,
            energyGain = 10,
            sellPrice = 60
        ),
        RecipeDef(
            id = "carrot_soup",
            nameVi = "Súp Cà Rốt",
            emoji = "🥣",
            ingredients = mapOf("carrot" to 2),
            hungerGain = 36,
            happinessGain = 14,
            energyGain = 28,
            sellPrice = 85
        ),
        RecipeDef(
            id = "strawberry_cake",
            nameVi = "Bánh Kem Dâu",
            emoji = "🍰",
            ingredients = mapOf("wheat" to 1, "strawberry" to 2),
            hungerGain = 42,
            happinessGain = 42,
            energyGain = 20,
            sellPrice = 165,
            curesMistake = true
        ),
        RecipeDef(
            id = "pumpkin_pie",
            nameVi = "Bánh Bí Ngô Vàng",
            emoji = "🥧",
            ingredients = mapOf("wheat" to 1, "pumpkin" to 1),
            hungerGain = 55,
            happinessGain = 25,
            energyGain = 35,
            sellPrice = 145
        ),
        RecipeDef(
            id = "royal_tart",
            nameVi = "Tart Hoàng Gia",
            emoji = "🧁",
            ingredients = mapOf("wheat" to 1, "strawberry" to 1, "blueberry" to 1),
            hungerGain = 80,
            happinessGain = 60,
            energyGain = 55,
            sellPrice = 220,
            curesMistake = true
        )
    )

    fun findCrop(id: String): CropDef? = crops.firstOrNull { it.id == id }
    fun findRecipe(id: String): RecipeDef? = recipes.firstOrNull { it.id == id }
}
