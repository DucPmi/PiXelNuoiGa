package com.pixelpet.tamagotchi.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixelpet.tamagotchi.audio.ChiptuneAudioEngine
import com.pixelpet.tamagotchi.model.FarmCatalog
import com.pixelpet.tamagotchi.model.FarmPlot
import com.pixelpet.tamagotchi.model.PetCatalog
import com.pixelpet.tamagotchi.model.PetSpecies
import com.pixelpet.tamagotchi.model.PetState
import com.pixelpet.tamagotchi.pixel.PixelArtRenderer
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

private val InkDark = Color(0xFF1E293B)

@Composable
fun TamagotchiRootScreen(viewModel: PetViewModel) {
    val state by viewModel.petState.collectAsState()
    if (!state.hasCompletedOnboarding) {
        EggRollingOnboardingScreen(viewModel = viewModel)
    } else {
        PixelPetFarmMainScreen(viewModel = viewModel)
    }
}

@Composable
private fun EggRollingOnboardingScreen(viewModel: PetViewModel) {
    val scope = rememberCoroutineScope()
    val allSpecies = PetCatalog.allSpecies

    var currentIndex by remember { mutableIntStateOf(0) }
    var isRolling by remember { mutableStateOf(false) }
    var hasRolledOnce by remember { mutableStateOf(false) }
    var petNickname by remember { mutableStateOf(allSpecies[0].nameVi) }
    var showManualPicker by remember { mutableStateOf(false) }

    val selectedSpecies = allSpecies[currentIndex]
    val eggBitmap = remember(selectedSpecies.id) {
        PixelArtRenderer.renderPetIconBitmap(selectedSpecies, stage = 1, sizePx = 160)
    }
    val previewAdultBitmap = remember(selectedSpecies.id) {
        PixelArtRenderer.renderPetIconBitmap(selectedSpecies, stage = 4, sizePx = 112)
    }

    val bgBrush = Brush.verticalGradient(
        colors = listOf(Color(0xFFE0F2FE), Color(0xFFFEF9C3), Color(0xFFDCFCE7))
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgBrush)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "PIXEL PET FARM",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 24.sp,
                color = InkDark
            )
            Text(
                text = "QUAY TRỨNG CHỌN CHỦ NÔNG TRẠI TÍ HON",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 11.5.sp,
                color = Color(0xFF0F766E)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(28.dp))
                    .background(Color(0xFFFFFBEB))
                    .border(3.5.dp, InkDark, RoundedCornerShape(28.dp))
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(152.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFFBAE6FD))
                            .border(3.dp, InkDark, RoundedCornerShape(20.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            bitmap = eggBitmap.asImageBitmap(),
                            contentDescription = selectedSpecies.nameVi,
                            filterQuality = FilterQuality.None,
                            modifier = Modifier.size(118.dp)
                        )
                    }

                    Text(
                        text = if (isRolling) "Đang quay trứng..." else selectedSpecies.stageNames[0],
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 17.sp,
                        color = InkDark
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFFFEF3C7))
                            .border(2.dp, InkDark, RoundedCornerShape(14.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Image(
                            bitmap = previewAdultBitmap.asImageBitmap(),
                            contentDescription = null,
                            filterQuality = FilterQuality.None,
                            modifier = Modifier.size(44.dp)
                        )
                        Column {
                            Text(
                                text = "Loài: ${selectedSpecies.nameVi}",
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = InkDark
                            )
                            Text(
                                text = "Nở sau 5p • Trồng cây & làm bánh!",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                color = Color(0xFF475569)
                            )
                        }
                    }

                    if (hasRolledOnce && !isRolling) {
                        OutlinedTextField(
                            value = petNickname,
                            onValueChange = { petNickname = it },
                            label = { Text("Đặt tên cho chủ nông trại") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            TactilePixelButton(
                label = if (isRolling) "🎲 ĐANG QUAY..." else if (hasRolledOnce) "🎲 QUAY TRỨNG KHÁC" else "🎲 QUAY TRỨNG NGẪU NHIÊN",
                bg = Color(0xFFFDE047),
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    if (isRolling) return@TactilePixelButton
                    isRolling = true
                    scope.launch {
                        val totalSteps = Random.nextInt(18, 28)
                        for (step in 0 until totalSteps) {
                            currentIndex = (currentIndex + 1) % allSpecies.size
                            ChiptuneAudioEngine.playTick()
                            val pauseMs = when {
                                step < totalSteps - 8 -> 55L
                                step < totalSteps - 4 -> 115L
                                else -> 210L
                            }
                            delay(pauseMs)
                        }
                        petNickname = allSpecies[currentIndex].nameVi
                        hasRolledOnce = true
                        isRolling = false
                        ChiptuneAudioEngine.playFanfare()
                    }
                }
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                TactilePixelButton(
                    label = "🐾 Chọn 20 loài",
                    bg = Color(0xFFBAE6FD),
                    modifier = Modifier.weight(1f),
                    onClick = { if (!isRolling) showManualPicker = true }
                )
                TactilePixelButton(
                    label = "✅ NHẬN TRỨNG",
                    bg = Color(0xFF86EFAC),
                    modifier = Modifier.weight(1f),
                    onClick = {
                        if (!isRolling) viewModel.adoptSpecies(selectedSpecies.id, petNickname)
                    }
                )
            }
        }
    }

    if (showManualPicker) {
        SpeciesPickerDialog(
            currentId = selectedSpecies.id,
            onSelect = { id ->
                val idx = allSpecies.indexOfFirst { it.id == id }.coerceAtLeast(0)
                currentIndex = idx
                petNickname = allSpecies[idx].nameVi
                hasRolledOnce = true
                showManualPicker = false
                ChiptuneAudioEngine.playTick()
            },
            onDismiss = { showManualPicker = false }
        )
    }
}

