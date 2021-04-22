import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * The average face is a special case of the 
 * general face because it doesn't have any weights  
 * to apply to the colour or coordinate offsets, and 
 * in this case I also use it to hold the mesh.
 */
public class AverageFace extends FaceParser {
    String index = "000";
    ArrayList<Triple> mesh;

    public AverageFace() {
        mesh = new ArrayList<>();
    }

    public void loadFace(String dataDir) {
        super.loadFace(dataDir, index);

        try {
            BufferedReader meshReader = 
                new BufferedReader(new FileReader(dataDir + "/mesh.csv"));
            String line = null;

            while((line = meshReader.readLine()) != null) {
                String[] parts = line.split(",");
                int first = Integer.parseInt(parts[0]) - 1;
                int second = Integer.parseInt(parts[1]) - 1;
                int third = Integer.parseInt(parts[2]) - 1;

                Triple triangle = new Triple(first, second, third);
                mesh.add(triangle);
            }  
            
            meshReader.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
