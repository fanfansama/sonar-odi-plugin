package org.sonar.plugins.odi.widget;

import org.sonar.api.web.*;

/**
 * 
 * @author Francois_Berthault
 *
 */
@UserRole(UserRole.USER)
@Description("Show how to use a Widget API")
@WidgetProperties({
        @WidgetProperty(key="param1",
                        description="This is a mandatory parameter",
                        optional=false
        ),
        @WidgetProperty(key="max",
                        description="max threshold",
                        type=WidgetPropertyType.INTEGER,
                        defaultValue="80"
        ),
        @WidgetProperty(key="param2",
                        description="This is an optional parameter"
        ),
        @WidgetProperty(key="floatprop",
                        description="test description"
        )
})
@Deprecated
public class SampleDashboardWidget extends AbstractRubyTemplate implements RubyRailsWidget {

  public String getId() {
    return "sample";
  }

  public String getTitle() {
    return "Sample";
  }

  @Override
  protected String getTemplatePath() {
    return "/sample_dashboard_widget.html.erb";
  }
}