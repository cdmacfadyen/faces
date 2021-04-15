import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import processing.core.PVector;

public class FaceParser {
    // ArrayList<PVector> 
    // so we want everything to be in 
    // the order it came in here
    // so that we can index it using the indexes from mesh.csv
    protected float colourOffsetWeight;
    protected float pointOffsetWeight;
    protected ArrayList<PVector> colourOffsets;
    protected ArrayList<PVector> pointOffsets;

    public FaceParser() {
        colourOffsets = new ArrayList<>();
        pointOffsets = new ArrayList<>();
    }

    public void loadFace(String dataDir, String index) {
        System.out.println(dataDir);
        System.out.println(index);

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
        