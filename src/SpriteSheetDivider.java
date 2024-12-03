import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class SpriteSheetDivider {
    private BufferedImage[][] sprite;
    private BufferedImage[][] shipParts;
    private BufferedImage[][] enemyParts;
    private BufferedImage[][] bosParts;
    private BufferedImage[][] itemParts;

    public SpriteSheetDivider(String filePath, int rows, int columns) {
        try {
            BufferedImage spriteSheet = ImageIO.read(new File(filePath));

            int frameWidth = spriteSheet.getWidth() / columns;
            int frameHeight = spriteSheet.getHeight() / rows;

            sprite = new BufferedImage[rows][columns];
            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < columns; col++) {
                    sprite[row][col] = spriteSheet.getSubimage(
                            col * frameWidth, row * frameHeight, frameWidth, frameHeight);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public BufferedImage[][] getEnemyParts() {
        int rows = 5;
        int cols = 5;
        enemyParts = new BufferedImage[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                enemyParts[i][j] = sprite[i][j + 5];
            }
        }
        return enemyParts;
    }

    public BufferedImage[][] getShipParts() {
        int rows = 5;
        int cols = 3;
        shipParts = new BufferedImage[rows][cols];

        for (int i = 0; i < rows; i++) {
            shipParts[i][0] = sprite[i][0];
            shipParts[i][1] = sprite[i][1];
            shipParts[i][2] = sprite[i][2];
        }
        return shipParts;
    }

    public BufferedImage[][] getBosParts() {
        int rows = 4;
        int cols = 6;
        bosParts = new BufferedImage[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                bosParts[i][j] = sprite[i + 6][j + 5];
            }
        }
        return bosParts;
    }

    public BufferedImage[][] getItemsPart() {
        int rows = 8;
        int cols = 13;
        itemParts = new BufferedImage[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                itemParts[i][j] = sprite[i][j];
            }
        }
        return itemParts;

    }
}