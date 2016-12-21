package fr.ldu.android.floodit.image.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;
import fr.ldu.android.floodit.image.FloodItActivity;
import fr.ldu.android.floodit.image.util.ColorUtils;

public class FloodItImageView extends View {

	private final Paint mRectPaint = new Paint();
	private int[][] mModel;
	private LinearGradient[] gradients = new LinearGradient[FloodItActivity.NB_OF_COLORS];
	private int[] colors;
	
	public FloodItImageView(Context context) {
        this(context, null);
    }

    public FloodItImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FloodItImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDraw(Canvas canvas) {
    	super.onDraw(canvas);
    	if (null != mModel) {
    		int nbLignes = mModel.length;
    		int nbColonnes = mModel[0].length;
    		
    		int width = getWidth();
    		if (0 != width) {
    			int height = getHeight();
    			int tileWidth = width / nbColonnes;
    			int tileHeight = height / nbLignes;
    			
    			// Calcul pour dessiner au centre du canvas
    			int margeGauche = (width - tileWidth * nbColonnes) / 2;
    			int margeHaute = (height - tileHeight * nbLignes) / 2;
    			
    			RectF rect = new RectF();
    			float cornerX = tileWidth/3;
    			float cornerY = tileHeight/3;
    			mRectPaint.setAntiAlias(cornerX >=1 && cornerY >=1);
    			
    			for (int i = 0; i<nbLignes; i++) {
    				float top = margeHaute + i*tileHeight;
    				float bot = top + tileHeight;
    				
    				for (int j = 0; j<nbColonnes; j++) {
    					
    					if (nbLignes <= 50) {
    						mRectPaint.setShader(getShader(mModel[i][j], tileHeight));
    					} else {
    						mRectPaint.setColor(colors[mModel[i][j]]);
    					}
    					
    					rect.set(margeGauche + j*tileWidth, top, margeGauche + (j+1)*tileWidth, bot);
    					if (mRectPaint.isAntiAlias()) {
    						canvas.drawRoundRect(rect , cornerX, cornerY, mRectPaint);
    					} else {
    						canvas.drawRect(rect, mRectPaint);
    					}
    				}
    			}
    		} 
    	}
    	
    }
    
	private Shader getShader(int i, int height) {
		LinearGradient lg = gradients[i];
		if (null == lg) {
			int color = colors[i];
			
			lg = new LinearGradient(0, height, 0, 0, ColorUtils.getMaxColor(color), 
					ColorUtils.getMinColor(color), Shader.TileMode.MIRROR);
			gradients[i] = lg;
		}
		return lg;
	}

//	private int getColor(int codeCol) {
//		switch (codeCol) {
//		case FloodItActivity.RED:
//			return Color.RED;
//		case FloodItActivity.GREEN:
//			return Color.GREEN;
//		case FloodItActivity.BLUE:
//			return Color.BLUE;
//		case FloodItActivity.YELLOW:
//			return Color.YELLOW;
//		case FloodItActivity.CYAN:
//			return Color.CYAN;
//		case FloodItActivity.PURPLE:
//			return Color.MAGENTA;
//		default:
//			break;
//		}
//
//		return -1;
//	}

	public void setModel(int[][] in_model) {
    	this.mModel = in_model;
    	this.invalidate();
    }

	public void setModelAndColors(int[][] in_model, int[] colors) {
    	this.mModel = in_model;
    	this.colors = colors;
    	this.invalidate();
    }
}
