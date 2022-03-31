import java.util.ArrayList;

public class Nodo {
    public int x;
    public int y;
    public Stato stato;
    public ArrayList<Nodo> vicini = new ArrayList<>();
    public Nodo predecessore;
    public int costoCammino;
}