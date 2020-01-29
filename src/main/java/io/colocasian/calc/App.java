package io.colocasian.calc;

import io.colocasian.math.Expression;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

/**
 * JavaFX App
 */
public class App extends Application {
    private static Expression solver = new Expression();
    private boolean sPanelOff;
    private int activeGrid;

    @Override
    public void start(Stage stage) {
        stage.setTitle("OreNo Calculator [GUI]");

        // Constants section
        int unit = 48;
        int gridGap = 4;
        int gridPad = 8;
        int gridCols = 6;
        int gridSpace = gridCols * unit + (gridCols - 1) * gridGap + 2 * gridPad;

        int dispGap = 4;
        int dispPad = 8;

        int sPanelWidth = 54;
        int sPanelHeight = 36;
        int sPanelGap = 3;
        int sPanelPad = 8;
        int sPanelSpace = sPanelWidth + 2 * sPanelPad;

        // Display ---------------(NEW LAYOUT)--------------

        // Input field
        TextField tfInput = new TextField();
        tfInput.setFont(Font.font("monospace", FontWeight.NORMAL, 18));
        tfInput.setPrefWidth(4 * unit + 3 * gridGap);
        tfInput.setMaxWidth(4 * unit + 3 * gridGap);
        tfInput.setMinWidth(4 * unit + 3 * gridGap);

        // Output field
        TextField tfOutput = new TextField();
        tfOutput.setFont(Font.font("monospace", FontWeight.BOLD, 26));
        //tfOutput.setPrefWidth(6 * unit + 5 * gridGap);
        //tfOutput.setMaxWidth(6 * unit + 5 * gridGap);
        tfOutput.setMinWidth(6 * unit + 5 * gridGap);
        tfOutput.setEditable(false);

        // Output panel
        VBox display = new VBox(dispGap, tfInput, tfOutput);
        display.setPadding(new Insets(dispPad, dispPad, dispGap, dispPad));

        // Regular Calculator --------------(NEW LAYOUT)---------------

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
        btnNums[0].setPrefWidth(2 * unit + gridGap);

        // Decimal point and Exponent keys
        Button btnDot = new Button(".");
        btnDot.setPrefSize(unit, unit);

        btnDot.setOnAction(e -> { tfInput.appendText("."); });

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
        grid.setPadding(new Insets(0, gridPad, gridPad, gridPad));
        grid.setVgap(gridGap);
        grid.setHgap(gridGap);

        // Setting the keys
        grid.add(btnNums[0], 0, 3, 2, 1);
        grid.add(btnNums[1], 0, 2, 1, 1);
        grid.add(btnNums[2], 1, 2, 1, 1);
        grid.add(btnNums[3], 2, 2, 1, 1);
        grid.add(btnNums[4], 0, 1, 1, 1);
        grid.add(btnNums[5], 1, 1, 1, 1);
        grid.add(btnNums[6], 2, 1, 1, 1);
        grid.add(btnNums[7], 0, 0, 1, 1);
        grid.add(btnNums[8], 1, 0, 1, 1);
        grid.add(btnNums[9], 2, 0, 1, 1);

        grid.add(btnDot, 2, 3, 1, 1);

        grid.add(btnAdd, 3, 2, 1, 1);
        grid.add(btnSub, 4, 2, 1, 1);
        grid.add(btnMul, 3, 1, 1, 1);
        grid.add(btnDiv, 4, 1, 1, 1);
        grid.add(btnPow, 5, 2, 1, 1);

        grid.add(btnBro, 5, 0, 1, 1);
        grid.add(btnBrc, 5, 1, 1, 1);

        grid.add(btnEqu, 3, 3, 1, 1);
        grid.add(btnAns, 4, 3, 1, 1);

        grid.add(btnAC, 3, 0, 1, 1);
        grid.add(btnCE, 4, 0, 1, 1);

        // Scientific grid ---------------------(NEW LAYOUT)--------------------
        Button btnJ = new Button("J");
        btnJ.setPrefSize(unit, unit);
        Button btnU = new Button("U");
        btnU.setPrefSize(unit, unit);
        Button btnS = new Button("S");
        btnS.setPrefSize(unit, unit);
        Button btnT = new Button("T");
        btnT.setPrefSize(unit, unit);
        Button btnR = new Button("R");
        btnR.setPrefSize(unit, unit);
        Button btnY = new Button("Y");
        btnY.setPrefSize(unit, unit);
        Button btnI = new Button("I");
        btnI.setPrefSize(unit, unit);
        Button btnN = new Button("N");
        btnN.setPrefSize(unit, unit);
        Button btnG = new Button("G");
        btnG.setPrefSize(unit, unit);

        // Grid Panel
        GridPane sciGrid = new GridPane();
        sciGrid.setPadding(new Insets(0, gridPad, gridPad, gridPad));
        sciGrid.setVgap(gridGap);
        sciGrid.setHgap(gridGap);

        // Button setting
        sciGrid.add(btnJ, 0, 3, 1, 1);
        sciGrid.add(btnU, 0, 2, 1, 1);
        sciGrid.add(btnS, 0, 1, 1, 1);
        sciGrid.add(btnT, 0, 0, 1, 1);
        sciGrid.add(btnR, 1, 0, 1, 1);
        sciGrid.add(btnY, 2, 0, 1, 1);
        sciGrid.add(btnI, 3, 0, 1, 1);
        sciGrid.add(btnN, 4, 0, 1, 1);
        sciGrid.add(btnG, 5, 0, 1, 1);

        // BorderPane for final setup --------------(NEW LAYOUT)---------------
        BorderPane panel = new BorderPane();

        // Side panel buttons and layout ------------(NEW LAYOUT)---------------
        sPanelOff = true;
        activeGrid = 0;

        Button btnReg = new Button("Reg");
        Button btnSci = new Button("Sci");
        btnReg.setPrefSize(sPanelWidth, sPanelHeight);
        btnSci.setPrefSize(sPanelWidth, sPanelHeight);

        btnReg.setOnAction(e -> {
            if (activeGrid != 0) {
                panel.setCenter(grid);
                activeGrid = 0;
            }
        });

        btnSci.setOnAction(e -> {
            if (activeGrid != 1) {
                panel.setCenter(sciGrid);
                activeGrid = 1;
            }
        });

        Button btnCyc = new Button("↻");
        btnCyc.setPrefSize(sPanelWidth, sPanelHeight);

        btnCyc.setOnAction(e -> {
            switch (activeGrid) {
                case 0:
                    panel.setCenter(sciGrid);
                    activeGrid = 1;
                    break;
                case 1:
                    panel.setCenter(grid);
                    activeGrid = 0;
                    break;
            }
        });

        VBox sPanel = new VBox(sPanelGap, btnReg, btnSci, btnCyc);
        sPanel.setPadding(new Insets(0, sPanelPad, sPanelPad, 0));

        // Panel changing buttons on main grid
        Button[] btnSwp = new Button[2];
        for (int i = 0; i < 2; i++) {
            btnSwp[i] = new Button(">>");
            btnSwp[i].setPrefSize(unit, unit);

            btnSwp[i].setOnAction(e -> {
                if (sPanelOff) {
                    stage.setWidth(gridSpace + sPanelSpace);
                    panel.setRight(sPanel);
                    for (int j = 0; j < 2; j++)
                        btnSwp[j].setText("<<");
                }
                else {
                    stage.setWidth(gridSpace);
                    panel.setRight(null);
                    for (int j = 0; j < 2; j++)
                        btnSwp[j].setText(">>");
                }
                sPanelOff = !sPanelOff;
            });
        }

        grid.add(btnSwp[0], 5, 3, 1, 1);
        sciGrid.add(btnSwp[1], 5, 3, 1, 1);

        panel.setTop(display);
        // panel.setCenter(sciGrid);
        switch (activeGrid) {
            case 0:
                panel.setCenter(grid);
                break;
            case 1:
                panel.setCenter(sciGrid);
                break;
            default:
                panel.setCenter(grid);
                break;
        }
        Scene scene = new Scene(panel);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
