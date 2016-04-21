package com.example.glushkov.zen;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;

public class Figure2 extends View {

    Paint paint, paintDot;
    Point pCenter;
    Point[] pointsExt = new Point[6];
    Point[] pointsInt = new Point[3];
    float dens;
    int R; // Внешний радиус
    int r; // Внутренний радиус
    int screenWidth, screenHeight;
    int edgeMargin = 40;    // dp
    int dotRadius = 3;      // dp

    Path innerTr, innerTrMini;


    public Figure2(Context context) {
        super(context);
        init();
    }

    public Figure2(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Figure2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public Figure2(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {

        dens = getResources().getDisplayMetrics().density;

        screenWidth = getResources().getDisplayMetrics().widthPixels;
        screenHeight = getResources().getDisplayMetrics().heightPixels;

        pCenter = new Point(screenWidth / 2, screenHeight / 2);

        R = (screenWidth - 2 * px(edgeMargin)) / 2;
        r = (int) (R * (Math.pow(3, 1f / 2f)) * (1f / 2f));

        dotRadius = px(dotRadius);

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(px(2));

        paintDot = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintDot.setColor(Color.BLACK);
        paintDot.setStyle(Paint.Style.STROKE);
        paintDot.setPathEffect(new DashPathEffect(new float[]{2, 5}, 0));

        setPointsAndElements();

    }

    private void setPointsAndElements() {

        pointsExt[0] = new Point(pCenter.x, pCenter.y - R);
        pointsExt[3] = new Point(pCenter.x, pCenter.y + R);

        pointsExt[1] = new Point(pCenter.x + r, pCenter.y - (R / 2));
        pointsExt[5] = new Point(pCenter.x - r, pCenter.y - (R / 2));

        pointsExt[2] = new Point(pCenter.x + r, pCenter.y + (R / 2));
        pointsExt[4] = new Point(pCenter.x - r, pCenter.y + (R / 2));

        pointsInt[0] = getCenterPoint(pointsExt[0], pointsExt[2]);
        pointsInt[1] = getCenterPoint(pointsExt[2], pointsExt[4]);
        pointsInt[2] = getCenterPoint(pointsExt[4], pointsExt[0]);

        // Внутренний треугольник BIG: 0 - 2 - 4 - 0
        innerTr  = new Path();
        innerTr.moveTo(pointsExt[0].x, pointsExt[0].y);
        innerTr.lineTo(pointsExt[2].x, pointsExt[2].y);
        innerTr.lineTo(pointsExt[4].x, pointsExt[4].y);
        innerTr.lineTo(pointsExt[0].x, pointsExt[0].y);

        // Внутренний треугольник MINI: 0int - 1int - 2int
        innerTrMini = new Path();
        innerTr.moveTo(pointsInt[0].x, pointsInt[0].y);
        innerTr.lineTo(pointsInt[1].x, pointsInt[1].y);
        innerTr.lineTo(pointsInt[2].x, pointsInt[2].y);
        innerTr.lineTo(pointsInt[0].x, pointsInt[0].y);

    }

    private int px(int dp) {
        return (int) (dens * dp);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Центральная точка
        canvas.drawCircle(pCenter.x, pCenter.y, dotRadius, paint);

        // Точки контура
        for (Point p : pointsExt) {
            canvas.drawCircle(p.x, p.y, dotRadius, paint);
        }

        // Точки внутренего треугольника
        for (Point p : pointsInt) {
            canvas.drawCircle(p.x, p.y, dotRadius, paint);
        }

        // Контур
        for (int i = 0; i < pointsExt.length; i++) {
            if (i == pointsExt.length - 1) {
                canvas.drawLine(pointsExt[i].x, pointsExt[i].y, pointsExt[0].x, pointsExt[0].y, paint);
            } else {
                canvas.drawLine(pointsExt[i].x, pointsExt[i].y, pointsExt[i + 1].x, pointsExt[i + 1].y, paint);
            }
        }


        canvas.drawPath(innerTr, paintDot);

        canvas.drawPath(innerTrMini, paintDot);

    }

    private Point getCenterPoint(Point a, Point b){
        Point p = new Point();
        p.x = (a.x + b.x) / 2;
        p.y = (a.y + b.y) / 2;
        return p;
    }
}
