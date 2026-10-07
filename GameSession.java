import java.util.List;

// Owner: คนที่ 3 (Menu & Game Logic)
public class GameSession {

    private User user;
    private Difficulty difficulty;
    private PhotoPair pair;              // คู่ภาพเดียวที่สุ่มมาจากคลังรวม
    private List<DifferenceSpot> activeSpots; // จุดย่อยที่เลือกมาให้หารอบนี้ (ขนาด = difficulty.getSpotCount())
    private int foundCount;
    private int misses;
    private int timeRemaining;

    public GameSession(User user, Difficulty difficulty) {
        // TODO:
        // 1) pair = สุ่มหยิบ 1 คู่จากคลังภาพรวม (เช่น โฟลเดอร์ images/pool/*)
        // 2) activeSpots = สุ่มเลือกจำนวน difficulty.getSpotCount() จาก pair.getAllSpots()
        // 3) timeRemaining = difficulty.getSeconds()
    }

    public PhotoPair getPair() {
        return null; // TODO
    }

    public List<DifferenceSpot> getActiveSpots() {
        return null; // TODO
    }

    public boolean handleClick(int x, int y) {
        return false; // TODO: เช็กเฉพาะใน activeSpots เท่านั้น (จุดอื่นในภาพที่ไม่ได้ถูกเลือกไม่ต้องนับ)
    }

    public void tick() {
        // TODO: timeRemaining--
    }

    public boolean isRoundComplete() {
        return false; // TODO: ทุกจุดใน activeSpots ถูกหาเจอหมดหรือยัง
    }

    public int getFoundCount() {
        return foundCount; // TODO
    }

    public int getMisses() {
        return misses; // TODO
    }

    public int getTimeRemaining() {
        return timeRemaining; // TODO
    }
}
