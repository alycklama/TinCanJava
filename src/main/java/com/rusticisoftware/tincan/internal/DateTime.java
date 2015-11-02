/*
 * Copyright 2015 Rustici Software.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.rusticisoftware.tincan.internal;

import org.joda.time.LocalDateTime;

public class DateTime {
	private hirondelle.date4j.DateTime dateTime;
	private boolean hasZuluTime = false;

	public DateTime() {	
		this.dateTime = new hirondelle.date4j.DateTime(LocalDateTime.now().toString());
	}

	public DateTime(String value) {
		if (value.endsWith("Z")) {
			hasZuluTime = true;
			this.dateTime = new hirondelle.date4j.DateTime(value.substring(0, value.length() -1));
		} else {
			this.dateTime = new hirondelle.date4j.DateTime(value);
		}
	}

	public String getDateTime() {
		if (hasZuluTime) {
			return dateTime.getRawDateString() + "Z";
		}
		return dateTime.getRawDateString();
	}
	
	public org.joda.time.DateTime getJodaDateTime() {
		return new org.joda.time.DateTime(this.getDateTime());
	}
}
