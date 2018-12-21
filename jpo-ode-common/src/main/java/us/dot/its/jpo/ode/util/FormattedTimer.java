/*******************************************************************************
 * Copyright 2018 572682
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package us.dot.its.jpo.ode.util;

/**
 * Timer class with formatting
 *
 */
public class FormattedTimer {
    /** */
    private long timerStart = 0L;
    /** */
    

    /**
     * Starts the timer
     */
    public FormattedTimer() {
        this.timerStart = System.currentTimeMillis();
    }

    /**
     * Formats the time lapsed to "{HH} hours {mm} minutes {ss.sss} seconds".
     *
     * @param time
     * @return
     */
    protected String format(long time) {
        
        float seconds;
        int minutes;
        int hours;
        
        StringBuilder sb = new StringBuilder();
            float timeInSeconds = ((float) (time)) / 1000;
            hours = (int) (timeInSeconds / 3600);
            timeInSeconds = timeInSeconds - (hours * 3600);
            minutes = (int) (timeInSeconds / 60);
            timeInSeconds = timeInSeconds - (minutes * 60);
            seconds = timeInSeconds;
            sb.append(hours);
            sb.append(" hours ");
            sb.append(minutes);
            sb.append(" minutes ");
            sb.append(seconds);
            sb.append(" seconds");
        return sb.toString();
    }

    /**
     * @return the formatted time lapsed
     */
    public String getFormattedTimer() {
        return format(System.currentTimeMillis() - timerStart);
    }
}
