/*
 * Copyright (c) 2012 Marcelo Vega
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package cl.chileagil.agileday2012.activity;

import com.actionbarsherlock.app.SherlockActivity;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import cl.chileagil.agileday2012.R;
import cl.chileagil.agileday2012.db.DatabaseAdapter;
import cl.chileagil.agileday2012.fragment.listener.UIListenManager;

/**
 * 
 * @author mvega
 *
 */
public class OpenSpaceDetailsActivity extends SherlockActivity {

	private final String TAG = getClass().getSimpleName();

	public static int THEME = R.style.Theme_Sherlock_Light_DarkActionBar;
	
	private DatabaseAdapter dbAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(THEME);
		
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_openspace_details);

		dbAdapter = new DatabaseAdapter(this);
		dbAdapter.open();

		if(getIntent().hasExtra("id")){
			
			Cursor c = dbAdapter.fetchEvent(getIntent().getLongExtra("id", -1));
			if(c != null && c.getCount() == 1){
				c.moveToFirst();
				String hour = c.getString(c.getColumnIndex(DatabaseAdapter.KEY_EVENTS_SPACE_HOUR));
			    String summary = c.getString(c.getColumnIndex(DatabaseAdapter.KEY_EVENTS_SPACE_SUMMARY));
			    String description = c.getString(c.getColumnIndex(DatabaseAdapter.KEY_EVENTS_SPACE_DESCRIPTION));
			    
			    TextView tvTitle = (TextView)findViewById(R.id.tv_space_details_title);
			    TextView tvHour = (TextView)findViewById(R.id.tv_space_details_hour);
			    TextView tvDescription = (TextView)findViewById(R.id.tv_space_details_description);
			    
			    
			    tvTitle.setText(summary);
			    tvHour.setText(hour);
			    tvDescription.setText(description);
			}
			
		} else {
			finish();
			return;
		}
		

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if(dbAdapter != null){
			try {
				dbAdapter.close();
			} catch(Exception e){
				Log.e(TAG, "Error en close db.", e);
			}
		}
	}


}
