/*==============================================================================
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

package us.dot.its.jpo.ode.context;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;

/**
 * AppContext is a singleton class designed for the jpo-ode-core module.
 * It provides a mechanism to access unique host-related data such as the `hostId`, which can
 * be utilized as a unique identifier for the machine or instance running the application.
 */
@Slf4j
public class AppContext {

  private String hostId;

  // Private constructor to prevent instantiation
  private AppContext() {
    init(); // Initialize the hostId during instance creation
  }

  // Inner static helper class responsible for holding the Singleton instance
  private static class AppContextHolder {
    // Instance is created only when AppContextHolder is accessed
    private static final AppContext INSTANCE = new AppContext();
  }

  /**
   * Public method to return the Singleton instance of AppContext.
   * Lazy-loaded and thread-safe without explicit synchronization.
   */
  public static AppContext getInstance() {
    return AppContextHolder.INSTANCE;
  }

  // Initialize the hostId with the hostname or a random UUID
  private void init() {
    String hostname;
    try {
      hostname = InetAddress.getLocalHost().getHostName();
    } catch (UnknownHostException e) {
      // Use a random hostname if an UnknownHostException occurs
      hostname = UUID.randomUUID().toString();
      log.error("Unknown host error, using random hostname", e);
    }
    hostId = hostname;
    log.info("Host ID initialized: {}", hostId);
  }

  /**
   * Get the hostId.
   * Ensures that the hostId is re-initialized if it is null or empty.
   *
   * @return the hostId
   */
  public String getHostId() {
    if (this.hostId == null || this.hostId.isEmpty()) {
      initializeHostId();
    }
    return hostId;
  }

  // Reinitialize the hostId if needed
  private void initializeHostId() {
    String hostname;
    try {
      hostname = InetAddress.getLocalHost().getHostName();
    } catch (UnknownHostException e) {
      // Use a random hostname if an UnknownHostException occurs
      hostname = UUID.randomUUID().toString();
      log.error("Unknown host error during reinitialization, using random hostname", e);
    }
    this.hostId = hostname;
    log.info("Host ID re-initialized: {}", hostId);
  }
}
