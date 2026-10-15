import java.util.ArrayList; // ArrayList = ลิสต์ที่เพิ่มของเข้าไปได้เรื่อยๆ
import java.util.List; // List = รายการของสิ่งของหลายชิ้น

import java.io.*; // เครื่องมืออ่าน/เขียนไฟล์ (BufferedReader, BufferedWriter, IOException ...)
import java.nio.file.*; // เครื่องมือจัดการไฟล์แบบใหม่ (Files, Path, Paths, StandardOpenOption)

// คลาส CSVManager = "คนดูแลไฟล์ข้อมูล" อ่าน/เขียนผู้ใช้และคะแนนลงไฟล์ .csv
// ไฟล์ CSV คือไฟล์ข้อความธรรมดา ข้อมูลแต่ละช่องคั่นด้วยจุลภาค (,)
public class CSVManager {
    private final Path userFile = Paths.get("data", "users.csv"); // ที่อยู่ไฟล์ผู้ใช้: data/users.csv (final = ตั้งแล้วเปลี่ยนไม่ได้)
    private final Path scoresFile = Paths.get("data", "scores.csv");// ที่อยู่ไฟล์คะแนน: data/scores.csv

    public CSVManager() { // constructor: ทำงานตอน new CSVManager() เพื่อเตรียมโฟลเดอร์และไฟล์ให้พร้อมใช้
        try { // ลองทำ ถ้าสร้างไฟล์ไม่ได้ให้ไปที่ catch
            Files.createDirectories(Paths.get("data")); // สร้างโฟลเดอร์ data (ถ้ามีอยู่แล้วก็ไม่เป็นไร)
            if (!Files.exists(userFile)) // ถ้ายังไม่มีไฟล์ผู้ใช้
                Files.createFile(userFile);  // ให้สร้างไฟล์ว่างขึ้นมา
            if (!Files.exists(scoresFile)) // ถ้ายังไม่มีไฟล์คะแนน
                Files.createFile(scoresFile); // ให้สร้างไฟล์ว่างขึ้นมา
        } catch (IOException e) { // ถ้าสร้างโฟลเดอร์/ไฟล์ไม่สำเร็จ
            throw new RuntimeException("สร้างไฟล์ไม่สำเร็จ", e); // โยน error ขึ้นไปให้ผู้เรียกจัดการ
        }
    }

    public List<User> readUsers() { // readUsers = อ่านผู้ใช้ทั้งหมดจากไฟล์ ส่งกลับเป็นลิสต์ของ User
        List<User> users = new ArrayList<>(); // เตรียมลิสต์เปล่าไว้เก็บผู้ใช้
        try (BufferedReader r = Files.newBufferedReader(userFile)) { // เปิดไฟล์เพื่ออ่าน (วงเล็บหลัง try ทำให้ปิดไฟล์ให้เองอัตโนมัติ)
            String line; // ตัวแปรเก็บข้อความของแต่ละบรรทัด
            while ((line = r.readLine()) != null) { // อ่านทีละบรรทัดจนหมดไฟล์
                if (line.isBlank()) // ถ้าบรรทัดนี้ว่างเปล่า
                    continue; // ข้ามไปบรรทัดถัดไป
                String[] parts = line.split(",", 2); // ตัดเป็นสองท่อนเท่านั้น (ชื่อ , รหัส) ถ้ารหัสมีจุลภาคก็ไม่แตกเพิ่ม
                if (parts.length == 2) { // ถ้าได้ครบสองท่อน
                    users.add(new User(parts[0], parts[1])); // สร้าง User จากชื่อกับรหัส แล้วใส่ลิสต์
                }
            }
        } catch (IOException e) { // ถ้าอ่านไฟล์ไม่ได้
            throw new RuntimeException("cant read user", e); // โยน error ขึ้นไป
        }
        return users; // ส่งลิสต์ผู้ใช้ทั้งหมดกลับไป
    }

    public void writeUser(User u) { // writeUser = เพิ่มผู้ใช้ใหม่ 1 คนต่อท้ายไฟล์
        try (BufferedWriter w = Files.newBufferedWriter(userFile, StandardOpenOption.CREATE, // เปิดไฟล์ผู้ใช้เพื่อเขียนต่อท้าย (ตัวเลือกอยู่บรรทัดถัดไป)
                StandardOpenOption.APPEND)) {  // เปิดไฟล์เพื่อเขียน: CREATE = ถ้าไม่มีให้สร้าง, APPEND = เขียน "ต่อท้าย" ไม่ทับของเดิม
            w.write(u.getUsername() + "," + u.getPassword()); // เขียนบรรทัดรูปแบบ ชื่อ,รหัส
            w.newLine(); // ขึ้นบรรทัดใหม่ 
        } catch (IOException e) { // ขึ้นบรรทัดใหม่
            throw new RuntimeException("Cant Create files", e); // โยน error ขึ้นไป
        }
    }
   

