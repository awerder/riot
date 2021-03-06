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
 * Portions created by the Initial Developer are Copyright (C) 2007
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.components.riot.form;

import org.riotfamily.components.model.Content;
import org.riotfamily.forms.AbstractEditorBinder;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 */
public class ContentEditorBinder extends AbstractEditorBinder {

	private Content content;
	
	public boolean isEditingExistingBean() {
		return true;
	}

	public void setBackingObject(Object backingObject) {
		content = (Content) backingObject;
		if (content == null) {
			content = new Content();
		}
	}
		
	public Object getBackingObject() {
		return content;
	}
	
	public Class<?> getBeanClass() {
		return content.getClass();
	}
	
	public Class<?> getPropertyType(String path) {
		return Object.class;
	}

	public Object getPropertyValue(String property) {
		return content.getValue(property);
	}

	public void setPropertyValue(String property, Object value) {
		content.setValue(property, value);
	}

}
