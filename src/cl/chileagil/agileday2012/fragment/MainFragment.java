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

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import cl.chileagil.agileday2012.R;
import cl.chileagil.agileday2012.activity.UCentralActivity;
import cl.chileagil.agileday2012.db.DatabaseAdapter;
import cl.chileagil.agileday2012.fragment.listener.UIListenManager;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.darvds.ribbonmenu.RibbonMenuView;
import com.darvds.ribbonmenu.iRibbonMenuCallback;
import com.google.api.ClientCredentials;
import com.google.api.calendar.AsyncLoadEvents;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.GoogleHeaders;
import com.google.api.client.googleapis.extensions.android.accounts.GoogleAccountManager;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.googleapis.services.GoogleKeyInitializer;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.common.collect.Lists;
import com.viewpagerindicator.TabPageIndicator;

public class MainFragment extends SherlockFragmentActivity implements iRibbonMenuCallback {

    private static final String TAG = "MainFragment";
    public static int THEME = R.style.Theme_Sherlock;
    

    private RibbonMenuView rbmView;
    
    
    /*************
     * *****  VIENE DEL CALENDAR   ******
     * **********
     */
    /** Logging level for HTTP requests/responses. */
    private static final Level LOGGING_LEVEL = Level.OFF;

    private static final String AUTH_TOKEN_TYPE = "cl";

    private static final int REQUEST_AUTHENTICATE = 0;

    final HttpTransport transport = AndroidHttp.newCompatibleTransport();
    final JsonFactory jsonFactory = new GsonFactory();

    static final String PREF_ACCOUNT_NAME = "accountName";

    static final String PREF_AUTH_TOKEN = "authToken";

    GoogleAccountManager accountManager;

    SharedPreferences settings;

    String accountName;

    String authToken;

    //TODO mover/refactorizar
    public com.google.api.services.calendar.Calendar client;

    //TODO mover/refactorizar
    private boolean received401;
    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(THEME);
        
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.fragment_main);
       
        //#### RibbonMenu
        rbmView = (RibbonMenuView) findViewById(R.id.ribbonMenuView1);
        rbmView.setMenuClickCallback(this);
        rbmView.setMenuItems(R.menu.activity_main_ribbon);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        
        //## Fragment
        //Set the pager with an adapter
        ViewPager pager = (ViewPager)findViewById(R.id.pager);
        pager.setAdapter(new MyAdapter(getSupportFragmentManager()));

        //Bind the title indicator to the adapter
        TabPageIndicator indicator = (TabPageIndicator)findViewById(R.id.indicator);
        indicator.setViewPager(pager);
        
        //TODO temporal?
