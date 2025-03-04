package us.dot.its.jpo.ode.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents an entity submitted to and consumed by the Situation Data Exchange (SDX) Deposit.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SDXDeposit {
  @JsonSerialize()
  private String estimatedRemovalDate;
  private String encodedMsg;
}
