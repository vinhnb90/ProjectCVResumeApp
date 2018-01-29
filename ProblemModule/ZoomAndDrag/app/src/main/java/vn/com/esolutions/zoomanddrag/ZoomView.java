package vn.com.esolutions.zoomanddrag;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.WindowManager;


/**
 * Created by VINH-PC on 1/22/2018.
 */

public class ZoomView extends View {
    private int displayWidth;
    private int displayHeight;

    Display display = null;

    private static final float MIN_ZOOM = 1f;
    private static final float MAX_ZOOM = 1f;
    private static final String TAG = "ZoomView";
    private ScaleGestureDetector scaleGestureDetector;
    private float scaleFactor = 1.0f;

    //x y khi bắt đầu nhấn xuống
    private float startX = 1.0f;
    private float startY = 1.0f;

    //lượng độ dài x, y khi thay đổi x, y trong hành động di chuyển ngón tay
    private float translateX = 1.0f;
    private float translateY = 1.0f;

    private float previousTranslateX = 0.f;
    private float previousTranslateY = 0.f;


    //This flag reflects whether the finger was actually dragged across the screen
    private boolean dragged = true;


    private MODE_ACTION modeAction;

    private enum MODE_ACTION {
        NONE(0),
        DRAG(1),
        ZOOM(2);

        public int value;

        MODE_ACTION(int value) {
            this.value = value;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        displayWidth = MeasureSpec.getSize(widthMeasureSpec);
        displayHeight = MeasureSpec.getSize(heightMeasureSpec);
    }

    public ZoomView(Context context) {
        super(context);
        scaleGestureDetector = new ScaleGestureDetector(context, new ScaleListener());
//        display = ((WindowManager)
//                getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
//        displayWidth = this.getMeasuredWidth();
//        displayHeight = this.getMeasuredHeight();
    }

    public ZoomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        scaleGestureDetector = new ScaleGestureDetector(context, new ScaleListener());
//        display = ((WindowManager)
//                getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
//        displayWidth = this.getWidth();
//        displayHeight = this.getHeight();
    }

    public ZoomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        scaleGestureDetector = new ScaleGestureDetector(context, new ScaleListener());
//        display = ((WindowManager)
//                getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
//        displayWidth = this.getMeasuredWidth();
//        displayHeight = this.getMeasuredHeight();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ZoomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        scaleGestureDetector = new ScaleGestureDetector(context, new ScaleListener());
//        display = ((WindowManager)
//                getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
//        displayWidth = this.getMeasuredWidth();
//        displayHeight = this.getMeasuredHeight();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.i(TAG, "----------------------");
//        Log.i(TAG, "onTouchEvent: event.getAction() = " + event.getAction() + " (" + Integer.toString(event.getAction(), 16) + ")");
//        Log.i(TAG, "onTouchEvent: MotionEvent.ACTION_MASK = " + MotionEvent.ACTION_MASK + " (" + Integer.toString(MotionEvent.ACTION_MASK, 16) + ")");

        int numberFinger = event.getPointerCount();
        Log.i(TAG, "onTouchEvent: numberFinger = " + numberFinger);
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                //tay nhấn screen
//                Log.i(TAG, "onTouchEvent: MotionEvent.ACTION_DOWN = " + MotionEvent.ACTION_DOWN + " (" + Integer.toString(MotionEvent.ACTION_DOWN, 16) + ")");
                modeAction = MODE_ACTION.DRAG;

                //ở lần đầu tiên
                startX = event.getX();
                startY = event.getY();

                Log.i(TAG, "onTouchEvent: event.getX() = " + event.getX() + " and getX() = " + getX());
                Log.i(TAG, "onTouchEvent: event.getY() = " + event.getY() + " and getY() = " + getY());
                break;

            case MotionEvent.ACTION_MOVE:
//                Log.i(TAG, "onTouchEvent: MotionEvent.ACTION_MOVE = " + MotionEvent.ACTION_MOVE + " (" + Integer.toString(MotionEvent.ACTION_MOVE, 16) + ")");

                //khi di chuyển sẽ tính được
                translateX = event.getX() - startX;
                translateY = event.getY() - startY;

