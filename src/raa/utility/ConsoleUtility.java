/*
 * ConsoleUtility.java
 * Copyright © 2018, Richard A. Arriaga
 * All rights reserved.
 */

package raa.utility;
import java.io.Console;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Formatter;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

/**
 * A {@code ConsoleUtility} is TODO: Document this!
 *
 * @author Richard Arriaga &lt;rich@availlang.org&gt;
 */
public abstract class ConsoleUtility
{
	/**
	 * Print a String.
	 *
	 * @param s
	 *        The String to print.
	 */
	public abstract void print (final String s);

	/**
	 * Print a String and then terminate the line.
	 *
	 * @param s
	 *        The String to print.
	 */
	public abstract void println (final String s);

	/**
	 * Print a String {@linkplain String#format(String, Object...) formatted
	 * String}.
	 *
	 * @param s
	 *        The base String that adheres to the rules for the base String in
	 *        {@link Formatter}.
	 * @param args
	 *        The {@code Formatter} arguments to place in the String.
	 */
	public void printf (final String s, final Object... args)
	{
		print(String.format(s, args));
	}

	/**
	 * Read a line of text from input.
	 *
	 * @param prompt
	 *        The String to print to prompt the user for input.
	 * @return A String.
	 */
	protected abstract String readLine (final String prompt);

	/**
	 * Read a nonempty String from input.
	 *
	 * @param prompt
	 *        The String to print to prompt the user for input.
	 * @return A String.
	 */
	public String readNonEmptyString (final String prompt)
	{
		String line = readLine(prompt);
		while (line.isEmpty())
		{
			println("Nothing was entered!");
			line = readLine(prompt);
		}
		return line;
	}

	/**
	 * Read a String directory path from input.
	 *
	 * @param prompt
	 *        The String to print to prompt the user for input.
	 * @return A String.
	 */
	public String readDirectoryPath (final String prompt)
	{
		do
		{
			final String rawPath = readNonEmptyString(prompt);
			final String path = FileUtility.platformAppropriatePath(rawPath);

			if (FileUtility.directoryExists(path))
			{
				return path;
			}
			else
			{
				println(rawPath + " is not a valid directory!");
			}
		}
		while (true);
	}

	/**
	 * Read a String file path from input.
	 *
	 * @param prompt
	 *        The String to print to prompt the user for input.
	 * @return A String.
	 */
	@SuppressWarnings("WeakerAccess")
	public String readFilePath (final String prompt)
	{
		do
		{
			final String rawPath = readNonEmptyString(prompt);
			final String path = FileUtility.platformAppropriatePath(rawPath);

			if (FileUtility.directoryExists(path))
			{
				return path;
			}
			else
			{
				println(rawPath + " is not a valid file!");
				return readFilePath(prompt);
			}
		}
		while (true);
	}

	/**
	 * Read a {@code boolean} from input.
	 *
	 * <p>{@code true} is indicated by:</p>
	 * <ul>
	 *     <li>y</li>
	 *     <li>yes</li>
	 *     <li>true</li>
	 * </ul>
	 *
	 * <p>All other entered values are considered {@code false}.</p>
	 *
	 * @param prompt
	 *        The String to print to prompt the user for input.
	 * @return A {@code boolean}.
	 */
	@SuppressWarnings("unused")
	public boolean readBoolean (final String prompt)
	{
		final String line = readNonEmptyString(prompt);
		return line.equalsIgnoreCase("y")
			|| line.equalsIgnoreCase("yes")
			|| line.equalsIgnoreCase("true");
	}

	/**
	 * Read an {@code integer} from input.
	 *
	 * @param prompt
	 *        The String to print to prompt the user for input.
	 * @return An {@code integer}.
	 */
	@SuppressWarnings("WeakerAccess")
	public int readInt (final String prompt)
	{
		do
		{
			final String line = readNonEmptyString(prompt);
			int choice;
			try
			{
				choice = Integer.parseInt(line);
			}
			catch (final NumberFormatException e)
			{
				println(line + " is not a valid integer");
				continue;
			}
			return choice;
		}
		while (true);
	}

	/**
	 * Read an {@code integer} from input that is greater or equal to the
	 * provided {@code lowerBound}.
	 *
	 * @param prompt
	 *        The String to print to prompt the user for input.
	 * @param lowerBound
	 *        The lower bound, inclusive, that the entry must be greater
	 *        than or equal to.
	 * @return An {@code integer}.
	 */
	public int readLowerBoundedInt (
		final String prompt,
		final int lowerBound)
	{
		do
		{
			final int entry = readInt(prompt);
			if (entry >= lowerBound)
			{
				return entry;
			}
			println(entry + " is not a greater than or equal to " + lowerBound);
			println("Entered value must be greater than equal to "
				+ lowerBound);
		}
		while (true);
	}

	/**
	 * Read an {@code integer} from input that is strictly lesser than the
	 * provided {@code upperBound}.
	 *
	 * @param prompt
	 *        The String to print to prompt the user for input.
	 * @param upperBound
	 *        The upper bound, not inclusive, that the entry must be lesser
	 *        than.
	 * @return An {@code integer}.
	 */
	public int readUpperBoundedInt (
		final String prompt,
		final int upperBound)
	{
		do
		{
			final int entry = readInt(prompt);
			if (entry < upperBound)
			{
				return entry;
			}
			println(entry + " is not a lesser than " + upperBound);
			println("Entered value must be lesser than " + upperBound);
		}
		while (true);
	}

