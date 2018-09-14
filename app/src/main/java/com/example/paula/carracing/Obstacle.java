package com.example.paula.carracing;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

public class Obstacle implements GameObject{

    private Rect rectangle;
    private Rect rectangle2;
    //private Rect rectangle3;
    private int color;

    public Rect getRectangle() {
        return rectangle;
    }

    public void incrementY(float y){
        rectangle.top += y;
        rectangle.bottom += y;
        rectangle2.top += y;
        rectangle2.bottom += y;
        //rectangle3.top += y;
        //rectangle3.bottom += y;
    }

    public Obstacle(int rectHeight, int color, int startX, int startY, int playerGap){
        this.color = color;
        //l,t,r,b
        int endRect = Constants.FIELD_WIDTH_LEFT+startX-(playerGap);
        endRect = endRect < Constants.FIELD_WIDTH_LEFT ? Constants.FIELD_WIDTH_LEFT : endRect;
        rectangle = new Rect(Constants.FIELD_WIDTH_LEFT, startY, endRect,startY+rectHeight);

        int startRect2 = (startX+2*playerGap) > Constants.FIELD_WIDTH_RIGHT ? Constants.FIELD_WIDTH_RIGHT : (startX+2*playerGap);
        rectangle2 = new Rect(startRect2, startY, Constants.FIELD_WIDTH_RIGHT, startY + rectHeight);
        //rectangle3 = new Rect((startX + 2*playerGap)+playerGap, startY, Constants.FIELD_WIDTH_LEFT, startY + rectHeight);

        Log.i("Obstacle: sizes", "Rec1:"+Constants.FIELD_WIDTH_LEFT+"-"+endRect);
        Log.i("Obstacle: sizes", "Rec2:"+startRect2+"-"+Constants.FIELD_WIDTH_RIGHT);
    }

    public boolean playerCollide(RectPlayer player){
        return Rect.intersects(rectangle, player.getRectangle()) ||
                Rect.intersects(rectangle2, player.getRectangle()); // ||
                        //Rect.intersects(rectangle3, player.getRectangle());
    }

    @Override
    public void draw(Canvas canvas) {
        Paint paint = new Paint();
        Paint paint2 = new Paint();
        paint.setColor(color);
        paint2.setColor(Color.RED);
        canvas.drawRect(rectangle, paint);
        canvas.drawRect(rectangle2, paint2);
        //canvas.drawRect(rectangle3, paint);
    }

    @Override
    public void update() {

    }
}