//        dbAdapter = new DatabaseAdapter(this);
//        dbAdapter.open();
//        createFakeData();
//        dbAdapter.close();
        
        //##### From Calendar
        HttpRequestInitializer requestInitializer = new HttpRequestInitializer() {
            public void initialize(HttpRequest request) throws IOException {
                request.getHeaders().setAuthorization(GoogleHeaders.getGoogleLoginValue(authToken));
            }
        };

        client = new com.google.api.services.calendar.
                Calendar.Builder(transport, jsonFactory, requestInitializer)
        .setApplicationName("Google-CalendarAndroidSample/1.0")
        .setJsonHttpRequestInitializer(new GoogleKeyInitializer(ClientCredentials.KEY))
        .build();

        settings = getPreferences(MODE_PRIVATE);
        accountName = settings.getString(PREF_ACCOUNT_NAME, null);
        authToken = settings.getString(PREF_AUTH_TOKEN, null);
        Logger.getLogger("com.google.api.client").setLevel(LOGGING_LEVEL);
        accountManager = new GoogleAccountManager(this);
        
        gotAccount();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getSupportMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    void gotAccount() {
        Account account = accountManager.getAccountByName(accountName);
        if (account == null) {
            chooseAccount();
            return;
        }
        if (authToken != null) {
            //Ya tengo elegido mi cuenta.
            //Solo si no tengo datos en la DB, lo pido, sino cargo lo que hay
            //y actualizo solo a peticion del usuario
            DatabaseAdapter dbAdapter = null;
            try {
                dbAdapter = new DatabaseAdapter(this);
                dbAdapter.open();
                if(dbAdapter.fetchCountEvents() <= 0){
                    onAuthToken();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    dbAdapter.close();
                } catch(Exception e){
                    e.printStackTrace();
                }
            }
            return;
        }
        accountManager.getAccountManager()
        .getAuthToken(account, AUTH_TOKEN_TYPE, true, new AccountManagerCallback<Bundle>() {

            public void run(AccountManagerFuture<Bundle> future) {
                try {
                    Bundle bundle = future.getResult();
                    if (bundle.containsKey(AccountManager.KEY_INTENT)) {
                        Intent intent = bundle.getParcelable(AccountManager.KEY_INTENT);
                        intent.setFlags(intent.getFlags() & ~Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivityForResult(intent, REQUEST_AUTHENTICATE);
                    } else if (bundle.containsKey(AccountManager.KEY_AUTHTOKEN)) {
                        setAuthToken(bundle.getString(AccountManager.KEY_AUTHTOKEN));
                        onAuthToken();
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);
                }
            }
        }, null);
    }

    private void chooseAccount() {
        accountManager.getAccountManager().getAuthTokenByFeatures(GoogleAccountManager.ACCOUNT_TYPE,
                AUTH_TOKEN_TYPE,
                null,
                MainFragment.this,
                null,
                null,
                new AccountManagerCallback<Bundle>() {

            public void run(AccountManagerFuture<Bundle> future) {
                Bundle bundle;
                try {
                    bundle = future.getResult();
                    setAccountName(bundle.getString(AccountManager.KEY_ACCOUNT_NAME));
                    setAuthToken(bundle.getString(AccountManager.KEY_AUTHTOKEN));
                    onAuthToken();
                } catch (OperationCanceledException e) {
                    // user canceled
                } catch (AuthenticatorException e) {
                    Log.e(TAG, e.getMessage(), e);
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage(), e);
                }
            }
        },
        null);
    }
    
    void onAuthToken() {
    	//new AsyncLoadCalendars(this).execute();
        new AsyncLoadEvents(this).execute();
    }

    void setAccountName(String accountName) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(PREF_ACCOUNT_NAME, accountName);
        editor.commit();
        this.accountName = accountName;
    }

    void setAuthToken(String authToken) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(PREF_AUTH_TOKEN, authToken);
        editor.commit();
        this.authToken = authToken;
    }
    
    /*********************************************************************/
    //TODO Lo siguiente tal vez debiera depender de implementar una interfaz
    public void onRequestCompleted() {
        received401 = false;
    }

    public void handleGoogleException(final IOException e) {
        if (e instanceof GoogleJsonResponseException) {
            GoogleJsonResponseException exception = (GoogleJsonResponseException) e;
            if (exception.getStatusCode() == 401 && !received401) {
                received401 = true;
                accountManager.invalidateAuthToken(authToken);
                authToken = null;
                SharedPreferences.Editor editor2 = settings.edit();
                editor2.remove(PREF_AUTH_TOKEN);
                editor2.commit();
                gotAccount();
            }
        }
        Log.e(TAG, e.getMessage(), e);
//        runOnUiThread(new Runnable() {
//            public void run() {
//                new AlertDialog.Builder(MainFragment.this).setTitle("Exception").setMessage(
//                        e.getMessage()).setNeutralButton("ok", null).create().show();
//            }
//        });
    }

    public void refresh() {
        //Collections.sort(calendars);
        //setListAdapter(
         //       new ArrayAdapter<CalendarInfo>(this, android.R.layout.simple_list_item_1, calendars));
    
        UIListenManager.updateView();
    }


    /*********************************************************************/
    
    
    @Override
    public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {

        switch (item.getItemId()) {
        case android.R.id.home:
            rbmView.toggleMenu();
            return true;
            
        case R.id.menu_refresh:
            setProgressBarIndeterminateVisibility(Boolean.TRUE); 
            onAuthToken();
            return true;
            
            
        }
        
        return super.onOptionsItemSelected(item);
        
            
            
    }
    

    @Override
    public void RibbonMenuItemClick(int itemId) {
        Log.d(TAG, "RibbonMenuItemClick:" + itemId);
        
        switch (itemId) {
            case R.id.ribbon_openspace_reglas:
                
                Intent i = new Intent(MainFragment.this, OpenSpaceTutorialFragment.class);
                startActivity(i);
            
            break;
            
            case R.id.ribbon_mapa:
                
                i = new Intent(MainFragment.this, UCentralActivity.class);
                startActivity(i);
            
            break;
            
            case R.id.ribbon_postit_3m:
                
            	Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.post-it.cl"));
            	startActivity(browserIntent);
            
            break;
            
        }
    }
    
    private static int NUM_FRAGMENTS = 7;
    
    public class MyAdapter extends FragmentPagerAdapter {
    	private static final int FRAGMENT_PROGRAMA = 0;
        private static final int FRAGMENT_SPACE_1 = 1;
        private static final int FRAGMENT_SPACE_2 = 2;
        private static final int FRAGMENT_SPACE_3 = 3;
        private static final int FRAGMENT_SPACE_4 = 4;
        private static final int FRAGMENT_SPACE_5 = 5;
        private static final int FRAGMENT_SPACE_6 = 6;
        
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
            
            case FRAGMENT_PROGRAMA:
                fragment = new CalendarOpenSpaceFragment();
                ((CalendarOpenSpaceFragment)fragment).setSpaceId(position);
                break;
                
            case FRAGMENT_SPACE_1:
                fragment = new CalendarOpenSpaceFragment();
                ((CalendarOpenSpaceFragment)fragment).setSpaceId(position);
                break;
                
            case FRAGMENT_SPACE_2:
                fragment = new CalendarOpenSpaceFragment();
                ((CalendarOpenSpaceFragment)fragment).setSpaceId(position);
                break;
                
            case FRAGMENT_SPACE_3:
                fragment = new CalendarOpenSpaceFragment();
                ((CalendarOpenSpaceFragment)fragment).setSpaceId(position);
                break;
                
            case FRAGMENT_SPACE_4:
                fragment = new CalendarOpenSpaceFragment();
                ((CalendarOpenSpaceFragment)fragment).setSpaceId(position);
                break;
                
            case FRAGMENT_SPACE_5:
                fragment = new CalendarOpenSpaceFragment();
                ((CalendarOpenSpaceFragment)fragment).setSpaceId(position);
                break;
                
            case FRAGMENT_SPACE_6:
                fragment = new CalendarOpenSpaceFragment();
                ((CalendarOpenSpaceFragment)fragment).setSpaceId(position);
                break;

            default:
                break;
            }
            return fragment;
        }
        
        @Override
        public CharSequence getPageTitle(int position) {
            String fragment = null;

            switch (position) {
            
            case FRAGMENT_PROGRAMA:
                fragment = "Programa";
                break;
                
            case FRAGMENT_SPACE_1:
                fragment = "Open Space 1";
                break;
                
            case FRAGMENT_SPACE_2:
                fragment = "Open Space 2";
                break;
                
            case FRAGMENT_SPACE_3:
                fragment = "Open Space 3";
                break;
                
            case FRAGMENT_SPACE_4:
                fragment = "Open Space 4";
                break;
                
            case FRAGMENT_SPACE_5:
                fragment = "Open Space 5";
                break;
                
            case FRAGMENT_SPACE_6:
                fragment = "Open Space 6";
                break;

            default:
                break;
            }
            return fragment;
        }

    }

}
