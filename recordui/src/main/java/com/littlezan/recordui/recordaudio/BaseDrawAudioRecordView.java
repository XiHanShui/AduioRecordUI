package com.littlezan.recordui.recordaudio;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * ClassName: BaseDrawAudioRecordView
 * Description:
 *
 * @author 彭赞
 * @version 1.0
 * @since 2018-08-22  15:32
 */
public abstract class BaseDrawAudioRecordView extends BaseAudioRecordView {
    public BaseDrawAudioRecordView(Context context) {
        super(context);
    }

    public BaseDrawAudioRecordView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseDrawAudioRecordView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
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
                //刻度间距线
                canvas.drawLine(locationX, ruleHorizontalLineHeight - bigScaleStrokeLength, locationX, ruleHorizontalLineHeight, bigScalePaint);
                if (showRuleText) {
                    int index = i / intervalCount;
                    canvas.drawText(formatTime(index), locationX + bigScaleStrokeWidth + 5, ruleHorizontalLineHeight - bigScaleStrokeLength + ruleTextSize / 1.5f, ruleTextPaint);
                }
            } else {
                //小刻度间隔线
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
        int halfHeight = canvas.getHeight() / 2;
        SampleLineModel lastSampleLineModel = sampleLineList.get(sampleLineList.size() - 1);

        Integer stopFlagIndex = null;
        for (int i = 0; i < drawRectList.size(); i++) {
            SampleLineModel sampleLineModel = drawRectList.get(i);
            canvas.drawLine(sampleLineModel.startX, sampleLineModel.startY, sampleLineModel.stopX, sampleLineModel.stopY, linePaint);
            float invertedStopY = halfHeight + sampleLineModel.stopY - sampleLineModel.startY;
            canvas.drawLine(sampleLineModel.startX, halfHeight, sampleLineModel.stopX, invertedStopY, lineInvertedPaint);
            if (sampleLineModel.stopFlag && sampleLineModel != lastSampleLineModel) {
                stopFlagIndex = i;
            }
            if (stopFlagIndex != null && stopFlagIndex + 1 == i) {
                int lineTop = (halfHeight - (halfHeight - ruleHorizontalLineHeight - rectMarginTop));
                int lineBottom = canvas.getHeight() - lineTop;
                canvas.drawLine(sampleLineModel.startX, lineTop, sampleLineModel.stopX, lineBottom, lineDeletePaint);
            }
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
        float circleX = canvas.getWidth() / 2 + getScrollX();
        float topCircleY = ruleHorizontalLineHeight - middleCircleRadius;
        float bottomCircleY = canvas.getHeight() / 2 + (canvas.getHeight() / 2 - ruleHorizontalLineHeight) + middleCircleRadius;

        //上圆
        canvas.drawCircle(circleX, topCircleY, middleCircleRadius, middleVerticalLinePaint);
        //下圆
        canvas.drawCircle(circleX, bottomCircleY, middleCircleRadius, middleVerticalLinePaint);
        //垂直 直线
        canvas.drawLine(circleX, topCircleY, circleX, bottomCircleY, middleVerticalLinePaint);
    }
}