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

import android.os.Bundle;
import cl.chileagil.agileday2012.R;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;

public class UCentralActivity extends MapActivity {

	private static final double LATITUD = -33.452639;
	private static final double LONGITUD = -70.651496;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		
		setContentView(R.layout.activity_ucentral);
	
		MapView mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);
		
		MapController mc = mapView.getController();
		
		GeoPoint point = null;
		point = new GeoPoint((int)(LATITUD*1000000),(int)(LONGITUD*1000000));
		mc.setCenter(point);
		mc.setZoom(20);
		
	}
	
	

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
	
}
