package com.example.stepcal.Activity;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class RouteMapView extends View {

    private final Paint backgroundPaint =
            new Paint(Paint.ANTI_ALIAS_FLAG);

    private final Paint gridPaint =
            new Paint(Paint.ANTI_ALIAS_FLAG);

    private final Paint routePaint =
            new Paint(Paint.ANTI_ALIAS_FLAG);

    private final Paint startPaint =
            new Paint(Paint.ANTI_ALIAS_FLAG);

    private final Paint endPaint =
            new Paint(Paint.ANTI_ALIAS_FLAG);

    private final List<Point> points =
            new ArrayList<>();

    private double minLat;
    private double maxLat;
    private double minLon;
    private double maxLon;

    public RouteMapView(
            Context context
    ) {

        super(context);

        initialize();
    }

    public RouteMapView(
            Context context,
            AttributeSet attrs
    ) {

        super(
                context,
                attrs
        );

        initialize();
    }

    public RouteMapView(
            Context context,
            AttributeSet attrs,
            int defStyleAttr
    ) {

        super(
                context,
                attrs,
                defStyleAttr
        );

        initialize();
    }

    private void initialize() {

        setLayerType(
                View.LAYER_TYPE_SOFTWARE,
                null
        );

        backgroundPaint.setColor(
                Color.rgb(241, 238, 243)
        );

        gridPaint.setColor(
                Color.rgb(222, 216, 226)
        );

        gridPaint.setStrokeWidth(
                1f
        );

        routePaint.setColor(
                Color.rgb(111, 53, 168)
        );

        routePaint.setStyle(
                Paint.Style.STROKE
        );

        routePaint.setStrokeWidth(
                10f
        );

        routePaint.setStrokeCap(
                Paint.Cap.ROUND
        );

        routePaint.setStrokeJoin(
                Paint.Join.ROUND
        );

        startPaint.setColor(
                Color.rgb(45, 170, 90)
        );

        startPaint.setStyle(
                Paint.Style.FILL
        );

        endPaint.setColor(
                Color.rgb(244, 123, 32)
        );

        endPaint.setStyle(
                Paint.Style.FILL
        );
    }

    public void clearRoute() {

        points.clear();

        minLat = 0;
        maxLat = 0;
        minLon = 0;
        maxLon = 0;

        invalidate();
    }

    public void addLocation(
            double latitude,
            double longitude
    ) {

        Point point =
                new Point(
                        latitude,
                        longitude
                );

        points.add(
                point
        );

        if (points.size() == 1) {

            minLat = latitude;
            maxLat = latitude;
            minLon = longitude;
            maxLon = longitude;

        } else {

            minLat =
                    Math.min(
                            minLat,
                            latitude
                    );

            maxLat =
                    Math.max(
                            maxLat,
                            latitude
                    );

            minLon =
                    Math.min(
                            minLon,
                            longitude
                    );

            maxLon =
                    Math.max(
                            maxLon,
                            longitude
                    );
        }

        invalidate();
    }

    @Override
    protected void onDraw(
            Canvas canvas
    ) {

        super.onDraw(
                canvas
        );

        canvas.drawRect(
                0,
                0,
                getWidth(),
                getHeight(),
                backgroundPaint
        );

        drawGrid(
                canvas
        );

        if (points.isEmpty()) {
            return;
        }

        drawRoute(
                canvas
        );
    }

    private void drawGrid(
            Canvas canvas
    ) {

        float gridSize =
                70f;

        for (
                float x = 0;
                x <= getWidth();
                x += gridSize
        ) {

            canvas.drawLine(
                    x,
                    0,
                    x,
                    getHeight(),
                    gridPaint
            );
        }

        for (
                float y = 0;
                y <= getHeight();
                y += gridSize
        ) {

            canvas.drawLine(
                    0,
                    y,
                    getWidth(),
                    y,
                    gridPaint
            );
        }
    }

    private void drawRoute(
            Canvas canvas
    ) {

        if (points.size() == 1) {

            float[] position =
                    getScreenPosition(
                            points.get(0)
                    );

            canvas.drawCircle(
                    position[0],
                    position[1],
                    15,
                    startPaint
            );

            return;
        }

        Path route =
                new Path();

        for (
                int i = 0;
                i < points.size();
                i++
        ) {

            float[] position =
                    getScreenPosition(
                            points.get(i)
                    );

            if (i == 0) {

                route.moveTo(
                        position[0],
                        position[1]
                );

            } else {

                route.lineTo(
                        position[0],
                        position[1]
                );
            }
        }

        canvas.drawPath(
                route,
                routePaint
        );

        Point first =
                points.get(0);

        Point last =
                points.get(
                        points.size() - 1
                );

        float[] start =
                getScreenPosition(
                        first
                );

        float[] end =
                getScreenPosition(
                        last
                );

        canvas.drawCircle(
                start[0],
                start[1],
                16,
                startPaint
        );

        canvas.drawCircle(
                end[0],
                end[1],
                16,
                endPaint
        );
    }

    private float[] getScreenPosition(
            Point point
    ) {

        float padding =
                45f;

        double latRange =
                maxLat - minLat;

        double lonRange =
                maxLon - minLon;

        if (latRange == 0) {
            latRange = 0.0001;
        }

        if (lonRange == 0) {
            lonRange = 0.0001;
        }

        float usableWidth =
                Math.max(
                        1,
                        getWidth()
                                - (padding * 2)
                );

        float usableHeight =
                Math.max(
                        1,
                        getHeight()
                                - (padding * 2)
                );

        float x =
                padding
                        + (float)
                        (
                                (point.longitude
                                        - minLon)
                                        / lonRange
                        )
                        * usableWidth;

        float y =
                padding
                        + (float)
                        (
                                1.0
                                        - (
                                        (point.latitude
                                                - minLat)
                                                / latRange
                                )
                        )
                        * usableHeight;

        return new float[]{
                x,
                y
        };
    }

    private static class Point {

        final double latitude;
        final double longitude;

        Point(
                double latitude,
                double longitude
        ) {

            this.latitude =
                    latitude;

            this.longitude =
                    longitude;
        }
    }
}