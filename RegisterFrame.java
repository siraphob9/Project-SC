import javax.swing.*;
import java.awt.*;
import java.util.List;

// Owner: นายวชิรวิทย์ กตกุลบัญชร (Authentication)
public class RegisterFrame extends JFrame {

    private CSVManager csvManager;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;

    public RegisterFrame() {
        super("PhotoHunt - Sign up");
        csvManager = new CSVManager();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        usernameField = new LoginFrame.HintTextField("username");
        passwordField = new LoginFrame.HintPasswordField("password");
        confirmPasswordField = new LoginFrame.HintPasswordField("confirm password");
        JButton loginButton = new JButton("login");
        JButton registerButton = new JButton("sign up");
        setContentPane(LoginFrame.createAuthPanel(true, usernameField,
                passwordField, confirmPasswordField,
                registerButton, loginButton));
        getRootPane().setDefaultButton(registerButton);

        registerButton.addActionListener(e -> {
            String password = new String(passwordField.getPassword());
            String confirmation = new String(confirmPasswordField.getPassword());
            if (!password.equals(confirmation)) {
                JOptionPane.showMessageDialog(this, "Passwords do not match.",
                        "Sign up", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                if (register(usernameField.getText(), password)) {
                    JOptionPane.showMessageDialog(this, "Registration successful. Please log in.");
                    openLogin();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Enter a username and password without commas or line breaks.\n"
                                    + "The username must not already exist.",
                            "Sign up", JOptionPane.WARNING_MESSAGE);
                }
            } catch (RuntimeException ex) {
                JOptionPane.showMessageDialog(this,
                        "Could not save the account. Please check CSVManager and the user data file.",
                        "Data error", JOptionPane.ERROR_MESSAGE);
            }
        });
        loginButton.addActionListener(e -> openLogin());
        pack();
        setMinimumSize(new Dimension(560, 640));
        setLocationRelativeTo(null);
    }

    // คืน false เมื่อข้อมูลไม่ถูกต้องหรือชื่อซ้ำ; ปัญหาการอ่าน/เขียนให้ผู้เรียกจัดการ
    public boolean register(String u, String p) {
        if (u == null || p == null || u.trim().isEmpty() || p.trim().isEmpty()) {
            return false;
        }
        String username = u.trim();
        // ป้องกันข้อมูลแตกคอลัมน์/แถวเมื่อ CSVManager ใช้ CSV แบบง่าย
        if (username.matches("(?s).*[\r\n,].*") || p.matches("(?s).*[\r\n,].*")) {
            return false;
        }
        List<User> users = csvManager.readUsers();
        if (users == null) {
            throw new IllegalStateException("CSVManager.readUsers() must return a list, not null.");
        }
        for (User user : users) {
            if (user != null && username.equals(user.getUsername())) {
                return false;
            }
        }
        csvManager.writeUser(new User(username, p));
        // ตรวจว่าบันทึกจริง ก่อนแจ้งสำเร็จ (writeUser ของโปรเจกต์ยังเป็น stub)
        List<User> savedUsers = csvManager.readUsers();
        if (savedUsers != null) {
            for (User user : savedUsers) {
                if (user != null && username.equals(user.getUsername()) && p.equals(user.getPassword())) {
                    return true;
                }
            }
        }
        throw new IllegalStateException("CSVManager did not persist the account.");
    }

    public void openLogin() {
        new LoginFrame().setVisible(true);
        dispose();
    }
}
