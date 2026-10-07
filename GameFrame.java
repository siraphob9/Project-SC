import javax.swing.*;

// Owner: คนที่ 4 (Gameplay & Photo)
public class GameFrame extends JFrame {

    private GameSession session;

    public GameFrame(GameSession session, String playerName) {
        super("PhotoHunt - Playing");
        // TODO: build the two-photo gameplay UI, start the countdown timer
        // ใช้ session.getPair() รูปเดียว + session.getActiveSpots() เป็นเป้าหมายของรอบนี้
    }

    public void displayLevel() {
        // TODO: วาด session.getPair().getLeft() / getRight() ลงสองแผง
    }

    public void handleClick(int x, int y) {
        // TODO: forward to session.handleClick(x, y), then updateDisplay()
        // ถ้า session.isRoundComplete() == true ให้เรียก openResult()
    }

    public void updateDisplay() {
        // TODO: refresh score/time/found-count labels
    }

    public void openResult() {
        // TODO: build a GameResult, save it via CSVManager, open ResultScoreboardFrame
    }
}
