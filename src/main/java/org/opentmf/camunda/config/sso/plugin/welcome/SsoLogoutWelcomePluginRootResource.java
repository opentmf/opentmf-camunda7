package org.opentmf.camunda.config.sso.plugin.welcome;

import jakarta.ws.rs.Path;
import org.camunda.bpm.welcome.resource.AbstractWelcomePluginRootResource;
import org.opentmf.camunda.config.sso.plugin.SsoLogoutPluginConstants;

/**
 * @author Abdullah Beker
 */
@Path("plugin/" + SsoLogoutPluginConstants.ID)
public class SsoLogoutWelcomePluginRootResource extends AbstractWelcomePluginRootResource {

  public SsoLogoutWelcomePluginRootResource() {
    super(SsoLogoutPluginConstants.ID);
  }
}
