package DnDCharacterSheet;

import java.lang.reflect.Field;
import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.swing.*;


public class CharacterSheetController {

    CharacterSheet characterSheet = new CharacterSheet();

    //for testing only
    ObservableList<String> list = FXCollections.observableArrayList(
            "Asparagus", "Beans", "Broccoli", "Cabbage", "Carrot",
            "Celery", "Cucumber", "Leek", "Mushroom", "Pepper",
            "Radish", "Shallot", "Spinach", "Swede", "Turnip");


    @FXML
    VBox spellPaneLevel0;
    @FXML
    ComboBox spellLevel0Box;
    @FXML
    GridPane spellLevel1Grid;


    ArrayList<HBox> spellLevel0HBoxList = new ArrayList<>();

    @FXML
    private void initialize() {
        spellLevel0Box.setItems(list);
    }

    public void SetValue(KeyEvent event) throws NoSuchFieldException, IllegalAccessException, ClassNotFoundException {

        /**************************
         **** DEFINE VARIABLES ****
         **************************/
        // Used to reset the caret position after filter.
        int caretPos = ((TextInputControl) event.getSource()).getCaretPosition();

        // Get the id of the control
        String id = ((Control) event.getSource()).getId();
        // Get the value in the field
        String text = ((TextInputControl) event.getSource()).getText();

        // The field name and data type are separated by an underscore
        String[] values = id.split("_", 3);
        // The field is the first value,
        String field = values[0];
        // And the datatype is the second.
        String dataType = values[1];
        // Get the other instance of the field if it exists
        boolean instance = false;
        if (values.length > 2) {
            instance = !Boolean.parseBoolean(values[2]);
        }

        // Get the field we're editing
        Field targetField = null;
        Field subclassField = null;
        // Check to see if we're referencing a subclass
        // The field name and data type are separated by an underscore
        String[] subclasses = field.split("-", 2);

        /**************************
         *** GET SELECTED FIELD ***
         **************************/
        if (subclasses.length > 1) {
            System.out.println("Set " + subclasses[0] + "." + subclasses[1] + ".");
            // The subclass is the first value,
            String subclass = subclasses[0];
            field = subclasses[1];
            // And its field is the second.
            subclassField = (CharacterSheet.class.getDeclaredField(subclass));
            targetField = (CharacterSheet.class.getDeclaredField(subclass)).getType().getDeclaredField(field);
        } else {
            // Get the field we're editing
            targetField = CharacterSheet.class.getDeclaredField(field);
        }


        // Filter text values
        text = Filter(subclassField, targetField, text, dataType);

        /**************************
         *** SET ALL CONTROL VALS *
         **************************/
        Scene scene = ((Control) event.getSource()).getScene();

        // Assign value to original field.
        ((TextInputControl) event.getSource()).setText(text);

        if (values.length > 2) {
            // Set the other field. (Use !instance to find the other field.)
            ((TextField) scene.lookup("#" + values[0] + "_" + dataType + "_" + !instance)).setText(text);
        }

        // Reposition the caret
        ((TextInputControl) event.getSource()).positionCaret(caretPos);
    }

    public String Filter(Field subclass, Field field, String text, String dataType) throws IllegalAccessException, NoSuchFieldException {

        field.setAccessible(true);
        Object value = null;
        String element = "";
        // Get array element
        if (dataType.contains("array")) {
            element = dataType.split("-")[1];
            dataType = dataType.split("-")[0];
        }

        System.out.println("Data type: " + dataType + "; fieldName: " + field.getName());
        // Ensure text meets requirements
        switch (dataType) {
            case "int":
                // Remove all non-numbers..
                // if(!text.equals("-")) {
                text = text.replaceFirst("[^\\d-]", "");
                if ((text.length() > 1)) {
                    String textsub = text.substring(1);
                    text = text.substring(0, 1) + textsub.replaceAll("[^\\d]", "");
                }
                System.out.println("Text is: " + text);
                // Make sure there is at least one number in the field
                if (!text.equals("") && !text.equals("-")) {
                    value = Integer.parseInt(text);
                    // }
                }
                break;
            case "float":
                // Remove all non-numbers..
                text = text.replaceAll("[^\\d.]", "");
                // Make sure there is at least one number in the field
                if (!text.equals("")) {
                    value = Float.parseFloat(text);
                }
                break;
            case "str":
                value = text;
                break;
        }

        if (value != null) {
            text = value.toString();
            // Set Value
            if (subclass != null & value != null) {
                // Make the subclass accessible if it's not null
                subclass.setAccessible(true);
                // Make a copy of the subclass variable within CharacterSheet
                //System.out.println("Subclass: " + subclass.getName());
                Object obj = subclass.get(characterSheet);
                //System.out.println("obj: " + obj.toString());
                // Change the subclass instantiation to have our new value
                field.set(obj, value);
                // Reset the subclass instantiation within character sheet
                subclass.set(characterSheet, obj);
            } else {
                field.set(characterSheet, value);
            }
        }

        return text;
    }

