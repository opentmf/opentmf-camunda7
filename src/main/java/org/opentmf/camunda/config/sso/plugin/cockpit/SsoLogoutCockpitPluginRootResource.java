package org.opentmf.camunda.config.sso.plugin.cockpit;

import jakarta.ws.rs.Path;
import org.camunda.bpm.cockpit.plugin.resource.AbstractCockpitPluginRootResource;
import org.opentmf.camunda.config.sso.plugin.SsoLogoutPluginConstants;

/**
 * @author Abdullah Beker
 */
@Path("plugin/" + SsoLogoutPluginConstants.ID)
public class SsoLogoutCockpitPluginRootResource extends AbstractCockpitPluginRootResource {

  public SsoLogoutCockpitPluginRootResource() {
    super(SsoLogoutPluginConstants.ID);
  }
}
