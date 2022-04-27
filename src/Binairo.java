import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class Binairo {
    private final ArrayList<ArrayList<String>> board;
    private final ArrayList<ArrayList<ArrayList<String>>> domain;
    private final int n;

    public Binairo(ArrayList<ArrayList<String>> board,
                   ArrayList<ArrayList<ArrayList<String>>> domain,
                   int n) {
        this.board = board;
        this.domain = domain;
        this.n = n;
    }

    public void start() {
        long tStart = System.nanoTime();
        State state = new State(board, domain);
        
        drawLine();
        System.out.println("Initial Board: \n");
        state.printBoard();
        drawLine();
        //System.out.println("DFgdfgdfgdfgdgdfgdfgfdgg");

        State res = backtrack(state);
        if (res != null) {
            System.out.println("Final Board: \n");
            res.printBoard();
            drawLine();
        }
        else {
            System.out.println("No solution!");
        }
        long tEnd = System.nanoTime();
        System.out.println("Total time: " + (tEnd - tStart)/1000000000.000000000);
    }

    private State backtrack(State state) {
        if (allAssigned(state))
            return state;

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {

                    if (state.getBoard().get(i).get(j).equals("E")) {
                        System.out.println("Empty " + i + " " + j);
                        String s = "w";
                        while(!state.getDomain().get(i).get(j).isEmpty()) {
                            ArrayList<ArrayList<String>> cBoard = state.getBoard();
                            ArrayList<ArrayList<ArrayList<String>>> cDomain = state.getDomain();

                            System.out.println(i + " " + j + " " + s);
                            cBoard.get(i).set(j, s);
                            cDomain.get(i).set(j, new ArrayList<>(List.of(
                                    "n"
                            )));
                            state.getDomain().get(i).get(j).remove(s);

                            State newState = new State(cBoard, cDomain);

                            if (isConsistent(newState)) {
                                System.out.println("raft tu");
                                State res = backtrack(newState);
                                if (res!= null) {
                                    //System.out.println("answer found");
                                    return res;
                                }
                                else if (s.equals("b")) {
                                    System.out.println("bargasht");
                                    cBoard.get(i).set(j, "E");
                                    cDomain.get(i).set(j, new ArrayList<>(List.of(
                                            "w",
                                            "b"
                                    )));
                                    return null;
                                }
                            }
                            else if(s.equals("b")) {
                                System.out.println("bargasht 2");
                                cBoard.get(i).set(j, "E");
                                cDomain.get(i).set(j, new ArrayList<>(List.of(
                                        "w",
                                        "b"
                                )));
                                return null;
                            }
                            s = "b";
                        }
                    }
            }
        }

        return null;
    }

    
    private boolean checkNumberOfCircles(State state) {
        ArrayList<ArrayList<String>> cBoard = state.getBoard();
        State newState = state.copy();
        //row
        for (int i = 0; i < n; i++) {
            int numberOfWhites = 0;
            int numberOfBlacks = 0;
            for (int j = 0; j < n; j++) {
                String a = cBoard.get(i).get(j);
                switch (a) {
                    case "w", "W" -> numberOfWhites++;
                    case "b", "B" -> numberOfBlacks++;
                }
            }
            if (numberOfBlacks > n/2 || numberOfWhites > n/2) {
                return false;
            }
            if (numberOfBlacks == n/2) {
                for (int j = 0; j < n; j++) {
                    String a = cBoard.get(i).get(j);
                    if (a.equals("E") && newState.getDomain().get(i).get(j).contains("b")) {
                        newState.removeIndexDomain(i, j, "b");
                        newState.limit++;
                        if (newState.getDomain().get(i).get(j).isEmpty())
                            return null;
                    }
                }
            }
            if (numberOfWhites == n/2) {
                for (int j = 0; j < n; j++) {
                    String a = cBoard.get(i).get(j);
                    if (a.equals("E") && newState.getDomain().get(i).get(j).contains("a")) {
                        newState.removeIndexDomain(i, j, "a");
                        newState.limit++;
                        if (newState.getDomain().get(i).get(j).isEmpty())
                            return null;
                    }
                }
            }
        }
        //column
        for (int i = 0; i < n; i++) {
            int numberOfWhites = 0;
            int numberOfBlacks = 0;
            for (int j = 0; j < n; j++) {
                String a = cBoard.get(j).get(i);
                switch (a) {
                    case "w", "W" -> numberOfWhites++;
                    case "b", "B" -> numberOfBlacks++;
                }
            }
            if (numberOfBlacks > n/2 || numberOfWhites > n/2) {
                return false;
            }

            if (numberOfBlacks == n/2) {
                for (int j = 0; j < n; j++) {
                    String a = cBoard.get(i).get(j);
                    if (a.equals("E") && newState.getDomain().get(i).get(j).contains("b")) {
                        newState.removeIndexDomain(i, j, "b");
                        newState.limit++;
                        if (newState.getDomain().get(i).get(j).isEmpty())
                            return null;
                    }
                }
            }
            if (numberOfWhites == n/2) {
                for (int j = 0; j < n; j++) {
                    String a = cBoard.get(i).get(j);
                    if (a.equals("E") && newState.getDomain().get(i).get(j).contains("a")) {
                        newState.removeIndexDomain(i, j, "a");
                        newState.limit++;
                        if (newState.getDomain().get(i).get(j).isEmpty())
                            return null;
                    }
                }
            }
        }
        return true;
    }
    
    private boolean checkAdjacency(State state) {
        ArrayList<ArrayList<String>> cBoard = state.getBoard();

        //Horizontal
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n-2; j++) {
                ArrayList<String> row = cBoard.get(i);
                String c1 = row.get(j).toUpperCase();
                String c2 = row.get(j+1).toUpperCase();
                String c3 = row.get(j+2).toUpperCase();
                if (c1.equals(c2) && c2.equals(c3) && !c1.equals("E")) {


                    //System.out.println("hor " + i + " " + j + " " + c1);
                    return false;
                }
            }
        }
        //column
        for (int j = 0; j < n; j++) {
            for (int i = 0; i < n-2; i++) {
                String c1 = cBoard.get(i).get(j).toUpperCase();
                String c2 = cBoard.get(i+1).get(j).toUpperCase();
                String c3 = cBoard.get(i+2).get(j).toUpperCase();
                if (c1.equals(c2) && c2.equals(c3) && !c1.equals("E")) {
                    //System.out.println("col");
                    return false;
                }
            }
        }

        return true;
    }
    
    private boolean checkIfUnique (State state) {
        ArrayList<ArrayList<String>> cBoard = state.getBoard();
        
        //check if two rows are duplicated
        for (int i = 0; i < n-1; i++) {
            for (int j = i+1; j < n; j++) {
                int count = 0;
                for (int k = 0; k < n; k++) {
                    String a = cBoard.get(i).get(k);
                    if (a.equals(cBoard.get(j).get(k)) && !a.equals("E")) {
                        count++;
                    }
                }
                if (count == n) {
                    return false;
                }
            }
        }

        //check if two columns are duplicated

        for (int j = 0; j < n-1; j++) {
            for (int k = j+1; k < n; k++) {
                int count = 0;
                for (int i = 0; i < n; i++) {
                    if (cBoard.get(i).get(j).equals(cBoard.get(i).get(k))) {
                        count++;
                    }
                }
                if (count == n) {
                    return false;
                }
            }
        }

        return true;
    }
    
    private boolean allAssigned(State state) {
        ArrayList<ArrayList<String>> cBoard = state.getBoard();

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                String s = cBoard.get(i).get(j);
                if (s.equals("E"))
                    return false;
            }
        }
        return true;
    }
        

    private boolean isFinished(State state) {
        return allAssigned(state) && checkAdjacency(state) && checkNumberOfCircles(state) && checkIfUnique(state);
    }

    private boolean isConsistent(State state) {
        if (checkNumberOfCircles(state))
            System.out.println("number");
        if (checkAdjacency(state))
            System.out.println("adj");
        if (checkIfUnique(state))
            System.out.println("uniq");
        return checkNumberOfCircles(state) && checkAdjacency(state) && checkIfUnique(state);
    }

    private void drawLine() {
        for (int i = 0; i < n*2; i++) {
            System.out.print("\u23E4\u23E4");
        }
        System.out.println();
    }
}
