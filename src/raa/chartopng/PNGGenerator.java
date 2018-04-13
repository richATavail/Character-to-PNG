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
import org.jetbrains.annotations.Nullable;
import raa.chartopng.AppRuntime.ExitCode;
import raa.configuration.State.Selection;
import raa.utility.FileUtility;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.IntStream;

/**
 * A {@code PNGGenerator} provides functionality for generating PNG files out
 * of characters for a given font.
 *
 * @author Richard Arriaga
 */
class PNGGenerator
{
	/**
	 * Create a {@link BufferedImage} for the indicated {@link Character}.
	 *
	 * @param text
	 *        The single-character {@code String} that holds the character
	 *        targeted for PNG generation.
	 * @param font
	 *        The target {@link Font} for the character.
	 * @param color
	 *        The target {@link Color} for the {@code Font}.
	 * @param pixelHeight
	 *        The total height in pixels of the PNG.
	 * @param pixelWidth
	 *        The total width in pixels of the PNG.
	 * @param widthOffset
	 *        The number of pixels to move the font away from the origin along
	 *        the horizontal axis.
	 * @param heightOffset
	 *        The number of pixels to move the font away from the origin along
	 *        the vertical axis. <em>NOTE: The vertical origin is equal to the
	 *        pixel height of the entire PNG.</em>
	 * @return A {@code BufferedImage}
	 */
	private static BufferedImage renderPNG (
		final @NotNull String text,
		final @NotNull Font font,
		final @NotNull Color color,
		final int pixelHeight,
		final int pixelWidth,
		final float widthOffset,
		final float heightOffset)
	{
		BufferedImage img = new BufferedImage(
			pixelWidth,
			pixelHeight,
			BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = createGraphics2D(img);
		g2d.setFont(font);
		g2d.setColor(color);
		g2d.drawString(text, widthOffset, heightOffset);

		g2d.dispose();

		return img;
	}

	/**
	 * Create the PNG files for the given {@link Selection}.
	 *
	 * @param baseDirectory
	 *        The base location where the image files should be saved.
	 * @param selection
	 *        The {@code Selection} that describes the generation properties.
	 * @param creationJobs
	 *        A {@link List} to add {@link Runnable}s that will generate the
	 *        images.
	 * @param workCount
	 *        A counter for the number of files to be created.
	 * @param startTime
	 *        The time the work starts for the purpose to report how long the
	 *        generation took.
	 * @return The {@link Collection} of directory names where files were
	 *         saved.
	 */
	static @NotNull Collection<String> generateImageFiles (
		final @NotNull String baseDirectory,
		final @NotNull Selection selection,
		final @NotNull List<Runnable> creationJobs,
		final @NotNull AtomicInteger workCount,
		final @NotNull AtomicLong startTime)
	{
		final int colorCount = selection.colors().size();
		final Map<NamedColor, String> directoryMap = new HashMap<>();
		final List<Integer> noCodePoints = new ArrayList<>();
		final List<Integer> noImage = new ArrayList<>();
		selection.colors().forEach(color ->
			directoryMap.put(
				color,
				FileUtility.createStringDir(
					baseDirectory,
					color.name(),
					selection)));
		selection.ranges().forEach(range ->
			IntStream.range(range.start(), range.end()).forEach(i ->
			{
				Optional<Font> optional = selection.selectedFonts().stream()
					.filter(font -> font.canDisplay(i)).findFirst();
				if (optional.isPresent())
				{
					final Font targetFont = optional.get().deriveFont(
						selection.fontStyle(), selection.fontSize());
					final String text = new String(Character.toChars(i));

					final Function<Color, BufferedImage> f =
						centerImageFunction(
							text,
							targetFont,
							selection.pixelHeight(),
							selection.pixelWidth());
					if (f != null)
					{
						workCount.addAndGet(colorCount);
						creationJobs.add(() -> selection
							.colors()
							.forEach(color ->
							{
								final String dir = directoryMap.get(color);
								exportPNG(
									f.apply(color),
									FileUtility.createFileName(
										dir, targetFont, text, ".png"));
								if (workCount.decrementAndGet() == 0)
								{
									long time = Instant.now().toEpochMilli()
										- startTime.get();
									AppRuntime.console().printf(
										"Run time (millis): %d%n",
										time);
									AppRuntime.console().println(
										AppRuntime.codePointReport(
											"No font support: ",
											noCodePoints));
									AppRuntime.console().println(
										AppRuntime.codePointReport(
											"Control characters: ",
											noImage));
									ExitCode.NORMAL_EXIT.shutdown();
								}
							}));
					}
					else
					{
						noImage.add(i);
					}
				}
				else
				{
					noCodePoints.add(i);
				}
			}));
		return directoryMap.values();
	}

	/**
	 * Export the {@link BufferedImage} to the indicated file name.
	 *
	 * @param img
	 *        The image to export.
	 * @param fileName
	 *        The name of the file that will hold this image.
	 */
	static void exportPNG (
		final @NotNull BufferedImage img,
		final @NotNull String fileName)
	{
		try
		{
			ImageIO.write(img, "png", new File(fileName));
		}
		catch (IOException ex)
		{
			System.err.println("Could not write " + fileName);
			ex.printStackTrace();
		}
	}

	/**
	 * Answer a {@link Function} that accepts a {@link Color} and answers a
	 * {@link BufferedImage} that is centered vertically and
	 * horizontally on the canvas based on the pixel width and pixel height of
	 * the font relative to the desired font size and the pixel width and pixel
	 * height of the whole image.
	 *
	 * @param text
	 *        The single-character {@code String} that holds the character
	 *        targeted for PNG generation.
	 * @param font
	 *        The target {@link Font} for the character.
	 * @param pixelHeight
	 *        The total height in pixels of the PNG.
	 * @param pixelWidth
	 *        The total width in pixels of the PNG.
	 * @return A {@code Function}.
	 */
	static @Nullable Function<Color, BufferedImage> centerImageFunction (
		final @NotNull String text,
		final @NotNull Font font,
		final int pixelHeight,
		final int pixelWidth)
	{
		int bottomBoundary = 0;
		int topBoundary = 1;
		int bottomOffset = 0;

		BufferedImage baseImg;

		while (bottomBoundary == 0 && topBoundary > 0)
		{
			bottomOffset++;
			baseImg = renderPNG(
				text,
				font,
				Color.BLUE,
				pixelHeight + 1,
				pixelWidth + 1,
				1,
				pixelHeight - bottomOffset);

			topBoundary = topBoundarySpace(baseImg, pixelWidth, pixelHeight);
			bottomBoundary =
				bottomBoundarySpace(baseImg, pixelWidth, pixelHeight);
		}

		int leftBoundary = 0;
		int rightBoundary = 1;
		int leftOffset = 0;
		while (leftBoundary == 0 && rightBoundary < pixelWidth - 1)
		{
			leftOffset++;
			baseImg = renderPNG(
				text,
				font,
				Color.BLUE,
				pixelHeight + 1,
				pixelWidth + 1,
				leftOffset,
				pixelHeight - bottomOffset);
			leftBoundary = leftBoundary(baseImg, pixelWidth, pixelHeight);
			rightBoundary = rightBoundary(baseImg, pixelWidth, pixelHeight);
		}

		if (topBoundary == 128
			&& bottomBoundary == 129
			&& leftBoundary == 128
			&& rightBoundary == 129)
		{
			return null;
		}

		float heightOffset =
			(float) ((pixelHeight - ((topBoundary + bottomBoundary) / 2.0))
				- bottomOffset);
		float widthOffset =
			(float) (((rightBoundary - leftBoundary) / 2.0) + leftOffset);

		return c -> renderPNG(
			text,
			font,
			c,
			pixelHeight,
			pixelWidth,
			widthOffset,
			heightOffset);
	}

	/**
	 * Create a {@link BufferedImage} that is centered vertically and
	 * horizontally on the canvas based on the pixel width and pixel height of
	 * the font relative to the desired font size and the pixel width and pixel
	 * height of the whole image.
	 *
	 * @param text
	 *        The single-character {@code String} that holds the character
	 *        targeted for PNG generation.
	 * @param font
	 *        The target {@link Font} for the character.
	 * @param color
	 *        The target {@link Color} for the {@code Font}.
	 * @param pixelHeight
	 *        The total height in pixels of the PNG.
	 * @param pixelWidth
	 *        The total width in pixels of the PNG.
	 * @return A {@code BufferedImage}.
	 */
	static @Nullable BufferedImage centerImage (
		final @NotNull String text,
		final @NotNull Font font,
		final @NotNull Color color,
		final int pixelHeight,
		final int pixelWidth)
	{
		Function<Color, BufferedImage> f = centerImageFunction(
			text, font, pixelHeight, pixelWidth);
		if (f != null)
		{
			return f.apply(color);
		}
		return null;
	}

	/**
	 * Create a PNG file for the provided single-character {@code String} iff
	 * the image can fully fit on the desired canvas size.
	 *
	 * <p>Reports if export could not be completed.</p>
	 *
	 * @param text
	 *        The single-character {@code String} that holds the character
	 *        targeted for PNG generation.
	 * @param font
	 *        The target {@link Font} for the character.
	 * @param color
	 *        The target {@link Color} for the {@code Font}.
	 * @param pixelHeight
	 *        The total height in pixels of the PNG.
	 * @param pixelWidth
	 *        The total width in pixels of the PNG.
	 * @param dir
	 *        The name of the directory the file should be exported to.
	 * @return {@code true} if PNG file created; {@code false otherwise}.
	 */
	static boolean conditionallyExportCenteredImage (
		final @NotNull String text,
		final @NotNull Font font,
		final @NotNull Color color,
		final int pixelHeight,
		final int pixelWidth,
		final @NotNull String dir)
	{
		BufferedImage img =
			centerImage(text, font, color, pixelHeight, pixelWidth);
		String fileName =
			FileUtility.createFileName(dir, font, text, ".png");
		if (img != null)
		{
			exportPNG(img, fileName);
			return true;
		}
		else
		{
			System.out.printf(
				"Could not render (%s): %s%n%n",
				Character.codePointAt(text, 0),
				fileName);
			return false;
		}
	}

	/**
	 * Answer the vertical offset for the given image from the bottom of the
	 * provided canvas. This value represents the number of horizontal rows that
	 * are fully translucent at the bottom of the image.  This value is
	 * necessary for determining the rectangular size of the colored image
	 * which is needed for the calculation of the centering vertical offset.
	 *
	 * @param img
	 *        The image to measure.
	 * @param pixelWidth
	 *        The total width in pixels of the PNG.
	 * @param pixelHeight
	 *        The total height in pixels of the PNG.
	 * @return The number of translucent pixel rows at the bottom of the image.
	 */
	private static int bottomBoundarySpace (
		final @NotNull BufferedImage img,
		final int pixelWidth,
		final int pixelHeight)
	{
		int y = pixelHeight;
		while (y > -1)
		{
			int x = 0;
			while (x < pixelWidth)
			{
				if (img.getRGB(x, y) != 0)
				{
					return pixelHeight - y;
				}
				x++;
			}
			y--;
		}

		return pixelHeight - y;
	}

	/**
	 * Answer the vertical offset for the given image from the top of the
	 * provided canvas. This value represents the number of horizontal rows that
	 * are fully translucent at the top of the image.  This value is necessary
	 * for determining the rectangular size of the colored image which is needed
	 * for the calculation of the centering vertical offset.
	 *
	 * @param img
	 *        The image to measure.
	 * @param pixelWidth
	 *        The total width in pixels of the PNG.
	 * @param pixelHeight
	 *        The total height in pixels of the PNG.
	 * @return The number of translucent pixel rows at the top of the image.
	 */
	private static int topBoundarySpace (
		final @NotNull BufferedImage img,
		final int pixelWidth,
		final int pixelHeight)
	{
		int y = 0;
		while (y < pixelHeight)
		{
			int x = 0;
			while (x < pixelWidth)
			{
				if (img.getRGB(x, y) != 0)
				{
					return y;
				}
				x++;
			}
			y++;
		}
		return y;
	}

	/**
	 * Answer the horizontal offset for the given image from the left of the
	 * provided canvas. This value represents the number of vertical columns
	 * that are fully translucent to the left of the image.  This value is
	 * necessary for determining the rectangular size of the colored image
	 * which is needed for the calculation of the centering horizontal offset.
	 *
	 * @param img
	 *        The image to measure.
	 * @param pixelWidth
	 *        The total width in pixels of the PNG.
	 * @param pixelHeight
	 *        The total height in pixels of the PNG.
	 * @return The number of translucent pixel columns to the left of the image.
	 */
	private static int leftBoundary (
		final @NotNull BufferedImage img,
		final int pixelWidth,
		final int pixelHeight)
	{
		int x = 0;
		while (x < pixelWidth)
		{
			int y = 0;
			while (y < pixelHeight)
			{
				if (img.getRGB(x, y) != 0)
				{
					return x;
				}
				y++;
			}
			x++;
		}

		return x;
	}

	/**
	 * Answer the horizontal offset for the given image from the right of the
	 * provided canvas. This value represents the number of vertical columns
	 * that are fully translucent to the right of the image.  This value is
	 * necessary for determining the rectangular size of the colored image
	 * which is needed for the calculation of the centering horizontal offset.
	 *
	 * @param img
	 *        The image to measure.
	 * @param pixelWidth
	 *        The total width in pixels of the PNG.
	 * @param pixelHeight
	 *        The total height in pixels of the PNG.
	 * @return The number of translucent pixel columns to the right of the
	 *         image.
	 */
	private static int rightBoundary (
		final @NotNull BufferedImage img,
		final int pixelWidth,
		final int pixelHeight)
	{
		int x = pixelWidth;
		while (x > -1)
		{
			int y = 0;
			while (y < pixelHeight)
			{
				if (img.getRGB(x,y) != 0)
				{
					return pixelWidth - x;
				}
				y++;
			}
			x--;
		}

		return pixelWidth - x;
	}

	/**
	 * Answer a {@link Graphics2D} with the following preferences:
	 *
	 * <p>
	 * <ul>
	 * <li>{@link RenderingHints#KEY_ALPHA_INTERPOLATION},
	 *     {@link RenderingHints#VALUE_ALPHA_INTERPOLATION_QUALITY}</li>
	 * <li>{@link RenderingHints#KEY_ANTIALIASING},
	 *     {@link RenderingHints#VALUE_ANTIALIAS_ON}</li>
	 * <li>{@link RenderingHints#KEY_COLOR_RENDERING},
	 *     {@link RenderingHints#VALUE_COLOR_RENDER_QUALITY}</li>
	 * <li>{@link RenderingHints#KEY_DITHERING},
	 *     {@link RenderingHints#VALUE_DITHER_ENABLE}</li>
	 * <li>{@link RenderingHints#KEY_FRACTIONALMETRICS},
	 *     {@link RenderingHints#VALUE_FRACTIONALMETRICS_ON}</li>
	 * <li>{@link RenderingHints#KEY_INTERPOLATION},
	 *     {@link RenderingHints#VALUE_INTERPOLATION_BILINEAR}</li>
	 * <li>{@link RenderingHints#KEY_RENDERING},
	 *     {@link RenderingHints#VALUE_RENDER_QUALITY}</li>
	 * <li>{@link RenderingHints#KEY_STROKE_CONTROL},
	 *     {@link RenderingHints#VALUE_STROKE_PURE}</li>
	 * </ul>
	 * </p>
	 *
	 * @param img
	 *        The {@link BufferedImage} to create the {@code Graphics2D} from.
	 * @return A {@code Graphics2D}.
	 */
	private static Graphics2D createGraphics2D (
		final @NotNull BufferedImage img)
	{
		Graphics2D g2d = img.createGraphics();
		g2d.setRenderingHint(
			RenderingHints.KEY_ALPHA_INTERPOLATION,
			RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		g2d.setRenderingHint(
			RenderingHints.KEY_ANTIALIASING,
			RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(
			RenderingHints.KEY_COLOR_RENDERING,
			RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		g2d.setRenderingHint(
			RenderingHints.KEY_DITHERING,
			RenderingHints.VALUE_DITHER_ENABLE);
		g2d.setRenderingHint(
			RenderingHints.KEY_FRACTIONALMETRICS,
			RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		g2d.setRenderingHint(
			RenderingHints.KEY_INTERPOLATION,
			RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2d.setRenderingHint(
			RenderingHints.KEY_RENDERING,
			RenderingHints.VALUE_RENDER_QUALITY);
		g2d.setRenderingHint(
			RenderingHints.KEY_STROKE_CONTROL,
			RenderingHints.VALUE_STROKE_PURE);

		return g2d;
	}

	/**
	 * Generate PNG files for all the character codes in the provided range.
	 *
	 * @param inclusive
	 *        The integer value that represents the start character code for
	 *        generating PNGs.
	 * @param exclusive
	 *        The integer value that represents the character code of the
	 *        last character + 1 to be generated (this character will not be
	 *        generated).
	 * @param chosenFont
	 *        The target {@link Font} for the character.
	 * @param dir
	 *        The name of the directory the file should be exported to.
	 * @param color
	 *        The target {@link Color} for the {@code Font}.
	 * @param pixelHeight
	 *        The total height in pixels of the PNG.
	 * @param pixelWidth
	 *        The total width in pixels of the PNG.
	 */
	static void generateRange (
		final int inclusive,
		final int exclusive,
		final @NotNull Font chosenFont,
		final @NotNull String dir,
		final @NotNull Color color,
		final int pixelHeight,
		final int pixelWidth)
	{
		StringBuilder sb = new StringBuilder();
		int created = 0;
		int sizeIssue = 0;
		int noImg = 0;

		for (int i = inclusive; i < exclusive; i++)
		{
			if (chosenFont.canDisplay(i))
			{
				if (PNGGenerator.conditionallyExportCenteredImage(
					new String(Character.toChars(i)),
					chosenFont,
					color,
					pixelHeight,
					pixelWidth,
					dir))
				{
					created++;
				}
				else
				{
					sizeIssue++;
				}
			}
			else
			{
				sb.append(i).append('\n');
				noImg++;
			}
		}
		System.out.println(
			chosenFont.getName() + " does not have:\n" + sb.toString());
		System.out.println("==============");
		System.out.println();
		System.out.println("Created: " + created);
		System.out.println("Size issue: " + sizeIssue);
		System.out.println("No Image: " + noImg);
	}

}