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

@SuppressWarnings("serial")
public class DdsRequest extends BaseRequest {

   public enum Dialog {
      VSD (154),
      ISD (162),
      ASD (156),
      ALL (-1);

      private final int id;

      Dialog(int id) {
         this.id = id;
      }

      public int getId() {
         return id;
      }

      public static Dialog getById(int id) {
         Dialog result = null;
         for (Dialog d : Dialog.values()) {
            if (d.getId() == id) {
               result = d;
               break;
            }
         }
         return result;
      }
   }

   public enum SystemName {
      SDC ("SDC 2.2"),
      SDW ("SDW 2.2"),
      SDPC ("SDPC 2.2");

      private final String name;

      SystemName(String name) {
         this.name = name;
      }

      public String getName() {
         return name;
      }
   }

   public enum EncodeType {base64, hex, json}

   private int dialogID;
   private String resultEncoding;

   public int getDialogID() {
      return dialogID;
   }

   public DdsRequest setDialogID(int dialogID) {
      this.dialogID = dialogID;
      return this;
   }

   public String getResultEncoding() {
      return resultEncoding;
   }

   public DdsRequest setResultEncoding(String resultEncoding) {
      this.resultEncoding = resultEncoding;
      return this;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + dialogID;
      result = prime * result
            + ((resultEncoding == null) ? 0 : resultEncoding.hashCode());
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
      DdsRequest other = (DdsRequest) obj;
      if (dialogID != other.dialogID)
         return false;
      if (resultEncoding == null) {
         if (other.resultEncoding != null)
            return false;
      } else if (!resultEncoding.equals(other.resultEncoding))
         return false;
      return true;
   }

}
