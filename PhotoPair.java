import java.awt.image.BufferedImage;
import java.util.List;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;



// Owner: นายสิรภพ โตสุวรรณ์ (Gameplay & Photo)
// เก็บจุดต่าง "เต็มชุด" ของคู่ภาพนี้ ไม่ผูกกับความยากใดความยากหนึ่ง
// ความยากจะมาเลือก subset ของ allSpots เองตอนสร้าง GameSession
public class PhotoPair {

    private BufferedImage left;
    private BufferedImage right;
    private List<DifferenceSpot> allSpots; // จุดต่างทั้งหมดที่เตรียมไว้ในภาพนี้ เช่น 8 จุด

    public PhotoPair(BufferedImage left, BufferedImage right, List<DifferenceSpot> allSpots){
        this.left = left;
        this.right = right;
        this.allSpots = allSpots;
    }

    public static  PhotoPair loadFromFolder(String folderPath){
        try{
            File leftFile = new File(folderPath, "image1.png");
            File rightFile = new File(folderPath, "image2.png");
            BufferedImage imgLeft = ImageIO.read(leftFile);
            BufferedImage imgRight = ImageIO.read(rightFile);

            List<DifferenceSpot> spots = new ArrayList<>();
            File spotsFile = new File(folderPath, "spots.csv");

            if(spotsFile.exists()){
                BufferedReader reader = new BufferedReader(new FileReader(spotsFile));
                String line;
                while ((line = reader.readLine()) != null) {
                    if(line.trim().isEmpty()) continue;

                    String[] parts = line.split(",");
                    if(parts.length >= 3){
                        int x = Integer.parseInt(parts[0].trim());
                        int y = Integer.parseInt(parts[1].trim());
                        int radius = Integer.parseInt(parts[2].trim());
                        spots.add(new DifferenceSpot(x, y, radius));
                    }
                }
                reader.close();
            }
            return new PhotoPair(imgLeft, imgRight, spots);
        }
        catch(IOException | NumberFormatException e){
            System.err.println("Error loading PhotPair from folder: " + folderPath);
            e.printStackTrace();
            return null;
        }
        
    }

    

    public BufferedImage getLeft() {
        return left; // TODO
    }

    public BufferedImage getRight() {
        return right; // TODO
    }

    public List<DifferenceSpot> getAllSpots() {
        return allSpots; // TODO
    }

}