package edu.ucsb.cs.cs185.justinliang.justinliangmultitouch;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.ImageView;

public class TouchView extends ImageView
{
	private static final int INVALID_POINTER_ID = -1;
		
	private float lastTouchedX = 0;
	private float lastTouchedY = 0;
	
	private float circleX = 0;
	private float circleY = 0;
	
	private float circleSize = 10;
	
	private float fX, fY, sX, sY;
    private int pointer1, pointer2;
    private float mAngle;
	
	private Matrix matrix;
	
	float saveScale = 1f;
	ScaleGestureDetector gestureDetector; // ScaleGestureDetector
	
	public TouchView(Context context)
	{
		super(context);
		gestureDetector = new ScaleGestureDetector(context, new ScaleListener());
		matrix = new Matrix();
		setImageMatrix(matrix);
        setScaleType(ScaleType.MATRIX);
        pointer1 = INVALID_POINTER_ID;
        pointer2 = INVALID_POINTER_ID;
	}
	
	public TouchView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		gestureDetector = new ScaleGestureDetector(context, new ScaleListener());
		matrix = new Matrix();
		setImageMatrix(matrix);
        setScaleType(ScaleType.MATRIX);
        pointer1 = INVALID_POINTER_ID;
        pointer2 = INVALID_POINTER_ID;
	}
	
	@Override
	public void setImageBitmap(Bitmap bm)
	{
		matrix.reset();
		setImageMatrix(matrix);
		super.setImageBitmap(bm);		
	}
	
	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		canvas.save(); 		
		// Create new Paint for the circle, set Paint color to red, draw circle
		Paint circlePaint = new Paint();
		circlePaint.setColor(Color.RED);
		canvas.drawCircle(circleX, circleY, circleSize, circlePaint);
		canvas.restore();
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) 
	{
		gestureDetector.onTouchEvent(event);
		final int action = MotionEventCompat.getActionMasked(event);
        switch (action) 
        {
	        case MotionEvent.ACTION_DOWN:
	        {
	        	lastTouchedX = event.getX();
	        	lastTouchedY = event.getY();
	        	pointer1 = event.getPointerId(event.getActionIndex());
	            break;	             
	        }
	         
	        case MotionEvent.ACTION_POINTER_DOWN:
	        {
	        	circleX = lastTouchedX;
	        	circleY = lastTouchedY;
	        	pointer2 = event.getPointerId(event.getActionIndex());
	        	sX = event.getX(event.findPointerIndex(pointer1));
	        	sY = event.getY(event.findPointerIndex(pointer1));
	        	fX = event.getX(event.findPointerIndex(pointer2));
	        	fY = event.getY(event.findPointerIndex(pointer2));
	        	break;
	        }
	         
	        case MotionEvent.ACTION_MOVE: 
	        {
	        	// Translate the matrix
	        	if(pointer2 == INVALID_POINTER_ID)
	        	{
		        	float dX = event.getX() - lastTouchedX;
	                float dY = event.getY() - lastTouchedY;
	                matrix.postTranslate(dX, dY);
	                lastTouchedX = event.getX();
	                lastTouchedY = event.getY();
	                circleX = event.getX();
		        	circleY = event.getY();
	        	}
                 
                // Get angle from rotating the matrix
                else if(pointer1 != INVALID_POINTER_ID && pointer2 != INVALID_POINTER_ID)
                {
                	//compute angle from gestures
                	float nfX, nfY, nsX, nsY;
                	float pivotX = gestureDetector.getFocusX();
                	float pivotY = gestureDetector.getFocusY();
                    nsX = event.getX(event.findPointerIndex(pointer1));
                    nsY = event.getY(event.findPointerIndex(pointer1));
                    nfX = event.getX(event.findPointerIndex(pointer2));
                    nfY = event.getY(event.findPointerIndex(pointer2));
                    mAngle = angleBetweenLines(fX, fY, sX, sY, nfX, nfY, nsX, nsY);
                    
                    // compute new coordinates for marker after rotation
                    float cAngle = (float) ((mAngle ) * (Math.PI/180)); // Convert to radians
                    float rotatedX = (float)(Math.cos(cAngle) * (circleX - pivotX) - Math.sin(cAngle) * (circleY-pivotY) + pivotX);
                    float rotatedY = (float)(Math.sin(cAngle) * (circleX - pivotX) + Math.cos(cAngle) * (circleY-pivotY) + pivotY);
                    circleX = rotatedX;
                    circleY = rotatedY;
                    // rotate matrix
                    matrix.postRotate(mAngle, pivotX, pivotY);
                }
	            break;
	         }
	         
	         case MotionEvent.ACTION_UP: 
	         {
	        	 circleX = event.getX();
	        	 circleY = event.getY();
	             pointer1 = INVALID_POINTER_ID;
	             break;
	         }
	         
	         case MotionEvent.ACTION_POINTER_UP: 
	         {
	        	 pointer2 = INVALID_POINTER_ID;
	             break;
	         }
         }
         setImageMatrix(matrix);
         invalidate();
         return true;
	 }
	 
	 // compute the angle between the pinches
	 private float angleBetweenLines (float fX, float fY, float sX, float sY, float nfX, float nfY, float nsX, float nsY)
	 {
		 float angle1 = (float) Math.atan2( (fY - sY), (fX - sX) );
		 float angle2 = (float) Math.atan2( (nfY - nsY), (nfX - nsX) );

		 float angle = ((float)Math.toDegrees(angle1 - angle2)) % 360;
		 if (angle < -180.f) angle += 360.0f;
		 if (angle > 180.f) angle -= 360.0f;
		 return angle;
	 }
	 
	 
	 private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener
	 {
		 @Override
		 public boolean onScale(ScaleGestureDetector detector) 
		 {			 
			 // get scale factor from focus point and post scale the matrix
			 float scaleFactor = detector.getScaleFactor();
			 float pivotX = detector.getFocusX();
			 float pivotY = detector.getFocusY();			 		 
			 matrix.postScale(scaleFactor, scaleFactor, pivotX, pivotY);
			 
			 // scale the circle size
			 circleSize = circleSize*scaleFactor;	
			 
			 // translate the circle with respect to the pivot point
			 circleX = pivotX + (circleX - pivotX) * scaleFactor;
			 circleY = pivotY + (circleY - pivotY) * scaleFactor;
			 return true;
		 }
	 }
}
