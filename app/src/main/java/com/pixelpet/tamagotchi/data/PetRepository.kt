package com.pixelpet.tamagotchi.data

import android.content.Context
import com.pixelpet.tamagotchi.model.FarmCatalog
import com.pixelpet.tamagotchi.model.FarmPlot
import com.pixelpet.tamagotchi.model.PetCatalog
import com.pixelpet.tamagotchi.model.PetState
import org.json.JSONArray
import org.json.JSONObject
import kotlin.random.Random

class PetRepository(context: Context) {

    private val prefs = context.getSharedPreferences("pixel_pet_farm_v3_prefs", Context.MODE_PRIVATE)

    fun loadState(): PetState {
        val now = System.currentTimeMillis()
        val raw = PetState(
            hasCompletedOnboarding = prefs.getBoolean("hasCompletedOnboarding", false),
            speciesId = prefs.getString("speciesId", "chick") ?: "chick",
            nickname = prefs.getString("nickname", "Gà Bông") ?: "Gà Bông",
            stage = prefs.getInt("stage", 1).coerceIn(1, 5),
            stageEnteredMillis = prefs.getLong("stageEnteredMillis", now),
            nextPoopAtMillis = prefs.getLong("nextPoopAtMillis", now + 30L * 60_000L),
            hunger = prefs.getInt("hunger", 80).coerceIn(0, 100),
            happiness = prefs.getInt("happiness", 80).coerceIn(0, 100),
            energy = prefs.getInt("energy", 90).coerceIn(0, 100),
            cleanliness = prefs.getInt("cleanliness", 100).coerceIn(0, 100),
            weightGrams = prefs.getInt("weightGrams", 5).coerceIn(5, 99),
            careMistakes = prefs.getInt("careMistakes", 0).coerceAtLeast(0),
            poopCount = prefs.getInt("poopCount", 0).coerceIn(0, 3),
            isSleeping = prefs.getBoolean("isSleeping", false),
            isSick = prefs.getBoolean("isSick", false),
            soundEnabled = prefs.getBoolean("soundEnabled", true),
            gold = prefs.getInt("gold", 60).coerceAtLeast(0),
            farmingXp = prefs.getInt("farmingXp", 0).coerceAtLeast(0),
            plots = parsePlots(prefs.getString("plotsJson", null)),
            cropBag = parseIntMap(prefs.getString("cropBagJson", null), mapOf("wheat" to 2)),
            dishBag = parseIntMap(prefs.getString("dishBagJson", null), mapOf("bread" to 1)),
            lastAction = prefs.getString("lastAction", "Trứng đang rung nhẹ... 5 phút nữa sẽ nở!") ?: "",
            lastEffect = prefs.getString("lastEffect", "IDLE") ?: "IDLE",
            lastUpdateMillis = prefs.getLong("lastUpdateMillis", now),
            birthMillis = prefs.getLong("birthMillis", now)
        )
        val updated = applyRealTimeSimulation(raw, now)
        if (updated != raw) {
            saveState(updated)
        }
        return updated
    }

    fun saveState(state: PetState) {
        prefs.edit()
            .putBoolean("hasCompletedOnboarding", state.hasCompletedOnboarding)
            .putString("speciesId", state.speciesId)
            .putString("nickname", state.nickname)
            .putInt("stage", state.stage)
            .putLong("stageEnteredMillis", state.stageEnteredMillis)
            .putLong("nextPoopAtMillis", state.nextPoopAtMillis)
            .putInt("hunger", state.hunger)
            .putInt("happiness", state.happiness)
            .putInt("energy", state.energy)
            .putInt("cleanliness", state.cleanliness)
            .putInt("weightGrams", state.weightGrams)
            .putInt("careMistakes", state.careMistakes)
            .putInt("poopCount", state.poopCount)
            .putBoolean("isSleeping", state.isSleeping)
            .putBoolean("isSick", state.isSick)
            .putBoolean("soundEnabled", state.soundEnabled)
            .putInt("gold", state.gold)
            .putInt("farmingXp", state.farmingXp)
            .putString("plotsJson", serializePlots(state.plots))
            .putString("cropBagJson", serializeIntMap(state.cropBag))
            .putString("dishBagJson", serializeIntMap(state.dishBag))
            .putString("lastAction", state.lastAction)
            .putString("lastEffect", state.lastEffect)
            .putLong("lastUpdateMillis", state.lastUpdateMillis)
            .putLong("birthMillis", state.birthMillis)
            .apply()
    }

