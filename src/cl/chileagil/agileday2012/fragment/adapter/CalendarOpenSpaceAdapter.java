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
package cl.chileagil.agileday2012.fragment.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import cl.chileagil.agileday2012.R;
import cl.chileagil.agileday2012.db.DatabaseAdapter;

public class CalendarOpenSpaceAdapter  extends CursorAdapter {
    
	private static final int PROGRAMA = 0;
	
	private final String TAG = getClass().getSimpleName();

    private final LayoutInflater mInflater;
    private long spaceId;
    
    public CalendarOpenSpaceAdapter(Context context, Cursor c, int timelineCount) {
        super(context, c);
        mInflater = LayoutInflater.from(context);

    }
    
    public void setSpaceId(long spaceId) {
		this.spaceId = spaceId;
	}
    
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
    	
    	final View view;

    	/*
    	 * Hago una diferencia entre el layout del programa y de de los Spaces
    	 * En el programa permito que el auto dependa del texto, de modo que siempre pueda leerse
    	 * En los Space limito el tamaño a la hora, de modo que independiente del texto, siempre
    	 * pueda tener alineados los horarios (a menos que en el calendar esten desordenados)
    	 * 
    	 */
    	if(this.spaceId == PROGRAMA){
    		view = mInflater.inflate(R.layout.fragmentlist_calendar_openspace_programa_element, parent, false);
    	} else {
    		view = mInflater.inflate(R.layout.fragmentlist_calendar_openspace_element, parent, false);
    	}
        
        return view;
    }
    
    @Override
    public void bindView(View timelineView, Context context, Cursor cursor) {

        final String hour = cursor.getString(cursor.getColumnIndex(DatabaseAdapter.KEY_EVENTS_SPACE_HOUR));
        final String summary = cursor.getString(cursor.getColumnIndex(DatabaseAdapter.KEY_EVENTS_SPACE_SUMMARY));


        TextView txHour = (TextView) timelineView.findViewById(R.id.textSectorElementHour);
        TextView txSummary = (TextView) timelineView.findViewById(R.id.textSectorElementSummary);
        
        txHour.setText(hour);
        txSummary.setText(summary);
    }

}
