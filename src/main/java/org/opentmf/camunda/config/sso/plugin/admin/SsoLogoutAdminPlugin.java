package org.opentmf.camunda.config.sso.plugin.admin;

import java.util.Set;
import org.camunda.bpm.admin.plugin.spi.impl.AbstractAdminPlugin;
import org.opentmf.camunda.config.sso.plugin.SsoLogoutPluginConstants;

/**
 * @author Abdullah Beker
 */
public class SsoLogoutAdminPlugin extends AbstractAdminPlugin {

  @Override
  public Set<Class<?>> getResourceClasses() {
    return Set.of(SsoLogoutAdminPluginRootResource.class);
  }

  @Override
  public String getId() {
    return SsoLogoutPluginConstants.ID;
  }
}
