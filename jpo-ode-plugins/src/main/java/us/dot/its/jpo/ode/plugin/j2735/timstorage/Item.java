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
package us.dot.its.jpo.ode.plugin.j2735.timstorage;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class Item extends Asn1Object {
   private static final long serialVersionUID = 1L;
   
   private String itis;
   private String text;

    public String getItis ()
    {
        return itis;
    }

    public void setItis (String itis)
    {
        this.itis = itis;
    }

    public String getText() {
      return text;
   }

   public void setText(String text) {
      this.text = text;
   }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((itis == null) ? 0 : itis.hashCode());
    result = prime * result + ((text == null) ? 0 : text.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Item other = (Item) obj;
    if (itis == null) {
      if (other.itis != null)
        return false;
    } else if (!itis.equals(other.itis))
      return false;
    if (text == null) {
      if (other.text != null)
        return false;
    } else if (!text.equals(other.text))
      return false;
    return true;
  }
}
