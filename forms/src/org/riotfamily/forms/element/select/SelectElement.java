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
package org.riotfamily.forms.element.select;

import org.riotfamily.forms.Editor;


/**
 * Interface to be implemented by elements that provide options the user
 * can choose from.
 */
public interface SelectElement extends Editor {
		
	public String getParamName();
	
	public void renderOption(OptionItem option);

	public boolean isSelected(OptionItem option);
	
	public Object getOptions();
	
	public void setOptions(Object model);
	
	public int getOptionIndex(OptionItem option);

	public void reset();

}