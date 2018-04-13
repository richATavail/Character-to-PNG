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

import java.awt.*;

/**
 * A {@code NamedColor} is a {@link Color} that knows its name/description.
 *
 * @author Richard Arriaga
 */
public class NamedColor
extends Color
{
	/**
	 * File system friendly name.
	 */
	private final String name;

	/**
	 * The file system friendly name of the {@link NamedColor}.
	 *
	 * @return A string.
	 */
	public String name ()
	{
		return name;
	}

	/**
	 * Label that is visually easy to read.
	 */
	private final String label;

	/**
	 * Answer a string that is visually easy to read.
	 *
	 * @return A String.
	 */
	public String label ()
	{
		return label;
	}

	/**
	 * Create a {@link NamedColor}.
	 *
	 * @param r
	 *        The red component
	 * @param g
	 *        The green component
	 * @param b
	 *        The blue component
	 * @param a
	 *        The alpha component
	 * @param name
	 *        The name of the color.
	 */
	public NamedColor (
		final int r,
		final int g,
		final int b,
		final int a,
		final @NotNull String name)
	{
		super(r, g, b, a);
		this.name = name;
		this.label = name;
	}

	/**
	 * Create a {@link NamedColor}.
	 *
	 * @param r
	 *        The red component
	 * @param g
	 *        The green component
	 * @param b
	 *        The blue component
	 * @param a
	 *        The alpha component
	 */
	public NamedColor (final int r, final int g, final int b, final int a)
	{
		super(r, g, b, a);
		this.name = "RGBA_" + r +  "_" + b + "_" + g + "_" + a;
		this.label = "RGBA(" + r +  "," + b + "," + g + "," + a + ")";
	}

	/**
	 * The color white.
	 */
	public final static NamedColor WHITE =
		new NamedColor(255, 255, 255, 255, "white");

	/**
	 * The color light gray.
	 */
	public final static NamedColor LIGHT_GRAY =
		new NamedColor(192, 192, 192, 255, "lightGray");

	/**
	 * The color gray.
	 */
	public final static NamedColor GRAY =
		new NamedColor(128, 128, 128, 255, "gray");

	/**
	 * The color dark gray.
	 */
	public final static NamedColor DARK_GRAY =
		new NamedColor(64, 64, 64, 255, "dark Gray");

	/**
	 * The color black.
	 */
	public final static NamedColor BLACK =
		new NamedColor(0, 0, 0, 255, "black");

	/**
	 * The color red.  In the default sRGB space.
	 * @since 1.4
	 */
	public final static NamedColor RED =
		new NamedColor(255, 0, 0, 255, "red");

	/**
	 * The color pink.
	 */
	public final static NamedColor PINK =
		new NamedColor(255, 175, 175, 255, "pink");

	/**
	 * The color orange.
	 */
	public final static NamedColor ORANGE =
		new NamedColor(255, 200, 0, 255, "orange");

	/**
	 * The color yellow.  In the default sRGB space.
	 * @since 1.4
	 */
	public final static NamedColor YELLOW =
		new NamedColor(255, 255, 0, 255, "yellow");

	/**
	 * The color green.
	 */
	public final static NamedColor GREEN =
		new NamedColor(0, 255, 0, 255, "green");

	/**
	 * The color magenta.
	 */
	public final static NamedColor MAGENTA =
		new NamedColor(255, 0, 255, 255, "magenta");

	/**
	 * The color cyan.
	 */
	public final static NamedColor CYAN =
		new NamedColor(0, 255, 255, 255, "cyan");

	/**
	 * The color blue.
	 */
	public final static NamedColor BLUE =
		new NamedColor(0, 0, 255, 255, "blue");
}
