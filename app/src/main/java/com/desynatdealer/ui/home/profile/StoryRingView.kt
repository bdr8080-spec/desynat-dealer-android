package com.desynatdealer.ui.home.profile

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class StoryRingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs)
{

    var segmentCount = 0
    var gapAngle = 10f

    var outerStrokeWidth = 6f   // brown ring
    var innerStrokeWidth = 2f   // white gap

    private val outerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = Color.parseColor("#6E4527") // brown
        strokeCap = Paint.Cap.ROUND
    }

    private val innerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = Color.WHITE  // gap color
        strokeCap = Paint.Cap.ROUND
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (segmentCount <= 0) return

        val size = width.toFloat()

        val outerRect = RectF(
            outerStrokeWidth,
            outerStrokeWidth,
            size - outerStrokeWidth,
            size - outerStrokeWidth
        )

        val sweepAngle = (360f - (segmentCount * gapAngle)) / segmentCount

        outerPaint.strokeWidth = outerStrokeWidth

        var startAngle = -90f
        for (i in 0 until segmentCount) {
            canvas.drawArc(outerRect, startAngle, sweepAngle, false, outerPaint)
            startAngle += sweepAngle + gapAngle
        }

        val innerOffset = outerStrokeWidth + 2f

        val innerRect = RectF(
            innerOffset,
            innerOffset,
            size - innerOffset,
            size - innerOffset
        )

        innerPaint.strokeWidth = innerStrokeWidth

        startAngle = -90f
        for (i in 0 until segmentCount) {
            canvas.drawArc(innerRect, startAngle, sweepAngle, false, innerPaint)
            startAngle += sweepAngle + gapAngle
        }
    }
}