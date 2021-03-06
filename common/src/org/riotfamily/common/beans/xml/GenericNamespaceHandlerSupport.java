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
package org.riotfamily.common.beans.xml;

import org.springframework.beans.factory.xml.BeanDefinitionDecorator;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public abstract class GenericNamespaceHandlerSupport extends NamespaceHandlerSupport {

	/**
	 * Registers a {@link SpringBeanDefinitionParser} for the given elementName.
	 */
	protected void registerSpringBeanDefinitionParser(String elementName) {
		registerBeanDefinitionParser(elementName, new SpringBeanDefinitionParser());
	}

	/**
	 * Registers a {@link SpringBeanDefinitionParser} and a decorator
	 * for the given elementName.
	 */
	protected void registerSpringBeanDefinitionParser(String elementName,
			BeanDefinitionDecorator decorator) {

		registerSpringBeanDefinitionParser(elementName);
		registerBeanDefinitionDecorator(elementName, decorator);
	}

	/**
	 * Registers a {@link GenericBeanDefinitionParser} for the given elementName
	 * that creates BeanDefinitions for the specified class.
	 */
	protected GenericBeanDefinitionParser register(String elementName, Class<?> beanClass) {
		GenericBeanDefinitionParser parser = new GenericBeanDefinitionParser(beanClass);
		registerBeanDefinitionParser(elementName, parser);
		return parser;
	}
	
	/**
	 * Registers a {@link GenericBeanDefinitionParser} for the given elementName.
	 * The bean class is passed as string to avoid runtime dependencies. If a
	 * dependency is missing, a warning is logged and the element is ignored. 
	 */
	protected GenericBeanDefinitionParser register(String elementName, 
			String className) {
		
		GenericBeanDefinitionParser parser = new GenericBeanDefinitionParser(className);
		registerBeanDefinitionParser(elementName, parser);
		return parser;
	}

	/**
	 * Registers a {@link GenericBeanDefinitionParser} for the given elementName
	 * that creates BeanDefinitions for the specified class. Additionally a
	 * decorator is registered for the elementName.
	 */
	protected GenericBeanDefinitionParser register(String elementName,
			Class<?> beanClass, BeanDefinitionDecorator decorator) {

		registerBeanDefinitionDecorator(elementName, decorator);
		return register(elementName, beanClass);
	}
	
	/**
	 * Registers a {@link GenericBeanDefinitionParser} for the given elementName
	 * that creates BeanDefinitions for the specified class. The bean class is 
	 * passed as string to avoid runtime dependencies. If a dependency is 
	 * missing, a warning is logged and support for the element is disabled.
	 * Additionally a decorator is registered for the elementName.
	 */
	protected GenericBeanDefinitionParser register(String elementName,
			String className, BeanDefinitionDecorator decorator) {

		registerBeanDefinitionDecorator(elementName, decorator);
		return register(elementName, className);
	}

	/**
	 * Registers a parser and decorator for the given elementName.
	 */
	protected void register(String elementName, BeanDefinitionParser parser,
			BeanDefinitionDecorator decorator) {

		registerBeanDefinitionParser(elementName, parser);
		registerBeanDefinitionDecorator(elementName, decorator);
	}
	
}
