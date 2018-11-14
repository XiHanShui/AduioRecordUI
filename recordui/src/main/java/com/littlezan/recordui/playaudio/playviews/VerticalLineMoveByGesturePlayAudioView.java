package com.littlezan.recordui.playaudio.playviews;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.animation.LinearInterpolator;

import com.littlezan.recordui.playaudio.BaseDrawPlayAudioView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * ClassName: VerticalLineMoveByGesturePlayAudioView
 * Description:
 *
 * @author 彭赞
 * @version 1.0
 * @since 2018-11-03  10:36
 */
public class VerticalLineMoveByGesturePlayAudioView extends BaseDrawPlayAudioView {

    private int verticalLineTouchHotSpot;
    private boolean isTouchViewMode = false;


    public VerticalLineMoveByGesturePlayAudioView(Context context) {
        super(context);
    }

    public VerticalLineMoveByGesturePlayAudioView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public VerticalLineMoveByGesturePlayAudioView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    {
        verticalLineTouchHotSpot = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, Resources.getSystem().getDisplayMetrics());
    }


    float touchActionX;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        isTouching = true;
        float currentX = event.getX();
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                stopPlay();
                if (playAudioCallBack != null) {
                    playAudioCallBack.onPausePlay();
                }
                float resolveX = currentX + getScrollX();
                isTouchViewMode = resolveX < centerLineX - verticalLineTouchHotSpot || resolveX > centerLineX + verticalLineTouchHotSpot;
                if (isTouchViewMode) {
                    super.onTouchEvent(event);
                } else {
                    touchActionX = currentX;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (isTouchViewMode) {
                    super.onTouchEvent(event);
                } else {
                    float moveX = currentX - touchActionX;
                    touchActionX = currentX;
                    centerLineX += moveX;
                    if (centerLineX >= lastSampleXWithRectGap) {
                        centerLineX = lastSampleXWithRectGap;
                    }
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                startPlay(getCurrentPlayingTimeInMillis());
                if (playAudioCallBack != null) {
                    playAudioCallBack.onResumePlay();
                }
                if (isTouchViewMode) {
                    super.onTouchEvent(event);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                if (isTouchViewMode) {
                    super.onTouchEvent(event);
                }
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 用于在画布移动过程中固定垂直线
     */
    int lastScrollX = 0;

    @Override
    public void drawVerticalTargetLine(Canvas canvas) {
        int currentScrollX = getScrollX();
        if (isTouching && isTouchViewMode) {
            //手指滑动
            int offset = currentScrollX - lastScrollX;
            centerLineX = centerLineX + offset;
            lastScrollX = currentScrollX;
        } else {
            //自动滚动
            lastScrollX = currentScrollX;
            centerLineX = isAutoScroll ? getScrollX() + getWidth() : centerLineX;
        }
        float startY = circleMarginTop;
        canvas.drawCircle(centerLineX, startY, circleRadius, centerLinePaint);
        canvas.drawLine(centerLineX, startY, centerLineX, getMeasuredHeight(), centerLinePaint);
        long currentPlayingTimeInMillis = getCurrentPlayingTimeInMillis();
        canvas.drawText(formatTime(currentPlayingTimeInMillis), centerLineX, startY - textPaint.getFontSpacing() / 2, textPaint);

        if (playAudioCallBack != null) {
            if (centerLineX >= lastSampleXWithRectGap) {
                isPlaying = false;
                isAutoScroll = false;
                playAudioCallBack.onPlayingFinish();
            } else {
                playAudioCallBack.onPlaying(currentPlayingTimeInMillis);
            }
        }

    }

    private String formatTime(long timeMillis) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("mm:ss", Locale.getDefault());
        Date date = new Date();
        date.setTime(timeMillis);
        return dateFormat.format(date);
    }

    public void setInitPlayingTime(final long timeInMillis) {
        stopPlay();
        setCenterLineXByTime(timeInMillis);
        postOnAnimation(new Runnable() {
            @Override
            public void run() {
                scrollTo((int) centerLineX - getWidth(), 0);
                invalidate();
            }
        });
    }

    @Override
    public void startPlay(long timeInMillis) {
        if (!isPlaying) {
            isTouching = false;
            setCenterLineXByTime(timeInMillis);
            isPlaying = true;
            if (centerLineX < getWidth() + getScrollX()) {
                startCenterLineToEndAnimation();
            } else {
                startTranslateView();
            }
            if (centerLineX >= lastSampleXWithRectGap - rectGap) {
                stopPlay();
            }
        }
    }

    ObjectAnimator animator;

    private void startCenterLineToEndAnimation() {
        isAutoScroll = false;
        final int animatorFromDX = lastSampleXWithRectGap > getWidth() ? getWidth() + getScrollX() : (int) lastSampleXWithRectGap;
        float dx = (animatorFromDX - centerLineX);
        final long duration = (long) (1000 * dx / (audioSourceFrequency * (lineWidth + rectGap)));
        animator = ObjectAnimator.ofFloat(this, "centerLineX", centerLineX, animatorFromDX);
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(duration);
        animator.start();
        animator.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                animator.removeAllListeners();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (isPlaying) {
                    startTranslateView();
                }
            }
        });

    }

    private void startTranslateView() {
        if (lastSampleXWithRectGap <= getWidth()) {
            return;
        }
        isAutoScroll = true;
        int dx = maxScrollX - getScrollX();
        final long duration = (1000 * dx / (audioSourceFrequency * (lineWidth + rectGap)));
        animator = ObjectAnimator.ofFloat(this, "translateX", getScrollX(), maxScrollX);
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(duration);
        animator.start();
        animator.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                animator.removeAllListeners();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                animator.removeAllListeners();
            }
        });
    }


    @Override
    public void stopPlay() {
        if (isPlaying) {
            isTouching = false;
            isPlaying = false;
            isAutoScroll = false;
            if (animator != null) {
                animator.cancel();
            }
        }
    }


}