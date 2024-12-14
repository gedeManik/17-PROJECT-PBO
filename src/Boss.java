import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Boss extends GameObject implements Behavior {
    private int health;
    int speed = 5;
    private ArrayList<Rectangle> bullets;

    public Boss(int x, int y, int width, int height, BufferedImage image, int health) {
        super(x, y, width, height, image);
        this.health = health;
        this.bullets = new ArrayList<>();
    }

    public int getHealth() {
        return health;
    }

    public void reduceHealth(int amount) {
        health -= amount;
    }

    public int getSpeed() {
        return health;
    }

    @Override
    public void move() {
        if (getX() <= 0 || getX() + getWidth() >= 395) {
            speed *= -1;
        }
        setX(getX() + speed);
    }

    @Override
    public void shoot() {
        int bulletX = getX() + getWidth() / 2 - 5;
        int bulletY = getY() + getHeight();
        bullets.add(new Rectangle(bulletX, bulletY, 10, 20));
    }

    public ArrayList<Rectangle> getBullets() {
        return bullets;
    }
}
