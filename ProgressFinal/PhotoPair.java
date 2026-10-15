import java.awt.image.BufferedImage; // BufferedImage = ชนิดข้อมูลที่เก็บรูปไว้ในหน่วย ความจำ
import java.util.List; //List = รายการของสิ่งของหลายชิ้น
import javax.imageio.ImageIO; // ImageIO = เครื่องมืออ่านไฟล์รูป (เช่น .png)
import java.io.File;// File = แทนไฟล์/โฟลเดอร์บนเครื่อง
import java.io.FileReader;// FileReader = ตัวเปิดไฟล์ข้อความเพื่ออ่าน
import java.io.BufferedReader;// BufferedReader = ตัวอ่านไฟล์ทีละบรรทัด
import java.io.IOException;// IOException = error ที่เกิดตอนอ่าน/เขียนไฟล์
import java.util.ArrayList;// ArrayList = ลิสต์ชนิดหนึ่งที่เพิ่มของเข้าไปได้เรื่อยๆ



// Owner: นายสิรภพ โตสุวรรณ์ (Gameplay & Photo)
// เก็บจุดต่าง "เต็มชุด" ของคู่ภาพนี้ ไม่ผูกกับความยากใดความยากหนึ่ง
// ความยากจะมาเลือก subset ของ allSpots เองตอนสร้าง GameSession
public class PhotoPair {// คลาส PhotoPair = "คู่รูป 1 ชุด" (รูปซ้าย + รูปขวา + จุดต่างทั้งหมดของคู่นี้)

    private BufferedImage left; //รูปซ้าย
    private BufferedImage right; //รูปขวา    
    private List<DifferenceSpot> allSpots; // จุดต่างทั้งหมดที่เตรียมไว้ในภาพนี้ เช่น 8 จุด

    public PhotoPair(BufferedImage left, BufferedImage right, List<DifferenceSpot> allSpots){ // constructor: รับรูปซ้าย รูปขวา และรายการจุดต่าง มาเก็บ
        this.left = left; // เก็บรูปซ้าย
        this.right = right; // เก็บรูปขวา
        this.allSpots = allSpots; // เก็บรายการจุดต่าง
    }
     // loadFromFolder = โหลดคู่รูปจากโฟลเดอร์ (static = เรียกด้วย PhotoPair.loadFromFolder(...) ได้เลย ไม่ต้อง new)
    public static  PhotoPair loadFromFolder(String folderPath){
        try{ // ลองทำตามนี้ ถ้ามีอะไรพังให้กระโดดไปที่ catch ด้านล่าง
            File leftFile = new File(folderPath, "image1.png");  //ชี้ไปที่ไฟล์รูปซ้าย (ในโฟลเดอร์นั้น)
            File rightFile = new File(folderPath, "image2.png"); // ชี้ไปที่ไฟล์รูปขวา
            BufferedImage imgLeft = ImageIO.read(leftFile); // อ่านไฟล์รูปซ้ายเข้ามาเป็นรูปในโปรแกรม
            BufferedImage imgRight = ImageIO.read(rightFile);  // อ่านไฟล์รูปขวา

            List<DifferenceSpot> spots = new ArrayList<>();  // เตรียมลิสต์เปล่าไว้เก็บจุดต่าง
            File spotsFile = new File(folderPath, "spots.csv"); // ชี้ไปที่ไฟล์พิกัดจุดต่าง

            if(spotsFile.exists()){ // ถ้ามีไฟล์ spots.csv อยู่จริงถึงจะอ่าน
                BufferedReader reader = new BufferedReader(new FileReader(spotsFile)); // เปิดไฟล์เพื่ออ่านทีละบรรทัด
                String line; // ตัวแปรเก็บข้อความของบรรทัดที่อ่านอยู่
                while ((line = reader.readLine()) != null) { // อ่านทีละบรรทัดจนหมดไฟล์ (หมดไฟล์จะได้ null)
                    if(line.trim().isEmpty()) continue; // ถ้าบรรทัดว่าง (ตัดช่องว่างแล้วไม่เหลืออะไร) ให้ข้ามไปบรรทัดถัดไป

                    String[] parts = line.split(","); // ตัดบรรทัดตามเครื่องหมายจุลภาค เช่น "79,57,55" -> ["79","57","55"]
                    if(parts.length >= 3){ // ต้องมีอย่างน้อย 3 ท่อน (x, y, radius) ถึงจะใช้ได้
                        int x = Integer.parseInt(parts[0].trim()); // แปลงท่อนแรกจากข้อความเป็นตัวเลข = x
                        int y = Integer.parseInt(parts[1].trim()); // ท่อนที่สอง = y
                        int radius = Integer.parseInt(parts[2].trim()); // ท่อนที่สาม = รัศมี
                        spots.add(new DifferenceSpot(x, y, radius)); // สร้างจุดต่างแล้วใส่เข้าลิสต์
                    }
                }
                reader.close(); // อ่านเสร็จแล้วปิดไฟล์
            }
            return new PhotoPair(imgLeft, imgRight, spots); // ประกอบเป็นคู่รูปแล้วส่งกลับ
        }
        catch(IOException | NumberFormatException e){ // ถ้าไฟล์หาย/อ่านไม่ได้ หรือมีข้อความที่แปลงเป็นเลขไม่ได้ (เช่นบรรทัดคอมเมนต์ #)
            System.err.println("Error loading PhotPair from folder: " + folderPath); // พิมพ์ข้อความ error บอกว่าโฟลเดอร์ไหนมีปัญหา
            e.printStackTrace(); // พิมพ์รายละเอียด error ลงคอนโซลไว้ให้ไล่หาสาเหตุ
            return null; // คืนค่าว่าง (null) = โหลดไม่สำเร็จ ผู้เรียกต้องเช็คเอง
        }
        
    }

    
    // getter: ให้ไฟล์อื่นหยิบรูปและจุดต่างไปใช้
    public BufferedImage getLeft() {
        return left; // ส่งรูปซ้ายกลับไป
    }

    public BufferedImage getRight() {
        return right; // ส่งรูปขวากลับไป
    }

    public List<DifferenceSpot> getAllSpots() {
        return allSpots; // ส่งจุดต่างกลับไป
    }

}