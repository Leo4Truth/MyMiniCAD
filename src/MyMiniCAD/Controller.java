package MyMiniCAD;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.text.DecimalFormat;
import java.text.ParsePosition;
import java.util.regex.Pattern;

public class Controller {
    /* Menu */
    public Menu menu_file;
    public MenuItem menu_item_new;
    public MenuItem menu_item_open;
    public MenuItem menu_item_save;
    public Menu menu_edit;
    public MenuItem menu_item_undo;
    public MenuItem menu_item_redo;
    public Menu menu_help;
    public MenuItem menu_item_help;
    public MenuItem menu_item_about;

    /* Tools */
    /* Vboxes */
    public VBox vbox_tools;
    public VBox vbox_shape;
    public VBox vbox_edit;
    public VBox vbox_text;
    public VBox vbox_attributes;

    /* Clear */
    public Button button_clear;

    /* Model */
    public RadioButton radio_button_model_draw;
    public RadioButton radio_button_model_select;

    /* Shape */
    public RadioButton radio_button_shape_line;
    public RadioButton radio_button_shape_rectangle;
    public RadioButton radio_button_shape_circle;
    public RadioButton radio_button_shape_text;

    /* Edit */
    public RadioButton radio_button_edit_resize;
    public RadioButton radio_button_edit_move;
    public RadioButton radio_button_edit_edit;
    public Button button_edit_remove;

    /* Attributes */
    public ColorPicker strokeColorPicker;
    public ColorPicker fillColorPicker;
    public TextField text_field_line_width;
    public CheckBox check_box_fill;

    /* Text */
    public TextField text_field_text_input;
    public TextField text_field_font_size;

    /* Canvas */
    public StackPane stack_pane_canvas;
    public Canvas canvas_0;

    private ToggleGroup toggle_group_model;
    private ToggleGroup toggle_group_shape;
    public ToggleGroup toggle_group_edit;

    /* Initialize the UI */
    /* Default Model is Draw model
     * Default Shape is Line */
    public void init() {
        /* Undo and redo are not implement so far
         * So their menu items are disabled */
        menu_item_undo.setDisable(true);
        menu_item_redo.setDisable(true);

        /* Set the model toggle button group */
        toggle_group_model = new ToggleGroup();
        radio_button_model_draw.setToggleGroup(toggle_group_model);
        radio_button_model_select.setToggleGroup(toggle_group_model);
        radio_button_model_draw.setSelected(true);

        /* Set the shape toggle button group */
        toggle_group_shape = new ToggleGroup();
        radio_button_shape_line.setToggleGroup(toggle_group_shape);
        radio_button_shape_rectangle.setToggleGroup(toggle_group_shape);
        radio_button_shape_circle.setToggleGroup(toggle_group_shape);
        radio_button_shape_text.setToggleGroup(toggle_group_shape);
        radio_button_shape_line.setSelected(true);

        /* Set the edit toggle button group */
        toggle_group_edit = new ToggleGroup();
        radio_button_edit_move.setToggleGroup(toggle_group_edit);
        radio_button_edit_resize.setToggleGroup(toggle_group_edit);
        radio_button_edit_edit.setToggleGroup(toggle_group_edit);

        radio_button_model_draw.setSelected(true);
        radio_button_shape_line.setSelected(true);

        vbox_edit.setDisable(true);
        vbox_text.setDisable(true);

        strokeColorPicker.setValue(Color.BLACK);
        fillColorPicker.setValue(Color.BLACK);
        text_field_line_width.setText("1.0");
        check_box_fill.setSelected(false);



        button_clear.setFocusTraversable(false);

        radio_button_model_draw.setFocusTraversable(false);
        radio_button_model_select.setFocusTraversable(false);

        radio_button_shape_line.setFocusTraversable(false);
        radio_button_shape_rectangle.setFocusTraversable(false);
        radio_button_shape_circle.setFocusTraversable(false);

        radio_button_shape_text.setFocusTraversable(false);
        radio_button_edit_move.setFocusTraversable(false);
        radio_button_edit_resize.setFocusTraversable(false);
        radio_button_edit_edit.setFocusTraversable(false);

        button_edit_remove.setFocusTraversable(false);
        text_field_text_input.setFocusTraversable(false);
        text_field_font_size.setFocusTraversable(false);
        text_field_line_width.setFocusTraversable(false);
        strokeColorPicker.setFocusTraversable(false);
        fillColorPicker.setFocusTraversable(false);
        check_box_fill.setFocusTraversable(false);
    }
}
