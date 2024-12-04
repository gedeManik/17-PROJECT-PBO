import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class GamePanel extends JPanel {
    private Timer timer;
    private int playerX = 180;
    private int playerY = 700;
    private Image playerImage;
    private Image enemyImage;
    private Image backgroundImage;
    private ArrayList<Rectangle> enemies;
    private ArrayList<Rectangle> bullets;
    private Random random;

    public GamePanel() {
        setBackground(Color.BLACK);
        setFocusable(true);

        playerImage = new ImageIcon("res\\Ship.png").getImage();
        enemyImage = new ImageIcon("res\\Enemy.png").getImage();
        backgroundImage = new ImageIcon("res\\bg-1.png").getImage();

        enemies = new ArrayList<>();
        bullets = new ArrayList<>();
        random = new Random();

        // Timer game
        timer = new Timer(16, e -> {
            spawnEnemies();
            moveEnemies();
            moveBullets();
            checkCollisions();
            repaint();
        });
        timer.start();

        setupKeyBindings();
    }

    private void setupKeyBindings() {
        // pesawat bergerak ke kiri 
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("LEFT"), "moveLeft");
        getActionMap().put("moveLeft", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                playerX = Math.max(playerX - 15, 0); 
                repaint();
            }
        });

        // pesawat bergerak ke kanan
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("RIGHT"), "moveRight");
        getActionMap().put("moveRight", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                playerX = Math.min(playerX + 15, getWidth() - 60); 
                repaint();
            }
        });

        // Fire bullet
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"), "fireBullet");
        getActionMap().put("fireBullet", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fireBullet();
                repaint();
            }
        });
    }

    private void fireBullet() {
        bullets.add(new Rectangle(playerX + 25, playerY, 10, 20));
    }

    private void spawnEnemies() {
        if (random.nextInt(100) < 2) {
            int x = random.nextInt(getWidth() - 60);
            enemies.add(new Rectangle(x, 0, 60, 60));
        }
    }

    private void moveEnemies() {
        Iterator<Rectangle> iterator = enemies.iterator();
        while (iterator.hasNext()) {
            Rectangle enemy = iterator.next();
            enemy.y += 5;
            if (enemy.y > getHeight()) {
                iterator.remove(); 
            }
        }
    }

    private void moveBullets() {
        // Move bullets upward
        Iterator<Rectangle> iterator = bullets.iterator();
        while (iterator.hasNext()) {
            Rectangle bullet = iterator.next();
            bullet.y -= 10;
            if (bullet.y < 0) {
                iterator.remove(); 
            }
        }
    }

    private void checkCollisions() {
        Iterator<Rectangle> enemyIterator = enemies.iterator();
        while (enemyIterator.hasNext()) {
            Rectangle enemy = enemyIterator.next();
            Iterator<Rectangle> bulletIterator = bullets.iterator();
            while (bulletIterator.hasNext()) {
                Rectangle bullet = bulletIterator.next();
                if (enemy.intersects(bullet)) {
                    enemyIterator.remove(); 
                    bulletIterator.remove(); 
                    break;
                }
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // background
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);

        // player
        g.drawImage(playerImage, playerX, playerY, 60, 60, null);

        // enemies
        for (Rectangle enemy : enemies) {
            g.drawImage(enemyImage, enemy.x, enemy.y, enemy.width, enemy.height, null);
        }

        // bullets
        g.setColor(Color.YELLOW);
        for (Rectangle bullet : bullets) {
            g.fillRect(bullet.x, bullet.y, bullet.width, bullet.height);
        }
    }
}