    public void SetArrayValue(KeyEvent event) throws NoSuchFieldException, IllegalAccessException {
        //subclass-field_element1-element2

        /**************************
         **** DEFINE VARIABLES ****
         **************************/
        // Used to reset the caret position after filter.
        Scene scene = ((Control) event.getSource()).getScene();
        int caretPos = ((TextInputControl) event.getSource()).getCaretPosition();
        String id = ((Control) event.getSource()).getId(); // Get the id of the control
        String text = ((TextInputControl) event.getSource()).getText(); // Get the value in the field
        String[] values = id.replace("array-", "").split("_", 3); // The field name and data type are separated by an underscore
        String field = values[0]; // The field is the first value,
        String subclass = field.split("-")[0];
        field = field.split("-")[1];
        int value = 0;

        // Get the value to assign
        // Remove all non-numbers..
        text = text.replaceAll("[^\\d]", "");

        // Make sure there is at least one number in the field
        if (text != "" && text != null) {


            if (!text.equals("")) {
                value = Integer.parseInt(text);
            }

            // And the datatype is the second.
            String[] elements = values[1].split("-");
            // Set 2-Dimensional Array
            if (elements.length > 1) {
                characterSheet.setStat(Integer.parseInt(elements[0]), Integer.parseInt(elements[1]), value);
                // now that it's set let's update the mod
                ((TextField) scene.lookup("#" + subclass + "-" + field + "_" + elements[0] + "-" + "1")).setText(String.valueOf(characterSheet.getStats().getStat(Integer.parseInt(elements[0]), 1)));
                // now update the total
                ((TextField) scene.lookup("#" + subclass + "-" + field + "_" + elements[0] + "-" + "0")).setText(String.valueOf(characterSheet.getStats().getStat(Integer.parseInt(elements[0]), 0)));

            }
            // Set 1-Dimensional Array
            else {
                Field targetField = (CharacterSheet.class.getDeclaredField(subclass)).getType().getDeclaredField(field);
                targetField.setAccessible(true);

                // Set Value
                if (subclass != null) {
                    Field subclassField = (CharacterSheet.class.getDeclaredField(subclass));
                    subclassField.setAccessible(true);

                    // Get value of target field
                    int[] temp = (int[]) targetField.get(subclassField.get(characterSheet));
                    // Change the value of the copy
                    temp[Integer.parseInt(elements[0])] = value;
                    // Set the target field to hold the copied object
                    targetField.set(subclassField.get(characterSheet), temp);
                } else {
                    // Make a copy of the target field within CharacterSheet
                    int[] temp = (int[]) targetField.get(characterSheet);
                    // Set the target field to hold our new value
                    temp[Integer.parseInt(elements[0])] = value;
                    // Set the field to hold the copy object
                    targetField.set(characterSheet, temp);
                }
            }
        }

        System.out.println("Misc stats AC value: " + characterSheet.getMiscStats().getAC()[3]);

        // Assign value to original field.
        ((TextInputControl) event.getSource()).setText(text);

        // Reposition the caret
        ((TextInputControl) event.getSource()).positionCaret(caretPos);
    }

