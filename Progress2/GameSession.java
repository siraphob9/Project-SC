import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Owner: คนที่ 3 (Menu & Game Logic)
public class GameSession {

    private User user;
    private Difficulty difficulty;
    private PhotoPair pair;              
    private List<DifferenceSpot> activeSpots; 
    private int foundCount;
    private int misses;
    private int timeRemaining;

    public GameSession(User user, Difficulty difficulty) {
        this.user = user;
        this.difficulty = difficulty;
        this.foundCount = 0;
        this.misses = 0;
        this.timeRemaining = difficulty.getSeconds();

        // 1) สุ่มหยิบ 1 คู่จากคลังภาพ (รอเมธอดจริงจากคนที่ 4)
        // สมมติคนที่ 4 สร้างเมธอด loadRandomFromPool() ไว้ให้
        String diffFolder = difficulty.name().toLowerCase();
        this.pair = PhotoPair.loadFromFolder("photos/" + diffFolder + "/pair01/"); 
        
        // 2) ดึงจุดทั้งหมดมาสุ่มเลือกตามจำนวนความยาก
        List<DifferenceSpot> allSpots = pair.getAllSpots();
        Collections.shuffle(allSpots); // สลับตำแหน่งจุดทั้งหมด
        
        // ตัดเอาเฉพาะจำนวนที่ต้องการตาม Difficulty
        int countToPick = Math.min(difficulty.getSpotCount(), allSpots.size());
        this.activeSpots = new ArrayList<>(allSpots.subList(0, countToPick));
    }

    public PhotoPair getPair() {
        return pair;
    }

    public List<DifferenceSpot> getActiveSpots() {
        return activeSpots;
    }

    // ตรวจสอบเมื่อมีการคลิก (คนที่ 4 จะเรียกฟังก์ชันนี้จาก GameFrame)
    public boolean handleClick(int x, int y) {
        // วนหาว่าคลิกโดนจุดไหนใน activeSpots ไหม
        for (DifferenceSpot spot : activeSpots) {
            if (spot.contains(x, y)) {
                if (!spot.isFound()) { // ถ้ายังไม่เคยหาเจอ
                    spot.setFound(true);
                    foundCount++;
                    return true; // คลิกโดนและถูกต้อง
                }
                return false; // คลิกโดนจุดที่เคยเจอแล้ว
            }
        }
        // ถ้าวนจบแล้วไม่โดนจุดไหนเลย แสดงว่าพลาด
        misses++;
        return false; 
    }

    public void tick() {
        if (timeRemaining > 0) {
            timeRemaining--;
        }
    }

    public boolean isRoundComplete() {
        // เช็คว่าหาเจอครบตามจำนวนที่ตั้งไว้หรือยัง
        return foundCount >= difficulty.getSpotCount(); 
    }

    public int getFoundCount() { return foundCount; }
    public int getMisses() { return misses; }
    public int getTimeRemaining() { return timeRemaining; }

    public User getUser(){ return  user; }

    public GameResult toResult(){
        int timeUsed = difficulty.getSeconds() - timeRemaining;
        int score = Math.max(0, foundCount * 100 - misses * 10 + timeRemaining);
        return new GameResult(user.getUsername(), difficulty, score, timeUsed);
    }
}
