import java.util.ArrayList;

public class TopicRead extends FileRead{
    public ArrayList<String> topics;    
    public TopicRead(String file, ArrayList<String> topics) {
        super(file);
        this.topics = topics;
    }
    
    @Override
     public void processLine(String line){        
           this.topics.add(line);
    }
    
}
