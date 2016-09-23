package search;

import org.apache.lucene.analysis.Analyzer;

import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.store.LockObtainFailedException;

import java.io.IOException;

//compare different analyzers
public class indexComparison {
	
    public static void main(String[] args) 
            throws CorruptIndexException, LockObtainFailedException, IOException {

    		String indexPath = "C:/Users/Bao/Desktop/index";
    		String docsPath = "C:/Users/Bao/Desktop/corpus/corpus";
    		Analyzer keyAn = new KeywordAnalyzer();
    		Analyzer simAn = new SimpleAnalyzer();
    		Analyzer stpAn = new StopAnalyzer();
    		Analyzer stdAn = new StandardAnalyzer();
    		
    		//KeywordAnalyzer
    		generateIndex keyIndex = new generateIndex(indexPath, docsPath, keyAn);
    		System.out.println("Keyword Analyzer:");
    		keyIndex.statistics(indexPath,"Keyword");
    		System.out.println();
    		
    		//SimpleAnalyzer
    		generateIndex simIndex = new generateIndex(indexPath, docsPath, simAn);
    		System.out.println("Simple Analyzer:");
    		simIndex.statistics(indexPath,"Simple");
     		System.out.println(); 
    		
     		//StopAnalyzer
    		generateIndex stpIndex = new generateIndex(indexPath, docsPath, stpAn);
        	System.out.println("Stop Analyzer:");
   	    	stpIndex.statistics(indexPath,"Stop");
    		System.out.println();   
    		
    		//StandardAnalyzer
    		generateIndex stdIndex = new generateIndex(indexPath, docsPath, stdAn);
    		System.out.println("Standard Analyzer:");	
    		stdIndex.statistics(indexPath, "Standard");	
    		
    }
}

