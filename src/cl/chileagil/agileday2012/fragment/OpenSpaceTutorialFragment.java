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

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import cl.chileagil.agileday2012.R;

import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.PageIndicator;

public class OpenSpaceTutorialFragment extends FragmentActivity {
    
    private static final String TAG = "OpenSpaceTutorialFragment";
    
    private MyAdapter mAdapter;
    private ViewPager mPager;
    private PageIndicator mIndicator;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simple_circles);

        mAdapter = new MyAdapter(getSupportFragmentManager());

        mPager = (ViewPager)findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);

        mIndicator = (CirclePageIndicator)findViewById(R.id.indicator);
        mIndicator.setViewPager(mPager);
    }
    
private static int NUM_FRAGMENTS = 5;
    
    public class MyAdapter extends FragmentPagerAdapter {
        private static final int FRAGMENT_SPACE_1 = 0;
        private static final int FRAGMENT_SPACE_2 = 1;
        private static final int FRAGMENT_SPACE_3 = 2;
        private static final int FRAGMENT_SPACE_4 = 3;
        private static final int FRAGMENT_SPACE_5 = 4;
        
        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return NUM_FRAGMENTS;
        }

        @Override
        public Fragment getItem(int position) {
            Log.d(TAG, "getItem (Fragment:" + position);
            Fragment fragment = null;

            switch (position) {
            case FRAGMENT_SPACE_1:
                fragment = new OpenSpaceTutorialSlide();
                ((OpenSpaceTutorialSlide)fragment).setSlide(position);
                break;
                
            case FRAGMENT_SPACE_2:
                fragment = new OpenSpaceTutorialSlide();
                ((OpenSpaceTutorialSlide)fragment).setSlide(position);
                break;
                
            case FRAGMENT_SPACE_3:
                fragment = new OpenSpaceTutorialSlide();
                ((OpenSpaceTutorialSlide)fragment).setSlide(position);
                break;
                
            case FRAGMENT_SPACE_4:
                fragment = new OpenSpaceTutorialSlide();
                ((OpenSpaceTutorialSlide)fragment).setSlide(position);
                break;
                
            case FRAGMENT_SPACE_5:
                fragment = new OpenSpaceTutorialSlide();
                ((OpenSpaceTutorialSlide)fragment).setSlide(position);
                break;
                
            default:
                break;
            }
            return fragment;
        }
       

    }

}
