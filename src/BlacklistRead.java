import java.util.ArrayList;
import java.util.Set;

public class BlacklistRead extends FileRead{
    public Set<String> blacklist;    
    public BlacklistRead(String file, Set<String> blacklist) {
        super(file);
        this.blacklist = blacklist;
    }
    
    @Override
     public void processLine(String line){        
           this.blacklist.add(line);
    }
    
}
