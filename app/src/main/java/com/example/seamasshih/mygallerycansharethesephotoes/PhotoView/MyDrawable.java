package com.example.seamasshih.mygallerycansharethesephotoes.PhotoView;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class MyDrawable {

    static CloseUp closeUp;
    static CloseDown closeDown;

    public static CloseUp createCloseUpIcon(){
        if (closeUp == null)
            closeUp = new CloseUp();
        return closeUp;
    }

    public static CloseDown createCloseDownIcon(){
        if (closeDown == null)
            closeDown = new CloseDown();
        return closeDown;
    }




    static class CloseUp extends Drawable{

        Paint paint;
        Path path;

        public CloseUp(){
            paint = new Paint();
            paint.setColor(Color.WHITE);
            paint.setAntiAlias(true);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(8);
            paint.setStrokeCap(Paint.Cap.ROUND);

            path = new Path();
        }

        @Override
        public void draw(@NonNull Canvas canvas) {
            int cx = canvas.getWidth()/2;
            int cy = canvas.getHeight()/2;
            int dx = canvas.getWidth()/8;
            int dy = canvas.getHeight()/8;

            path.reset();
            path.moveTo(cx-dx,cy);
            path.lineTo(cx,cy-dy);
            path.lineTo(cx+dy,cy);
            path.moveTo(cx-dx,cy+dy);
            path.lineTo(cx,cy);
            path.lineTo(cx+dy,cy+dy);

            canvas.drawPath(path,paint);
        }

        @Override
        public void setAlpha(int alpha) {

        }

        @Override
        public void setColorFilter(@Nullable ColorFilter colorFilter) {

        }

        @Override
        public int getOpacity() {
            return PixelFormat.OPAQUE;
        }
    }



    static class CloseDown extends Drawable{

        Paint paint;
        Path path;

        public CloseDown(){
            paint = new Paint();
            paint.setColor(Color.WHITE);
            paint.setAntiAlias(true);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(8);
            paint.setStrokeCap(Paint.Cap.ROUND);

            path = new Path();
        }

        @Override
        public void draw(@NonNull Canvas canvas) {
            int cx = canvas.getWidth()/2;
            int cy = canvas.getHeight()/2;
            int dx = canvas.getWidth()/8;
            int dy = canvas.getHeight()/8;

            path.reset();
            path.moveTo(cx-dx,cy);
            path.lineTo(cx,cy+dy);
            path.lineTo(cx+dy,cy);
            path.moveTo(cx-dx,cy-dy);
            path.lineTo(cx,cy);
            path.lineTo(cx+dy,cy-dy);

            canvas.drawPath(path,paint);
        }

        @Override
        public void setAlpha(int alpha) {

        }

        @Override
        public void setColorFilter(@Nullable ColorFilter colorFilter) {

        }

        @Override
        public int getOpacity() {
            return PixelFormat.OPAQUE;
        }
    }
}