@Composable
private fun PixelPetFarmMainScreen(viewModel: PetViewModel) {
    val state by viewModel.petState.collectAsState()
    val animFrame by viewModel.animFrame.collectAsState()
    val nowMillis by viewModel.nowMillis.collectAsState()

    var activeTab by remember { mutableIntStateOf(0) } // 0=Thú, 1=Nông Trại, 2=Lò Bánh
    var showSpeciesDialog by remember { mutableStateOf(false) }
    var showEvolutionDialog by remember { mutableStateOf(false) }
    var showMiniGameDialog by remember { mutableStateOf(false) }
    var showRenameDialog by remember { mutableStateOf(false) }
    var plantingPlotIndex by remember { mutableStateOf<Int?>(null) }

    val bgBrush = Brush.verticalGradient(
        colors = listOf(Color(0xFFBAE6FD), Color(0xFFFEF9C3), Color(0xFFDCFCE7))
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgBrush)
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Top bar: Vàng + Kỹ năng Nông Dân + Nhạc + Roll Trứng
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    MiniChipButton("🪙 ${state.gold}G", Color(0xFFFDE047)) {}
                    MiniChipButton("🌾 Lv.${state.farmingLevel}", Color(0xFFBBF7D0)) {}
                }
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    MiniChipButton(
                        label = if (state.soundEnabled) "🔊" else "🔇",
                        bg = if (state.soundEnabled) Color(0xFF86EFAC) else Color(0xFFE2E8F0),
                        onClick = { viewModel.toggleSound() }
                    )
                    MiniChipButton("🎲 Roll", Color(0xFFFED7AA)) { viewModel.openStepRolling() }
                }
            }

            // 3 Tab chính: Thú Nuôi • Nông Trại • Lò Bánh
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val readyCount = state.readyCropsCount(nowMillis)
                TabPill(
                    label = "🐾 Thú Nuôi",
                    selected = activeTab == 0,
                    modifier = Modifier.weight(1f)
                ) { activeTab = 0 }
                TabPill(
                    label = if (readyCount > 0) "🌱 Trại ($readyCount!)" else "🌱 Nông Trại",
                    selected = activeTab == 1,
                    modifier = Modifier.weight(1f)
                ) { activeTab = 1 }
                TabPill(
                    label = "🍰 Lò Bánh (${state.totalDishes})",
                    selected = activeTab == 2,
                    modifier = Modifier.weight(1f)
                ) { activeTab = 2 }
            }

            // Thông báo trạng thái chung
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFFFFBEB))
                    .border(2.dp, InkDark, RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp, vertical = 7.dp)
            ) {
                Text(
                    text = state.lastAction,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = InkDark,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Nội dung từng Tab
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                when (activeTab) {
                    0 -> PetCareTab(
                        state = state,
                        animFrame = animFrame,
                        onFeed = { viewModel.feed() },
                        onClean = { viewModel.clean() },
                        onPlay = { viewModel.play() },
                        onSleep = { viewModel.toggleSleep() },
                        onHeal = { viewModel.heal() },
                        onOpenMiniGame = { showMiniGameDialog = true },
                        onOpenSpecies = { showSpeciesDialog = true },
                        onOpenEvolution = { showEvolutionDialog = true },
                        onRename = { showRenameDialog = true }
                    )
                    1 -> FarmTab(
                        state = state,
                        nowMillis = nowMillis,
                        onTapEmptyPlot = { idx -> plantingPlotIndex = idx },
                        onWaterPlot = { idx -> viewModel.waterPlot(idx) },
                        onHarvestPlot = { idx -> viewModel.harvestPlot(idx) },
                        onSellCrop = { cid -> viewModel.sellCrop(cid) }
                    )
                    else -> BakeryTab(
                        state = state,
                        onBake = { rid -> viewModel.bakeRecipe(rid) },
                        onFeedDish = { rid -> viewModel.feed(rid) },
                        onSellDish = { rid -> viewModel.sellDish(rid) }
                    )
                }
            }
        }
    }

    plantingPlotIndex?.let { idx ->
        SeedPickerDialog(
            gold = state.gold,
            onPick = { cid ->
                viewModel.plantCrop(idx, cid)
                plantingPlotIndex = null
            },
            onDismiss = { plantingPlotIndex = null }
        )
    }

    if (showSpeciesDialog) {
        SpeciesPickerDialog(
            currentId = state.speciesId,
            onSelect = { id ->
                viewModel.adoptSpecies(id)
                showSpeciesDialog = false
            },
            onDismiss = { showSpeciesDialog = false }
        )
    }

    if (showEvolutionDialog) {
        EvolutionTreeDialog(
            species = state.species,
            currentStage = state.stage,
            countdown = state.formattedEvolutionCountdown(nowMillis),
            careMistakes = state.careMistakes,
            onDismiss = { showEvolutionDialog = false }
        )
    }

    if (showMiniGameDialog) {
        GuessDirectionMiniGameDialog(
            onGuess = { left -> viewModel.playGuessDirectionGame(left) },
            onDismiss = { showMiniGameDialog = false }
        )
    }

    if (showRenameDialog) {
        RenameDialog(
            initialName = state.nickname,
            onConfirm = { newName ->
                viewModel.rename(newName)
                showRenameDialog = false
            },
            onDismiss = { showRenameDialog = false }
        )
    }
}

