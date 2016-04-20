package com.example.glushkov.zen;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;

public class Figure1 extends View {

    Paint paint;
    Point[] points = new Point[6];
    Point[] points_i = new Point[3];    // Внутренние вершины
    float dens;

    public Figure1(Context context) {
        super(context);
        init();
    }

    public Figure1(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Figure1(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public Figure1(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        setFramePoints();
        canvas.drawCircle(points[0].x, points[0].y, dot_R, paint);
        canvas.drawCircle(points[3].x, points[3].y, dot_R, paint);

        canvas.drawCircle(points[5].x, points[5].y, dot_R, paint);
        canvas.drawCircle(points[1].x, points[1].y, dot_R, paint);
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLACK);
        dens = getResources().getDisplayMetrics().density;
    }

    int R = 0;  // "радиус" фигуры
    int outer_margin = 50;  // в dp
    int dot_R = 10;
    int T = 76; // высота внутреннего мини-треугольника


    private void setFramePoints() {

        int width = getWidth();
        int height = getHeight();

        R = width - (px(outer_margin) * 2);

        int top_verc = (height - R) / 2;
        //int bot_verc = top_verc + ;

        points[0] = new Point(width / 2, top_verc);
        points[3] = new Point(width / 2, ((height - R) / 2) + R);

        points[5] = new Point(px(outer_margin), top_verc + px(T));
        points[1] = new Point(width - px(outer_margin), top_verc + px(T));

    }

        private int px(int dp) {
            return (int) (dens * dp);
        }
}
