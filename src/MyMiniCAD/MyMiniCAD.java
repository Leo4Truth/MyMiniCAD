package MyMiniCAD;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;

public class MyMiniCAD extends Application {
    /* UI */
    private Stage primaryStage;
    private Controller controller;

    /* Settings */
    private Settings settings;

    /* File directory */
    private File savedDir;

    /* Shape Controller */
    private MyShapeStack myShapeStack;
    private int selectedIndex;
    private MyShape selectedShape;
    private boolean isMoving = false;
    private boolean isResizing = false;

    /* Graphics Paint */
    private Canvas canvas;
    private Canvas selectedCanvas = null;
    private GraphicsContext gc;

    /* The coordinate of the position a mouse pressed event occurs */
    private double startX;
    private double startY;

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MyMiniCAD.fxml"));
        VBox root = fxmlLoader.load();
        controller = fxmlLoader.getController();
        controller.init();

        primaryStage.setTitle("MyMiniCAD");
        primaryStage.setScene(new Scene(root, 1105, 825));
        primaryStage.show();
        primaryStage.setResizable(false);
        this.primaryStage = primaryStage;

        /* Initialize the settings and the stack */
        settings = new Settings();
        myShapeStack = new MyShapeStack();

        /* Push the background into the stack */
        MyShape background = new MyShape(0, 0, 0, 1000, 800, settings);
        myShapeStack.push(background);

        /* Initialize the initial save directory as the current work directory */
        savedDir = new File(".\\");