    private fun applyRealTimeSimulation(state: PetState, now: Long): PetState {
        if (!state.hasCompletedOnboarding) return state
        var cur = state

        while (cur.stage < 5) {
            val needMs = cur.requiredStageDurationMillis
            val elapsedInStage = (now - cur.stageEnteredMillis).coerceAtLeast(0L)
            if (elapsedInStage >= needMs) {
                if (cur.stage == 4 && cur.careMistakes > 2) break
                val newStage = cur.stage + 1
                val minW = minWeightForStage(newStage)
                cur = cur.copy(
                    stage = newStage,
                    stageEnteredMillis = cur.stageEnteredMillis + needMs,
                    weightGrams = cur.weightGrams.coerceAtLeast(minW),
                    nextPoopAtMillis = now + nextPoopIntervalMillis(newStage),
                    lastAction = if (newStage == 2) {
                        "TÁCH! Trứng đã nở thành ${cur.species.stageNames[1]}! Bắt đầu làm nông thôi!"
                    } else {
                        "TIẾN HÓA Cấp $newStage/5: ${cur.species.stageNames[newStage - 1]}!"
                    },
                    lastEffect = "EVOLVE"
                )
            } else {
                break
            }
        }

        if (cur.stage == 1) {
            return cur.copy(lastUpdateMillis = now)
        }

        val elapsedMinutes = ((now - cur.lastUpdateMillis).coerceAtLeast(0L) / 60_000L).toInt()
        if (elapsedMinutes < 5) return cur

        val steps = (elapsedMinutes / 20).coerceAtLeast(1)
        val hungerDrop = if (cur.isSleeping) steps * 2 else steps * 6
        val happyDrop = if (cur.isSleeping) steps else steps * 5
        val energyDelta = if (cur.isSleeping) steps * 12 else -(steps * 2)

        val newHunger = (cur.hunger - hungerDrop).coerceIn(0, 100)
        val newEnergy = (cur.energy + energyDelta).coerceIn(0, 100)

        var newPoop = cur.poopCount
        var nextPoopAt = cur.nextPoopAtMillis
        if (!cur.isSleeping && now >= nextPoopAt && newPoop < 3) {
            val interval = nextPoopIntervalMillis(cur.stage)
            val cycles = (((now - nextPoopAt) / interval).toInt() + 1).coerceAtMost(3 - newPoop)
            newPoop = (newPoop + cycles).coerceIn(0, 3)
            nextPoopAt = now + interval
        }

        val newClean = (cur.cleanliness - newPoop * 8 * steps.coerceAtMost(2)).coerceIn(0, 100)
        val newHappy = (cur.happiness - happyDrop - newPoop * 3).coerceIn(0, 100)

        var mistakes = cur.careMistakes
        if ((newHunger == 0 || newHappy == 0) && elapsedMinutes >= 15) {
            mistakes += 1
        }

        val nowSick = cur.isSick || (newPoop >= 2 && newClean < 40) || (newHunger == 0 && elapsedMinutes >= 30)
        val autoWake = cur.isSleeping && newEnergy >= 100

        val msg = when {
            cur.lastEffect == "EVOLVE" -> cur.lastAction
            nowSick -> "Bé bị ốm rồi! Nhấn Thuốc hoặc Dọn phân ngay nhé."
            autoWake -> "Ngủ đủ giấc rồi! Bé sẵn sàng ra đồng làm nông!"
            newPoop > cur.poopCount -> "Bé vừa đi vệ sinh! Nhấn Dọn để phòng sạch sẽ nhé."
            newHunger <= 25 -> "Đói bụng quá... nướng bánh hoặc cho bé ăn đi nào!"
            else -> cur.lastAction
        }

        return cur.copy(
            hunger = newHunger,
            happiness = newHappy,
            energy = newEnergy,
            cleanliness = newClean,
            poopCount = newPoop,
            nextPoopAtMillis = nextPoopAt,
            careMistakes = mistakes,
            isSleeping = if (autoWake) false else cur.isSleeping,
            isSick = nowSick,
            lastAction = msg,
            lastEffect = if (autoWake) "IDLE" else cur.lastEffect,
            lastUpdateMillis = now
        )
    }

    // --- CHĂM SÓC THÚ NUÔI ---