    public List<GameResult> readScores(Difficulty d) { // readScores = อ่านคะแนนทั้งหมดของ "ระดับ d" แล้วเรียงจากมากไปน้อย
        List<GameResult> results = new ArrayList<>(); // ลิสต์เปล่าไว้เก็บผลที่ตรงระดับ
        try (BufferedReader r = Files.newBufferedReader(scoresFile)) { // เปิดไฟล์คะแนนเพื่ออ่าน
            String line; // เก็บข้อความแต่ละบรรทัด
            while ((line = r.readLine()) != null) { // อ่านทีละบรรทัดจนหมดไฟล์
                if (line.isBlank()) // บรรทัดว่าง
                    continue; // ข้าม
                String[] p = line.split(","); // ตัดเป็นท่อน: [ชื่อ, ระดับ, คะแนน, เวลา]
                if (p.length < 4)  // ถ้าท่อนไม่ครบ 4 ถือว่าบรรทัดเสีย
                    continue; // ข้าม
                Difficulty diff = Difficulty.valueOf(p[1]); // แปลงข้อความ "EASY" กลับเป็นตัวเลือก Difficulty.EASY (ถ้าสะกดผิดจะ error)
                if (diff != d) // ถ้าไม่ใช่ระดับที่ต้องการ
                    continue; // ข้าม
                results.add(new GameResult(p[0], diff, Integer.parseInt(p[2]), Integer.parseInt(p[3])));  // สร้างผลเกมจากข้อมูลในบรรทัด (แปลงคะแนนและเวลาจากข้อความเป็นตัวเลข) แล้วใส่ลิสต์
            }
        } catch (IOException e) { // ถ้าอ่านไฟล์ไม่ได้
            throw new RuntimeException("cant read score", e); // โยน error ขึ้นไป
        }
        results.sort((a, b) -> b.getScore() - a.getScore()); // เรียงตามคะแนนจากมากไปน้อย (b ลบ a = ตัวคะแนนมากกว่าอยู่ก่อน)
        return results; // ส่งผลที่เรียงแล้วกลับไป

    }

    public void writeScore(GameResult r) { // writeScore = บันทึกผลเกม 1 รอบต่อท้ายไฟล์คะแนน
        if (r.getPlayerName() != null && r.getPlayerName().equals("Guest")) { // ถ้าชื่อผู้เล่นคือ Guest (เล่นแบบแขก)
            return;  // จบเมธอดทันที = ไม่บันทึกคะแนน
        }
        try (BufferedWriter w = Files.newBufferedWriter(scoresFile, StandardOpenOption.CREATE, // เปิดไฟล์คะแนนเพื่อเขียนต่อท้าย (ตัวเลือกอยู่บรรทัดถัดไป)
                StandardOpenOption.APPEND)) { // เปิดไฟล์คะแนนเพื่อเขียนต่อท้าย
            w.write(r.getPlayerName() + "," + r.getDifficulty().name() + "," + r.getScore() + "," + r.getTimeUsed());  // เขียนบรรทัดรูปแบบ ชื่อ,ระดับ,คะแนน,เวลา เช่น ploy,EASY,450,92 (name() = ชื่อตัวเลือกเป็นข้อความ)
            w.newLine(); // ขึ้นบรรทัดใหม่
        } catch (IOException e) { // ถ้าเขียนไม่สำเร็จ
            throw new RuntimeException("Cant save score", e); // โยน error ขึ้นไป 
        }
        
    }
    // TestCSV = คลาสไว้ทดสอบ CSVManager เดี่ยวๆ ไม่มีผลต่อตัวเกม
    public class TestCSV {
        public static void main(String[] args) { // จุดเริ่มรันของการทดสอบ
            CSVManager csv = new CSVManager(); // สร้างตัวจัดการไฟล์
            csv.writeUser(new User("ploy", "1234")); // ลองเพิ่มผู้ใช้ ploy
            System.out.println(csv.readUsers().size()); // ควรเพิ่มขึ้น 1 ทุกครั้งที่รัน  (พิมพ์จำนวนผู้ใช้ทั้งหมด)

            csv.writeScore(new GameResult("ploy", Difficulty.EASY, 450, 92)); // ลองบันทึกคะแนนตัวอย่าง
            for (GameResult r : csv.readScores(Difficulty.EASY)) { // วนอ่านคะแนนระดับ EASY ทีละรายการ
                System.out.println(r.getPlayerName() + " : " + r.getScore()); // พิมพ์ ชื่อ : คะแนน
            }
        }
    }

}
