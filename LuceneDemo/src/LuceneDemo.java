

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;


public class LuceneDemo 
{
	public static String readFile(File f) {
		StringBuffer sb = new StringBuffer();
		Scanner scan;
		try {
			scan = new Scanner(f);
		
		while(scan.hasNext()){
			sb.append(scan.nextLine());
		}
		System.out.println(sb.toString());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sb.toString();
		
	}
	public static void main(String[] args)
	{
		try
		{
			//	Specify the analyzer for tokenizing text.
		    //	The same analyzer should be used for indexing and searching
			SmartChineseAnalyzer analyzer = new SmartChineseAnalyzer(Version.LUCENE_46);
			
			//	Code to create the index
			Directory index = new RAMDirectory();
			
			IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_46, analyzer);
			File dir = new File("�����ձ������");
			IndexWriter w = new IndexWriter(index, config);
			
			for(File f:dir.listFiles()){
				String fileN = f.getPath();
				
				if(!fileN.contains("Title")&&!fileN.equals("/Users/Page/Downloads/�����ձ������/.DS_Store")){
					String titlePath = fileN.substring(0, fileN.length()-4)+"_Title.txt";
					String content = readFile(f);
					String title = readFile(new File(titlePath));
					addDoc(w,content,title);
				}
			}
			
			String text = "IK Analyzer��һ����ϴʵ�ִʺ��ķ��ִʵ����ķִʿ�Դ���߰�����ʹ����ȫ�µ����������ϸ�����з��㷨��";
			String text2 = "�ִʵ����ķִʿ�Դ���߰�����ʹ����ȫ�µ����������ϸ�����з��㷨��";
			String text3 = "IK Analyzer��һ����ϴʵ�ִʺ��ķ��ִʵ����ķִʿ�Դ���߰�";
			String text4 = "��ʹ����ȫ�µ����������ϸ�����з��㷨��";
			
			addDoc(w,text, "193398817");
			addDoc(w, text2, "55320055Z");
			addDoc(w, text3, "55063554A");
			addDoc(w, text4, "9900333X");
			w.close();
			
			//	Text to search
			String querystr = args.length > 0 ? args[0] : "����";
			
			//	The "title" arg specifies the default field to use when no field is explicitly specified in the query
			Query q = new QueryParser(Version.LUCENE_46, "content", analyzer).parse(querystr);
			
			// Searching code
			int hitsPerPage = 10;
		    IndexReader reader = DirectoryReader.open(index);
		    IndexSearcher searcher = new IndexSearcher(reader);
		    TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, true);
		    searcher.search(q, collector);
		    ScoreDoc[] hits = collector.topDocs().scoreDocs;
		    
		    //	Code to display the results of search
		    System.out.println("Found " + hits.length + " hits.");

		    for(int i=0;i<hits.length;++i) 
		    {
		    	System.out.println(hits[i].score);
		      int docId = hits[i].doc;
		      Document d = searcher.doc(docId);
		      System.out.println((i + 1) + "."+docId+". " + d.get("isbn") + "\\t" + d.get("title"));
		    }
		    
		    // reader can only be closed when there is no need to access the documents any more
		    reader.close();
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
	}
	private static void addDoc(IndexWriter w, String content, String title) throws IOException 
	{
		  Document doc = new Document();
		  // A text field will be tokenized
		  doc.add(new TextField("content", content, Field.Store.YES));
		  // We use a string field for isbn because we don\'t want it tokenized
		  doc.add(new StringField("title", title, Field.Store.YES));
		  w.addDocument(doc);
	}
}