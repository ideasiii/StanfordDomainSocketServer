package chineseparser;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;

import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreePrint;

public class ChineseParser
{
  LexicalizedParser myParser = null;
  TreePrint myTreePrint = new TreePrint("oneline");
  String myInputFile = null;
  String myOutputFile = null;
  String myInputStatusFile = null;
  String myOutputStatusFile = null;
  public static final String QUIT_SERVER_COMMAND = "COMMAND=QUIT_SERVER";
  public static final String POS_COMMAND = "COMMAND=POS";
  public static final String PARSE_COMMAND = "COMMAND=PARSE";
  private static final String DEFAULT_PARSE_FILE_PATH = "chineseFactored.ser.gz";

  public ChineseParser(String parserFile)
  {
	if (parserFile == null || parserFile.length() == 0) {
		parserFile = DEFAULT_PARSE_FILE_PATH;
	}
    System.out.println("Loading Chinese parser from " + parserFile + "...");
    this.myParser = new LexicalizedParser(parserFile);
    System.out.println("Loading Chinese parser finish!");
    
    this.myParser.setOptionFlags(new String[] { "-maxLength", "40" });
  }
  
  public String beginParse(String inputStr)
  {
    String command = POS_COMMAND;
    // String command = PARSE_COMMAND;
    String parseResult = "";
    try {
    	if (command.equals(POS_COMMAND)) {
    	      parseResult = posFor(inputStr);
    	    } else {
    	      parseResult = parse(inputStr);
    	    }
    } catch (Exception e) {
    	throw e;
    }
    return parseResult;
  }
  
  public static void killServer()
  {
    System.out.println("Terminating Chinese parser server");
    System.exit(0);
  }
  
  public String posFor(String sentence)
  {
    return posFor(parseTreeFor(sentence));
  }
  
  public static String posFor(Tree tree)
  {
    StringBuffer sb = new StringBuffer();
    
    Iterator<Tree> it = tree.iterator();
    while (it.hasNext())
    {
      Tree node = (Tree)it.next();
      if (node.isPreTerminal())
      {
        Tree[] words = node.children();
        for (Tree word : words) {
          sb.append(word.label().value());
        }
        sb.append("/");
        
        sb.append(node.label().value());
        if (it.hasNext()) {
          sb.append(" ");
        }
      }
    }
    return sb.toString();
  }
  
  public String parse(String sentence)
  {
    return treeToString(parseTreeFor(sentence));
  }
  
  public Tree parseTreeFor(String sentence)
  {
    return this.myParser.apply(sentence);
  }
  
  public String treeToString(Tree tree)
  {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    this.myTreePrint.printTree(tree, pw);
    pw.flush();
    
    return sw.toString().trim();
  }
  
  public static void displayUsage()
  {
    System.out.println("Usage: java -jar ChineseParserServer.jar -p parserFile -i inputFile -o outputFile -is inputStatusFile -os outputStatusFile [-l maxSentenceLength]");
    System.out.println("To load the parser, you may need to specify more heap space memory for the JVM with the -Xmx tag");
    System.out.println("E.g. java -Xmx400m -jar ChineseParserServer.jar -p chineseFactored.ser.gz -i input.txt -o output.txt -is input.status -os output.status -l 40");
    System.out.println("To get pos data, write flag 'COMMAND=POS' to input status file (default).");
    System.out.println("To get parse data, write flag 'COMMAND=PARSE' to input status file.");
    System.out.println("To stop server, write flag 'COMMAND=QUIT_SERVER' to input status file.");
  }

}
