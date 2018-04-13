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

import raa.utility.FileUtility;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * An interactive console application that accepts user input for the purpose
 * of generating PNG files out of individual font characters.
 *
 * <p>
 * <strong>NOTE:</strong> This application expects lower volumes of files to be
 * written. Any large volumes should utilize {@link XMLApplication} as it
 * generates files concurrently.
 * </p>
 *
 * @author Richard Arriaga
 */
public class ConsoleApplication
{
	/**
	 * The main loop of the command line application.
	 *
	 * @param args
	 *        No inputs
	 */
	public static void main(String[] args)
	{
		AppRuntime.initialize();
		final String dir = FileUtility.platformAppropriatePath(
			AppRuntime.console().readNonEmptyString(
				"Enter target directory for files: "));
		FileUtility.createDir(dir);
		AppRuntime.console().println("Set the image size");

		int height = AppRuntime.console().readLowerBoundedInt("\tHeight: ", 1);
		int width = AppRuntime.console().readLowerBoundedInt("\tWidth: ", 1);
		AppRuntime.console().print(FontManager.fontList());

		final int fontChoice = AppRuntime.console().readBoundedInt(
			"Select a font: ", 1, FontManager.fontCount() + 1);

		int fontSize = AppRuntime.console().readLowerBoundedInt(
			"Enter the requested font size (pt): ", 1);

		AppRuntime.console().println("Font Style");
		AppRuntime.console().println("==========");
		AppRuntime.console().println("\t1. Plain");
		AppRuntime.console().println("\t2. Bold");
		AppRuntime.console().println("\t3. Italic");

		int fontStyle = AppRuntime.console().readBoundedInt(
			"Select style (1 - 3): ", 1, 4);

		String styleName = "";

		switch (fontStyle)
		{
			case 1:
				styleName = "Plain";
				break;
			case 2:
				styleName = "Bold";
				break;
			case 3:
				styleName = "Italic";
				break;
			default:
				System.err.println("Shouldn't happen!");
				System.exit(1);
		}

		final Font chosenFont = FontManager.getFont(fontChoice - 1)
				.deriveFont(fontStyle - 1, fontSize);

		AppRuntime.console().println(options);
		final int colorChoice = AppRuntime.console().readBoundedInt(
			"Select option: ", 1, optionCount + 1);

		Color color;
		String colorName;
		if (colorChoice > optionCount)
		{
			AppRuntime.console().println("Provide the RGBA values:");
			int red = AppRuntime.console().readBoundedInt("R: ", 0, 256);
			int blue = AppRuntime.console().readBoundedInt("B: ", 0, 256);
			int green = AppRuntime.console().readBoundedInt("G: ", 0, 256);
			int alpha = AppRuntime.console().readBoundedInt("A: ", 0, 256);

			color = new Color(red, blue, green, alpha);
			colorName = String.format(
				"RGBA(%d,%d,%d,%d)", red, blue, green, alpha);
		}
		else
		{
			ColorOption colorOption = choice(colorChoice);
			color = colorOption.color;
			colorName = colorOption.name;
		}

		final int MIN_UNICODE = 33;
		final int MAX_UNICODE = 65535;

		boolean addRange = true;
		while (addRange)
		{
			AppRuntime.console().printf(
				"\nSelect unicode decimal character range [%d - %d]\n",
				MIN_UNICODE,
				MAX_UNICODE);
			int start = AppRuntime.console().readBoundedInt(
				"\tEnter unicode start (inclusive): ",
				MIN_UNICODE,
				MAX_UNICODE + 1);
			int end = AppRuntime.console().readBoundedInt(
				"\tEnter unicode end (exclusive): ",
				start + 1,
				MAX_UNICODE + 1);
			PNGGenerator.generateRange(
				start, end, chosenFont, dir, color, height, width);
			addRange =
				AppRuntime.console().readBoolean("Add another range? (y/n): ");
		}

		AppRuntime.console().println("\nSummary\n=======");
		AppRuntime.console().printf("%tSize: %dx%d%n", width, height);
		AppRuntime.console().printf(
			"%tFont: %s (%s) %s %dpt%n",
			colorName,
			styleName,
			chosenFont.getName(),
			fontSize);
		AppRuntime.console().println("%tFiles written to: " + dir);
	}


	/**
	 * A list of options made available for the command line application.
	 */
	private static String options;

	/**
	 * A {@link Map} of integers to the {@link ColorOption} it represents.
	 */
	private static Map<Integer, ColorOption> choiceToOption = new HashMap<>();

	/**
	 * Answer the appropriate {@link ColorOption} for the provided integer
	 * input.
	 *
	 * @param i
	 *        The key value to lookup.
	 * @return A {@code ColorOption}.
	 */
	private static ColorOption choice (int i)
	{
		return choiceToOption.get(i);
	}

	/**
	 * The number of enums values in this enumeration.
	 */
	private static int optionCount = ColorOption.values().length;

	//initialize the color options.
	static
	{
		final StringBuilder sb =
			new StringBuilder("\nColor Option\n============\n");
		int j = 0;
		for (int i = 0; i < optionCount; i++)
		{
			j = i + 1;
			ColorOption c = ColorOption.values()[i];
			choiceToOption.put(j, c);
			sb
				.append('\t')
				.append(j)
				.append(". ")
				.append(c.name)
				.append(' ')
				.append(c.rgbaLabel)
				.append('\n');
		}
		sb.append('\t').append(j + 1).append(". Custom RGBA");
		options = sb.toString();
	}
}
