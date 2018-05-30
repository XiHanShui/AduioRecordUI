package com.littlezan.recordui.recordaudio;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * ClassName: AudioRecord
 * Description:
 *
 * @author 彭赞
 * @version 1.0
 * @since 2017-12-07  20:00
 */
public class AudioRecordView extends BaseAudioRecordView {

    private static final String TAG = "AudioRecordView";

    Paint ruleHorizontalLinePaint = new Paint();
    Paint smallScalePaint = new Paint();
    Paint bigScalePaint = new Paint();
    TextPaint ruleTextPaint = new TextPaint();
    Paint middleHorizontalLinePaint = new Paint();
    Paint middleVerticalLinePaint = new Paint();
    Paint linePaint = new Paint();
    Paint lineInvertedPaint = new Paint();
    TextPaint bottomTextPaint = new TextPaint();
    Paint bottomRectPaint = new Paint();


    /**
     * 提前刻画量
     */
    protected int mDrawOffset;


    public AudioRecordView(Context context) {
        super(context);
        init();
    }

    public AudioRecordView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AudioRecordView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {

        ruleHorizontalLinePaint.setAntiAlias(true);
        ruleHorizontalLinePaint.setStrokeWidth(ruleHorizontalLineStrokeWidth);
        ruleHorizontalLinePaint.setColor(ruleHorizontalLineColor);


        smallScalePaint.setStrokeWidth(smallScaleStrokeWidth);
        smallScalePaint.setColor(ruleVerticalLineColor);
        smallScalePaint.setStrokeCap(Paint.Cap.ROUND);

        bigScalePaint.setColor(ruleVerticalLineColor);
        bigScalePaint.setStrokeWidth(bigScaleStrokeWidth);
        bigScalePaint.setStrokeCap(Paint.Cap.ROUND);

        ruleTextPaint.setAntiAlias(true);
        ruleTextPaint.setColor(ruleTextColor);
        ruleTextPaint.setTextSize(ruleTextSize);
        ruleTextPaint.setTextAlign(Paint.Align.LEFT);


        middleHorizontalLinePaint.setAntiAlias(true);
        middleHorizontalLinePaint.setStrokeWidth(middleHorizontalLineStrokeWidth);
        middleHorizontalLinePaint.setColor(middleHorizontalLineColor);

        middleVerticalLinePaint.setAntiAlias(true);
        middleVerticalLinePaint.setStrokeWidth(middleVerticalLineStrokeWidth);
        middleVerticalLinePaint.setColor(middleVerticalLineColor);

        linePaint.setAntiAlias(true);
        linePaint.setStrokeWidth(lineWidth);
        linePaint.setStrokeCap(Paint.Cap.ROUND);
        linePaint.setColor(rectColor);

        lineInvertedPaint.setAntiAlias(true);
        lineInvertedPaint.setStrokeWidth(lineWidth);
        lineInvertedPaint.setStrokeCap(Paint.Cap.ROUND);
        lineInvertedPaint.setColor(rectInvertColor);

        bottomTextPaint.setAntiAlias(true);
        bottomTextPaint.setColor(bottomTextColor);
        bottomTextPaint.setTextSize(bottomTextSize);
        bottomTextPaint.setTextAlign(Paint.Align.CENTER);

        bottomRectPaint.setAntiAlias(true);
        bottomRectPaint.setColor(bottomRectColor);


        mDrawOffset = scaleIntervalLength;
    }

    @Override
    protected void makeSampleLine(float percent) {
        if (lineLocationX >= maxLength) {
            //超出采样时间
            stopRecord();
            return;
        }
        SampleLineModel sampleLineModel = new SampleLineModel();
        int rectBottom = getMeasuredHeight() / 2;
        int lineTop = (int) (rectBottom - (rectBottom - ruleHorizontalLineHeight - rectMarginTop) * percent);
        sampleLineModel.startX = lineLocationX + lineWidth / 2;
        sampleLineModel.stopX = sampleLineModel.startX;
        sampleLineModel.startY = lineTop;
        sampleLineModel.stopY = rectBottom;
        lineLocationX = lineLocationX + lineWidth + rectGap;
        sampleLineList.add(sampleLineModel);
    }

