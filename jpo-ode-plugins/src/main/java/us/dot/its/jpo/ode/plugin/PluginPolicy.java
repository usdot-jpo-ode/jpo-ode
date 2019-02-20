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
/*
 * security-test - PluginPolicy.java - Copyright Â© 2007 David Roden
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 */

package us.dot.its.jpo.ode.plugin;

import java.security.AllPermission;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.Policy;
import java.security.cert.Certificate;

/**
 * General policy for JPO ODE PLugins 
 * 
 */
public class PluginPolicy extends Policy {

	/**
	 * Returns {@link AllPermission} for any code sources that do not end in
	 * rogue.jar and an empty set of permissions for code sources that do end
	 * in rogue.jar, denying access to all local resources to the rogue
	 * plugin.
	 * 
	 * @param codeSource
	 *            The code source to get the permissiosn for
	 * @return The permissions for the given code source
	 */
	@Override
	public PermissionCollection getPermissions(CodeSource codeSource) {
		Permissions p = new Permissions();
		Certificate[] certificates = codeSource.getCertificates();
		
		for (@SuppressWarnings("unused") Certificate cert : certificates) {
			//TODO -- Validate certificates associated with the jar file
		}
		
		// For now permit every plugin
		p.add(new AllPermission());
		
		return p;
	}
}
