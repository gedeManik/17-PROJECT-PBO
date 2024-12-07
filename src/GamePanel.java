import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class GamePanel extends JPanel {
    private Timer timer;
    private int playerX = 180;
    private int playerY = 700;
    private BufferedImage[][] shipParts;
    private BufferedImage[][] enemyParts;
    private BufferedImage[][] itemsParts;
    private int currentShipIndex = 3;
    private int currentDirection = 1;

    private Image backgroundImage;
    private int backgroundY1;
    private int backgroundY2;
    private final int backgroundSpeed = 2;
    private ArrayList<Enemy> enemies;
    private ArrayList<Rectangle> bullets;
    private ArrayList<Item> items;
    private Random random;
    private AudioPlayer audioPlayer;
    private int playerHealth = 3;
    private int score = 0;
    private Image heartFull;
    private Image heartLoss;

    private int collisionCount = 0; // Untuk melacak tabrakan
    private boolean gameOver = false; // Status game selesai
    
    public GamePanel() {
        setBackground(Color.BLACK);
        setFocusable(true);

        // Inisialisasi AudioPlayer
        audioPlayer = new AudioPlayer();
        audioPlayer.playBackgroundMusic("res/bg_music.wav");

        // Inisialisasi pembagian sprite
        SpriteSheetDivider dividerShip = new SpriteSheetDivider("res" + File.separator + "Ship.png", 10, 10);
        SpriteSheetDivider dividerItems = new SpriteSheetDivider("res" + File.separator + "Item.png", 8, 13);
        shipParts = dividerShip.getShipParts();
        enemyParts = dividerShip.getEnemyParts();
        itemsParts = dividerItems.getItemsPart();

        heartFull = new ImageIcon("res/hearts.png").getImage();
        heartLoss = new ImageIcon("res/heartsLess.png").getImage();

        // Muat gambar latar
        backgroundImage = new ImageIcon("res\\bg-1.png").getImage();

        // Inisialisasi posisi latar belakang
        backgroundY1 = 0;
        backgroundY2 = -1000; // Nilai default sementara

        // Inisialisasi elemen permainan
        enemies = new ArrayList<>();
        bullets = new ArrayList<>();
        items = new ArrayList<>();
        random = new Random();

        // Timer untuk memperbarui permainan
        timer = new Timer(16, e -> {
            backgroundY1 += backgroundSpeed;
            backgroundY2 += backgroundSpeed;

            if (backgroundY1 >= getHeight()) {
                backgroundY1 = backgroundY2 - getHeight();
            }
            if (backgroundY2 >= getHeight()) {
                backgroundY2 = backgroundY1 - getHeight();
            }
            spawnEnemies();
            moveEnemies();
            moveBullets();
            moveItems();
            checkCollisions();
            repaint();
        });
        timer.start();

        setupKeyBindings();

        // Tambahkan ComponentListener untuk menangani perubahan ukuran panel
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                backgroundY1 = 0;
                backgroundY2 = -getHeight(); // Update nilai backgroundY2 sesuai tinggi panel
            }
        });
    }

    private void setupKeyBindings() {
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("LEFT"), "moveLeft");
        getActionMap().put("moveLeft", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                playerX = Math.max(playerX - 15, 0);
                currentDirection = 0;
                repaint();
            }
        });

        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("RIGHT"), "moveRight");
        getActionMap().put("moveRight", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                playerX = Math.min(playerX + 15, getWidth() - 60);
                currentDirection = 2;
                repaint();
            }
        });

        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("released LEFT"), "stopLeft");
        getActionMap().put("stopLeft", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentDirection = 1;
                repaint();
            }
        });

        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("released RIGHT"), "stopRight");
        getActionMap().put("stopRight", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentDirection = 1;
                repaint();
            }
        });

        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"), "fireBullet");
        getActionMap().put("fireBullet", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                bullets.add(new Rectangle(playerX + 25, playerY, 10, 20));
                audioPlayer.playSoundEffect("res/shoot.wav");
                repaint();
            }
        });
    }

    private void spawnEnemies() {
        if (random.nextInt(100) < 2) {
            int x = random.nextInt(getWidth() - 60);
            int enemyRow = random.nextInt(enemyParts.length);
            int enemyCol = random.nextInt(enemyParts[0].length);
            BufferedImage enemyImage = enemyParts[enemyRow][enemyCol];
            enemies.add(new Enemy(x, 0, 60, 60, enemyImage));
        }
    }

    private void spawnItem(int x, int y) {
        if (random.nextInt(100) < 100) {
            String[] itemTypes = { "shield", "multi-shot", "heal", "laser" };
            String itemType = itemTypes[random.nextInt(itemTypes.length)];
            BufferedImage itemImage = null;

            switch (itemType) {
                case "shield":
                    itemImage = itemsParts[0][0];
                    break;
                case "multi-shot":
                    itemImage = itemsParts[0][1];
                    break;
                case "heal":
                    itemImage = itemsParts[0][2];
                    break;
                case "laser":
                    itemImage = itemsParts[0][3];
                    break;
            }

            if (itemImage != null) {
                items.add(new Item(x, y, 40, 40, itemImage, itemType));
            }
        }
    }

    private void moveEnemies() {
        Iterator<Enemy> iterator = enemies.iterator();
        while (iterator.hasNext()) {
            Enemy enemy = iterator.next();
            enemy.moveDown(5);
            if (enemy.getY() > getHeight()) {
                iterator.remove();
            }
        }
    }

    private void moveBullets() {
        Iterator<Rectangle> iterator = bullets.iterator();
        while (iterator.hasNext()) {
            Rectangle bullet = iterator.next();
            bullet.y -= 10;
            if (bullet.y < 0) {
                iterator.remove();
            }
        }
    }

    private void moveItems() {
        Iterator<Item> iterator = items.iterator();
        while (iterator.hasNext()) {
            Item item = iterator.next();
            item.moveDown(5);
            if (item.getY() > getHeight()) {
                iterator.remove();
            }
        }
    }

    private void checkCollisions() {
        Iterator<Enemy> enemyIterator = enemies.iterator();
        while (enemyIterator.hasNext()) {
            Enemy enemy = enemyIterator.next();
            if (new Rectangle(playerX, playerY, 60, 60).intersects(enemy.getBounds())) {
                collisionCount++;
                playerHealth--;
                enemyIterator.remove();
                if (playerHealth <= 0) {
                    gameOver = true;
                    timer.stop();
                    showGameOverDialog();
                    return;
                }

            }

            Iterator<Rectangle> bulletIterator = bullets.iterator();
            while (bulletIterator.hasNext()) {
                Rectangle bullet = bulletIterator.next();
                if (enemy.getBounds().intersects(bullet)) {
                    spawnItem(enemy.getX(), enemy.getY());
                    audioPlayer.playSoundEffect("res/explode.wav");
                    score += 20;
                    enemyIterator.remove();
                    bulletIterator.remove();
                    break;
                }
            }
        }

        Iterator<Item> itemIterator = items.iterator();
        while (itemIterator.hasNext()) {
            Item item = itemIterator.next();
            if (new Rectangle(playerX, playerY, 60, 60).intersects(item.getBounds())) {
                applyItemEffect(item.getType());
                itemIterator.remove();
            }
        }
    }

    private void playerCollisions() {
        Iterator<Enemy> enemyIterator = enemies.iterator();
        while (enemyIterator.hasNext()) {
            Enemy enemy = enemyIterator.next();
            if (new Rectangle(playerX, playerY, 60, 60).intersects(enemy.getBounds())) {
                audioPlayer.playSoundEffect("res/explode.wav");
                timer.stop();
                JOptionPane.showMessageDialog(this, "Game Over!", "Collision Detected", JOptionPane.ERROR_MESSAGE);
                break;
            }
        }
    }

   

    private void showGameOverDialog() {
        String playerName = JOptionPane.showInputDialog(this, "Game Over! Masukkan nama Anda:", "Game Over", JOptionPane.PLAIN_MESSAGE);
        if (playerName != null && !playerName.isEmpty()) {
            int finalScore = calculateFinalScore(); 
            saveToDatabase(playerName, finalScore);
            JOptionPane.showMessageDialog(this, "Skor Anda telah disimpan!", "Info", JOptionPane.INFORMATION_MESSAGE);
        }

        SwingUtilities.getWindowAncestor(this).dispose();
        SpaceWarGUI.main(null);
    }

    private int calculateFinalScore() {
        int baseScore = 1000;
        int penalty = collisionCount * 100; 
        return Math.max(baseScore - penalty, 0); 
    }

    private void saveToDatabase(String playerName, int score) {
        DatabaseConn.savePlayerToDatabase(playerName, score);
    }


    private void applyItemEffect(String itemType) {
        switch (itemType) {
            case "shield":
                System.out.println("Shield activated!");
                break;
            case "multi-shot":
                System.out.println("Multi-shot enabled!");
                break;
            case "heal":
                System.out.println("heal deployed!");
                if (playerHealth < 3) {
                    playerHealth++;
                    System.out.println("Health restored! Current HP: " + playerHealth);
                }
                break;
            case "laser":
                System.out.println("Laser charged!");
                break;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.drawImage(backgroundImage, 0, backgroundY1, getWidth(), getHeight(), null);
        g.drawImage(backgroundImage, 0, backgroundY2, getWidth(), getHeight(), null);

        g.drawImage(shipParts[currentShipIndex][currentDirection], playerX, playerY, 60, 60, null);

        for (Enemy enemy : enemies) {
            g.drawImage(enemy.getImage(), enemy.getX(), enemy.getY(), enemy.getWidth(), enemy.getHeight(), null);
        }

        for (Rectangle bullet : bullets) {
            g.setColor(Color.YELLOW);
            g.fillRect(bullet.x, bullet.y, bullet.width, bullet.height);
        }

        for (Item item : items) {
            g.drawImage(item.getImage(), item.getX(), item.getY(), 40, 40, null);
        }

        // Gambar HP di pojok kiri atas (menampilkan gambar hati penuh dan hilang)
        for (int i = 0; i < 3; i++) {
            if (i < playerHealth) {
                // Gambar hati penuh untuk HP yang ada
                g.drawImage(heartFull, 10 + i * 40, 10, 30, 30, null);
            } else {
                // Gambar hati hilang untuk HP yang hilang
                g.drawImage(heartLoss, 10 + i * 40, 10, 30, 30, null);
            }
        }

        g.setColor(Color.WHITE); // Warna teks score
        g.setFont(new Font("Arial", Font.BOLD, 24)); // Font score
        String scoreText = "Score: " + score;
        int textWidth = g.getFontMetrics().stringWidth(scoreText);
        g.drawString(scoreText, getWidth() - textWidth - 20, 30);

    }
}
