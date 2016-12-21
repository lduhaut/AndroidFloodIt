package fr.ldu.android.floodit.image;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import fr.ldu.android.components.ColorPicker;
import fr.ldu.android.components.ColorPicker.OnColorSelectedListener;
import fr.ldu.android.floodit.R;
import fr.ldu.android.floodit.image.util.ColorUtils;
import fr.ldu.android.floodit.image.util.PrefsManager;

public class PreferencesActivity extends Activity {
	
	RadioGroup mRadioGroup;
	RadioButton mRadioDiffPerso, mRadioFacile, mRadioMoyen, mRadioDiff, mRadioTresDiff, mRadioMaster;
	EditText mEditNbRows, mEditNbCols, mName;
	Button mValider;
	int nbRowInitial, nbColInitial;
	Dialog mColorPicker;
	Button[] mButtonColors = new Button[FloodItActivity.NB_OF_COLORS];
	
	private final PrefsManager prefs = PrefsManager.getInstance(this);
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prefs);
        
        mRadioGroup = (RadioGroup) findViewById(R.id.diffRadioGroup);
        mRadioDiffPerso = (RadioButton) findViewById(R.id.radioDiffPerso);
        mRadioFacile = (RadioButton) findViewById(R.id.radioFacile);
        mRadioMoyen = (RadioButton) findViewById(R.id.radioMoyen);
        mRadioDiff = (RadioButton) findViewById(R.id.radioDiff);
        mRadioTresDiff = (RadioButton) findViewById(R.id.radioTresDiff);
        mRadioMaster = (RadioButton) findViewById(R.id.radioMaster);
        mEditNbRows = (EditText) findViewById(R.id.prefsNbRows);
        mEditNbCols = (EditText) findViewById(R.id.prefsNbCols);
        mValider = (Button) findViewById(R.id.btnValiderPrefs);
        mName = (EditText) findViewById(R.id.prefs_name);
        mName.setText(prefs.getName());
        
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				mEditNbCols.setEnabled(checkedId == R.id.radioDiffPerso);
				mEditNbRows.setEnabled(checkedId == R.id.radioDiffPerso);
			}
		});
        
        mValider.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int nbRowsChecked = getNbRowsChecked();
				int nbColsChecked = getNbColsChecked();
				
				if (validate(nbRowsChecked) && validate(nbColsChecked)) {
					prefs.setName(mName.getText().toString());
					
					prefs.setDimensions(nbColsChecked, nbRowsChecked);
					
					Intent res = new Intent(FloodItActivity.ACTION_CHGT_DIFFICULTE);
					res.putExtra("diff_changed", nbColsChecked != nbColInitial || nbRowsChecked != nbRowInitial);
					PreferencesActivity.this.setResult(RESULT_OK, res);
					
					PreferencesActivity.this.finish();
				}
			}

			private boolean validate(int nbChecked) {
				if (nbChecked < 0 || nbChecked > 200) {
					
					Toast toast = Toast.makeText(PreferencesActivity.this, "Les dimensions personnalisées doivent être comprises entre 1 et 200", Toast.LENGTH_LONG);
					toast.show();
					
					return false;
				}
				return true;
			}

		});
        
        int nbRows = prefs.getNbOfRows();
        int nbCols = prefs.getNbOfCols();
        nbRowInitial = nbRows;
        nbColInitial = nbCols;
        
        mButtonColors[0] = (Button) findViewById(R.id.prefsBtn0);
        mButtonColors[1] = (Button) findViewById(R.id.prefsBtn1);
        mButtonColors[2] = (Button) findViewById(R.id.prefsBtn2);
        mButtonColors[3] = (Button) findViewById(R.id.prefsBtn3);
        mButtonColors[4] = (Button) findViewById(R.id.prefsBtn4);
        mButtonColors[5] = (Button) findViewById(R.id.prefsBtn5);
        
        for (int i = 0; i < mButtonColors.length; i++) {
        	ColorUtils.renderButton(mButtonColors[i], prefs.getColor(i));
        }
        
        if (nbRows == nbCols) {
        	switch (nbRows) {
        	case 5 : mRadioGroup.check(R.id.radioFacile);break;
        	case 15 : mRadioGroup.check(R.id.radioMoyen);break;
        	case 30 : mRadioGroup.check(R.id.radioDiff);break;
        	case 50 : mRadioGroup.check(R.id.radioTresDiff);break;
        	case 100 : mRadioGroup.check(R.id.radioMaster);break;
        	default: mRadioGroup.check(R.id.radioDiffPerso);
	        	mEditNbCols.setText("" + nbCols);
	        	mEditNbRows.setText("" + nbRows);
	        	break;
        	}
        	
        } else {
        	mRadioGroup.check(R.id.radioDiffPerso);
        	mEditNbCols.setText("" + nbCols);
        	mEditNbRows.setText("" + nbRows);
        }
    }
    
	private int getNbRowsChecked() {
		int idChecked = mRadioGroup.getCheckedRadioButtonId();
		
		if (idChecked == R.id.radioFacile) {
			return 5;
		} else if (idChecked == R.id.radioMoyen) {
			return 15;
		} else if (idChecked == R.id.radioDiff) {
			return 30;
		} else if (idChecked == R.id.radioTresDiff) {
			return 50;
		} else if (idChecked == R.id.radioMaster) {
			return 100;
		} else if (idChecked == R.id.radioDiffPerso) {
			 String val = mEditNbRows.getText().toString();
			 return val.length() == 0 ? -1 : Integer.parseInt(val);
		} 
		return 15;
	}
	
	private int getNbColsChecked() {
		int idChecked = mRadioGroup.getCheckedRadioButtonId();
		
		if (idChecked == R.id.radioDiffPerso) {
			 String val = mEditNbCols.getText().toString();
			 return val.length() == 0 ? -1 : Integer.parseInt(val);
		} else {
			return getNbRowsChecked();
		}
	}
	
	private static boolean isShowingColorPicker;
	@Override
	protected void onPause() {
		super.onPause();
		Log.i("LDU", "onpause");
		if (null != mColorPicker && mColorPicker.isShowing()) {
			Log.i("LDU", "onpause");
			mColorPicker.dismiss();
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		Log.i("LDU", "onresume " + mIndexButtonClicked);
		if (isShowingColorPicker) {
			initCOlorPicker();
			mColorPicker.show();
		}
	}
	
	static int mIndexButtonClicked = -1;
	public void changeCouleur(View v) {
		
		switch (v.getId()) {
			case R.id.prefsBtn0 : mIndexButtonClicked = 0; break;
			case R.id.prefsBtn1 : mIndexButtonClicked = 1; break;
			case R.id.prefsBtn2 : mIndexButtonClicked = 2; break;
			case R.id.prefsBtn3 : mIndexButtonClicked = 3; break;
			case R.id.prefsBtn4 : mIndexButtonClicked = 4; break;
			case R.id.prefsBtn5 : mIndexButtonClicked = 5; break;
			default : break;
		}
		
		if (mColorPicker == null) {
			initCOlorPicker();
		}
		isShowingColorPicker = true;
		mColorPicker.show();
	}
	
	private void initCOlorPicker() {
		mColorPicker = new Dialog(this);
		mColorPicker.setTitle(R.string.choisirCouleur);
		mColorPicker.setContentView(new ColorPicker(this,
				new OnColorSelectedListener() {
					@Override
					public void colorSelected(int color) {
						PreferencesActivity.this.majCouleur(color);
						mColorPicker.dismiss();
						isShowingColorPicker = false;
					}
				}, prefs.getColor(mIndexButtonClicked)));
	}

	private void majCouleur(int color) {
		ColorUtils.renderButton(mButtonColors[mIndexButtonClicked], color);
		
		prefs.setColor(mIndexButtonClicked, color);
		
		mIndexButtonClicked = -1;
	}
}