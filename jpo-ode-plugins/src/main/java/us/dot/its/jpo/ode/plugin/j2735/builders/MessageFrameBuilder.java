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
package us.dot.its.jpo.ode.plugin.j2735.builders;

public class MessageFrameBuilder {

    private MessageFrameBuilder() {
       throw new UnsupportedOperationException();
    }

    // Custom exception
    public static class MessageFrameBuilderException extends Exception {

        private static final long serialVersionUID = -952945144250120705L;

        public MessageFrameBuilderException(String msg) {
            super(msg);
        }

        public MessageFrameBuilderException(String msg, Exception e) {
            super(msg, e);
        }

    }

}
