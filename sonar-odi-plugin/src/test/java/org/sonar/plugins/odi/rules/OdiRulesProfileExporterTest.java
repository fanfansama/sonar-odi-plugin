package org.sonar.plugins.odi.rules;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.StringWriter;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RulePriority;
import org.sonar.plugins.odi.foundation.Odi;
import org.sonar.plugins.odi.rules.OdiConstants;
import org.sonar.plugins.odi.rules.OdiProfileExporter;
import org.sonar.test.TestUtils;

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
public class OdiRulesProfileExporterTest {
	private OdiProfileExporter exporter;

	@Before
	public void setUp() {
		exporter = new OdiProfileExporter();
	}

	@Test
	public void shouldSetMimeType() {
		assertThat(exporter.getMimeType(), is("application/xml"));
	}

	@Test
	public void shouldExportProfile() throws Exception {
		RulesProfile profile = RulesProfile.create("Sonar Odi Way", Odi.KEY);

		Rule rule = Rule.create(OdiConstants.REPOSITORY_KEY,
				"org.codenarc.rule.basic.AddEmptyStringRule",
				"Add Empty String");

		profile.activateRule(rule, RulePriority.MAJOR);

		rule = Rule.create(OdiConstants.REPOSITORY_KEY,
				"org.codenarc.rule.size.ClassSizeRule", "Class Size");

		profile.activateRule(rule, RulePriority.MAJOR);

		StringWriter writer = new StringWriter();
		exporter.exportProfile(profile, writer);

		TestUtils
				.assertSimilarXml(
						TestUtils
								.getResourceContent("/org/sonar/plugins/odi/codenarc/CodeNarcProfileExporterTest/exportProfile.xml"),
						writer.toString());
	}

	@Test
	public void shouldExportParameters() throws Exception {
		RulesProfile profile = RulesProfile.create("Sonar Odi Way", Odi.KEY);
		Rule rule = Rule.create(OdiConstants.REPOSITORY_KEY,
				"org.codenarc.rule.size.ClassSizeRule", "Class Size");
		rule.createParameter("maxLines");
		profile.activateRule(rule, RulePriority.MAJOR).setParameter("maxLines",
				"20");

		StringWriter writer = new StringWriter();
		exporter.exportProfile(profile, writer);

		TestUtils
				.assertSimilarXml(
						TestUtils
								.getResourceContent("/org/sonar/plugins/odi/codenarc/CodeNarcProfileExporterTest/exportParameters.xml"),
						writer.toString());
	}
}
