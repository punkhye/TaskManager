package Frames;

import DBConnection.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RegisterFrame extends JFrame {

    JLabel usernameLabel = new JLabel("Username: ");
    JLabel passwordLabel = new JLabel("Password: ");
    JTextField usernameTextField=  new JTextField(10);
    JPasswordField passwordField = new JPasswordField(10);

    JButton registerButton = new JButton("Register");
    JButton backToTheLoginFrameButton = new JButton("Back");

    JPanel firstPanel = new JPanel();
    JPanel secondPanel = new JPanel();
    JPanel thirdPanel = new JPanel();

    static Connection conn = null;
    static PreparedStatement statement = null;
    static ResultSet result = null;

    RegisterFrame(){
        this.setTitle("Register");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(300, 300);
        this.setLocationRelativeTo(null);
        this.setLayout(new GridLayout(3,1));
        this.add(firstPanel);
        this.add(secondPanel);
        this.add(thirdPanel);
        firstPanel.setBackground(Color.lightGray);
        secondPanel.setBackground(Color.lightGray);
        thirdPanel.setBackground(Color.lightGray);
        firstPanel.add(usernameLabel);
        firstPanel.add(usernameTextField);
        secondPanel.add(passwordLabel);
        secondPanel.add(passwordField);
        thirdPanel.add(registerButton);
        thirdPanel.add(backToTheLoginFrameButton);


        registerButton.addActionListener(e -> {
            conn = DBConnection.getConnection();
            String username = usernameTextField.getText();
            String password = new String(passwordField.getPassword());
            String str = "insert into users(username,password) values(?,?)";
            int rowsInserted = 0;
            try {
                if(username == null || username.isEmpty() || password.isEmpty()) {
                   JOptionPane.showMessageDialog(null,"Username or password is empty");
                }else{
                    statement = conn.prepareStatement(str);
                    statement.setString(1, username);
                    statement.setString(2, password);

                    statement.execute();
                     rowsInserted = statement.executeUpdate();
                }

                if(rowsInserted > 0){
                JOptionPane.showMessageDialog(null, "User has been registered successfully");

                 this.dispose();
                 LoginFrame loginFrame = new LoginFrame();

                }else{
                    JOptionPane.showMessageDialog(null, "ERROR");
                }

            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }

        });

        backToTheLoginFrameButton.addActionListener(e -> {
           this.dispose();
           LoginFrame loginFrame = new LoginFrame();

        });

        this.setVisible(true);
    }

}
