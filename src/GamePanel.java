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

    // image
    private BufferedImage[][] shipParts;
    private BufferedImage[][] enemyParts;
    private BufferedImage[][] itemsParts;
    private BufferedImage[][] bosParts;
    private int currentShipIndex = 4;
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
    private Boss boss;
    private boolean isBossFightActive = false;

    // Player
    private int playerX = 180;
    private int playerY = 700;
    private int playerEnergy = 100;
    private int maxEnergy = 100;
    private final int energyCost = 10;
    private final int energyRegenRate = 10;
    private final int energyRegenDelay = 1000;
    private long lastEnergyRegenTime = 0;
    private boolean canShoot = true;

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
        SpriteSheetDivider dividerBos = new SpriteSheetDivider("res" + File.separator + "Ship.png", 5, 5);
        shipParts = dividerShip.getShipParts();
        enemyParts = dividerShip.getEnemyParts();
        itemsParts = dividerItems.getItemsPart();
        bosParts = dividerBos.getBosParts();

        heartFull = new ImageIcon("res/hearts.png").getImage();
        heartLoss = new ImageIcon("res/heartsLess.png").getImage();

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

            spawnBos();
            spawnEnemies();
            moveEnemies();
            moveBullets();
            moveItems();

            if (isBossFightActive && boss != null) {
                if (random.nextInt(100) < 5) { //Boss shoots with a 5% chance per frame
                    boss.shoot();
                }
            }

            regenerateEnergy();
            resetShootAbility();
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
                handlePlayerShooting();
                repaint();
            }
        });
    }

    private void handlePlayerShooting() {
        if (playerEnergy >= energyCost && canShoot) {
            bullets.add(new Rectangle(playerX + 25, playerY, 10, 20));
            playerEnergy -= energyCost;
            canShoot = false;
            audioPlayer.playSoundEffect("res/shoot.wav");
        }
    }

    private void resetShootAbility() {
        canShoot = true;
    }

    private void spawnEnemies() {
        if (isBossFightActive)
            return;

        int spawnChance = 2 - (score / 1000);
        if (random.nextInt(100) < Math.max(spawnChance, 1)) {
            int x = random.nextInt(getWidth() - 60);
            int enemyRow = random.nextInt(enemyParts.length);
            int enemyCol = random.nextInt(enemyParts[0].length);
            BufferedImage enemyImage = enemyParts[enemyRow][enemyCol];
            enemies.add(new Enemy(x, 0, 60, 60, enemyImage));
        }
    }

    private void spawnBos() {
        if (!isBossFightActive && score > 0 && score % 1000 == 0) {
            int x = random.nextInt(getWidth() - 60);
            int bosRow = random.nextInt(2);
            int bosCol = random.nextInt(3);
            BufferedImage bosImage = bosParts[bosRow][bosCol];

            if (isBossFightActive && boss != null) {
                int newHealth = boss.getHealth() + 100;
                boss = new Boss(x, 0, 100, 100, bosImage, newHealth);
            } else {
                boss = new Boss(x, 0, 100, 100, bosImage, 100);
            }

            isBossFightActive = true;
        }
    }

    private void spawnItem(int x, int y) {
        if (random.nextInt(100) < 5) {
            String[] itemTypes = { "heal", "energy" };
            String itemType = itemTypes[random.nextInt(itemTypes.length)];
            BufferedImage itemImage = null;

            switch (itemType) {
                case "heal":
                    itemImage = itemsParts[0][2];
                    break;
                case "energy":
                    itemImage = itemsParts[1][2];
                    break;
            }

            if (itemImage != null) {
                items.add(new Item(x, y, 40, 40, itemImage, itemType));
            }
        }
    }

    private void moveEnemies() {
        if (isBossFightActive) {
            boss.move();
        }

        Iterator<Enemy> iterator = enemies.iterator();
        while (iterator.hasNext()) {
            Enemy enemy = iterator.next();
            enemy.move();
            if (enemy.getY() > getHeight()) {
                iterator.remove();
            }
        }
    }

    private void moveBullets() {
        int bulletSpeed = 5 + (score / 1000);

        Iterator<Rectangle> iterator = bullets.iterator();
        while (iterator.hasNext()) {
            Rectangle bullet = iterator.next();
            bullet.y -= 10;
            if (bullet.y < 0) {
                iterator.remove();
            }
        }

        if (isBossFightActive && boss != null) {
            ArrayList<Rectangle> bossBullets = boss.getBullets();
            Iterator<Rectangle> bossBulletIterator = bossBullets.iterator();
            while (bossBulletIterator.hasNext()) {
                Rectangle bossBullet = bossBulletIterator.next();
                bossBullet.y += bulletSpeed;
                if (bossBullet.y > getHeight()) {
                    bossBulletIterator.remove();
                }
            }
        }
    }

    private void moveItems() {
        Iterator<Item> iterator = items.iterator();
        while (iterator.hasNext()) {
            Item item = iterator.next();
            item.move();
            if (item.getY() > getHeight()) {
                iterator.remove();
            }
        }
    }

    private void regenerateEnergy() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastEnergyRegenTime >= energyRegenDelay) {
            if (playerEnergy < maxEnergy) {
                playerEnergy += energyRegenRate;
                if (playerEnergy > maxEnergy) {
                    playerEnergy = maxEnergy;
                }
            }
            lastEnergyRegenTime = currentTime;
        }
    }

    private void checkCollisions() {
        if (isBossFightActive && boss != null) {
            for (Rectangle bossBullet : boss.getBullets()) {
                if (new Rectangle(playerX, playerY, 60, 60).intersects(bossBullet)) {
                    collisionCount++;
                    playerHealth--;
                    if (playerHealth <= 0) {
                        audioPlayer.playSoundEffect("res/explode.wav");
                        gameOver = true;
                        timer.stop();
                        showGameOverDialog();
                        return;
                    }
                    boss.getBullets().remove(bossBullet);
                    break;
                }
            }
        }

        Iterator<Enemy> enemyIterator = enemies.iterator();
        while (enemyIterator.hasNext()) {
            Enemy enemy = enemyIterator.next();
            if (new Rectangle(playerX, playerY, 60, 60).intersects(enemy.getBounds())) {
                collisionCount++;
                playerHealth--;
                enemyIterator.remove();
                if (playerHealth <= 0) {
                    audioPlayer.playSoundEffect("res/explode.wav");
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

        // Handle collisions with the boss
        if (isBossFightActive) {
            Iterator<Rectangle> bulletIterator = bullets.iterator();
            while (bulletIterator.hasNext()) {
                Rectangle bullet = bulletIterator.next();
                if (boss.getBounds().intersects(bullet)) {
                    boss.reduceHealth(10);
                    if (boss.getHealth() <= 0) {
                        audioPlayer.playSoundEffect("res/explode.wav");
                        isBossFightActive = false;
                        boss = null;
                        score += 200;
                        break;
                    }
                    bulletIterator.remove(); // Remove the bullet if it hits the boss
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

    // private void showGameOverDialog() {
    //     String playerName = JOptionPane.showInputDialog(this, "Game Over! Masukkan nama Anda:", "Game Over",
    //             JOptionPane.PLAIN_MESSAGE);
    //     if (playerName != null && !playerName.isEmpty()) {
    //         int finalScore = calculateFinalScore();
    //         saveToDatabase(playerName, finalScore);
    //         JOptionPane.showMessageDialog(this, "Skor Anda telah disimpan!", "Info", JOptionPane.INFORMATION_MESSAGE);
    //     }

    //     SwingUtilities.getWindowAncestor(this).dispose();
    //     SpaceWarGUI.main(null);
    // }

    // private int calculateFinalScore() {
    //     int baseScore = 1000;
    //     int penalty = collisionCount * 100;
    //     return Math.max(baseScore - penalty, 0);
    // }

    private void saveToDatabase(String playerName, int score) {
        DatabaseConn.savePlayerToDatabase(playerName, score);
    }

    private void showGameOverDialog() {
        JDialog gameOverDialog = new JDialog((Frame) null, "SpaceWar!", true);
        gameOverDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        gameOverDialog.setSize(400, 300);
        gameOverDialog.setLocationRelativeTo(this);

        // Panel utama untuk dialog
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(Color.BLACK);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        // Label "Game Over"
        JLabel gameOverLabel = new JLabel("Game Over!");
        gameOverLabel.setFont(new Font("Arial", Font.BOLD, 30));
        gameOverLabel.setForeground(Color.RED);
        gameOverLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(gameOverLabel);

        // Spasi
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Input nama pemain
        JLabel nameLabel = new JLabel("Masukkan nama Anda:");
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(nameLabel);

        JTextField nameField = new JTextField();
        nameField.setMaximumSize(new Dimension(200, 30));
        nameField.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(nameField);

        // Spasi
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Tombol Save dan Cancel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.BLACK);

        JButton saveButton = new JButton("Save");
        saveButton.setFont(new Font("Arial", Font.PLAIN, 20));
        saveButton.setForeground(Color.WHITE);
        saveButton.setBackground(Color.DARK_GRAY);
        saveButton.setFocusPainted(false);
        saveButton.setBorderPainted(false);
        buttonPanel.add(saveButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font("Arial", Font.PLAIN, 20));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setBackground(Color.DARK_GRAY);
        cancelButton.setFocusPainted(false);
        cancelButton.setBorderPainted(false);
        buttonPanel.add(cancelButton);

        mainPanel.add(buttonPanel);

        // Tombol Save action listener
        saveButton.addActionListener(e -> {
            String playerName = nameField.getText().trim();
            if (!playerName.isEmpty()) {
                int finalScore = score;
                saveToDatabase(playerName, finalScore);
    
                // Kustom GUI untuk pesan dialog
                JDialog messageDialog = new JDialog((Frame) null, "SpaceWar!", true);
                messageDialog.setSize(300, 200);
                messageDialog.setLocationRelativeTo(gameOverDialog);
    
                JPanel panel = new JPanel();
                panel.setBackground(Color.BLACK);
                panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    
                JLabel messageLabel = new JLabel("Skor Anda telah disimpan!");
                messageLabel.setFont(new Font("Arial", Font.BOLD, 18));
                messageLabel.setForeground(Color.GREEN);
                messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                panel.add(Box.createRigidArea(new Dimension(0, 50)));
                panel.add(messageLabel);
    
                JButton okButton = new JButton("OK");
                okButton.setFont(new Font("Arial", Font.PLAIN, 16));
                okButton.setForeground(Color.WHITE);
                okButton.setBackground(Color.DARK_GRAY);
                okButton.setFocusPainted(false);
                okButton.setBorderPainted(false);
                okButton.setAlignmentX(Component.CENTER_ALIGNMENT);
                panel.add(Box.createRigidArea(new Dimension(0, 20)));
                panel.add(okButton);
    
                okButton.addActionListener(ev -> messageDialog.dispose());
    
                messageDialog.add(panel);
                messageDialog.setVisible(true);
            }
            gameOverDialog.dispose();
            SwingUtilities.getWindowAncestor(this).dispose(); // Menutup jendela game
            SpaceWarGUI.main(null); // Membuka menu utama
        });

        // Tombol Cancel action listener
        cancelButton.addActionListener(e -> {
            gameOverDialog.dispose();
            SwingUtilities.getWindowAncestor(this).dispose(); // Menutup jendela game
            SpaceWarGUI.main(null); // Membuka menu utama
        });

        gameOverDialog.add(mainPanel);
        gameOverDialog.setVisible(true);
    }

    // private int calculateFinalScore() {
    //     int baseScore = 1000;
    //     int penalty = collisionCount * 100; 
    //     return Math.max(baseScore - penalty, 0); 
    // }

    // private void saveToDatabase(String playerName, int score) {
    //     DatabaseConn.savePlayerToDatabase(playerName, score);
    // }


    private void applyItemEffect(String itemType) {
        switch (itemType) {
            case "heal":
                // Ensure health only increases if it's less than the maximum (3)
                if (playerHealth < 3) {
                    playerHealth++;
                    System.out.println("Health restored! Current HP: " + playerHealth);
                }
                break;

            case "energy":
                maxEnergy += 10; // Increase max energy by 10
                playerEnergy = maxEnergy; // Restore energy to max
                System.out.println("Energy upgraded! Max Energy: " + maxEnergy);
                break;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.drawImage(backgroundImage, 0, backgroundY1, getWidth(), getHeight(), null);
        g.drawImage(backgroundImage, 0, backgroundY2, getWidth(), getHeight(), null);

        g.drawImage(shipParts[currentShipIndex][currentDirection], playerX, playerY, 60, 60, null);

        g.setColor(Color.DARK_GRAY);
        g.fillRoundRect(10, 40, 200, 20, 20, 20); // Draw background of energy bar

        int energyWidth = (int) ((double) playerEnergy / maxEnergy * 200);
        g.setColor(Color.GREEN);
        g.fillRoundRect(10, 40, energyWidth, 20, 20, 20);

        if (isBossFightActive && boss != null) {
            g.drawImage(boss.getImage(), boss.getX(), boss.getY(), boss.getWidth(), boss.getHeight(), null);

            for (Rectangle bossBullet : boss.getBullets()) {
                g.setColor(Color.RED);
                g.fillRect(bossBullet.x, bossBullet.y, bossBullet.width, bossBullet.height);
            }
        }

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
                g.drawImage(heartFull, 10 + i * 40, 10, 30, 30, null);
            } else {
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