	/**
	 * Read an {@code integer} from input that is bounded by the {@code
	 * lowerBound}, inclusive, and the {@code upperBound}, exclusive.
	 *
	 * @param prompt
	 *        The String to print to prompt the user for input.
	 * @param lowerBound
	 *        The lower bound inclusive, that the entry must be greater
	 *        than or equal to.
	 * @param upperBound
	 *        The upper bound, not inclusive, that the entry must be lesser
	 *        than.
	 * @return An {@code integer}.
	 */
	public int readBoundedInt (
		final String prompt,
		final int lowerBound,
		final int upperBound)
	{
		do
		{
			final int entry = readInt(prompt);
			if (entry < upperBound && entry >= lowerBound)
			{
				return entry;
			}
			println(entry + " is not in the range [" + lowerBound + ","
				+ upperBound + ").\nEntered value must be in this range.");
		}
		while (true);
	}

	/**
	 * Read many space-delimited {@code integers} from input for the provided
	 * options.
	 *
	 * @param prompt
	 *        The String to print to prompt the user for input.
	 * @param options
	 *        A {@link Set} of {@code integers} that are the available options
	 *        to choose from.
	 * @return An {@code integer}.
	 */
	@SuppressWarnings("WeakerAccess")
	public Set<Integer> multiSelectIntsFromChoices (
		final String prompt,
		final Set<Integer> options)
	{
		do
		{
			final String line = readNonEmptyString(prompt);
			final String[] selected = line.trim().split(" ");
			final Set<Integer> choices = new HashSet<>();

			for (final String value : selected)
			{
				try
				{
					int choice = Integer.parseInt(value);
					if (!options.contains(choice))
					{
						println(choice + " is not a valid option");
						break;
					}
					choices.add(choice);
				}
				catch (final NumberFormatException e)
				{
					println(line + " contains invalid options");
					break;
				}
			}
			if (choices.size() == selected.length)
			{
				return choices;
			}
		}
		while (true);
	}

	/**
	 * Read an {@code integer} from the input that is one of the values from
	 * the provided options.
	 *
	 * @param prompt
	 *        The String to print to prompt the user for input.
	 * @param options
	 *        An array of {@code integers} that are the available options to
	 *        choose from.
	 * @return An {@code integer}.
	 */
	public int readIntFromChoices (final String prompt, Integer... options)
	{
		assert options.length > 0 : "Need options to choose from!";
		do
		{
			final Set<Integer> choices = new HashSet<>(Arrays.asList(options));
			final int choice = readInt(prompt);

			if (!choices.contains(choice))
			{
				println(choice + " is not a valid option");
				continue;
			}
			return choice;
		}
		while (true);
	}

	/**
	 * Read an {@code integer} from the input that is one of the values from
	 * the provided options.
	 *
	 * @param prompt
	 *        The String to print to prompt the user for input.
	 * @param options
	 *        A {@link Set} of {@code integers} that are the available options
	 *        to choose from.
	 * @return An {@code integer}.
	 */
	public int readIntFromChoices (final String prompt, Set<Integer> options)
	{
		assert options.size() > 0 : "Need options to choose from!";
		do
		{
			final int choice = readInt(prompt);
			if (!options.contains(choice))
			{
				println(choice + " is not a valid option");
				continue;
			}
			return choice;
		}
		while (true);
	}

	/**
	 * Answer a new {@link ConsoleUtility} appropriate to the environment.
	 *
	 * @return A {@code ConsoleUtility}.
	 */
	public static ConsoleUtility newUtility ()
	{
		return System.console() == null
			? new ScannerPrintStreamConsoleUtility()
			: new SystemConsoleUtility();
	}

	/**
	 * A {@code SystemConsoleUtility} is a {@link ConsoleUtility} that
	 * specifically utilizes the {@link System#console()} for input and output.
	 */
	private static class SystemConsoleUtility extends ConsoleUtility
	{
		/**
		 * The {@link System#console()}.
		 */
		private final Console console = System.console();

		@Override
		public void print (final String s)
		{
			console.readLine(s);
		}

		@Override
		public void println (final String s)
		{
			console.printf("%s%n", s);
		}

		@Override
		public String readLine (final String prompt)
		{
			return console.readLine(prompt);
		}

		/* Prevent instantiation outside the outer class */
		private SystemConsoleUtility () { /* Do Nothing */ }
	}

	/**
	 * A {@code SystemConsoleUtility} is a {@link ConsoleUtility} that
	 * specifically utilizes the {@link System#in} in a {@link Scanner} for
	 * input and the {@link System#in} {@link PrintStream} for output.
	 */
	private static class ScannerPrintStreamConsoleUtility extends ConsoleUtility
	{
		/**
		 * A {@link Scanner} to be used for getting user input if the {@link
		 * Console} is not available.
		 */
		private final Scanner scanner = new Scanner(System.in);

		/**
		 * The {@link PrintStream} to print data if the {@link Console} is not
		 * available.
		 */
		private final PrintStream out = System.out;

		@Override
		public void print (final String s)
		{
			out.print(s);
		}

		@Override
		public void println (final String s)
		{
			out.println(s);
		}

		@Override
		public String readLine (final String prompt)
		{
			out.print(prompt);
			return scanner.nextLine();
		}
		/* Prevent instantiation outside the outer class */
		private ScannerPrintStreamConsoleUtility () { /* Do Nothing */ }
	}
}
