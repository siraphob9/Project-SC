import java.util.ArrayList;   // ArrayList = ลิสต์ที่เพิ่มของเข้าไปได้เรื่อยๆ
import java.util.Collections; // Collections = เครื่องมือจัดการลิสต์ เช่น สลับลำดับแบบสุ่ม (shuffle)
import java.util.List;        // List = รายการของสิ่งของหลายชิ้น
import java.io.File;          // File = แทนไฟล์/โฟลเดอร์บนเครื่อง
import java.util.Arrays;      // Arrays = เครื่องมือจัดการอาร์เรย์ (ใช้แปลงอาร์เรย์เป็นลิสต์)

// คลาส GameSession = "ผู้ควบคุมกติกา" ของเกม 1 รอบ จำว่าเล่นระดับไหน เจอกี่จุด พลาดกี่ครั้ง เหลือเวลาเท่าไหร่
public class GameSession {

    private User user;                        // ผู้เล่นที่กำลังเล่น
    private Difficulty difficulty;            // ระดับความยากที่เลือก
    private PhotoPair pair;                   // คู่รูปที่ใช้เล่นรอบนี้
    private List<DifferenceSpot> activeSpots; // จุดต่างที่ใช้จริงในรอบนี้ (เลือกมาจากทั้งหมดตามระดับ)
    private int foundCount;                   // เจอจุดต่างไปแล้วกี่จุด
    private int misses;                       // คลิกพลาดไปกี่ครั้ง
    private int timeRemaining;                // เวลาที่เหลือ (วินาที)

    // constructor: ทำงานตอน new GameSession(user, difficulty) เพื่อเตรียมเกมรอบใหม่
    public GameSession(User user, Difficulty difficulty) {
        this.user = user;                                // เก็บผู้เล่น
        this.difficulty = difficulty;                    // เก็บระดับความยาก
        this.foundCount = 0;                             // เริ่มต้นเจอ 0 จุด
        this.misses = 0;                                 // เริ่มต้นพลาด 0 ครั้ง
        this.timeRemaining = difficulty.getSeconds();    // เวลาเริ่มต้น = เวลาเต็มของระดับนั้น


        this.pair = loadRandomPair(difficulty);          // สุ่มหยิบคู่รูป 1 ชุดจากโฟลเดอร์ของระดับนี้
        
        // 2) ดึงจุดทั้งหมดมาสุ่มเลือกตามจำนวนความยาก
        List<DifferenceSpot> allSpots = pair.getAllSpots(); // ดึงจุดต่างทั้งหมดของคู่รูปนี้
        Collections.shuffle(allSpots); // สลับตำแหน่งจุดทั้งหมด
        
        // ตัดเอาเฉพาะจำนวนที่ต้องการตาม Difficulty
        // Math.min = เอาค่าที่น้อยกว่าระหว่าง "จำนวนที่ระดับนี้ต้องการ" กับ "จำนวนจุดที่รูปมีจริง"
        int countToPick = Math.min(difficulty.getSpotCount(), allSpots.size());
        // subList(0, countToPick) = หยิบจุดตั้งแต่ลำดับ 0 ถึงก่อน countToPick แล้วทำเป็นลิสต์ใหม่เก็บไว้ใน activeSpots
        this.activeSpots = new ArrayList<>(allSpots.subList(0, countToPick));
    }

    // loadRandomPair = เปิดโฟลเดอร์ของระดับนั้น แล้วสุ่มหยิบโฟลเดอร์คู่รูปมา 1 ชุด
    public PhotoPair loadRandomPair(Difficulty d){
        // ตั้งชื่อโฟลเดอร์ระดับ: ถ้า NORMAL ใช้ "normal" ถ้าอื่นใช้ชื่อตัวพิมพ์เล็ก (easy / hard)
        // ระวัง: ถ้าโฟลเดอร์ในเครื่องชื่อ medium แต่โค้ดหา normal จะหาไม่เจอ ต้องให้ชื่อตรงกัน
        String level =(d ==Difficulty.NORMAL)? "normal": d.name().toLowerCase();
        File levelDir = new File("photos/" + level); // ชี้ไปที่โฟลเดอร์ เช่น photos/hard
        File[]folders = levelDir.listFiles(File::isDirectory); // ขอรายการ "โฟลเดอร์ย่อย" ข้างใน (pair01, pair02, ...)
        if(folders == null || folders.length == 0){ // ถ้าหาโฟลเดอร์ไม่เจอ หรือข้างในไม่มีโฟลเดอร์ย่อยเลย
            throw new IllegalStateException("Not found Photo from this Folders "+ levelDir.getAbsolutePath()); // โยน error บอกที่อยู่เต็ม
        }

        List<File> candidates = new ArrayList<>(Arrays.asList(folders)); // แปลงอาร์เรย์โฟลเดอร์เป็นลิสต์
    Collections.shuffle(candidates); // สลับลำดับโฟลเดอร์แบบสุ่ม
    for(File folder : candidates){ // ลองทีละโฟลเดอร์ตามลำดับที่สลับแล้ว
    PhotoPair p = PhotoPair.loadFromFolder(folder.getPath()); // พยายามโหลดคู่รูปจากโฟลเดอร์นี้
    if(p != null){ // ถ้าโหลดสำเร็จ (ไม่ใช่ null)
        return p;  // ส่งคู่รูปนี้กลับเลย (จบเมธอด)
     }
    }
    // ถ้าวนจนหมดแล้วไม่มีโฟลเดอร์ไหนโหลดสำเร็จเลย
    throw new IllegalStateException("Loading Photo Failed please check folders "+ levelDir.getAbsolutePath());

 }
    // getter: ให้หน้าเล่นเกมหยิบคู่รูปไปแสดง
    public PhotoPair getPair(){
        return pair;    // ส่งคู่รูปปัจจุบันกลับไป
    }
    

