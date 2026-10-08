import java.util.ArrayList;
import java.util.List;

import java.io.*;
import java.nio.file.*;


public class CSVManager {

    private final Path userFile = Paths.get("data", "users.csv");
    private final Path scoresFile = Paths.get("data", "scores.csv");

    public CSVManager() {
        try {
            Files.createDirectories(Paths.get("data"));
            if (!Files.exists(userFile))
                Files.createFile(userFile);
            if (!Files.exists(scoresFile))
                Files.createFile(scoresFile);
        } catch (IOException e) {
            throw new RuntimeException("สร้างไฟล์ไม่สำเร็จ", e);
        }
    }

    public List<User> readUsers() {
        List<User> users = new ArrayList<>();
        try (BufferedReader r = Files.newBufferedReader(userFile)) {
            String line;
            while ((line = r.readLine()) != null) {
                if (line.isBlank())
                    continue;
                String[] parts = line.split(",", 2);
                if (parts.length == 2) {
                    users.add(new User(parts[0], parts[1]));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("cant read user", e);
        }
        return users;
    }

    public void writeUser(User u) {
        try (BufferedWriter w = Files.newBufferedWriter(userFile, StandardOpenOption.CREATE,
                StandardOpenOption.APPEND)) {
            w.write(u.getUsername() + "," + u.getPassword());
            w.newLine();
        } catch (IOException e) {
            throw new RuntimeException("Cant Create files", e);
        }
    }

    public List<GameResult> readScores(Difficulty d) {
        List<GameResult> results = new ArrayList<>();
        try (BufferedReader r = Files.newBufferedReader(scoresFile)) {
            String line;
            while ((line = r.readLine()) != null) {
                if (line.isBlank())
                    continue;
                String[] p = line.split(",");
                if (p.length < 4)
                    continue;
                Difficulty diff = Difficulty.valueOf(p[1]);
                if (diff != d)
                    continue;
                results.add(new GameResult(p[0], diff, Integer.parseInt(p[2]), Integer.parseInt(p[3])));
            }
        } catch (IOException e) {
            throw new RuntimeException("cant read score", e);
        }
        results.sort((a, b) -> b.getScore() - a.getScore());
        return results;

    }

    public void writeScore(GameResult r) {
        try (BufferedWriter w = Files.newBufferedWriter(scoresFile, StandardOpenOption.CREATE,
                StandardOpenOption.APPEND)) {
            w.write(r.getPlayerName() + "," + r.getDifficulty().name() + "," + r.getScore() + "," + r.getTimeUsed());
            w.newLine();
        } catch (IOException e) {
            throw new RuntimeException("Cant save score", e);
        }
    }

    public class TestCSV {
        public static void main(String[] args) {
            CSVManager csv = new CSVManager();
            csv.writeUser(new User("ploy", "1234"));
            System.out.println(csv.readUsers().size()); // ควรเพิ่มขึ้น 1 ทุกครั้งที่รัน

            csv.writeScore(new GameResult("ploy", Difficulty.EASY, 450, 92));
            for (GameResult r : csv.readScores(Difficulty.EASY)) {
                System.out.println(r.getPlayerName() + " : " + r.getScore());
            }
        }
    }

}
