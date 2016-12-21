package fr.ldu.android.floodit.image;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import android.app.ListActivity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import fr.ldu.android.floodit.R;
import fr.ldu.android.floodit.image.model.Level;
import fr.ldu.android.floodit.image.model.Score;
import fr.ldu.android.floodit.image.util.LocalFloodItDb;

public class ScoresActivity extends ListActivity {
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Puisque non créé par xml, ajustement de quelques propriétés graphiques
        final ListView listView = getListView();
        listView.setCacheColorHint(Color.BLACK);
        listView.setBackgroundColor(Color.BLACK);
        listView.setDivider(new ColorDrawable(Color.WHITE));
        listView.setDividerHeight(1);
        
        int[] nbCols;
        int[] nbRows;
        int[] scores;
        String[] names;
        Bundle extras = getIntent().getExtras();
        if (null == extras || extras.isEmpty()) {
        	// si pas d'extra, on va chercher tous les scores en base
        	
        	// Pas en asynchrone ... c'est mal !
        	LocalFloodItDb db = new LocalFloodItDb(this);
    		db.open();
        	Cursor c = db.getAllScores();
    		
    		int count = c.getCount();
    		
    		nbRows = new int[count];
    		nbCols = new int[count];
    		scores = new int[count];
    		names = new String[count];
    		
    		// l.nb_col, l.nb_rows, s.score, s.name
    		int i = 0;
    		while (c.moveToNext()) {
    			nbCols[i] = c.getInt(0);
    			nbRows[i] = c.getInt(1);
    			scores[i] = c.getInt(2);
    			names[i] = c.getString(3);
    			
    			i++;
    		}
    		
    		db.close();
        	
        } else {
        	nbCols = extras.getIntArray("nbCols");
        	nbRows = extras.getIntArray("nbRows");
        	scores = extras.getIntArray("scores");
        	names = extras.getStringArray("names");
        }
    	
    	// treemap pour que ce soit ordonné
    	Map<Level, List<Score>> scoresByLevel = new TreeMap<Level, List<Score>>();
    	if (nbCols.length > 0) {
    		Level lvl = new Level(nbRows[0], nbCols[0]);
    		scoresByLevel.put(lvl, new ArrayList<Score>());
    		List<Score> liste;
    		int index = 1;
    		for (int i = 0; i < nbCols.length; i++) {
    			int r = nbRows[i];
				int c = nbCols[i];
				if (lvl.getNbRows() != r && lvl.getNbCols() != c) {
    				lvl = new Level(r, c);
    				index = 1;
    			}
				liste = scoresByLevel.get(lvl);
				if (null == liste) {
					scoresByLevel.put(lvl, liste = new ArrayList<Score>());
				}
				
				liste.add(new Score(scores[i], names[i], index++));
    		}
    	}
    	
        setListAdapter(new ScoresListAdapter(this, scoresByLevel));
    }
    
    private static class ScoresListAdapter extends BaseAdapter {

    	private static final int ITEM_VIEW_TYPE_SCORE = 0;
        private static final int ITEM_VIEW_TYPE_LEVEL = ITEM_VIEW_TYPE_SCORE + 1;
        
        private static final int ITEM_VIEW_TYPE_COUNT = ITEM_VIEW_TYPE_LEVEL + 1;
		private static final String DOT = ".";
        
		private LayoutInflater mLayoutInflater;
		private Object[] objects;
		
		static class ViewHolder {
            TextView mTitleView;
            TextView mSubtitleView;
            TextView mPositionView;
        }

		public ScoresListAdapter(Context context,
				Map<Level, List<Score>> scoresByLevel) {
			mLayoutInflater = LayoutInflater.from(context);

			int numberOrRows = scoresByLevel.size();
			for (List<Score> liste : scoresByLevel.values()) {
				numberOrRows += liste.size();
			}
			
			int i = 0;
			objects = new Object[numberOrRows];
			for (Entry<Level, List<Score>> e : scoresByLevel.entrySet()) {
				objects[i++] = e.getKey();
				
				for (Score s : e.getValue()) {
					objects[i++] = s;
				}
			}
		}

		@Override
		public int getCount() {
			return objects.length;
		}
		
		@Override
		public int getViewTypeCount() {
			return ITEM_VIEW_TYPE_COUNT;
		}

		@Override
		public Object getItem(int i) {
			return objects[i];
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final int type = getItemViewType(position);
            ViewHolder viewHolder;

            if (convertView == null) {
                convertView = mLayoutInflater.inflate(type == ITEM_VIEW_TYPE_LEVEL ? R.layout.level_list_item
                        : R.layout.score_list_item, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.mTitleView = (TextView) convertView.findViewById(R.id.title);
                viewHolder.mSubtitleView = (TextView) convertView.findViewById(R.id.score);
                viewHolder.mPositionView = (TextView) convertView.findViewById(R.id.score_position);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            switch (type) {
                case ITEM_VIEW_TYPE_LEVEL:
                	final Level level = (Level) getItem(position);
                    viewHolder.mTitleView.setText(level.toString());
                    break;

                case ITEM_VIEW_TYPE_SCORE:
                    final Score score = (Score) getItem(position);
                    viewHolder.mTitleView.setText(score.getName());
                    viewHolder.mSubtitleView.setText("" + score.getScore());
                    viewHolder.mPositionView.setText(score.getPosition() + DOT);
            }

            return convertView;
		}
		
		@Override
		public int getItemViewType(int position) {
			Object item = getItem(position);
			if (item instanceof Level) {
				return ITEM_VIEW_TYPE_LEVEL;
			} else if (item instanceof Score) {
				return ITEM_VIEW_TYPE_SCORE;
			} else {
				return super.getItemViewType(position);
			}
		}
    	
		@Override
        public boolean isEnabled(int position) {
            return false;
        }

        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }
    }
}