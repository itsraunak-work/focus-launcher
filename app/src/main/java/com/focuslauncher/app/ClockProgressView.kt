package com.focuslauncher.app

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.math.*

class ClockProgressView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private var totalSeconds = 30
    private var remainingSeconds = 30
    private val dp = resources.displayMetrics.density

    private val bgRingPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFF0D0D0D.toInt()
        style = Paint.Style.STROKE
        strokeWidth = 7f * dp
    }

    private val arcPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFFFFFFFF.toInt()
        style = Paint.Style.STROKE
        strokeWidth = 7f * dp
        strokeCap = Paint.Cap.ROUND
    }

    private val minorTickPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFF1A1A1A.toInt()
        style = Paint.Style.STROKE
        strokeWidth = 1f * dp
        strokeCap = Paint.Cap.ROUND
    }

    private val majorTickPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFF2E2E2E.toInt()
        style = Paint.Style.STROKE
        strokeWidth = 2f * dp
        strokeCap = Paint.Cap.ROUND
    }

    private val numberPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFFFFFFFF.toInt()
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create("sans-serif-thin", Typeface.NORMAL)
    }

    private val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFF2E2E2E.toInt()
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create("sans-serif-light", Typeface.NORMAL)
    }

    private val oval = RectF()

    fun setProgress(remaining: Int, total: Int) {
        remainingSeconds = remaining
        totalSeconds = total
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val size = min(width, height).toFloat()
        val cx = size / 2f
        val cy = size / 2f
        val ringRadius = size / 2f - 28f * dp

        numberPaint.textSize = size * 0.30f
        labelPaint.textSize = size * 0.08f

        oval.set(cx - ringRadius, cy - ringRadius, cx + ringRadius, cy + ringRadius)

        // 60 tick marks around the edge
        for (i in 0 until 60) {
            val angleRad = Math.toRadians(i * 6.0 - 90)
            val isMajor = i % 5 == 0
            val outerR = ringRadius + 11f * dp
            val innerR = outerR - if (isMajor) 14f * dp else 7f * dp
            canvas.drawLine(
                (cx + outerR * cos(angleRad)).toFloat(),
                (cy + outerR * sin(angleRad)).toFloat(),
                (cx + innerR * cos(angleRad)).toFloat(),
                (cy + innerR * sin(angleRad)).toFloat(),
                if (isMajor) majorTickPaint else minorTickPaint
            )
        }

        // Background ring
        canvas.drawArc(oval, -90f, 360f, false, bgRingPaint)

        // Progress arc depleting clockwise
        val sweep = if (totalSeconds > 0)
            (remainingSeconds.toFloat() / totalSeconds.toFloat()) * 360f else 0f
        canvas.drawArc(oval, -90f, sweep, false, arcPaint)

        // Center seconds number
        val textY = cy - (numberPaint.descent() + numberPaint.ascent()) / 2f
        canvas.drawText(remainingSeconds.toString(), cx, textY, numberPaint)

        // "sec" label below number
        canvas.drawText("sec", cx, textY + numberPaint.textSize * 0.65f, labelPaint)
    }
}