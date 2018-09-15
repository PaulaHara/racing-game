package com.example.paula.carracing;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.Log;

import java.util.ArrayList;

public class ObstacleManager {
    // higher index = lower on screen = higher y value
    private ArrayList<Obstacle> mObstacles;
    private int playerGap;
    private int obstacleGap;
    private int obstacleHeight;
    private int color;

    private long startTime;
    private long initTime;

    private int score = 0;

    public ObstacleManager(int playerGap, int obstacleGap, int obstacleHeight, int color){
        this.playerGap = playerGap;
        this.obstacleGap = obstacleGap;
        this.obstacleHeight = obstacleHeight;
        this.color = color;

        startTime = initTime = System.currentTimeMillis();

        mObstacles = new ArrayList<>();

        populateObstacles();
    }

    public boolean playerCollide(RectPlayer player){
        for(Obstacle ob : mObstacles){
            if(ob.playerCollide(player)){
                return true;
            }
        }
        return false;
    }

    private void populateObstacles(){
        int currY = -5*Constants.SCREEN_HEIGHT/4;
        while(currY < 0){
            int xStart = (int) (Math.random()*(Constants.FIELD_WIDTH_RIGHT - playerGap));
            mObstacles.add(new Obstacle(obstacleHeight, color, xStart, currY, playerGap));
            currY += obstacleHeight + obstacleGap;
        }
    }

    public void update(){
        if(startTime < Constants.INIT_TIME){
            startTime = Constants.INIT_TIME;
        }

        int elapsedTime = (int) (System.currentTimeMillis() - startTime);
        startTime = System.currentTimeMillis();
        float speed = (float)(Math.sqrt(1 + (startTime - initTime)/2000.0)) * Constants.SCREEN_HEIGHT/10000.0f;

        for(Obstacle ob : mObstacles){
            ob.incrementY(speed * elapsedTime);
        }
        if(mObstacles.get(mObstacles.size() - 1).getRectangle().top >= Constants.SCREEN_HEIGHT){
            int xStart = (int) (Math.random()*(Constants.FIELD_WIDTH_RIGHT - playerGap));
            mObstacles.add(0, new Obstacle(obstacleHeight, color, xStart,
                    mObstacles.get(0).getRectangle().top - obstacleHeight - obstacleGap, playerGap));
            mObstacles.remove(mObstacles.size() - 1);
            score += 10;
        }
        score++;
    }

    public void draw(Canvas canvas){
        for(Obstacle ob : mObstacles){
            ob.draw(canvas);
            //canvas.drawBitmap(trunks[0], null, ob.getRectangle(), new Paint());
        }
        Paint paint = new Paint();
        paint.setTextSize(100);
        paint.setColor(Color.MAGENTA);
        canvas.drawText("" + score, 50, 50 + paint.descent() - paint.ascent(), paint);
    }
}
