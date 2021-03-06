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

import java.io.PrintWriter;
import java.util.List;

import org.riotfamily.common.markup.TagWriter;
import org.riotfamily.forms.MessageUtils;


/**
 * A select-box widget.
 */
public class SelectBox extends AbstractSingleSelectElement {

	private static final String DEFAULT_CHOOSE_LABEL_KEY = 
			"label.selectBox.choose";
	
	private String chooseLabelKey;

	private String chooseLabel;
	
	public SelectBox() {
		setOptionRenderer(new OptionTagRenderer());
	}

	/**
	 * @deprecated Use @link #setChooseLabel(String) or 
	 * @link #setChooseLabelKey(String) instead.
	 */
	public void setShowChooseOption(boolean show) {
		if (show) {
			chooseLabelKey = DEFAULT_CHOOSE_LABEL_KEY;
		}
		else {
			chooseLabelKey = null;
			chooseLabel = null;
		}
	}

	public void setChooseLabel(String chooseLabel) {
		this.chooseLabel = chooseLabel;
	}

	public void setChooseLabelKey(String chooseLabelKey) {
		this.chooseLabelKey = chooseLabelKey;
	}
	
	protected List<OptionItem> createOptionItems() {
		List<OptionItem> optionItems = super.createOptionItems();
		if (chooseLabelKey != null) {
			chooseLabel = MessageUtils.getMessage(this, chooseLabelKey);
		}
		if (chooseLabel != null) {
			OptionItem chooseOption = new OptionItem(null, null, chooseLabel, null, this);
			optionItems.add(0, chooseOption);
		}
		else if (!isRequired()) {
			OptionItem emptyOption = new OptionItem(null, null, "", null, this);
			optionItems.add(0, emptyOption);
		}
		return optionItems;
	}
	
	public Object getValue() {
		List<OptionItem> optionItems = getOptionItems();
		if (optionItems.size() == 1 && isRequired()) {
			return optionItems.get(0).getValue();
		}
		return super.getValue();
	}
			
	protected void renderInternal(PrintWriter writer) {
		List<OptionItem> optionItems = getOptionItems();
		TagWriter selectTag = new TagWriter(writer)
				.start("select")
				.attribute("id", getEventTriggerId())
				.attribute("class", getStyleClass())
				.attribute("name", getParamName())			
				.attribute("size", 1)
				.attribute("disabled", !isEnabled())
				.body();
		
		for (OptionItem item : optionItems) {
			item.render();
		}
		selectTag.end();
		
	}

}
