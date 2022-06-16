package com.example.composeinteropinvalidateissue

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class DrawAndInvalidateViewImpl @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {


    private var throbbing = true

    private val outerCirclePaint: Paint =
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
        }

    private val middleCircleMaskPaint =
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        }

    private val innerCirclePaint =
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
        }

    private val scale = 1f

    private val outerCircleDiameter = 50 * scale
    private val middleCircleDiameter = 40 * scale
    private val innerCircleDiameter = 30 * scale

    private var innerCircleDynamicDiameter = innerCircleDiameter

    private val outerCircleRadius = outerCircleDiameter / 2
    private val middleCircleRadius = middleCircleDiameter / 2
    private val innerCircleRadius: Float
        get() = innerCircleDynamicDiameter / 2

    /**
     * Updated in onLayout incase of resize/move
     * Only need the width because the onMeasured sets the same height/width and we are drawing circles
     */
    private var center: Float = 0f

    private val alphaAnimator =
        ValueAnimator.ofFloat(51f, 255f).apply {
            duration = 2000
            repeatMode = ValueAnimator.REVERSE
            repeatCount = ValueAnimator.INFINITE
        }

    private val pulseAnimator =
        ValueAnimator.ofFloat(0.9f, 1.0f).apply {
            duration = 2000
            repeatMode = ValueAnimator.REVERSE
            repeatCount = ValueAnimator.INFINITE
        }

    init {
        if (throbbing) startAnimationIfVisible()
    }

    private fun startAnimationIfVisible() {
        when (visibility) {
            VISIBLE -> if (throbbing) startAnimation()
        }
    }

    private fun startAnimation() {
        if (!alphaAnimator.isRunning) startPulsingAnimation(alphaAnimator)
        if (!pulseAnimator.isRunning) startAlphaAnimation(pulseAnimator)
    }

    private fun stopAnimation() {
        alphaAnimator.finish()
        pulseAnimator.finish()
    }

    override fun onDetachedFromWindow() {
        stopAnimation()
        super.onDetachedFromWindow()
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        when (visibility) {
            VISIBLE -> if (throbbing) startAnimation()
            GONE, INVISIBLE -> stopAnimation()
        }
        super.onVisibilityChanged(changedView, visibility)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        /**
         * This will short circuit the height/width attrs as not really required
         */
        setMeasuredDimension(outerCircleDiameter.toInt(), outerCircleDiameter.toInt())
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        center = (width / 2).toFloat()
    }

    override fun onDraw(canvas: Canvas) {
        with(canvas) {
            super.onDraw(canvas)
            drawCircle(center, center, outerCircleRadius, outerCirclePaint)
            drawCircle(center, center, middleCircleRadius, middleCircleMaskPaint)
            drawCircle(center, center, innerCircleRadius, innerCirclePaint)
        }

        if (throbbing) invalidate()
    }

    //Cancel does not remote update listeners, this ext function will.
    private fun ValueAnimator.finish() {
        cancel()
        removeAllUpdateListeners()
    }

    private fun startAlphaAnimation(valueAnimator: ValueAnimator) {
        valueAnimator.start()
        valueAnimator.addUpdateListener {
            innerCircleDynamicDiameter = innerCircleDiameter * (it.animatedValue as Float)
        }
    }

    private fun startPulsingAnimation(valueAnimator: ValueAnimator) {
        valueAnimator.start()
        valueAnimator.addUpdateListener {
            innerCirclePaint.alpha = (it.animatedValue as Float).toInt()
        }
    }
}
