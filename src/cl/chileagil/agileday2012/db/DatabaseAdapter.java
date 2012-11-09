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
package cl.chileagil.agileday2012.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseAdapter {

    private final static String TAG = "DatabaseAdapter";

    private SQLiteDatabase mDb;
    private final Context mCtx;
    private DatabaseHelper mDbHelper;

    private static final String DB_NAME = "agileday2012_004";
    private static final int DB_VERSION = 2;

    public static final String TABLE_NAME_EVENTS = "calendar";
    public static final String KEY_EVENTS_ROWID = "_id";
    public static final String KEY_EVENTS_SPACE_ID = "space_id";
    public static final String KEY_EVENTS_SPACE_HOUR = "hour";
    public static final String KEY_EVENTS_SPACE_SUMMARY = "summary";
    public static final String KEY_EVENTS_SPACE_DESCRIPTION = "description";

    private static final String CREATE_TABLE_EVENTS = 
            "create table IF NOT EXISTS " + TABLE_NAME_EVENTS + " (" + 
                    KEY_EVENTS_ROWID + " integer primary key autoincrement, " +
                    KEY_EVENTS_SPACE_ID + " integer, " +
                    KEY_EVENTS_SPACE_HOUR + " text, " +
                    KEY_EVENTS_SPACE_SUMMARY + " text, " +
                    KEY_EVENTS_SPACE_DESCRIPTION + " text);";

    private static class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE_EVENTS);

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVers, int newVers) {
            db.execSQL("DROP TABLE IF EXISTS " + CREATE_TABLE_EVENTS);
            onCreate(db);
        }
    }

    public DatabaseAdapter(Context context) {
        this.mCtx = context;
    }

    public DatabaseAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        mDbHelper.onCreate(mDb);
        return this;
    }

    public void close() {
        if (mDb.isOpen()) {
            mDbHelper.close();
        }
    }

    public long createEvent(long spaceId, String hour, String summary, String description) {

        Log.d(TAG, "## createEvent:" + spaceId + " " + hour + " " + summary);
        
        long rv = -1;
        if (mDb == null || !mDb.isOpen()) {
            return rv;
        }

        ContentValues storeValues = new ContentValues();

        storeValues.put(KEY_EVENTS_SPACE_ID, spaceId);
        storeValues.put(KEY_EVENTS_SPACE_HOUR, hour);
        storeValues.put(KEY_EVENTS_SPACE_SUMMARY, summary);
        storeValues.put(KEY_EVENTS_SPACE_DESCRIPTION, description);

        try {
            rv = mDb.insert(TABLE_NAME_EVENTS, null, storeValues);
        } catch (Exception e) {
            Log.e(TAG, "Exception creating event ", e);
        }
        return rv;
    }

    public Cursor fetchEvents(long spaceId) {
        
        Log.d(TAG, "### fetchEvents: " + spaceId);
        
        if (mDb == null || !mDb.isOpen()) {
            return null;
        }

        Cursor cursor = mDb.query(TABLE_NAME_EVENTS, new String[] { KEY_EVENTS_ROWID, KEY_EVENTS_SPACE_HOUR, KEY_EVENTS_SPACE_SUMMARY,
                KEY_EVENTS_SPACE_DESCRIPTION }, KEY_EVENTS_SPACE_ID + "= ?", new String[] { String.valueOf(spaceId) }, null, null,
                KEY_EVENTS_SPACE_HOUR + " ASC");
        return cursor;
    }
    
    public Cursor fetchEvent(long rowId) {

    	Log.d(TAG, "### fetchEvents: " + rowId);

    	if (mDb == null || !mDb.isOpen()) {
    		return null;
    	}

    	Cursor cursor = mDb.query(TABLE_NAME_EVENTS, new String[] { KEY_EVENTS_ROWID, KEY_EVENTS_SPACE_HOUR, KEY_EVENTS_SPACE_SUMMARY,
    			KEY_EVENTS_SPACE_DESCRIPTION }, KEY_EVENTS_ROWID + "= ?", new String[] { String.valueOf(rowId) }, null, null,
    			KEY_EVENTS_SPACE_HOUR + " ASC");

    	return cursor;
    }
    
    public int fetchCountEvents() {
        if (mDb == null || !mDb.isOpen()) {
            return -1;
        }

        Cursor cursor = mDb.query(TABLE_NAME_EVENTS, new String[] { KEY_EVENTS_ROWID, KEY_EVENTS_SPACE_HOUR, KEY_EVENTS_SPACE_SUMMARY,
                KEY_EVENTS_SPACE_DESCRIPTION }, null, null, null, null,
                KEY_EVENTS_SPACE_HOUR + " ASC");
        if(cursor != null){
            return cursor.getCount();
        } else {
            return 0;
        }
    }
    
    public void deleteEvents(long spaceId) {
        if (mDb != null || mDb.isOpen()) {
            Log.d(TAG," SQL: Delete Calendar from sectorId:" + spaceId);
            mDb.delete(TABLE_NAME_EVENTS, KEY_EVENTS_SPACE_ID + "= ?", new String[] { String.valueOf(spaceId) });  
        }
        
    }

}
