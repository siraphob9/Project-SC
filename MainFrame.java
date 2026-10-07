import javax.swing.*;

// Owner: คนที่ 3 (Menu & Game Logic)
public class MainFrame extends JFrame {

    private User user;
    private String playerName;
    private Difficulty difficulty;

    public MainFrame(User user) {
        super("PhotoHunt - Select difficulty");
        // TODO: build the difficulty-select UI
    }

    public void setPlayerName(String name) {
        // TODO
    }

    public void selectDifficulty(Difficulty d) {
        // TODO
    }

    public void startGame() {
        // TODO: dispose(); new GameFrame(new GameSession(user, difficulty), playerName).setVisible(true);
    }
}
