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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.xml.sax.SAXException;
import raa.chartopng.ColorOption;
import raa.chartopng.NamedColor;
import raa.utility.configuration.Configuration;
import raa.utility.configuration.XMLConfigurator;
import raa.utility.configuration.XMLConfiguratorState;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A {@code State} encapsulates the state of a {@link XMLConfigurator}.
 *
 * @author Richard Arriaga
 */
public class State
extends XMLConfiguratorState<GeneratorPlan, Element, State>
{
	/**
	 * The {@link Selection} that is presently being built.
	 */
	@Nullable State.Selection selection;

	/**
	 * Answer {@link #selection}.
	 *
	 * @return A {@link Selection}.
	 */
	@NotNull Selection selection ()
	{
		final Selection s = selection;
		assert s != null : "Should be set at this point!";
		return s;
	}

	/**
	 * The {@linkplain Selection selections} for PNG generation.
	 */
	@NotNull
	List<State.Selection> selections = new ArrayList<>();

	/**
	 * A {@code Selection} is a grouping of selected proprerties to generate
	 * character PNGs.  This specifies {@link Font} priority, font size, colors,
	 * and code point ranges.
	 */
	public static class Selection
	{
		/**
		 * The identifying name for this {@link Selection}.
		 */
		private @NotNull String selectionName = "";

		/**
		 * Set the identifying name for this {@link Selection}.
		 *
		 * @param name
		 *        The name to set.
		 */
		void setSelectionName (final @NotNull String name)
		{
			this.selectionName = name;
		}

		/**
		 * Answer the identifying name for this {@link Selection}.
		 *
		 * @return A String.
		 */
		public @NotNull String selectionName ()
		{
			return selectionName;
		}

		/**
		 * Method to confirm that the {@link Selection} is valid.
		 *
		 * @throws SAXException
		 */
		void validateSelection ()
			throws SAXException
		{
			boolean isValid = !selectionName.isEmpty()
				&& hasFontSize
				&& hasPixelHeight
				&& hasPixelWidth
				&& colors.size() > 0
				&& selectedFonts.size() > 0
				&& ranges.size() > 0;

			if (!isValid)
			{
				throw new SAXException("Invalid State.Selection");
			}
		}

		/**
		 * An ordered collection of {@link Font} preferences for character
		 * generation.  If a glyph is not available for a font, the next font in
		 * the list is tried until the list is exhausted.
		 */
		private final @NotNull List<Font> selectedFonts = new ArrayList<>();

		/**
		 * Add a font to the ordered collection of {@link Font} preferences for
		 * character generation.  If a glyph is not available for a font, the
		 * next font in the list is tried until the list is exhausted.
		 *
		 * @param font
		 *        The {@link Font} to add.
		 */
		void addFont (final @NotNull Font font)
		{
			selectedFonts.add(font);
		}

		/**
		 * Answer the ordered collection of {@link Font} preferences for
		 * character generation.  If a glyph is not available for a font, the
		 * next font in the list is tried until the list is exhausted.
		 *
		 * @return A {@code List}.
		 */
		public @NotNull List<Font> selectedFonts ()
		{
			return selectedFonts;
		}

		/**
		 * The integer point font size to use (must be > 0);
		 */
		int fontSize;

		/**
		 * Answer the {@linkplain #fontSize font size}.
		 *
		 * @return An {@code integer}.
		 */
		public int fontSize ()
		{
			return fontSize;
		}

		/**
		 * Indicates the {@linkplain Font#style font style}:
		 *
		 * <ul>
		 *     <li>1 - plain</li>
		 *     <li>2 - bold</li>
		 *     <li>3 - italic</li>
		 * </ul>
		 */
		int fontStyle = 1;

		/**
		 * Answer the {@linkplain #fontStyle font style}.
		 *
		 * <ul>
		 *     <li>1 - plain</li>
		 *     <li>2 - bold</li>
		 *     <li>3 - italic</li>
		 * </ul>
		 *
		 * @return 1, 2, or 3.
		 */
		public int fontStyle ()
		{
			return fontStyle;
		}

		/**
		 * Indicates whether or not the {@link #fontStyle} value has been
		 * populated.
		 */
		boolean hasFontStyle = false;

		/**
		 * Indicates whether or not the {@link #fontSize} value has been
		 * populated.
		 */
		boolean hasFontSize = false;

		/**
		 * The {@link List} of {@linkplain Color colors} to generate the fonts
		 * in.  A glyph is attempted to be generated in each of the colors
		 * present.
		 */
		private final @NotNull List<NamedColor> colors = new ArrayList<>();

		/**
		 * Add the {@link NamedColor} to the {@link List} of {@linkplain Color
		 * colors} to generate the fonts in.  A glyph is attempted to be
		 * generated in each of the colors present.
		 *
		 * @param color
		 *        The {@code NamedColor} to add.
		 */
		void addColor (final NamedColor color)
		{
			colors.add(color);
		}

		/**
		 * Answer the {@linkplain NamedColor colors} to be generated.
		 *
		 * @return A {@link List} of colors.
		 */
		public @NotNull List<NamedColor> colors ()
		{
			return colors;
		}

		/**
		 * The {@link Color} presently being built.
		 */
		@Nullable ColorChoice colorChoice;

		@NotNull ColorChoice colorChoice ()
		{
			final ColorChoice c = colorChoice;
			assert c != null : "Should be set by now!";
			return c;
		}

		/**
		 * The minimum character code point to generate from this {@link
		 * Selection}.
		 */
		private int minCodePoint = Integer.MAX_VALUE;

		/**
		 * Answer the minimum character code point to generate from this {@link
		 * Selection}.
		 *
		 * @return An {@code integer}.
		 */
		public int minCodePoint ()
		{
			return minCodePoint;
		}

		/**
		 * The maximum character code point to generate from this {@link
		 * Selection}.
		 */
		private int maxCodePoint = Integer.MIN_VALUE;

		/**
		 * Answer the maximum character code point to generate from this {@link
		 * Selection}.
		 *
		 * @return An {@code integer}.
		 */
		public int maxCodePoint ()
		{
			return maxCodePoint;
		}

		/**
		 * The {@link List} of character code point {@linkplain Range ranges}
		 * to generate.
		 */
		private final @NotNull List<Range> ranges = new ArrayList<>();

		/**
		 * Add a {@link Range} to the {@link List} of character code point
		 * {@linkplain Range ranges} to generate.
		 *
		 * @param range
		 *        The {@code Range} to add.
		 */
		void addRange (final @NotNull Range range)
		{
			maxCodePoint = Math.max(range.end, maxCodePoint);
			minCodePoint = Math.min(range.start, minCodePoint);
			ranges.add(range);
		}

		/**
		 * Answer the {@link List} of character code point {@linkplain Range
		 * ranges} to generate.
		 *
		 * @return A {@code List}.
		 */
		public @NotNull List<Range> ranges ()
		{
			return ranges;
		}

		/**
		 * The {@link Range} presently being built.
		 */
		@Nullable Range rangeChoice;

		/**
		 * Answer the {@link Range} presently being built.
		 *
		 * @return A {@code Range}.
		 */
		@NotNull Range rangeChoice ()
		{
			final Range r = rangeChoice;
			assert r != null : "Should be set!";
			return r;
		}

		/**
		 * The height in pixels of the entire PNG.
		 */
		int pixelHeight;

		/**
		 * Answer the height in pixels for this selection.
		 *
		 * @return A positive integer.
		 */
		public int pixelHeight ()
		{
			return pixelHeight;
		}

		/**
		 * Indicates whether or not the {@link #pixelHeight} value has been
		 * populated.
		 */
		boolean hasPixelHeight = false;

		/**
		 * The width in pixels of the entire PNG.
		 */
		int pixelWidth;

		/**
		 * Answer the width in pixels for this selection.
		 *
		 * @return A positive integer.
		 */
		public int pixelWidth ()
		{
			return pixelWidth;
		}

		/**
		 * Indicates whether or not the {@link #pixelWidth} value has been
		 * populated.
		 */
		boolean hasPixelWidth = false;


	}

	/**
	 * A {@code ColorChoice} is a class that holds property information required
	 * to generate a {@link Color}.  This is specified by either choosing a
	 * {@link ColorOption} or specifying a custom RBGA.
	 */
	static class ColorChoice
	{
		/**
		 * The name of the {@link ColorOption}.
		 */
		@Nullable String colorName;

		/**
		 * Answer the name of the {@link ColorOption}.
		 *
		 * @return A String.
		 */
		String colorName ()
		{
			final String c = colorName;
			assert c != null : "Should be set!";
			return c;
		}

		/**
		 * The RBGA int red value.
		 */
		int red;

		/**
		 * Indicates whether or not the {@link #red} value has been populated.
		 */
		boolean hasRed = false;

		/**
		 * The RBGA int blue value.
		 */
		int blue;

		/**
		 * Indicates whether or not the {@link #blue} value has been populated.
		 */
		boolean hasBlue = false;

		/**
		 * The RBGA int green value.
		 */
		int green;

		/**
		 * Indicates whether or not the {@link #green} value has been populated.
		 */
		boolean hasGreen = false;

		/**
		 * The RBGA int alpha value.
		 */
		int alpha;

		/**
		 * Indicates whether or not the {@link #alpha} value has been populated.
		 */
		boolean hasAlpha = false;
	}

	/**
	 * A {@code Range} specifies a range of character code points to generate.
	 */
	public static class Range
	{
		/**
		 * The start of the {@link Range} (inclusive).
		 */
		int start;

		/**
		 * Answer the start of the range to generate.
		 *
		 * @return The start.
		 */
		public int start ()
		{
			return start;
		}

		/**
		 * Indicates whether or not {@link #start} is populated.
		 */
		boolean hasStart = false;

		/**
		 * The end of the {@link Range} (exclusive).
		 */
		int end;

		/**
		 * Answer the end (exclusive) of the range to generate.
		 *
		 * @return The end.
		 */
		public int end ()
		{
			return end;
		}

		/**
		 * Indicates whether or not {@link #end} is populated.
		 */
		boolean hasEnd = false;

		@Override
		public String toString ()
		{
			return "[" + start + ", " + end + ")";
		}
	}

	/**
	 * Create a {@link State}.
	 *
	 * @param configuration
	 *        The {@link Configuration configuration}.
	 */
	State (final GeneratorPlan configuration)
	{
		super(configuration);
	}
}
