package com.example.glushkov.zen;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;

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
        innerTr = new Path();
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
        //canvas.drawCircle(pCenter.x, pCenter.y, dotRadius, paint);

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

    private Point getCenterPoint(Point a, Point b) {
        Point p = new Point();
        p.x = (a.x + b.x) / 2;
        p.y = (a.y + b.y) / 2;
        return p;
    }

    Handler uiHandler = new Handler(Looper.getMainLooper());
    float percent = 0.5f;


    public void startRotate() {
        final LinearFunction lf_4_0 = new LinearFunction(pointsExt[4], pointsExt[0]);
        final LinearFunction lf_0_2 = new LinearFunction(pointsExt[0], pointsExt[2]);
        final LinearFunction lf_2_4 = new LinearFunction(pointsExt[2], pointsExt[4]);
        Timer mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                pointsInt[2] = lf_4_0.getNextPoint(pointsInt[2]);
                pointsInt[0] = lf_0_2.getNextPoint(pointsInt[0]);
                pointsInt[1] = lf_2_4.getNextPoint(pointsInt[1]);
                postInvalidate();
            }
        }, 0, 50);
    }

    class LinearFunction {

        public float K;
        public float B;

        private Point pointA, pointB;

        public LinearFunction(Point pointA, Point pointB) {
            this.pointA = pointA;
            this.pointB = pointB;
            // y = k * x + b
            K = ((float) (pointB.y - pointA.y)) / ((float) (pointB.x - pointA.x));
            B = (float) pointA.y - K * (float) pointA.x;
        }

        public Point getNextPoint(Point startPoint) {
            int newX = startPoint.x + 1;
            if (newX > pointB.x) newX = pointA.x;
            int newY = (int) (K * newX + B);
            return new Point(newX, newY);
        }
    }
}
