package com.pixelpet.tamagotchi.data

import android.content.Context
import com.pixelpet.tamagotchi.model.PetCatalog
import com.pixelpet.tamagotchi.model.PetState

class PetRepository(context: Context) {

    private val prefs = context.getSharedPreferences("pixel_tamagotchi_prefs", Context.MODE_PRIVATE)

    fun loadState(): PetState {
        val now = System.currentTimeMillis()
        val raw = PetState(
            speciesId = prefs.getString("speciesId", "chick") ?: "chick",
            nickname = prefs.getString("nickname", "Gà Bông") ?: "Gà Bông",
            stage = prefs.getInt("stage", 1).coerceIn(1, 5),
            xp = prefs.getInt("xp", 0).coerceAtLeast(0),
            hunger = prefs.getInt("hunger", 85).coerceIn(0, 100),
            happiness = prefs.getInt("happiness", 85).coerceIn(0, 100),
            energy = prefs.getInt("energy", 85).coerceIn(0, 100),
            cleanliness = prefs.getInt("cleanliness", 100).coerceIn(0, 100),
            poopCount = prefs.getInt("poopCount", 0).coerceIn(0, 3),
            isSleeping = prefs.getBoolean("isSleeping", false),
            isSick = prefs.getBoolean("isSick", false),
            lastAction = prefs.getString("lastAction", "Chào bạn! Nhấn Ăn hoặc Chơi nhé!") ?: "",
            lastEffect = prefs.getString("lastEffect", "IDLE") ?: "IDLE",
            lastUpdateMillis = prefs.getLong("lastUpdateMillis", now),
            birthMillis = prefs.getLong("birthMillis", now)
        )
        val updated = applyTimeDecay(raw, now)
        if (updated != raw) {
            saveState(updated)
        }
        return updated
    }

    fun saveState(state: PetState) {
        prefs.edit()
            .putString("speciesId", state.speciesId)
            .putString("nickname", state.nickname)
            .putInt("stage", state.stage)
            .putInt("xp", state.xp)
            .putInt("hunger", state.hunger)
            .putInt("happiness", state.happiness)
            .putInt("energy", state.energy)
            .putInt("cleanliness", state.cleanliness)
            .putInt("poopCount", state.poopCount)
            .putBoolean("isSleeping", state.isSleeping)
            .putBoolean("isSick", state.isSick)
            .putString("lastAction", state.lastAction)
            .putString("lastEffect", state.lastEffect)
            .putLong("lastUpdateMillis", state.lastUpdateMillis)
            .putLong("birthMillis", state.birthMillis)
            .apply()
    }

    private fun applyTimeDecay(state: PetState, now: Long): PetState {
        val elapsedMinutes = ((now - state.lastUpdateMillis).coerceAtLeast(0L) / 60_000L).toInt()
        if (elapsedMinutes < 10) return state

        val steps = (elapsedMinutes / 15).coerceAtLeast(1)
        val hungerDrop = if (state.isSleeping) steps * 2 else steps * 4
        val happyDrop = if (state.isSleeping) steps else steps * 3
        val energyDelta = if (state.isSleeping) steps * 8 else -(steps * 2)

        val newHunger = (state.hunger - hungerDrop).coerceIn(0, 100)
        val newEnergy = (state.energy + energyDelta).coerceIn(0, 100)
        val extraPoop = if (!state.isSleeping && elapsedMinutes >= 45) (elapsedMinutes / 60).coerceAtLeast(1) else 0
        val newPoop = (state.poopCount + extraPoop).coerceIn(0, 3)
        val newClean = (state.cleanliness - newPoop * 10 * steps.coerceAtMost(2)).coerceIn(0, 100)
        val newHappy = (state.happiness - happyDrop - newPoop * 4).coerceIn(0, 100)
        val nowSick = state.isSick || (newPoop >= 3 && newClean < 35) || newHunger <= 5

        val autoWake = state.isSleeping && newEnergy >= 100
        val msg = when {
            nowSick -> "Bé bị ốm rồi! Nhấn Thuốc hoặc Dọn phân ngay nhé."
            autoWake -> "Ngủ đủ giấc rồi! Bé đã thức dậy tràn đầy năng lượng!"
            newPoop > state.poopCount -> "Úi, bé vừa đi vệ sinh! Nhấn Dọn để giữ sạch nhé."
            newHunger < 30 -> "Bụng kêu rột rột... cho bé ăn đi nào!"
            else -> state.lastAction
        }

        return state.copy(
            hunger = newHunger,
            happiness = newHappy,
            energy = newEnergy,
            cleanliness = newClean,
            poopCount = newPoop,
            isSleeping = if (autoWake) false else state.isSleeping,
            isSick = nowSick,
            lastAction = msg,
            lastEffect = if (autoWake) "IDLE" else state.lastEffect,
            lastUpdateMillis = now
        )
    }

    fun feed(): PetState {
        val s = loadState()
        if (s.isSleeping) {
            return saveAndReturn(s.copy(lastAction = "Bé đang ngủ khò khò! Đánh thức trước đã nhé.", lastEffect = "SLEEP"))
        }
        val willPoop = s.hunger >= 75 && s.poopCount < 3
        val newPoop = if (willPoop) s.poopCount + 1 else s.poopCount
        val next = addXpAndCheckEvolution(
            s.copy(
                hunger = (s.hunger + 26).coerceAtMost(100),
                happiness = (s.happiness + 6).coerceAtMost(100),
                poopCount = newPoop,
                cleanliness = if (willPoop) (s.cleanliness - 18).coerceAtLeast(0) else s.cleanliness,
                lastAction = if (willPoop) "Ăn no căng! Úi, bé đi ị mất rồi!" else "Ngon tuyệt! Bụng no thêm rồi (+15 XP)",
                lastEffect = "FEED",
                lastUpdateMillis = System.currentTimeMillis()
            ),
            gainXp = 15
        )
        return saveAndReturn(next)
    }

