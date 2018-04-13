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

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import raa.chartopng.ColorOption;
import raa.chartopng.FontManager;
import raa.chartopng.NamedColor;
import raa.configuration.State.Range;
import raa.utility.configuration.XMLElement;

import java.awt.*;
import java.util.Collections;
import java.util.Set;

/**
 *  An {@code Element} represents a legal element of the configuration schema.
 *
 *  @author Richard Arriaga
 */
public enum Element
implements XMLElement<GeneratorPlan, Element, State>
{
	/**
	 * The outermost element.
	 */
	SELECTIONS
	{
		@Override
		@Contract(pure = true)
		public @NotNull Set<Element> allowedParents ()
		{
			return Collections.emptySet();
		}
	},

	/**
	 * A {@code SELECTION} indicates a single {@link Font}, {@link Color}, and
	 * size selection for a number of character code ranges.
	 */
	SELECTION
	{
		@Override
		@Contract(pure = true)
		public @NotNull Set<Element> allowedParents ()
		{
			return Collections.singleton(SELECTIONS);
		}

		@Override
		public void startElement (
			final @NotNull State state, final @NotNull Attributes attributes)
		{
			state.selection = new State.Selection();
		}

		@Override
		public void endElement (final @NotNull State state) throws SAXException
		{
			state.selection().validateSelection();
			state.selections.add(state.selection());
			state.configuration().selections.add(state.selection());
			state.selections.add(state.selection);
			state.selection = null;
		}
	},

	/**
	 * The height in pixels of the image.
	 */
	@SuppressWarnings("unused")
	SELECTION_NAME
	{
		@Override
		@Contract(pure = true)
		public @NotNull Set<Element> allowedParents ()
		{
			return Collections.singleton(SELECTION);
		}

		@Override
		public void startElement (
			final @NotNull State state,
			final @NotNull Attributes attributes)
		{
			state.startAccumulator();
		}

		@Override
		public void endElement (final @NotNull State state)
		{
			state.stopAccumulator();
			state.selection().setSelectionName(state.accumulatorContents());
		}
	},

	/**
	 * An ordered collection of {@link Font} preference for character
	 * generation.  If a glyph is not available for a font, the next font in
	 * the list is tried until the list is exhausted.
	 */
	FONTS
	{
		@Override
		@Contract(pure = true)
		public @NotNull Set<Element> allowedParents ()
		{
			return Collections.singleton(SELECTION);
		}
	},

	/**
	 * Contains the description of one {@link Font}.
	 */
	FONT
	{
		@Override
		@Contract(pure = true)
		public @NotNull Set<Element> allowedParents ()
		{
			return Collections.singleton(FONTS);
		}
	},

	/**
	 * The style of the {@link Font}.
	 *
	 * <ul>
	 *     <li>1 - plain</li>
	 *     <li>2 - bold</li>
	 *     <li>3 - italic</li>
	 * </ul>
	 */
	@SuppressWarnings("unused")
	FONT_STYLE
	{
		@Override
		@Contract(pure = true)
		public @NotNull Set<Element> allowedParents ()
		{
			return Collections.singleton(FONT);
		}

		@Override
		public void startElement (
			final @NotNull State state,
			final @NotNull Attributes attributes)
		{
			state.startAccumulator();
		}

		@Override
		public void endElement (final @NotNull State state)
		{
			state.stopAccumulator();
			state.selection().fontStyle =
				Integer.parseInt(state.accumulatorContents());
			assert state.selection().fontStyle > 0
				&& state.selection().fontStyle < 4
				: String.format(
					"font style must be 1, 2, or 3, but %d was provided",
				state.selection().fontStyle);
			state.selection().hasFontStyle = true;
		}
	},

	/**
	 * A single {@link Font} name as described by this application.
	 */
	@SuppressWarnings("unused")
	FONT_NAME
	{
		@Override
		@Contract(pure = true)
		public @NotNull Set<Element> allowedParents ()
		{
			return Collections.singleton(FONT);
		}

		@Override
		public void startElement (
			final @NotNull State state,
			final @NotNull Attributes attributes)
		{
			state.startAccumulator();
		}

		@Override
		public void endElement (final @NotNull State state) throws SAXException
		{
			state.stopAccumulator();
			String fonrName = state.accumulatorContents();
			final Font font = FontManager.getFont(fonrName);
			if (font != null)
			{
				state.selection().addFont(font);
			}
			else
			{
				throw new SAXException(
					"Font Selection, " + fonrName
						+ ", is not a valid font option.");
			}
		}
	},

	/**
	 * The point size of the {@link Font}.
	 */
	@SuppressWarnings("unused")
	FONT_SIZE
	{
		@Override
		@Contract(pure = true)
		public @NotNull Set<Element> allowedParents ()
		{
			return Collections.singleton(FONT);
		}

		@Override
		public void startElement (
			final @NotNull State state,
			final @NotNull Attributes attributes)
		{
			state.startAccumulator();
		}

		@Override
		public void endElement (final @NotNull State state)
		{
			state.stopAccumulator();
			state.selection().fontSize =
				Integer.parseInt(state.accumulatorContents());
			assert state.selection().fontSize > 0;
			state.selection().hasFontSize = true;

		}
	},

	/**
	 * The height in pixels of the image.
	 */
	@SuppressWarnings("unused")
	PIXEL_HEIGHT
	{
		@Override
		@Contract(pure = true)
		public @NotNull Set<Element> allowedParents ()
		{
			return Collections.singleton(SELECTION);
		}

		@Override
		public void startElement (
			final @NotNull State state,
			final @NotNull Attributes attributes)
		{
			state.startAccumulator();
		}

		@Override
		public void endElement (final @NotNull State state)
		{
			state.stopAccumulator();
			state.selection().pixelHeight =
				Integer.parseInt(state.accumulatorContents());
			state.selection().hasPixelHeight = true;

		}
	},

	/**
	 * The width in pixels of the image.
	 */
	@SuppressWarnings("unused")
	PIXEL_WIDTH
	{
		@Override
		@Contract(pure = true)
		public @NotNull Set<Element> allowedParents ()
		{
			return Collections.singleton(SELECTION);
		}

		@Override
		public void startElement (
			final @NotNull State state,
			final @NotNull Attributes attributes)
		{
			state.startAccumulator();
		}

		@Override
		public void endElement (final @NotNull State state)
		{
			state.stopAccumulator();
			state.selection().pixelWidth = Integer.parseInt(state.accumulatorContents());
			state.selection().hasPixelWidth = true;

		}
	},

	/**
	 * A {@code COLOR} is a section that contains either a single tag, {@link
	 * #COLOR_NAME} that represents a {@link ColorOption} or the four
	 * tags {@link #RED}, {@link #BLUE}, {@link #GREEN}, and {@link #ALPHA} that
	 * represent the RBGA of a {@link Color}.
	 */
	COLORS
	{
		@Override
		@Contract(pure = true)
		public @NotNull Set<Element> allowedParents ()
		{
			return Collections.singleton(SELECTION);
		}
	},

	/**
	 * A {@code COLOR} is a section that contains either a single tag, {@link
	 * #COLOR_NAME} that represents a {@link ColorOption} or the four
	 * tags {@link #RED}, {@link #BLUE}, {@link #GREEN}, and {@link #ALPHA} that
	 * represent the RBGA of a {@link Color}.
	 */
	COLOR
	{
		@Override
		@Contract(pure = true)
		public @NotNull Set<Element> allowedParents ()
		{
			return Collections.singleton(COLORS);
		}

		@Override
		public void startElement (
			final @NotNull State state, final @NotNull Attributes attributes)
		{
			state.selection().colorChoice = new State.ColorChoice();
		}

		@Override
		public void endElement (final @NotNull State state) throws SAXException
		{
			NamedColor color;
			if (state.selection().colorChoice().colorName == null)
			{
				assert state.selection().colorChoice().hasRed
					&& state.selection().colorChoice().hasBlue
					&& state.selection().colorChoice().hasGreen
					&& state.selection().colorChoice().hasAlpha;

				color = new NamedColor(
					state.selection().colorChoice().red,
					state.selection().colorChoice().blue,
					state.selection().colorChoice().green,
					state.selection().colorChoice().alpha);

			}
			else
			{
				ColorOption colorOption = ColorOption.colorOption(
					state.selection().colorChoice().colorName());

				if (colorOption == null)
				{
					throw new SAXException("Color Selection, "
						+ state.selection().colorChoice().colorName
						+ ", is not a valid color option.");
				}

				color = colorOption.color();
			}

			state.selection().addColor(color);
		}
	},

	/**
	 * The name of a {@link ColorOption}.
	 */
	@SuppressWarnings("unused")
	COLOR_NAME
	{
		@Override
		@Contract(pure = true)
		public @NotNull Set<Element> allowedParents ()
		{
			return Collections.singleton(COLORS);
		}

		@Override
		public void startElement (
			final @NotNull State state,
			final @NotNull Attributes attributes)
		{
			state.startAccumulator();
		}

		@Override
		public void endElement (final @NotNull State state)
		{
			state.stopAccumulator();
			state.selection().colorChoice().colorName = state.accumulatorContents();
			state.selection().addColor(ColorOption.colorOption(
				state.selection().colorChoice().colorName()).color());
		}
	},

	/**
	 * The red value in the RGBA of {@link Color}.
	 */
	@SuppressWarnings("unused")
	RED
	{
		@Override
		@Contract(pure = true)
		public @NotNull Set<Element> allowedParents ()
		{
			return Collections.singleton(COLOR);
		}

		@Override
		public void startElement (
			final @NotNull State state,
			final @NotNull Attributes attributes)
		{
			state.startAccumulator();
		}

		@Override
		public void endElement (final @NotNull State state)
		{
			state.stopAccumulator();
			state.selection().colorChoice().red =
				Integer.parseInt(state.accumulatorContents());
			assert state.selection().colorChoice().red > -1
				&& state.selection().colorChoice().red < 256;
			state.selection().colorChoice().hasRed = true;
		}
	},

	/**
	 * The blue value in the RGBA of {@link Color}.
	 */
	@SuppressWarnings("unused")
	BLUE
	{
		@Override
		@Contract(pure = true)
		public @NotNull Set<Element> allowedParents ()
		{
			return Collections.singleton(COLOR);
		}

		@Override
		public void startElement (
			final @NotNull State state,
			final @NotNull Attributes attributes)
		{
			state.startAccumulator();
		}

		@Override
		public void endElement (final @NotNull State state)
		{
			state.stopAccumulator();
			state.selection().colorChoice().blue =
				Integer.parseInt(state.accumulatorContents());
			assert state.selection().colorChoice().blue > -1
				&& state.selection().colorChoice().blue < 256;
			state.selection().colorChoice().hasBlue = true;
		}
	},

	/**
	 * The green value in the RGBA of {@link Color}.
	 */
	@SuppressWarnings("unused")
	GREEN
	{
		@Override
		@Contract(pure = true)
		public @NotNull Set<Element> allowedParents ()
		{
			return Collections.singleton(COLOR);
		}

		@Override
		public void startElement (
			final @NotNull State state,
			final @NotNull Attributes attributes)
		{
			state.startAccumulator();
		}

		@Override
		public void endElement (final @NotNull State state)
		{
			state.stopAccumulator();
			state.selection().colorChoice().green =
				Integer.parseInt(state.accumulatorContents());
			assert state.selection().colorChoice().green > -1
				&& state.selection().colorChoice().green < 256;
			state.selection().colorChoice().hasGreen = true;
		}
	},

	/**
	 * The alpha value in the RGBA of {@link Color}.
	 */
	@SuppressWarnings("unused")
	ALPHA
	{
		@Override
		@Contract(pure = true)
		public @NotNull Set<Element> allowedParents ()
		{
			return Collections.singleton(COLOR);
		}

		@Override
		public void startElement (
			final @NotNull State state,
			final @NotNull Attributes attributes)
		{
			state.startAccumulator();
		}

		@Override
		public void endElement (final @NotNull State state)
		{
			state.stopAccumulator();
			state.selection().colorChoice().alpha =
				Integer.parseInt(state.accumulatorContents());
			assert state.selection().colorChoice().alpha > -1
				&& state.selection().colorChoice().alpha < 256;
			state.selection().colorChoice().hasAlpha = true;
		}
	},

	/**
	 * The ranges of character code points to generate.
	 */
	CODE_POINT_RANGES
	{
		@Override
		@Contract(pure = true)
		public @NotNull Set<Element> allowedParents ()
		{
			return Collections.singleton(SELECTION);
		}
	},

	RANGE
	{
		@Override
		public Set<Element> allowedParents ()
		{
			return Collections.singleton(CODE_POINT_RANGES);
		}

		@Override
		public void startElement (
			final @NotNull State state,
			final @NotNull Attributes attributes)
		{
			state.selection().rangeChoice = new Range();
		}

		@Override
		public void endElement (final @NotNull State state) throws SAXException
		{
			if (state.selection().rangeChoice().hasStart
				&& state.selection().rangeChoice().hasEnd)
			{
				state.selection().addRange(state.selection().rangeChoice());
			}
			else
			{
				throw new SAXException("Codepoint range is incomplete.");
			}
		}
	},

	/**
	 * The start code point for character generation (inclusive).
	 */
	@SuppressWarnings("unused")
	START
	{
		@Override
		public Set<Element> allowedParents ()
		{
			return Collections.singleton(RANGE);
		}

		@Override
		public void startElement (
			final @NotNull State state,
			final @NotNull Attributes attributes)
		{
			state.startAccumulator();
		}

		@Override
		public void endElement (final @NotNull State state) throws SAXException
		{
			state.stopAccumulator();
			int value = Integer.parseInt(state.accumulatorContents());

			if (value < 0)
			{
				throw new SAXException("Codepoint range must > -1.");
			}
			state.selection().rangeChoice().start = value;
			state.selection().rangeChoice().hasStart = true;
		}
	},

	/**
	 * The end code point for character generation (exclusive).
	 */
	@SuppressWarnings("unused")
	END
	{
		@Override
		public Set<Element> allowedParents ()
		{
			return Collections.singleton(RANGE);
		}

		@Override
		public void startElement (
			final @NotNull State state,
			final @NotNull Attributes attributes)
		{
			state.startAccumulator();
		}

		@Override
		public void endElement (final @NotNull State state) throws SAXException
		{
			state.stopAccumulator();
			int value = Integer.parseInt(state.accumulatorContents());

			if (value < 0)
			{
				throw new SAXException("Codepoint range must > 0.");
			}
			state.selection().rangeChoice().end = value;
			state.selection().rangeChoice().hasEnd = true;
		}
	},

	/**
	 * The base directory to output the png files.
	 */
	@SuppressWarnings("unused")
	TARGET_DIRECTORY
	{
		@Override
		public Set<Element> allowedParents ()
		{
			return Collections.singleton(SELECTIONS);
		}

		@Override
		public void startElement (
			final @NotNull State state, final @NotNull Attributes attributes)
		{
			state.startAccumulator();
		}

		@Override
		public void endElement (final @NotNull State state)
		{
			state.stopAccumulator();
			state.configuration().targetDirectory = state.accumulatorContents();
		}
	};

	@Override
	@Contract(pure = true)
	public @NotNull String qName ()
	{
		return name().toLowerCase();
	}

	@Override
	public void startElement (
		final @NotNull State state,
		final @NotNull Attributes attributes)
	{
		// Nothing.
	}

	@Override
	public void endElement (final @NotNull State state)
	throws SAXException
	{
		// Nothing.
	}
}
