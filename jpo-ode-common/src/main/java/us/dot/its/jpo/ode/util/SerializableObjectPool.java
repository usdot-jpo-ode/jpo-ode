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

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;

public abstract class SerializableObjectPool<T> implements Serializable {
   private static final long serialVersionUID = 914977959079989774L;

   private long expirationTime;

   private Hashtable<T, Long> locked, unlocked;

   protected SerializableObjectPool() {
      expirationTime = 30000; // 30 seconds
      locked = new Hashtable<>();
      unlocked = new Hashtable<>();
   }

   protected abstract T create();

   public abstract boolean validate(T o);

   public abstract void expire(T o);

   public synchronized T checkOut() {
      long now = System.currentTimeMillis();
      T t;
      if (!unlocked.isEmpty()) {
         Enumeration<T> e = unlocked.keys();
         while (e.hasMoreElements()) {
            t = e.nextElement();
            if ((now - unlocked.get(t)) > expirationTime) {
               // object has expired
               unlocked.remove(t);
               expire(t);
               t = null;	//NOSONAR
            } else {
               if (validate(t)) {
                  unlocked.remove(t);
                  locked.put(t, now);
                  return (t);
               } else {
                  // object failed validation
                  unlocked.remove(t);
                  expire(t);
                  t = null;	//NOSONAR
               }
            }
         }
      }
      // no objects available, create a new one
      t = create();
      locked.put(t, now);
      return (t);
   }

   public synchronized void checkIn(T t) {
      locked.remove(t);
      unlocked.put(t, System.currentTimeMillis());
   }
}
