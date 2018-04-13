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
import java.util.HashMap;
import java.util.Map;

/**
 * A {@code ColorOption} is an enum that provides functionality for choosing
 * colors for fonts.
 *
 * @author Richard Arriaga
 */
public enum ColorOption
{
	/**
	 * {@link Color} rgba = (255, 255, 255, 255).
	 */
	WHITE("white", "(255, 255, 255, 255)", NamedColor.WHITE),

	/**
	 * {@link Color} rgb = (0, 0, 0, 255).
	 */
	BLACK("black", "(0, 0, 0, 255)", NamedColor.BLACK),

	/**
	 * {@link Color} rgb = (255, 0, 0, 255).
	 */
	RED("red", "(255, 0, 0, 255)", NamedColor.RED),

	/**
	 * {@link Color} rgb = (0, 0, 255, 255).
	 */
	BLUE("blue", "(0, 0, 255, 255)", NamedColor.BLUE),

	/**
	 * {@link Color} rgb = (0, 255, 0, 255).
	 */
	GREEN("green", "(0, 255, 0, 255)", NamedColor.GREEN),

	/**
	 * {@link Color} rgb = (255, 255, 0, 255).
	 */
	YELLOW("yellow", "(255, 255, 0, 255)", NamedColor.YELLOW),

	/**
	 * {@link Color} rgb = (255, 175, 175, 255).
	 */
	PINK("pink", "(255, 175, 175, 255)", NamedColor.PINK),

	/**
	 * {@link Color} rgb = (255, 200, 0, 255).
	 */
	ORANGE("orange", "(255, 200, 0, 255)", NamedColor.ORANGE),

	/**
	 * {@link Color} rgb = (255, 0, 255, 255).
	 */
	MAGENTA("magenta", "(255, 0, 255, 255)", NamedColor.MAGENTA),

	/**
	 * {@link Color} rgb = (0, 255, 255, 255).
	 */
	CYAN("cyan", "(0, 255, 255, 255)", NamedColor.CYAN),

	/**
	 * {@link Color} rgb = (192, 192, 192, 255).
	 */
	LIGHT_GRAY("light gray", "(192, 192, 192, 255)", NamedColor.LIGHT_GRAY),

	/**
	 * {@link Color} rgb = (128, 128, 128, 255).
	 */
	GRAY("gray", "(128, 128, 128, 255)", NamedColor.GRAY),

	/**
	 * {@link Color} rgb = (64, 64, 64, 255).
	 */
	DARK_GRAY("dark gray", "(64, 64, 64, 255)", NamedColor.DARK_GRAY);

	/**
	 * The {@link Color} this {@link ColorOption} represents.
	 */
	final NamedColor color;

	/**
	 * The name of the {@link ColorOption}.
	 */
	final String name;

	/**
	 * The string label indicating the RGBA values.
	 */
	final String rgbaLabel;

	/**
	 * Create a {@link ColorOption}.
	 *
	 * @param name
	 *        The name of the {@code ColorOption}.
	 * @param rgbaLabel
	 *        The string label indicating the RGBA values.
	 * @param color
	 *        The {@link Color} this {@code ColorOption} represents.
	 */
	ColorOption (
		final String name,
		final String rgbaLabel,
		final NamedColor color)
	{
		this.color = color;
		this.rgbaLabel = rgbaLabel;
		this.name = name;
	}

	/**
	 * Answer the contained {@link Color}.
	 *
	 * @return A {@code Color}.
	 */
	public NamedColor color ()
	{
		return color;
	}

	/**
	 * A map that provides a lookup from the {@link ColorOption#name} to
	 * the {@link ColorOption}.
	 */
	private static Map<String, ColorOption> colorMap = new HashMap<>();
	static
	{
		for (ColorOption option : ColorOption.values())
		{
			colorMap.put(option.name, option);
		}
	}

	/**
	 * Answer the {@link ColorOption} for the given color name.
	 *
	 * @param colorName
	 *        The name of the color option to get.
	 * @return A {@code ColorOption}.
	 */
	public static ColorOption colorOption (final @NotNull String colorName)
	{
		return colorMap.get(colorName);
	}
}
