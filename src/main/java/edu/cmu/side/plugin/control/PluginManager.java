package edu.cmu.side.plugin.control;

import java.io.File;
import java.io.IOException;

import java.nio.file.Path;
import java.nio.file.Paths;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
//import java.nio.file.FileSystems;

import oracle.xml.parser.v2.DOMParser;
import oracle.xml.parser.v2.XMLDocument;
import oracle.xml.parser.v2.XMLParseException;

import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.yerihyo.yeritools.collections.MapToolkit.ListValueMap;
import com.yerihyo.yeritools.debug.YeriDebug;
import com.yerihyo.yeritools.io.FileToolkit;
import com.yerihyo.yeritools.text.StringToolkit;
import com.yerihyo.yeritools.xml.XMLToolkit;

import edu.cmu.side.plugin.SIDEPlugin;

public class PluginManager {
	
	private static PluginManager pluginManager;
	private ListValueMap<String,PluginWrapper> pluginTypeMap = new ListValueMap<String,PluginWrapper>();
	private String errorMessage;
	private File pluginFile;
	
	public ListValueMap<String,PluginWrapper> getPluginTypeMap()
	{
		return pluginTypeMap; 
	}
	
	public Collection<PluginWrapper> getAllPlugins() {
		return getPluginTypeMap().valueElements();
	}
	public Set<String> getPluginTypes()
	{
		return  getPluginTypeMap().keySet();
	}

	synchronized public PluginWrapper getPluginWrapperByPluginClassName(String pluginClassName) {
		PluginWrapper result = null;

		for (Iterator<PluginWrapper> iTemp = getAllPlugins().iterator(); iTemp.hasNext();) {
			PluginWrapper pTemp = iTemp.next();
			if (pTemp.getConfigMap().get(PluginWrapper.CLASSNAME).equalsIgnoreCase(pluginClassName)) {
				result = pTemp;
				break;
			}
		}
		return result;
	}

	public List<SIDEPlugin> getPluginCollectionByType(String type) {
		List<SIDEPlugin> pluginList = new ArrayList<SIDEPlugin>();
		for(PluginWrapper pluginWrapper : getPluginWrapperCollectionByType(type)){
			SIDEPlugin sidePlugin = pluginWrapper.getSIDEPlugin();
			pluginList.add(sidePlugin);
		}
		return pluginList;
	}
	public List<PluginWrapper> getPluginWrapperCollectionByType(String type){
		List<PluginWrapper> pluginWrapperList = new ArrayList<PluginWrapper>();
		
		for(String key :  getPluginTypeMap().keySet()){
			if(!key.startsWith(type)){ continue; }
			pluginWrapperList.addAll( getPluginTypeMap().get(key));
		}
		return pluginWrapperList;
	}
	
	public static SIDEPlugin[] getSIDEPluginArrayByType(String type)
	{
		List<SIDEPlugin> sidePluginList = new ArrayList<SIDEPlugin>();
		
		Collection<PluginWrapper> pluginWrapperCollection =  getSharedPluginManager().getPluginTypeMap().get(type);
		for(PluginWrapper pluginWrapper : pluginWrapperCollection){
			SIDEPlugin sidePlugin = pluginWrapper.getSIDEPlugin();
			sidePluginList.add(sidePlugin);
		}
		return sidePluginList.toArray(new SIDEPlugin[0]);
	}

	public void addPluginWrapper(PluginWrapper pluginWrapper) {
		 getPluginTypeMap().add(pluginWrapper.getType(), pluginWrapper);
	}

	@Override
	public String toString() {
		return "PluginWrapper Manager";
	}
	
	private static XMLDocument XMLFromFile(File configFile) throws XMLParseException, SAXException, MalformedURLException, IOException
	{
        // Get an instance of the parser
        DOMParser parser = new DOMParser();

		//Generate a URL from the filename.
		URI url = configFile.toURI();

        // Set various parser options: validation on,
        // warnings shown, error stream set to stderr.
        parser.setErrorStream(System.err);
        //parser.setValidationMode(DTD_validation);
        parser.showWarnings(true);

        //Parse the document.
        parser.parse(url.toURL());

        // Obtain the document.
        XMLDocument doc = parser.getDocument();
        return doc;
	}
	
	private static Collection<PluginWrapper> createPluginOfFolder(File rootFolder, StringBuilder errorComment){
		List<PluginWrapper> pluginList = new ArrayList<PluginWrapper>();
		
		// config file
		File configFile = new File(rootFolder, "config.xml");
		if (!configFile.exists()) { return null; }
		
		XMLDocument config = null;
		try {
			config = XMLFromFile(configFile);//XMLBoss.XMLFromFile(configFile);
		} catch (Exception e) {
			errorComment.append(e.getMessage());
			return null;
		}
		if (config == null) {
			errorComment.append("The xmiFile '").append(FileToolkit.getAbsoluteCanonicalPath(configFile)).append("' has an invalid internal structure.");
			return null;
		}
		
		Element root = config.getDocumentElement();
		for (Element element : XMLToolkit.getChildElements(root)) {
			PluginWrapper pluginWrapper = new PluginWrapper(rootFolder,element);
			try {
				pluginWrapper.fromXML(element);
			} catch (Exception e) {
				errorComment.append(e.getMessage()).append(StringToolkit.newLine());
			}
			pluginList.add(pluginWrapper);
		}
		return pluginList;
	}

