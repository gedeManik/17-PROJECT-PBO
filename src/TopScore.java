import javax.swing.*;
import java.awt.*;
import java.util.List;

public class TopScore extends JPanel {

    private ImageIcon backgroundImage;

    public TopScore() {
        // Load the background image using ImageIcon
        backgroundImage = new ImageIcon("res/Bg-1.png");

        setLayout(new GridLayout(3, 1)); // Set GridLayout to match the SpaceWarGUI layout

        // Grid 1: Title Label ("TOP SCORE")
        JLabel titleLabel = new JLabel("TOP SCORE", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);
        add(titleLabel);

        // Grid 2: Empty Space (just for layout spacing, similar to SpaceWarGUI)
        JPanel topScore = new JPanel();
        topScore.setOpaque(false); // Transparent background to allow the background image to show
        topScore.setLayout(new GridLayout(10, 1)); // 10 rows, 1 column for top 10 scores
        add(topScore);

        // Fetch and display top scores from the database
        fetchAndDisplayTopScores(topScore);
        
        // Grid 3: Top Score Display (Using a JPanel to display scores)
        add(new JLabel()); // Empty label for spacing

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            // Draw the background image to fill the panel
            g.drawImage(backgroundImage.getImage(), 0, 0, getWidth(), getHeight(), this);
        }
    }

    private void fetchAndDisplayTopScores(JPanel topScore) {
        List<String> topScores = DatabaseConn.getTopScores(10); // Fetch top 10 scores from the database
        int rank = 1;
        for (String scoreData : topScores) {
            JLabel scoreLabel = new JLabel(rank + ". " + scoreData);
            scoreLabel.setFont(new Font("Arial", Font.PLAIN, 18));
            scoreLabel.setHorizontalAlignment(SwingConstants.CENTER);
            scoreLabel.setForeground(Color.WHITE); // White text to stand out on background
            topScore.add(scoreLabel);
            rank++;
        }
    }
}
