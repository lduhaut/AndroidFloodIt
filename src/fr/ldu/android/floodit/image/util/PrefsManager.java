package fr.ldu.android.floodit.image.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import fr.ldu.android.floodit.image.FloodItActivity;

public class PrefsManager {

	private static final String KEY_NB_COLS = "nbCols";
	private static final String KEY_NB_ROWS = "nbRows";
	private static final String KEY_COLOR = "color";
	private static final String KEY_NAME = "nom";

//	private static final int[] DEFAULT_COLORS = {Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.CYAN, Color.MAGENTA};
	private static final int[] DEFAULT_COLORS = {-28160, -8704, -65505, -12713729, -15270144, -16736513};
	
	private static PrefsManager instance;
	
	private static int mNbOfRows, mNbOfCols;
	private static int[] mColors;
	private static String mName;
	private static SharedPreferences mSharedPrefs;
	private static Editor mEditor;
	
	private PrefsManager(Context context) {
		mColors = new int[FloodItActivity.NB_OF_COLORS];
		
		mSharedPrefs = context.getSharedPreferences(Constantes.FR_LDU_ANDROID_FLOODIT_PREFS, Context.MODE_PRIVATE);
		mEditor = mSharedPrefs.edit();
		
		mNbOfRows = mSharedPrefs.getInt(KEY_NB_ROWS, 15);
		mNbOfCols = mSharedPrefs.getInt(KEY_NB_COLS, 15);
		
		mName = mSharedPrefs.getString(KEY_NAME, "");
        
		for (int i = 0; i<FloodItActivity.NB_OF_COLORS; i++) {
			mColors[i] = mSharedPrefs.getInt(KEY_COLOR + i, DEFAULT_COLORS[i]);
		}
	}
	
	/**
	 * Retourne l'instance de PrefsManager. Si une instance a déjà été initialisée avec un contexte different, c'est l'ancien contexte qui sera utilisé.
	 * @param context
	 * @return
	 */
	public static PrefsManager getInstance(Context context) {
		if (null == instance) {
			instance = new PrefsManager(context);
		}
		return instance;
	}

	public int getNbOfRows() {
		return mNbOfRows;
	}
	
	public int getNbOfCols() {
		return mNbOfCols;
	}
	
	public void setDimensions(int nbOfCols, int nbOfRows) {
		mNbOfCols = nbOfCols;
		mNbOfRows = nbOfRows;
		
		mEditor.putInt(KEY_NB_COLS, mNbOfCols);
		mEditor.putInt(KEY_NB_ROWS, mNbOfRows);
		mEditor.commit();
	}
	
	public int getColor(int index) {
		return mColors[index];
	}
	
	public int[] getColors() {
		return mColors;
	}
	
	public void setColor(int index, int color) {
		mColors[index] = color;
		
		mEditor.putInt(KEY_COLOR + index, color);
		mEditor.commit();
	}
	
	public String getName() {
		return mName;
	}
	
	public void setName(String name) {
		mName = name;
		
		mEditor.putString(KEY_NAME, mName);
		mEditor.commit();
	}
}
