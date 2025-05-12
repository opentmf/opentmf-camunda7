package org.opentmf.camunda.config.sso.plugin.tasklist;

import java.util.Set;
import org.camunda.bpm.tasklist.plugin.spi.impl.AbstractTasklistPlugin;
import org.opentmf.camunda.config.sso.plugin.SsoLogoutPluginConstants;

/**
 * @author Abdullah Beker
 */
public class SsoLogoutTasklistPlugin extends AbstractTasklistPlugin {

  @Override
  public Set<Class<?>> getResourceClasses() {
    return Set.of(SsoLogoutTasklistPluginRootResource.class);
  }

  @Override
  public String getId() {
    return SsoLogoutPluginConstants.ID;
  }
}