    public void setHP(KeyEvent event) throws IllegalAccessException, NoSuchFieldException, ClassNotFoundException {

        Scene scene = ((Control) event.getSource()).getScene();
        String id = ((Control) event.getSource()).getId();

        // Check if we're dealing damage, because that's a different thing entirely
        if (!id.equals("damagefield")) {
            // set the value in the GUI
            SetValue(event);
            // if we're NOT typing in the currentHP box, make sure it gets updated
            if (!id.equals("hitPoints-currentHP_int")) {
                ((TextField) scene.lookup("#hitPoints-currentHP_int")).setText(String.valueOf(characterSheet.getHp().getCurrentHP()));
            }

            if (!id.equals("#hitPoints-tempHP_int")) {
                // ((TextField) scene.lookup("#hitPoints-tempHP_int")).setText(String.valueOf(characterSheet.getHp().getTempHP()));
            }
        } else if (id.equals("damagefield")) {

            // basically writing a truncated version of setValue with Filter here
            int caretPos = ((TextInputControl) event.getSource()).getCaretPosition();
            String text = ((TextInputControl) event.getSource()).getText();

            // make sure it's not blank to avoid errors
            if (text != "" && text != null) {
                //Check to see if we're entering a negative number
                // Allow a dash for the first character
                text = text.replaceFirst("[^\\d-]", "");
                // If the length of the string is greater than 1, then replace everything NOT an integer
                if ((text.length() > 1)) {
                    String textsub = text.substring(1);
                    text = text.substring(0, 1) + textsub.replaceAll("[^\\d]", "");
                }

                System.out.println("Text is: " + text);

                ((TextField) scene.lookup("#damagefield")).setText(text);
                ((TextInputControl) event.getSource()).positionCaret(caretPos);


                if (event.getCode() == KeyCode.ENTER) {
                    // set the current hp
                    characterSheet.getHp().changeCurrentHP(Integer.parseInt(text));
                    // clear the damage field
                    ((TextField) scene.lookup("#damagefield")).setText("");
                    // set the temp HP in case they had temp HP
                    ((TextField) scene.lookup("#hitPoints-tempHP_int")).setText(String.valueOf(characterSheet.getHp().getTempHP()));
                    // set the current HP in case they did not have temp HP
                    ((TextField) scene.lookup("#hitPoints-currentHP_int")).setText(String.valueOf(characterSheet.getHp().getCurrentHP()));
                }
            }
        }

        ((ProgressBar) scene.lookup("#hpBar")).setProgress((float) Math.abs(Math.abs(characterSheet.getHp().getBleedOut()) + characterSheet.getHp().getCurrentHP()) / (Math.abs(characterSheet.getHp().getBleedOut()) + characterSheet.getHp().getMaxHP()));

    }

    public void dynamicSpellAdder(ActionEvent event) {

        /*
        //get the number of children in the box minus 1 since it already has a box in it
        int index = spellPaneLevel0.getChildren().size() - 1;

        System.out.println("The size of the VBox is: " + index);
        // add the HBox to the arraylist so we can keep track of it
        spellLevel0HBoxList.add(new HBox(new ComboBox(list)));
        // find the HBox in the arraylist then the ComboBox so we can set the combobox's values
        ((ComboBox) ((spellLevel0HBoxList.get(index).getChildren()).get(0))).setEditable(true);
        ((ComboBox) ((spellLevel0HBoxList.get(index).getChildren()).get(0))).setPrefWidth(770);
        ((ComboBox) ((spellLevel0HBoxList.get(index).getChildren()).get(0))).setMaxWidth(1.7976931348623157E308);
        // add the member of the arraylist to the GUI
        spellPaneLevel0.getChildren().add(spellLevel0HBoxList.get(index));
        // disable the previous box
        */

        /*
        ComboBox temp = new ComboBox(list);
        temp.setEditable(true);
        temp.setPrefWidth(770);
        temp.setMaxWidth(1.7976931348623157E308);
        */

        int rowindex = spellLevel1Grid.getRowCount();

        // add new items to list
        spellLevel1Grid.add(new TextField(), 0, rowindex, 1, 1);
        spellLevel1Grid.add(new ComboBox(list), 1, rowindex, 1, 1);
        spellLevel1Grid.add(new Button("X"), 2, rowindex, 1, 1);
       // spellLevel1Grid.getRowConstraints().add(rowindex, new RowConstraints(30));
                //set(rowindex, new RowConstraints(30));
                //add(new RowConstraints(30));


        // get all the fields we're currently working with to format them
        TextField currentfield = (TextField) getNodeFromGridPane(spellLevel1Grid, 0, rowindex);
        ComboBox currentbox = (ComboBox) getNodeFromGridPane(spellLevel1Grid, 1, rowindex);
        Button currentbutton = (Button) getNodeFromGridPane(spellLevel1Grid, 2, rowindex);

        //format the items
        currentbox.setEditable(true);
        currentbox.setPrefWidth(770);
        currentbox.setMaxWidth(1.7976931348623157E308);
        //spellLevel1Grid.setMargin(currentbox, new Insets(3, 0, 0, 0));

       currentbutton.setOnAction(new EventHandler<ActionEvent>() {
           @Override public void handle(ActionEvent e) {

              spellLevel1Grid.getChildren().removeIf(node -> GridPane.getRowIndex(node) != null && GridPane.getRowIndex(node) == rowindex);
       }
       });
    }

    private Node getNodeFromGridPane(GridPane gridPane, int col, int row) {
        ObservableList<Node> children = gridPane.getChildren();

        // step through the entire list of children and check
        // No, finding a node by its location in a gridpane is not natively supported by Java for some reason
        for (Node node : children) {
            if (GridPane.getColumnIndex(node) != null && GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) != null && GridPane.getRowIndex(node) == row) {
                return node;
            }
        }
        return null;

    }
}
