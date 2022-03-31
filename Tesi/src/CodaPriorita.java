import java.util.PriorityQueue;

public class CodaPriorita {
    PriorityQueue<Pair> queue = new PriorityQueue<>();

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public void add(Nodo x, int priorita) {
        queue.add(new Pair(x, priorita));
    }

    public Nodo remove() {
        return queue.remove().nodo;
    }

}

class Pair implements Comparable<Pair>{
    Nodo nodo;
    int priorita;

    public Pair(Nodo nodo, int priorita) {
        this.nodo = nodo;
        this.priorita = priorita;
    }

    @Override
    public int compareTo(Pair o) {
        return priorita - o.priorita;
    }
}
