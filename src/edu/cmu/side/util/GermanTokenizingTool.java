package edu.cmu.side.util;

import java.util.Properties;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.PTBTokenizer.PTBTokenizerFactory;   // not needed? 
// import edu.stanford.nlp.pipeline.TokenizerAnnotator;

// import edu.stanford.nlp.process.WhitespaceTokenizer.WhitespaceTokenizerFactory;
// import edu.stanford.nlp.pipeline.StanfordCoreNLP; 
// import edu.stanford.nlp.international.german.process.GermanTokenizerPostProcessor; 
// import edu.stanford.nlp.pipeline.AnnotatorImplementations; 
// import edu/stanford/nlp/models/mwt/german/german-mwt.tsv; 
// import edu/stanford/nlp/models/pos-tagger/german-ud.tagger

import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class GermanTokenizingTool extends AbstractTokenizingTool
{
	// private static final Properties tokenizeProps = new Properties();
	// TokenizerAnnotator germanAnnotator;
    // String options = "invertible,ptb3Escaping=false,splitHyphenated=true"; 

	@Override
	public TokenizerFactory<CoreLabel> createTokenizerFactory()
	{
		System.out.println("In GermanTokenizingTool.java");
/**		
		tokenizeProps.setProperty("annotators", "tokenize, ssplit, mwt");
		// tokenizeProps.setProperty("annotators", "tokenize");
		tokenizeProps.setProperty("tokenize.language", "de"); 
		tokenizeProps.setProperty("tokenize.postProcessor", "edu.stanford.nlp.international.german.process.GermanTokenizerPostProcessor"); 
		tokenizeProps.setProperty("mwt.mappingFile", "edu/stanford/nlp/models/mwt/german/german-mwt.tsv"); 
		germanAnnotator = new TokenizerAnnotator(true,tokenizeProps,options); 
		return germanAnnotator.initFactory(null,tokenizeProps,options); 
**/
		// tokenizeProps.setProperty("", "");
		
		// >>>>>> StanfordCoreNLP pipeline = new StanfordCoreNLP(tokenizeProps);
		
		// String tempOut = pipeline.STANFORD_TOKENIZE; 
		// String tempOut = pipeline.AnnotatorPool.toString(); 
		// System.out.println(tempOut);
		
		// AnnotatorImplementations annotateImpls = new AnnotatorImplementations(); 
		// annotateImpls = pipeline.getAnnotatorImplementations(); 
		// System.out.println("AnnotatorImplementations:"); 
		// System.out.println(annotateImpls.toString());
		// System.out.println(pipeline.getAnnotatorImplementations().toString()); 		
		
		// FIXME: there is no special german tagger
		//return new WhitespaceTokenizerFactory<CoreLabel>(new CoreLabelTokenFactory(true), "invertible,unicodeQuotes=true,untokenizable=firstKeep");
		// return PTBTokenizerFactory.newPTBTokenizerFactory(new CoreLabelTokenFactory(true), "invertible,unicodeQuotes=true,untokenizable=firstKeep");
		
		// add ',untokenizable=firstKeep'? 
		// return PTBTokenizerFactory.newPTBTokenizerFactory(new CoreLabelTokenFactory(true), "invertible,ptb3Escaping=false,splitHyphenated=true");
		
		
		// TokenizerAnnotator tokAnn = new TokenizerAnnotator(true,tokenizeProps,"invertible,ptb3Escaping=false,splitHyphenated=true");
		// return PTBTokenizerFactory.newPTBTokenizerFactory(new CoreLabelTokenFactory(true),tokenizeProps,"invertible,ptb3Escaping=false,splitHyphenated=true"); 
		// return GermanTokenizer.GermanTokenizerFactory(new CoreLabelTokenFactory(true),"invertible,ptb3Escaping=false,splitHyphenated=true");  
		// return new GermanTokenizer.GermanTokenizerFactory(new CoreLabelTokenFactory(true),"invertible,unicodeQuotes=true,ptb3Escaping=false,splitHyphenated=true"); 
		return new GermanTokenizer.GermanTokenizerFactory(new CoreLabelTokenFactory(true)); 		
		
		
	}

	@Override
	protected MaxentTagger createTagger()
	{
		return new MaxentTagger("toolkits/maxent/german-fast-caseless.tagger");
	}

	@Override
	public
	String stopwordsFilename()
	{
		return "toolkits/german.stp";
	}
	
	@Override
	public String punctuationFilename()
	{
		return "toolkits/punctuation_german.stp";
	}

}
