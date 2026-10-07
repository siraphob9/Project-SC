import java.awt.image.BufferedImage;
import java.util.List;

// Owner: คนที่ 4 (Gameplay & Photo)
// เก็บจุดต่าง "เต็มชุด" ของคู่ภาพนี้ ไม่ผูกกับความยากใดความยากหนึ่ง
// ความยากจะมาเลือก subset ของ allSpots เองตอนสร้าง GameSession
public class PhotoPair {

    private BufferedImage left;
    private BufferedImage right;
    private List<DifferenceSpot> allSpots; // จุดต่างทั้งหมดที่เตรียมไว้ในภาพนี้ เช่น 8 จุด

    public PhotoPair(BufferedImage left, BufferedImage right, List<DifferenceSpot> allSpots) {
        // TODO
    }

    public static PhotoPair loadFromFolder(String folderPath) {
        return null; // TODO: อ่าน left.png, right.png, spots.txt (เต็มชุด) จากโฟลเดอร์
    }

    public BufferedImage getLeft() {
        return null; // TODO
    }

    public BufferedImage getRight() {
        return null; // TODO
    }

    public List<DifferenceSpot> getAllSpots() {
        return null; // TODO
    }
}
