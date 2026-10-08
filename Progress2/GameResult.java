
public class GameResult {

    private String playerName;
    private Difficulty difficulty;
    private int score;
    private int timeUsed;

    public GameResult(String playerName, Difficulty difficulty, int score, int timeUsed) {
        this.playerName = playerName;
        this.difficulty = difficulty;
        this.score = score;
        this.timeUsed = timeUsed;
    }

    public String getPlayerName() {
        return playerName;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public int getScore() {
        return score;
    }

    public int getTimeUsed() {
        return timeUsed;
    }
}
