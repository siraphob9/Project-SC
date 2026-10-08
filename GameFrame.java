package photohunt.ui;

import  photohunt.model.DifferenceSpot;
import photohunt.model.GameSession;
import photohunt.model.PhotoPair;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import javax.swing.*;

public class GameFrame extends JFrame {
    private GameSession session;
    private ImagePanel leftPanel;
    private ImagePanel rightPanel;
    private JLabel timeLabel;
    private JLabel foundLabel;
    private JLabel missLabel;
    private Timer timer;

    public GameFrame(GameSession session) {
        this.session = session;
        setTitle("Photo Hunt");
        setSize(1000, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        // TODO: สร้าง UI (2 panel แสดงรูป, label เวลา/คะแนน/พลาด) + Timer
        JPanel top = new JPanel(new GridLayout(1, 3));
        timeLabel = new JLabel("", SwingConstants.CENTER);
        foundLabel = new JLabel("", SwingConstants.CENTER);
        missLabel = new JLabel("", SwingConstants.CENTER);
        for(JLabel l : new JLabel[]{timeLabel, foundLabel, missLabel}){
            l.setFont(l.getFont().deriveFont(Font.BOLD, 16f));
            top.add(l);
        }
        add(top, BorderLayout.NORTH);

        leftPanel = new ImagePanel();
        rightPanel = new ImagePanel();
        JPanel center = new JPanel(new GridLayout(1, 2, 8, 0));
        center.add(leftPanel);
        center.add(rightPanel);
        add(center, BorderLayout.CENTER);
        
        timer = new Timer(1000, e ->{
            session.tick();
            updateDisplay();
            if(session.getTimeRemaining() <= 0){
                timer.stop();
                openResult();
            }
        });

        displayLevel();
        timer.start();
    }

    /** TODO: โหลดภาพ session.getCurrentPair() (ImageIO.read จากไฟล์) แสดงบน panel ทั้งสองฝั่ง */
    public void displayLevel() { 
        PhotoPair pair = session.getCurrentPair();
        try{
            BufferedImage img1 = ImageIO.read(new File(pair.getImage1Path()));
            BufferedImage img2 = ImageIO.read(new File(pair.getImage2Path()));
            leftPanel.setImage(img1);
            rightPanel.setImage(img2);
            leftPanel.setSpots(pair.getSpots());
            rightPanel.setSpots(pair.getSpots());
        }
        catch(IOException e){
            JOptionPane.showMessageDialog(this, "Picture is not loaded "+ e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        updateDisplay();
   }     
    /** TODO: ส่งพิกัดคลิกให้ session.handleClick แล้ว updateDisplay */
    public void handleClick(int x, int y) {
        session.handleClick(x, y);
        updateDisplay();
        if(session.isFinished()){
            timer.stop();
            openResult();
        }
        else if(session.isLevelComplete()){
            session.nextPair();
            displayLevel();
        }
     }

    /** TODO: อัปเดตเวลา / จุดที่เจอ / วงกลมทับจุดที่เจอ */
    public void updateDisplay() {
        int total = session.getCurrentPair().getSpots().size();
        timeLabel.setText("Time left: " + session.getTimeRemaining() + " s");
        foundLabel.setText("Founded: " + session.getFoundCount() + " / " + total);
        missLabel.setText("Misses: " + session.getMisses());
        leftPanel.repaint();
        rightPanel.repaint();
     }
     
    public void openResult() {
        timer.stop();
        new ResultScoreboardFrame(session.toResult()).setVisible(true);
        dispose();
    }

    private class ImagePanel extends JPanel{
        private  BufferedImage image;
        private java.util.List<DifferenceSpot> spots;
        private double scale = 1;
        private int offX = 0;
        private int offY = 0;

        ImagePanel(){
            setBackground(Color.DARK_GRAY);
            addMouseListener(new MouseAdapter(){
                @Override 
                public void mousePressed(MouseEvent e){
                    if(image == null)
                        return ;
                    int ix = (int) Math.round((e.getX() - offX) / scale);
                    int iy = (int) Math.round((e.getY() - offY) / scale);
                    if(ix < 0 || iy < 0 || ix >= image.getWidth() || iy >= image.getHeight())
                        return ;
                    GameFrame.this.handleClick(ix, iy);
                }
            });
        }

        void setImage(BufferedImage image){
            this.image = image;
        }
        void setSpots(java.util.List<DifferenceSpot> spots){
            this.spots = spots;
        }
    }
}
