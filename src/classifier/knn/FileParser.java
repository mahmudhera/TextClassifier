package classifier.knn;

import java.io.File;
import java.util.ArrayList;

/**
 *
 * @author Hera
 */
public abstract class FileParser {
    
    File file;
    
    FileParser(File file) {
        this.file = file;
    }
    
    FileParser(String fileName) {
        this.file = new File(fileName);
    }
    
    public abstract ArrayList getDataList();
    
}