    fun feedBestAvailableDish(specificDishId: String? = null): PetState {
        val s = loadState()
        if (s.stage == 1) {
            return saveAndReturn(s.copy(lastAction = "Trứng chưa nở! Chờ thêm ${s.formattedEvolutionCountdown()} nhé.", lastEffect = "IDLE"))
        }
        if (s.isSleeping) {
            return saveAndReturn(s.copy(lastAction = "Bé đang ngủ khò khò! Đánh thức trước đã nhé.", lastEffect = "SLEEP"))
        }
        if (s.hunger >= 95) {
            return saveAndReturn(s.copy(lastAction = "Bé lắc đầu: No căng rồi, không ăn nữa đâu!", lastEffect = "IDLE"))
        }

        val chosenId = specificDishId?.takeIf { (s.dishBag[it] ?: 0) > 0 }
            ?: FarmCatalog.recipes.reversed().firstOrNull { (s.dishBag[it.id] ?: 0) > 0 }?.id

        if (chosenId != null) {
            val recipe = FarmCatalog.findRecipe(chosenId)!!
            val updatedBag = s.dishBag.toMutableMap()
            val remain = (updatedBag[chosenId] ?: 1) - 1
            if (remain <= 0) updatedBag.remove(chosenId) else updatedBag[chosenId] = remain
            val newMistakes = if (recipe.curesMistake && s.careMistakes > 0) s.careMistakes - 1 else s.careMistakes
            return saveAndReturn(
                s.copy(
                    hunger = (s.hunger + recipe.hungerGain).coerceAtMost(100),
                    happiness = (s.happiness + recipe.happinessGain).coerceAtMost(100),
                    energy = (s.energy + recipe.energyGain).coerceAtMost(100),
                    weightGrams = (s.weightGrams + 1).coerceAtMost(99),
                    careMistakes = newMistakes,
                    dishBag = updatedBag,
                    lastAction = "Măm ${recipe.emoji} ${recipe.nameVi}! +${recipe.hungerGain}% Đói, +${recipe.energyGain}% NL!",
                    lastEffect = "FEED",
                    lastUpdateMillis = System.currentTimeMillis()
                )
            )
        }

        // Hết bánh trong kho -> ăn cơm trắng cứu đói
        return saveAndReturn(
            s.copy(
                hunger = (s.hunger + 15).coerceAtMost(100),
                weightGrams = (s.weightGrams + 1).coerceAtMost(99),
                lastAction = "Ăn tạm 🍚 Cơm trắng (+15% Đói). Ra đồng trồng cây làm bánh ngon hơn nhé!",
                lastEffect = "FEED",
                lastUpdateMillis = System.currentTimeMillis()
            )
        )
    }

    fun clean(): PetState {
        val s = loadState()
        val hadDirt = s.poopCount > 0 || s.cleanliness < 100
        return saveAndReturn(
            s.copy(
                poopCount = 0,
                cleanliness = 100,
                happiness = (s.happiness + if (hadDirt) 12 else 3).coerceAtMost(100),
                lastAction = if (hadDirt) "Đã dọn sạch phân! Chuồng trại thơm tho rồi!" else "Nông trại đang rất sạch sẽ!",
                lastEffect = "CLEAN",
                lastUpdateMillis = System.currentTimeMillis()
            )
        )
    }

    fun play(bonusHappy: Int = 24, customMessage: String? = null): PetState {
        val s = loadState()
        if (s.stage == 1) {
            return saveAndReturn(s.copy(lastAction = "Trứng đang ấp ấm, chờ nở rồi chơi cùng bé nhé!", lastEffect = "IDLE"))
        }
        if (s.isSleeping) {
            return saveAndReturn(s.copy(lastAction = "Bé đang ngủ, đánh thức bé dậy chơi nhé!", lastEffect = "SLEEP"))
        }
        if (s.energy < 10) {
            return saveAndReturn(s.copy(lastAction = "Bé mệt rồi! Cho bé đi ngủ hoặc ăn bánh nạp năng lượng nhé.", lastEffect = "IDLE"))
        }
        val minW = minWeightForStage(s.stage)
        return saveAndReturn(
            s.copy(
                happiness = (s.happiness + bonusHappy).coerceAtMost(100),
                energy = (s.energy - 10).coerceAtLeast(0),
                hunger = (s.hunger - 5).coerceAtLeast(0),
                weightGrams = (s.weightGrams - 1).coerceAtLeast(minW),
                lastAction = customMessage ?: "Chơi vui quá đi! (+1 tim vui, -1g)",
                lastEffect = "PLAY",
                lastUpdateMillis = System.currentTimeMillis()
            )
        )
    }

