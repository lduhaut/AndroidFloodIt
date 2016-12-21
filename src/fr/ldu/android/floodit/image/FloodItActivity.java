package fr.ldu.android.floodit.image;

import java.util.Random;
import java.util.Stack;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import fr.ldu.android.floodit.R;
import fr.ldu.android.floodit.image.service.SaveAndGetScoresService;
import fr.ldu.android.floodit.image.util.ColorUtils;
import fr.ldu.android.floodit.image.util.PrefsManager;
import fr.ldu.android.floodit.image.view.FloodItImageView;

public class FloodItActivity extends Activity {
	
	public static final int RED = 0;
	public static final int GREEN = RED + 1;
	public static final int BLUE = GREEN + 1;
	public static final int YELLOW = BLUE + 1;
	public static final int CYAN = YELLOW + 1;
	public static final int PURPLE = CYAN + 1;
	
	public static final int NB_OF_COLORS = PURPLE + 1;
	
	private int[][] mModel;
	private int mNbCoups = 0;
	
	private FloodItImageView mImgView;
	private Button[] mBtnsCommande = new Button[NB_OF_COLORS];
	
	private EditText mEditTextName;
	
	public static final String ACTION_AFFICHE_SCORES = "fr.ldu.android.ACTION_AFFICHE_SCORES";
	public static final String ACTION_CHGT_DIFFICULTE = "fr.ldu.android.ACTION_CHGT_DIFFICULTE";
	private PrefsManager prefs;
	
