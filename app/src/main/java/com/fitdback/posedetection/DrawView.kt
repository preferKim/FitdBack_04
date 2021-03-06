/*
 * Copyright 2018 Zihua Zeng (edvard_hua@live.com), Lang Feng (tearjeaker@hotmail.com)
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fitdback.posedetection

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.Style.FILL
import android.graphics.PointF
import android.os.Handler
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.fitdback.algorithm.FeedbackAlgorithm
import java.util.concurrent.CopyOnWriteArrayList

class DrawView : View {

    private var mRatioWidth = 0
    private var mRatioHeight = 0

    private val mDrawPoint = CopyOnWriteArrayList<PointF>()
    private var mWidth: Int = 0
    private var mHeight: Int = 0
    private var mRatioX: Float = 0.toFloat()
    private var mRatioY: Float = 0.toFloat()
    private var mImgWidth: Int = 0
    private var mImgHeight: Int = 0

    private val mColorArray = intArrayOf(
            resources.getColor(R.color.white, null),        // 0
            resources.getColor(R.color.white, null),       // 1
            resources.getColor(R.color.white, null),    // 2
            resources.getColor(R.color.white, null),    // 3
            resources.getColor(R.color.white, null),    // 4
            resources.getColor(R.color.white, null),    // 5
            resources.getColor(R.color.white, null),    // 6
            resources.getColor(R.color.white, null),    // 7
            resources.getColor(R.color.white, null),        // 8
            resources.getColor(R.color.white, null),       // 9
            resources.getColor(R.color.white, null),      // 10
            resources.getColor(R.color.white, null),       // 11
            resources.getColor(R.color.white, null),       // 12
            resources.getColor(R.color.white, null),       // 13
            resources.getColor(R.color.white, null)
    )

    private val circleRadius: Float by lazy {
        dip(4).toFloat()
    }

    private val mPaint: Paint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG).apply {
            style = FILL
            strokeWidth = dip(10).toFloat()
            textSize = sp(13).toFloat()
        }
    }

    constructor(context: Context) : super(context)

    constructor(
            context: Context,
            attrs: AttributeSet?
    ) : super(context, attrs)

    constructor(
            context: Context,
            attrs: AttributeSet?,
            defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr)

    fun setImgSize(
            width: Int,
            height: Int
    ) {
        mImgWidth = width
        mImgHeight = height
        requestLayout()
    }

    /**
     * Scale according to the device.
     * @param point 2*14
     */
    fun setDrawPoint(
            point: Array<FloatArray>,
            ratio: Float
    ) {
        mDrawPoint.clear()

        var tempX: Float
        var tempY: Float
        for (i in 0..13) {
            tempX = point[0][i] / ratio / mRatioX
            tempY = point[1][i] / ratio / mRatioY
            mDrawPoint.add(PointF(tempX, tempY))
        }
    }

    /**
     * Sets the aspect ratio for this view. The size of the view will be measured based on the ratio
     * calculated from the parameters. N11er, that is,
     * calling setAspectRatio(2, 3) and setAspectRatio(4, 6) make the same result.
     *
     * @param width  Relative horizontal size
     * @param height Relative vertical size
     */
    fun setAspectRatio(
            width: Int,
            height: Int
    ) {
        if (width < 0 || height < 0) {
            throw IllegalArgumentException("Size cannot be negative.")
        }
        mRatioWidth = width
        mRatioHeight = height
        requestLayout()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (mDrawPoint.isEmpty()) return
        var prePointF: PointF? = null
        mPaint.color = 0x802AC1BC.toInt() //  ???????????? color
        val p1 = mDrawPoint[1]


        // TODO: 143??? ??? ?????? Fix : ????????? ?????? ??? ????????? ?????????

        for ((index, pointF) in mDrawPoint.withIndex()) {

            // ?????? ??? ??? ????????? ?????? ??????

                if (index == 1) continue
                when (index) {
                    //0-1
                    0 -> {
                        canvas.drawLine(pointF.x, pointF.y, p1.x, p1.y, mPaint)
                        Log.d("??????", mDrawPoint[0].y.toString())
                        Log.d("???", mDrawPoint[13].y.toString())
                    }
                    // 1-2, 1-5, 1-8, 1-11
                    2, 5, 8, 11 -> {
                        mPaint.color = 0x802AC1BC.toInt()
                        canvas.drawLine(p1.x, p1.y, pointF.x, pointF.y, mPaint)
                    }
                    /*else -> {
                    if (prePointF != null) {
                        mPaint.color = 0xfffaff0d.toInt() // skeleton ?????? ??????
                        canvas.drawLine(prePointF.x, prePointF.y, pointF.x, pointF.y, mPaint)
                    }
                }*/
                    9, 10 -> {
                        if (prePointF != null) {
                            mPaint.color = 0x802AC1BC.toInt() // skeleton ?????? ??????
                            canvas.drawLine(
                                    prePointF.x,
                                    prePointF.y,
                                    pointF.x,
                                    pointF.y,
                                    mPaint
                            )
                        }
                    }
                    else -> {
                        if (prePointF != null) {
                            mPaint.color = 0x802AC1BC.toInt() // skeleton ?????? ??????
                            canvas.drawLine(
                                    prePointF.x,
                                    prePointF.y,
                                    pointF.x,
                                    pointF.y,
                                    mPaint
                            )
                        }
                    }
                }
                prePointF = pointF



        }

        // ??? ?????????

        try {
            for ((index, pointF) in mDrawPoint.withIndex()) {
                if (mDrawPoint[13].y > mDrawPoint[2].y && mDrawPoint[13].y > mDrawPoint[5].y &&

                        mDrawPoint[10].y > mDrawPoint[5].y && mDrawPoint[10].y > mDrawPoint[2].y
                ) {
                    mPaint.color = mColorArray[index]
                    canvas.drawCircle(pointF.x, pointF.y, circleRadius, mPaint)
                }
            }
        }
        catch(e: ArrayIndexOutOfBoundsException){
        }

        /*if(FeedbackAlgorithm.isStart){
            FeedbackAlgorithm.isStart=false
            FeedbackAlgorithm.del_start = System.currentTimeMillis()
            FeedbackAlgorithm.del_time = 0
        }
        if(FeedbackAlgorithm.del_time<=5000){
            FeedbackAlgorithm.del_time = System.currentTimeMillis() - FeedbackAlgorithm.del_start
        }*/

        // FeedBack ????????????
        if (FeedbackAlgorithm.isPlaying&&FeedbackAlgorithm.delay_tf) {
            when (FeedbackAlgorithm.exr_mode) {
                "squat" -> FeedbackAlgorithm.squat(context, mDrawPoint)

                "plank" -> FeedbackAlgorithm.plank(context, mDrawPoint)

                "free_exr" -> FeedbackAlgorithm.free_exr(context, mDrawPoint)

                "sidelr" -> FeedbackAlgorithm.sidelr(context, mDrawPoint)
            }
        }
    }

    override fun onMeasure(
            widthMeasureSpec: Int,
            heightMeasureSpec: Int
    ) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val width = View.MeasureSpec.getSize(widthMeasureSpec)
        val height = View.MeasureSpec.getSize(heightMeasureSpec)
        if (0 == mRatioWidth || 0 == mRatioHeight) {
            setMeasuredDimension(width, height)
        } else {
            if (width < height * mRatioWidth / mRatioHeight) {
                mWidth = width
                mHeight = width * mRatioHeight / mRatioWidth
            } else {
                mWidth = height * mRatioWidth / mRatioHeight
                mHeight = height
            }
        }

        setMeasuredDimension(mWidth, mHeight)

        mRatioX = mImgWidth.toFloat() / mWidth
        mRatioY = mImgHeight.toFloat() / mHeight
    }
}
