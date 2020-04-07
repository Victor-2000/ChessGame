import javafx.event.EventHandler;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import javafx.scene.input.MouseEvent;

public class ImageButton {

    private final Image STARTING_IMG;
    private final Image SELECTED_IMG;
    private final double posX;
    private final double posY;
    private final double width;
    private final double height;
    private final ActionType type;

    public ImageButton(GraphicsContext gc, Image STARTING_IMG, Image SELECTED_IMG, double posX, double posY,
                       double width, double height, ActionType type){
        this.STARTING_IMG = STARTING_IMG;
        this.SELECTED_IMG = SELECTED_IMG;
        this.type = type;
        this.posX = posX;
        this.posY = posY;
        this.width = width;
        this.height = height;

        gc.drawImage(STARTING_IMG,posX,posY,height,width);
    }

    public double getHeight() {
        return height;
    }

    public double getPosX() {
        return posX;
    }

    public double getPosY() {
        return posY;
    }

    public double getWidth() {
        return width;
    }

    public void activate(GraphicsContext gc){
        gc.drawImage(SELECTED_IMG,posX,posY,height,width);
    }

    public void deactivate(GraphicsContext gc){
        gc.drawImage(STARTING_IMG,posX,posY,height,width);
    }

    public ActionType getType() {
        return type;
    }
}
