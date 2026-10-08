import javax.swing.*;
import java.awt.*;
import java.util.List;

// Owner: นายวชิรวิทย์ กตกุลบัญชร (Authentication)
public class LoginFrame extends JFrame {

    private CSVManager csvManager;
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginFrame() {
        super("PhotoHunt - Login");
        csvManager = new CSVManager();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        usernameField = new LoginFrame.HintTextField("username");
        passwordField = new LoginFrame.HintPasswordField("password");
        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("sign up");
        setContentPane(LoginFrame.createAuthPanel(false, usernameField,
                passwordField, null,
                loginButton, registerButton));
        getRootPane().setDefaultButton(loginButton);

        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            if (username.trim().isEmpty() || password.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a username and password.",
                        "Login", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                User user = login(username, password);
                if (user != null) {
                    passwordField.setText("");
                    openMain(user);
                } else {
                    JOptionPane.showMessageDialog(this, "Incorrect username or password.",
                            "Login", JOptionPane.WARNING_MESSAGE);
                    passwordField.setText("");
                    passwordField.requestFocusInWindow();
                }
            } catch (RuntimeException ex) {
                JOptionPane.showMessageDialog(this,
                        "Could not read accounts. Please check CSVManager and the user data file.",
                        "Data error", JOptionPane.ERROR_MESSAGE);
            }
        });
        registerButton.addActionListener(e -> openRegister());
        pack();
        setMinimumSize(new Dimension(560, 640));
        setLocationRelativeTo(null);
    }

    // ตรวจทั้งชื่อและรหัสผ่านให้ตรงกับบัญชีเดียวกัน
    public User login(String u, String p) {
        if (u == null || p == null || u.trim().isEmpty() || p.trim().isEmpty()) {
            return null;
        }
        String username = u.trim();
        List<User> users = csvManager.readUsers();
        if (users == null) {
            throw new IllegalStateException("CSVManager.readUsers() must return a list, not null.");
        }
        for (User user : users) {
            if (user != null && username.equals(user.getUsername()) && p.equals(user.getPassword())) {
                return user;
            }
        }
        return null;
    }

    public void openRegister() {
        new RegisterFrame().setVisible(true);
        dispose();
    }

    public void openMain(User user) {
        if (user == null) {
            throw new IllegalArgumentException("A logged-in user is required.");
        }
        new MainFrame(user).setVisible(true);
        dispose();
    }

    // ใช้หน้าตาเดียวกันทั้งสองหน้า โดยไม่ต้องเพิ่มไฟล์ UI แยก
    static JPanel createAuthPanel(boolean signUp, JTextField username,
            JPasswordField password, JPasswordField confirmation,
            JButton primary, JButton link) {
        JPanel panel = new JPanel(null) {
            @Override
            public void paint(Graphics g) {
                Graphics2D copy = (Graphics2D) g.create();
                copy.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                super.paint(copy);
                copy.dispose();
            }

            @Override
            public void doLayout() {
                int width = Math.min(368, getWidth() - 64);
                int x = (getWidth() - width) / 2;
                int top = Math.max(24, (int) (getHeight() * 0.20));
                getComponent(0).setBounds(x - 30, top, width + 60, 40);
                getComponent(1).setBounds(x - 30, top + 40, width + 60, 32);
                int fieldTop = top + (signUp ? 100 : 132);
                username.setBounds(x, fieldTop, width, 45);
                password.setBounds(x, fieldTop + (signUp ? 81 : 76), width, 45);
                if (signUp) {
                    confirmation.setBounds(x, fieldTop + 162, width, 45);
                }
                int buttonTop = fieldTop + (signUp ? 233 : 149);
                primary.setBounds(x, buttonTop, width, 57);
                getComponent(getComponentCount() - 1).setBounds(
                        x - 35, buttonTop + 76, width + 70, 32);
            }
        };
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(1174, 836));
        JLabel title = new JLabel("Photo hunt", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", signUp ? Font.BOLD : Font.PLAIN,
                signUp ? 26 : 30));
        title.setForeground(new Color(25, 25, 25));
        JLabel subtitle = new JLabel(signUp ? "create your account" : "welcome back",
                SwingConstants.CENTER);
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, signUp ? 16 : 30));
        subtitle.setForeground(new Color(178, 178, 178));
        panel.add(title);
        panel.add(subtitle);
        panel.add(username);
        panel.add(password);
        if (signUp) {
            panel.add(confirmation);
        }
        primary.setFont(new Font("SansSerif", Font.PLAIN, signUp ? 16 : 21));
        primary.setForeground(Color.WHITE);
        primary.setBackground(new Color(76, 76, 76));
        primary.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D copy = (Graphics2D) g.create();
                copy.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                ButtonModel model = ((JButton) c).getModel();
                copy.setColor(model.isPressed() ? new Color(50, 50, 50)
                        : model.isRollover() ? new Color(90, 90, 90) : c.getBackground());
                copy.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 12, 12);
                if (c.isFocusOwner()) {
                    copy.setColor(new Color(170, 170, 170));
                    copy.drawRoundRect(2, 2, c.getWidth() - 5, c.getHeight() - 5, 10, 10);
                }
                copy.dispose();
                super.paint(g, c);
            }
        });
        primary.setOpaque(false);
        primary.setContentAreaFilled(false);
        primary.setBorderPainted(false);
        primary.setFocusPainted(false);
        primary.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        panel.add(primary);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 4));
        footer.setOpaque(false);
        JLabel prompt = new JLabel(signUp ? "already have account?" : "dont have any account?");
        prompt.setFont(new Font("SansSerif", Font.PLAIN, 16));
        prompt.setForeground(new Color(178, 178, 178));
        link.setFont(new Font("SansSerif", Font.PLAIN, 16));
        link.setForeground(new Color(76, 76, 76));
        link.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        link.setContentAreaFilled(false);
        link.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        footer.add(prompt);
        footer.add(link);
        panel.add(footer);
        return panel;
    }

    private static void styleInput(JTextField field, String hint) {
        field.setFont(new Font("SansSerif", Font.PLAIN, 13));
        field.setForeground(Color.BLACK);
        field.setCaretColor(Color.BLACK);
        field.setBackground(Color.WHITE);
        field.setOpaque(false);
        field.setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 14));
        field.getAccessibleContext().setAccessibleName(hint);
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override public void focusGained(java.awt.event.FocusEvent e) { field.repaint(); }
            @Override public void focusLost(java.awt.event.FocusEvent e) { field.repaint(); }
        });
    }

    private static void paintInputBorder(Graphics g, JTextField field) {
        Graphics2D copy = (Graphics2D) g.create();
        copy.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        copy.setColor(field.isFocusOwner() ? new Color(120, 120, 120) : new Color(213, 213, 213));
        copy.drawRoundRect(0, 0, field.getWidth() - 1, field.getHeight() - 1, 12, 12);
        copy.dispose();
    }

    private static void paintHint(Graphics g, JTextField field, String hint) {
        if (field.getDocument().getLength() == 0) {
            Graphics2D copy = (Graphics2D) g.create();
            copy.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            copy.setColor(Color.BLACK);
            copy.setFont(field.getFont());
            FontMetrics metrics = copy.getFontMetrics();
            copy.drawString(hint, 14,
                    (field.getHeight() - metrics.getHeight()) / 2 + metrics.getAscent());
            copy.dispose();
        }
    }

    static class HintTextField extends JTextField {
        private final String hint;
        HintTextField(String hint) {
            this.hint = hint;
            styleInput(this, hint);
        }
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            paintHint(g, this, hint);
        }
        @Override protected void paintBorder(Graphics g) { paintInputBorder(g, this); }
    }

    static class HintPasswordField extends JPasswordField {
        private final String hint;
        HintPasswordField(String hint) {
            this.hint = hint;
            styleInput(this, hint);
            setEchoChar('\u2022');
        }
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            paintHint(g, this, hint);
        }
        @Override protected void paintBorder(Graphics g) { paintInputBorder(g, this); }
    }
}