@Composable
private fun PetCareTab(
    state: PetState,
    animFrame: Int,
    onFeed: () -> Unit,
    onClean: () -> Unit,
    onPlay: () -> Unit,
    onSleep: () -> Unit,
    onHeal: () -> Unit,
    onOpenMiniGame: () -> Unit,
    onOpenSpecies: () -> Unit,
    onOpenEvolution: () -> Unit,
    onRename: () -> Unit
) {
    val sceneBitmap = remember(state, animFrame) {
        PixelArtRenderer.renderSceneBitmap(state, widthPx = 480, heightPx = 240, animFrame = animFrame)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(26.dp))
                .background(Color(0xFFFFFBEB))
                .border(3.dp, InkDark, RoundedCornerShape(26.dp))
                .padding(12.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f).clickable(onClick = onRename)) {
                        Text(
                            text = "${state.nickname} ✎",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp,
                            color = InkDark
                        )
                        Text(
                            text = "Cấp ${state.stage}/5 • ${state.stageName}",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = Color(0xFF0F766E)
                        )
                    }
                    MiniChipButton("⏳ ${state.formattedEvolutionCountdown()}", Color(0xFFFEF08A)) { onOpenEvolution() }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF334155))
                        .border(2.5.dp, InkDark, RoundedCornerShape(16.dp))
                        .padding(6.dp)
                ) {
                    Image(
                        bitmap = sceneBitmap.asImageBitmap(),
                        contentDescription = "Pixel Pet Stage",
                        contentScale = ContentScale.FillWidth,
                        filterQuality = FilterQuality.None,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(2f)
                            .clip(RoundedCornerShape(10.dp))
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White)
                        .border(2.dp, InkDark, RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 7.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text("🍗 Đói: ${heartsRow(state.hungerHearts)} (${state.hunger}%)", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = InkDark)
                        Text("💖 Vui: ${heartsRow(state.happinessHearts)} (${state.happiness}%)", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = InkDark)
                    }
                    Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text("⚡ NL ${state.energy}% • ✨ ${state.cleanliness}%", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = InkDark)
                        Text("⚖️ ${state.weightGrams}g • Lỗi: ${state.careMistakes}", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = if (state.careMistakes > 2) Color(0xFFDC2626) else Color(0xFF0F766E))
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TactilePixelButton("🍰 Cho ăn (${state.totalDishes})", Color(0xFFFDE047), Modifier.weight(1f), onFeed)
                    TactilePixelButton("🧹 Dọn phân", Color(0xFF86EFAC), Modifier.weight(1f), onClean)
                    TactilePixelButton("🎾 Vui chơi", Color(0xFFF9A8D4), Modifier.weight(1f), onPlay)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TactilePixelButton(if (state.isSleeping) "☀️ Đánh thức" else "💤 Đi ngủ", Color(0xFF93C5FD), Modifier.weight(1f), onSleep)
                    TactilePixelButton("💊 Thuốc", Color(0xFFFCA5A5), Modifier.weight(1f), onHeal)
                    TactilePixelButton("🎮 Mini Game", Color(0xFFC4B5FD), Modifier.weight(1f), onOpenMiniGame)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TactilePixelButton("🐾 Đổi 20 Thú", Color(0xFF67E8F9), Modifier.weight(1f), onOpenSpecies)
                    TactilePixelButton("⭐ 5 Cấp Tiến Hóa", Color(0xFFFDBA74), Modifier.weight(1f), onOpenEvolution)
                }
            }
        }
    }
}

