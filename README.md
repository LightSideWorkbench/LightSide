[![Alt text for the image](HTML/lightside_research_title.png)](https://github.com/LightSideWorkbench/LightSide/blob/main/HTML/lightside_research_title.png)


The LightSide Researcher's Workbench is an open-source text-mining tool released under the GNU General Public License. 

See `copyright/gpl.txt` for more information.

To build from source, use *ant*:

    ant build

To build with Chinese support, use

    ant build-intl

This will compile the workbench and run a modest set of unit tests. 
After that, you can run LightSide by executing run.sh (Linux, Mac) or LightSIDE.bat (Windows)

To add new feature-extraction, machine-learning, or analysis tools to the workbench, you'll want to write a plugin. 
See the appendix in the [Researcher's Manual](https://github.com/LightSideWorkbench/LightSide/blob/main/LightSide_Researchers_Manual.pdf) for more information, and the core LightSide [plugins repository](https://github.com/LightSideWorkbench/Genesis-Plugins) for examples.


<hr style="border: 2px solid gray;">

## Running LightSide on Mac

For a more detailed version of these instructions, see [Tips for Running LightSide on Mac](https://github.com/LightSideWorkbench/LightSide/blob/main/Tips%20for%20Running%20LightSide%20on%20Mac.pdf).

1.	Install a Java version between Java 1.8 (aka Java 8) and Java 17 if it is not already installed.

    _Note_: LightSide is likely to work with Java versions greater than Java 17. It has not yet been tested beyond Java 17. 

2.	Running LightSide from a terminal:
    *	In a terminal window, navigate to the directory where LightSide is installed – the same directory that this document is in. E.g.:  `cd 
    ~/Desktop/Lightside`
    *	Enter: `./LightSide.command`. (Include the period "." at the beginning of the command.)
    *	If you get a response of "operation not permitted," you may have to give your terminal "Full Disk Access".

<hr style="border: 2px solid gray;">

## Running LightSide on PC

For a more detailed version of these instructions, see [Tips for Running LightSide on PC](https://github.com/LightSideWorkbench/LightSide/blob/main/Tips%20for%20Running%20LightSide%20on%20PC.pdf).

1.	Install a Java version between Java 1.8 (aka Java 8) and Java 17 if it is not already installed.

    _Note_: LightSide is likely to work with Java versions greater than Java 17. It has not yet been tested beyond Java 17. 

2.	Running LightSide from File Explorer:
    *	In File Explorer, navigate to the directory where LightSide is installed – the same directory that this document is in. E.g.:  `Desktop\Lightside`
    *	Double-click file `LightSide.bat`. If you get a message that it is from an unidentified developer, approve it.

3.	Running LightSide from a terminal:
    *	In a terminal window, navigate to the directory where LightSide is installed – the same directory that this document is in. E.g.:  cd `Desktop/Lightside`.
    *	Enter: `.\LightSide.bat`. (Include the period "." at the beginning of the command.)


<hr style="border: 2px solid gray;">

If you have a problem running LightSide after following the instructions above, the file lightside_log.log at the top level of the LightSide directory will likely provide information about what went wrong. Scroll down to the bottom of the file to see the most recent log messages.

