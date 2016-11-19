/*******************************************************************************
 * Copyright (c) 2015 US DOT - Joint Program Office
 *
 * The Government has unlimited rights to all documents/material produced under 
 * this task order. All documents and materials, to include the source code of 
 * any software produced under this contract, shall be Government owned and the 
 * property of the Government with all rights and privileges of ownership/copyright 
 * belonging exclusively to the Government. These documents and materials may 
 * not be used or sold by the Contractor without written permission from the CO.
 * All materials supplied to the Government shall be the sole property of the 
 * Government and may not be used for any other purpose. This right does not 
 * abrogate any other Government rights.
 *
 * Contributors:
 *     Booz | Allen | Hamilton - initial API and implementation
 *******************************************************************************/
package us.dot.its.jpo.ode.model;

import java.math.BigDecimal;


@SuppressWarnings("serial")
public class BaseRequest extends OdeObject {

   private BigDecimal nwLat;
   private BigDecimal nwLon;
   private BigDecimal seLat;
   private BigDecimal seLon;
   
   public BaseRequest() {
   	super();
   }
   
	public BaseRequest(BigDecimal nwLat, BigDecimal nwLon, BigDecimal seLat, BigDecimal seLon) {
	   super();
	   this.nwLat = nwLat;
	   this.nwLon = nwLon;
	   this.seLat = seLat;
	   this.seLon = seLon;
   }

	static BaseRequest create() {
		return new BaseRequest();
	}
	
	public BigDecimal getNwLat() {
		return nwLat;
	}

	public BaseRequest setNwLat(BigDecimal nwLat) {
		this.nwLat = nwLat;
		return this;
	}

	public BigDecimal getNwLon() {
		return nwLon;
	}

	public BaseRequest setNwLon(BigDecimal nwLon) {
		this.nwLon = nwLon;
		return this;
	}

	public BigDecimal getSeLat() {
		return seLat;
	}

	public BaseRequest setSeLat(BigDecimal seLat) {
		this.seLat = seLat;
		return this;
	}

	public BigDecimal getSeLon() {
		return seLon;
	}

	public BaseRequest setSeLon(BigDecimal seLon) {
		this.seLon = seLon;
		return this;
	}

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((nwLat == null) ? 0 : nwLat.hashCode());
      result = prime * result + ((nwLon == null) ? 0 : nwLon.hashCode());
      result = prime * result + ((seLat == null) ? 0 : seLat.hashCode());
      result = prime * result + ((seLon == null) ? 0 : seLon.hashCode());
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
      BaseRequest other = (BaseRequest) obj;
      if (nwLat == null) {
         if (other.nwLat != null)
            return false;
      } else if (!nwLat.equals(other.nwLat))
         return false;
      if (nwLon == null) {
         if (other.nwLon != null)
            return false;
      } else if (!nwLon.equals(other.nwLon))
         return false;
      if (seLat == null) {
         if (other.seLat != null)
            return false;
      } else if (!seLat.equals(other.seLat))
         return false;
      if (seLon == null) {
         if (other.seLon != null)
            return false;
      } else if (!seLon.equals(other.seLon))
         return false;
      return true;
   }

}
