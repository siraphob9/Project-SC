// Owner: คนที่ 3 (Menu & Game Logic)
// เปลี่ยนจาก "จำนวนภาพ" เป็น "จำนวนจุดต่างที่ต้องหาให้เจอ" ในคู่ภาพเดียวที่สุ่มมา
public enum Difficulty {
    EASY,
    MEDIUM,
    HARD;

    // TODO: กำหนดจำนวนจุดและเวลาของแต่ละระดับ เช่น
    // EASY(3, 60), MEDIUM(5, 90), HARD(8, 120);

    public int getSpotCount() {
        return 0; // TODO: จำนวนจุดต่างที่ต้องหาในรอบนี้
    }

    public int getSeconds() {
        return 0; // TODO
    }
}
