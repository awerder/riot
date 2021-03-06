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
package org.riotfamily.pages.setup;

import java.util.HashMap;
import java.util.Map;

import org.riotfamily.common.util.SpringUtils;
import org.riotfamily.pages.model.Page;
import org.riotfamily.pages.model.PageNode;
import org.riotfamily.pages.setup.config.ChildPageTypeDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.StringUtils;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 */
public class PageTypeHierarchy implements ApplicationContextAware {

	private Map<String, String> childTypes = new HashMap<String, String>();
	
	public void setApplicationContext(ApplicationContext ctx) {
		for (ChildPageTypeDefinition def : 
				SpringUtils.beansOfTypeIncludingAncestors(ctx, 
				ChildPageTypeDefinition.class).values()) {
		
			childTypes.put(def.getParent(), def.getChild());
		}
	}
	
	public String getChildType(String parentType) {
		return childTypes.get(parentType);
	}
	
	public String getChildType(Page page) {
		String pageType = page != null ? page.getPageType() : null;
		return childTypes.get(pageType);
	}
	
	public String[] getChildTypeOptions(Page page) {
		return StringUtils.commaDelimitedListToStringArray(getChildType(page));
	}

	public String[] getChildTypeOptions(String type) {
		return StringUtils.commaDelimitedListToStringArray(getChildType(type));
	}

	public boolean isValidChild(PageNode node, Page page) {
		return isValidTypeFor(node.getPageType(), page.getPageType());
	}
	
	public boolean isValidTypeFor(String parentType, String type) {
		for (String childType : getChildTypeOptions(parentType)) {
			if (childType.equals(type)) {
				return true;
			}
		}
		return false;
	}

	public String initPageType(PageNode node) {
		String pageType = null;
		if (node.getPageType() == null && node.getParent() != null) {
			pageType = getChildType(node.getParent().getPageType());
			node.setPageType(pageType);
		}
		return pageType;
	}
}
