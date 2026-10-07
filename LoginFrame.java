import javax.swing.*;

// Owner: คนที่ 1 (Authentication)
public class LoginFrame extends JFrame {

    private CSVManager csvManager;

    public LoginFrame() {
        super("PhotoHunt - Login");
        // TODO: build the login UI
    }

    public User login(String u, String p) {
        return null; // TODO
    }

    public void openRegister() {
        // TODO: dispose(); new RegisterFrame().setVisible(true);
    }

    public void openMain(User user) {
        // TODO: dispose(); new MainFrame(user).setVisible(true);
    }
}
