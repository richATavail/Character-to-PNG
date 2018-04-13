OVERVIEW
--------------------------------------------------------------------------------
This project came about to assist a friend building a rogue-like. The rogue-like
UI uses tiles, however the intent was to still utilize unicode characters to
draw the game board. This application is geared towards generating mass amounts
of png files from characters of varying fonts, styles, and colors.

The application has two entry points for generating files:
	1) ConsoleApplication.java
		A command line tool that allows for the  customization of character code
		point ranges via command line driven interaction between the user and
		the application. The intent here is to
	   create small sample sets of font files.
	2) XMLApplication.java
		A command line tool that prompts the user for the location of a
		configuration file (or uses the default config/generator_plan.xml). The
		XML format as described in the default configuration file allows for the
		generation of multiple fonts, styles, colors, and ranges. Unlike
		ConsoleApplication, the expectation is higher volumes of files, so these
		files are generated concurrently.