    @Override
    protected int getSampleCount() {
        return sampleLineList.size();
    }


    @Override
    protected void setCanScrollX() {
        int widthMiddle = getMeasuredWidth() / 2;
        maxScrollX = lineLocationX < widthMiddle ? rectGap : lineLocationX - widthMiddle;
        minScrollX = lineLocationX < widthMiddle ? -lineLocationX : -widthMiddle - rectGap;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (showRule) {
            drawScale(canvas);
        }
        drawLine(canvas);
        drawCenterVerticalLine(canvas);
    }


    private void drawScale(Canvas canvas) {
        int firstPoint = (getScrollX() - mDrawOffset) / scaleIntervalLength;
        int lastPoint = (getScrollX() + canvas.getWidth() + mDrawOffset) / (scaleIntervalLength);
        for (int i = firstPoint; i < lastPoint; i++) {
            float locationX = i * scaleIntervalLength;
            if (i % intervalCount == 0) {
                canvas.drawLine(locationX, ruleHorizontalLineHeight - bigScaleStrokeLength, locationX, ruleHorizontalLineHeight, bigScalePaint);
                if (showRuleText) {
                    int index = i / intervalCount;
                    canvas.drawText(formatTime(index), locationX + bigScaleStrokeWidth + 5, ruleHorizontalLineHeight - bigScaleStrokeLength + ruleTextSize / 1.5f, ruleTextPaint);
                }
            } else {
                canvas.drawLine(locationX, ruleHorizontalLineHeight - smallScaleStrokeLength, locationX, ruleHorizontalLineHeight, smallScalePaint);
            }
        }
        //画轮廓线
        canvas.drawLine(getScrollX(), ruleHorizontalLineHeight, getScrollX() + canvas.getWidth(), ruleHorizontalLineHeight, ruleHorizontalLinePaint);
    }

