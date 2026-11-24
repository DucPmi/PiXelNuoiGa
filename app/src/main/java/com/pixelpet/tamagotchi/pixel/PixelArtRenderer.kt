package com.pixelpet.tamagotchi.pixel

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import com.pixelpet.tamagotchi.model.PetSpecies
import com.pixelpet.tamagotchi.model.PetState
import com.pixelpet.tamagotchi.model.SilhouetteType

object PixelArtRenderer {

    private val OUTLINE = 0xFF1E293B.toInt()
    private val WHITE = 0xFFFFFFFF.toInt()
    private val CHEEK = 0xFFFB7185.toInt()
    private val GOLD = 0xFFFACC15.toInt()
    private val POOP_BROWN = 0xFF92400E.toInt()
    private val POOP_LIGHT = 0xFFB45309.toInt()

    fun renderSceneBitmap(state: PetState, widthPx: Int = 360, heightPx: Int = 180, animFrame: Int = 0): Bitmap {
        val gridW = 60
        val gridH = 30
        val tiny = Bitmap.createBitmap(gridW, gridH, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(tiny)
        val paint = Paint().apply { isAntiAlias = false }

        if (state.isSleeping) {
            paint.color = 0xFF1E1B4B.toInt()
            canvas.drawRect(0f, 0f, gridW.toFloat(), 22f, paint)
            paint.color = 0xFF312E81.toInt()
            canvas.drawRect(0f, 16f, gridW.toFloat(), 22f, paint)
            paint.color = 0xFFFDE047.toInt()
            drawPx(canvas, paint, 8, 4, 3, 3)
            drawPx(canvas, paint, 20, 3, 1, 1)
            drawPx(canvas, paint, 46, 5, 1, 1)
            drawPx(canvas, paint, 52, 3, 1, 1)
            paint.color = 0xFF166534.toInt()
            canvas.drawRect(0f, 22f, gridW.toFloat(), gridH.toFloat(), paint)
            paint.color = 0xFF14532D.toInt()
            canvas.drawRect(0f, 26f, gridW.toFloat(), gridH.toFloat(), paint)
        } else {
            paint.color = 0xFF7DD3FC.toInt()
            canvas.drawRect(0f, 0f, gridW.toFloat(), 14f, paint)
            paint.color = 0xFFBAE6FD.toInt()
            canvas.drawRect(0f, 14f, gridW.toFloat(), 22f, paint)
            paint.color = 0xFFFDE047.toInt()
            drawPx(canvas, paint, 50, 3, 4, 4)
            paint.color = WHITE
            val cloudShift = (animFrame % 2)
            drawPx(canvas, paint, 6 + cloudShift, 4, 7, 2)
            drawPx(canvas, paint, 8 + cloudShift, 3, 4, 1)
            // Hàng rào gỗ nông trại Stardew ở nền sau
            paint.color = 0xFFB45309.toInt()
            drawPx(canvas, paint, 0, 19, gridW, 1)
            for (fx in 2 until gridW step 8) {
                drawPx(canvas, paint, fx, 18, 2, 4)
            }
            paint.color = 0xFF4ADE80.toInt()
            canvas.drawRect(0f, 22f, gridW.toFloat(), 26f, paint)
            paint.color = 0xFF22C55E.toInt()
            canvas.drawRect(0f, 26f, gridW.toFloat(), gridH.toFloat(), paint)
            // Luống đất nhỏ bên trái có mầm cây
            paint.color = 0xFF78350F.toInt()
            drawPx(canvas, paint, 3, 24, 11, 4)
            if (state.plots.any { it.cropId != null }) {
                paint.color = 0xFF86EFAC.toInt()
                drawPx(canvas, paint, 5, 23, 2, 2)
                drawPx(canvas, paint, 9, 23, 2, 2)
            }
            if (state.readyCropsCount() > 0) {
                paint.color = GOLD
                drawPx(canvas, paint, 5, 22, 2, 1)
                drawPx(canvas, paint, 9, 22, 2, 1)
            }
        }

        paint.color = 0x44000000
        drawPx(canvas, paint, 24, 24, 12, 2)

        val bounceX = if (state.stage == 1 && animFrame % 2 == 1) 1 else 0
        val bounceY = if (state.isSleeping || state.stage == 1) 0 else if (animFrame % 2 == 1) -1 else 0
        drawPetSprite(
            canvas = canvas,
            paint = paint,
            species = state.species,
            stage = state.stage,
            ox = 22 + bounceX,
            oy = 9 + bounceY,
            isSleeping = state.isSleeping,
            isSick = state.isSick
        )

        val poopXs = listOf(42, 48, 15)
        for (i in 0 until state.poopCount.coerceAtMost(3)) {
            drawPoop(canvas, paint, poopXs[i], 21)
        }

        when {
            state.isSleeping -> {
                paint.color = 0xFFFDE047.toInt()
                drawZzz(canvas, paint, 40, 7 + (animFrame % 2))
            }
            state.isSick -> {
                paint.color = 0xFFEF4444.toInt()
                drawCross(canvas, paint, 41, 8)
            }
            state.lastEffect == "FEED" -> {
                drawFoodIcon(canvas, paint, 11, 17)
                drawHeart(canvas, paint, 41, 7)
            }
            state.lastEffect == "PLAY" -> {
                drawBallIcon(canvas, paint, 42, 16)
                drawHeart(canvas, paint, 12, 8)
            }
            state.lastEffect == "CLEAN" || state.lastEffect == "EVOLVE" -> {
                drawSparkle(canvas, paint, 12, 8)
                drawSparkle(canvas, paint, 44, 9)
                drawHeart(canvas, paint, 41, 15)
            }
            state.lastEffect == "HEAL" -> {
                drawHeart(canvas, paint, 41, 8)
                drawSparkle(canvas, paint, 13, 9)
            }
            state.happiness >= 80 && state.stage > 1 -> {
                drawHeart(canvas, paint, 41, 8)
            }
        }

        return Bitmap.createScaledBitmap(tiny, widthPx, heightPx, false)
    }

    fun renderPetIconBitmap(species: PetSpecies, stage: Int, sizePx: Int = 128): Bitmap {
        val grid = 18
        val tiny = Bitmap.createBitmap(grid, grid, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(tiny)
        val paint = Paint().apply { isAntiAlias = false }
        drawPetSprite(canvas, paint, species, stage, ox = 1, oy = 1, isSleeping = false, isSick = false)
        return Bitmap.createScaledBitmap(tiny, sizePx, sizePx, false)
    }

    private fun drawPetSprite(
        canvas: Canvas,
        paint: Paint,
        species: PetSpecies,
        stage: Int,
        ox: Int,
        oy: Int,
        isSleeping: Boolean,
        isSick: Boolean
    ) {
        if (stage == 1) {
            drawEgg(canvas, paint, species, ox, oy)
            return
        }

        val p = species.primaryColor
        val s = species.secondaryColor
        val a = species.accentColor

        if (stage >= 5) {
            paint.color = GOLD
            drawPx(canvas, paint, ox + 5, oy + 0, 1, 2)
            drawPx(canvas, paint, ox + 7, oy + 0, 2, 2)
            drawPx(canvas, paint, ox + 10, oy + 0, 1, 2)
            drawPx(canvas, paint, ox + 5, oy + 2, 6, 1)
            paint.color = 0xFFFEF08A.toInt()
            drawPx(canvas, paint, ox + 0, oy + 6, 2, 4)
            drawPx(canvas, paint, ox + 14, oy + 6, 2, 4)
        }

        val bodyLeft = if (stage == 2) ox + 4 else ox + 3
        val bodyTop = if (stage == 2) oy + 6 else oy + 4
        val bodyW = if (stage == 2) 8 else 10
        val bodyH = if (stage == 2) 7 else 9

        paint.color = OUTLINE
        drawPx(canvas, paint, bodyLeft - 1, bodyTop, bodyW + 2, bodyH)
        drawPx(canvas, paint, bodyLeft, bodyTop - 1, bodyW, bodyH + 2)

        paint.color = p
        drawPx(canvas, paint, bodyLeft, bodyTop, bodyW, bodyH)

        paint.color = s
        drawPx(canvas, paint, bodyLeft + 2, bodyTop + bodyH - 4, bodyW - 4, 3)

        drawSpeciesFeatures(canvas, paint, species.silhouette, stage, ox, oy, p, s, a)

        if (species.silhouette != SilhouetteType.SLIME && species.silhouette != SilhouetteType.OCTOPUS) {
            paint.color = OUTLINE
            drawPx(canvas, paint, bodyLeft + 1, bodyTop + bodyH, 2, 2)
            drawPx(canvas, paint, bodyLeft + bodyW - 3, bodyTop + bodyH, 2, 2)
        }

        val eyeY = bodyTop + 3
        val leftEyeX = bodyLeft + 2
        val rightEyeX = bodyLeft + bodyW - 4
        paint.color = OUTLINE
        when {
            isSleeping -> {
                drawPx(canvas, paint, leftEyeX, eyeY + 1, 2, 1)
                drawPx(canvas, paint, rightEyeX, eyeY + 1, 2, 1)
            }
            isSick -> {
                drawPx(canvas, paint, leftEyeX, eyeY, 1, 1)
                drawPx(canvas, paint, leftEyeX + 1, eyeY + 1, 1, 1)
                drawPx(canvas, paint, rightEyeX, eyeY + 1, 1, 1)
                drawPx(canvas, paint, rightEyeX + 1, eyeY, 1, 1)
            }
            else -> {
                drawPx(canvas, paint, leftEyeX, eyeY, 2, 2)
                drawPx(canvas, paint, rightEyeX, eyeY, 2, 2)
                paint.color = WHITE
                drawPx(canvas, paint, leftEyeX, eyeY, 1, 1)
                drawPx(canvas, paint, rightEyeX, eyeY, 1, 1)
            }
        }

        if (!isSick) {
            paint.color = CHEEK
            drawPx(canvas, paint, leftEyeX - 1, eyeY + 2, 1, 1)
            drawPx(canvas, paint, rightEyeX + 2, eyeY + 2, 1, 1)
        }

        paint.color = if (species.silhouette == SilhouetteType.CHICK || species.silhouette == SilhouetteType.PENGUIN || species.silhouette == SilhouetteType.OWL) {
            a
        } else {
            OUTLINE
        }
        drawPx(canvas, paint, ox + 7, eyeY + 2, 2, 1)
    }

    private fun drawEgg(canvas: Canvas, paint: Paint, species: PetSpecies, ox: Int, oy: Int) {
        paint.color = OUTLINE
        drawPx(canvas, paint, ox + 5, oy + 3, 6, 1)
        drawPx(canvas, paint, ox + 4, oy + 4, 8, 2)
        drawPx(canvas, paint, ox + 3, oy + 6, 10, 6)
        drawPx(canvas, paint, ox + 4, oy + 12, 8, 2)
        drawPx(canvas, paint, ox + 5, oy + 14, 6, 1)

        paint.color = WHITE
        drawPx(canvas, paint, ox + 5, oy + 4, 6, 2)
        drawPx(canvas, paint, ox + 4, oy + 6, 8, 6)
        drawPx(canvas, paint, ox + 5, oy + 12, 6, 2)

        paint.color = species.primaryColor
        drawPx(canvas, paint, ox + 5, oy + 6, 2, 2)
        drawPx(canvas, paint, ox + 9, oy + 9, 2, 2)
        paint.color = species.accentColor
        drawPx(canvas, paint, ox + 6, oy + 10, 2, 2)
        drawPx(canvas, paint, ox + 9, oy + 5, 1, 2)
    }

    private fun drawSpeciesFeatures(
        canvas: Canvas,
        paint: Paint,
        type: SilhouetteType,
        stage: Int,
        ox: Int,
        oy: Int,
        p: Int,
        s: Int,
        a: Int
    ) {
        when (type) {
            SilhouetteType.CHICK -> {
                paint.color = a
                drawPx(canvas, paint, ox + 7, oy + 2, 2, 2)
                if (stage >= 3) {
                    paint.color = s
                    drawPx(canvas, paint, ox + 1, oy + 7, 2, 3)
                    drawPx(canvas, paint, ox + 13, oy + 7, 2, 3)
                }
            }
            SilhouetteType.CAT, SilhouetteType.FOX -> {
                paint.color = p
                drawPx(canvas, paint, ox + 3, oy + 2, 2, 2)
                drawPx(canvas, paint, ox + 11, oy + 2, 2, 2)
                if (stage >= 3) {
                    paint.color = if (type == SilhouetteType.FOX) s else a
                    drawPx(canvas, paint, ox + 13, oy + 8, 2, 4)
                }
            }
            SilhouetteType.DOG -> {
                paint.color = a
                drawPx(canvas, paint, ox + 2, oy + 3, 2, 4)
                drawPx(canvas, paint, ox + 12, oy + 3, 2, 4)
            }
            SilhouetteType.DRAGON -> {
                paint.color = s
                drawPx(canvas, paint, ox + 4, oy + 2, 1, 2)
                drawPx(canvas, paint, ox + 11, oy + 2, 1, 2)
                if (stage >= 3) {
                    paint.color = a
                    drawPx(canvas, paint, ox + 1, oy + 6, 2, 4)
                    drawPx(canvas, paint, ox + 13, oy + 6, 2, 4)
                }
            }
            SilhouetteType.DINO -> {
                paint.color = a
                drawPx(canvas, paint, ox + 6, oy + 2, 4, 2)
                if (stage >= 3) {
                    drawPx(canvas, paint, ox + 13, oy + 9, 2, 3)
                }
            }
            SilhouetteType.RABBIT -> {
                paint.color = p
                drawPx(canvas, paint, ox + 4, oy + 1, 2, 3)
                drawPx(canvas, paint, ox + 10, oy + 1, 2, 3)
                paint.color = a
                drawPx(canvas, paint, ox + 5, oy + 2, 1, 2)
                drawPx(canvas, paint, ox + 10, oy + 2, 1, 2)
            }
            SilhouetteType.PANDA -> {
                paint.color = s
                drawPx(canvas, paint, ox + 3, oy + 2, 2, 2)
                drawPx(canvas, paint, ox + 11, oy + 2, 2, 2)
                drawPx(canvas, paint, ox + 4, oy + 6, 3, 3)
                drawPx(canvas, paint, ox + 9, oy + 6, 3, 3)
            }
            SilhouetteType.PENGUIN -> {
                paint.color = p
                drawPx(canvas, paint, ox + 1, oy + 7, 2, 4)
                drawPx(canvas, paint, ox + 13, oy + 7, 2, 4)
            }
            SilhouetteType.SHARK -> {
                paint.color = a
                drawPx(canvas, paint, ox + 7, oy + 1, 2, 3)
                drawPx(canvas, paint, ox + 13, oy + 8, 2, 3)
            }
            SilhouetteType.TURTLE -> {
                paint.color = a
                drawPx(canvas, paint, ox + 4, oy + 3, 8, 2)
            }
            SilhouetteType.SQUIRREL -> {
                paint.color = a
                drawPx(canvas, paint, ox + 13, oy + 5, 2, 7)
            }
            SilhouetteType.OWL -> {
                paint.color = a
                drawPx(canvas, paint, ox + 3, oy + 2, 2, 2)
                drawPx(canvas, paint, ox + 11, oy + 2, 2, 2)
            }
            SilhouetteType.FROG -> {
                paint.color = p
                drawPx(canvas, paint, ox + 4, oy + 2, 3, 2)
                drawPx(canvas, paint, ox + 9, oy + 2, 3, 2)
            }
            SilhouetteType.BEE -> {
                paint.color = s
                drawPx(canvas, paint, ox + 3, oy + 8, 10, 1)
                drawPx(canvas, paint, ox + 3, oy + 10, 10, 1)
                paint.color = WHITE
                drawPx(canvas, paint, ox + 1, oy + 5, 2, 3)
                drawPx(canvas, paint, ox + 13, oy + 5, 2, 3)
            }
            SilhouetteType.UNICORN -> {
                paint.color = GOLD
                drawPx(canvas, paint, ox + 7, oy + 0, 2, 4)
                paint.color = a
                drawPx(canvas, paint, ox + 12, oy + 4, 2, 5)
            }
            SilhouetteType.OCTOPUS -> {
                paint.color = p
                drawPx(canvas, paint, ox + 3, oy + 13, 2, 2)
                drawPx(canvas, paint, ox + 7, oy + 13, 2, 2)
                drawPx(canvas, paint, ox + 11, oy + 13, 2, 2)
            }
            SilhouetteType.SHEEP -> {
                paint.color = WHITE
                drawPx(canvas, paint, ox + 4, oy + 2, 8, 2)
                drawPx(canvas, paint, ox + 2, oy + 5, 1, 6)
                drawPx(canvas, paint, ox + 13, oy + 5, 1, 6)
            }
            SilhouetteType.HEDGEHOG -> {
                paint.color = a
                drawPx(canvas, paint, ox + 2, oy + 3, 2, 2)
                drawPx(canvas, paint, ox + 7, oy + 2, 2, 2)
                drawPx(canvas, paint, ox + 12, oy + 3, 2, 2)
            }
            SilhouetteType.SLIME -> {
                paint.color = WHITE
                drawPx(canvas, paint, ox + 5, oy + 5, 2, 1)
                if (stage >= 4) {
                    paint.color = GOLD
                    drawPx(canvas, paint, ox + 7, oy + 2, 2, 2)
                }
            }
        }
    }

    private fun drawPoop(canvas: Canvas, paint: Paint, x: Int, y: Int) {
        paint.color = OUTLINE
        drawPx(canvas, paint, x, y + 2, 5, 2)
        drawPx(canvas, paint, x + 1, y, 3, 2)
        paint.color = POOP_BROWN
        drawPx(canvas, paint, x + 1, y + 2, 3, 1)
        paint.color = POOP_LIGHT
        drawPx(canvas, paint, x + 2, y + 1, 1, 1)
    }

    private fun drawHeart(canvas: Canvas, paint: Paint, x: Int, y: Int) {
        paint.color = 0xFFFB7185.toInt()
        drawPx(canvas, paint, x, y, 2, 2)
        drawPx(canvas, paint, x + 3, y, 2, 2)
        drawPx(canvas, paint, x + 1, y + 1, 3, 2)
        drawPx(canvas, paint, x + 2, y + 3, 1, 1)
    }

    private fun drawSparkle(canvas: Canvas, paint: Paint, x: Int, y: Int) {
        paint.color = GOLD
        drawPx(canvas, paint, x + 1, y, 1, 3)
        drawPx(canvas, paint, x, y + 1, 3, 1)
    }

    private fun drawZzz(canvas: Canvas, paint: Paint, x: Int, y: Int) {
        drawPx(canvas, paint, x, y, 3, 1)
        drawPx(canvas, paint, x + 1, y + 1, 1, 1)
        drawPx(canvas, paint, x, y + 2, 3, 1)
        drawPx(canvas, paint, x + 5, y - 2, 3, 1)
        drawPx(canvas, paint, x + 6, y - 1, 1, 1)
        drawPx(canvas, paint, x + 5, y, 3, 1)
    }

    private fun drawCross(canvas: Canvas, paint: Paint, x: Int, y: Int) {
        drawPx(canvas, paint, x + 1, y, 1, 3)
        drawPx(canvas, paint, x, y + 1, 3, 1)
    }

    private fun drawFoodIcon(canvas: Canvas, paint: Paint, x: Int, y: Int) {
        paint.color = 0xFFEA580C.toInt()
        drawPx(canvas, paint, x, y, 4, 3)
        paint.color = WHITE
        drawPx(canvas, paint, x + 4, y + 1, 2, 1)
    }

    private fun drawBallIcon(canvas: Canvas, paint: Paint, x: Int, y: Int) {
        paint.color = 0xFFEC4899.toInt()
        drawPx(canvas, paint, x, y, 4, 4)
        paint.color = GOLD
        drawPx(canvas, paint, x + 1, y + 1, 2, 2)
    }

    private fun drawPx(canvas: Canvas, paint: Paint, x: Int, y: Int, w: Int, h: Int) {
        canvas.drawRect(x.toFloat(), y.toFloat(), (x + w).toFloat(), (y + h).toFloat(), paint)
    }
}
