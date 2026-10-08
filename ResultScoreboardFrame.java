import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.*;
import javax.swing.border.EmptyBorder;


public class ResultScoreboardFrame extends JFrame {

    private GameResult result;
    private CSVManager csvManager;
    private User user;

    public ResultScoreboardFrame(GameResult result, User user) {
        super("PhotoHunt - Result");
        this.result = result;
        this.user = user;
        this.csvManager = new CSVManager();


        JPanel root = new JPanel();
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.setBackground(Color.white);
        root.setBorder(new EmptyBorder(40,60,40,60));
    
       

        JLabel title = new JLabel("Game Over",SwingConstants.CENTER);
        title.setFont(new Font("Sanesari",Font.PLAIN,20));
        title.setForeground(new Color(0x1F1F1E));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        root.add(title);
        root.add(Box.createVerticalStrut(24));
        root.add(buildScoreCard());
        root.add(Box.createVerticalStrut(28));

        setContentPane(root);
        displayResult();
        displayScoreboard(result.getDifficulty());

        root.add(Box.createVerticalStrut(28));


        JButton playAgain = buildButton("Play again");
        playAgain.addActionListener(e->{
            dispose();
            new GameFrame(new GameSession(user, result.getDifficulty()), result.getPlayerName()).setVisible(true);
        });
        root.add(playAgain);
        root.add(Box.createVerticalStrut(14));



        JButton menuBtn = buildButton("Back to menu");  
        menuBtn.addActionListener(e -> backToMain());   
        root.add(menuBtn);


        setContentPane(root);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
        setMinimumSize(new Dimension(420,420));
        setLocationRelativeTo(null);

        
    }
    private JPanel buildScoreCard(){
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card,BoxLayout.Y_AXIS));
        card.setBackground(new Color(0xF1F1F1));
        card.setBorder(new EmptyBorder(18,24,18,24));
        card.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.setMaximumSize(new Dimension(280,140));

        JLabel caption = new JLabel("your score",SwingConstants.CENTER);
        caption.setFont(new Font("SaneSerif",Font.PLAIN,12));
        caption.setForeground(new Color(0x77776F));
        caption.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel scoreLabel = new JLabel(result.getScore() + " pts",SwingConstants.CENTER);
        scoreLabel.setForeground(new Color(0x1F1F1E));
        scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel timeLabel = new JLabel("Time: "+ formatTime (result.getTimeUsed()),SwingConstants.CENTER);
        timeLabel.setFont(new Font("Saneserif",Font.PLAIN,12));
        timeLabel.setForeground(new Color(0x77776F));
        timeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        
        card.add(caption);
        card.add(Box.createVerticalStrut(8));
        card.add(scoreLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(timeLabel);
        return card;
    }
    private JButton buildButton(String text){
        JButton b = new JButton(text);
        b.setFont(new Font("SaneSerif",Font.PLAIN,15));
        b.setForeground(Color.WHITE);
        b.setBackground(new Color(0x4F4F4F));
        b.setOpaque(true);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setAlignmentX(Component.CENTER_ALIGNMENT);
        b.setMaximumSize(new Dimension(260,42));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;

    }

    private String formatTime(int seconds){
        int m = seconds/60;
        int s  = seconds %60;
        return m + ":" + String.format("%02d",s);
    }




    public void displayResult() {
       add(new JLabel("Player: " + result.getPlayerName()));
       add(new JLabel("Score: "+result.getScore()));
       add(new JLabel("Time usde: "+result.getTimeUsed() + "s"));
    }

    public void displayScoreboard(Difficulty d) {
        java.util.List<GameResult> top = csvManager.readScores(d);
        add(new JLabel("====Top score(" + d +")---"));

    }

    public void backToMain() {
       dispose();
       new MainFrame(user).setVisible(true);
    }

    public class TestResultScreen {
    public static void main(String[] args) {
        GameResult fake = new GameResult("Ploy", Difficulty.EASY, 450, 92);
        User fakeUser = new User("ploy", "1234");
        new ResultScoreboardFrame(fake, fakeUser).setVisible(true);
    }
}
}
