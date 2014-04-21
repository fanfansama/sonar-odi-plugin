package org.sonar.plugins.odi.rules;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.sonar.api.rules.XMLRuleParser;
import org.sonar.plugins.odi.rules.OdiRuleRepository;

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
public class OdiRulesRuleRepositoryTest {

	@Test
	public void test() {
		OdiRuleRepository repo = new OdiRuleRepository(new XMLRuleParser());
		assertThat(repo.createRules().size(), is(3));
	}

}