    private String formatTime(int index) {
        String temp = "";
        if (index >= 0 && index <= maxLength / intervalCount) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("mm:ss", Locale.getDefault());
            Date date = new Date();
            date.setTime(TimeUnit.SECONDS.toMillis(index));
            temp = dateFormat.format(date);
        }
        return temp;
    }

    private void drawLine(Canvas canvas) {
        int middleLineY = canvas.getHeight() / 2;
        canvas.drawLine(getScrollX(), middleLineY, getScrollX() + canvas.getWidth(), middleLineY, middleHorizontalLinePaint);

        //从数据源中找出需要绘制的矩形
        List<SampleLineModel> drawRectList = getDrawSampleLineList(canvas);
        if (drawRectList == null || drawRectList.size() == 0) {
            return;
        }
        //绘制采样点
        for (SampleLineModel sampleLineModel : drawRectList) {
            canvas.drawLine(sampleLineModel.startX, sampleLineModel.startY, sampleLineModel.stopX, sampleLineModel.stopY, linePaint);
            int invertedStartY = canvas.getHeight() / 2;
            float invertedStopY = invertedStartY + sampleLineModel.stopY - sampleLineModel.startY;
            canvas.drawLine(sampleLineModel.startX, invertedStartY, sampleLineModel.stopX, invertedStopY, lineInvertedPaint);
        }
    }

    private List<SampleLineModel> getDrawSampleLineList(Canvas canvas) {
        if (sampleLineList.size() == 0) {
            return null;
        }
        List<SampleLineModel> resultList = new ArrayList<>();

        int rectWidthWithGap = lineWidth + rectGap;
        int recentlyRectIndex = getScrollX() / rectWidthWithGap;
        if (recentlyRectIndex < 0) {
            recentlyRectIndex = 0;
        } else if (recentlyRectIndex >= sampleLineList.size()) {
            recentlyRectIndex = sampleLineList.size() - 1;
        }

        int mixWidth = getScrollX() - rectWidthWithGap;
        int maxWidth = isRecording ? getScrollX() + canvas.getWidth() / 2 + rectWidthWithGap : getScrollX() + canvas.getWidth() + rectWidthWithGap;
        for (int i = recentlyRectIndex; i < sampleLineList.size(); i++) {
            SampleLineModel next = sampleLineList.get(i);
            if (next.startX >= mixWidth && next.startX + lineWidth / 2 <= maxWidth) {
                resultList.add(next);
            }
            if (next.startX > maxWidth) {
                break;
            }
        }

        return resultList;
    }


    private void drawCenterVerticalLine(Canvas canvas) {

        float circleX = translateVerticalLineX;
        int canvasMiddle = canvas.getWidth() / 2;
        if (isStartVerticalLineTranslate || circleX < canvasMiddle) {
            circleX = circleX + getScrollX();
        } else if (isStartRecordTranslateCanvas || circleX >= getScrollX() + canvasMiddle - rectGap) {
            circleX = getScrollX() + canvasMiddle + rectGap;
        }
        centerLineX = circleX;
        float topCircleY = ruleHorizontalLineHeight - middleCircleRadius;
        float bottomCircleY = canvas.getHeight() / 2 + (canvas.getHeight() / 2 - ruleHorizontalLineHeight) + middleCircleRadius;
        //底部颜色
        canvas.drawRect(getScrollX(), bottomCircleY - middleCircleRadius, getScrollX() + canvas.getWidth(), canvas.getHeight(), bottomRectPaint);
        //上圆
        canvas.drawCircle(circleX, topCircleY, middleCircleRadius, middleVerticalLinePaint);
        //下圆
        canvas.drawCircle(circleX, bottomCircleY, middleCircleRadius, middleVerticalLinePaint);
        //垂直 直线
        Log.d(TAG, "drawCenterVerticalLine: lll circleX = " + circleX + ", getScrollX() = " + getScrollX());
        canvas.drawLine(circleX, topCircleY, circleX, bottomCircleY, middleVerticalLinePaint);

        //底部文字
        SimpleDateFormat dateFormat = new SimpleDateFormat("mm:ss", Locale.getDefault());
        Date date = new Date();
        int length = intervalCount * scaleIntervalLength;
        date.setTime((long) (circleX * 1000L / length));

        String time = dateFormat.format(date);

        int decimal = (int) (circleX * 10 / length % 10);
        String text;
        if (getScrollX() == 0) {
            text = time + "/" + recordTimeInMinutes;
        } else {
            text = time + "." + decimal + "/" + recordTimeInMinutes;
        }
        canvas.drawText(text, getScrollX() + canvasMiddle, bottomCircleY + bottomTextSize + 20, bottomTextPaint);

        if (recordCallBack != null) {
            if (centerLineX == 0) {
                centerTimeMillis = 0;
            } else {
                centerTimeMillis = (long) (centerLineX * 1000L / (recordSamplingFrequency * (lineWidth + rectGap)));
            }
            if (lineLocationX > 0) {
                if (centerLineX >= lineLocationX) {
                    if (isPlayingRecord()) {
                        recordCallBack.onFinishPlayingRecord();
                    }
                } else {
                    recordCallBack.onCenterLineTime(centerTimeMillis);
                }
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        reset();
    }


    public void reset() {
        stopRecord();
        stopPlayRecord();
        centerLineX = 0;
        currentRecordTime = 0;
        mLastX = 0;
        sampleLineList.clear();
        lineLocationX = 0;
        minScrollX = 0;
        maxScrollX = 0;
        translateX = 0;
        translateVerticalLineX = middleCircleRadius/2;
        scrollTo(minScrollX, 0);
        invalidate();
        if (recordCallBack != null) {
            recordCallBack.onRecordCurrent(currentRecordTime, currentRecordTime);
        }
    }
}
