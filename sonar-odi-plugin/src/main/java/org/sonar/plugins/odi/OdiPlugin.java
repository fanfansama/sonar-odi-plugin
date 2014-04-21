package org.sonar.plugins.odi;

import java.util.Arrays;
import java.util.List;

import org.sonar.api.Plugin;
import org.sonar.api.Properties;
import org.sonar.api.Property;
import org.sonar.plugins.odi.foundation.Odi;
import org.sonar.plugins.odi.rules.OdiExecutor;
import org.sonar.plugins.odi.rules.OdiProfileExporter;
import org.sonar.plugins.odi.rules.OdiProfileImporter;
import org.sonar.plugins.odi.rules.OdiRuleRepository;
import org.sonar.plugins.odi.rules.SonarWayProfile;
import org.sonar.plugins.odi.widget.SampleDashboardWidget;
import org.sonar.plugins.odi.widget.SampleMetrics;

/**
 * Sonar Odi Plugin
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02
 * 
 * @author Francois_Berthault
 * 
 */
@Properties({
    @Property(
            key = OdiPlugin.ODI_METRICS_REPORT_PATH,
            name = "Report file",
            description = "Path (absolute or relative) to Odi Metrics XML report in case generation is not handle by the plugin.",
            module = true,
            project = true,
            global = false
        )	,
		@Property(key = OdiPlugin.SERVER_URL_PROPERTY, 	defaultValue = "", name = "Server URL", description = "Example : jdbc:mysql://localhost:3306/sonar", global = true, project = true, module = false),
		@Property(key = OdiPlugin.DRIVER_PROPERTY, 		defaultValue = "", name = "Driver", description = "com.mysql.jdbc.Driver", global = true, project = true, module = false),
		@Property(key = OdiPlugin.USERNAME_PROPERTY, 	defaultValue = "", name = "Username", global = true, project = true, module = false),
		@Property(key = OdiPlugin.PASSWORD_PROPERTY, 	defaultValue = "", name = "Password", global = true, project = true, module = false)

})
public class OdiPlugin implements Plugin {

	public final static String ODI_METRICS_REPORT_PATH = "sonar.odi.metrics.reportPath";
	
	public final static String SERVER_URL_PROPERTY = "sonar.odi.jdbc.url";
	public final static String DRIVER_PROPERTY = "sonar.odi.jdbc.driver";
	public final static String USERNAME_PROPERTY = "sonar.odi.login.secured";
	public final static String PASSWORD_PROPERTY = "sonar.odi.password.secured";

	/**
	 * @deprecated this is not used anymore
	 */
	public String getKey() {
		return Odi.KEY;
	}

	/**
	 * @deprecated this is not used anymore
	 */
	public String getName() {
		return "My ODI - Sonar plugin";
	}

	/**
	 * @deprecated this is not used anymore
	 */
	public String getDescription() {
		return "Analysis of ODI projects";
	}

	// This is where you're going to declare all your Sonar extensions
	@SuppressWarnings("unchecked")
	public List getExtensions() {
		return Arrays.asList(

				// ODI rules CodeNarc
				OdiRuleRepository.class, 
				OdiProfileExporter.class,
				OdiProfileImporter.class, 
				SonarWayProfile.class,
				// violation->CodeNarcExecutor.class,
				// violation->CodeNarcXMLParser.class,

				// Analyse metrics
				OdiExecutor.class,
				
				// Foundation
				Odi.class,

				// main sensor
				OdiSensor.class,

				// metric sample
				SampleMetrics.class,

				// sample widget
				SampleDashboardWidget.class);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
}
