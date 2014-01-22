import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class CustomizedDicTest {

//	public static void testIkAnalyser() throws Exception {
//
//		String content = "刘朱九思江丁北庄村苏东台唐洋轩轩我是中国人陆小凤黑河东京朱总";
//
//		StringReader reader = new StringReader(content);
//
//		Analyzer analyzer = new IKAnalyzer();
//
//		TokenStream ts = analyzer.tokenStream("", reader);
//
//		CharTermAttribute termAtt = (CharTermAttribute) ts
//				.getAttribute(CharTermAttribute.class);
//
//		while (ts.incrementToken()) {
//
//			System.out.println(termAtt.toString());
//
//		}
//
//	}

	public static void main(String[] args) {
		String str = "最希望从企业得到的是独家的内容或销售信息，获得打折或促销信息等；最不希望企业进行消息或广告轰炸及访问用户的个人信息等。这值得使用社会化媒体的企业研究";

		IKAnalysis(str);
		}

		public static String IKAnalysis(String str) {
		StringBuffer sb = new StringBuffer();
		try {
		// InputStream in = new FileInputStream(str);//
		byte[] bt = str.getBytes();// str
		InputStream ip = new ByteArrayInputStream(bt);
		Reader read = new InputStreamReader(ip);
		IKSegmenter iks = new IKSegmenter(read, true);
		Lexeme t;
		while ((t = iks.next()) != null) {
		sb.append(t.getLexemeText() + " , ");

		}
		sb.delete(sb.length() - 1, sb.length());
		} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		}
		System.out.println(sb.toString());
		return sb.toString();

		}
}