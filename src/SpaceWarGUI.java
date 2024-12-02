package src;
import javax.swing.*;
import java.awt.*;

public class SpaceWarGUI {
    public static void main(String[] args) {
        // Create the JFrame
        JFrame frame = new JFrame("SpaceWar!");
        int width = 400;  // Set the width
        int height = 800; // Set the height for 1:2 ratio
        frame.setSize(width, height);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null); // Center the frame

        // Load the background image and scale it to match the frame size
        String imagePath = "res\\Bg-1.png";
        ImageIcon originalIcon = new ImageIcon(imagePath);
        Image scaledImage = originalIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);

        // Create a JLabel with the scaled background image
        JLabel backgroundLabel = new JLabel(scaledIcon);
        backgroundLabel.setLayout(new GridLayout(3, 1)); // Set GridLayout 1x3

        // Grid 1: Title
        JLabel titleLabel = new JLabel("SpaceWar!", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);
        backgroundLabel.add(titleLabel);

        // Grid 2: Empty Space
        backgroundLabel.add(new JLabel()); // Add an empty label for spacing

        // Grid 3: Buttons Panel (Login and Register in vertical alignment)
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false); // Transparent panel
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS)); // Vertical alignment

        // Add Login button
        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.PLAIN, 18));
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT); // Center align
        loginButton.setMaximumSize(new Dimension(150, 40)); // Set button size
        buttonPanel.add(loginButton);

        // Add spacing between buttons
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Add Register button
        JButton registerButton = new JButton("Register");
        registerButton.setFont(new Font("Arial", Font.PLAIN, 18));
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT); // Center align
        registerButton.setMaximumSize(new Dimension(150, 40)); // Set button size
        buttonPanel.add(registerButton);

        backgroundLabel.add(buttonPanel); // Add the button panel to Grid 3

        // Add the backgroundLabel to the frame
        frame.setContentPane(backgroundLabel);

        // Make the frame visible
        frame.setVisible(true);
    }
}
