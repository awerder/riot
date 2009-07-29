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
package org.riotfamily.components.cache;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.cachius.CacheService;
import org.riotfamily.cachius.CachiusContext;
import org.riotfamily.components.model.ContentContainer;
import org.riotfamily.components.support.EditModeUtils;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public final class ComponentCacheUtils {

	private ComponentCacheUtils() {
	}
	
	/**
	 * Returns the tag for the given container id.
	 */
	private static String getContainerTag(ContentContainer container, 
			boolean preview) {
		
		return ContentContainer.class.getName() + '#' + container.getId() 
				+ (preview ? "-preview" : "-live");
	}
	
	public static void addContainerTags(ContentContainer container, HttpServletRequest request) {
		addContainerTags(container, EditModeUtils.isPreview(request, container));
	}
	
	public static void addContainerTags(ContentContainer container, boolean preview) {
		CachiusContext.tag(getContainerTag(container, preview));
	}
	
	/**
	 * Invalidates the live and preview version of the container.
	 */
	public static void invalidateContainer(CacheService cacheService, ContentContainer container) {
		cacheService.invalidateTaggedItems(getContainerTag(container, false));
		cacheService.invalidateTaggedItems(getContainerTag(container, true));
	}
	
	/**
	 * Invalidates the preview version of the container.
	 */
	public static void invalidatePreviewVersion(CacheService cacheService, ContentContainer container) {
		cacheService.invalidateTaggedItems(getContainerTag(container, true));
	}

}
