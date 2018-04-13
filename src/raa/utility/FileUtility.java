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
package raa.utility;

import raa.chartopng.NamedColor;
import org.jetbrains.annotations.NotNull;
import raa.configuration.State.Selection;

import java.awt.*;
import java.io.File;

/**
 * A {@code FileUtility} is a utility class to help when dealing with files.
 *
 * @author Richard Arriaga
 */
public class FileUtility
{
	/**
	 * Answer the platform-specific path for the provided String path.
	 *
	 * @param path
	 *        A String directory/file path.
	 * @return A String.
	 */
	public static String platformAppropriatePath (final String path)
	{
		final String[] pathArray = path.contains("/")
			? path.split("/")
			: path.split("\\\\");
		return String.join(File.separator, pathArray);
	}

	/**
	 * Create the given directory.
	 *
	 * @param dirName
	 *        The name of the directory to create.
	 */
	public static void createDir (final @NotNull String dirName)
	{
		File dir = new File(dirName);
		dir.mkdirs();
	}

	/**
	 * Answer a String that represents a directory from {@link
	 * Font#getName()} and {@link NamedColor#name()}.
	 *
	 * <p>
	 * The directory takes the form:
	 * </p>
	 * <p>
	 * <em>{FONT_NAME}/{COLOR_NAME}/size_{FONT_SIZE}</em>
	 * </p>
	 *
	 * @param baseDirectory
	 *        The base directory to create this directory in.
	 * @param colorName
	 *        The String name of the color.
	 * @param selection
	 *        The {@link Selection} to be generated.
	 * @return A String.
	 */
	public static @NotNull String createStringDir (
		final @NotNull String baseDirectory,
		final @NotNull String colorName,
		final @NotNull Selection selection)
	{
		final String dirName = new StringBuilder(baseDirectory)
			.append(File.separator)
			.append(selection.selectionName())
			.append(File.separator)
			.append(colorName)
			.append(File.separator)
			.append(CharacterSupport.unicodeValue(
				Character.toChars(selection.minCodePoint())[0]))
			.append("_")
			.append(CharacterSupport.unicodeValue(
				Character.toChars(selection.maxCodePoint())[0]))
			.toString();
		File dir = new File(dirName);
		dir.mkdirs();
		return dirName;
	}

	/**
	 * Answer a PNG file name for the given directory name and character.
	 *
	 * @param dirName
	 *        The name of the directory the file will be in.
	 * @param text
	 *        The single character string to create a file name for.
	 * @param suffix
	 *        Any suffix to be added to the end of the file name.
	 * @return A file with directory.
	 */
	public static @NotNull String createFileName (
		final @NotNull String dirName,
		final @NotNull Font font,
		final @NotNull String text,
		final @NotNull String suffix)
	{
		return dirName
			+ File.separator
			+ font.getName()
			+ "_"
			+ CharacterSupport.unicodeValue(text.charAt(0))
			+ suffix;
	}

	/**
	 * Check to see if the provided directory exists.
	 *
	 * @return {@code true} the file exists; {@code false} otherwise.
	 */
	public static boolean directoryExists (final String path)
	{
		final File f = new File(platformAppropriatePath(path));
		return f.exists() && f.isDirectory();
	}
}
