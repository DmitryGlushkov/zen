package com.example.glushkov.zen;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;

public class Figure2 extends View {

    Paint paint;
    Point pCenter;
    Point[] pointsExt = new Point[6];
    float dens;
    int R; // Внешний радиус
    int r; // Внутренний радиус
    int screenWidth, screenHeight;
    int edgeMargin = 70;    // dp
    int dotRadius = 3;      // dp


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

        dotRadius = px(dotRadius);

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLACK);

        setPoints();

    }

    private void setPoints() {

        pointsExt[0] = new Point(pCenter.x, pCenter.y - R);
        pointsExt[3] = new Point(pCenter.x, pCenter.y + R);

        pointsExt[1] = new Point(pCenter.x + R, pCenter.y - (R / 2));
        pointsExt[5] = new Point(pCenter.x - R, pCenter.y - (R / 2));

        pointsExt[2] = new Point(pCenter.x + R, pCenter.y + (R / 2));
        pointsExt[4] = new Point(pCenter.x - R, pCenter.y + (R / 2));

    }

    private int px(int dp) {
        return (int) (dens * dp);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Center point
        canvas.drawCircle(pCenter.x, pCenter.y, dotRadius, paint);
        for (Point p : pointsExt) {
            canvas.drawCircle(p.x, p.y, dotRadius, paint);
        }
    }
}
