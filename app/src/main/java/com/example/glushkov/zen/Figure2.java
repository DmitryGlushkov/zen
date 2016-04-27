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

    LinearFunction lf_4_0, lf_0_2, lf_4_2;

    private void setPointsAndElements() {

        // Точки контура шестиугольника

        pointsExt[0] = new Point(pCenter.x, pCenter.y - R);
        pointsExt[3] = new Point(pCenter.x, pCenter.y + R);

        pointsExt[1] = new Point(pCenter.x + r, pCenter.y - (R / 2));
        pointsExt[5] = new Point(pCenter.x - r, pCenter.y - (R / 2));

        pointsExt[2] = new Point(pCenter.x + r, pCenter.y + (R / 2));
        pointsExt[4] = new Point(pCenter.x - r, pCenter.y + (R / 2));

        // Функции прямых внутреннего большего треугольника

        lf_4_0 = new LinearFunction(pointsExt[4], pointsExt[0]);
        lf_0_2 = new LinearFunction(pointsExt[0], pointsExt[2]);
        lf_4_2 = new LinearFunction(pointsExt[4], pointsExt[2]);

        // Точки внутреннего треугольника, располагаются в центе ребер большего треугольника

        pointsInt[0] = lf_0_2.centerPoint;
        pointsInt[1] = lf_4_2.centerPoint;
        pointsInt[2] = lf_4_0.centerPoint;

        // Внутренний треугольник BIG: 0 - 2 - 4 - 0
        innerTr = new Path();
        innerTr.moveTo(pointsExt[0].x, pointsExt[0].y);
        innerTr.lineTo(pointsExt[2].x, pointsExt[2].y);
        innerTr.lineTo(pointsExt[4].x, pointsExt[4].y);
        innerTr.lineTo(pointsExt[0].x, pointsExt[0].y);

        // Внутренний треугольник MINI: 0int - 1int - 2int
        innerTrMini = new Path();
        innerTrMini.moveTo(pointsInt[0].x, pointsInt[0].y);
        innerTrMini.lineTo(pointsInt[1].x, pointsInt[1].y);
        innerTrMini.lineTo(pointsInt[2].x, pointsInt[2].y);
        innerTrMini.lineTo(pointsInt[0].x, pointsInt[0].y);

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


    public void startRotate() {

        Timer mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                pointsInt[0] = lf_0_2.getNextPointIncr();
                pointsInt[2] = lf_4_0.getNextPointIncr();
                pointsInt[1] = lf_4_2.getNextPointDecr();

                // inner tr
                innerTrMini.rewind();
                innerTrMini.moveTo(pointsInt[0].x, pointsInt[0].y);
                innerTrMini.lineTo(pointsInt[1].x, pointsInt[1].y);
                innerTrMini.lineTo(pointsInt[2].x, pointsInt[2].y);
                innerTrMini.lineTo(pointsInt[0].x, pointsInt[0].y);

                postInvalidate();

            }
        }, 0, 10);
    }

    class LinearFunction {

        public static final int STEP_COUNT = 100;

        public float K;
        public float B;

        public Point pointA, pointB, centerPoint;

        private Point currentPoint = null;

        private float deltaX, deltaX_step;

        int lastStep = -1;

        public LinearFunction(Point pointA, Point pointB) { // y = k * x + b

            this.pointA = pointA;
            this.pointB = pointB;

            K = ((float) (pointB.y - pointA.y)) / ((float) (pointB.x - pointA.x));
            B = (float) pointA.y - K * (float) pointA.x;

            centerPoint = getCenterPoint(pointA, pointB);

            deltaX = pointB.x - pointA.x;

            if (deltaX < 0) throw new RuntimeException("deltaX less than zero !");

            deltaX_step = deltaX / (float) STEP_COUNT;
        }

        public Point getNextPointIncr() {
            lastStep++;
            if (lastStep > STEP_COUNT) lastStep = 0;
            return calcCurrentPoint();
        }

        public Point getNextPointDecr() {
            lastStep--;
            if (lastStep < 0) lastStep = STEP_COUNT;
            return calcCurrentPoint();
        }

        private Point calcCurrentPoint(){

            // start check
            if (currentPoint == null) {
                currentPoint = centerPoint;
                lastStep = STEP_COUNT / 2;
                return currentPoint;
            }

            int newY, newX;
            // get new X
            newX = (int) ((float) pointA.x + (deltaX_step * lastStep));
            // get new Y
            if (Math.abs(K) == 0) {
                newY = (int) (B);
            } else {
                newY = (int) (K * newX + B);
            }

            currentPoint.x = newX;
            currentPoint.y = newY;

            return currentPoint;
        }



        private Point getCenterPoint(Point a, Point b) {
            Point p = new Point();
            p.x = (a.x + b.x) / 2;
            p.y = (a.y + b.y) / 2;
            return p;
        }
    }
}