	protected static ListValueMap<String,PluginWrapper> createPluginTypeMap(File rootFolder, StringBuilder errorComment) {
//		System.out.println("PluginManager.ListValueMap, rootFolder path: " + rootFolder.getAbsolutePath());
		YeriDebug.ASSERT(rootFolder.isDirectory());
		
		ListValueMap<String,PluginWrapper> pluginTypeMap = new ListValueMap<String,PluginWrapper>();

		// config file
		Collection<? extends PluginWrapper> pluginCollection = createPluginOfFolder(rootFolder, errorComment);
		if(pluginCollection!=null){
			for(PluginWrapper pluginWrapper : pluginCollection){
				pluginTypeMap.add(pluginWrapper.getType(), pluginWrapper);
			}
		}
		
		// for recursive call onfolders
		File[] fileArray = rootFolder.listFiles();
		for(File file : fileArray){
			if(!file.isDirectory()){ continue; }
			ListValueMap<String,PluginWrapper> childPluginTypeMap = createPluginTypeMap(file, errorComment);
			pluginTypeMap.putAll(childPluginTypeMap);
		}
		return pluginTypeMap;
	}
	

	
	//the only pluginManager anyone cares about is the one instantiated by Workbench.
	private PluginManager(File rootFolder) {
		// Traverse the directory, building a PluginCollection for each folder
		// that we find
//		rootFolderCanonical = rootFolder.getCanonicalPath();
//		if(!rootFolderCanonical.isDirectory())
		if(!rootFolder.isDirectory())
//			throw new IllegalArgumentException("Plugin folder '"+rootFolder.toString() + "' is not a directory.");
			throw new IllegalArgumentException("Plugin folder '"+rootFolder.getAbsolutePath() + "' is not a directory.");
		pluginFile = rootFolder;

		//initialize static pluginTypeMap.
		StringBuilder errorStringBuilder = new StringBuilder();
		pluginTypeMap = createPluginTypeMap(rootFolder, errorStringBuilder);
		errorMessage = errorStringBuilder.toString();
		
	}

	public String getErrorMessage() {
		return errorMessage;
	}
	
	public static PluginManager getSharedPluginManager()
	{
		Path currentRelativePath = Paths.get("");
		Path currentAbsolutePath = currentRelativePath.toAbsolutePath();
		Integer lastPathElementIndex = currentAbsolutePath.getNameCount() - 1;
		Integer penultimatePathElementIndex = lastPathElementIndex - 1;
		String lastPathElement = currentAbsolutePath.getName(lastPathElementIndex).toString();
		String penultimatePathElement = currentAbsolutePath.getName(penultimatePathElementIndex).toString();
//		System.out.println("PluginManager constructor - current lastPathElement: " + lastPathElement);
		Path partialPath = null;
		String pathRoot = currentAbsolutePath.getRoot().toString();
		if (penultimatePathElement.equals("LightSide") && lastPathElement.equals("lib")) {
			partialPath = Paths.get("plugins");
		} else if (lastPathElement.equals("LightSide")) {
			partialPath = Paths.get("lib/plugins");
		} else if (lastPathElement.equals("Genesis-Plugins")) {
			String pathPrefix = currentAbsolutePath.subpath(0,lastPathElementIndex).toString();
			currentAbsolutePath = Paths.get(pathRoot + pathPrefix);
			partialPath = Paths.get("LightSide/lib/plugins");
		} else if (penultimatePathElement.equals("Genesis-Plugins") && lastPathElement.equals("lib")) {
//			Integer skipGenesisPluginsPathIndex = penultimatePathElementIndex - 1;
//			String pathPrefix = currentAbsolutePath.subpath(0, skipGenesisPluginsPathIndex).toString();
			String pathPrefix = currentAbsolutePath.subpath(0,penultimatePathElementIndex).toString();
			currentAbsolutePath = Paths.get(pathRoot + pathPrefix);
			partialPath = Paths.get("LightSide/lib/plugins");
		}
		Path rootFolderPath = currentAbsolutePath.resolve(partialPath);
		File rootFolder = new File(rootFolderPath.toString());
//		System.out.println("PluginManager constructor - rootFolder getAbsolutePath: " + rootFolder.getAbsolutePath());
		if(pluginManager == null)
		{
			pluginManager = new PluginManager(rootFolder); //TODO: maybe make this more flexible.
		}
		return pluginManager;
	}
	
	public static SIDEPlugin getPluginByClassname(String classname)
	{
		 return getSharedPluginManager().getPluginWrapperByPluginClassName(classname).getSIDEPlugin();
	}
}
