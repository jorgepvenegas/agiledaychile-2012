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
package cl.chileagil.agileday2012.fragment.listener;

import java.util.HashMap;
import java.util.Set;

import android.util.Log;


public class UIListenManager {

	private static HashMap<Long, UpdateUIListener> listener = new HashMap<Long, UpdateUIListener>();
	
	public static synchronized void registerUIUPdater(Long id, UpdateUIListener updater){
		
		listener.put(id, updater);
	}
	
	public static synchronized void unregisterUIUPdater(Long id){
		listener.remove(id);
	}
	
	public static synchronized void updateView(){
		Log.d("UIListenManager","updateView ");
		Set<Long> keySet = listener.keySet();
		for (Long id : keySet) {
			updateView(id);
		}
	}
	
	public static synchronized void updateView(Long id){
		UpdateUIListener updateUIListener = listener.get(id);
		if(updateUIListener != null){
			updateUIListener.update();
		}
	}
}
