package com.example.stepcal.Activity;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class ProgressGaugeView extends View {

    private final Paint trackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint percentPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint labelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final RectF arcRect = new RectF();

    private float progress = 0f;

    public ProgressGaugeView(Context context) {
        super(context);
        init();
    }

    public ProgressGaugeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ProgressGaugeView(
            Context context,
            AttributeSet attrs,
            int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        trackPaint.setStyle(Paint.Style.STROKE);
        trackPaint.setStrokeCap(Paint.Cap.ROUND);
        trackPaint.setStrokeWidth(dp(12));
        trackPaint.setColor(Color.parseColor("#F4CDB8"));

        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeCap(Paint.Cap.ROUND);
        progressPaint.setStrokeWidth(dp(12));
        progressPaint.setColor(Color.parseColor("#F2A477"));

        percentPaint.setStyle(Paint.Style.FILL);
        percentPaint.setColor(Color.WHITE);
        percentPaint.setTextAlign(Paint.Align.CENTER);
        percentPaint.setTypeface(
                android.graphics.Typeface.create(
                        android.graphics.Typeface.DEFAULT,
                        android.graphics.Typeface.BOLD
                )
        );

        labelPaint.setStyle(Paint.Style.FILL);
        labelPaint.setColor(Color.parseColor("#FFF1E8"));
        labelPaint.setTextAlign(Paint.Align.CENTER);
        labelPaint.setTypeface(
                android.graphics.Typeface.create(
                        android.graphics.Typeface.DEFAULT,
                        android.graphics.Typeface.NORMAL
                )
        );

        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    public void setProgress(int value) {

        if (value < 0) {
            value = 0;
        }

        if (value > 100) {
            value = 100;
        }

        progress = value;

        invalidate();
    }

    private float dp(float value) {

        return value * getResources()
                .getDisplayMetrics()
                .density;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);

        float width = getWidth();
        float height = getHeight();

        float centerX = width / 2f;

        float radius =
                Math.min(
                        width * 0.36f,
                        height * 0.70f
                );

        float centerY =
                height * 0.82f;

        arcRect.set(
                centerX - radius,
                centerY - radius,
                centerX + radius,
                centerY + radius
        );

        // Background track.
        canvas.drawArc(
                arcRect,
                180f,
                180f,
                false,
                trackPaint
        );

        // Completed portion.
        if (progress > 0) {

            canvas.drawArc(
                    arcRect,
                    180f,
                    180f * (progress / 100f),
                    false,
                    progressPaint
            );
        }

        // Percentage.
        percentPaint.setTextSize(dp(30));

        float percentY =
                centerY - dp(15);

        canvas.drawText(
                String.format(
                        "%d%%",
                        Math.round(progress)
                ),
                centerX,
                percentY,
                percentPaint
        );

        // Label.
        labelPaint.setTextSize(dp(12));

        canvas.drawText(
                "TASKS COMPLETE",
                centerX,
                percentY + dp(23),
                labelPaint
        );
    }
}