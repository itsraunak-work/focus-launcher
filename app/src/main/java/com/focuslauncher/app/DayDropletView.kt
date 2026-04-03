package com.focuslauncher.app

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import java.util.Calendar
import kotlin.math.abs

class DayDropletView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private val totalDays = 365
    private val dropsPerRow = 30

    private val dp = resources.displayMetrics.density

    private val pastPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFFEEEEEE.toInt(); style = Paint.Style.FILL }

    private val futurePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFF141414.toInt(); style = Paint.Style.FILL }

    private val todayPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFF888888.toInt(); style = Paint.Style.FILL }

    private val todayRingPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFF444444.toInt(); style = Paint.Style.STROKE; strokeWidth = 1f * dp }

    val dayOfYear: Int = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)

    // Callback when today is tapped
    var onTodayTapped: (() -> Unit)? = null

    private val radius = 5f * dp
    private val gap = 5.5f * dp
    private val cellSize = radius * 2f + gap

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP) {
            val totalRowWidth = dropsPerRow * cellSize - gap
            val startX = (width - totalRowWidth) / 2f + radius
            val todayIndex = dayOfYear - 1
            val row = todayIndex / dropsPerRow
            val col = todayIndex % dropsPerRow
            val cx = startX + col * cellSize
            val cy = radius + row * cellSize

            // Check if tap is within today's droplet
            if (abs(event.x - cx) < radius * 3 && abs(event.y - cy) < radius * 3) {
                onTodayTapped?.invoke()
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val totalRowWidth = dropsPerRow * cellSize - gap
        val startX = (width - totalRowWidth) / 2f + radius

        for (i in 0 until totalDays) {
            val row = i / dropsPerRow
            val col = i % dropsPerRow
            val cx = startX + col * cellSize
            val cy = radius + row * cellSize

            when {
                i == dayOfYear - 1 -> {
                    canvas.drawCircle(cx, cy, radius, todayPaint)
                    canvas.drawCircle(cx, cy, radius + 3f * dp, todayRingPaint)
                }
                i < dayOfYear -> canvas.drawCircle(cx, cy, radius, pastPaint)
                else -> canvas.drawCircle(cx, cy, radius * 0.7f, futurePaint)
            }
        }
    }
}