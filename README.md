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