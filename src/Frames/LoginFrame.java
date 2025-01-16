package Frames;

import DBConnection.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static Frames.MainFrame.loggedUser;


public class LoginFrame extends JFrame {

    JLabel usernameLabel = new JLabel("Username: ");
    JLabel passwordLabel = new JLabel("Password: ");
    JTextField usernameTextField  = new JTextField(10);
    JPasswordField passwordField = new JPasswordField(10);
    JButton loginButton = new JButton("Login");
    JButton registerButton = new JButton("Register");


    static Connection conn = null;
    static PreparedStatement statement = null;
    static ResultSet result = null;

    public LoginFrame() {
        this.setSize(400, 200);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setTitle("Login");
        this.setLayout(new FlowLayout());
        this.add(usernameLabel);
        this.add(usernameTextField);
        this.add(passwordLabel);
        this.add(passwordField);
        this.add(loginButton);
        this.add(registerButton);

            loginButton.addActionListener(e -> {
                conn = DBConnection.getConnection();
                String username = usernameTextField.getText();
                String password = new String(passwordField.getPassword());
                String str = "SELECT * FROM Users where username=? and password=? ";
                try {

                    statement = conn.prepareStatement(str);
                    statement.setString(1, username);
                   statement.setString(2, password);
                   result = statement.executeQuery();

                    if (result != null && result.next()) {
                        JOptionPane.showMessageDialog(null, "Login successful!");

                        loggedUser.setUser_id(result.getInt("user_id"));
                        loggedUser.setUsername(result.getString("username"));
                        loggedUser.setPassword(result.getString("password"));
                       this.dispose();
                        MainFrame frame = new MainFrame();

                    } else {
                        JOptionPane.showMessageDialog(null, "Invalid username or password.");
                    }

                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }


            });

            registerButton.addActionListener(e -> {
                this.dispose();
                RegisterFrame frame = new RegisterFrame();


            });


        this.setVisible(true);


    }




}
