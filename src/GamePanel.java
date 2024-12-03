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

    public GamePanel() {
        setBackground(Color.BLACK);
        setFocusable(true);

        // Inisialisasi pembagian sprite
        SpriteSheetDivider dividerShip = new SpriteSheetDivider("res" + File.separator + "Ship.png", 10, 10);
        SpriteSheetDivider dividerItems = new SpriteSheetDivider("res" + File.separator + "Item.png", 8, 13);
        shipParts = dividerShip.getShipParts();
        enemyParts = dividerShip.getEnemyParts();
        itemsParts = dividerItems.getItemsPart();

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
        if (random.nextInt(100) < 5) {
            String[] itemTypes = { "shield", "multi-shot", "minion", "laser" };
            String itemType = itemTypes[random.nextInt(itemTypes.length)];
            BufferedImage itemImage = null;

            switch (itemType) {
                case "shield":
                    itemImage = itemsParts[0][0];
                    break;
                case "multi-shot":
                    itemImage = itemsParts[0][1];
                    break;
                case "minion":
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
            Iterator<Rectangle> bulletIterator = bullets.iterator();
            while (bulletIterator.hasNext()) {
                Rectangle bullet = bulletIterator.next();
                if (enemy.getBounds().intersects(bullet)) {
                    spawnItem(enemy.getX(), enemy.getY());
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

    private void applyItemEffect(String itemType) {
        switch (itemType) {
            case "shield":
                System.out.println("Shield activated!");
                break;
            case "multi-shot":
                System.out.println("Multi-shot enabled!");
                break;
            case "minion":
                System.out.println("Minion deployed!");
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
    }
}
