import java.awt.*;
import java.awt.image.BufferedImage;

public class Item {
    private int x, y, width, height;
    private BufferedImage image;
    private String type;

    public Item(int x, int y, int width, int height, BufferedImage image, String type) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.image = image;
        this.type = type;
    }

    public void moveDown(int speed) {
        y += speed;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public BufferedImage getImage() {
        return image;
    }

    public String getType() {
        return type;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