                double distance = Math.sqrt(Math.pow(event.getX() - (startX + previousTranslateX), 2) + Math.pow(event.getY() - (startY + previousTranslateY), 2));
                if (distance > 0) {
                    dragged = true;
                }
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                //ngón thứ 2 nhấn
//                Log.i(TAG, "onTouchEvent: MotionEvent.ACTION_POINTER_DOWN = " + MotionEvent.ACTION_POINTER_DOWN + " (" + Integer.toString(MotionEvent.ACTION_POINTER_DOWN, 16) + ")");
                modeAction = MODE_ACTION.ZOOM;
                break;

            case MotionEvent.ACTION_UP:
                //không còn ngón nào trên screen
//                Log.i(TAG, "onTouchEvent: MotionEvent.ACTION_UP = " + MotionEvent.ACTION_UP + " (" + Integer.toString(MotionEvent.ACTION_UP, 16) + ")");
                modeAction = MODE_ACTION.NONE;


                previousTranslateX = translateX;
                previousTranslateY = translateY;
                break;

            case MotionEvent.ACTION_POINTER_UP:
//                Log.i(TAG, "onTouchEvent: MotionEvent.ACTION_POINTER_UP = " + MotionEvent.ACTION_POINTER_UP + " (" + Integer.toString(MotionEvent.ACTION_POINTER_UP, 16) + ")");
                //nhấc chỉ còn để lại 1 ngón trên screen
                modeAction = MODE_ACTION.DRAG;

                //lưu điểm cuối
                previousTranslateX = translateX;
                previousTranslateY = translateY;
                break;
        }

        Log.i(TAG, "onTouchEvent: MODE_ACTION = " + modeAction);
        scaleGestureDetector.onTouchEvent(event);


        translateX = (translateX * -1) < 0 ? 0 : translateX;
        translateY = (translateY * -1) < 0 ? 0 : translateY;

        //The only time we want to re-draw the canvas is if we are panning (which happens when the mode is
        //DRAG and the zoom factor is not equal to 1) or if we're zooming
        if ((modeAction == MODE_ACTION.DRAG && scaleFactor != 1f) || modeAction == MODE_ACTION.ZOOM) {
            invalidate();
        }


        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //draw canvas
        canvas.drawBitmap(((BitmapDrawable) this.getBackground()).getBitmap(), new Rect(0, 0, getWidth(), getHeight() ), new Rect((int)translateX, (int)translateY, (int)(translateX+getX()), (int)(translateY+getY())), null);
        canvas.save();


        //scale
        canvas.scale(scaleFactor, scaleFactor);
        Log.i(TAG, "onDraw: canvas.scale(scaleFactor, scaleFactor) = " + scaleFactor);


//        canvas.drawBitmap(((BitmapDrawable)this.getBackground()).getBitmap(),new Rect(getWidth(),getHeight(), 0,0), new Rect(getWidth()/2,getHeight()/2,0,0), null);
        if ((translateX * -1) < 0) {
            translateX = 0;
        } else if ((translateX * -1) > (scaleFactor - 1) * displayWidth) {
            translateX = (1 - scaleFactor) * displayWidth;
        }

        if (translateY * -1 < 0) {
            translateY = 0;
        } else if ((translateY * -1) > (scaleFactor - 1) * displayHeight) {
            translateY = (1 - scaleFactor) * displayHeight;
        }


        canvas.translate(translateX / scaleFactor, translateY / scaleFactor);
        Log.i(TAG, "onDraw: canvas.translate(translateX/scaleFactor, translateY/scaleFactor) = canvas.translate(" + translateX + "/" + scaleFactor + ", " + translateY + "/" + scaleFactor + ")");
        canvas.restore();
    }

    private class ScaleListener implements ScaleGestureDetector.OnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
            scaleFactor *= scaleGestureDetector.getScaleFactor();
            Log.i(TAG, "onScale: scaleFactor = " + scaleFactor);
            scaleFactor = Math.max(MIN_ZOOM, Math.min(scaleFactor, MAX_ZOOM));
            Log.i(TAG, "onScale: scaleFactor max = " + scaleFactor + "MIN_ZOOM = " + MIN_ZOOM + " and MAX_ZOOM = " + MAX_ZOOM);
            //onScale() không được gọi khi dragger
            //onScale(…) method is called only when zooming happens and not when panning or dragging happens
//            invalidate();
            return true;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
            return false;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector scaleGestureDetector) {

        }
    }
}
