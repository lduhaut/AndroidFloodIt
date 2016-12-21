package fr.ldu.android.floodit.image.util;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.widget.Button;

public class ColorUtils {

	private static final int COLOR_MAX_RVB = 255;
	private static final int COLOR_MIN_RVB = 0;
	private static final int COLOR_DELTA = 50;
	private static final int COLOR_HALF_DELTA = COLOR_DELTA/2;
	public static void renderButton(Button button, int color) {
		int red = Color.red(color);
		int green = Color.green(color);
		int blue = Color.blue(color);
		
		GradientDrawable background = new GradientDrawable(
				Orientation.TOP_BOTTOM, new int[]{
						getMaxColor(red, green, blue, COLOR_DELTA), 
						getMinColor(red, green, blue, COLOR_DELTA)});
		background.setCornerRadius(9);
		button.setBackgroundDrawable(background);
	}
	
	public static int getMaxColor(int color) {
		int red = Color.red(color);
		int green = Color.green(color);
		int blue = Color.blue(color);
		
		return getMaxColor(red, green, blue, COLOR_DELTA);
	}
	public static int getMinColor(int color) {
		int red = Color.red(color);
		int green = Color.green(color);
		int blue = Color.blue(color);
		
		return getMinColor(red, green, blue, COLOR_HALF_DELTA);
	}
	private static int getMaxColor(int r, int v, int b, int delta) {
		return Color.rgb(Math.min(COLOR_MAX_RVB, r + delta), Math.min(COLOR_MAX_RVB, v + delta), Math.min(COLOR_MAX_RVB, b + delta));
	}
	private static int getMinColor(int r, int v, int b, int delta) {
		return Color.rgb(Math.max(COLOR_MIN_RVB, r - delta), Math.max(COLOR_MIN_RVB, v - delta), Math.max(COLOR_MIN_RVB, b - delta));
	}
}
