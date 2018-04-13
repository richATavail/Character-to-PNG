/*
 * AppRuntime.java
 * Copyright © 2018, Richard A. Arriaga
 * All rights reserved.
 */

package raa.chartopng;
import org.jetbrains.annotations.NotNull;
import raa.utility.ConsoleUtility;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;
import java.util.concurrent.TimeUnit;

/**
 * A {@code AppRuntime} is is the object that holds the runtime state for this
 * application.
 *
 * @author Richard Arriaga &lt;rich@availlang.org&gt;
 */
public class AppRuntime
{
	/**
	 * {@code ExitCode} abstracts the allowed values for calls of {@link
	 * System#exit(int)}.
	 *
	 * <ol start="0">
	 *     <li>Normal exit: {@link #NORMAL_EXIT}</li>
	 *     <li>An unexpected error: {@link #UNSPECIFIED_ERROR}</li>
	 *     <li>An error in the configuration: {@link #CONFIGURATION_ERROR}</li>
	 * </ol>
	 */
	public enum ExitCode
	{
		/** Normal exit. */
		NORMAL_EXIT (0),

		/** An unexpected error. */
		UNSPECIFIED_ERROR(1),

		/** An error in the configuration. */
		CONFIGURATION_ERROR(2);

		/**
		 * The status code for {@link System#exit(int)}.
		 */
		private final int status;

		/**
		 * Shutdown the application with this {@link ExitCode}.
		 */
		public void shutdown ()
		{
			System.exit(status);
		}

		/**
		 * Construct an {@link ExitCode}.
		 *
		 * @param status
		 *        The status code for {@link System#exit(int)}.
		 */
		ExitCode (int status)
		{
			this.status = status;
		}
	}

	/**
	 * The sole {@link AppRuntime}.
	 */
	private static AppRuntime soleInstance;

	/**
	 * The {@link ThreadPoolExecutor} used by this application.
	 */
	private final @NotNull ThreadPoolExecutor threadPoolExecutor;

	/**
	 * Initialize the run-time environment.
	 */
	public static void initialize ()
	{
		// Should only be done once.
		if (soleInstance == null)
		{
			soleInstance = new AppRuntime();
		}
	}

	/**
	 * Schedule the provided {@link Runnable} with the {@link
	 * #threadPoolExecutor}.
	 *
	 * @param r
	 *        The {@link Runnable} to execute.
	 */
	public static void scheduleTask (final @NotNull Runnable r)
	{
		soleInstance.threadPoolExecutor.execute(r);
	}

	/**
	 * The {@link Semaphore} that is used to keep the application from shutting
	 * down at the end of the a {@code main} method.
	 */
	private final static Semaphore applicationSemaphore =
		new Semaphore(0);

	/**
	 * Block the main thread of execution from completing.
	 */
	public static void block ()
	{
		try
		{
			applicationSemaphore.acquire();
		}
		catch (final InterruptedException e)
		{
			ExitCode.NORMAL_EXIT.shutdown();
		}
	}

	/**
	 * The {@link ConsoleUtility} that provides interactivity for the running
	 * application.
	 */
	private final @NotNull ConsoleUtility console = ConsoleUtility.newUtility();

	public static @NotNull ConsoleUtility console()
	{
		return soleInstance.console;
	}

	/**
	 * Answer a String that provides a described report on a {@link List} of
	 * character code points.
	 *
	 * @param description
	 *        The String description of the attached list.
	 * @param codePoints
	 *        A {@code List} of character code points to report on.
	 * @return A String.
	 */
	public static @NotNull String codePointReport (
		final @NotNull String description,
		final @NotNull List<Integer> codePoints)
	{
		boolean subsequent = false;
		final StringBuilder sb = new StringBuilder(description);

		for (int i = 0; i < codePoints.size(); i++)
		{
			int current = codePoints.get(i);

			if (subsequent)
			{
				sb.append(", ");
			}
			sb.append(current);
			int lastWrite = current;

			while (i < codePoints.size())
			{
				if (i + 1 < codePoints.size())
				{
					int next = codePoints.get(i + 1);
					if (current + 1 == next)
					{
						current = next;
						i++;
					} else
					{
						if (lastWrite != current)
						{
							sb.append("-").append(current);
						}
						subsequent = true;
						break;
					}
				} else
				{
					if (lastWrite != current)
					{
						sb.append("-").append(current);
						subsequent = true;
					}
					break;
				}
			}

		}
		return sb.toString();
	}

	/**
	 * Only allow the {@link AppRuntime} to be created internally.
	 */
	private AppRuntime ()
	{
		this.threadPoolExecutor = new ThreadPoolExecutor(
			Runtime.getRuntime().availableProcessors(),
			Runtime.getRuntime().availableProcessors() << 2,
			10L,
			TimeUnit.SECONDS,
			new LinkedBlockingQueue<>(),
			runnable ->
			{
				final Thread thread = new Thread(runnable);
				thread.setDaemon(true);
				return thread;
			},
			new AbortPolicy());
	}
}
