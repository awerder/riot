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
package org.riotfamily.forms.element;

import java.io.PrintWriter;
import java.util.List;

import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.forms.Container;
import org.riotfamily.forms.ContainerElement;
import org.riotfamily.forms.DHTMLElement;
import org.riotfamily.forms.Editor;
import org.riotfamily.forms.Element;
import org.riotfamily.forms.ErrorUtils;
import org.riotfamily.forms.MessageUtils;
import org.riotfamily.forms.TemplateUtils;
import org.riotfamily.forms.event.Button;
import org.riotfamily.forms.event.JavaScriptEvent;
import org.riotfamily.forms.request.FormRequest;



/**
 * Element that visually groups other elements.
 */
public class ElementGroup extends TemplateElement implements ContainerElement,
		DHTMLElement {

	private Container container = new Container();

	private String labelKey;
	
	private boolean labelItems = true;

	private boolean collapsible;
	
	private boolean expanded = true;
	
	private boolean clientHasExpandedState;
	
	public ElementGroup() {
		super("group", TemplateUtils.getTemplatePath(ElementGroup.class));
		addComponent("elements", container);
		addComponent("expandButton", new ExpandButton());
	}

	public List<Element> getElements() {
		return container.getElements();
	}
	
	public void addElement(Element element) {
		container.addElement(element);
	}
	
	public void removeElement(Element element) {
		container.removeElement(element);
	}

	public void setLabelKey(String key) {
		labelKey = key;
	}	

	public boolean isLabelItems() {
		return labelItems;
	}

	public void setLabelItems(boolean labelItems) {
		this.labelItems = labelItems;
	}

	public String getLabel() {
		if (labelKey == null) {
			return "";
		}
		return MessageUtils.getMessage(this, labelKey);
	}
	
	public String getSystemStyleClass() {
		if (labelKey != null) {
			return FormatUtils.toCssClass(labelKey);
		}
		return null;
	}
	
	public void setCollapsible(boolean collapsible) {
		this.collapsible = collapsible;
		this.expanded = !collapsible;
	}
	
	public boolean isCollapsible() {
		return this.collapsible;
	}
	
	public boolean isExpanded() {
		return this.expanded;
	}
	
	protected void processRequestCompontents(FormRequest request) {
		if (clientHasExpandedState) {
			super.processRequestCompontents(request);
		}
	}
	
	protected void renderInternal(PrintWriter writer) {
		if (!expanded) {
			for (Element element : getElements()) {
				if (ErrorUtils.hasErrors(element)) {
					expanded = true;
					break;
				}
				if (element.isRequired() && element instanceof Editor) {
					Editor editor = (Editor) element;
					if (editor.getValue() == null) {
						expanded = true;
						break;
					}
				}
			}
		}
		clientHasExpandedState = expanded;
		super.renderInternal(writer);
	}
	
	protected void toggle() {
		expanded = !expanded;
		if (expanded && !clientHasExpandedState) {
			if (getFormListener() != null) {
				getFormListener().elementChanged(this);			
			}
			focus();
		}
	}
	
	public String getInitScript() {
		return collapsible && expanded 
				? TemplateUtils.getInitScript(this) 
				: null;
	}
	
	public class ExpandButton extends Button {
		
		private ExpandButton() {
		}
		
		@Override
		public String getLabel() {
			return expanded
					? getCollapseLabel()
					: getExpandLabel();
		}

		@Override
		public String getSystemStyleClass() {
			return expanded 
					? "button button-collapse" 
					: "button button-expand";
		}
		
		public String getExpandLabel() {
			return FormatUtils.stripTags(MessageUtils.getMessage(this, 
					"label.elementGroup.expand"));
		}
		
		public String getCollapseLabel() {
			return FormatUtils.stripTags(MessageUtils.getMessage(this, 
					"label.elementGroup.collapse"));
		}
		
		@Override
		protected void onClick() {
			toggle();
		}
		
		@Override
		public int getEventTypes() {
			return JavaScriptEvent.ON_CLICK;
		}
	}
}
