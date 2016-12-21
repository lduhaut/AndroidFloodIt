package fr.ldu.android.floodit.image.service;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import fr.ldu.android.floodit.image.FloodItActivity;
import fr.ldu.android.floodit.image.util.LocalFloodItDb;

public class SaveAndGetScoresService extends IntentService {

	public SaveAndGetScoresService() {
		super(SaveAndGetScoresService.class.getSimpleName());
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		String name = intent.getStringExtra("name");
		int nbCol = intent.getIntExtra("nbCol", 0);
		int nbRows = intent.getIntExtra("nbRows", 0);
		int score = intent.getIntExtra("score", 0);
		
		LocalFloodItDb db = new LocalFloodItDb(this);
		db.open();
		
		// Enregistrement du score
		db.createScore(nbCol, nbRows, score, name);
		
		// Recuperation des scores
		Cursor c = db.getAllScoresAtLevel(nbCol, nbRows);
		
		int count = c.getCount();
		
		int[] nbRowsArray = new int[count];
		int[] nbColsArray = new int[count];
		int[] scoresArray = new int[count];
		String[] namesArray = new String[count];
		map2Arrays(c, nbCol, nbRows, nbColsArray, nbRowsArray, scoresArray, namesArray);
		
		db.close();
		
		Intent listeScoresIntent = new Intent(FloodItActivity.ACTION_AFFICHE_SCORES);
		listeScoresIntent.putExtra("scores", scoresArray);
		listeScoresIntent.putExtra("nbRows", nbRowsArray);
		listeScoresIntent.putExtra("nbCols", nbColsArray);
		listeScoresIntent.putExtra("names", namesArray);
		sendBroadcast(listeScoresIntent);
		
		stopSelf();
	}
	
	private void map2Arrays(Cursor c, int nbCol, int nbRow, int[] nbColsArray, int[] nbRowsArray,
			int[] scoresArray, String[] namesArray) {
		int i = 0;
		while (c.moveToNext()) {
			nbColsArray[i] = nbCol;
			nbRowsArray[i] = nbRow;
			scoresArray[i] = c.getInt(2);
			namesArray[i] = c.getString(3);
			
			i++;
		}
	}
}
