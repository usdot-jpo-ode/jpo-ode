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
package us.dot.its.jpo.ode.plugin.asn1;

import java.io.BufferedInputStream;

import us.dot.its.jpo.ode.plugin.OdePlugin;

public interface J2735Plugin extends OdePlugin {

    Asn1Object decodeUPERMessageFrameHex(String hexMsg);
    Asn1Object decodeUPERBsmHex(String hexMsg);

    String encodeUPERBase64(Asn1Object asn1Object);

    String encodeUPERHex(Asn1Object asn1Object);

    byte[] encodeUPERBytes(Asn1Object asn1Object);

    Asn1Object decodeUPERBsmBytes(byte[] byteArrayMsg);

    Asn1Object decodeUPERMessageFrameBytes(byte[] byteArrayMsg);

    Asn1Object decodeUPERMessageFrameStream(BufferedInputStream ins);

    Asn1Object decodeUPERBsmStream(BufferedInputStream ins);

}