        /* Set KeyBoard Event Listner */
        root.getScene().setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                keyBoardEventHandler(event);
            }
        });

        setActionListner();
    }

    public static void main(String[] args) {
        launch(args);
    }

    /* Set the Action Listeners */
    private void setActionListner() {
        /*------------------------------------------------------------------------------------------------------------*/
        /* Menu */
        /* New */
        /* Create a new cad file, clear the current cad file */
        controller.menu_item_new.setOnAction(event -> {
            myShapeStack.clear();
            primaryStage.setTitle("MyMiniCAD");

            /* remove all canvas from the stack pane children, but remain the background */
            while (controller.stack_pane_canvas.getChildren().size()>1) {
                controller.stack_pane_canvas.getChildren().remove(controller.stack_pane_canvas.getChildren().get(1));
            }
        });

        /* Open */
        /* Open an existing cad file
         * A OpenFileDialog will appear */
        controller.menu_item_open.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open File");
            FileChooser.ExtensionFilter extensionFilter =
                    new FileChooser.ExtensionFilter("CAD files (*.cad)", "*.cad");
            fileChooser.getExtensionFilters().add(extensionFilter);
            fileChooser.setInitialDirectory(savedDir);
            File file = fileChooser.showOpenDialog(primaryStage);
            if (file != null) {
                open(file);
                savedDir = new File(file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf('\\')));
                primaryStage.setTitle(file.toString());
                System.out.println("savedDir = " + savedDir.toString());
                System.out.println(file.getAbsoluteFile());
                System.out.println("opened");
            }
            else {
                System.out.println("canceled open operaton");
            }

        });

        /* Save */
        /* Save the current cad file
         * A saveFileDialog will appear */
        controller.menu_item_save.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save File");
            fileChooser.setInitialFileName("untitled");
            FileChooser.ExtensionFilter extensionFilter =
                    new FileChooser.ExtensionFilter("CAD files (*.cad)", "*.cad");
            fileChooser.getExtensionFilters().add(extensionFilter);
            fileChooser.setInitialDirectory(savedDir);
            File file = fileChooser.showSaveDialog(primaryStage);
            if (file != null) {
                save(file.getAbsoluteFile().toString());
                savedDir = new File(file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf('\\')));
                primaryStage.setTitle(file.toString());
                System.out.println("savedDir = " + savedDir.toString());
                System.out.println(file.getAbsoluteFile());
                System.out.println("saved");
            }
            else {
                System.out.println("canceled save operation");
            }
        });

        /* About */
        /* An Alert (Information type) will appear when the about menu item is clicked.
         * An introduction then will be present on it */
        controller.menu_item_about.setOnAction(event -> {
            String nameInfoString = "\tMyMiniCAD\n\tby LIU Yuwen\n";
            String versionInfoString = "\tversion: 1.0\n";
            String aboutContentString = "\tMyMinuCAD is a simple drawing tool, operating in CAD mode, " +
                    "can place lines, rectangles, circles and text, " +
                    "select graphics, modify parameters such as color, etc., " +
                    "can drag graphics and resize, save and restore.\n" +
                    "\tSome functions such as undo and redo are not implemented at the moment.";
            Alert aboutInfo = new Alert(Alert.AlertType.INFORMATION, aboutContentString);
            aboutInfo.setHeaderText(nameInfoString + versionInfoString);
            aboutInfo.setTitle("About");
            aboutInfo.show();
        });

        /* Help */
        /* An Alert (Information type) will appear when the help menu item is clicked.
         * Help information then will be present on it */
        controller.menu_item_help.setOnAction(event -> {
            String helpInfoString = "For more infomation about MyMiniCAD " +
                    "please read the experiment report and readme document.";
            Alert helpInfo = new Alert(Alert.AlertType.INFORMATION, helpInfoString);
            helpInfo.setHeaderText("MyMiniCAD Help");
            helpInfo.setTitle("Help");
            helpInfo.show();
        });

        /*------------------------------------------------------------------------------------------------------------*/
        /* Clear */
        /* A Button */
        /* Clear all MyShape Objects in the stack and refresh the canvas */
        controller.button_clear.setOnAction(event -> {
            myShapeStack.clear();

            /* remove all canvas from the stack pane children, but remain the background */
            while (controller.stack_pane_canvas.getChildren().size()>1) {
                controller.stack_pane_canvas.getChildren().remove(controller.stack_pane_canvas.getChildren().get(1));
            }
        });

        /* ---------------------------------------------------------------------------------------------------------- */
        /* Attributes */
        /* A ColorPicker */
        /* Set the color of the stroke */
        controller.strokeColorPicker.setOnAction(event -> {
            settings.setStrokeColorString(controller.strokeColorPicker.getValue().toString());

            /* if in select.edit model, and a MyShape Object is selected
             * The change of the stroke color setting will work on the selected Object */
            if (settings.getModel()==Settings.SETTINGS_MODEL_SELECT) {
                if (settings.getEdit()==Settings.SETTING_EDIT_EDIT) {
                    if (selectedIndex != -1) {
                        gc.clearRect(0, 0, 1000, 800);
                        gc.setStroke(controller.strokeColorPicker.getValue());
                        selectedShape.getSettings().setStrokeColorString(
                                controller.strokeColorPicker.getValue().toString());
                        editDrawShape(selectedShape);
                    }
                }
            }
        });

        /* A ColorPicker */
        /* Set the color of the fill */
        controller.fillColorPicker.setOnAction(event -> {
            settings.setFillColorString(controller.fillColorPicker.getValue().toString());

            /* if in select.edit model, and a MyShape Object is selected
             * The change of the fill color setting will work on the selected Object */
            if (settings.getModel()==Settings.SETTINGS_MODEL_SELECT) {
                if (settings.getEdit()==Settings.SETTING_EDIT_EDIT) {
                    if (selectedIndex != -1) {
                        gc.clearRect(0, 0, 1000, 800);
                        gc.setFill(controller.fillColorPicker.getValue());
                        selectedShape.getSettings().setFillColorString(
                                controller.fillColorPicker.getValue().toString());
                        editDrawShape(selectedShape);
                    }
                }
            }
        });

        /* Line Width */
        /* A TextField */
        /* Set the line width */
        controller.text_field_line_width.textProperty().addListener((observable, oldValue, newValue) -> {
            /* The line width formatter */
            if(!newValue.matches("^[1-9]?[0-9]{1}+([.][0-9]?[0-9]?+)?$") && !newValue.isEmpty()) {
                controller.text_field_line_width.setText(oldValue);
            }

            /* Update the settings
             * if the textFiled is empty, the line width will be set as the default value: 1.0 */
            String line_width_string = controller.text_field_line_width.getText();
            if (line_width_string.isEmpty()) {
                settings.setLineWidth(Double.parseDouble("1.0"));
            }
            else {
                settings.setLineWidth(Double.parseDouble(line_width_string));
            }

            /* if in select.edit model, and a MyShape Object is selected
             * The change of the fill color setting will work on the selected Object */
            if (settings.getModel()==Settings.SETTINGS_MODEL_SELECT) {
                if (settings.getEdit()==Settings.SETTING_EDIT_EDIT) {
                    if (selectedIndex != -1) {
                        gc.clearRect(0, 0, 1000, 800);
                        gc.setLineWidth(settings.getLineWidth());
                        selectedShape.getSettings().setLineWidth(settings.getLineWidth());
                        editDrawShape(selectedShape);
                    }
                }
            }
        });

        /* Fill */
        /* A CheckBox */
        /* Set if the geometric objects to be painted are filled or not */
        controller.check_box_fill.setOnAction(event -> {
            settings.setFilled(controller.check_box_fill.isSelected());

            /* if in select.edit model, and a MyShape Object is selected
             * The change of the fillv or not setting will work on the selected Object */
            if (settings.getModel()==Settings.SETTINGS_MODEL_SELECT) {
                if (settings.getEdit()==Settings.SETTING_EDIT_EDIT) {
                    if (selectedIndex != -1) {
                        gc.clearRect(0, 0, 1000, 800);

                        selectedShape.getSettings().setFilled(settings.isFilled());
                        editDrawShape(selectedShape);
                    }
                }
            }
        });

        /* ---------------------------------------------------------------------------------------------------------- */
        /* Model */
        /* Draw */
        /* A RadioButton in the Model ToggleGroup */
        /* Set the model as draw */
        controller.radio_button_model_draw.setOnAction(event -> {
            /* set the default settings of the draw model
             * and change some UI */
            settings.setModel(Settings.SETTINGS_MODEL_DRAW);
            settings.setShape(Settings.SETTINGS_SHAPE_LINE);

            controller.radio_button_edit_move.setSelected(false);
            controller.radio_button_edit_resize.setSelected(false);
            controller.radio_button_edit_edit.setSelected(false);

            controller.vbox_shape.setDisable(false);
            controller.vbox_edit.setDisable(true);
            controller.vbox_text.setDisable(true);
            controller.vbox_attributes.setDisable(false);

            controller.radio_button_shape_line.setSelected(true);

            /* Exit Select Model
             * if some MyShape Object is select
             * repaint it and remove its border */
            if (selectedIndex != -1) {
                gc.clearRect(0, 0, 1000, 800);
                editDrawShape(selectedShape);
                selectedIndex = -1;
            }
        });

        /* Select */
        /* A RadioButton in the Model ToggleGroup */
        /* Set the model as select */
        controller.radio_button_model_select.setOnAction(event -> {
            /* set the default settings of the select model
             * and change some UI */
            settings.setModel(Settings.SETTINGS_MODEL_SELECT);
            controller.vbox_shape.setDisable(true);
            controller.vbox_edit.setDisable(false);

            controller.vbox_attributes.setDisable(true);

            controller.radio_button_shape_line.setSelected(false);
            controller.radio_button_shape_rectangle.setSelected(false);
            controller.radio_button_shape_circle.setSelected(false);
            controller.radio_button_shape_text.setSelected(false);

            controller.radio_button_edit_move.setSelected(true);

            settings.setEdit(Settings.SETTING_EDIT_MOVE);
            selectedIndex = -1;
        });

        /* ---------------------------------------------------------------------------------------------------------- */
        /* Shape */
        /* A ToggleGroup is only enabled in Draw Model */

        /* Line */
        controller.radio_button_shape_line.setOnAction(event -> {
            settings.setShape(Settings.SETTINGS_SHAPE_LINE);
            controller.vbox_text.setDisable(true);
        });

        /* Rectangele */
        controller.radio_button_shape_rectangle.setOnAction(event -> {
            settings.setShape(Settings.SETTINGS_SHAPE_RECTANGLE);
            controller.vbox_text.setDisable(true);
        });

        /* Circle*/
        /* Note: in fact it is Oval or Ellipse */
        controller.radio_button_shape_circle.setOnAction(event -> {
            settings.setShape(Settings.SETTINGS_SHAPE_CIRCLE);
            controller.vbox_text.setDisable(true);
        });

        /* Text */
        controller.radio_button_shape_text.setOnAction(event -> {
            settings.setShape(Settings.SETTINGS_SHAPE_TEXT);
            controller.vbox_text.setDisable(false);
        });

        /* ---------------------------------------------------------------------------------------------------------- */
        /* Edit */
        /* A ToggleGroup is only enabled in Select Model */

        /* Move */
        controller.radio_button_edit_move.setOnAction(event -> {
            settings.setEdit(Settings.SETTING_EDIT_MOVE);
            controller.radio_button_edit_move.setFocusTraversable(false);
            controller.vbox_attributes.setDisable(true);
        });

        /* Resize */
        controller.radio_button_edit_resize.setOnAction(event -> {
            settings.setEdit(Settings.SETTING_EDIT_RESIZE);

            controller.vbox_attributes.setDisable(true);
        });

        /* Edit */
        controller.radio_button_edit_edit.setOnAction(event -> {
            settings.setEdit(Settings.SETTING_EDIT_EDIT);

            controller.vbox_attributes.setDisable(false);
        });

        /* Remove */
        /* A Button */
        controller.button_edit_remove.setOnAction(event -> {
            settings.setEdit(Settings.SETTING_EDIT_REMOVE);
            controller.radio_button_edit_move.setSelected(false);
            controller.radio_button_edit_resize.setSelected(false);
            controller.radio_button_edit_edit.setSelected(false);
            if (selectedIndex != -1) {
                controller.stack_pane_canvas.getChildren().remove(
                        controller.stack_pane_canvas.getChildren().get(selectedIndex));
                myShapeStack.remove(selectedIndex);
                selectedIndex = -1;
            }
            settings.setEdit(Settings.SETTING_EDIT_MOVE);
            controller.radio_button_edit_move.setSelected(true);
        });

        /* ---------------------------------------------------------------------------------------------------------- */
        /* Text */
        /* The font size must be 1-bit or 2-bit decimal */
        controller.text_field_font_size.textProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue.matches("[1-9]{0,1}[0-9]{0,1}")){
                controller.text_field_font_size.setText(oldValue);
            }

            String font_size_text = controller.text_field_font_size.getText();
            if (font_size_text.isEmpty()) {
                controller.text_field_font_size.setText("8");
                settings.setFontSize(Integer.parseInt("8"));
            }
            else {
                settings.setFontSize(Integer.parseInt(font_size_text));
            }
        });

        /* ---------------------------------------------------------------------------------------------------------- */
        /* Canvas Events */

        /* Mouse pressed */
        controller.stack_pane_canvas.setOnMousePressed((MouseEvent event) -> {
            startX = event.getX();
            startY = event.getY();

            /* Draw model */
            if (settings.getModel() == Settings.SETTINGS_MODEL_DRAW) {
                canvas = new Canvas(1000.0f, 800.0f);
                controller.stack_pane_canvas.getChildren().add(canvas);
                gc = canvas.getGraphicsContext2D();
                String strokeColorString = settings.getStrokeColorString();
                String fillColorString = settings.getFillColorString();
                gc.setStroke(Color.web(strokeColorString));
                gc.setFill(Color.web(fillColorString));
            }
            /* Select Model */
            else if (settings.getModel() == Settings.SETTINGS_MODEL_SELECT) {
                /* update select object */
                int newSelectedIndex = (myShapeStack.select(startX, startY) != null) ?
                        myShapeStack.select(startX, startY).getIndex() : -1;
                if (newSelectedIndex != selectedIndex && selectedIndex != -1) {
                    gc.clearRect(0, 0, 1000, 800);
                    editDrawShape(selectedShape);
                }
                selectedIndex = newSelectedIndex;

                if (selectedIndex == -1) {
                    System.out.println("select nothing");
                }
                else {
                    System.out.println("select index " + selectedIndex);
                    selectedShape = myShapeStack.select(startX, startY);
                    selectedCanvas = (Canvas) controller.stack_pane_canvas.getChildren().get(selectedIndex);
                    gc = selectedCanvas.getGraphicsContext2D();
                    gc.setLineWidth(selectedShape.getSettings().getLineWidth());
                    String strokeColorString = selectedShape.getSettings().getStrokeColorString();
                    String fillColorString = selectedShape.getSettings().getFillColorString();
                    gc.setStroke(Color.web(strokeColorString));
                    gc.setFill(Color.web(fillColorString));
                    drawBorder(gc, selectedShape.getStartX(), selectedShape.getStartY(),
                            selectedShape.getEndX(), selectedShape.getEndY(), selectedShape.getSettings().isFilled());
                }

                /* Move */
                if (settings.getEdit() == Settings.SETTING_EDIT_MOVE) {
                    System.out.println("Move");
                    isMoving = true;
                }
                /* Resize */
                else if (settings.getEdit() == Settings.SETTING_EDIT_RESIZE) {
                    System.out.println("Resize");
                    isResizing = true;
                }
            }
            myShapeStack.printInfo();
        });

        /* Drag mouse */
        controller.stack_pane_canvas.setOnMouseDragged((MouseEvent event) -> {
            double currentX = event.getX();
            double currentY = event.getY();

            // translation of the mouse drag event
            // from the current coordinates to the coordinate where the mouse pressed
            double dx = currentX - startX;
            double dy = currentY - startY;

            double width = currentX > startX ? (currentX - startX) : (startX - currentX);
            double height = currentY > startY ? (currentY - startY) : (startY - currentY);
            double realStartX = currentX > startX ? startX : currentX;
            double realStartY = currentY > startY ? startY : currentY;

            /* Draw Model */
            if (settings.getModel() == Settings.SETTINGS_MODEL_DRAW) {
                gc.clearRect(0.0, 0.0, 1000.0, 800.0);
                gc.save();
                gc.setLineWidth(settings.getLineWidth());
                switch (settings.getShape()) {
                    case Settings.SETTINGS_SHAPE_LINE:
                    {
                        gc.strokeLine(startX, startY, currentX, currentY);
                        gc.restore();
                    }
                    break;
                    case Settings.SETTINGS_SHAPE_RECTANGLE:
                    {
                        if (settings.isFilled()) {
                            gc.fillRect(realStartX, realStartY, width, height);
                        }
                        gc.strokeRect(realStartX, realStartY, width, height);
                        gc.restore();
                    }
                    break;
                    case Settings.SETTINGS_SHAPE_CIRCLE:
                    {
                        if (settings.isFilled()) {
                            gc.fillOval(realStartX, realStartY, width, height);
                        }
                        gc.strokeOval(realStartX, realStartY, width, height);
                        gc.restore();
                    }
                    break;
                    case Settings.SETTINGS_SHAPE_TEXT:
                    {
                        gc.strokeRect(realStartX, realStartY, width, height);
                        gc.restore();
                    }
                    default:
                        break;
                }
            }
            /* Select Model */
            else if (settings.getModel() == Settings.SETTINGS_MODEL_SELECT) {
                /* Move */
                if (settings.getEdit() == Settings.SETTING_EDIT_MOVE) {
                    if (selectedIndex != -1 && isMoving) {
                        gc.save();
                        gc.clearRect(0, 0, 1000, 800);
                        drawBorder(gc, selectedShape.getStartX() + dx, selectedShape.getStartY() + dy,
                                selectedShape.getEndX() + dx, selectedShape.getEndY() + dy,
                                selectedShape.getSettings().getShape()==Settings.SETTINGS_SHAPE_TEXT);
                        moveDrawShape(selectedShape, dx, dy);
                        gc.restore();
                    }
                }
                /* Resize */
                else if (settings.getEdit() == Settings.SETTING_EDIT_RESIZE) {
                    if (selectedIndex != -1 && isResizing) {
                        gc.clearRect(0, 0, 1000, 800);
                        drawBorder(gc, selectedShape.getStartX(), selectedShape.getStartY(), currentX, currentY,
                                selectedShape.getSettings().getShape()==Settings.SETTINGS_SHAPE_TEXT);
                        resizeDrawShape(selectedShape, currentX, currentY);
                        gc.restore();
                    }
                }
            }

            myShapeStack.printInfo();
        });

        /* Release mouse */
        controller.stack_pane_canvas.setOnMouseReleased((MouseEvent event) -> {
            double endX = event.getX();
            double endY = event.getY();

            double dx = endX - startX;
            double dy = endY - startY;

            double width = endX > startX ? dx : -dx;
            double height = endY > startY ? dy : -dy;
            double realStartX = endX > startX ? startX : endX;
            double realStartY = endY > startY ? startY : endY;

            if (settings.getModel() == Settings.SETTINGS_MODEL_DRAW) {
                gc.clearRect(0.0, 0.0, 1000.0, 800.0);
                gc.save();
                gc.setLineWidth(settings.getLineWidth());
                switch (settings.getShape()) {
                    case Settings.SETTINGS_SHAPE_LINE:
                    {
                        gc.strokeLine(startX, startY, endX, endY);
                        gc.restore();
                    }
                    break;
                    case Settings.SETTINGS_SHAPE_RECTANGLE:
                    {
                        if (settings.isFilled()) {
                            gc.fillRect(realStartX, realStartY, width, height);
                        }
                        gc.strokeRect(realStartX, realStartY, width, height);
                        gc.restore();
                    }
                    break;
                    case Settings.SETTINGS_SHAPE_CIRCLE:
                    {
                        if (settings.isFilled()) {
                            gc.fillOval(realStartX, realStartY, width, height);
                        }
                        gc.strokeOval(realStartX, realStartY, width, height);

                        gc.restore();
                    }
                    break;
                    case Settings.SETTINGS_SHAPE_TEXT:
                    {
                        realStartY = endY > startY ? endY : startY;
                        gc.setFont(new Font("font", settings.getFontSize()));
                        if (settings.isFilled()) {
                            gc.fillText(controller.text_field_text_input.getText(), realStartX, realStartY, width);
                        }
                        gc.strokeText(controller.text_field_text_input.getText(), realStartX, realStartY, width);

                        gc.restore();
                    }
                    break;
                    default:
                        break;
                }

                MyShape shape = new MyShape(myShapeStack.size() + 1, startX, startY, endX, endY, settings);
                if (settings.getShape() == Settings.SETTINGS_SHAPE_TEXT) {
                    shape.setText(controller.text_field_text_input.getText());
                }
                myShapeStack.push(shape);
            }
            // Select Model
            else if (settings.getModel() == Settings.SETTINGS_MODEL_SELECT) {
                // if selected
                if (selectedIndex != -1) {
                    gc.save();
                    gc.clearRect(0, 0, 1000, 800);

                    if (isMoving) {
                        moveDrawShape(selectedShape, dx, dy);

                        // update information of the shape in stack
                        selectedShape.setStartX(selectedShape.getStartX()+dx);
                        selectedShape.setStartY(selectedShape.getStartY()+dy);
                        selectedShape.setEndX(selectedShape.getEndX()+dx);
                        selectedShape.setEndY(selectedShape.getEndY()+dy);

                        drawBorder(gc, selectedShape.getStartX(), selectedShape.getStartY(),
                                selectedShape.getEndX(), selectedShape.getEndY(),
                                selectedShape.getSettings().getShape()==Settings.SETTINGS_SHAPE_TEXT);
                        isMoving = false;
                    }
                    else if (isResizing) {
                        resizeDrawShape(selectedShape, endX, endY);

                        // update information of the shape in stack
                        selectedShape.setEndX(endX);
                        selectedShape.setEndY(endY);

                        drawBorder(gc, selectedShape.getStartX(), selectedShape.getStartY(),
                                selectedShape.getEndX(), selectedShape.getEndY(),
                                selectedShape.getSettings().getShape()==Settings.SETTINGS_SHAPE_TEXT);
                        isResizing = false;
                    }
                    else {
                        editDrawShape(selectedShape);
                        drawBorder(gc, selectedShape.getStartX(), selectedShape.getStartY(),
                                selectedShape.getEndX(), selectedShape.getEndY(),
                                selectedShape.getSettings().getShape()==Settings.SETTINGS_SHAPE_TEXT);
                    }

                }
            }

            myShapeStack.printInfo();
        });
    }

    /* Draw the border of the MyShape Object */
    private void drawBorder(GraphicsContext theGc,
                            double startX, double startY, double endX, double endY, boolean isText) {
        double width = endX > startX ? (endX - startX) : (startX - endX);
        double height = endY > startY ? (endY - startY) : (startY - endY);
        double realStartX = endX > startX ? startX : endX;
        double realStartY = endY > startY ? startY : endY;

        theGc.strokeRect(realStartX, realStartY, width, height);
    }

    /* Draw Selected MyShape Object
     * this function will be called in select.edit model */
    private void editDrawShape(MyShape selectedShape) {
        double width = (selectedShape.getEndX() > selectedShape.getStartX()) ?
                (selectedShape.getEndX() - selectedShape.getStartX()) :
                (selectedShape.getStartX() - selectedShape.getEndX());
        double height = (selectedShape.getEndY() > selectedShape.getStartY()) ?
                (selectedShape.getEndY() - selectedShape.getStartY()) :
                (selectedShape.getStartY() - selectedShape.getEndY());
        double realStartX = (selectedShape.getEndX() > selectedShape.getStartX()) ?
                selectedShape.getStartX() : selectedShape.getEndX();
        double realStartY = (selectedShape.getEndY() > selectedShape.getStartY()) ?
                selectedShape.getStartY() : selectedShape.getEndY();

        String strokeColorString = selectedShape.getSettings().getStrokeColorString();
        String fillColorString = selectedShape.getSettings().getFillColorString();
        gc.setStroke(Color.web(strokeColorString));
        gc.setFill(Color.web(fillColorString));
        gc.setLineWidth(selectedShape.getSettings().getLineWidth());
        gc.setFont(new Font("font", selectedShape.getSettings().getFontSize()));

        switch (selectedShape.getSettings().getShape()) {
            case Settings.SETTINGS_SHAPE_LINE:
            {
                gc.strokeLine(selectedShape.getStartX(), selectedShape.getStartY(),
                        selectedShape.getEndX(), selectedShape.getEndY());
                gc.restore();
            }
            break;
            case Settings.SETTINGS_SHAPE_RECTANGLE:
            {
                if (selectedShape.getSettings().isFilled()) {
                    gc.fillRect(realStartX, realStartY, width, height);
                }
                gc.strokeRect(realStartX, realStartY, width, height);

                gc.restore();
            }
            break;
            case Settings.SETTINGS_SHAPE_CIRCLE:
            {
                if (selectedShape.getSettings().isFilled()) {
                    gc.fillOval(realStartX, realStartY, width, height);
                }
                gc.strokeOval(realStartX, realStartY, width, height);

                gc.restore();
            }
            break;
            case Settings.SETTINGS_SHAPE_TEXT:
            {
                realStartY = (selectedShape.getEndY() > selectedShape.getStartY()) ?
                        selectedShape.getEndY() : selectedShape.getStartY();
                if (selectedShape.getSettings().isFilled()) {
                    gc.fillText(controller.text_field_text_input.getText(), realStartX, realStartY, width);
                }
                gc.strokeText(controller.text_field_text_input.getText(), realStartX, realStartY, width);

                gc.restore();
            }
            break;
            default:
                break;
        }
    }

    /* Draw Selected MyShape Object
     * this function will be called in select.move model */
    private void moveDrawShape(MyShape selectedShape, double dx, double dy) {
        double width = (selectedShape.getEndX() > selectedShape.getStartX()) ?
                (selectedShape.getEndX() - selectedShape.getStartX()) :
                (selectedShape.getStartX() - selectedShape.getEndX());
        double height = (selectedShape.getEndY() > selectedShape.getStartY()) ?
                (selectedShape.getEndY() - selectedShape.getStartY()) :
                (selectedShape.getStartY() - selectedShape.getEndY());
        double realStartX = (selectedShape.getEndX() > selectedShape.getStartX()) ?
                selectedShape.getStartX() : selectedShape.getEndX();
        double realStartY = (selectedShape.getEndY() > selectedShape.getStartY()) ?
                selectedShape.getStartY() : selectedShape.getEndY();

        realStartX += dx;
        realStartY += dy;

        String strokeColorString = selectedShape.getSettings().getStrokeColorString();
        String fillColorString = selectedShape.getSettings().getFillColorString();
        gc.setStroke(Color.web(strokeColorString));
        gc.setFill(Color.web(fillColorString));
        gc.setLineWidth(selectedShape.getSettings().getLineWidth());
        gc.setFont(new Font("font", selectedShape.getSettings().getFontSize()));

        switch (selectedShape.getSettings().getShape()) {
            case Settings.SETTINGS_SHAPE_LINE:
            {
                gc.strokeLine(selectedShape.getStartX() + dx, selectedShape.getStartY() + dy,
                        selectedShape.getEndX() + dx, selectedShape.getEndY() + dy);
                gc.restore();
            }
            break;
            case Settings.SETTINGS_SHAPE_RECTANGLE:
            {
                if (selectedShape.getSettings().isFilled()) {
                    gc.fillRect(realStartX, realStartY, width, height);
                }
                gc.strokeRect(realStartX, realStartY, width, height);

                gc.restore();
            }
            break;
            case Settings.SETTINGS_SHAPE_CIRCLE:
            {
                if (selectedShape.getSettings().isFilled()) {
                    gc.fillOval(realStartX, realStartY, width, height);
                }
                gc.strokeOval(realStartX, realStartY, width, height);

                gc.restore();
            }
            break;
            case Settings.SETTINGS_SHAPE_TEXT:
            {
                realStartY = (selectedShape.getEndY() > selectedShape.getStartY()) ?
                        selectedShape.getEndY() : selectedShape.getStartY();
                realStartY += dy;
                if (selectedShape.getSettings().isFilled()) {
                    gc.fillText(selectedShape.getText(), realStartX, realStartY, width);
                }
                gc.strokeText(selectedShape.getText(), realStartX, realStartY, width);

                gc.restore();
            }
            break;
            default:
                break;
        }

    }

    /* Draw Selected MyShape Object
     * this function will be called in select.resize model */
    private void resizeDrawShape(MyShape selectedShape, double endX, double endY) {
        double width = (endX > selectedShape.getStartX()) ?
                (endX - selectedShape.getStartX()) : (selectedShape.getStartX() - endX);
        double height = (endY > selectedShape.getStartY()) ?
                (endY - selectedShape.getStartY()) : (selectedShape.getStartY() - endY);
        double realStartX = (endX > selectedShape.getStartX()) ? selectedShape.getStartX() : endX;
        double realStartY = (endY > selectedShape.getStartY()) ? selectedShape.getStartY() : endY;


        String strokeColorString = selectedShape.getSettings().getStrokeColorString();
        String fillColorString = selectedShape.getSettings().getFillColorString();
        gc.setStroke(Color.web(strokeColorString));
        gc.setFill(Color.web(fillColorString));
        gc.setLineWidth(selectedShape.getSettings().getLineWidth());
        gc.setFont(new Font("font", selectedShape.getSettings().getFontSize()));

        switch (selectedShape.getSettings().getShape()) {
            case Settings.SETTINGS_SHAPE_LINE:
            {
                gc.strokeLine(selectedShape.getStartX(), selectedShape.getStartY(), endX, endY);
                gc.restore();
            }
            break;
            case Settings.SETTINGS_SHAPE_RECTANGLE:
            {
                if (selectedShape.getSettings().isFilled()) {
                    gc.fillRect(realStartX, realStartY, width, height);
                }
                gc.strokeRect(realStartX, realStartY, width, height);

                gc.restore();
            }
            break;
            case Settings.SETTINGS_SHAPE_CIRCLE:
            {
                if (selectedShape.getSettings().isFilled()) {
                    gc.fillOval(realStartX, realStartY, width, height);
                }
                gc.strokeOval(realStartX, realStartY, width, height);

                gc.restore();
            }
            break;
            case Settings.SETTINGS_SHAPE_TEXT:
            {
                realStartY = (endY > selectedShape.getStartY()) ? endY : selectedShape.getStartY();
                if (selectedShape.getSettings().isFilled()) {
                    gc.fillText(controller.text_field_text_input.getText(), realStartX, realStartY, width);
                }
                gc.strokeText(controller.text_field_text_input.getText(), realStartX, realStartY, width);

                gc.restore();
            }
            break;
            default:
                break;
        }
    }

    /* Open the specific cad file
     * 1. clear the stack
     * 2. read in the MyShape Objects
     * 3. push the Objects into the stack
     * 4. paint all MyShapes Objects in order on the empty canvas
     * */
    private void open(File file) {
        FileInputStream fileIn = null;
        int count;

        myShapeStack.clear();

        /* remove all canvas from the stack pane children, but remain the background */
        while (controller.stack_pane_canvas.getChildren().size()>1) {
            controller.stack_pane_canvas.getChildren().remove(controller.stack_pane_canvas.getChildren().get(1));
        }
        try {
            fileIn = new FileInputStream(file);
            ObjectInputStream input = new ObjectInputStream(fileIn);
            MyShape myShape;
            myShape = (MyShape)input.readObject();
            while (true) {
                myShapeStack.push(myShape);
                openDrawShape(myShape);
                myShape = (MyShape)input.readObject();
                //123
                if (myShape.getSettings().getShape() == Settings.SETTINGS_SHAPE_TEXT) {
                    System.out.println(myShape.getText());
                }
            }
        } catch (FileNotFoundException | ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* Save the current MyShape Objects and the canvas into a specific cad file */
    private void save(String name) {
        try (ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(name))) {
            for (int i=1; i<=myShapeStack.size(); i++) {
                output.writeObject(myShapeStack.get(i));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        primaryStage.setTitle(name);
    }

    /* Draw the MyShape Object
     * This method will be called when open a existing cad file
     * and paint the MyShape Objects of it */
    private void openDrawShape(MyShape myShape) {
        double dx = myShape.getEndX() - myShape.getStartX();
        double dy = myShape.getEndY() - myShape.getStartY();

        double width = (dx > 0) ? dx : -dx;
        double height = (dy > 0) ? dy : -dy;
        double realStartX = (dx > 0) ? myShape.getStartX() : myShape.getEndX();
        double realStartY = (dy > 0) ? myShape.getStartY() : myShape.getEndY();
        double realEndX = (dx > 0) ? myShape.getEndX() : myShape.getStartX();
        double realEndY = (dy > 0) ? myShape.getEndY() : myShape.getStartY();

        Canvas canvas = new Canvas(1000, 800);
        gc = canvas.getGraphicsContext2D();
        gc.setLineWidth(myShape.getSettings().getLineWidth());
        gc.setStroke(Color.web(myShape.getSettings().getStrokeColorString()));
        gc.setFill(Color.web(myShape.getSettings().getFillColorString()));
        gc.setFont(new Font("font", myShape.getSettings().getFontSize()));

        switch (myShape.getSettings().getShape()) {
            case Settings.SETTINGS_SHAPE_LINE:
            {
                gc.strokeLine(myShape.getStartX(), myShape.getStartY(),
                        myShape.getEndX(), myShape.getEndY());
            }
            break;
            case Settings.SETTINGS_SHAPE_RECTANGLE:
            {
                if (myShape.getSettings().isFilled()) {
                    gc.fillRect(realStartX, realStartY, width, height);
                }
                gc.strokeRect(realStartX, realStartY, width, height);
            }
            break;
            case Settings.SETTINGS_SHAPE_CIRCLE:
            {
                if (myShape.getSettings().isFilled()) {
                    gc.fillOval(realStartX, realStartY, width, height);
                }
                gc.strokeOval(realStartX, realStartY, width, height);
            }
            break;
            case Settings.SETTINGS_SHAPE_TEXT:
            {
                realStartY = (dy > 0) ? myShape.getEndY() : myShape.getStartY();
                if (myShape.getSettings().isFilled()) {
                    gc.fillText(myShape.getText(), realStartX, realStartY);
                }
                gc.strokeText(myShape.getText(), realStartX, realStartY);
            }
            break;
            default:
                break;
        }
        controller.stack_pane_canvas.getChildren().add(canvas);
    }

    private void keyBoardEventHandler(KeyEvent event) {
        /* select*/
        if (settings.getModel() == Settings.SETTINGS_MODEL_SELECT) {
            /* move */
            if (settings.getEdit() == Settings.SETTING_EDIT_MOVE) {
                double dx = 0.0, dy = 0.0;

                if (event.getCode() == KeyCode.UP) {
                    dx = 0.0;
                    dy = -5.0;
                }
                if (event.getCode() == KeyCode.DOWN) {
                    dx = 0.0;
                    dy = 5.0;
                }
                if (event.getCode() == KeyCode.LEFT) {
                    dx = -5.0;
                    dy = 0.0;
                }
                if (event.getCode() == KeyCode.RIGHT) {
                    dx = 5.0;
                    dy = 0.0;
                }

                gc.clearRect(0.0, 0.0, 1000.0, 800.0);
                moveDrawShape(selectedShape, dx, dy);

                // update information of the shape in stack
                selectedShape.setStartX(selectedShape.getStartX()+dx);
                selectedShape.setStartY(selectedShape.getStartY()+dy);
                selectedShape.setEndX(selectedShape.getEndX()+dx);
                selectedShape.setEndY(selectedShape.getEndY()+dy);

                drawBorder(gc, selectedShape.getStartX(), selectedShape.getStartY(),
                        selectedShape.getEndX(), selectedShape.getEndY(),
                        selectedShape.getSettings().getShape()==Settings.SETTINGS_SHAPE_TEXT);
            }
            /* edit */
            if (settings.getEdit() == Settings.SETTING_EDIT_EDIT) {
                Double newLineWidth = selectedShape.getSettings().getLineWidth();
                Integer newFontSize = selectedShape.getSettings().getFontSize();

                if (event.getCode() == KeyCode.UP) {
                    newLineWidth += 1.0;
                }
                if (event.getCode() == KeyCode.DOWN) {
                    newLineWidth -= 1.0;
                }

                if (selectedShape.getSettings().getShape() == Settings.SETTINGS_SHAPE_TEXT) {
                    if (event.getCode() == KeyCode.RIGHT) {
                        newFontSize += 2;
                    }
                    if (event.getCode() == KeyCode.LEFT) {
                        newFontSize -= 2;
                    }
                    selectedShape.getSettings().setLineWidth(newLineWidth);
                }
                gc.clearRect(0.0, 0.0, 1000.0, 800.0);
                editDrawShape(selectedShape);

                // update information of the shape in stack
                settings.setLineWidth(newLineWidth);
                controller.text_field_line_width.setText(newLineWidth.toString());

                drawBorder(gc, selectedShape.getStartX(), selectedShape.getStartY(),
                        selectedShape.getEndX(), selectedShape.getEndY(),
                        selectedShape.getSettings().getShape()==Settings.SETTINGS_SHAPE_TEXT);
            }
        }
    }
}
