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
package cl.chileagil.agileday2012.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import cl.chileagil.agileday2012.R;
import cl.chileagil.agileday2012.activity.OpenSpaceDetailsActivity;
import cl.chileagil.agileday2012.db.DatabaseAdapter;
import cl.chileagil.agileday2012.fragment.adapter.CalendarOpenSpaceAdapter;
import cl.chileagil.agileday2012.fragment.listener.UIListenManager;
import cl.chileagil.agileday2012.fragment.listener.UpdateUIListener;

public class CalendarOpenSpaceFragment extends ListFragment implements UpdateUIListener {
    private final String TAG = getClass().getSimpleName();
 
    private DatabaseAdapter dbAdapter;
    private Cursor eventsCursor;
    private CalendarOpenSpaceAdapter sectorOpenSpaceAdapter;
    private long spaceId;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Conexion DB
        dbAdapter = new DatabaseAdapter(getActivity());
        dbAdapter.open();
        eventsCursor = dbAdapter.fetchEvents(this.spaceId);

        View v = inflater.inflate(R.layout.fragmentlist_calendar_openspace, container, false);
        
        UIListenManager.registerUIUPdater(spaceId, this);
        
        return v;
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        sectorOpenSpaceAdapter = new CalendarOpenSpaceAdapter(getActivity(), eventsCursor, eventsCursor.getCount());
        sectorOpenSpaceAdapter.setSpaceId(this.spaceId);
        setListAdapter(sectorOpenSpaceAdapter);
    }
    
    
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
    	super.onListItemClick(l, v, position, id);
    	
    	try {
    			Intent i = new Intent(this.getActivity(), OpenSpaceDetailsActivity.class);      
    			i.putExtra("id", id);
    			startActivity(i);
    	} catch (Exception e) {
    		Log.e(TAG, "Error obteniendo item", e);
		}
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView");
        dbClose();
        UIListenManager.unregisterUIUPdater(spaceId);
    }

    private void dbClose(){
        try {
            if(eventsCursor != null && !eventsCursor.isClosed()){
                eventsCursor.close();
            }
        } catch(Exception e){
            Log.e(TAG, "Error en close cursor.", e);
        }

        if(dbAdapter != null){
            try {
                dbAdapter.close();
            } catch(Exception e){
                Log.e(TAG, "Error en close db.", e);
            }
        }
    }
    
    public void setSpaceId(long spaceId) {
        this.spaceId = spaceId;
    }

    @Override
    public void update() {
        getActivity().runOnUiThread(new Runnable() {
            @SuppressWarnings("deprecation")
            @Override
            public void run() {
                sectorOpenSpaceAdapter.getCursor().requery();
                sectorOpenSpaceAdapter.notifyDataSetChanged();
            }
        });
        
    }
    
}

    
    
    
    

