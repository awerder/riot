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
package org.riotfamily.forms.factory;

import java.util.HashMap;

import org.riotfamily.forms.Form;

public abstract class AbstractFormRepository implements FormRepository {

	private HashMap<String, FormFactory> factories = new HashMap<String, FormFactory>();

	public boolean containsForm(String id) {
		return factories.containsKey(id);
	}

	public FormFactory getFormFactory(String id) {
		FormFactory factory = (FormFactory) factories.get(id);
		if (factory == null) {
			throw new FormDefinitionException("No such form: " + id);
		}
		return factory;
	}

	public Form createForm(String id) {
		Form form = getFormFactory(id).createForm();
		form.setId(id);
		return form;
	}

	public Class<?> getBeanClass(String id) {
		return getFormFactory(id).getBeanClass();
	}

	public void registerFormFactory(String id, FormFactory formFactory) {
		factories.put(id, formFactory);
	}

	protected HashMap<String, FormFactory> getFactories() {
		return this.factories;
	}

}
