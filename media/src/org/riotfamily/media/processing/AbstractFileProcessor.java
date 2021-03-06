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
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.media.processing;

import org.riotfamily.media.model.RiotFile;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 * @deprecated
 */
public abstract class AbstractFileProcessor implements FileProcessor, 
		BeanNameAware, InitializingBean {

	private String beanName;
	
	private String variant;

	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	public void setVariant(String variant) {
		this.variant = variant;
	}
	
	public void afterPropertiesSet() {
		if (variant == null) {
			variant = beanName;
		}
	}
	
	public void process(RiotFile data) throws FileProcessingException {
		try {
			data.addVariant(variant, createVariant(data));
		}
		catch (Exception e) {
			throw new FileProcessingException(e);
		}
	}
	
	protected abstract RiotFile createVariant(RiotFile data) throws Exception;

}
