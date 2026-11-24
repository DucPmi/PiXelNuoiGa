package com.pixelpet.tamagotchi.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.pixelpet.tamagotchi.audio.ChiptuneAudioEngine
import com.pixelpet.tamagotchi.data.PetRepository
import com.pixelpet.tamagotchi.model.PetState
import com.pixelpet.tamagotchi.widget.TamagotchiWidgetProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

class PetViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = PetRepository(application)

    private val _petState = MutableStateFlow(repo.loadState())
    val petState: StateFlow<PetState> = _petState.asStateFlow()

    private val _animFrame = MutableStateFlow(0)
    val animFrame: StateFlow<Int> = _animFrame.asStateFlow()

    private val _nowMillis = MutableStateFlow(System.currentTimeMillis())
    val nowMillis: StateFlow<Long> = _nowMillis.asStateFlow()

    init {
        ChiptuneAudioEngine.isSoundEnabled = _petState.value.soundEnabled
        viewModelScope.launch {
            var tick = 0
            while (true) {
                delay(700L)
                _animFrame.value = (_animFrame.value + 1) % 4
                _nowMillis.value = System.currentTimeMillis()
                tick++
                if (tick % 10 == 0) {
                    val refreshed = repo.loadState()
                    if (refreshed.stage > _petState.value.stage) {
                        ChiptuneAudioEngine.playFanfare()
                        syncWidget()
                    }
                    _petState.value = refreshed
                }
            }
        }
    }

    fun feed(dishId: String? = null) {
        val next = repo.feedBestAvailableDish(dishId)
        if (next.lastEffect == "FEED") ChiptuneAudioEngine.playFeed() else ChiptuneAudioEngine.playTick()
        _petState.value = next
        syncWidget()
    }

    fun clean() {
        ChiptuneAudioEngine.playClean()
        _petState.value = repo.clean()
        syncWidget()
    }

    fun play() {
        val next = repo.play()
        if (next.lastEffect == "PLAY") ChiptuneAudioEngine.playPlay() else ChiptuneAudioEngine.playTick()
        _petState.value = next
        syncWidget()
    }

    fun playGuessDirectionGame(guessedLeft: Boolean): Boolean {
        val petLookedLeft = Random.nextBoolean()
        val won = guessedLeft == petLookedLeft
        val dir = if (petLookedLeft) "TRÁI" else "PHẢI"
        val msg = if (won) {
            "Đoán trúng hướng $dir! Bé cực kỳ phấn khích (+35% Vui)"
        } else {
            "Bé quay sang $dir mất rồi, thử lại nhé! (+12% Vui)"
        }
        if (won) ChiptuneAudioEngine.playFanfare() else ChiptuneAudioEngine.playTick()
        _petState.value = repo.play(bonusHappy = if (won) 35 else 12, customMessage = msg)
        syncWidget()
        return won
    }

    fun toggleSleep() {
        ChiptuneAudioEngine.playSleep()
        _petState.value = repo.toggleSleep()
        syncWidget()
    }

    fun heal() {
        ChiptuneAudioEngine.playHeal()
        _petState.value = repo.heal()
        syncWidget()
    }

    fun plantCrop(plotIndex: Int, cropId: String) {
        ChiptuneAudioEngine.playFarm()
        _petState.value = repo.plantCrop(plotIndex, cropId)
        syncWidget()
    }

    fun waterPlot(plotIndex: Int) {
        ChiptuneAudioEngine.playClean()
        _petState.value = repo.waterPlot(plotIndex)
        syncWidget()
    }

    fun harvestPlot(plotIndex: Int) {
        ChiptuneAudioEngine.playFanfare()
        _petState.value = repo.harvestPlot(plotIndex)
        syncWidget()
    }

    fun sellCrop(cropId: String) {
        ChiptuneAudioEngine.playTick()
        _petState.value = repo.sellCrop(cropId)
        syncWidget()
    }

    fun bakeRecipe(recipeId: String) {
        ChiptuneAudioEngine.playBake()
        _petState.value = repo.bakeRecipe(recipeId)
        syncWidget()
    }

    fun sellDish(recipeId: String) {
        ChiptuneAudioEngine.playTick()
        _petState.value = repo.sellDish(recipeId)
        syncWidget()
    }

    fun adoptSpecies(speciesId: String, customName: String? = null) {
        ChiptuneAudioEngine.playFanfare()
        _petState.value = repo.adoptSpecies(speciesId, customName)
        syncWidget()
    }

    fun rename(name: String) {
        _petState.value = repo.rename(name)
        syncWidget()
    }

    fun toggleSound() {
        val nextEnabled = !_petState.value.soundEnabled
        ChiptuneAudioEngine.isSoundEnabled = nextEnabled
        if (nextEnabled) {
            ChiptuneAudioEngine.startBgm()
            ChiptuneAudioEngine.playTick()
        } else {
            ChiptuneAudioEngine.stopBgm()
        }
        _petState.value = repo.setSoundEnabled(nextEnabled)
    }

    fun openStepRolling() {
        ChiptuneAudioEngine.playTick()
        _petState.value = repo.resetToRolling()
    }

    private fun syncWidget() {
        TamagotchiWidgetProvider.updateAll(getApplication())
    }
}