    fun clean(): PetState {
        val s = loadState()
        val hadDirt = s.poopCount > 0 || s.cleanliness < 100
        val next = addXpAndCheckEvolution(
            s.copy(
                poopCount = 0,
                cleanliness = 100,
                happiness = (s.happiness + if (hadDirt) 12 else 4).coerceAtMost(100),
                lastAction = if (hadDirt) "Đã dọn sạch bong kin kít! (+12 XP)" else "Phòng đang rất sạch sẽ rồi!",
                lastEffect = "CLEAN",
                lastUpdateMillis = System.currentTimeMillis()
            ),
            gainXp = if (hadDirt) 12 else 3
        )
        return saveAndReturn(next)
    }

    fun play(bonusXp: Int = 18, customMessage: String? = null): PetState {
        val s = loadState()
        if (s.isSleeping) {
            return saveAndReturn(s.copy(lastAction = "Bé đang ngủ, đánh thức bé dậy chơi nhé!", lastEffect = "SLEEP"))
        }
        if (s.energy < 12) {
            return saveAndReturn(s.copy(lastAction = "Bé mệt quá rồi! Cho bé đi ngủ nạp năng lượng nhé.", lastEffect = "IDLE"))
        }
        val next = addXpAndCheckEvolution(
            s.copy(
                happiness = (s.happiness + 24).coerceAtMost(100),
                energy = (s.energy - 12).coerceAtLeast(0),
                hunger = (s.hunger - 8).coerceAtLeast(0),
                lastAction = customMessage ?: "Chơi vui quá đi! (+$bonusXp XP)",
                lastEffect = "PLAY",
                lastUpdateMillis = System.currentTimeMillis()
            ),
            gainXp = bonusXp
        )
        return saveAndReturn(next)
    }

    fun toggleSleep(): PetState {
        val s = loadState()
        val nowSleeping = !s.isSleeping
        val next = s.copy(
            isSleeping = nowSleeping,
            energy = if (!nowSleeping) (s.energy + 15).coerceAtMost(100) else s.energy,
            lastAction = if (nowSleeping) "Tắt đèn đi ngủ... Khò khò Zzz" else "Chào buổi sáng! Bé đã tỉnh giấc!",
            lastEffect = if (nowSleeping) "SLEEP" else "IDLE",
            lastUpdateMillis = System.currentTimeMillis()
        )
        return saveAndReturn(next)
    }

    fun heal(): PetState {
        val s = loadState()
        val next = addXpAndCheckEvolution(
            s.copy(
                isSick = false,
                cleanliness = (s.cleanliness + 25).coerceAtMost(100),
                happiness = (s.happiness + 10).coerceAtMost(100),
                energy = (s.energy + 10).coerceAtMost(100),
                lastAction = if (s.isSick) "Uống thuốc xong bé khỏe lại ngay! (+10 XP)" else "Tắm mát & bổ sung vitamin khỏe khoắn!",
                lastEffect = "HEAL",
                lastUpdateMillis = System.currentTimeMillis()
            ),
            gainXp = if (s.isSick) 10 else 5
        )
        return saveAndReturn(next)
    }

    fun selectSpecies(speciesId: String, customName: String? = null): PetState {
        val species = PetCatalog.findById(speciesId)
        val now = System.currentTimeMillis()
        val fresh = PetState(
            speciesId = species.id,
            nickname = customName?.takeIf { it.isNotBlank() } ?: species.nameVi,
            stage = 1,
            xp = 0,
            hunger = 85,
            happiness = 85,
            energy = 90,
            cleanliness = 100,
            poopCount = 0,
            isSleeping = false,
            isSick = false,
            lastAction = "Đã nhận nuôi ${species.nameVi}! Chăm sóc để tiến hóa 5 cấp nhé!",
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

    fun triggerInstantEvolvePreview(): PetState {
        val s = loadState()
        val nextStage = if (s.stage >= 5) 1 else s.stage + 1
        val updated = s.copy(
            stage = nextStage,
            xp = 0,
            lastAction = "Tiến hóa Cấp $nextStage/5: ${s.species.stageNames[nextStage - 1]}!",
            lastEffect = "EVOLVE",
            lastUpdateMillis = System.currentTimeMillis()
        )
        return saveAndReturn(updated)
    }

    private fun addXpAndCheckEvolution(state: PetState, gainXp: Int): PetState {
        if (state.stage >= 5) {
            return state.copy(xp = state.xpForNextStage)
        }
        var stage = state.stage
        var xp = state.xp + gainXp
        var evolved = false
        while (stage < 5) {
            val need = when (stage) {
                1 -> 40
                2 -> 80
                3 -> 130
                else -> 200
            }
            if (xp >= need) {
                xp -= need
                stage++
                evolved = true
            } else {
                break
            }
        }
        return if (evolved) {
            state.copy(
                stage = stage,
                xp = xp,
                lastAction = "CHÚC MỪNG! Tiến hóa Cấp $stage: ${state.species.stageNames[stage - 1]}!",
                lastEffect = "EVOLVE"
            )
        } else {
            state.copy(xp = xp)
        }
    }

    private fun saveAndReturn(state: PetState): PetState {
        saveState(state)
        return state
    }
}
