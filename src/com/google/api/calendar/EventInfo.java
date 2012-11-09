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
package com.google.api.calendar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.google.api.client.util.DateTime;
import com.google.common.collect.Lists;

public class EventInfo {
    
    private static final SimpleDateFormat SDF_HOUR = new SimpleDateFormat("HH:mm");
    
    private String summary;
    private String start;
    private String end;
    private List<String> attenders;
    
    public EventInfo(String sumary, DateTime start, DateTime end){
        attenders = Lists.newArrayList();
        this.summary = sumary;
        this.start = SDF_HOUR.format(new Date(start.getValue()));
        this.end = SDF_HOUR.format(new Date(end.getValue()));
    }
    
    public void addAttender(String attender){
        attenders.add(attender);
    }

    public String getSummary() {
        return summary;
    }

    public String getStart() {
        return start;
    }

    public String getEnd() {
        return end;
    }

    public List<String> getAttenders() {
        return attenders;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("EventInfo [summary=");
        builder.append(summary);
        builder.append(", start=");
        builder.append(start);
        builder.append(", end=");
        builder.append(end);
        builder.append(", attenders=");
        builder.append(attenders);
        builder.append("]");
        return builder.toString();
    }

}
