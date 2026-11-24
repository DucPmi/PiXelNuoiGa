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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.pixelpet.tamagotchi.model.PetCatalog
import com.pixelpet.tamagotchi.model.PetSpecies
import com.pixelpet.tamagotchi.pixel.PixelArtRenderer

private val InkDark = Color(0xFF1E293B)

@Composable
fun TamagotchiMainScreen(viewModel: PetViewModel) {
    val state by viewModel.petState.collectAsState()
    val animFrame by viewModel.animFrame.collectAsState()

    var showSpeciesDialog by remember { mutableStateOf(false) }
    var showEvolutionDialog by remember { mutableStateOf(false) }
    var showMiniGameDialog by remember { mutableStateOf(false) }
    var showRenameDialog by remember { mutableStateOf(false) }

    val bgBrush = Brush.verticalGradient(
        colors = listOf(Color(0xFFFEF9C3), Color(0xFFFDE68A), Color(0xFFFBCFE8))
    )

    val sceneBitmap = remember(state, animFrame) {
        PixelArtRenderer.renderSceneBitmap(state, widthPx = 480, heightPx = 240, animFrame = animFrame)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgBrush)
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header máy nuôi thú cổ điển
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "PIXEL TAMAGOTCHI",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp,
                        color = InkDark
                    )
                    Text(
                        text = "${state.nickname} • ${state.stageName} (Cấp ${state.stage}/5)",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F766E),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                PixelButton(
                    label = "Đổi tên",
                    bg = Color(0xFFFFFFFF),
                    onClick = { showRenameDialog = true }
                )
            }

            // Màn hình LCD Pixel Art tươi sáng
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFFFFFBEB))
                    .border(3.dp, InkDark, RoundedCornerShape(18.dp))
                    .padding(10.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Tuổi: ${state.ageDays} ngày",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = InkDark
                        )
                        Text(
                            text = if (state.stage >= 5) "TIẾN HÓA TỐI ĐA" else "XP: ${state.xp}/${state.xpForNextStage}",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFEA580C)
                        )
                    }

                    Image(
                        bitmap = sceneBitmap.asImageBitmap(),
                        contentDescription = "Pixel Pet Stage",
                        contentScale = ContentScale.FillWidth,
                        filterQuality = FilterQuality.None,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(2f)
                            .clip(RoundedCornerShape(12.dp))
                            .border(2.5.dp, InkDark, RoundedCornerShape(12.dp))
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFE0F2FE))
                            .border(1.5.dp, InkDark, RoundedCornerShape(10.dp))
                            .padding(horizontal = 10.dp, vertical = 7.dp)
                    ) {
                        Text(
                            text = state.lastAction,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = InkDark
                        )
                    }
                }
            }

            // 4 Thanh chỉ số pixel tươi sáng
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White.copy(alpha = 0.88f))
                    .border(2.5.dp, InkDark, RoundedCornerShape(16.dp))
                    .padding(12.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        StatBar("🍗 No bụng", state.hunger, Color(0xFFFACC15), Modifier.weight(1f))
                        StatBar("💖 Vui vẻ", state.happiness, Color(0xFFF472B6), Modifier.weight(1f))
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        StatBar("⚡ Năng lượng", state.energy, Color(0xFF38BDF8), Modifier.weight(1f))
                        StatBar("✨ Sạch sẽ", state.cleanliness, Color(0xFF4ADE80), Modifier.weight(1f))
                    }
                }
            }

            // 6 Nút chăm sóc chính
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CareActionButton("🍗 Cho ăn", Color(0xFFFDE047), Modifier.weight(1f)) { viewModel.feed() }
                    CareActionButton("🧹 Dọn phân", Color(0xFF86EFAC), Modifier.weight(1f)) { viewModel.clean() }
                    CareActionButton("🎾 Vui chơi", Color(0xFFF9A8D4), Modifier.weight(1f)) { viewModel.play() }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CareActionButton(
                        if (state.isSleeping) "☀️ Đánh thức" else "💤 Đi ngủ",
                        Color(0xFF93C5FD),
                        Modifier.weight(1f)
                    ) { viewModel.toggleSleep() }
                    CareActionButton("💊 Chữa bệnh", Color(0xFFFCA5A5), Modifier.weight(1f)) { viewModel.heal() }
                    CareActionButton("🎮 Mini Game", Color(0xFFC4B5FD), Modifier.weight(1f)) { showMiniGameDialog = true }
                }
            }

            // Thanh chọn 20 thú nuôi & xem 5 cấp tiến hóa
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CareActionButton(
                    label = "🐾 Chọn 20 Loài Thú",
                    color = Color(0xFF67E8F9),
                    modifier = Modifier.weight(1f)
                ) { showSpeciesDialog = true }

                CareActionButton(
                    label = "⭐ 5 Cấp Tiến Hóa",
                    color = Color(0xFFFDBA74),
                    modifier = Modifier.weight(1f)
                ) { showEvolutionDialog = true }
            }

            // Hướng dẫn ghim Widget 4x3
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFDCFCE7))
                    .border(2.dp, InkDark, RoundedCornerShape(14.dp))
                    .padding(10.dp)
            ) {
                Text(
                    text = "Mẹo: Nhấn giữ màn hình chính → Tiện ích (Widgets) → chọn 'Nuôi Thú Pixel (4x3)' để cho ăn, dọn phân, ngủ và chơi trực tiếp ngoài màn hình!",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    color = InkDark
                )
            }
        }
    }

    if (showSpeciesDialog) {
        SpeciesPickerDialog(
            currentId = state.speciesId,
            onSelect = { id ->
                viewModel.selectSpecies(id)
                showSpeciesDialog = false
            },
            onDismiss = { showSpeciesDialog = false }
        )
    }

    if (showEvolutionDialog) {
        EvolutionTreeDialog(
            species = state.species,
            currentStage = state.stage,
            onTestNextStage = { viewModel.previewNextStage() },
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
private fun StatBar(label: String, value: Int, color: Color, modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, fontFamily = FontFamily.Monospace, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = InkDark)
            Text("$value%", fontFamily = FontFamily.Monospace, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = InkDark)
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color(0xFFE2E8F0))
                .border(1.5.dp, InkDark, RoundedCornerShape(6.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction = (value / 100f).coerceIn(0f, 1f))
                    .height(12.dp)
                    .background(color)
            )
        }
    }
}

