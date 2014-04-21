package org.sonar.plugins.odi.rules;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;

import javax.xml.bind.Marshaller;

import org.sonar.api.BatchExtension;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.Project;
import org.sonar.api.utils.SonarException;
import org.sonar.api.utils.TimeProfiler;
import org.sonar.plugins.odi.OdiVersion;
import org.sonar.plugins.odi.utils.OdiUtils;

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
public class OdiExecutor implements BatchExtension {

	private RulesProfile rulesProfile;
	private OdiProfileExporter profileExporter;
	private Project project;

	public OdiExecutor(RulesProfile rulesProfile,
			OdiProfileExporter profileExporter, Project project) {
		this.rulesProfile = rulesProfile;
		this.profileExporter = profileExporter;
		this.project = project;
	}

	public File execute() {
		TimeProfiler profiler = new TimeProfiler().start("Execute odi "
				+ OdiVersion.getVersion());

		OdiUtils.LOG.warn("make the treatment");

		File target = new File(project.getFileSystem().getBuildDir()
				.getAbsolutePath()
				+ "/odiReport.xml");
		try {
			target.createNewFile();

			FileWriter fw = new FileWriter(target, true);
			BufferedWriter output = new BufferedWriter(fw);

			// en attendant la creation de l'analyse ...
			StringBuffer sb = new StringBuffer();
			sb.append("<BugCollection timestamp='1330251124706' analysisTimestamp='1330256856798' sequence='0' release='' version='2.0.0'>");
			sb.append("<Project projectName='My ODI Sonar plugin' />");
			sb.append("<BugInstance type='org.codenarc-odi.rule.exceptions.CatchErrorRule'>");
			sb.append("	<LongMessage>ceci est un long message</LongMessage>");
			sb.append("	<SourceLine start='8' classname='org.sonar.plugins.odi.foundation.Odi' sourcepath='org/sonar/plugins/odi/foundation/Odi.java' sourcefile='Odi.java' end='15'>");
			sb.append("		<Message>At Odi.java:[lines 8-15]</Message>");
			sb.append("	</SourceLine>");
			sb.append("</BugInstance>");
			sb.append("</BugCollection>");

			output.write(sb.toString());

			output.flush();

			output.close();

		} catch (IOException e) {
			throw new SonarException("The odi XML report can't be create at '"
					+ project.getFileSystem().getBuildDir().getAbsolutePath()
					+ "/odiReport.xml'");

		}

		profiler.stop();
		return target;
	}

	private File getConfiguration() {
		try {
			StringWriter writer = new StringWriter();
			profileExporter.exportProfile(rulesProfile, writer);
			return project.getFileSystem().writeToWorkingDirectory(
					writer.toString(), "checkProfile.xml");
		} catch (IOException e) {
			throw new SonarException("Can not generate ODI configuration file",
					e);
		}
	}

}
