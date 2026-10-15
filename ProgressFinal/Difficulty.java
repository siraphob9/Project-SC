// Owner: คนที่ 3 (Menu & Game Logic)
public enum Difficulty { // สร้างชนิดข้อมูลชื่อ Difficulty    
    EASY(3, 60),    // Easy: หา 3 จุด, เวลา 60 วินาที
    NORMAL(5, 90),  // Normal: หา 5 จุด, เวลา 90 วินาที
    HARD(7, 120);   // Hard: หา 7 จุด, เวลา 120 วินาที

    private final int spotCount;    // ประกาศตัวแปรเเก็บค่า spotCount แบบซ่อนและแก้ไขไม่ได้ด้วย private final 
    private final int seconds;      // ประกาศตัวแปรเก็บค่า seconds แบบซ่อนและแก้ไขไม่ได้ด้วย private final

    // Constructor รับค่าจาก Enum ด้านบน
    Difficulty(int spotCount, int seconds) {
        this.spotCount = spotCount; // เก็บค่า spotCount ในตัวนี้
        this.seconds = seconds; // เก็บค่า seconds ในตัวนี้
    }

    public int getSpotCount() { // สร้างตัวแปรมาให้ไฟล์อื่นดึงไปใช้
        return spotCount;
    }

    public int getSeconds() { // สร้างตัวแปรมาให้ไฟล์อื่นดึงไปใช้
        return seconds;
    }
}