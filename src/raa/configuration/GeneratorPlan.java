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
package raa.configuration;

import raa.configuration.State.Selection;
import org.jetbrains.annotations.NotNull;
import raa.utility.configuration.Configuration;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * A {@code GeneratorPlan} is a {@link Configuration} that represents the plan
 * for character PNG file generation.
 *
 * @author Richard Arriaga
 */
public class GeneratorPlan
implements Configuration
{
	/**
	 * The default configuration {@linkplain Path path}.
	 */
	public static final @NotNull
	Path defaultConfigurationPath =
		Paths.get("config" + File.separator + "generator_plan.xml");

	/**
	 * The {@linkplain Selection selections} for PNG generation.
	 */
	@NotNull
	List<Selection> selections = new ArrayList<>();

	/**
	 * Answer the {@linkplain Selection selections} for PNG generation.
	 *
	 * @return A {@link List} of selections.
	 */
	public List<Selection> selections ()
	{
		return selections;
	}

	/**
	 * The base directory to output the png files.
	 */
	String targetDirectory = "png";

	/**
	 * Answer the base directory to output the png files.
	 *
	 * @return A {@code String} representing the directory.
	 */
	public String targetDirectory ()
	{
		return targetDirectory;
	}

	@Override
	public boolean isValid ()
	{
		return selections.size() > 0;
	}
}
