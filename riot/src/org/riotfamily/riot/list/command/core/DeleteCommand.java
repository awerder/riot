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
package org.riotfamily.riot.list.command.core;

import org.riotfamily.riot.list.command.BatchCommand;
import org.riotfamily.riot.list.command.CommandContext;
import org.riotfamily.riot.list.command.CommandResult;
import org.riotfamily.riot.list.command.result.RefreshSiblingsResult;

/**
 * Command that deletes an item. To prevent accidental deletion a confirmation
 * message is displayed.
 */
public class DeleteCommand extends AbstractCommand implements BatchCommand {

	public static final String ACTION_DELETE = "delete";
	
	public DeleteCommand() {
		setShowOnForm(true);
	}

	@Override
	public String getAction() {
		return ACTION_DELETE;
	}
	
	@Override
	public boolean isEnabled(CommandContext context) {
		return context.getObjectId() != null;
	}

	@Override
	public String getConfirmationMessage(CommandContext context) {
		Object[] args = getDefaultMessageArgs(context);
		return context.getMessageResolver().getMessage("confirm.delete", args, 
				"Do you really want to delete this element?");
	}

	public String getBatchConfirmationMessage(CommandContext context) {
		return context.getMessageResolver().getMessage("confirm.delete.selected", 
				"Do you really want to delete all selected elements?");
	}
		
	public CommandResult execute(CommandContext context) {
		Object item = context.getBean();
		context.getDao().delete(item, context.getParent());
		return new RefreshSiblingsResult(context);
	}
	
}
