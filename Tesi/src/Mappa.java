import java.io.*;
import java.util.Date;
import java.util.Random;
import java.util.Scanner;

public class Mappa {
    public int larghezza;
    public int altezza;
    public Nodo[][] campo;
    public Nodo start;
    public Nodo end;

    public Mappa(int altezza, int larghezza) {
        this.larghezza = larghezza;
        this.altezza = altezza;
        campo = new Nodo[altezza][larghezza];
        for (int i = 0; i < altezza; i++) {
            for (int j = 0; j < larghezza; j++) {
                campo[i][j] = new Nodo();
                campo[i][j].x = i;
                campo[i][j].y = j;
                campo[i][j].stato = Stato.LIBERO;
            }
        }
        resetAllVicini();
    }

    public void setVicini(Nodo nodo) {
        if(nodo.stato != Stato.OCCUPATO) {
            //nord
            if (nodo.x - 1 >= 0 && campo[nodo.x - 1][nodo.y].stato != Stato.OCCUPATO) {
                nodo.vicini.add(campo[nodo.x - 1][nodo.y]);
            }
            //sud
            if (nodo.x + 1 < altezza && campo[nodo.x + 1][nodo.y].stato != Stato.OCCUPATO) {
                nodo.vicini.add(campo[nodo.x + 1][nodo.y]);
            }
            //ovest
            if (nodo.y - 1 >= 0 && campo[nodo.x][nodo.y - 1].stato != Stato.OCCUPATO) {
                nodo.vicini.add(campo[nodo.x][nodo.y - 1]);
            }
            //est
            if (nodo.y + 1 < larghezza && campo[nodo.x][nodo.y + 1].stato != Stato.OCCUPATO) {
                nodo.vicini.add(campo[nodo.x][nodo.y + 1]);
            }
            // se un blocco non ha vicini allora è occupato
            if (nodo.vicini.size() == 0) nodo.stato = Stato.OCCUPATO;
        }
    }

    public void resetAllVicini() {
        for (int i = 0; i < altezza; i++) {
            for (int j = 0; j < larghezza; j++) {
                campo[i][j].vicini.clear();
                setVicini(campo[i][j]);
            }
        }
    }

    public void resetPredecessori() {
        for (int i = 0; i < altezza; i++) {
            for (int j = 0; j < larghezza; j++) {
                campo[i][j].predecessore = null;
            }
        }
    }

    public void reset(double density) {
        Random rd = new Random(new Date().getTime());
        int blocchiOccupati = 0;
        int x,y;
        while((double) blocchiOccupati / (altezza * larghezza) < density) {
            x = rd.nextInt(0, altezza);
            y = rd.nextInt(0, larghezza);
            if(campo[x][y].stato == Stato.LIBERO) {
                campo[x][y].stato = Stato.OCCUPATO;
                blocchiOccupati++;
            }
        }

        // start e finish
        x = rd.nextInt(0, altezza);
        y = rd.nextInt(0, larghezza);
        campo[x][y].stato = Stato.PARTENZA;
        start = campo[x][y];
        start.predecessore = null;
        x = rd.nextInt(0, altezza);
        y = rd.nextInt(0, larghezza);
        campo[x][y].stato = Stato.ARRIVO;
        end = campo[x][y];

        resetAllVicini();
    }

    public int distanzaManhattan(Nodo a, Nodo b) {
        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
    }

    public void exportToFile(File customMap) throws FileNotFoundException {
        PrintWriter pw = new PrintWriter(customMap);
        pw.println(altezza + " " + larghezza);
        for (int i = 0; i < altezza; i++) {
            for (int j = 0; j < larghezza; j++) {
                switch(campo[i][j].stato) {
                    case LIBERO -> pw.write(".");
                    case OCCUPATO -> pw.write("X");
                    case PARTENZA -> pw.write("S");
                    case ARRIVO -> pw.write("E");
                }
            }
            if (i != altezza - 1) {
                pw.println();
            }

        }
        pw.flush();
        pw.close();
    }
    // add vicini
    public Mappa importFromFile(File savedMap) throws IOException {

        Scanner sc = new Scanner(new FileReader(savedMap));

        int i = 0;

        if (sc.hasNextLine()) {
            int altezza = sc.nextInt();
            int larghezza = sc.nextInt();
            System.out.println(altezza + " " + larghezza);
        }
        Mappa newMap = new Mappa(altezza, larghezza);
        sc.nextLine();
        while(sc.hasNextLine()) {
            String line = sc.nextLine();
            System.out.println(line);
            for (int j = 0; j < line.length(); j++) {
                char c = line.charAt(j);
                switch(c) {
                    case 'X' -> newMap.campo[i][j].stato = Stato.OCCUPATO;
                    case 'S' -> newMap.campo[i][j].stato = Stato.PARTENZA;
                    case 'E' -> newMap.campo[i][j].stato = Stato.ARRIVO;
                }
            }
            i++;
        }

        sc.close();
        return newMap;
    }

    public void printConsole() {
        for (int i = 0; i < altezza; i++) {
            for (int j = 0; j < larghezza; j++) {
                switch(campo[i][j].stato) {
                    case OCCUPATO -> System.out.print("X");
                    case LIBERO -> System.out.print(".");
                    case PARTENZA -> System.out.print("S");
                    case ARRIVO -> System.out.print("F");
                }
            }
            System.out.println();
        }
    }
}
