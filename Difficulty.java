// Owner: คนที่ 3 (Menu & Game Logic)
public enum Difficulty {
    EASY(5, 60),    // Easy: หา 5 จุด, เวลา 60 วินาที
    NORMAL(7, 90),  // Normal: หา 7 จุด, เวลา 90 วินาที
    HARD(9, 120);   // Hard: หา 9 จุด, เวลา 120 วินาที

    private final int spotCount;
    private final int seconds;

    // Constructor รับค่าจาก Enum ด้านบน
    Difficulty(int spotCount, int seconds) {
        this.spotCount = spotCount;
        this.seconds = seconds;
    }

    public int getSpotCount() {
        return spotCount;
    }

    public int getSeconds() {
        return seconds;
    }
}