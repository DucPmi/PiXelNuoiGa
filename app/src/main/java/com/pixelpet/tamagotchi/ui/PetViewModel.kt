package com.pixelpet.tamagotchi.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
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

    init {
        viewModelScope.launch {
            while (true) {
                delay(650L)
                _animFrame.value = (_animFrame.value + 1) % 4
            }
        }
    }

    fun refresh() {
        _petState.value = repo.loadState()
        syncWidget()
    }

    fun feed() {
        _petState.value = repo.feed()
        syncWidget()
    }

    fun clean() {
        _petState.value = repo.clean()
        syncWidget()
    }

    fun play() {
        _petState.value = repo.play()
        syncWidget()
    }

    fun playGuessDirectionGame(guessedLeft: Boolean): Boolean {
        val petLookedLeft = Random.nextBoolean()
        val won = guessedLeft == petLookedLeft
        val dir = if (petLookedLeft) "TRÁI" else "PHẢI"
        val msg = if (won) {
            "Đoán trúng hướng $dir! Bé cực kỳ phấn khích (+25 XP)"
        } else {
            "Bé quay sang $dir mất rồi, thử lại nhé! (+10 XP)"
        }
        _petState.value = repo.play(bonusXp = if (won) 25 else 10, customMessage = msg)
        syncWidget()
        return won
    }

    fun toggleSleep() {
        _petState.value = repo.toggleSleep()
        syncWidget()
    }

    fun heal() {
        _petState.value = repo.heal()
        syncWidget()
    }

    fun selectSpecies(speciesId: String) {
        _petState.value = repo.selectSpecies(speciesId)
        syncWidget()
    }

    fun rename(name: String) {
        _petState.value = repo.rename(name)
        syncWidget()
    }

    fun previewNextStage() {
        _petState.value = repo.triggerInstantEvolvePreview()
        syncWidget()
    }

    private fun syncWidget() {
        TamagotchiWidgetProvider.updateAll(getApplication())
    }
}
