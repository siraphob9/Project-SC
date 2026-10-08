import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;

public class GameFrame extends JFrame {
    private GameSession session;
    private ImagePanel leftPanel;
    private ImagePanel rightPanel;
    private JLabel timeLabel;
    private JLabel foundLabel;
    private JLabel missLabel;
    private Timer timer;
    private boolean resultOpened = false ;

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
        PhotoPair pair = session.getPair();
        leftPanel.setImage(pair.getLeft());
        rightPanel.setImage(pair.getRight());
        leftPanel.setSpots(session.getActiveSpots());
        rightPanel.setSpots(session.getActiveSpots());
        updateDisplay();
    }
    /** TODO: ส่งพิกัดคลิกให้ session.handleClick แล้ว updateDisplay */
    public void handleClick(int x, int y) {
        if(resultOpened) return;
        session.handleClick(x, y);
        updateDisplay();
        if(session.isRoundComplete()){
            openResult();
        }
    }

    /** TODO: อัปเดตเวลา / จุดที่เจอ / วงกลมทับจุดที่เจอ */
    public void updateDisplay() {
        int total = session.getActiveSpots().size();
        timeLabel.setText("Time left: " + session.getTimeRemaining() + " s");
        foundLabel.setText("Founded: " + session.getFoundCount() + " / " + total);
        missLabel.setText("Misses: " + session.getMisses());
        leftPanel.repaint();
        rightPanel.repaint();
     }
     
    public void openResult() {
        if(resultOpened) return;
        resultOpened = true ;
        timer.stop();
        GameResult result = session.toResult();
        new CSVManager().writeScore(result);
        new ResultScoreboardFrame(result, session.getUser()).setVisible(true);
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
    
    @Override 
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        if(image == null)
            return ;
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        scale = Math.min((double) getWidth() / image.getWidth(),(double) getHeight() / image.getHeight());
        int w = (int) (image.getWidth() * scale);
        int h = (int) (image.getHeight() * scale);
        offX = (getWidth() - w) / 2;
        offY = (getHeight() - h) / 2;
        g2.drawImage(image, offX, offY, w, h, null);

        if(spots != null){
            g2.setColor(Color.RED);
            g2.setStroke(new BasicStroke(3f));
            for(DifferenceSpot s : spots){
                if(!s.isFound())
                    continue;
                int r = (int) Math.round(s.getRadius() * scale);
                int cx = offX + (int) Math.round(s.getX() * scale);
                int cy = offY + (int) Math.round(s.getY() * scale);
                g2.drawOval(cx - r, cy - r, 2 * r, 2 * r);
            }
        }
        g2.dispose();
    }
  }
}
