package org.riotfamily.pages.riot.security;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.pages.mapping.PageResolver;
import org.riotfamily.pages.model.Page;
import org.riotfamily.pages.model.Site;
import org.riotfamily.riot.security.auth.RiotUser;
import org.riotfamily.riot.security.policy.AuthorizationPolicy;

public class SiteUserPolicy implements AuthorizationPolicy {
	
	private PageResolver pageResolver;
	
	private int order = Integer.MAX_VALUE - 2;
	
	public SiteUserPolicy(PageResolver pageResolver) {
		this.pageResolver = pageResolver;
	}
	
    public int getOrder() {
		return this.order;
	}

    public void setOrder(int order) {
		this.order = order;
	}
	
	public Permission getPermission(RiotUser riotUser, String action, Object object) {
		if (riotUser instanceof SiteUser) {
			SiteUser user = (SiteUser) riotUser;
			
			if (isLimited(user)) {
				boolean denied = false;
				if (object != null && object.getClass().isArray()) {
					Object[] objects = (Object[]) object;
					for (Object o : objects) {
						denied |= isDenied(user, o);
					}
				}
				else {
					denied |= isDenied(user, object);
				}
				if (denied) {
					return Permission.DENIED;
				}
			}
		}
		return Permission.ABSTAIN;
	}
		
	private boolean isDenied(SiteUser user, Object object) {
		if (object instanceof Site) {
			Site site = (Site) object;
			return !user.getSites().contains(site);
		}
		if (object instanceof HttpServletRequest) {
			HttpServletRequest request = (HttpServletRequest) object;
			Page page = pageResolver.getPage(request);
			return page != null && !user.getSites().contains(page.getSite());
		}
		return false;
	}

	protected boolean isLimited(SiteUser siteUser) {
		Set<Site> sites = siteUser.getSites();
		if (sites != null && sites.size() > 0) {
			return true;
		}
		return false;
	}
	
}
