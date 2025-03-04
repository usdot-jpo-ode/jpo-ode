/*==============================================================================
 *
 * This source file was generated by a tool.
 * Beware manual edits might be overwritten in future releases.
 * asn1jvm v1.0-SNAPSHOT
 *
 *------------------------------------------------------------------------------
 * Copyright 2024 USDOT
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
 *============================================================================*/

package us.dot.its.jpo.ode.plugin.j2735.common;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import us.dot.its.jpo.ode.plugin.types.Asn1Enumerated;

@Getter
@JsonSerialize(using = ExtentSerializer.class)
@JsonDeserialize(using = ExtentDeserializer.class)
public enum Extent implements Asn1Enumerated {
	USEINSTANTLYONLY(0, "useInstantlyOnly"), USEFOR3METERS(1, "useFor3meters"), USEFOR10METERS(2,
			"useFor10meters"), USEFOR50METERS(3, "useFor50meters"), USEFOR100METERS(4,
					"useFor100meters"), USEFOR500METERS(5, "useFor500meters"), USEFOR1000METERS(6,
							"useFor1000meters"), USEFOR5000METERS(7, "useFor5000meters"), USEFOR10000METERS(8,
									"useFor10000meters"), USEFOR50000METERS(9, "useFor50000meters"), USEFOR100000METERS(
											10, "useFor100000meters"), USEFOR500000METERS(11,
													"useFor500000meters"), USEFOR1000000METERS(12,
															"useFor1000000meters"), USEFOR5000000METERS(13,
																	"useFor5000000meters"), USEFOR10000000METERS(14,
																			"useFor10000000meters"), FOREVER(15,
																					"forever");

	private final int index;
	private final String name;

	public boolean hasExtensionMarker() {
		return false;
	}

	private Extent(int index, String name) {
		this.index = index;
		this.name = name;
	}

	public int maxIndex() {
		return 15;
	}
}