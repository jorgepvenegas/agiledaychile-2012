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

import cl.chileagil.agileday2012.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class OpenSpaceTutorialSlide extends Fragment {

    private int slide;
    
    public void setSlide(int slide) {
        this.slide = slide;
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        
        View v = null;
        
        switch (slide) {
        case 0:
            v = inflater.inflate(R.layout.openspace_tutorial_1, container, false);
            break;
            
        case 1:
            v = inflater.inflate(R.layout.openspace_tutorial_2, container, false);
            break;
            
        case 2:
            v = inflater.inflate(R.layout.openspace_tutorial_3, container, false);
            break;
            
        case 3:
            v = inflater.inflate(R.layout.openspace_tutorial_4, container, false);
            break;
            
        case 4:
            v = inflater.inflate(R.layout.openspace_tutorial_5, container, false);
            break;
            
        default:
            v = inflater.inflate(R.layout.openspace_tutorial_1, container, false);
            break;
        }
        
        
        return v;
    }
    
    
}
