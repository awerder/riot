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
package org.riotfamily.common.web.mapping;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.common.beans.MapWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.util.ClassUtils;
import org.springframework.web.servlet.handler.AbstractHandlerMapping;

/**
 * Abstract base class for mappings that support reverse look-ups.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public abstract class AbstractReverseHandlerMapping 
		extends AbstractHandlerMapping implements ReverseHandlerMapping {
	
	/**
	 * Returns the URL of a mapped handler.
	 * @param handlerName The name of the handler
	 * @param prefix Optional prefix to sort out ambiguities
	 * @param attributes Optional attributes to fill out wildcards. Can either 
	 * 		  be <code>null</code>, a primitive wrapper, a Map or a bean.
	 * @param request The current request
	 */
	public String getUrlForHandler(String handlerName, String prefix, 
			Object attributes, UrlResolverContext context) {
		
		Map<String, ?> defaults = getDefaults(context);
		return getUrlForHandler(handlerName, prefix, attributes, defaults, context);
	}
	
	/**
	 * Returns a Map of default values that are used to build URLs. The default
	 * implementation return <code>null</code>.
	 */
	protected Map<String, ?> getDefaults(UrlResolverContext context) {
		return null;
	}
	
	/**
	 * Returns the URL of a mapped handler.
	 * @param handlerName The name of the handler
	 * @param prefix Optional prefix to sort out ambiguities
	 * @param attributes Optional attributes to fill out wildcards. Can either 
	 * 		  be <code>null</code>, a primitive wrapper, a Map or a bean.
	 * @param defaults Optional Map of default values that are used when no
	 * 		  attribute value was provided for a certain wildcard.
	 * @param request The current request
	 */
	@SuppressWarnings("unchecked")
	protected String getUrlForHandler(String handlerName, String prefix, 
			Object attributes, Map<String, ?> defaults, 
			UrlResolverContext context) {
		
		if (attributes == null) {
			return getUrlForHandler(handlerName, defaults, prefix, context);
		}
		if (attributes instanceof Map) {
			return getUrlForHandlerWithMap(handlerName, 
					(Map<String, ?>) attributes, 
					defaults, prefix, context); 
		}
		
		if (attributes instanceof String ||
				ClassUtils.isPrimitiveOrWrapper(attributes.getClass())) {
			
			return getUrlForHandlerWithAttribute(handlerName, attributes, 
					defaults, prefix, context);
		}
		
		if (attributes instanceof Collection) {
			Collection c = (Collection) attributes;
			attributes = c.toArray(new Object[c.size()]);
		}
		
		if (attributes.getClass().isArray()) {
			return getUrlForHandlerWithArray(handlerName, (Object[]) attributes, prefix, context);
		}
		
		return getUrlForHandlerWithBean(handlerName, attributes, defaults, 
				prefix, context);
	}

	/**
	 * Returns the URL for a handler without any extra wildcards. If the pattern
	 * contains wildcards all of them must be present in the defaults map.
	 */
	private String getUrlForHandler(String handlerName, 
			Map<String, ?> defaults, String prefix, 
			UrlResolverContext context) {
		
		AttributePattern p = getPatternForHandler(handlerName, prefix, context, 
				null, defaults, 0);
		
		if (p == null) {
			return null;
		}
		String url = p.fillInAttribute(null, defaults);
		return addServletMappingIfNecessary(url, context);
	}
	
	/**
	 * Returns the URL for a handler with one custom wildcard. If the pattern
	 * contains wildcards all of them must be present in the defaults map.
	 */
	private String getUrlForHandlerWithAttribute(String handlerName, 
			Object attribute, Map<String, ?> defaults, String prefix, 
			UrlResolverContext context) {
		
		AttributePattern p = getPatternForHandler(handlerName, prefix, context, 
				null, defaults, 1);
		
		if (p == null) {
			return null;
		}
		String url = p.fillInAttribute(attribute, defaults);
		return addServletMappingIfNecessary(url, context);
	}
	
	private String getUrlForHandlerWithArray(String handlerName, 
			Object[] attributes, String prefix, 
			UrlResolverContext context) {
		
		AttributePattern p = getPatternForHandler(handlerName, prefix, context, 
				null, null, attributes.length);
		
		if (p == null) {
			return null;
		}
		String url = p.fillInAttributes(attributes);
		return addServletMappingIfNecessary(url, context);
	}
	
	/**
	 * Returns the URL for a handler that is mapped with multiple wildcards.
	 * The wildcard replacements are taken from the given Map.
	 */
	private String getUrlForHandlerWithMap(String beanName, 
			Map<String, ?> attributes, Map<String, ?> defaults, 
			String prefix, UrlResolverContext context) {
		
		List<AttributePattern> patterns = getPatternsForHandler(beanName, prefix, context);
		if (patterns == null || patterns.isEmpty()) {
			return null;
		}
		for (AttributePattern p : patterns) {
			if (p.canFillIn(attributes, defaults, 0)) {
				String path = p.fillInAttributes(new MapWrapper(attributes), defaults);
				return addServletMappingIfNecessary(path, context);
			}
		}
		return null;
	}
	
	/**
	 * Returns the URL for a handler that is mapped with multiple wildcards.
	 * The wildcard replacements are taken from the given bean or the map
	 * of defaults, in case the bean has no matching property or the property
	 * value is <code>null</code>.
	 * @throws IllegalArgumentException if more than one mapping is registered
	 */
	private String getUrlForHandlerWithBean(String beanName, Object bean,
			Map<String, ?> defaults, String prefix, 
			UrlResolverContext context) {
		
		AttributePattern p = getPatternForHandler(beanName, prefix, context);
		if (p != null) {
			String path = p.fillInAttributes(new BeanWrapperImpl(bean), defaults);
			return addServletMappingIfNecessary(path, context);
		}
		return null;
	}
	
	/**
	 * Returns all {@link AttributePattern patterns} for the handler with the
	 * specified name that start with the given prefix.
	 *  
	 * @param handlerName Name of the handler
	 * @param prefix Optional prefix to narrow the the result
	 * @param request The current request
	 */
	protected List<AttributePattern> getPatternsForHandler(String handlerName, String prefix, 
			UrlResolverContext context) {
		
		List<AttributePattern> patterns = getPatternsForHandler(handlerName, context);
		if (patterns == null || patterns.isEmpty() 
				|| prefix == null || prefix.length() == 0) {
			
			return patterns;
		}
		ArrayList<AttributePattern> matchingPatterns = new ArrayList<AttributePattern>();
		for (AttributePattern p : patterns) {
			if (p.startsWith(prefix)) {
				matchingPatterns.add(p);
			}
		}
		return matchingPatterns;
	}
	
	/**
	 * Subclasses must implement this method and return all 
	 * {@link AttributePattern patterns} for the handler with the specified
	 * name.
	 */
	protected abstract List<AttributePattern> getPatternsForHandler(
			String beanName, UrlResolverContext context);
	
	protected String addServletMappingIfNecessary(String path, 
			UrlResolverContext context) {
		
		return path;
	}
	
	/**
	 * Returns the pattern for the handler with the given name that contains
	 * all the given wildcards.
	 * @throws IllegalArgumentException if more than one mapping is registered
	 */
	protected AttributePattern getPatternForHandler(String handlerName, 
			String prefix, UrlResolverContext context, 
			Map<String, Object> attributes, Map<String, ?> defaults, 
			int anonymousWildcards) {
		
		List<AttributePattern> patterns = getPatternsForHandler(handlerName, prefix, context);
		if (patterns == null || patterns.isEmpty()) {
			return null;
		}

		AttributePattern result = null;
		Iterator<AttributePattern> it = patterns.iterator();
		while (it.hasNext()) {
			AttributePattern p = it.next();
			if (p.canFillIn(attributes, defaults, anonymousWildcards)) {
				if (result != null) {
					throw new IllegalArgumentException("Exactly one mapping with "
							+ anonymousWildcards + " anonymous wildcards is required "
							+ "for hander "	+ handlerName);
				}
				result = p;
			}
		}
		if (result == null) {
			throw new IllegalArgumentException("Could not find mapping with "
					+ anonymousWildcards + " anonymous wildcards "
					+ "for hander "	+ handlerName);
		}
		return result;
	}
	
	/**
	 * Returns the pattern for the handler with the given name.
	 * @throws IllegalArgumentException if more than one mapping is registered
	 */
	protected AttributePattern getPatternForHandler(String handlerName, 
			String prefix, UrlResolverContext context) {
		
		List<AttributePattern> patterns = getPatternsForHandler(handlerName, prefix, context);
		if (patterns == null || patterns.isEmpty()) {
			return null;
		}
		if (patterns.size() != 1) {
			throw new IllegalArgumentException("Ambigious mapping - more than " 
					+ "one pattern is registered for hander " + handlerName);
		}
		return patterns.get(0);
	}
	
	/**
	 * Exposes the name of the matched handler as request attribute, unless
	 * the attribute is already present.
	 * @see #TOP_LEVEL_HANDLER_NAME_ATTRIBUTE
	 */
	protected void exposeHandlerName(String beanName, HttpServletRequest request) {
		if (request.getAttribute(TOP_LEVEL_HANDLER_NAME_ATTRIBUTE) == null) {
			request.setAttribute(TOP_LEVEL_HANDLER_NAME_ATTRIBUTE, beanName);
		}
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> getWildcardAttributes(HttpServletRequest request) {
		return (Map<String, Object>) request.getAttribute(AttributePattern.EXPOSED_ATTRIBUTES);
	}
}
