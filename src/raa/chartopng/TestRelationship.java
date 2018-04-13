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
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * A main application class that allows for experimenting with fonts.
 *
 * @author Richard Arriaga
 */
public class TestRelationship
{
	static ColorOption colorOption = ColorOption.BLACK.RED;
	static Font baseFont = FontManager.getFont("Monaco");
	static int fontSize = 100; //TODO change me to alter app;

	public static void main(String[] args)
	{
		String dir =
			"png" + File.separator + baseFont.getName() + File.separator
				+ colorOption.name;

		String testText = new String(Character.toChars(3499));

		FileUtility.createDir(dir);

		int limit = 101;

		for (int i = 0; i + fontSize < limit; i ++)
		{
			int tempFontSize = fontSize + i;
			Font chosenFont = baseFont.deriveFont(0, tempFontSize);
			BufferedImage img = PNGGenerator.centerImage(
				testText,
				chosenFont,
				colorOption.color,
				128,
				128);

			String fileName =
				FileUtility.createFileName(
					dir, chosenFont, testText, " " + tempFontSize);

			if (img != null)
			{
				PNGGenerator.exportPNG(img, fileName);
				System.out.println("File: " + fileName);
			}
			else
			{
				System.out.println("Size too big: " + fileName);
			}
			System.out.println();
		}
	}
}
