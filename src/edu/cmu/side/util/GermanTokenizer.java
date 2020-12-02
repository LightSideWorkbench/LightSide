package edu.cmu.side.util;

import java.io.*;
import java.util.*;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.io.RuntimeIOException;
import edu.stanford.nlp.util.Generics;
import edu.stanford.nlp.util.PropertiesUtils;
import edu.stanford.nlp.util.StringUtils;


import edu.stanford.nlp.process.AbstractTokenizer;
import edu.stanford.nlp.process.PTBTokenizer;
// import edu.stanford.nlp.process.PTBTokenizer.PTBTokenizerFactory;
import edu.stanford.nlp.international.german.process.GermanTokenizerPostProcessor; 
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.LexedTokenFactory;
import edu.stanford.nlp.process.Tokenizer;
import edu.stanford.nlp.process.TokenizerFactory;
// import edu.stanford.nlp.sequences.SeqClassifierFlags;
// import edu.stanford.nlp.ie.crf.CRFClassifier;
// import edu.stanford.nlp.pipeline.TokenizerAnnotator;
// import edu.cmu.side.util.GermanTokenizerAnnotator;
// import edu.stanford.nlp.process.PTBLexer;
// import edu.stanford.nlp.process.PTBLexer.flex; 

 
public class GermanTokenizer<T extends HasWord> extends AbstractTokenizer<T>  {
	  // public class GermanTokenizer<T extends HasWord> extends PTBTokenizer<T>  {

	  // private final PTBLexer lexer;
	  // private static final CRFClassifier<CoreLabel> segmenter;
	  // private final Tokenizer<T> theTokenizer;  
	  // private final TokenizorAnnotator germanAnnotator; 
	  // TokenizorAnnotator germanAnnotator; 
	  // private final GermanTokenizer<CoreLabel> germanTokenizer;  
		  
      private static String germanOptions = "invertible,ptb3Escaping=false,splitHyphenated=true"; 
      // String germanOptions = "invertible,unicodeQuotes=true,ptb3Escaping=false,splitHyphenated=true"; 
      // String germanOptions = "invertible,unicodeQuotes=false,ptb3Escaping=false,splitHyphenated=true";    	
	  // private static final Properties tokenizeProps = new Properties();	
	  private static final Properties lexerProperties = new Properties();	// Not really doing anything with this
	  // private final PTBTokenizer.PTBTokenizerFactory ptbFactory; 
	  // private final Tokenizer tokenizer; 
	  private static PTBTokenizer<CoreLabel> ptbTokenizer; 
	  // private final GermanTokenizerPostProcessor germanPostProcessor; 
	  private static final GermanTokenizerPostProcessor germanPostProcessor = new GermanTokenizerPostProcessor(); 
	  private final LexedTokenFactory<T> theTokenFactory;  
	  private List<CoreLabel> tokens;
	  // List<T> tokens;
	  // ArrayList tokenArray; 
	  T token; 
	  Object tokenArray[]; 
	  // T tokenArray[]; 
	  // T[] tokenArray; 
	  // Corelabel[] tokenArray; 
	  private Integer currentTokenIndex = 0; 
	  private Integer numTokens; 
	  

	  public static GermanTokenizer<CoreLabel> newGermanTokenizer(Reader r, LexedTokenFactory<CoreLabel> tokenFactory) {
		  	System.out.println("Debug: In newGermanTokenizer");
		    return new GermanTokenizer<>(r, tokenFactory);
		  }	  
	  
	  public GermanTokenizer(Reader r, LexedTokenFactory<T> tokenFactory) {
		  
		  System.out.println("Debug: In GermanTokenizer constructor");
		  theTokenFactory = tokenFactory;
		  
		  // NOT GOING TO USE tokenizeProps? 
		  // tokenizeProps.setProperty("annotators", "tokenize, ssplit, mwt");
		  // tokenizeProps.setProperty("tokenize.language", "de"); 
		  // tokenizeProps.setProperty("tokenize.verbose", "true"); 
		  // tokenizeProps.setProperty("tokenize.postProcessor", "edu.stanford.nlp.international.german.process.GermanTokenizerPostProcessor"); 
		  // tokenizeProps.setProperty("mwt.mappingFile", "edu/stanford/nlp/models/mwt/german/german-mwt.tsv"); 
		  
		  // ptbFactory = PTBTokenizer.PTBTokenizerFactory.newPTBTokenizerFactory(tokenFactory,germanOptions); 
		  // ptbTokenizer = (PTBTokenizer) ptbFactory.getTokenizer(r); 
		  
		  ptbTokenizer = new PTBTokenizer(r,tokenFactory,germanOptions); 
		  	  
		  // Get from MWT process next? 
	  }