    fun toggleSleep(): PetState {
        val s = loadState()
        if (s.stage == 1) {
            return saveAndReturn(s.copy(lastAction = "Trứng đang ấp ấm trong ổ, sắp nở rồi!", lastEffect = "IDLE"))
        }
        val nowSleeping = !s.isSleeping
        return saveAndReturn(
            s.copy(
                isSleeping = nowSleeping,
                energy = if (!nowSleeping) (s.energy + 15).coerceAtMost(100) else s.energy,
                lastAction = if (nowSleeping) "Tắt đèn đi ngủ... Khò khò Zzz" else "Chào buổi sáng! Bé sẵn sàng làm nông!",
                lastEffect = if (nowSleeping) "SLEEP" else "IDLE",
                lastUpdateMillis = System.currentTimeMillis()
            )
        )
    }

    fun heal(): PetState {
        val s = loadState()
        val cured = s.isSick
        val reducedMistake = if (!cured && s.careMistakes > 0) s.careMistakes - 1 else s.careMistakes
        return saveAndReturn(
            s.copy(
                isSick = false,
                careMistakes = reducedMistake,
                cleanliness = (s.cleanliness + 20).coerceAtMost(100),
                happiness = (s.happiness + 8).coerceAtMost(100),
                energy = (s.energy + 8).coerceAtMost(100),
                lastAction = if (cured) "Uống thuốc xong bé khỏe lại ngay!" else "Tắm mát & uống vitamin khỏe khoắn!",
                lastEffect = "HEAL",
                lastUpdateMillis = System.currentTimeMillis()
            )
        )
    }

    // --- NÔNG TRẠI (STARDEW VALLEY LOOP) ---

    fun plantCrop(plotIndex: Int, cropId: String): PetState {
        val s = loadState()
        if (s.stage == 1) {
            return saveAndReturn(s.copy(lastAction = "Chờ trứng nở (5 phút) thì bé mới ra đồng gieo hạt được!"))
        }
        if (s.isSleeping) {
            return saveAndReturn(s.copy(lastAction = "Bé đang ngủ, đánh thức bé dậy làm nông nhé!"))
        }
        if (s.energy < 5) {
            return saveAndReturn(s.copy(lastAction = "Hết năng lượng làm nông! Cho bé ăn bánh hoặc đi ngủ nhé."))
        }
        val crop = FarmCatalog.findCrop(cropId) ?: return s
        val plot = s.plots.getOrNull(plotIndex) ?: return s
        if (plot.cropId != null) return s
        if (s.gold < crop.seedCost) {
            return saveAndReturn(s.copy(lastAction = "Thiếu vàng! Cần ${crop.seedCost}G để mua hạt ${crop.nameVi}."))
        }
        val now = System.currentTimeMillis()
        val newPlots = s.plots.map {
            if (it.index == plotIndex) it.copy(cropId = crop.id, plantedAtMillis = now, watered = false) else it
        }
        return saveAndReturn(
            s.copy(
                gold = s.gold - crop.seedCost,
                energy = (s.energy - 5).coerceAtLeast(0),
                plots = newPlots,
                lastAction = "${s.nickname} đã gieo ${crop.emoji} ${crop.nameVi} (-${crop.seedCost}G, -5% NL)!",
                lastEffect = "PLAY",
                lastUpdateMillis = now
            )
        )
    }

    fun waterPlot(plotIndex: Int): PetState {
        val s = loadState()
        if (s.stage == 1 || s.isSleeping) return s
        val plot = s.plots.getOrNull(plotIndex) ?: return s
        if (plot.cropId == null || plot.watered) return s
        if (s.energy < 4) {
            return saveAndReturn(s.copy(lastAction = "Bé hơi mệt, cần ít nhất 4% năng lượng để tưới nước!"))
        }
        val newPlots = s.plots.map {
            if (it.index == plotIndex) it.copy(watered = true) else it
        }
        return saveAndReturn(
            s.copy(
                energy = (s.energy - 4).coerceAtLeast(0),
                plots = newPlots,
                lastAction = "💧 Đã tưới nước! Cây lớn nhanh hơn 30%!",
                lastEffect = "CLEAN",
                lastUpdateMillis = System.currentTimeMillis()
            )
        )
    }

