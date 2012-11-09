/*
 * Copyright (c) 2012 Google Inc.
 * Copyright (C) 2012 Marcelo Vega
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.api.calendar;

import java.io.IOException;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import cl.chileagil.agileday2012.db.DatabaseAdapter;
import cl.chileagil.agileday2012.fragment.MainFragment;

import com.google.api.services.calendar.Calendar.Calendars.Get;
import com.google.api.services.calendar.Calendar.Events.List;
import com.google.api.services.calendar.model.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.Events;

public class AsyncLoadEvents extends AsyncTask<Void, Void, Void> {

    private String TAG = getClass().getSimpleName();

    private final String[] calendarIds = new String[7];
    {
    	calendarIds[0] = "86q7cqer2vdnrojrfr3uj6528s@group.calendar.google.com"; //Programa
        calendarIds[1] = "ksa8pr9r22v1ks0778ns2bi2ds@group.calendar.google.com"; //Open Space 1
        calendarIds[2] = "neme2f0tdssubikgq1a9crniq0@group.calendar.google.com"; //Open Space 2
        calendarIds[3] = "80ffbe280e3gn2t0stiugkjejc@group.calendar.google.com"; //Open Space 3
        calendarIds[4] = "bcajdr1mb9k7fa83jv9ihfh8ms@group.calendar.google.com"; //Open Space 4
        calendarIds[5] = "4mlghcncvkb1717rfms0sboosc@group.calendar.google.com"; //Open Space 5
        calendarIds[6] = "j7slvi95o51d53o2vuncpv00hc@group.calendar.google.com"; //Open Space 6
    }
    
    
    private final MainFragment calendarSample;

    
    private final ProgressDialog dialog;
    private com.google.api.services.calendar.Calendar client;

    public AsyncLoadEvents(MainFragment calendarSample) {
        
        this.calendarSample = calendarSample;

        client = calendarSample.client;
        dialog = new ProgressDialog(calendarSample);
    }

    @Override
    protected void onPreExecute() {
        dialog.setMessage("Cargando información ...");
        dialog.show();
    }

    @Override
    protected Void doInBackground(Void... arg0) {
        DatabaseAdapter dbAdapter = null;
        try {
            dbAdapter = new DatabaseAdapter(calendarSample);
            dbAdapter.open();
            
            for(int spaceId = 0; spaceId < calendarIds.length; spaceId++){
                String calendarId = calendarIds[spaceId];
                dbAdapter.deleteEvents(spaceId);
                List list = client.events().list(calendarId);
                
                Events events = list.execute();
                
                EventInfo eventInfo;
                
                for(Event event : events.getItems()){
                    Log.d(TAG, "Evento:" + event.getSummary() + " " + event.getStart().getDateTime() + " - " + event.getEnd().getDateTime());
                    eventInfo = new EventInfo(event.getSummary(), event.getStart().getDateTime(), event.getEnd().getDateTime());
                    
                    dbAdapter.createEvent(spaceId, eventInfo.getStart(), event.getSummary(), event.getDescription());
                    
                    if(event.getAttendees() != null){
                        for(EventAttendee attendee : event.getAttendees()){
                            Log.d(TAG, " * " + attendee.getEmail());
                            eventInfo.addAttender(attendee.getEmail());
                        }
                    }
                    
                }
            }
          
        } catch (IOException e) {
            calendarSample.handleGoogleException(e);
        } finally {
            try {
                dbAdapter.close();
            } catch(Exception e){
                //TODO cambiar
                e.printStackTrace();
            }
            calendarSample.onRequestCompleted();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        dialog.dismiss();
        calendarSample.refresh();
    }
}
