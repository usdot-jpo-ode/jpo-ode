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

import us.dot.its.jpo.ode.context.AppContext;
import us.dot.its.jpo.ode.util.JsonUtils;

@SuppressWarnings("serial")
public class OdeRequest extends BaseRequest {
   public static class OdeRequestException extends OdeException {

      public OdeRequestException(String message) {
         super(message);
      }

   }

   public enum DataSource {
      SDC, SDW, SDPC, TEST_UPLOAD, DEPOSIT_SDC, DEPOSIT_SDW
   }

   private String id;
   private DataSource dataSource;
   private OdeRequestType requestType;
   private OdeDataType dataType;

   public OdeRequest() {
      super();
   }

   public OdeRequest(OdeRequest other) {
      super();
      this.setDataSource(other.getDataSource());
      this.setDataType(other.getDataType());
      this.setId(other.getId());
      this.setNwLat(other.getNwLat());
      this.setNwLon(other.getNwLon());
      this.setRequestType(other.getRequestType());
      this.setSeLat(other.getSeLat());
      this.setSeLon(other.getSeLon());
   }

   public OdeRequest(BigDecimal nwLat, BigDecimal nwLon, BigDecimal seLat, BigDecimal seLon) {
      super(nwLat, nwLon, seLat, seLon);
   }

   public static OdeRequest create(String rtype, String dtype, String message) throws OdeRequestException {
      OdeRequest odeRequest = null;
      OdeRequestType requestType = OdeRequestType.getByShortName(rtype);
      if (requestType == OdeRequestType.Deposit) {
         odeRequest = (OdeRequest) JsonUtils.fromJson(message, OdeDepRequest.class);
      } else {
         OdeStatus status = new OdeStatus().setCode(OdeStatus.Code.INVALID_REQUEST_TYPE_ERROR).setMessage(String
               .format("Invalid request type %s. Valid request types are %s.", rtype, OdeRequestType.shortNames()));
         throw new OdeRequestException(status.toString());
      }
      odeRequest.setRequestType(requestType);

      OdeDataType dataType = OdeDataType.getByShortName(dtype);
      if (dataType == null) {
         OdeStatus status = new OdeStatus().setCode(OdeStatus.Code.INVALID_DATA_TYPE_ERROR).setMessage(
               String.format("Invalid data type %s. Valid data types are %s.", dtype, OdeDataType.shortNames()));
         throw new OdeRequestException(status.toString());
      }
      odeRequest.setDataType(dataType);

      odeRequest.setId(buildRequestId(odeRequest));
      return odeRequest;
   }

   public static String buildRequestId(OdeRequest odeRequest) {
      return AppContext.getInstance().getParam(AppContext.ODE_HOSTNAME) + odeRequest.getClass().getSimpleName()
            + String.valueOf(odeRequest.hashCode());
   }

   public String getId() {
      return id;
   }

   public OdeRequest setId(String id) {
      this.id = id;
      return this;
   }

   public OdeRequestType getRequestType() {
      return requestType;
   }

   public DataSource getDataSource() {
      return dataSource;
   }

   public OdeRequest setDataSource(DataSource dataSource) {
      this.dataSource = dataSource;
      return this;
   }

   public OdeRequest setRequestType(OdeRequestType requestType) {
      this.requestType = requestType;
      return this;
   }

   public OdeDataType getDataType() {
      return dataType;
   }

   public OdeRequest setDataType(OdeDataType dataType) {
      this.dataType = dataType;
      return this;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((dataSource == null) ? 0 : dataSource.hashCode());
      result = prime * result + ((dataType == null) ? 0 : dataType.hashCode());
      result = prime * result + ((id == null) ? 0 : id.hashCode());
      result = prime * result + ((requestType == null) ? 0 : requestType.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (!super.equals(obj))
         return false;
      if (getClass() != obj.getClass())
         return false;
      OdeRequest other = (OdeRequest) obj;
      if (dataSource != other.dataSource)
         return false;
      if (dataType != other.dataType)
         return false;
      if (id == null) {
         if (other.id != null)
            return false;
      } else if (!id.equals(other.id))
         return false;
      if (requestType != other.requestType)
         return false;
      return true;
   }

}