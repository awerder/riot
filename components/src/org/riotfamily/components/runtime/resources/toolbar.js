riot.Toolbar = Class.create({
	initialize: function() {
		this.edit = true;
		this.publish = true;
		this.buttons = $H({
			gotoRiot: new riot.ToolbarButton('gotoRiot', '${toolbarButton.gotoRiot}', null, riot.path),
			browse: new riot.ToolbarButton('browse', '${toolbarButton.browse}'),
			insert: new riot.ToolbarButton('insert', '${toolbarButton.insert}', '.riot-component-list'),
			remove: new riot.ToolbarButton('remove', '${toolbarButton.remove}', '.riot-component-list'),
			edit: new riot.ToolbarButton('edit', '${toolbarButton.edit}', '.riot-text-editor'),
			properties: new riot.ToolbarButton('properties', '${toolbarButton.properties}', '.riot-form'),
			move: new riot.ToolbarButton('move', '${toolbarButton.move}', '.riot-component-list'),
			logout: new riot.ToolbarButton('logout', '${toolbarButton.logout}'),
			discard: new riot.ToolbarButton('discard', '${toolbarButton.discard}'),
			publish: new riot.ToolbarButton('publish', '${toolbarButton.publish}')
		});

		this.buttons.get('logout').applyHandler = this.logout;
		
		var buttonElements = this.buttons.values().pluck('element');
		document.body.appendChild(this.element = RBuilder.node('div', {id: 'riot-toolbar'},
			RBuilder.node('div', {id: 'riot-toolbar-buttons'}, buttonElements)
		));
		document.body.appendChild(this.inspectorPanel = RBuilder.node('div', {id: 'riot-inspector'}));

		this.applyButton = new riot.ToolbarButton('apply', '${toolbarButton.apply}').activate();
		this.applyButton.enable = function() {
			this.enabled = true;
			new Effect.Appear(this.element, {duration: 0.4});
		}
		this.applyButton.disable = function() {
			this.enabled = false;
			this.element.hide();
		}
		this.applyButton.element.hide();
		this.element.appendChild(this.applyButton.element);
	},

	activate: function() {
		if (this.edit) { 
			var dirty = $$('.riot-dirty').any(function(e) {
				return !riotContainerIds || riotContainerIds.indexOf(
						parseInt(e.readAttribute('riot:containerId'))) != -1;
			})
			
			if (!dirty || !this.publish) this.disablePublishButtons();
			this.buttons.values().invoke('activate');
			this.buttons.get('browse').select();
		}
	},

	enablePublishButtons: function() {
		if (this.publish) {
			this.buttons.get('publish').enable();
			this.buttons.get('discard').enable();
		}
	},
	
	disablePublishButtons: function() {
		this.buttons.get('publish').disable();
		this.buttons.get('discard').disable();
	},
	
	buttonSelected: function(button) {
		if (this.selectedButton) {
			this.selectedButton.reset();
			this.setInspector(null);
		}
		this.selectedButton = button;
	},

	buttonDisabled: function(button) {
		if (this.selectedButton == button) {
			this.selectedButton = null;
		}
	},

	setInspector: function(inspector) {
		if (inspector != this.inspector) {
			this.removeInspector();
			if (inspector != null) {
				this.inspector = inspector;
				this.inspectorPanel.appendChild(inspector);
				this.showInspector();
			}
		}
	},

	removeInspector: function() {
		if (this.inspector) {
			Element.remove(this.inspector);
			this.inspector.onclose();
			this.inspector = null;
			this.hideInspector();
		}
	},

	showInspector: function() {
		if (this.inspector) {
			this.inspectorPanel.style.display = 'block';
		}
	},

	hideInspector: function() {
		this.inspectorPanel.style.display = 'none';
	},

	logout: function() {
		ComponentEditor.logout(function() {
			location.reload();
		});
	},
	
	renderProxy: function(doc) {
		var buttonElements = this.buttons.values().invoke('createProxyElement');
		var el = RBuilder.node('div', {id: 'riot-toolbar'},
			RBuilder.node('div', {id: 'riot-toolbar-buttons'}, buttonElements)
		);
		doc.body.appendChild(el);
		el.appendChild(this.applyButton.createProxyElement().show());
	}
})

riot.ToolbarButton = Class.create({
	initialize: function(handler, title, selector, href) {
		this.handler = handler;
		this.title = title.stripTags();
		this.selector = selector;
		this.element = this.createElement();
		
		if (href) {
			this.element.href = href;
			this.element.target = 'riot';
		}
		else {
			this.element.href = '#';
			this.element.onclick = this.onclick.bind(this);
		}
		this.enabled = true;
	},
	
	createElement: function() {
		return RBuilder.node('a', {
			id: 'riot-toolbar-button-' + this.handler,
			className: this.getClassName(),
			title: this.title
		});
	},
	
	createProxyElement: function() {
		var el = this.createElement();
		el.onclick = this.click.bind(this);
		return el;
	},
	
	getClassName: function() {
		var className = 'toolbar-button';
		if (this.selected) {
			className += '-active';
		}
		else if (!this.enabled) {
			className += '-disabled';
		}
		return className;
	},
	
	onclick: function() {
		if (this.active) this.click();
		return false;
	},

	select: function() {
		if (this.enabled && !this.selected) {
			this.selected = true;
			this.element.className = this.getClassName();
			riot.toolbar.buttonSelected(this);
			return true;
		}
		return false;
	},

	click: function() {
		if (this.select()) {
			this.applyHandler(true);
		}
	},

	activate: function() {
		this.active = true;
		this.element.className = this.getClassName();
		return this;
	},

	enable: function() {
		if (!this.enabled) {		
			this.enabled = true;
			this.element.className = this.getClassName();
		}		
		return this;
	},

	disable: function() {
		this.enabled = false;
		this.reset();
		if (riot.toolbar) riot.toolbar.buttonDisabled(this);
		return this;
	},

	reset: function() {
		if (this.selected) {
			this.selected = false;
			this.applyHandler(false);
		}
		this.element.className = this.getClassName();
		return this;
	},

	getHandlerTargets: function() {
		return document.body.select(this.selector).reject(function(el) {
			return el.up('.riot-child-dump');
		});
	},

	applyHandler: function(enable) {
		if (enable && this.precondition) {
			this.precondition(this.applyHandlerInternal.bind(this, true));
		}
		else {
			if (this.beforeApply) {
				this.beforeApply(enable);
			}
			this.applyHandlerInternal(enable);
			if (this.afterApply) {
				this.afterApply(enable);
			}
			if (enable && window.onRiotToolbarClick) {
				onRiotToolbarClick(this.handler);
			}
		}
	},
	
	applyHandlerInternal: function(enable) {
		dwr.engine.setActiveReverseAjax(true);
		if (this.selector) {
			var targets = this.getHandlerTargets();
			for (var i = 0; i < targets.length; i++) {
				var target = riot.getWrapper(targets[i], this.selector)
				var method = this.handler + (enable ? 'On' : 'Off');
				if (target[method]) {
					target[method]();
				}
			}
		}
		else {
			var method = this.handler + (enable ? 'On' : 'Off');
			if (riot[method]) {
				riot[method]();
			}
		}
	},
	
	reApplyHandler: function() {
		this.applyHandler(false);
		this.applyHandler(true);
	}
});

riot.toolbar = new riot.Toolbar();

riot.showNotification = function(message) {
	var el = $('riot-notification');
	if (!el) {
		el = new Element('div', {id: 'riot-notification'});
		document.body.appendChild(el);
	}
	el.innerHTML = message;
	dwr.engine.setActiveReverseAjax(false);
}
