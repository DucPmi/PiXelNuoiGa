package com.pixelpet.tamagotchi.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.pixelpet.tamagotchi.MainActivity
import com.pixelpet.tamagotchi.R
import com.pixelpet.tamagotchi.data.PetRepository
import com.pixelpet.tamagotchi.pixel.PixelArtRenderer

class TamagotchiWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        appWidgetIds.forEach { id ->
            updateWidget(context, appWidgetManager, id)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        val repo = PetRepository(context)
        when (intent.action) {
            ACTION_FEED -> {
                repo.feed()
                updateAll(context)
            }
            ACTION_CLEAN -> {
                repo.clean()
                updateAll(context)
            }
            ACTION_PLAY -> {
                repo.play()
                updateAll(context)
            }
            ACTION_SLEEP -> {
                repo.toggleSleep()
                updateAll(context)
            }
            ACTION_HEAL -> {
                repo.heal()
                updateAll(context)
            }
            ACTION_TICK -> {
                repo.loadState()
                updateAll(context)
            }
        }
    }

    companion object {
        const val ACTION_FEED = "com.pixelpet.tamagotchi.ACTION_FEED"
        const val ACTION_CLEAN = "com.pixelpet.tamagotchi.ACTION_CLEAN"
        const val ACTION_PLAY = "com.pixelpet.tamagotchi.ACTION_PLAY"
        const val ACTION_SLEEP = "com.pixelpet.tamagotchi.ACTION_SLEEP"
        const val ACTION_HEAL = "com.pixelpet.tamagotchi.ACTION_HEAL"
        const val ACTION_TICK = "com.pixelpet.tamagotchi.ACTION_TICK"

        fun updateAll(context: Context) {
            val manager = AppWidgetManager.getInstance(context)
            val ids = manager.getAppWidgetIds(
                ComponentName(context, TamagotchiWidgetProvider::class.java)
            )
            ids.forEach { updateWidget(context, manager, it) }
        }

        private fun updateWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val state = PetRepository(context).loadState()
            val views = RemoteViews(context.packageName, R.layout.widget_tamagotchi)

            val animFrame = ((System.currentTimeMillis() / 1000L) % 2L).toInt()
            val sceneBitmap = PixelArtRenderer.renderSceneBitmap(
                state = state,
                widthPx = 360,
                heightPx = 180,
                animFrame = animFrame
            )

            views.setImageViewBitmap(R.id.iv_pixel_scene, sceneBitmap)
            views.setTextViewText(
                R.id.tv_pet_header,
                "${state.nickname} • Cấp ${state.stage}/5 (${state.stageName})"
            )
            val xpText = if (state.stage >= 5) "MAX" else "XP ${state.xp}/${state.xpForNextStage}"
            views.setTextViewText(R.id.tv_pet_xp, xpText)

            val statusSummary = if (state.isSick) {
                "BỊ ỐM! Đói ${state.hunger}% • Sạch ${state.cleanliness}% • Nhấn Thuốc"
            } else if (state.poopCount > 0) {
                "Có ${state.poopCount} bãi phân! Đói ${state.hunger}% • Vui ${state.happiness}%"
            } else {
                "Đói ${state.hunger}% • Vui ${state.happiness}% • Ngủ ${state.energy}% • Sạch ${state.cleanliness}%"
            }
            views.setTextViewText(R.id.tv_status_line, statusSummary)
            views.setTextViewText(
                R.id.btn_sleep,
                if (state.isSleeping) "☀️
                if (state.isSleeping) "☀️\nDậy" else "💤\nNgủ"

            views.setOnClickPendingIntent(R.id.btn_feed, broadcastIntent(context, ACTION_FEED, 101))
            views.setOnClickPendingIntent(R.id.btn_clean, broadcastIntent(context, ACTION_CLEAN, 102))
            views.setOnClickPendingIntent(R.id.btn_play, broadcastIntent(context, ACTION_PLAY, 103))
            views.setOnClickPendingIntent(R.id.btn_sleep, broadcastIntent(context, ACTION_SLEEP, 104))
            views.setOnClickPendingIntent(R.id.btn_heal, broadcastIntent(context, ACTION_HEAL, 105))
            views.setOnClickPendingIntent(R.id.iv_pixel_scene, broadcastIntent(context, ACTION_TICK, 106))

            val openIntent = Intent(context, MainActivity::class.java)
            val openPending = PendingIntent.getActivity(
                context,
                107,
                openIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.btn_open, openPending)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        private fun broadcastIntent(context: Context, action: String, reqCode: Int): PendingIntent {
            val intent = Intent(context, TamagotchiWidgetProvider::class.java).apply {
                this.action = action
            }
            return PendingIntent.getBroadcast(
                context,
                reqCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }
    }
}
