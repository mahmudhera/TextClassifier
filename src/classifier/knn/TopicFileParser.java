package classifier.knn;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Hera
 */
public class TopicFileParser extends FileParser {
    
    TopicFileParser(String fileName) {
        super(fileName);
    }
    
    @Override
    public ArrayList getDataList() {
        List<String> topics;
        try {
            topics = Files.readAllLines(super.file.toPath(),
                    StandardCharsets.UTF_8);
        } catch (IOException ex) {
            Logger.getLogger(TopicFileParser.class.getName()).log(Level.SEVERE, null, ex);
            topics = null;
        }
        return new ArrayList(topics);
    }
    
}
