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
package org.riotfamily.riot.security.session;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.riot.editor.ui.FrameSetController;
import org.riotfamily.riot.runtime.RiotRuntime;
import org.riotfamily.riot.runtime.RiotRuntimeAware;
import org.riotfamily.riot.security.auth.RiotUser;

/**
 * HandlerInterceptor that sends a redirect to the login URL in case the
 * user is not logged in.
 *  
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public class LoginInterceptor extends AccessControlInterceptor 
		implements RiotRuntimeAware {
	
	private String loginUrl;
	
	private RiotRuntime runtime;
	
	public void setLoginUrl(String loginUrl) {
		this.loginUrl = loginUrl;
	}
	
	public void setRiotRuntime(RiotRuntime runtime) {
		this.runtime = runtime;
	}

	/**
	 * Returns <code>true</code> if a principal is set, otherwise 
	 * <code>false</code> is returned and a redirect to the login form is sent.
	 */
	protected boolean isAuthorized(HttpServletRequest request,
			HttpServletResponse response, RiotUser user) throws Exception {
		
		if (user != null) {
			return true;
		}
		else {
			if (request.getParameter(FrameSetController.REQUESTED_URL_PARAM) != null) {				
				request.getSession().setAttribute(FrameSetController.REQUESTED_URL_PARAM, 
						request.getParameter(FrameSetController.REQUESTED_URL_PARAM));
			}
			response.sendRedirect(response.encodeRedirectURL(
					request.getContextPath() + runtime.getServletPrefix() 
					+ loginUrl));
			
			return false;
		}
	}
	
}
