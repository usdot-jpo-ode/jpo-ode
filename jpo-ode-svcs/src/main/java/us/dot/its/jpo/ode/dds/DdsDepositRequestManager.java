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
package us.dot.its.jpo.ode.dds;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.dds.DdsRequest.EncodeType;
import us.dot.its.jpo.ode.model.OdeDepRequest;
import us.dot.its.jpo.ode.model.OdeRequest;

public class DdsDepositRequestManager extends DdsRequestManager<DdsStatusMessage> {

   public DdsDepositRequestManager(OdeProperties odeProperties)
         throws us.dot.its.jpo.ode.dds.DdsRequestManager.DdsRequestManagerException {
      super(odeProperties);
   }

   @Override
   protected DdsRequest buildDdsRequest(OdeRequest odeRequest) throws DdsRequestManagerException {

      DdsDepRequest ddsDepReq = new DdsDepRequest();

      if (odeRequest instanceof OdeDepRequest) {
         OdeDepRequest odeDepReq = (OdeDepRequest) odeRequest;
         String sEncodeType = StringUtils.upperCase(odeDepReq.getEncodeType());
         if (sEncodeType != null) {
            EncodeType encodeType = DdsRequest.EncodeType.valueOf(sEncodeType);
            ddsDepReq.setSystemDepositName(DdsDepositRequestManager.systemName(odeDepReq).getName())
                  .setEncodeType(encodeType.name()).setEncodedMsg(odeDepReq.getData());
         } else {
            throw new DdsRequestManagerException("Invalid or unsupported EncodeType Deposit: " + sEncodeType
                  + ". Supported encode types are: " + Arrays.asList(DdsRequest.EncodeType.values()));
         }
      } else {
         throw new DdsRequestManagerException("Invalid Request: " + odeRequest.toJson(odeProperties.getVerboseJson()));
      }
      return ddsDepReq;
   }
}
