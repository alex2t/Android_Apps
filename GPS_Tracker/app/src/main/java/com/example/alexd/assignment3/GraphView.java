package com.example.alexd.assignment3;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GraphView extends View {
    List<Integer> times;
    List<Float> altitudes;
    Paint graphPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    Paint axes = new Paint(Paint.ANTI_ALIAS_FLAG);
    int screenH;
    int screenW;
    int colW;
    int colH;
    int columnCount;

    public GraphView(Context context) {
        super(context);
        init(null, 0);
    }
    /**
     * @param context
     * @param attrs
     */
    public GraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    /**
     * @param context
     * @param attrs
     * @param defStyle
     */
    public GraphView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    /**
     * This method is called in every constructor to initialize random indices and
     * background color of the board...
     * @param attrs
     * @param defStyle
     */
    private void init(AttributeSet attrs, int defStyle) {
        graphPaint.setColor(Color.MAGENTA);
        graphPaint.setStyle(Paint.Style.FILL);
        axes.setColor(Color.BLACK);
        axes.setStyle(Paint.Style.FILL);
    }


    @Override
    public void onSizeChanged (int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        screenW = w;
        screenH = h;
    }

    public void drawGraph(List<Integer> times, List<Float> altitudes){
        this.times = times;
        this.altitudes = altitudes;
        columnCount = times.size();
        invalidate();
    }
    public void setBackgroundResource (int resid){
        super.setBackgroundResource(resid);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        colW = (screenW - 10) / columnCount;
        colH = (screenH - 10) / columnCount;
        int graphStep = 20;
        int columnSpace = 5;
        float maxAltitude = Collections.max(altitudes);

        canvas.drawText("ALTITUDE(m)", 0, 10,  graphPaint);
        canvas.drawText("Time(s)",screenH/2,screenW,graphPaint);
        canvas.drawText("0",25,screenW-25,graphPaint);
        canvas.drawText(times.get(times.size()-1)+"",screenH-20,screenW-25,graphPaint);
        canvas.drawText(String.format("%.1f" ,maxAltitude),0,30,graphPaint);
        canvas.drawLine(40,20,40,screenH,axes);
        canvas.drawLine(0,screenH-40,screenW,screenH-40,axes);

        for (int i=0; i<columnCount-1; i++)
        {
            canvas.drawLine(i*colW+40,altitudes.get(i),(i+1)*colW+40,altitudes.get(i+1),graphPaint);
        }
    }

    /**
     * This method is called when the screen orientation or resolution is changed. This makes the random view square irrespective
     * of the screen size and orientation of the device...
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // This stores the size of one side of the square...
        int size = 0;
        // Get screen height and width...
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        // check what is less width or height. make the size equal to which is less...
        if (width < height) {
            size = width;
        } else {
            size = height;
        }

        // Then set dimension of the custom view to size*size, which will make it a square in shape...
        setMeasuredDimension(size, size);
    }
}