@Composable
private fun FarmTab(
    state: PetState,
    nowMillis: Long,
    onTapEmptyPlot: (Int) -> Unit,
    onWaterPlot: (Int) -> Unit,
    onHarvestPlot: (Int) -> Unit,
    onSellCrop: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Bảng 6 ô đất nông trại (3x2)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(Color(0xFFDCFCE7))
                .border(3.dp, InkDark, RoundedCornerShape(22.dp))
                .padding(12.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("🌱 VƯỜN NÔNG TRẠI (6 Ô ĐẤT)", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.ExtraBold, fontSize = 12.sp, color = InkDark)
                    Text("⚡ NL thú: ${state.energy}%", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color(0xFF0F766E))
                }

                for (row in 0..1) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        for (col in 0..2) {
                            val plot = state.plots[row * 3 + col]
                            FarmPlotCard(
                                plot = plot,
                                nowMillis = nowMillis,
                                modifier = Modifier.weight(1f),
                                onTap = {
                                    when {
                                        plot.cropId == null -> onTapEmptyPlot(plot.index)
                                        plot.isReady(nowMillis) -> onHarvestPlot(plot.index)
                                        !plot.watered -> onWaterPlot(plot.index)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }

        // Kho Nông Sản (Bán hoặc giữ làm bánh)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFFFFFBEB))
                .border(2.5.dp, InkDark, RoundedCornerShape(20.dp))
                .padding(12.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("🧺 KHO NÔNG SẢN (Giữ để nướng bánh hoặc bán lấy Vàng)", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.ExtraBold, fontSize = 11.sp, color = InkDark)
                val owned = FarmCatalog.crops.filter { (state.cropBag[it.id] ?: 0) > 0 }
                if (owned.isEmpty()) {
                    Text("Kho trống! Chạm ô đất phía trên để gieo hạt nhé.", fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = Color(0xFF475569))
                } else {
                    owned.forEach { crop ->
                        val qty = state.cropBag[crop.id] ?: 0
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.White)
                                .border(1.5.dp, InkDark, RoundedCornerShape(10.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("${crop.emoji} ${crop.nameVi}  x$qty", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = InkDark)
                            MiniChipButton("Bán +${crop.sellPrice}G", Color(0xFFFDE047)) { onSellCrop(crop.id) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FarmPlotCard(
    plot: FarmPlot,
    nowMillis: Long,
    modifier: Modifier = Modifier,
    onTap: () -> Unit
) {
    val crop = plot.crop()
    val ready = plot.isReady(nowMillis)
    val bg = when {
        crop == null -> Color(0xFFD6B98C)
        ready -> Color(0xFFFEF08A)
        plot.watered -> Color(0xFF93C5FD)
        else -> Color(0xFFBBF7D0)
    }
    val remSec = (plot.remainingMillis(nowMillis) / 1000L).coerceAtLeast(0L)
    val timerStr = if (remSec >= 60) "${remSec / 60}p ${remSec % 60}s" else "${remSec}s"

    Column(
        modifier = modifier
            .height(98.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(bg)
            .border(2.dp, InkDark, RoundedCornerShape(14.dp))
            .clickable(onClick = onTap)
            .padding(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (crop == null) {
            Text("➕", fontSize = 22.sp)
            Text("Gieo hạt", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 10.5.sp, color = InkDark)
        } else {
            Text(if (ready) crop.emoji else "🌱", fontSize = 24.sp)
            Text(crop.nameVi, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 10.5.sp, color = InkDark, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(
                text = when {
                    ready -> "✨ Thu hoạch!"
                    !plot.watered -> "💧 Tưới ($timerStr)"
                    else -> "⏳ $timerStr"
                },
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 9.5.sp,
                color = if (ready) Color(0xFFEA580C) else InkDark
            )
        }
    }
}

@Composable
private fun BakeryTab(
    state: PetState,
    onBake: (String) -> Unit,
    onFeedDish: (String) -> Unit,
    onSellDish: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(Color(0xFFFFF1F2))
                .border(3.dp, InkDark, RoundedCornerShape(22.dp))
                .padding(12.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("🔥 LÒ NƯỚNG BÁNH & BẾP LÀNG", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.ExtraBold, fontSize = 12.sp, color = InkDark)

                FarmCatalog.recipes.forEach { recipe ->
                    val canBake = recipe.ingredients.all { (cid, need) -> (state.cropBag[cid] ?: 0) >= need }
                    val ownedCount = state.dishBag[recipe.id] ?: 0
                    val ingText = recipe.ingredients.entries.joinToString(" + ") { (cid, need) ->
                        val c = FarmCatalog.findCrop(cid)
                        val have = state.cropBag[cid] ?: 0
                        "${c?.emoji ?: ""}$have/$need"
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color.White)
                            .border(2.dp, InkDark, RoundedCornerShape(14.dp))
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${recipe.emoji} ${recipe.nameVi} (Có: $ownedCount)",
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 12.sp,
                                    color = InkDark
                                )
                                Text(
                                    text = "Nguyên liệu: $ingText • +${recipe.hungerGain}% Đói, +${recipe.energyGain}% NL",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp,
                                    color = Color(0xFF475569)
                                )
                            }
                            MiniChipButton(
                                label = "🔥 Nướng",
                                bg = if (canBake) Color(0xFF86EFAC) else Color(0xFFE2E8F0)
                            ) { onBake(recipe.id) }
                        }

                        if (ownedCount > 0) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                MiniChipButton("🍽️ Cho bé ăn", Color(0xFFFDE047)) { onFeedDish(recipe.id) }
                                MiniChipButton("🪙 Bán +${recipe.sellPrice}G", Color(0xFFBAE6FD)) { onSellDish(recipe.id) }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SeedPickerDialog(
    gold: Int,
    onPick: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Chọn Hạt Giống (Có ${gold}G)", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
                FarmCatalog.crops.forEach { crop ->
                    val afford = gold >= crop.seedCost
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (afford) Color(0xFFFEF9C3) else Color(0xFFF1F5F9))
                            .border(1.5.dp, InkDark, RoundedCornerShape(10.dp))
                            .clickable(enabled = afford) { onPick(crop.id) }
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("${crop.emoji} ${crop.nameVi}", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = InkDark)
                            Text("Chín: ${crop.growSeconds / 60} phút • Bán: ${crop.sellPrice}G", fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = Color(0xFF475569))
                        }
                        Text("${crop.seedCost}G", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.ExtraBold, fontSize = 12.sp, color = if (afford) Color(0xFF0F766E) else Color(0xFFDC2626))
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Đóng") } }
    )
}

@Composable
private fun TabPill(label: String, selected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .height(38.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) Color(0xFFFDE047) else Color.White.copy(alpha = 0.85f))
            .border(if (selected) 2.5.dp else 1.5.dp, InkDark, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 11.sp,
            color = InkDark,
            maxLines = 1
        )
    }
}

private fun heartsRow(filled: Int): String {
    val full = filled.coerceIn(0, 4)
    return "♥".repeat(full) + "♡".repeat(4 - full)
}

@Composable
private fun MiniChipButton(label: String, bg: Color, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(bg)
            .border(2.dp, InkDark, RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 9.dp, vertical = 5.dp)
    ) {
        Text(label, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = InkDark)
    }
}

@Composable
private fun TactilePixelButton(label: String, bg: Color, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .height(44.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .border(2.dp, InkDark, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 11.sp,
            color = InkDark,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun SpeciesPickerDialog(
    currentId: String,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Kho 20 Loài Thú", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold) },
        text = {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.height(360.dp),
                contentPadding = PaddingValues(4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(PetCatalog.allSpecies) { sp ->
                    val iconBmp = remember(sp.id) {
                        PixelArtRenderer.renderPetIconBitmap(sp, stage = 4, sizePx = 96)
                    }
                    val selected = sp.id == currentId
                    Column(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (selected) Color(0xFFFEF08A) else Color(0xFFF1F5F9))
                            .border(if (selected) 2.5.dp else 1.5.dp, InkDark, RoundedCornerShape(12.dp))
                            .clickable { onSelect(sp.id) }
                            .padding(6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(bitmap = iconBmp.asImageBitmap(), contentDescription = sp.nameVi, filterQuality = FilterQuality.None, modifier = Modifier.size(46.dp))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(sp.nameVi, fontFamily = FontFamily.Monospace, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = InkDark, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Đóng") } }
    )
}

@Composable
private fun EvolutionTreeDialog(
    species: PetSpecies,
    currentStage: Int,
    countdown: String,
    careMistakes: Int,
    onDismiss: () -> Unit
) {
    val timelines = listOf(
        "Nở sau 5 phút",
        "Lên Cấp 3 sau 65 phút",
        "Lên Cấp 4 sau 2 ngày (48g)",
        "Lên Cấp 5 sau 4 ngày (Lỗi <= 2)",
        "Cấp Huyền Thoại tối đa"
    )
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("5 Cấp Tiến Hóa • ${species.nameVi}", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
                Text("Tiến hóa tiếp theo: $countdown • Lỗi chăm sóc: $careMistakes", fontFamily = FontFamily.Monospace, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F766E))
                for (stage in 1..5) {
                    val bmp = remember(species.id, stage) {
                        PixelArtRenderer.renderPetIconBitmap(species, stage = stage, sizePx = 96)
                    }
                    val active = stage == currentStage
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (active) Color(0xFFFEF08A) else Color(0xFFF8FAFC))
                            .border(if (active) 2.dp else 1.dp, InkDark, RoundedCornerShape(10.dp))
                            .padding(7.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(bitmap = bmp.asImageBitmap(), contentDescription = species.stageNames[stage - 1], filterQuality = FilterQuality.None, modifier = Modifier.size(38.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Cấp $stage: ${species.stageNames[stage - 1]}", fontFamily = FontFamily.Monospace, fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = InkDark)
                            Text(timelines[stage - 1], fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = Color(0xFF475569))
                        }
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Đóng") } }
    )
}

@Composable
private fun GuessDirectionMiniGameDialog(
    onGuess: (Boolean) -> Boolean,
    onDismiss: () -> Unit
) {
    var resultText by remember { mutableStateOf("Đoán bé sẽ quay sang TRÁI hay PHẢI để tăng tim Vui!") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Mini Game: Đoán Hướng", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(resultText, fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    TactilePixelButton("⬅️ TRÁI", Color(0xFF93C5FD), Modifier.weight(1f)) {
                        val won = onGuess(true)
                        resultText = if (won) "CHUẨN LUÔN! +35% Vui!" else "Chưa trúng, bé vẫn vui +12%!"
                    }
                    TactilePixelButton("PHẢI ➡️", Color(0xFFF9A8D4), Modifier.weight(1f)) {
                        val won = onGuess(false)
                        resultText = if (won) "CHUẨN LUÔN! +35% Vui!" else "Chưa trúng, bé vẫn vui +12%!"
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Xong") } }
    )
}

@Composable
private fun RenameDialog(
    initialName: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var text by remember { mutableStateOf(initialName) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Đặt tên cho thú nuôi") },
        text = { OutlinedTextField(value = text, onValueChange = { text = it }, singleLine = true, label = { Text("Tên bé") }) },
        confirmButton = { TextButton(onClick = { onConfirm(text) }, enabled = text.isNotBlank()) { Text("Lưu") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Hủy") } }
    )
}
