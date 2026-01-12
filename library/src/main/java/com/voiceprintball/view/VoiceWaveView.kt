package com.voiceprintball.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt
import kotlin.math.sin

/**
 * 声纹小球动画 View
 * 
 * 支持在 XML 中配置属性，也支持代码动态修改
 * 
 * XML 使用示例：
 * ```xml
 * <com.voiceprintball.view.VoiceWaveView
 *     android:layout_width="match_parent"
 *     android:layout_height="120dp"
 *     app:voicewave_ballColor="#FFFFFF"
 *     app:voicewave_ballCount="3"
 *     app:voicewave_animationSpeed="1.0"
 *     app:voicewave_amplitudeSensitivity="1.25" />
 * ```
 * 
 * 代码使用示例：
 * ```kotlin
 * val voiceWaveView = findViewById<VoiceWaveView>(R.id.voice_wave)
 * voiceWaveView.setBallColor(Color.WHITE)
 * voiceWaveView.pushAmplitude01(0.5f) // 输入 0-1 的振幅值
 * ```
 * 
 * @author VoiceprintBall Team
 */
class VoiceWaveView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    @ColorInt
    private var ballColor: Int = 0xFFFFFFFF.toInt()

    private var ballCount: Int = 3
    private var ballRadius: Float = 6.8f // dp
    private var ballGap: Float = 20f // dp
    private var maxJumpHeight: Float = 14f // dp
    private var animationSpeed: Float = 0.06f
    private var amplitudeSensitivity: Float = 1.25f
    private var idleAmplitude: Float = 0.16f
    private var smoothFactor: Float = 0.85f

    private var animator: ValueAnimator? = null

    private var amp01: Float = 0f
    private var t: Float = 0f
    private var smoothAmp: Float = 0f

    init {
        // 解析 XML 属性
        attrs?.let { parseAttributes(it, defStyleAttr) }
    }

    private fun parseAttributes(attrs: AttributeSet, defStyleAttr: Int) {
        val typedArray = context.obtainStyledAttributes(
            attrs,
            com.voiceprintball.R.styleable.VoiceWaveView,
            defStyleAttr,
            0
        )

        try {
            ballColor = typedArray.getColor(
                com.voiceprintball.R.styleable.VoiceWaveView_voicewave_ballColor,
                0xFFFFFFFF.toInt()
            )

            ballCount = typedArray.getInt(
                com.voiceprintball.R.styleable.VoiceWaveView_voicewave_ballCount,
                3
            ).coerceIn(1, 10) // 限制在 1-10 之间

            ballRadius = typedArray.getDimension(
                com.voiceprintball.R.styleable.VoiceWaveView_voicewave_ballRadius,
                dp(6.8f)
            ) / resources.displayMetrics.density

            ballGap = typedArray.getDimension(
                com.voiceprintball.R.styleable.VoiceWaveView_voicewave_ballGap,
                dp(20f)
            ) / resources.displayMetrics.density

            maxJumpHeight = typedArray.getDimension(
                com.voiceprintball.R.styleable.VoiceWaveView_voicewave_maxJumpHeight,
                dp(14f)
            ) / resources.displayMetrics.density

            animationSpeed = typedArray.getFloat(
                com.voiceprintball.R.styleable.VoiceWaveView_voicewave_animationSpeed,
                1.0f
            ) * 0.06f // 基础速度因子

            amplitudeSensitivity = typedArray.getFloat(
                com.voiceprintball.R.styleable.VoiceWaveView_voicewave_amplitudeSensitivity,
                1.25f
            )

            idleAmplitude = typedArray.getFloat(
                com.voiceprintball.R.styleable.VoiceWaveView_voicewave_idleAmplitude,
                0.16f
            ).coerceIn(0f, 1f)

            smoothFactor = typedArray.getFloat(
                com.voiceprintball.R.styleable.VoiceWaveView_voicewave_smoothFactor,
                0.85f
            ).coerceIn(0f, 1f)
        } finally {
            typedArray.recycle()
        }
    }

    /**
     * 设置小球颜色
     * @param color 颜色值
     */
    fun setBallColor(@ColorInt color: Int) {
        ballColor = color
        invalidate()
    }

    /**
     * 获取小球颜色
     */
    @ColorInt
    fun getBallColor(): Int = ballColor

    /**
     * 设置小球数量
     * @param count 数量，范围 1-10
     */
    fun setBallCount(count: Int) {
        ballCount = count.coerceIn(1, 10)
        invalidate()
    }

    /**
     * 获取小球数量
     */
    fun getBallCount(): Int = ballCount

    /**
     * 设置动画速度
     * @param speed 速度因子，默认 1.0
     */
    fun setAnimationSpeed(speed: Float) {
        animationSpeed = speed * 0.06f
    }

    /**
     * 设置声纹灵敏度
     * @param sensitivity 灵敏度因子，默认 1.25
     */
    fun setAmplitudeSensitivity(sensitivity: Float) {
        amplitudeSensitivity = sensitivity
    }

    /**
     * 输入振幅值，范围 0..1
     * 0 表示待机状态（轻微起伏）
     * 1 表示最大振幅（跳动幅度最大）
     * 
     * @param value 振幅值，范围 0..1
     */
    fun pushAmplitude01(value: Float) {
        amp01 = value.coerceIn(0f, 1f)
    }

    /**
     * 获取当前振幅值
     */
    fun getAmplitude01(): Float = amp01

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        startAnimation()
    }

    override fun onDetachedFromWindow() {
        stopAnimation()
        super.onDetachedFromWindow()
    }

    private fun startAnimation() {
        if (animator != null) return
        
        // 创建无限循环的 ValueAnimator
        animator = ValueAnimator.ofFloat(0f, 1f).apply {
            repeatCount = ValueAnimator.INFINITE
            duration = Long.MAX_VALUE // 无限时长
            
            addUpdateListener { animation ->
                // 更新动画时间
                t += animationSpeed
                
                // 平滑插值：避免振幅突变
                smoothAmp = smoothAmp * smoothFactor + amp01 * (1f - smoothFactor)
                
                // 触发重绘
                invalidate()
            }
            
            start()
        }
    }

    private fun stopAnimation() {
        animator?.cancel()
        animator = null
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val w = width.toFloat()
        val h = height.toFloat()
        if (w <= 0f || h <= 0f) return

        val cy = h / 2f
        val centerX = w / 2f
        val gap = dp(ballGap)
        val baseR = dp(ballRadius)
        val maxJump = dp(maxJumpHeight)

        // idle：轻微起伏
        val idle = idleAmplitude + 0.06f * sin(t)

        // speaking：输入振幅驱动
        val speak = (smoothAmp * amplitudeSensitivity).coerceIn(0f, 1f)

        paint.color = ballColor

        // 绘制多个小球
        val startOffset = -(ballCount - 1) * 0.5f * 0.9f
        for (i in 0 until ballCount) {
            val offset = startOffset + i * 0.9f
            val p = idle + speak * wave01(t + offset)
            val x = centerX + (i - (ballCount - 1) * 0.5f) * gap
            val y = cy - bezierArcY(p.coerceIn(0f, 1f)) * maxJump
            val r = baseR * (1f + 0.18f * p)

            canvas.drawCircle(x, y, r, paint)
        }
    }

    private fun wave01(x: Float): Float = ((sin(x) + 1f) * 0.5f).coerceIn(0f, 1f)

    private fun bezierArcY(t: Float): Float {
        val p0 = 0f
        val p1 = 1f
        val p2 = 0f
        val u = 1f - t
        return u * u * p0 + 2f * u * t * p1 + t * t * p2
    }

    private fun dp(v: Float): Float = v * resources.displayMetrics.density
}

