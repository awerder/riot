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
 * Portions created by the Initial Developer are Copyright (C) 2008
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Jan-Frederic Linde [jfl at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.website.misc;

import java.util.Locale;

import javax.activation.FileTypeMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.io.IOUtils;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

/**
 * @author Jan-Frederic Linde [jfl at neteye dot de]
 * @since 7.0
 */
public class CountryFlagController implements Controller {
	
	private FileTypeMap fileTypeMap;
	
	private Resource location;
	
	private String defaultFlag;
	
	private String suffix;
	
	public CountryFlagController(FileTypeMap fileTypeMap) {
		this.fileTypeMap = fileTypeMap;
	}

	public void setLocation(Resource location) {
		this.location = location;
	}
	
	public void setDefaultFlag(String defaultFlag) {
		this.defaultFlag = defaultFlag;
	}
	
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}
	
	public ModelAndView handleRequest(HttpServletRequest request, 
					HttpServletResponse response) throws Exception {
		
		Resource res = null;
		String localeString = (String)request.getAttribute("locale");
		if (localeString != null) {
			Locale locale = StringUtils.parseLocaleString(localeString);			
			if (locale != null) {			
				String flagName = locale.getCountry();
				if (flagName != null) {
					res = location.createRelative(flagName.toLowerCase() + suffix);
				}
			}
		}
		
		if (res == null || !res.exists()) {
			res = location.createRelative(defaultFlag + suffix);
		}
		 		
		if (res.exists()) {			
			response.setContentType(getContentType(res));
			IOUtils.serve(res.getInputStream(), response.getOutputStream());
		}		
		return null;
	}
	
	protected String getContentType(Resource resource) {
		if (resource == null) {
			return null;
		}
		return fileTypeMap.getContentType(resource.getFilename());
	}

}
