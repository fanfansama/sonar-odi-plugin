package org.sonar.plugins.odi;

import java.io.File;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.measures.Measure;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.JavaFile;
import org.sonar.api.resources.Project;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RuleFinder;
import org.sonar.api.rules.Violation;
import org.sonar.api.utils.Logs;
import org.sonar.plugins.odi.foundation.Odi;
import org.sonar.plugins.odi.rules.OdiConstants;
import org.sonar.plugins.odi.rules.OdiExecutor;
import org.sonar.plugins.odi.rules.OdiXmlReportParser;
import org.sonar.plugins.odi.utils.OdiUtils;
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
public class OdiSensor implements Sensor {

	private String serverUrl;
	private String driver;
	private String username;
	private String password;

	private OdiExecutor executor;
	private RulesProfile profile;
	private RuleFinder ruleFinder;

	public OdiSensor(RulesProfile profile, RuleFinder ruleFinder,
			OdiExecutor executor) {
		this.profile = profile;
		this.ruleFinder = ruleFinder;
		this.executor = executor;
	}

	private void initParams(Project project) {
		Configuration configuration = project.getConfiguration();
		serverUrl = configuration.getString(OdiPlugin.SERVER_URL_PROPERTY);
		username = configuration.getString(OdiPlugin.USERNAME_PROPERTY);
		password = configuration.getString(OdiPlugin.PASSWORD_PROPERTY);
		driver = configuration.getString(OdiPlugin.DRIVER_PROPERTY);

	}

	private boolean isMandatoryParametersNotEmpty() {
		return StringUtils.isNotEmpty(serverUrl)
				&& StringUtils.isNotEmpty(driver)
				&& StringUtils.isNotEmpty(username)
				&& StringUtils.isNotEmpty(password);
	}

	public boolean shouldExecuteOnProject(Project project) {
		return project.getLanguage().equals(Odi.INSTANCE);
	}

	public void analyse(Project project, SensorContext sensorContext) {

		initParams(project);

		if (!isMandatoryParametersNotEmpty()) {
			OdiUtils.LOG
					.warn("jdbc, driver, username and password must not be empty.");
			return;
		}

		File report = getExistingOdiReportFile(project,
				OdiPlugin.ODI_METRICS_REPORT_PATH);
		if (report == null) {
			report = executor.execute();
		}

		OdiXmlReportParser reportParser = new OdiXmlReportParser(report);
		List<OdiXmlReportParser.Violation> fbViolations = reportParser
				.getViolations();
		for (OdiXmlReportParser.Violation fbViolation : fbViolations) {
			Rule rule = ruleFinder.findByKey(OdiConstants.REPOSITORY_KEY,
					fbViolation.getType());
			if (rule != null) { // ignore violations from report, if rule not
								// activated in Sonar
				JavaFile resource = new JavaFile(
						fbViolation.getSonarJavaFileKey());
				if (sensorContext.getResource(resource) != null) {
					Violation violation = Violation.create(rule, resource)
							.setLineId(fbViolation.getStart())
							.setMessage(fbViolation.getLongMessage());
					sensorContext.saveViolation(violation);
				} else {
					// TODO : gérer les sources, j'ai by-passé les fichiers de
					// sources
					// OdiUtils.LOG.warn("resource not found : {}",fbViolation.getType());

					Violation violation = Violation.create(rule, resource)
							.setLineId(fbViolation.getStart())
							.setMessage(fbViolation.getLongMessage());
					sensorContext.saveViolation(violation);
				}
			} else {
				Logs.INFO.warn("odi rule '{}' not active in Sonar.",
						fbViolation.getType());
			}
		}

		saveLabelMeasure(sensorContext);
		saveNumericMeasure(sensorContext);
	}

	protected File getExistingOdiReportFile(Project project,
			String reportProperty) {
		File report = getReportFromProperty(project, reportProperty);
		if (report == null || !report.exists() || !report.isFile()) {
			OdiUtils.LOG.warn("Odi report " + reportProperty
					+ " not found at {}", report);
			report = null;
		}
		return report;
	}

	private File getReportFromProperty(Project project, String reportProperty) {
		String path = (String) project.getProperty(reportProperty);
		if (path != null) {
			return project.getFileSystem().resolvePath(path);
		}
		return null;
	}

	private void saveNumericMeasure(SensorContext context) {
		// Sonar API includes many libraries like commons-lang and
		// google-collections
		context.saveMeasure(SampleMetrics.RANDOM, RandomUtils.nextDouble());
	}

	private void saveLabelMeasure(SensorContext context) {
		Measure measure = new Measure(SampleMetrics.MESSAGE, "Hello World!");
		context.saveMeasure(measure);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
}