    fun harvestPlot(plotIndex: Int): PetState {
        val s = loadState()
        val now = System.currentTimeMillis()
        val plot = s.plots.getOrNull(plotIndex) ?: return s
        val crop = plot.crop() ?: return s
        if (!plot.isReady(now)) return s

        // Kỹ năng Nông Dân càng cao càng dễ thu hoạch Sao Vàng x2 (chuẩn Stardew Valley)
        val goldStarChance = (15 + s.farmingLevel * 8).coerceAtMost(65)
        val isGoldStar = Random.nextInt(100) < goldStarChance
        val qty = if (isGoldStar) 2 else 1

        val newBag = s.cropBag.toMutableMap()
        newBag[crop.id] = (newBag[crop.id] ?: 0) + qty

        val newPlots = s.plots.map {
            if (it.index == plotIndex) FarmPlot(index = it.index) else it
        }
        val starText = if (isGoldStar) " ⭐SAO VÀNG x2!" else " x1"
        return saveAndReturn(
            s.copy(
                plots = newPlots,
                cropBag = newBag,
                farmingXp = s.farmingXp + crop.farmXp,
                happiness = (s.happiness + 6).coerceAtMost(100),
                lastAction = "Thu hoạch ${crop.emoji} ${crop.nameVi}$starText (+${crop.farmXp} Farm XP)",
                lastEffect = "EVOLVE",
                lastUpdateMillis = now
            )
        )
    }

    fun sellCrop(cropId: String): PetState {
        val s = loadState()
        val count = s.cropBag[cropId] ?: 0
        if (count <= 0) return s
        val crop = FarmCatalog.findCrop(cropId) ?: return s
        val newBag = s.cropBag.toMutableMap()
        if (count == 1) newBag.remove(cropId) else newBag[cropId] = count - 1
        return saveAndReturn(
            s.copy(
                gold = s.gold + crop.sellPrice,
                cropBag = newBag,
                lastAction = "Đã bán 1 ${crop.emoji} ${crop.nameVi} thu về +${crop.sellPrice}G!",
                lastUpdateMillis = System.currentTimeMillis()
            )
        )
    }

    // --- LÒ NƯỚNG BÁNH / BẾP ---

    fun bakeRecipe(recipeId: String): PetState {
        val s = loadState()
        if (s.stage == 1) {
            return saveAndReturn(s.copy(lastAction = "Chờ trứng nở rồi bé mới phụ bạn nướng bánh được!"))
        }
        if (s.isSleeping) {
            return saveAndReturn(s.copy(lastAction = "Bé đang ngủ, đánh thức dậy cùng làm bánh nhé!"))
        }
        val recipe = FarmCatalog.findRecipe(recipeId) ?: return s
        for ((cid, need) in recipe.ingredients) {
            if ((s.cropBag[cid] ?: 0) < need) {
                return saveAndReturn(s.copy(lastAction = "Thiếu nguyên liệu! Hãy trồng thêm ngoài Nông Trại nhé."))
            }
        }
        val newCrops = s.cropBag.toMutableMap()
        for ((cid, need) in recipe.ingredients) {
            val left = (newCrops[cid] ?: 0) - need
            if (left <= 0) newCrops.remove(cid) else newCrops[cid] = left
        }
        val newDishes = s.dishBag.toMutableMap()
        newDishes[recipe.id] = (newDishes[recipe.id] ?: 0) + 1
        return saveAndReturn(
            s.copy(
                cropBag = newCrops,
                dishBag = newDishes,
                farmingXp = s.farmingXp + 12,
                happiness = (s.happiness + 5).coerceAtMost(100),
                lastAction = "Nướng xong ${recipe.emoji} ${recipe.nameVi}! Cho bé ăn hoặc bán kiếm ${recipe.sellPrice}G!",
                lastEffect = "EVOLVE",
                lastUpdateMillis = System.currentTimeMillis()
            )
        )
    }

    fun sellDish(recipeId: String): PetState {
        val s = loadState()
        val count = s.dishBag[recipeId] ?: 0
        if (count <= 0) return s
        val recipe = FarmCatalog.findRecipe(recipeId) ?: return s
        val newDishes = s.dishBag.toMutableMap()
        if (count == 1) newDishes.remove(recipeId) else newDishes[recipeId] = count - 1
        return saveAndReturn(
            s.copy(
                gold = s.gold + recipe.sellPrice,
                dishBag = newDishes,
                lastAction = "Bán ${recipe.emoji} ${recipe.nameVi} tại chợ làng thu về +${recipe.sellPrice}G!",
                lastUpdateMillis = System.currentTimeMillis()
            )
        )
    }

