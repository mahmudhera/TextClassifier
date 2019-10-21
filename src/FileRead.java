
import java.io.*;
import java.util.ArrayList;

public abstract class FileRead {
    String file = "";

    public FileRead(String file) {
        this.file = file;        
    }
    
    public void processFile(){
        processFile(null);
    }
    
    public void processFile(Integer lineCount){
        try {
            
            // Open the file
            FileInputStream fstream = new FileInputStream(file);
            
            // Get the object of DataInputStream
            DataInputStream in = new DataInputStream(fstream);
            
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            
            int cnt = 0;
            //read whole file
            if(lineCount == null){
                //Read File Line By Line
                while ((strLine = br.readLine()) != null) {
                    processLine(strLine);
                }
                
            }else{
                //Read File Line By Line                
                while ((strLine = br.readLine()) != null && cnt<lineCount) {
                    processLine(strLine);
                    cnt++;
                }
            }
            
            
            //Close the input stream
            in.close();
        } catch (Exception e) {//Catch exception if any
            e.printStackTrace();
            System.err.println("Error: " + e.getMessage());
        }
    }

    abstract void processLine(String line);
}
