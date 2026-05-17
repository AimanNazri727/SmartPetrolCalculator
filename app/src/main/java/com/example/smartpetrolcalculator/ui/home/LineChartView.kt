package com.example.smartpetrolcalculator.ui.home

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

data class ChartDataSet(
    val label: String,
    val values: List<Float>,
    val color: Int
)

class LineChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val datasets = mutableListOf<ChartDataSet>()
    private var labels   = listOf<String>()

    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style     = Paint.Style.STROKE
        strokeWidth = 4f
        strokeJoin  = Paint.Join.ROUND
        strokeCap   = Paint.Cap.ROUND
    }

    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }

    private val dotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }

    private val dotBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style       = Paint.Style.STROKE
        strokeWidth = 2.5f
        color       = Color.WHITE
    }

    private val gridPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style       = Paint.Style.STROKE
        strokeWidth = 1f
        color       = Color.parseColor("#F0F0F0")
        pathEffect  = DashPathEffect(floatArrayOf(8f, 6f), 0f)
    }

    private val axisPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style       = Paint.Style.STROKE
        strokeWidth = 1.5f
        color       = Color.parseColor("#DDDDDD")
    }

    private val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize  = 20f
        color     = Color.parseColor("#888888")
        textAlign = Paint.Align.CENTER
    }

    private val yLabelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize  = 20f
        color     = Color.parseColor("#888888")
        textAlign = Paint.Align.RIGHT
    }

    private val valuePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize    = 18f
        textAlign   = Paint.Align.CENTER
        typeface    = Typeface.DEFAULT_BOLD
    }

    private val pLeft   = 115f
    private val pRight  = 20f
    private val pTop    = 30f
    private val pBottom = 50f

    fun setData(data: List<ChartDataSet>, xLabels: List<String>) {
        datasets.clear()
        datasets.addAll(data)
        labels = xLabels
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (datasets.isEmpty()) return

        val cw = width  - pLeft - pRight
        val ch = height - pTop  - pBottom

        val allVals = datasets.flatMap { it.values }.filter { it > 0f }
        if (allVals.isEmpty()) return

        val minVal = (allVals.minOrNull() ?: 0f) - 0.15f
        val maxVal = (allVals.maxOrNull() ?: 5f)  + 0.15f
        val range  = maxVal - minVal
        val n      = datasets.firstOrNull()?.values?.size ?: 0
        if (n < 2) return

        // Background subtle gradient feel
        val bgPaint = Paint().apply {
            color = Color.parseColor("#FAFAFA")
            style = Paint.Style.FILL
        }
        canvas.drawRect(pLeft, pTop, pLeft + cw, pTop + ch, bgPaint)

        // Grid lines
        val gridCount = 5
        for (i in 0..gridCount) {
            val y    = pTop + ch - (i.toFloat() / gridCount) * ch
            canvas.drawLine(pLeft, y, pLeft + cw, y, gridPaint)
            val yVal = minVal + (i.toFloat() / gridCount) * range
            canvas.drawText("RM%.2f".format(yVal), pLeft - 6f, y + 7f, yLabelPaint)
        }

        // X axis labels — show only every other label if crowded
        val step = if (labels.size > 6) 2 else 1
        labels.forEachIndexed { i, lbl ->
            if (i % step == 0 || i == labels.size - 1) {
                val x = pLeft + (i.toFloat() / (labels.size - 1)) * cw
                canvas.drawText(lbl, x, height.toFloat() - 6f, labelPaint)
            }
        }

        // Axes
        canvas.drawLine(pLeft, pTop, pLeft, pTop + ch, axisPaint)
        canvas.drawLine(pLeft, pTop + ch, pLeft + cw, pTop + ch, axisPaint)

        // Draw datasets
        datasets.forEachIndexed { dsIdx, dataset ->
            val pts = dataset.values
            if (pts.isEmpty()) return@forEachIndexed

            val coords = pts.mapIndexed { i, v ->
                val x = pLeft + (i.toFloat() / (pts.size - 1)) * cw
                val y = pTop  + ch - ((v - minVal) / range) * ch
                PointF(x, y)
            }

            // Fill with gradient alpha
            val fillPath = Path()
            fillPath.moveTo(coords.first().x, pTop + ch)
            coords.forEach { fillPath.lineTo(it.x, it.y) }
            fillPath.lineTo(coords.last().x, pTop + ch)
            fillPath.close()

            val shader = LinearGradient(
                0f, pTop, 0f, pTop + ch,
                Color.argb(80, Color.red(dataset.color), Color.green(dataset.color), Color.blue(dataset.color)),
                Color.argb(5,  Color.red(dataset.color), Color.green(dataset.color), Color.blue(dataset.color)),
                Shader.TileMode.CLAMP
            )
            fillPaint.shader = shader
            canvas.drawPath(fillPath, fillPaint)
            fillPaint.shader = null

            // Draw smooth line using cubic bezier
            val linePath = Path()
            linePath.moveTo(coords[0].x, coords[0].y)
            for (i in 1 until coords.size) {
                val prev = coords[i - 1]
                val curr = coords[i]
                val cpX  = (prev.x + curr.x) / 2f
                linePath.cubicTo(cpX, prev.y, cpX, curr.y, curr.x, curr.y)
            }
            linePaint.color = dataset.color
            canvas.drawPath(linePath, linePaint)

            // Dots with value label on last point
            dotPaint.color = dataset.color
            coords.forEachIndexed { i, pt ->
                val radius = if (i == coords.size - 1) 9f else 6f
                canvas.drawCircle(pt.x, pt.y, radius, dotPaint)
                canvas.drawCircle(pt.x, pt.y, radius, dotBorderPaint)

                // Show value on last dot
                if (i == coords.size - 1) {
                    valuePaint.color = dataset.color
                    canvas.drawText("RM%.2f".format(pts[i]), pt.x, pt.y - 14f, valuePaint)
                }
            }
        }
    }
}