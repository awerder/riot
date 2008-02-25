/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 * 
 * The Original Code is Riot.
 * 
 * The Initial Developer of the Original Code is
 * Neteye GmbH.
 * Portions created by the Initial Developer are Copyright (C) 2006
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.riot.security.auth;

import org.springframework.util.Assert;

/**
 * AuthenticationService that uses a fixed username/password combination.
 * This class is intended for development purposes only.
 */
public class StaticAuthenticationService 
		implements UserLookupAuthenticationService {

	public static final String DEFAULT_USERNAME = "root";
	
	private static final RiotUser ROOT = new RootUser();
	
	private String username = DEFAULT_USERNAME;
	
	private String password;
	
	
	public void setPassword(String password) {
		this.password = password;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public RiotUser authenticate(String username, String password) {
		Assert.notNull("No password set.");
		if (this.username.equals(username) && this.password.equals(password)) {
			return ROOT;
		}
		return null;
	}
	
	public RiotUser getUserById(String userId) {
		if (RootUser.ID.equals(userId)) {
			return ROOT;
		}
		return null;
	}

	private static class RootUser implements RiotUser {

		private static String ID = "root";
		
		public String getUserId() {
			return ID;
		}
	}
}
