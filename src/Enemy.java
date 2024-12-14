import java.awt.image.BufferedImage;

public class Enemy extends GameObject implements Behavior {

    public Enemy(int x, int y, int width, int height, BufferedImage image) {
        super(x, y, width, height, image);
    }

    @Override
    public void move() {
        y += 5;
    }

    @Override
    public void shoot() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'shot'");
    }

}
