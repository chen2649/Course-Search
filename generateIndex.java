package search;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.io.File;

public class generateIndex {

	// use analyzer, indexwriter for indexing
	public generateIndex(String indexPath,String docsPath, Analyzer a) 
			throws IOException {
		File docs_path=new File(docsPath);
	    Directory dir=FSDirectory.open(Paths.get(indexPath));
	    Analyzer analyzer = a;
	    IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
	    iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
	    IndexWriter indexWriter= new IndexWriter(dir,iwc);
	    
	    //loop over the documents and access fields in documents
	    for (File f:docs_path.listFiles()){
			String fileName = f.getName();
			String readBuffer = readFile(fileName,docsPath);        //readFile method is constructed below
			StringBuilder builder = new StringBuilder(readBuffer);
			
			String startDocTag = "<DOC>";
			String endDocTag = "</DOC>";
			int docStart = builder.indexOf(startDocTag);   
			while(docStart != -1)    
			{
			   docStart += startDocTag.length();
			   int docEnd = builder.indexOf(endDocTag,docStart);
			   
			   if(docEnd > 0)
			   {
				   StringBuilder document = new StringBuilder(builder.substring(docStart,docEnd).trim());
				   
				   Document doc = new Document();
				   String docNo = extract(document,"<DOCNO>", "</DOCNO>");    //extract method is constructed below
				   String dateLine = extract(document,"<DATELINE>", "</DATELINE>");
				   String head = extract(document,"<HEAD>", "</HEAD>");
				   String byLine = extract(document,"<BYLINE>", "</BYLINE>");
				   String text = extract(document,"<TEXT>", "</TEXT>");

				   doc.add(new StringField("DOCNO", docNo, Field.Store.YES));
				   doc.add(new StringField("DATELINE", dateLine, Field.Store.YES));
				   doc.add(new TextField("HEAD",head, Field.Store.YES));
				   doc.add(new TextField("BYLINE",byLine, Field.Store.YES));
				   doc.add(new TextField("TEXT",text, Field.Store.YES));
				   indexWriter.addDocument(doc); 
			   }
			   
			   docStart = builder.indexOf(startDocTag, docEnd);
			}	
	     
	    }
		indexWriter.forceMerge(1);
		indexWriter.commit();
		
		indexWriter.close();
	}
	
	//construct a readFile method to read documents above
	private String readFile(String file,String corpusPath) throws IOException 
	{
		file = corpusPath+"\\"+file;
		FileReader fileReader = new FileReader (file);
		BufferedReader reader = new BufferedReader(fileReader);
	    String         line = null;
	    StringBuilder  stringBuilder = new StringBuilder();
	    String         ls = System.getProperty("line.separator");

	    while((line = reader.readLine()) != null ) 
	    {
	        stringBuilder.append( line );
	        stringBuilder.append( ls );
	    }
		reader.close();
		
	    return stringBuilder.toString();    
	}
		
	//construct an extract method to extract context of a field above
	private String extract(StringBuilder buf, String startTag, String endTag)
	{
		String stringBetweenTags = new String();
		int k1 = buf.indexOf(startTag);
		while(k1 > 0)    
		{
		   k1 += startTag.length();
		   int k2 = buf.indexOf(endTag,k1);
		      
		   if (k2>=0)
		   {
			   stringBetweenTags +=(" " + buf.substring(k1,k2).trim());  
		   }
		   
		   k1 = buf.indexOf(startTag, k2);
		}
		return stringBetweenTags;	  
	}
	
	
	//get some statistics about the index and save the tokens to a text file. 
    public void statistics(String indexPath, String s) 
			throws CorruptIndexException, LockObtainFailedException,IOException{
    	IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexPath)));
		//Print the total number of documents in the corpus
		System.out.println("Total number of documents in the corpus:"+reader.maxDoc());
					
		//Print the size of the vocabulary for <field>content</field>, only available per-segment.
		Terms vocabulary = MultiFields.getTerms(reader, "TEXT");
		System.out.println("Number of terms for this field: "+vocabulary.size());
				
		//Print the total number of tokens for <field>TEXT</field>
		System.out.println("Number of tokens for this field: "+vocabulary.getSumTotalTermFreq());
			
		//Print the vocabulary for <field>TEXT</field>
		TermsEnum iterator = vocabulary.iterator();
		BytesRef byteRef = null;
		System.out.println("\n*******Vocabulary-Start**********");
		PrintWriter writer=new PrintWriter("tokens_output_"+s+".txt");
        while((byteRef = iterator.next()) != null) {
		String term = byteRef.utf8ToString();
		//System.out.println(term+"\t");
		writer.println(term);
		}
		System.out.println("\n*******Vocabulary-End**********");
		writer.close();
		
		reader.close();		
		}
}