	boolean ouvertureScore = false;
	private BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ACTION_AFFICHE_SCORES)) {
                
            	Intent ouvrirScores = new Intent(FloodItActivity.this, ScoresActivity.class);
            	ouvrirScores.putExtras(intent);
            	startActivity(ouvrirScores);
            	
            	ouvertureScore = true;
            }
        }
    };
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = PrefsManager.getInstance(this);
        reinit();
    }
    
    private void reinit() {
    	mNbCoups = 0;
    	
    	createModel();
    	initUI();
	}
    
    @Override
    protected void onResume() {
        super.onResume();
        // Register for Intent broadcasts
        IntentFilter filter = new IntentFilter(ACTION_AFFICHE_SCORES);
        registerReceiver(mIntentReceiver, filter);
        
        if (ouvertureScore) {
        	ouvertureScore = false;
        	reinit();
        }
    }

    @Override
    protected void onPause() {
        unregisterReceiver(mIntentReceiver);
        super.onPause();
    }
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
    	super.onConfigurationChanged(newConfig);
    	
    	initUI();
    }
    
    private void initUI() {
    	setContentView(R.layout.jeu_img);
    	
    	mImgView = (FloodItImageView) findViewById(R.id.imageView);
        mImgView.setModelAndColors(mModel, prefs.getColors());
        
        mBtnsCommande[0] = (Button) findViewById(R.id.btnRouge);
        mBtnsCommande[1] = (Button) findViewById(R.id.btnVert);
        mBtnsCommande[2] = (Button) findViewById(R.id.btnBleu);
        mBtnsCommande[3] = (Button) findViewById(R.id.btnYellow);
        mBtnsCommande[4] = (Button) findViewById(R.id.btnCyan);
        mBtnsCommande[5] = (Button) findViewById(R.id.btnPurple);
        
        for (Button b : mBtnsCommande) {
			b.setEnabled(true);
		}
        
        for (int i = 0; i < mBtnsCommande.length; i++) {
        	ColorUtils.renderButton(mBtnsCommande[i], prefs.getColor(i));
        }
    }

	private void createModel() {
		int nbOfRows = prefs.getNbOfRows();
		int nbOfCols = prefs.getNbOfCols();
		mModel = new int[nbOfRows][nbOfCols];
		
		Random rand = new Random();
		int randomMax = NB_OF_COLORS;
		
		for (int i = 0; i < nbOfRows; i++) {
			for (int j = 0; j < nbOfCols; j++) {
				mModel[i][j] = rand.nextInt(randomMax);
			}
		}
	}
	
	public void changeCouleur(View v) {
		mNbCoups++;
		
		int color = -1;
		
		switch (v.getId()) {
		case R.id.btnBleu:
			color = BLUE;
			break;
		case R.id.btnRouge:
			color = RED;
			break;
		case R.id.btnVert:
			color = GREEN;
			break;
		case R.id.btnYellow:
			color = YELLOW;
			break;
		case R.id.btnPurple:
			color = PURPLE;
			break;
		case R.id.btnCyan:
			color = CYAN;
			break;
		default:
			break;
		}
		
		if (color != -1) {
			jouer(color);
			
			mImgView.setModel(mModel);
		}
	}
	
	private void jouer(int newColor) {
		int firstColor = mModel[0][0];

		if (firstColor != newColor) {
			Stack<Integer> toBeChecked = new Stack<Integer>();
			
			final int nbOfRows = prefs.getNbOfRows();
			final int nbOfCols = prefs.getNbOfCols();
			
			toBeChecked.add(getTileId(nbOfCols, 0, 0));
			while (!toBeChecked.isEmpty()) {
				int idATraiter = toBeChecked.pop();
				
				int row = idATraiter/nbOfCols;
				int col = idATraiter%nbOfCols;
				
				if (mModel[row][col] != newColor) {
					mModel[row][col] = newColor;

					if (row < nbOfRows - 1
							&& firstColor == mModel[row + 1][col]) {
						toBeChecked.add(getTileId(nbOfCols, row + 1, col));
					}

					if (col < nbOfCols - 1
							&& firstColor == mModel[row][col + 1]) {
						toBeChecked.add(getTileId(nbOfCols, row, col + 1));
					}

					if (row > 0 && firstColor == mModel[row - 1][col]) {
						toBeChecked.add(getTileId(nbOfCols, row - 1, col));
					}

					if (col > 0 && firstColor == mModel[row][col - 1]) {
						toBeChecked.add(getTileId(nbOfCols, row, col - 1));
					}
				}
			}
		}
		
		if (checkVictoire()) sayYouWon();
	}
	
	private boolean checkVictoire() {
		int nbOfRows = prefs.getNbOfRows();
		int nbOfCols = prefs.getNbOfCols();
		
		int firstCol = mModel[0][0];
		for (int i = 0; i<nbOfRows; i++) {
			for (int j = 0; j<nbOfCols; j++) {
				if (mModel[i][j] != firstCol) return false;
			}
		}
		return true;
	}

	private int getTileId(int nbCol, int rowPos, int colPos) {
		
		return nbCol * rowPos + colPos;
	}
	
	private void sayYouWon() {
		for (Button b : mBtnsCommande) {
			b.setEnabled(false);
		}
		
		String text = getString(R.string.victoire, mNbCoups);
    	
    	final Dialog dialog = new Dialog(this);

    	dialog.setContentView(R.layout.victoire);
    	dialog.setTitle("You won!!");

    	final TextView textView = (TextView) dialog.findViewById(R.id.victoire_texte);
    	textView.setText(text);
    	
    	mEditTextName = (EditText) dialog.findViewById(R.id.victoire_name);
    	mEditTextName.setText(prefs.getName());
    	
    	Button btnValider = (Button) dialog.findViewById(R.id.victoire_btnValider);
    	btnValider.setOnClickListener(new View.OnClickListener() {
    		// On le copie là pour qu'il ne soit pas remis à 0 dans l'enregistrement.
    		int nbCoups = FloodItActivity.this.mNbCoups;
    		
			@Override
			public void onClick(View v) {
				Intent saveScoreIntent = new Intent(FloodItActivity.this, SaveAndGetScoresService.class);
				saveScoreIntent.putExtra("score", nbCoups);
				saveScoreIntent.putExtra("nbRows", prefs.getNbOfRows());
				saveScoreIntent.putExtra("nbCol", prefs.getNbOfCols());
				saveScoreIntent.putExtra("name", FloodItActivity.this.mEditTextName.getText().toString());
				startService(saveScoreIntent);
				
				dialog.dismiss();
			}
		});
    	
    	dialog.show();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menu, menu);
	    return true;
	}
	
	final static int REQ_CODE_PREFS = 1;
	@Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {

        switch (item.getItemId()) {
	        case R.id.menu_new_game:
	        	reinit();
	        	break;
            case R.id.menu_prefs:
                startActivityForResult(new Intent(this, PreferencesActivity.class),
                		REQ_CODE_PREFS);
                return true;
            case R.id.menu_scores:
                startActivity(new Intent(this, ScoresActivity.class));
                return true;
        }

        return super.onMenuItemSelected(featureId, item);
    }
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQ_CODE_PREFS && null != data) {
			
			// dans ce cas on a intent.action = ACTION_CHGT_DIFFICULTE
			
			if (data.getBooleanExtra("diff_changed", false)) {
				reinit();
			} else {
		        // on redessine pour prendre en compteles nouvelles couleurs
				initUI();
			}
		}
	}
}