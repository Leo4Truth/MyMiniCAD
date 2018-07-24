package MyMiniCAD;

import java.io.Serializable;

public class Settings implements Serializable {
    public static final int SETTINGS_MODEL_DRAW = 0;
    public static final int SETTINGS_MODEL_SELECT = 1;
    private int model = SETTINGS_MODEL_DRAW;

    public static final int SETTINGS_SHAPE_NONE = -1;
    public static final int SETTINGS_SHAPE_LINE = 0;
    public static final int SETTINGS_SHAPE_RECTANGLE = 1;
    public static final int SETTINGS_SHAPE_CIRCLE = 2;
    public static final int SETTINGS_SHAPE_TEXT = 3;
    private int shape = SETTINGS_SHAPE_LINE;

    public static final int SETTING_EDIT_MOVE = 0;
    public static final int SETTING_EDIT_RESIZE = 1;
    public static final int SETTING_EDIT_EDIT = 2;
    public static final int SETTING_EDIT_REMOVE = 3;
    private int edit = SETTING_EDIT_MOVE;

    private int fontSize = 8;
    private double lineWidth = 1.0;
    private boolean filled = false;

    /* For save and open file, we need to restore color settings in serializable objects
     * So restore their value as string
     * Note: the javafx.scene.paint.Color class is not serializable */
    private String strokeColorString = "0x000000FF";
    private String fillColorString = "0x000000FF";

    /* Default Constructor */
    Settings() {
        model = SETTINGS_MODEL_DRAW;
        shape = SETTINGS_SHAPE_LINE;
        edit = SETTING_EDIT_MOVE;
    }

    /* Copy Constructor */
    Settings(Settings settings) {
        this.model = settings.model;
        this.shape = settings.shape;
        this.fontSize = settings.fontSize;
        this.lineWidth = settings.lineWidth;
        this.filled = settings.filled;
        this.strokeColorString = settings.strokeColorString;
        this.fillColorString = settings.fillColorString;
        this.edit = settings.edit;
    }

    /* Set and Get Methods */
    public void setModel(int model) {
        this.model = model;
        switch (model) {
            case SETTINGS_MODEL_DRAW:
            {
                setShape(SETTINGS_SHAPE_LINE);
            }
            case SETTINGS_MODEL_SELECT:
            {
                setShape(SETTINGS_SHAPE_NONE);
            }
            default:
        }
    }

    public int getModel() {
        return model;
    }

    public void setShape(int shape) {
        this.shape = shape;
    }

    public int getShape() {
        return shape;
    }

    public void setFillColorString(String fillColorString) {
        this.fillColorString = fillColorString;
    }

    public String getFillColorString() {
        return fillColorString;
    }

    public void setStrokeColorString(String strokeColorString) {
        this.strokeColorString = strokeColorString;
    }

    public String getStrokeColorString() {
        return strokeColorString;
    }

    public void setFilled(boolean filled) {
        this.filled = filled;
    }

    public boolean isFilled() {
        return filled;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setLineWidth(double lineWidth) {
        this.lineWidth = lineWidth;
    }

    public double getLineWidth() {
        return lineWidth;
    }

    public void setEdit(int edit) {
        this.edit = edit;
    }

    public int getEdit() {
        return edit;
    }
}
