package Frames;

import Data.LoggedUser;

import javax.swing.*;

public class MainFrame extends JFrame {

      static LoggedUser loggedUser = new LoggedUser();


    public MainFrame() {

        this.setSize(500, 700);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);




        this.setVisible(true);

    }


}