    // --- NHẬN NUÔI & CÀI ĐẶT ---

    fun adoptSpecies(speciesId: String, customName: String? = null): PetState {
        val species = PetCatalog.findById(speciesId)
        val now = System.currentTimeMillis()
        val keepSound = prefs.getBoolean("soundEnabled", true)
        val fresh = PetState(
            hasCompletedOnboarding = true,
            speciesId = species.id,
            nickname = customName?.trim()?.takeIf { it.isNotEmpty() } ?: species.nameVi,
            stage = 1,
            stageEnteredMillis = now,
            nextPoopAtMillis = now + 35L * 60_000L,
            hunger = 85,
            happiness = 85,
            energy = 95,
            cleanliness = 100,
            weightGrams = 5,
            careMistakes = 0,
            poopCount = 0,
            isSleeping = false,
            isSick = false,
            soundEnabled = keepSound,
            gold = 60,
            farmingXp = 0,
            plots = List(6) { FarmPlot(index = it) },
            cropBag = mapOf("wheat" to 2),
            dishBag = mapOf("bread" to 1),
            lastAction = "Đã nhận trứng ${species.nameVi}! Chờ 5 phút nở rồi cùng làm nông trại nhé!",
            lastEffect = "EVOLVE",
            lastUpdateMillis = now,
            birthMillis = now
        )
        return saveAndReturn(fresh)
    }

    fun rename(newName: String): PetState {
        val trimmed = newName.trim()
        if (trimmed.isEmpty()) return loadState()
        return saveAndReturn(loadState().copy(nickname = trimmed))
    }

    fun setSoundEnabled(enabled: Boolean): PetState {
        return saveAndReturn(loadState().copy(soundEnabled = enabled))
    }

    fun resetToRolling(): PetState {
        return saveAndReturn(loadState().copy(hasCompletedOnboarding = false))
    }

    private fun minWeightForStage(stage: Int): Int = when (stage) {
        1 -> 5
        2 -> 10
        3 -> 20
        4 -> 30
        else -> 35
    }

    private fun nextPoopIntervalMillis(stage: Int): Long {
        return if (stage <= 2) {
            Random.nextLong(25L, 36L) * 60_000L
        } else {
            Random.nextLong(150L, 211L) * 60_000L
        }
    }

    private fun serializePlots(plots: List<FarmPlot>): String {
        val arr = JSONArray()
        plots.forEach { p ->
            arr.put(JSONObject().apply {
                put("index", p.index)
                put("cropId", p.cropId ?: "")
                put("plantedAtMillis", p.plantedAtMillis)
                put("watered", p.watered)
            })
        }
        return arr.toString()
    }

    private fun parsePlots(raw: String?): List<FarmPlot> {
        if (raw.isNullOrBlank()) return List(6) { FarmPlot(index = it) }
        return runCatching {
            val arr = JSONArray(raw)
            List(6) { idx ->
                if (idx < arr.length()) {
                    val o = arr.getJSONObject(idx)
                    val cid = o.optString("cropId").takeIf { it.isNotBlank() }
                    FarmPlot(
                        index = idx,
                        cropId = cid,
                        plantedAtMillis = o.optLong("plantedAtMillis", 0L),
                        watered = o.optBoolean("watered", false)
                    )
                } else {
                    FarmPlot(index = idx)
                }
            }
        }.getOrElse { List(6) { FarmPlot(index = it) } }
    }

    private fun serializeIntMap(map: Map<String, Int>): String {
        val obj = JSONObject()
        map.forEach { (k, v) -> if (v > 0) obj.put(k, v) }
        return obj.toString()
    }

    private fun parseIntMap(raw: String?, fallback: Map<String, Int>): Map<String, Int> {
        if (raw == null) return fallback
        return runCatching {
            val obj = JSONObject(raw)
            val out = mutableMapOf<String, Int>()
            val keys = obj.keys()
            while (keys.hasNext()) {
                val k = keys.next()
                val v = obj.optInt(k, 0)
                if (v > 0) out[k] = v
            }
            out
        }.getOrDefault(fallback)
    }

    private fun saveAndReturn(state: PetState): PetState {
        saveState(state)
        return state
    }
}