@Composable
private fun CareActionButton(label: String, color: Color, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(color)
            .border(2.dp, InkDark, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp,
            fontWeight = FontWeight.ExtraBold,
            color = InkDark,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun PixelButton(label: String, bg: Color, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(bg)
            .border(2.dp, InkDark, RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = InkDark
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
        title = {
            Text(
                "Chọn Thú Nuôi (20 loài)",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.height(380.dp),
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
                        Image(
                            bitmap = iconBmp.asImageBitmap(),
                            contentDescription = sp.nameVi,
                            filterQuality = FilterQuality.None,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = sp.nameVi,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = InkDark,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Đóng") }
        }
    )
}

@Composable
private fun EvolutionTreeDialog(
    species: PetSpecies,
    currentStage: Int,
    onTestNextStage: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "5 Cấp Tiến Hóa • ${species.nameVi}",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            bitmap = bmp.asImageBitmap(),
                            contentDescription = species.stageNames[stage - 1],
                            filterQuality = FilterQuality.None,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Cấp $stage: ${species.stageNames[stage - 1]}",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = InkDark
                            )
                            Text(
                                text = if (active) "Đang ở cấp này" else if (stage < currentStage) "Đã mở khóa" else "Chăm sóc để đạt cấp này",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                color = Color(0xFF475569)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onTestNextStage) { Text("Thử tiến hóa +1") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Đóng") }
        }
    )
}

@Composable
private fun GuessDirectionMiniGameDialog(
    onGuess: (Boolean) -> Boolean,
    onDismiss: () -> Unit
) {
    var resultText by remember { mutableStateOf("Đoán xem bé sẽ quay sang TRÁI hay PHẢI để nhận thưởng +25 XP!") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Mini Game: Đoán Hướng", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(resultText, fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    CareActionButton("⬅️ TRÁI", Color(0xFF93C5FD), Modifier.weight(1f)) {
                        val won = onGuess(true)
                        resultText = if (won) "CHUẨN LUÔN! +25 XP & +24% Vui!" else "Chưa trúng, bé vẫn vui +10 XP!"
                    }
                    CareActionButton("PHẢI ➡️", Color(0xFFF9A8D4), Modifier.weight(1f)) {
                        val won = onGuess(false)
                        resultText = if (won) "CHUẨN LUÔN! +25 XP & +24% Vui!" else "Chưa trúng, bé vẫn vui +10 XP!"
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Xong") }
        }
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
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                singleLine = true,
                label = { Text("Tên bé") }
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(text) }, enabled = text.isNotBlank()) { Text("Lưu") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Hủy") }
        }
    )
}
