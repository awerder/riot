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
package org.riotfamily.components.editor;

import java.io.IOException;


/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public interface EntityEditor {

	public String createObject(String editorId);
	
	public void deleteObject(String editorId, String objectId);
	
	public String getText(String editorId, String objectId, String property);
	
	public void updateText(String editorId, String objectId, 
			String property, String value);
	
	public String cropImage(String listId, String objectId, String property, 
			Long imageId, int width, int height, int x, int y, int scaledWidth)
			throws IOException;
}
