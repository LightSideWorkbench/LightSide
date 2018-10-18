The LightSide Researcher's Workbench is an open-source text-mining tool released under the GNU General Public License. 
To download the latest public release, visit [http://ankara.lti.cs.cmu.edu/side](http://ankara.lti.cs.cmu.edu/side).
See `copyright/gpl.txt` for more information.

![Codeship](https://www.codeship.io/projects/175d7e90-a872-0131-b075-7a776696ef02/status "Codeship Status")

This is a mirror of the LightSide [bitbucket repository](https://bitbucket.org/lightsidelabs/lightside).

To build from source, use *ant*:

    ant build

This will compile the workbench and run a modest set of unit tests. 
After that, you can run LightSide by executing run.sh (Linux, Mac) or LightSIDE.bat (Windows)

To add new feature-extraction, machine-learning, or analysis tools to the workbench, you'll want to write a plugin. 
See the appendix in the [Researcher's Manual](http://ankara.lti.cs.cmu.edu/side/LightSide_Researchers_Manual.pdf) for more information, and the core LightSide [plugins repository](https://bitbucket.org/lightsidelabs/genesis-plugins) for examples.


Note: uses library yeritools, mentioned here: http://lti-side.wikidot.com/developer:documentation
but for which source code seems no longer to be available.


Note: to upgrade weka I had to do the following:
  * get the latest weka.jar file into lib
  * use java -jar lib/weka.jar
  * use the package manager to install libLinear, LibSVM, ...
  * move wekafiles from my home directory into lightside directory
  * download mtj.jar, ... etc. and also put in lib (because of this problem: https://web.archive.org/web/20170519101449/https://list.waikato.ac.nz/pipermail/wekalist/2017-February/068928.html)
  
