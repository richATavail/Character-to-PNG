/*
 * FileUtility.java
 * Copyright © 2018, Richard Arriaga.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * * Neither the name of the copyright holder nor the names of the contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE..
 */
package raa.chartopng;
import org.jetbrains.annotations.NotNull;
import raa.chartopng.AppRuntime.ExitCode;
import raa.configuration.GenerationPlanConfigurator;
import raa.configuration.GeneratorPlan;
import raa.utility.FileUtility;
import raa.utility.configuration.ConfigurationException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * The main application for generation of font PNGs using an XML configuration
 * file.
 *
 * <p>
 * <strong>NOTE:</strong> This method expects high volumes of files to be
 * written, so it writes files concurrently. If smaller amounts of files are
 * expected to be written, in order to create samples more quickly, utilize
 * {@link ConsoleApplication}.
 * </p>
 *
 * @author Richard Arriaga
 */
public class XMLApplication
{
	/**
	 * The main loop of the XML application.
	 *
	 * @param commandLineArguments
	 *        The location of the XML generator plan file.
	 */
	public static void main(String[] commandLineArguments)
	{
		AppRuntime.initialize();
		final Path configurationPath;
		if (AppRuntime.console().readBoolean("Use custom config file? (y/n) "))
		{
			configurationPath = Paths.get(FileUtility.platformAppropriatePath(
				AppRuntime.console().readNonEmptyString(
					"Enter configuration file location: ")));
		}
		else
		{
			configurationPath = GeneratorPlan.defaultConfigurationPath;
		}
		AppRuntime.console().println("Evaluating work requirements...");
		final AtomicLong startTime =
			new AtomicLong(Instant.now().toEpochMilli());
		final GeneratorPlan configuration;
		try
		{
			configuration = newGeneratorPlan(configurationPath);
		}
		catch (final @NotNull IOException | ConfigurationException e)
		{
			System.err.format(
				"Plan creation failed [%s].",
				configurationPath.toAbsolutePath());
			e.printStackTrace(System.err);
			ExitCode.CONFIGURATION_ERROR.shutdown();
			return;
		}

		final List<Runnable> creationJobs = new ArrayList<>();
		final AtomicInteger workCount = new AtomicInteger(0);
		final List<String> createdDirectories = new ArrayList<>();

		configuration.selections().forEach(selection ->
			createdDirectories.addAll(PNGGenerator.generateImageFiles(
				FileUtility.platformAppropriatePath(
					configuration.targetDirectory()),
				selection,
				creationJobs,
				workCount,
				startTime)));

		AppRuntime.console().println("File count: " + workCount.get());
		AppRuntime.console().println("Output directories:");
		createdDirectories.forEach(
			dir -> AppRuntime.console().println("\t" + dir));
		AppRuntime.console().println("Generating files...");
		for (final Runnable job : creationJobs)
		{
			AppRuntime.scheduleTask(job);
		}
		AppRuntime.block();
	}

	/**
	 * Answer an appropriate {@link GeneratorPlan}.
	 *
	 * @param configurationPath
	 *        The {@linkplain Path path} to the XML configuration file.
	 * @return A {@linkplain GeneratorPlan#isValid() valid} {@code
	 *         Configuration}.
	 * @throws IOException
	 *         If the XML configuration file could not be read completely for
	 *         any reason.
	 * @throws ConfigurationException
	 *         If configuration failed for any reason.
	 */
	private static @NotNull GeneratorPlan newGeneratorPlan (
		final @NotNull Path configurationPath)
	throws IOException, ConfigurationException
	{
		final GeneratorPlan configuration = new GeneratorPlan();
		try (
			final InputStream in = Files.newInputStream(
				configurationPath, StandardOpenOption.READ))
		{
			final GenerationPlanConfigurator configurator = new GenerationPlanConfigurator(
				configuration, in);
			configurator.updateConfiguration();
		}
		return configuration;
	}
}
