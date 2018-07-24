package MyMiniCAD;

import java.io.Serializable;

/* Class to represent a shape object in a cad file
 * To implement save and open functions, this class must be Serializable
 * In this way, using Object I/O, can we write MyShape objects
 * directly into a cad file and read them from a cad file. */
public class MyShape implements Serializable {
    /* Index of the shape in MyShapeStack */
    private int index;

    /* Each geometric object is within a rectangle border
     * MyShape Object records the Vertex Coordinate of the border */
    private double startX;
    private double startY;
    private double endX;
    private double endY;

    /* The text of the text shape */
    private String text;

    /* Settings of the MyShape Object */
    private Settings settings;

    /* Construct a MyShape Object with
     * 1. its index in stack
     * 2. vertices coordinates of its border
     * 3. and the settings at the time it was created */
    MyShape(int index, double startX, double startY, double endX, double endY, Settings settings) {
        this.index = index;
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.settings = new Settings(settings);
    }

    /* Methods to judge if a mouse click event has selected the MyShape Object */
    public boolean selectShape(double x, double y) {
        return x <= ((startX > endX) ? startX : endX) &&
                x >= ((startX < endX) ? startX : endX) &&
                y <= ((startY > endY) ? startY : endY) &&
                y >= ((startY < endY) ? startY : endY);

    }

    /* Get and Set Methods */
    public void setStartX(double startX) {
        this.startX = startX;
    }

    public double getStartX() {
        return startX;
    }

    public void setStartY(double startY) {
        this.startY = startY;
    }

    public double getStartY() {
        return startY;
    }

    public void setEndX(double endX) {
        this.endX = endX;
    }

    public double getEndX() {
        return endX;
    }

    public void setEndY(double endY) {
        this.endY = endY;
    }

    public double getEndY() {
        return endY;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    public Settings getSettings() {
        return settings;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}
