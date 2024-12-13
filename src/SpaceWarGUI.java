
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

        // Add play button
        JButton playButton = new JButton("Play");
        playButton.setFont(new Font("Arial", Font.PLAIN, 25));
        playButton.setOpaque(false);
        playButton.setContentAreaFilled(false);
        playButton.setBorderPainted(false);
        playButton.setForeground(Color.WHITE);
        playButton.setAlignmentX(Component.CENTER_ALIGNMENT); // Center align
        playButton.setMaximumSize(new Dimension(150, 40)); // Set button size
        buttonPanel.add(playButton);        

        // Add spacing between buttons
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Add topscore button
        JButton scoreButton = new JButton("Top Score");
        scoreButton.setFont(new Font("Arial", Font.PLAIN, 25));
        scoreButton.setOpaque(false);
        scoreButton.setContentAreaFilled(false);
        scoreButton.setBorderPainted(false);
        scoreButton.setForeground(Color.WHITE);
        scoreButton.setAlignmentX(Component.CENTER_ALIGNMENT); // Center align
        scoreButton.setMaximumSize(new Dimension(150, 40)); // Set button size
        buttonPanel.add(scoreButton);

        // Add spacing between buttons
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Add exit button
        JButton exitButton = new JButton("Exit");
        exitButton.setFont(new Font("Arial", Font.PLAIN, 25));
        exitButton.setOpaque(false);
        exitButton.setContentAreaFilled(false);
        exitButton.setBorderPainted(false);
        exitButton.setForeground(Color.WHITE);
        exitButton.setAlignmentX(Component.CENTER_ALIGNMENT); // Center align
        exitButton.setMaximumSize(new Dimension(150, 40)); // Set button size
        buttonPanel.add(exitButton);

        backgroundLabel.add(buttonPanel); // Add the button panel to Grid 3

        // Add the backgroundLabel to the frame
        frame.setContentPane(backgroundLabel);

        playButton.addActionListener(e -> {
            GamePanel gamePanel = new GamePanel();
            frame.getContentPane().removeAll(); 
            frame.setContentPane(gamePanel); // pindah ke game panel
            frame.revalidate();
            frame.repaint();

            gamePanel.requestFocusInWindow();
        });

        scoreButton.addActionListener(e -> {
            TopScore topscore = new TopScore();
            frame.getContentPane().removeAll(); 
            frame.setContentPane(topscore); // pindah ke game panel
            frame.revalidate();
            frame.repaint();
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            topscore.requestFocusInWindow();
        });

        // Make the frame visible
        frame.setVisible(true);
    }
}
