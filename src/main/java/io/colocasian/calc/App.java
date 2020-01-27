package io.colocasian.calc;

import io.colocasian.math.Expression;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

/**
 * JavaFX App
 */
public class App extends Application {
    private static Expression solver = new Expression();

    @Override
    public void start(Stage stage) {
        stage.setTitle("OreNo Calculator [GUI]");

        // Unit size for a button
        int unit = 48;

        // Input field
        TextField tfInput = new TextField();
        tfInput.setFont(Font.font("monospace", FontWeight.NORMAL, 18));

        //Output field
        TextField tfOutput = new TextField();
        tfOutput.setFont(Font.font("monospace", FontWeight.BOLD, 26));
        tfOutput.setEditable(false);

        // Numeric keys
        Button btnNums[] = new Button[10];
        for (int i = 0; i < 10; i++) {
            final int x = i;
            btnNums[x] = new Button(Integer.toString(x));
            btnNums[x].setPrefSize(unit, unit);

            btnNums[x].setOnAction(e -> {
                tfInput.appendText(Integer.toString(x));
            });
        }

        // Decimal point and Exponent keys
        Button btnDot = new Button(".");
        Button btnExp = new Button("EXP");
        btnDot.setPrefSize(unit, unit);
        btnExp.setPrefSize(unit, unit);

        btnDot.setOnAction(e -> { tfInput.appendText("."); });
        btnExp.setOnAction(e -> { tfInput.appendText("e"); });

        // Common operators
        Button btnAdd = new Button("+");
        Button btnSub = new Button("-");
        Button btnMul = new Button("*");
        Button btnDiv = new Button("/");
        Button btnPow = new Button("^");
        btnAdd.setPrefSize(unit, unit);
        btnSub.setPrefSize(unit, unit);
        btnMul.setPrefSize(unit, unit);
        btnDiv.setPrefSize(unit, unit);
        btnPow.setPrefSize(unit, unit);

        btnAdd.setOnAction(e -> { tfInput.appendText("+"); });
        btnSub.setOnAction(e -> { tfInput.appendText("-"); });
        btnMul.setOnAction(e -> { tfInput.appendText("*"); });
        btnDiv.setOnAction(e -> { tfInput.appendText("/"); });
        btnPow.setOnAction(e -> { tfInput.appendText("^"); });

        // Parentheses key
        Button btnBro = new Button("(");
        Button btnBrc = new Button(")");
        btnBro.setPrefSize(unit, unit);
        btnBrc.setPrefSize(unit, unit);

        btnBro.setOnAction(e -> { tfInput.appendText("("); });
        btnBrc.setOnAction(e -> { tfInput.appendText(")"); });

        // Equals key and Ans key
        Button btnEqu = new Button("=");
        Button btnAns = new Button("Ans");
        btnEqu.setPrefSize(unit, unit);
        btnAns.setPrefSize(unit, unit);

        btnEqu.setOnAction(e -> {
            try {
                tfOutput.setText(Double.toString(solver.evaluate(tfInput.getText()).doubleValue()));
            }
            catch (ArithmeticException aerror) {
                tfOutput.setText("EE:" + aerror.getMessage());
            }
            catch (Exception what) {
                tfOutput.setText("UE:" + what.getMessage());
            }
        });

        // AC and CE
        Button btnAC = new Button("AC");
        Button btnCE = new Button("CE");
        btnAC.setPrefSize(unit, unit);
        btnCE.setPrefSize(unit, unit);

        btnAC.setOnAction(e -> {
            tfInput.clear();
            tfOutput.clear();
        });

        btnCE.setOnAction(e -> {
            String initText = tfInput.getText();
            if (!initText.isEmpty())
                tfInput.setText(initText.substring(0, initText.length()-1));
        });

        // GridPane layout
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(8, 8, 8, 8));
        grid.setVgap(4);
        grid.setHgap(4);

        // Setting the keys
        grid.add(btnNums[0], 0, 5, 1, 1);
        grid.add(btnNums[1], 0, 4, 1, 1);
        grid.add(btnNums[2], 1, 4, 1, 1);
        grid.add(btnNums[3], 2, 4, 1, 1);
        grid.add(btnNums[4], 0, 3, 1, 1);
        grid.add(btnNums[5], 1, 3, 1, 1);
        grid.add(btnNums[6], 2, 3, 1, 1);
        grid.add(btnNums[7], 0, 2, 1, 1);
        grid.add(btnNums[8], 1, 2, 1, 1);
        grid.add(btnNums[9], 2, 2, 1, 1);

        grid.add(btnDot, 1, 5, 1, 1);
        grid.add(btnExp, 2, 5, 1, 1);

        grid.add(btnAdd, 3, 4, 1, 1);
        grid.add(btnSub, 4, 4, 1, 1);
        grid.add(btnMul, 3, 3, 1, 1);
        grid.add(btnDiv, 4, 3, 1, 1);
        grid.add(btnPow, 5, 4, 1, 1);

        grid.add(btnBro, 5, 2, 1, 1);
        grid.add(btnBrc, 5, 3, 1, 1);

        grid.add(btnEqu, 3, 5, 1, 1);
        grid.add(btnAns, 4, 5, 1, 1);

        grid.add(btnAC, 3, 2, 1, 1);
        grid.add(btnCE, 4, 2, 1, 1);

        grid.add(tfInput, 0, 0, 4, 1);
        grid.add(tfOutput, 0, 1, 6, 1);

        Scene scene = new Scene(grid);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