    // getter: ให้หน้าเล่นเกมหยิบรายการจุดต่างที่ใช้จริงไปวาดวงกลม
    public List<DifferenceSpot> getActiveSpots() {
        return activeSpots; // ส่งรายการจุดต่างที่ใช้ในรอบนี้กลับไป
    }

    // ตรวจสอบเมื่อมีการคลิก (คนที่ 4 จะเรียกฟังก์ชันนี้จาก GameFrame)
    // รับพิกัดที่คลิก (พิกเซลของรูปต้นฉบับ) ส่งกลับ true ถ้าคลิกโดนจุดต่างใหม่ที่ยังไม่เคยเจอ
    public boolean handleClick(int x, int y) {
        // วนหาว่าคลิกโดนจุดไหนใน activeSpots ไหม
        for (DifferenceSpot spot : activeSpots) { // วนดูจุดต่างทีละจุด
            if (spot.contains(x, y)) { // ถ้าตำแหน่งที่คลิกอยู่ในวงกลมของจุดนี้
                if (!spot.isFound()) { // ถ้ายังไม่เคยหาเจอ
                    spot.setFound(true); // ทำเครื่องหมายว่าจุดนี้เจอแล้ว
                    foundCount++;        // เพิ่มจำนวนจุดที่เจอขึ้น 1
                    return true; // คลิกโดนและถูกต้อง
                }
                return false; // คลิกโดนจุดที่เคยเจอแล้ว (ไม่นับว่าพลาด)
            }
        }
        // ถ้าวนจบแล้วไม่โดนจุดไหนเลย แสดงว่าพลาด
        misses++;      // นับว่าพลาดเพิ่ม 1 ครั้ง
        return false;  // คืนค่า false = ไม่ได้จุดใหม่
    }

    // tick = ลดเวลาลง 1 วินาที (GameFrame เรียกทุกวินาที)
    public void tick() {
        if (timeRemaining > 0) { // ถ้ายังมีเวลาเหลือ
            timeRemaining--;     // ลดเวลาลง 1 (ไม่ให้ต่ำกว่า 0)
        }
    }

    // isRoundComplete = เจอครบแล้วหรือยัง (ถ้าครบ = เกมจบ)
    public boolean isRoundComplete() {
        // เช็คว่าหาเจอครบตามจำนวนที่ตั้งไว้หรือยัง
        // ระวัง: เทียบกับจำนวนของระดับ (difficulty.getSpotCount()) ถ้ารูปมีจุดน้อยกว่านั้น (เช่นรูปแคมป์ปิ้ง 6 จุดใน hard ที่ต้อง 7)
        // เกมจะไม่มีวันจบ ควรเทียบกับ activeSpots.size() แทน
        return foundCount >= difficulty.getSpotCount(); 
    }

    // getter ต่างๆ: ให้ GameFrame อ่านค่าไปแสดงบนหน้าจอ
    public int getFoundCount() { return foundCount; }         // จำนวนจุดที่เจอแล้ว
    public int getMisses() { return misses; }                 // จำนวนครั้งที่พลาด
    public int getTimeRemaining() { return timeRemaining; }   // เวลาที่เหลือ

    public User getUser(){ return  user; } // ผู้เล่นของเกมรอบนี้

    // toResult = สรุปเกมรอบนี้เป็น GameResult เพื่อส่งไปหน้าสรุปผลและบันทึกคะแนน
    public GameResult toResult(){
        int timeUsed = difficulty.getSeconds() - timeRemaining; // เวลาที่ใช้ = เวลาเต็ม - เวลาที่เหลือ
        // คะแนน = จุดที่เจอ x 100 - พลาด x 10 + เวลาที่เหลือ (Math.max(0, ...) ไม่ให้ติดลบ)
        int score = Math.max(0, foundCount * 100 - misses * 10 + timeRemaining);
        return new GameResult(user.getUsername(), difficulty, score, timeUsed); // สร้างผลเกมแล้วส่งกลับ
    }
}