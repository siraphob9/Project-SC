// Owner: นายวชิรวิทย์ กตกุลบัญชร (Authentication) // คอมเมนต์บอกชื่อคนเขียนโค้ด
public class User { // สร้างคลาสชื่อ User

    private String username; // ตัวแปรเก็บชื่อผู้ใช้งาน
    private String password; // ตัวแปรเก็บรหัสผ่าน
    private boolean isGuest; // ตัวแปรเก็บสถานะว่าเป็น Guest (แขกที่ไม่ได้สมัครสมาชิก) หรือไม่ (ค่าเป็น true/false)

    // Constructor แบบที่ 1: สำหรับผู้ใช้ทั่วไปที่สมัครสมาชิกแล้ว
    public User(String username, String password) { 
        this.username = username; // ตั้งค่าชื่อผู้ใช้
        this.password = password; // ตั้งค่ารหัสผ่าน
        this.isGuest = false; // กำหนดว่าไม่ใช่ Guest เพราะมีชื่อกับรหัสแล้ว
    }
    
    // Constructor แบบที่ 2: สำหรับผู้ใช้ที่กดปุ่มเล่นแบบ Guest
    public User(boolean isGuest) { 
        this.username = "Guest"; // ตั้งชื่อให้เลยอัตโนมัติว่า "Guest"
        this.password = ""; // รหัสผ่านปล่อยว่างไว้
        this.isGuest = isGuest; // ตั้งสถานะ Guest ตามที่ส่งมา (ปกติจะเป็น true)
    }
    
    // Getter Methods ไว้ดึงข้อมูลไปใช้
    public String getUsername() { // ดึงชื่อผู้ใช้
        return username; // ส่งค่าชื่อผู้ใช้กลับไป
    }

    public String getPassword() { // ดึงรหัสผ่าน
        return password; // ส่งค่ารหัสผ่านกลับไป
    }
    
    public boolean isGuest(){ // เช็คว่าเป็น Guest ไหม
        return isGuest; // ส่งสถานะจริง/เท็จกลับไป
    }
}