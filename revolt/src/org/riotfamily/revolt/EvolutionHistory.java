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
package org.riotfamily.revolt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.sql.DataSource;

import org.riotfamily.revolt.support.DialectResolver;
import org.riotfamily.revolt.support.LogTable;
import org.springframework.beans.factory.BeanNameAware;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * 
 */
public class EvolutionHistory implements BeanNameAware {

	private String[] depends = new String[0];
	
	private String moduleName;
	
	private String checkTableName;
	
	private List<ChangeSet> changeSets;

	private DataSource dataSource;
	
	private Dialect dialect;
	
	private LogTable logTable;
	
	private ArrayList<String> appliedIds;
	
	public EvolutionHistory(DataSource dataSource) {
		this.dataSource = dataSource;
		this.dialect = new DialectResolver().getDialect(dataSource);
	}
	
	public DataSource getDataSource() {
		return this.dataSource;
	}
	
	public Dialect getDialect() {
		return this.dialect;
	}

	public void setBeanName(String name) {
		this.moduleName = name;
	}
	
	public void setCheckTableName(String checkTableName) {
		this.checkTableName = checkTableName;
	}
	
	public String getModuleName() {
		return this.moduleName;
	}

	public void setChangeSets(ChangeSet[] changeSets) {
		this.changeSets = new ArrayList<ChangeSet>();
		for (int i = 0; i < changeSets.length; i++) {
			ChangeSet changeSet = changeSets[i];
			changeSet.setHistory(this);
			changeSet.setSequenceNumber(i);
			this.changeSets.add(changeSet);
		}
	}

	/**
	 * Initializes the history from the given log-table.
	 */
	public void init(LogTable logTable) {
		this.logTable = logTable;
		appliedIds = new ArrayList<String>();
		appliedIds.addAll(logTable.getAppliedChangeSetIds(moduleName));
	}

	/**
	 * Returns a script that needs to be executed in order update the schema.
	 */
	public Script getScript() {
		if (!logTable.exists() || !logTable.tableExists(checkTableName)) {
			return getInitScript();
		}
		else {
			return getMigrationScript();
		}
	}
	
	private Script getInitScript() {
		Script script = new Script();
		for (ChangeSet changeSet : changeSets) {
			script.append(markAsApplied(changeSet));
		}
		return script;
	}
	
	private Script getMigrationScript() {
		Script script = new Script();
		for (ChangeSet changeSet : changeSets) {
			if (!isApplied(changeSet)) {
				script.append(changeSet.getScript(dialect));
				script.append(markAsApplied(changeSet));
			}
		}
		return script;
	}
		
	/**
	 * Returns whether the given changeSet has already been applied.
	 * @throws DatabaseOutOfSyncException If the ChangeSet id in the log-table
	 * 		   doesn't match the the one of the ChangeSet
	 */
	private boolean isApplied(ChangeSet changeSet) {
		if (appliedIds.size() > changeSet.getSequenceNumber()) {
			String appliedId = (String) appliedIds.get(
					changeSet.getSequenceNumber());
			
			if (appliedId.equals(changeSet.getId())) {
				return true;
			}
			throw new EvolutionException("ChangeSet number " 
					+ changeSet.getSequenceNumber() + " should be [" 
					+ changeSet.getId() + "] but is [" + appliedId + "]");
		}
		return false;
	}
	
	/**
	 * Returns a script that can be used to add an entry to the log-table that
	 * marks the given ChangeSet as applied. 
	 * @throws DatabaseOutOfSyncException If the sequence number of the 
	 * 		   ChangeSet doesn't match the number of already applied changes.
	 */
	private Script markAsApplied(ChangeSet changeSet) {
		if (changeSet.getSequenceNumber() != appliedIds.size()) {
			throw new EvolutionException("ChangeSet [" 
					+ changeSet.getId() + "] is number " 
					+ changeSet.getSequenceNumber() + " but there are already "
					+ appliedIds.size() + " ChangeSets applied!");
		}
		appliedIds.add(changeSet.getId());
		return logTable.getInsertScript(changeSet);
	}

	public void setDepends(String[] depends) {
		this.depends = depends;
	}

	public boolean dependsOn(EvolutionHistory history) {
		return Arrays.asList(depends).contains(history.getModuleName());
	}
}
