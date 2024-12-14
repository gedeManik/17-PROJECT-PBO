import java.awt.image.BufferedImage;

public class Item extends GameObject implements Behavior {
    private String type; // Type of the item (health, energy, shield, extra-bullet)

    public Item(int x, int y, int width, int height, BufferedImage image, String type) {
        super(x, y, width, height, image);
        this.type = type;
    }

    public String getType() {
        return type;
    }

    @Override
    public void move() {
        y += 5;
    }

    @Override
    public void shoot() {
        throw new UnsupportedOperationException("Unimplemented method 'shoot'");
    }
}
