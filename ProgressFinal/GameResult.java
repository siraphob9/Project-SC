public class GameResult { // ประกาศสร้างคลาสชื่อ GameResult เพื่อใช้เก็บข้อมูลผลการเล่น

    private String playerName; // สร้างตัวแปรเก็บชื่อผู้เล่น (เป็นข้อความ)
    private Difficulty difficulty; // สร้างตัวแปรเก็บระดับความยากของเกม (ดึงมาจากคลาส Difficulty)
    private int score; // สร้างตัวแปรเก็บคะแนน (เป็นตัวเลขจำนวนเต็ม)
    private int timeUsed; // สร้างตัวแปรเก็บเวลาที่ใช้เล่นไป (เป็นตัวเลขจำนวนเต็ม)

    // Constructor: ฟังก์ชันนี้จะถูกเรียกใช้ตอนที่เราต้องการสร้าง "กล่องเก็บข้อมูล" ใบใหม่
    public GameResult(String playerName, Difficulty difficulty, int score, int timeUsed) {
        this.playerName = playerName; // เอาชื่อที่ส่งเข้ามา ไปเก็บในตัวแปร playerName ของคลาสนี้
        this.difficulty = difficulty; // เอาระดับความยากที่ส่งเข้ามา ไปเก็บในตัวแปร difficulty
        this.score = score; // เอาคะแนนที่ส่งเข้ามา ไปเก็บในตัวแปร score
        this.timeUsed = timeUsed; // เอาเวลาที่ส่งเข้ามา ไปเก็บในตัวแปร timeUsed
    }

    // ด้านล่างนี้คือ Getter Methods เอาไว้สำหรับดึงข้อมูลแต่ละอย่างออกไปใช้งาน
    public String getPlayerName() { // ฟังก์ชันสำหรับขออ่านชื่อผู้เล่น
        return playerName; // ส่งค่าชื่อผู้เล่นกลับไป
    }

    public Difficulty getDifficulty() { // ฟังก์ชันสำหรับขออ่านระดับความยาก
        return difficulty; // ส่งค่าระดับความยากกลับไป
    }

    public int getScore() { // ฟังก์ชันสำหรับขออ่านคะแนน
        return score; // ส่งค่าคะแนนกลับไป
    }

    public int getTimeUsed() { // ฟังก์ชันสำหรับขออ่านเวลาที่ใช้
        return timeUsed; // ส่งค่าเวลากลับไป
    }
}