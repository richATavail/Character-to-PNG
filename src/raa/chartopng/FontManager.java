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
import sun.awt.CGraphicsEnvironment;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * A {@code FontManager} manages the available fonts for this application.
 *
 * @author Richard Arriaga
 */
public class FontManager
{
	/**
	 * A {@link Map} of {@linkplain Font#name font names} to an index into
	 * an array containing all the {@linkplain Font fonts}.
	 */
	static final Map<String, Integer> fontRedirectMap = new HashMap<>();

	/**
	 * An array that contains all the available fonts in this application.
	 */
	static Font[] fonts;

	/**
	 * A numbered list of all the fonts in this application.
	 */
	private static final String fontList;
	static
	{
		StringBuilder sb = new StringBuilder("Font Options\n============\n");
		fonts = new CGraphicsEnvironment().getAllFonts();
		for (int i = 0; i < fonts.length; i++)
		{
			final String fontName = fonts[i].getName();
			fontRedirectMap.put(fontName, i + 1);
			sb
				.append('\t')
				.append(i + 1)
				.append(".  ")
				.append(fontName)
				.append('\n');
		}
		fontList = sb.toString();
	}

	/**
	 * Answer the numbered list of all the fonts in this application.
	 *
	 * @return A String list of all available fonts.
	 */
	public static String fontList ()
	{
		return fontList;
	}

	/**
	 * The number of available fonts.
	 *
	 * @return The number of fongs.
	 */
	public static int fontCount ()
	{
		return fonts.length;
	}

	/**
	 * Answer the named {@link Font}.
	 *
	 * @param fontName
	 *        The name of the {@code Font} to retrieve.
	 * @return A {@code Font}.
	 */
	public static Font getFont(String fontName)
	{
		final Integer fontId =  fontRedirectMap.get(fontName);

		if (fontId != null)
		{
			return fonts[fontId - 1];
		}

		return null;
	}

	/**
	 * Answer the {@link Font} at the index in the available font array.
	 *
	 * @return A {@code Font}.
	 */
	public static Font getFont(int fontId)
	{
		return fonts[fontId];
	}
}
