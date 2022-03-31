import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Controller {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button exportBtn;

    @FXML
    private GridPane grid;

    @FXML
    private Button importBtn;

    @FXML
    private Label labelTesto;

    @FXML
    private Button solve;

    @FXML
    private ComboBox<String> selezioneAlgoritmo;

    private Main main;
    private Mappa map;
    private Pane[][] gridMatrix;

    public void setMain(Main main) {
        this.main = main;
    }

    public void drawMap(Mappa map) {
        //grid.getChildren().clear();
        gridMatrix = new Pane[map.altezza][map.larghezza];
        while(grid.getRowConstraints().size() > 0){
            grid.getRowConstraints().remove(0);
        }

        while(grid.getColumnConstraints().size() > 0){
            grid.getColumnConstraints().remove(0);
        }
        for (int i = 0; i < map.altezza; i++) {
            for (int j = 0; j < map.larghezza; j++) {
                Pane pane = new Pane();
                gridMatrix[i][j] = pane;
                // ------ DEBUG ON CLICK---------
                pane.setOnMouseClicked(e -> {
                    int x = GridPane.getRowIndex(pane);
                    int y = GridPane.getColumnIndex(pane);
                    System.out.println("Posizione: ["+ x + "][" + y +"]");
                    System.out.println("Distanza di man: " + map.distanzaManhattan(map.campo[x][y], map.end));
                    System.out.print("Vicini: ");
                    for (int k = 0; k < map.campo[x][y].vicini.size(); k++) {
                        System.out.print("(" + map.campo[x][y].vicini.get(k).x + ", " + map.campo[x][y].vicini.get(k).y+ ")");
                        if(k < map.campo[x][y].vicini.size() - 1) System.out.print(", ");
                    }
                    System.out.println();
                });
                // -------------------------------
                pane.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
                pane.setPrefSize(20,20);
                if(map.campo[i][j].stato == Stato.OCCUPATO)
                    pane.setStyle("-fx-background-color: #000000");
                else if (map.campo[i][j].stato == Stato.PARTENZA)
                    pane.setStyle("-fx-background-color: #0000FF");
                else if(map.campo[i][j].stato == Stato.ARRIVO)
                    pane.setStyle("-fx-background-color: #FF0000");
                else
                    pane.setStyle("-fx-background-color: #FFFFFF");
                grid.add(pane, j, i, 1, 1);
            }
        }
        for (int j = 0; j < map.larghezza; j++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setHgrow(Priority.ALWAYS);
            grid.getColumnConstraints().add(cc);
        }

        for (int j = 0; j < map.altezza; j++) {
            RowConstraints rc = new RowConstraints();
            rc.setVgrow(Priority.ALWAYS);
            grid.getRowConstraints().add(rc);
        }
    }

    public void cleanPanes() {
        for (int i = 0; i < map.altezza; i++) {
            for (int j = 0; j < map.larghezza; j++) {
                if(map.campo[i][j].stato == Stato.LIBERO) {
                    gridMatrix[i][j].setStyle("-fx-background-color: #FFFFFF");
                }
            }
        }
    }

    /* ALGORITMO GREEDY BEST-FIRST-SEARCH
    * Ad ogni iterazione espande il nodo più promettente in base ad una funzione euristica fornita.
    * Nel nostro caso la funzione è la distanza di Manhattan del nodo dalla fine.
    * Se i nodi di partenza e arrivo sono collegati l'algoritmo restituisce un cammino che tuttavia può NON essere
    * ottimo. Il vantaggio è che l'algoritmo è molto veloce e i nodi esaminati sono una piccola parte del totale. */
    public boolean greedyBFS(Mappa map) {
        map.resetPredecessori();
        CodaPriorita queue = new CodaPriorita();
        ArrayList<Nodo> visitati = new ArrayList<>();

        queue.add(map.start, 0);
        visitati.add(map.start);

        Nodo curr;
        while(!queue.isEmpty()) {
            curr = queue.remove();
            if(curr == map.end) {
                return true;
            }
            for (int i = 0; i < curr.vicini.size(); i++) {
                Nodo vicino = curr.vicini.get(i);
                if (!visitati.contains(vicino)) {
                    visitati.add(vicino);
                    queue.add(vicino, map.distanzaManhattan(vicino, map.end));
                    vicino.predecessore = curr;
                }
            }
        }
        return false;
    }

    public boolean dijkstra(Mappa map) {
        map.resetPredecessori();
        CodaPriorita queue = new CodaPriorita();
        ArrayList<Nodo> visitati = new ArrayList<>();

        map.start.costoCammino = 0;
        queue.add(map.start, 0);
        visitati.add(map.start);

        Nodo curr;
        while(!queue.isEmpty()) {
            curr = queue.remove();
            if(curr == map.end) return true;
            for (int i = 0; i < curr.vicini.size(); i++) {
                Nodo vicino = curr.vicini.get(i);
                int costoCamminoCorrente = curr.costoCammino + 1;
                if(!visitati.contains(vicino) || costoCamminoCorrente < vicino.costoCammino) {
                    visitati.add(vicino);
                    vicino.costoCammino = costoCamminoCorrente;
                    queue.add(vicino, costoCamminoCorrente);
                    vicino.predecessore = curr;
                }
            }

        }
        return false;
    }

    public boolean aStar(Mappa map) {
        map.resetPredecessori();
        CodaPriorita queue = new CodaPriorita();
        ArrayList<Nodo> visitati = new ArrayList<>();

        map.start.costoCammino = 0;
        queue.add(map.start, 0);
        visitati.add(map.start);

        Nodo curr;
        while(!queue.isEmpty()) {
            curr = queue.remove();
            if(curr == map.end) return true;
            for (int i = 0; i < curr.vicini.size(); i++) {
                Nodo vicino = curr.vicini.get(i);
                int costoCamminoCorrente = curr.costoCammino + 1;
                if(!visitati.contains(vicino) || costoCamminoCorrente < vicino.costoCammino) {
                    visitati.add(vicino);
                    vicino.costoCammino = costoCamminoCorrente;
                    queue.add(vicino, costoCamminoCorrente + map.distanzaManhattan(vicino, map.end));
                    vicino.predecessore = curr;
                }
            }

        }
        return false;
    }

    public void setEvents(Stage stage) {
        String currentPath = Paths.get("./saved_maps").toAbsolutePath().normalize().toString();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(currentPath));

        importBtn.setOnAction((e) -> {
            File customMap = fileChooser.showOpenDialog(stage);
            if(customMap != null) {
                try {
                    map = map.importFromFile(customMap);
                    drawMap(map);
                    labelTesto.setText("Mappa caricata con successo!");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        exportBtn.setOnAction((e) -> {
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("File di testo", "*.txt"));
            File customMap = fileChooser.showSaveDialog(stage);
            try {
                map.exportToFile(customMap);
                labelTesto.setText("Mappa salvata con successo!");
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
        });

        solve.setOnAction((e) -> {
            boolean solved = false;
            int lunghezzaCammino = 0;
            if(selezioneAlgoritmo.getValue() != null) {
                cleanPanes();
                switch (selezioneAlgoritmo.getValue()) {
                    case "Greedy Best-First-Search" -> solved = greedyBFS(map);
                    case "Dijkstra" -> solved = dijkstra(map);
                    case "A*" -> solved = aStar(map);
                }

                if (solved) {
                    Nodo nodo;
                    for (int i = 0; i < map.altezza; i++) {
                        for (int j = 0; j < map.larghezza; j++) {
                            nodo = map.campo[i][j];
                            if (nodo.predecessore != null && nodo.stato != Stato.ARRIVO) {
                                gridMatrix[nodo.x][nodo.y].setStyle("-fx-background-color: #00AAFF");
                            }
                        }
                    }
                    nodo = map.end;
                    while (nodo.predecessore != null) {
                        if (nodo != map.start && nodo != map.end) {
                            gridMatrix[nodo.x][nodo.y].setStyle("-fx-background-color: #FFFF00");
                        }
                        lunghezzaCammino++;
                        nodo = nodo.predecessore;
                    }
                    labelTesto.setText("Percorso trovato con successo! Lunghezza: " + lunghezzaCammino);
                } else {
                    labelTesto.setText("Percorso non trovato! I punti non sono collegati.");
                }
            }
        });
    }

    @FXML
    void initialize() {
        selezioneAlgoritmo.getItems().addAll("Greedy Best-First-Search", "Dijkstra", "A*");
        map = new Mappa(Main.ALTEZZA, Main.LARGHEZZA);
        map.reset(0.3);
        //map.printConsole(); //debug
        drawMap(map);
    }

}
