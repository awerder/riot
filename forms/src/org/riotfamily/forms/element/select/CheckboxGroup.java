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
import java.util.HashMap;

import org.riotfamily.forms.DHTMLElement;
import org.riotfamily.forms.TemplateUtils;
import org.riotfamily.forms.resource.FormResource;
import org.riotfamily.forms.resource.ResourceElement;
import org.riotfamily.forms.resource.Resources;
import org.riotfamily.forms.resource.ScriptResource;


/**
 * Multi-select element that uses a group of checkboxes to render the options.
 * Internally a template is used in order to allow the customization of
 * the layout.
 */
public class CheckboxGroup extends AbstractMultiSelectElement 
		implements DHTMLElement, ResourceElement {

	protected static final FormResource RESOURCE = new ScriptResource(
			"riot-js/checkbox.js", "RiotCheckboxGroup", Resources.PROTOTYPE);
	
	private String template;

	public CheckboxGroup() {
		setOptionRenderer(new InputTagRenderer("checkbox"));
		template = TemplateUtils.getTemplatePath(CheckboxGroup.class);
	}
	
	public String getEventTriggerId() {		
		return getId();
	}

	public FormResource getResource() {
		return RESOURCE;
	}
	
	public void setTemplate(String template) {
		this.template = template;
	}

	protected void renderInternal(PrintWriter writer) {
		HashMap<String, Object> model = new HashMap<String, Object>();
		model.put("element", this);
		model.put("options", getOptionItems());
		getFormContext().getTemplateRenderer().render(template, model, writer);
	}
	
	public String getInitScript() {
		return "new RiotCheckboxGroup('" + getId() + "');";
	}

	public boolean isCompositeElement() {
		return true;
	}
}