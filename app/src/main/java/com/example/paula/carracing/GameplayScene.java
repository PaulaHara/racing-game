package com.example.paula.carracing;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.MotionEvent;

public class GameplayScene implements Scene {

    private Rect r = new Rect();

    private RectPlayer player;
    private Point playerPoint;
    private ObstacleManager mObstacleManager;

    private boolean movingPlayer = false;

    private boolean gameOver = false;
    private long gameOverTime;

    private OrientationData mOrientationData;
    private long frameTime;

    private Resources mResources;

    public GameplayScene(Resources resources){
        player = new RectPlayer(new Rect(100,100,200,200), Color.rgb(255, 0, 0));
        playerPoint = new Point(Constants.FIELD_WIDTH_RIGHT/2, 3*Constants.SCREEN_HEIGHT/4);
        player.update(playerPoint);

        mObstacleManager = new ObstacleManager(200, 380, 75, Color.BLACK);

        mOrientationData = new OrientationData();
        mOrientationData.register();
        frameTime = System.currentTimeMillis();

        mResources = resources;
    }

    public void reset(){
        playerPoint = new Point(Constants.FIELD_WIDTH_RIGHT/2, 3*Constants.SCREEN_HEIGHT/4);
        player.update(playerPoint);
        mObstacleManager = new ObstacleManager(200, 380, 75, Color.BLACK);
        movingPlayer = false;
    }

    @Override
    public void update() {
        if(!gameOver) {
            if(frameTime < Constants.INIT_TIME){
                frameTime = Constants.INIT_TIME;
            }
            int elapsedTime = (int) (System.currentTimeMillis() - frameTime);
            frameTime = System.currentTimeMillis();
            if(mOrientationData.getOrientation() != null && mOrientationData.getStartOrientation() != null) {
                float pitch = mOrientationData.getOrientation()[1] - mOrientationData.getStartOrientation()[1];
                float roll = mOrientationData.getOrientation()[2] - mOrientationData.getStartOrientation()[2];

                float xspeed = 2 * roll * Constants.FIELD_WIDTH_RIGHT/1000f;
                float yspeed = pitch * Constants.SCREEN_HEIGHT/1000f;

                playerPoint.x += Math.abs(xspeed*elapsedTime) > 5 ? xspeed*elapsedTime : 0;
                playerPoint.y -= Math.abs(yspeed*elapsedTime) > 5 ? yspeed*elapsedTime : 0;
            }

            if(playerPoint.x < Constants.FIELD_WIDTH_LEFT){
                playerPoint.x = Constants.FIELD_WIDTH_LEFT;
            }else if(playerPoint.x > Constants.FIELD_WIDTH_RIGHT){
                playerPoint.x = Constants.FIELD_WIDTH_RIGHT;
            }
            if(playerPoint.y < 0){
                playerPoint.y = 0;
            }else if(playerPoint.y > Constants.SCREEN_HEIGHT){
                playerPoint.y = Constants.SCREEN_HEIGHT;
            }

            player.update(playerPoint);
            mObstacleManager.update();

            if(mObstacleManager.playerCollide(player)){
                gameOver = true;
                gameOverTime = System.currentTimeMillis();
            }
        }
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawColor(Color.BLUE);

        Rect r = new Rect();
        r.set(0, 0, Constants.FIELD_WIDTH_LEFT, Constants.SCREEN_HEIGHT);
        Rect r2 = new Rect();
        r2.set(Constants.FIELD_WIDTH_RIGHT, 0, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
        Paint paint2 = new Paint();
        paint2.setColor(Color.GREEN);
        canvas.drawRect(r, paint2);
        canvas.drawRect(r2, paint2);

        player.draw(canvas);
        mObstacleManager.draw(canvas);

        if(gameOver){
            Paint paint = new Paint();
            paint.setTextSize(100);
            paint.setColor(Color.MAGENTA);
            drawCenterText(canvas, paint, "Game Over");
        }
    }

    @Override
    public void terminate() {
        SceneManager.ACTIVE_SCENE = 0;
    }

    @Override
    public void receiveTouch(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if(!gameOver && player.getRectangle().contains((int) event.getX(), (int) event.getY())){
                    movingPlayer = true;
                }
                if(gameOver && System.currentTimeMillis() - gameOverTime >= 2000){
                    reset();
                    gameOver = false;
                    mOrientationData.newGame();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if(!gameOver && movingPlayer) {
                    playerPoint.set((int) event.getX(), (int) event.getY());
                }
                break;
            case MotionEvent.ACTION_UP:
                movingPlayer = false;
                break;

        }
    }

    private void drawCenterText(Canvas canvas, Paint paint, String text){
        paint.setTextAlign(Paint.Align.LEFT);
        canvas.getClipBounds(r);
        int cHeight = r.height();
        int cWidth = r.width();
        paint.getTextBounds(text, 0, text.length(), r);
        float x = cWidth/2f - r.width()/2f - r.left;
        float y = cHeight/2f + r.height() / 2f - r.bottom;
        canvas.drawText(text, x, y, paint);
    }
}
