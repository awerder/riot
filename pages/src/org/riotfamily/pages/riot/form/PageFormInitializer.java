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
package org.riotfamily.pages.riot.form;

import org.riotfamily.forms.Form;
import org.riotfamily.forms.FormInitializer;
import org.riotfamily.forms.element.NestedForm;
import org.riotfamily.forms.element.select.SelectBox;
import org.riotfamily.forms.factory.FormRepository;
import org.riotfamily.pages.model.Page;
import org.riotfamily.pages.model.Site;
import org.riotfamily.pages.setup.PageTypeHierarchy;
import org.riotfamily.riot.form.ui.FormUtils;

/**
 * FormInitializer that imports form fields defined in content-forms.xml.
 * If a new page is edited, the {@link PageTypeHierarchy} is asked for
 * possible page types. If more than one page type is configured, a
 * dropdown is added that lets the user select a type.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.6
 */
public class PageFormInitializer implements FormInitializer {

	private PageTypeHierarchy pageTypeHierarchy;
	
	private FormRepository repository;

	public PageFormInitializer(PageTypeHierarchy pageTypeHierarchy, 
			FormRepository repository) {
		
		this.pageTypeHierarchy = pageTypeHierarchy;
		this.repository = repository;
	}

	public void initForm(Form form) {
		String pageType = null;
		SelectBox sb = null;
		if (form.isNew())  {
			Page parentPage = null;
			Object parent = FormUtils.loadParent(form);
			if (parent instanceof Page) {
				parentPage = (Page) parent;
				form.setAttribute("pageId", parentPage.getId());
				form.setAttribute("siteId", parentPage.getSite().getId());
			}
			else if (parent instanceof Site) {
				Site site = (Site) parent;
				form.setAttribute("siteId", site.getId());
			}
			String[] pageTypes = pageTypeHierarchy.getChildTypeOptions(parentPage);
			if (pageTypes.length > 0) {
				sb = createPageTypeBox(form, pageTypes);
				pageType = pageTypes[0];
			}
			else {
				pageType = "default";
			}
		}
		else {
			Page page = (Page) form.getBackingObject();
			form.setAttribute("pageId", page.getId());
			form.setAttribute("siteId", page.getSite().getId());
			pageType = page.getPageType();
		}
		
		PagePropertiesEditor ppe = new PagePropertiesEditor(repository, form, pageType);
		
		if (sb != null) {
			sb.addChangeListener(ppe);
		}
		form.addElement(ppe, "pageProperties");
	}
	
	private SelectBox createPageTypeBox(Form form, String[] handlerNames) {
		if (handlerNames.length > 1) {
			SelectBox sb = new SelectBox();
			sb.setRequired(true);
			sb.setOptions(handlerNames);
			sb.setLabelMessageKey("page.pageType.");
			sb.setAppendLabel(true);
			NestedForm nodeForm = new NestedForm();
			nodeForm.setIndent(false);
			nodeForm.setRequired(true);
			form.addElement(nodeForm, "node");
			nodeForm.addElement(sb, "pageType");
			return sb;
		}
		return null;
	}
	
}
