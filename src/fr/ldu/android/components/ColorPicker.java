package fr.ldu.android.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.graphics.Paint.Style;
import android.view.MotionEvent;
import android.view.View;

public class ColorPicker extends View {

	private static final int COLOR_MAX = 255;
	private static final float PI = 3.1415926f;
	private static final float DEUX_PI = 2 * PI;
	private static final float DEUX_PI_SUR_3 = DEUX_PI / 3;
	private static final float QUATRE_PI_SUR_3 = 2 * DEUX_PI_SUR_3;

	private OnColorSelectedListener mListener;
	private int mCouleur;
	private final Paint mCenterPaint, mBlackPaint, mAroundCenterPaint, mColorRangePaint;
	private int mCenterRadius, mCenterX, mCenterY;
	int[] mColorRange;
	private boolean mPaintArountCenter;
	
	public ColorPicker(Context context, OnColorSelectedListener listener) {
		this(context, listener, Color.BLACK);
	}
	
	public ColorPicker(Context context, OnColorSelectedListener listener, int couleurInitiale) {
		super(context);
		mListener = listener;
		mCouleur = couleurInitiale;
		
		mCenterPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mAroundCenterPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mColorRangePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mBlackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mBlackPaint.setColor(Color.BLACK);
		
		mAroundCenterPaint.setStyle(Style.STROKE);
		mAroundCenterPaint.setStrokeWidth(convertToPixel(5));
		
		mColorRange = new int[] {
				Color.RED, Color.YELLOW, Color.GREEN, Color.CYAN, Color.BLUE, Color.MAGENTA, Color.RED
        };
        Shader s = new SweepGradient(0, 0, mColorRange, null);
        
        mColorRangePaint.setShader(s);
        
		mCenterRadius = convertToPixel(30);
		mCenterX = getWidth()/2;
		mCenterY = getHeight()/2;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// Zone de choix de la couleur.
		canvas.translate(mCenterX, mCenterY);
		canvas.drawRect(new Rect(-mCenterX, -mCenterY, mCenterX, mCenterY), mColorRangePaint); 
		
		// On evide le centre.
		canvas.translate(-mCenterX, -mCenterY);
		canvas.drawCircle(mCenterX, mCenterY, mCenterRadius + 20, mBlackPaint);
		
		// On dessine le bouton de sélection central
		mCenterPaint.setColor(mCouleur);
		canvas.drawCircle(mCenterX, mCenterY, mCenterRadius, mCenterPaint);
		
		// Si besoin, on dessine un contour du bouton central, lors de la sélection.
		if (mPaintArountCenter) {
			mAroundCenterPaint.setColor(mCouleur);
			canvas.drawCircle(mCenterX, mCenterY, mCenterRadius + 10, mAroundCenterPaint);
		}
		
		super.onDraw(canvas);
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		mCenterX = w/2;
		mCenterY = h/2;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX() - mCenterX;
        float y = event.getY() - mCenterY;
        boolean inCenter = Math.sqrt(x*x + y*y) <= mCenterRadius;
        mPaintArountCenter = false;
        
        switch (event.getAction()) {
        	case MotionEvent.ACTION_DOWN :
        		if (inCenter) {
        			mPaintArountCenter = true;
        			invalidate();
        			break;
        		}
	        case MotionEvent.ACTION_MOVE :
	        	if (!inCenter) {
		        	// Angle entre -Pi et Pi
	                float angle = (float)java.lang.Math.atan2(y, x);
	                if (angle < 0) {
	                	angle += 2*PI;
	                }
	                
	                mCouleur = calculateColor(angle);
	        	} else {
	        		mPaintArountCenter = true;
	        	}
	        	invalidate();
	            break;
            case MotionEvent.ACTION_UP :
                if (inCenter && null != mListener) {
                    mListener.colorSelected(mCenterPaint.getColor());
                }
                break;
        }
        return true;
	}
	
	/**
	 * A partir de l'angle entre 0 et 2Pi, calcule la couleur correspondante.
	 */
	private int calculateColor(float angle) {
		int red, green, blue;
		
		if (angle > DEUX_PI_SUR_3 && angle < QUATRE_PI_SUR_3) {
			red = 0;
		} else if (angle > QUATRE_PI_SUR_3) {
			red = Math.round((angle - QUATRE_PI_SUR_3) * COLOR_MAX / DEUX_PI_SUR_3);
		} else {
			red = Math.round((DEUX_PI_SUR_3 - angle) * COLOR_MAX / DEUX_PI_SUR_3);
		}
		
		if (angle > QUATRE_PI_SUR_3) {
			green = 0;
		} else {
			green = Math.round((DEUX_PI_SUR_3 - Math.abs(angle - DEUX_PI_SUR_3)) * COLOR_MAX / DEUX_PI_SUR_3);
		}
		
		if (angle < DEUX_PI_SUR_3) {
			blue = 0;
		} else {
			blue = Math.round((DEUX_PI_SUR_3 - Math.abs(angle - QUATRE_PI_SUR_3)) * COLOR_MAX / DEUX_PI_SUR_3);
		}
		
		// Maximisation de la luminosité
		float factor = 255f / Math.max(green, Math.max(red, blue));
		red = Math.round(red * factor);
		green = Math.round(green * factor);
		blue = Math.round(blue * factor);
		
		return Color.rgb(red, green, blue);
	}

	private int convertToPixel(int dp) {
		// Récupération de la densité de l'écran
		final float scale = getResources().getDisplayMetrics().density;
		// Conversion en pixels
		return (int) (dp * scale + 0.5f);
	}
	
	/**
	 * Interface du listener déclenché lorsque l'on "clique" sur le rond central.
	 */
	public interface OnColorSelectedListener {
        void colorSelected(int color);
    }
}
