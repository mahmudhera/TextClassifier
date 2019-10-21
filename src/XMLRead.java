
import classifier.nb.Document;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XMLRead extends FileRead {

    public ArrayList<Document> documents;
    public Set<String> blacklist;
    public String topic;

    public XMLRead(String file, ArrayList<Document> documents, Set<String> blacklist, String topic) {
        super(file);
        this.documents = documents;
        this.blacklist = blacklist;
        this.topic = topic;
    }

    @Override
    public void processLine(String line) {
        String strb = null;
        String urlRegex = "\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
        if (line.trim().startsWith("<row")) {

            //extract body 
            try {
               strb = line.substring(line.indexOf("Body=\"") + "Body=\"".length());

                //strb = line.substring(line.indexOf("Body=\"&lt;p&gt;") + "Body=\"&lt;p&gt;".length(), line.indexOf("&lt;/p&gt;&#xA"));
                //strb = line.substring(line.indexOf("Body=\"") + "Body=\"".length(), line.indexOf(" OwnerUserId=\""));
            } catch (Exception e) {
            }

//            try {
//                strb = line.substring(line.indexOf("Body=\"") + "Body=\"".length(), line.indexOf(" OwnerUserId=\""));
//            } catch (Exception e) {
//            }

            if (strb == null) {
                return;
            }

            //remove some more 
            strb = strb.replaceAll("&#xA", " ");

            //remove tags <>
            strb = strb.replaceAll("((&lt;)(strong)(&gt;))", "");
            strb = strb.replaceAll("((&lt;)(/strong)(&gt;))", "");
            strb = strb.replaceAll("((&lt;)(blockquote)(&gt;))", "");
            strb = strb.replaceAll("((&lt;)(/blockquote)(&gt;))", "");
            strb = strb.replaceAll("&lt;img src=&quot;", "");
            strb = strb.replaceAll("alt=&quot;", "");
            strb = strb.replaceAll("rel=&quot;nofollow&quot;", "");
            strb = strb.replaceAll("((&lt;)[^&gt;]+(&gt;))", "");

            //remove alone <, or alone >
            strb = strb.replaceAll("((&lt;)|(&gt;)|(&quot;))", " ");

            //remove some more
            strb = strb.replaceAll("((a href=)|(/a&gt;))", " ");

            //remove url
            strb = strb.replaceAll(urlRegex, " ");

            //remove extra characters
            strb = strb.replaceAll("(@)|(;)|(:)|(\\()|(\\))|(\")|(\\?)|(\\!)|(\\{)|(\\})|(\\[)|(\\])", " ");

            //remove words. or words, words, 
            Pattern p = Pattern.compile("([\\s][\\w]+)((\\,)|(\\.))");
            Matcher m = p.matcher(strb);

            StringBuffer result = new StringBuffer();
            while (m.find()) {
                //System.out.println("Masking: " + m.group(2));
                m.appendReplacement(result, m.group(1));
                //System.out.println("***" + m.group(1));
            }
            m.appendTail(result);
            //System.out.println("***" + result);                
            strb = new String(result);

            strb = strb.replaceAll(",", " ");
            strb = strb.replaceAll("\\.", " ");
            strb = strb.replaceAll("=", " ");
            strb = strb.replaceAll("/", " ");
            strb = strb.replaceAll("'", " ");
            strb = strb.replaceAll("\"", " ");
            strb = strb.replaceAll("\\-", " ");

            //make lowercase
            strb = strb.toLowerCase();

            //remove pronoun, prepositions, conjunctions
            for (String blackwords : blacklist) {
                strb = strb.replace(" " + blackwords + " ", " ");
            }

            //separate the words
            String[] words = strb.split("(\\s+)");
            for (int i = 0; i < words.length; i++) {
                words[i] = words[i].trim();
                //words[i] = words[i].concat("########");
            }
            ArrayList<String> wordsList = new ArrayList<>(Arrays.asList(words));

            //remove pronoun, prepositions, conjunctions (again)
            for (String blackword : blacklist) {
                wordsList.remove((String) blackword);
            }

//            strb = "";
//            for (String w : wordsList) {
//                strb = strb + " ##### " + w;
//            }
//            System.out.println(strb);
            HashMap<String, Integer> wordCounts = new HashMap<String, Integer>();
            for (String w : wordsList) {
                Integer i = wordCounts.get(w);
                if (i == null) {
                    wordCounts.put(w, 1);
                } else {
                    wordCounts.put(w, i + 1);
                }
            }
            if (wordCounts.size() != 0) {
                this.documents.add(new Document(wordCounts, topic));
            }

        }

    }

}
