import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import processing.core.PVector;

/**
 * The FaceParser class basically just loads in 
 * all of the data from the csv files as is 
 * and holds them to be combined with the average 
 * face. 
 */
public class FaceParser {
    protected float colourOffsetWeight; // from tx_ev
    protected float pointOffsetWeight;  // from sh_ev
    protected ArrayList<PVector> colourOffsets; // from tx_xxx
    protected ArrayList<PVector> pointOffsets;  // from sh_xxx

    public FaceParser() {
        colourOffsets = new ArrayList<>();
        pointOffsets = new ArrayList<>();
    }

    public void loadFace(String dataDir, String index) {
        String shPath  = dataDir + "/sh_" + index + ".csv";
        String txPath = dataDir + "/tx_" + index + ".csv";
        String txWeightPath = dataDir + "/tx_ev.csv"; 
        String shWeightPath = dataDir + "/sh_ev.csv"; 
        try {
            BufferedReader shReader = 
                new BufferedReader(new FileReader(shPath));
            String line = null;
            while((line = shReader.readLine()) != null) {
                String[] parts = line.split(",");   // always 3 long
                float xOffset = Float.parseFloat(parts[0]);
                float yOffset = Float.parseFloat(parts[1]);
                float zOffset = Float.parseFloat(parts[2]);
                PVector pointOffset = new PVector(xOffset, yOffset,zOffset);
                pointOffsets.add(pointOffset);
            }
            
            shReader.close();

            BufferedReader txReader = 
                new BufferedReader(new FileReader(txPath));
                
            line = null;
            while((line = txReader.readLine()) != null) {
                String[] parts = line.split(",");
                float red = Float.parseFloat(parts[0]);
                float green = Float.parseFloat(parts[1]);
                float blue = Float.parseFloat(parts[2]);

                PVector colourOffset = new PVector(red, green, blue);
                colourOffsets.add(colourOffset);
            }

            txReader.close();


            BufferedReader txWeightReader = 
                new BufferedReader(new FileReader(txWeightPath));

            line = null;
            int weightIndex = Integer.parseInt(index);
            int weightCounter = 0;

            while((line = txWeightReader.readLine()) != null)
            {
                if(weightCounter == weightIndex) {
                    colourOffsetWeight = Float.parseFloat(line);
                    break;
                }
                weightCounter++;
            }

            txWeightReader.close();

            BufferedReader shWeightReader = 
                new BufferedReader(new FileReader(shWeightPath));
            
                weightCounter = 0;
            while((line = shWeightReader.readLine()) != null)
            {
                if(weightCounter == weightIndex) {
                    pointOffsetWeight = Float.parseFloat(line);
                    break;

                }
                weightCounter++;
            }

            shWeightReader.close();
        } catch (FileNotFoundException e) {
            System.out.println(
                "Couldn't load data files, maybe the data directory isn't correct.");
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-geneated catch block
            e.printStackTrace();
        }      
    } 
}
        