	  @Override
	  public List<T> tokenize() {
		  
		  System.out.println("Debug: In GermanTokenizer.tokenize()");
		  // static T token; 
		  tokens = ptbTokenizer.tokenize();
		  tokens = germanPostProcessor.process(tokens);
		  numTokens = tokens.size(); 
		  // tokenArray = new T[numTokens];
		  tokenArray = new Object[numTokens];
		  System.out.println("numTokens: " + numTokens.toString()); 
		  
		  // tokenArray = (T[]) tokens.toArray(new Object[tokens.size()]);
		  for (int i=0; i < numTokens; i++) {
			  System.out.println("Debug: GermanTokenizer tokenize() loop - 1");
			  System.out.println("tokenArray length: " + String.valueOf(tokenArray.length)); 
			  // System.out.println("Debug: GermanTokenizer tokenize() loop - 2");
			  token = (T) tokens.get(i);
			  if (token != null) {
				  System.out.println("token " + String.valueOf(i) + ": " + token.toString()); 
				  // tokenArray[i] = "foo"; 
				  tokenArray[i] = token; 
			  }
		  }	
		  System.out.println("Debug: GermanTokenizer tokenize() complete");
		  return (List<T>) tokens; 
	  }

	  @Override
	  protected T getNext() {
		  if (currentTokenIndex >= numTokens) {
			  return null; 
		  } else {
			  // T token = tokenArray[currentTokenIndex];
			  T token = (T) tokenArray[currentTokenIndex];
			  currentTokenIndex++; 
			  return token; 			  
		  }
//		    try {
//		      return (T) lexer.next();
//		    } catch (IOException e) {
//		      throw new RuntimeIOException(e);
//		    }
	  }
	  
	  
 	    

	  public static class GermanTokenizerFactory<T extends HasWord> implements TokenizerFactory<T>, Serializable  {

	 
		private static final long serialVersionUID = 8042746676832221075L;
		protected final LexedTokenFactory<T> factory;
	    // protected Properties lexerProperties = new Properties();	// Not really doing anything with this 

	    
	    public static TokenizerFactory<CoreLabel> newTokenizerFactory() {
	      return new GermanTokenizerFactory<>(new CoreLabelTokenFactory(true));
	    }

	    GermanTokenizerFactory(LexedTokenFactory<T> factory) {
	      this.factory = factory;
	    }

	    @Override
	    public Iterator<T> getIterator(Reader r) {
	      return getTokenizer(r);
	    }

	    @Override
	    public Tokenizer<T> getTokenizer(Reader r) {
	      return new GermanTokenizer<>(r, factory);
	    }

	    /**
	     * options: A comma-separated list of options
	     */
	    @Override
	    public void setOptions(String options) {
	      String[] optionList = options.split(",");
	      for (String option : optionList) {
	        lexerProperties.setProperty(option, "true");    	// Not really doing anything with this
	      }
	    }

	    @Override
	    public Tokenizer<T> getTokenizer(Reader r, String extraOptions) {
	      setOptions(extraOptions);                  			// Not really doing anything with this
	      return getTokenizer(r);
	    }

	  } // end static class GermanTokenizerFactory

	 

	  public static TokenizerFactory<CoreLabel> factory() {
	    TokenizerFactory<CoreLabel> tf = GermanTokenizerFactory.newTokenizerFactory();
		for (String option : lexerProperties.stringPropertyNames()) {
	      tf.setOptions(option);								// Not really doing anything with this
	    }
	    return tf;
	  }
}