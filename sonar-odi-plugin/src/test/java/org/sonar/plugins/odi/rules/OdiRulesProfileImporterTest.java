package org.sonar.plugins.odi.rules;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.rules.ActiveRule;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RuleFinder;
import org.sonar.api.utils.ValidationMessages;
import org.sonar.plugins.odi.rules.OdiConstants;
import org.sonar.plugins.odi.rules.OdiProfileImporter;
import org.sonar.test.TestUtils;

import java.io.Reader;
import java.io.StringReader;

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
public class OdiRulesProfileImporterTest {

	private OdiProfileImporter importer;
	private ValidationMessages messages;

	@Before
	public void setUp() {
		importer = new OdiProfileImporter(newRuleFinder());
		messages = ValidationMessages.create();
	}

	@Test
	public void shouldImportProfile() {
		Reader reader = new StringReader(
				TestUtils
						.getResourceContent("/org/sonar/plugins/odi/codenarc/CodeNarcProfileExporterTest/exportProfile.xml"));

		RulesProfile profile = importer.importProfile(reader, messages);

		assertThat(messages.hasErrors(), is(false));
		assertThat(profile.getActiveRules().size(), is(2));
		assertThat(profile.getActiveRule(OdiConstants.REPOSITORY_KEY,
				"org.codenarc.rule.basic.AddEmptyStringRule"), notNullValue());
		assertThat(profile.getActiveRule(OdiConstants.REPOSITORY_KEY,
				"org.codenarc.rule.size.ClassSizeRule"), notNullValue());
	}

	@Test
	public void shouldImportParameters() {
		Reader reader = new StringReader(
				TestUtils
						.getResourceContent("/org/sonar/plugins/odi/codenarc/CodeNarcProfileExporterTest/exportParameters.xml"));
		RulesProfile profile = importer.importProfile(reader, messages);

		assertThat(messages.hasErrors(), is(false));
		ActiveRule activeRule = profile.getActiveRule(
				OdiConstants.REPOSITORY_KEY,
				"org.codenarc.rule.size.ClassSizeRule");
		assertThat(activeRule.getActiveRuleParams().size(), is(1));
		assertThat(activeRule.getParameter("maxLines"), is("20"));
	}

	private RuleFinder newRuleFinder() {
		RuleFinder ruleFinder = mock(RuleFinder.class);
		when(ruleFinder.findByKey(anyString(), anyString())).thenAnswer(
				new Answer<Rule>() {
					public Rule answer(InvocationOnMock invocation)
							throws Throwable {
						String repositoryKey = (String) invocation
								.getArguments()[0];
						String ruleKey = (String) invocation.getArguments()[1];
						Rule rule = Rule.create()
								.setRepositoryKey(repositoryKey)
								.setKey(ruleKey);
						if (StringUtils.equals(ruleKey,
								"org.codenarc.rule.size.ClassSizeRule")) {
							rule.createParameter("maxLines");
						}
						return rule;
					}
				});
		return ruleFinder;
	}
}
