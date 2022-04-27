import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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

    private State AC3(State state) {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (state.getBoard().get(i).get(j).equals("E")) {
                    for (int k = 0; k < state.getDomain().get(i).get(j).size(); k++) {
                        String s = state.getDomain().get(i).get(j).get(k);
                        state.setIndexBoard(i, j, s);
                        if (isConsistent(state) == null) {
                            state.removeIndexDomain(i, j, s);
                            if (state.getDomain().get(i).get(j).isEmpty())
                                return null;
                            else {
                                s = state.getDomain().get(i).get(j).get(0);
                                state.setIndexBoard(i, j, s);
                                state = isConsistent(state);
                                if (state == null)
                                    return null;
                            }
                        }
                    }
                    state.setIndexBoard(i, j, "E");
                }
            }
        }
        return state;
    }

    private State backtrack(State state) {
        if (allAssigned(state))
            return state;

        state = AC3(state);
        if (state == null)
            return null;

        for (int MRV = 1; MRV < 3; MRV++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (state.getBoard().get(i).get(j).equals("E") && state.getDomain().get(i).get(j).size() == MRV) {
                        if (MRV == 1) {
                            String s = state.getDomain().get(i).get(j).get(0);
                            State newState = state.copy();

                            newState.setIndexBoard(i, j, s);

                            newState = isConsistent(newState);
                            if (newState != null) {
                                return backtrack(newState);
                            } else
                                return null;
                        }

                        else {
                            String s = state.getDomain().get(i).get(j).get(0);
                            String s1 = state.getDomain().get(i).get(j).get(1);

                            State newState = state.copy();
                            State newState1 = state.copy();

                            newState.setIndexBoard(i, j, s);
                            newState1.setIndexBoard(i, j, s1);

                            newState = isConsistent(newState);
                            newState1 = isConsistent(newState1);

                            if (newState != null && newState1 == null) {
                                return backtrack(newState);
                            }

                            else if (newState1 != null && newState == null) {
                                return backtrack(newState1);
                            }

                            else if (newState != null) {
                                if (newState1.limit > newState.limit) {
                                    newState = backtrack(newState);
                                    if (newState == null) {
                                        return backtrack(newState1);
                                    } else
                                        return newState;
                                } else {
                                    newState1 = backtrack(newState1);
                                    if (newState1 == null) {
                                        return backtrack(newState);
                                    } else
                                        return newState1;
                                }
                            }

                            else
                                return null;
                        }
                    }
                }
            }
        }
        return state;
    }



    private State checkNumberOfCircles(State state) {
        ArrayList<ArrayList<String>> cBoard = state.getBoard();
        State newState = state.copy();
        newState.limit = 0;

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
                return null;
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
                return null;
            }

            if (numberOfBlacks == n/2) {
                for (int j = 0; j < n; j++) {
                    String a = cBoard.get(j).get(i);
                    if (a.equals("E") && newState.getDomain().get(j).get(i).contains("b")) {
                        newState.removeIndexDomain(j, i, "b");
                        newState.limit++;
                        if (newState.getDomain().get(j).get(i).isEmpty())
                            return null;
                    }
                }
            }
            if (numberOfWhites == n/2) {
                for (int j = 0; j < n; j++) {
                    String a = cBoard.get(j).get(i);
                    if (a.equals("E") && newState.getDomain().get(j).get(i).contains("a")) {
                        newState.removeIndexDomain(j, i, "a");
                        newState.limit++;
                        if (newState.getDomain().get(j).get(i).isEmpty())
                            return null;
                    }
                }
            }
        }
        return newState;
    }
    
    private State checkAdjacency(State state, int l) {
        ArrayList<ArrayList<String>> cBoard = state.getBoard();
        State newState = state.copy();
        newState.limit = l;

        //Horizontal
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n-2; j++) {
                ArrayList<String> row = cBoard.get(i);
                String c1 = row.get(j);
                String c2 = row.get(j+1);
                String c3 = row.get(j+2);
                if (c1.equalsIgnoreCase(c2) && c2.equalsIgnoreCase(c3) && !c1.equals("E")) {
                    return null;
                }
                else if ( c1.equalsIgnoreCase(c2) && c3.equals("E")
                        && newState.getDomain().get(i).get(j + 2).contains(c1.toLowerCase())) {
                    newState.removeIndexDomain(i, j + 2, c1.toLowerCase());
                    newState.limit++;
                    if (newState.getDomain().get(i).get(j + 2).isEmpty())
                        return null;
                }

                else if (c1.equalsIgnoreCase(c3) && c2.equals("E")
                        && newState.getDomain().get(i).get(j + 1).contains(c1.toLowerCase())) {
                    newState.removeIndexDomain(i, j + 1, c1.toLowerCase());
                    newState.limit++;
                    if (newState.getDomain().get(i).get(j + 1).isEmpty())
                        return null;
                }

                else if (c2.equalsIgnoreCase(c3) && c1.equals("E")
                        && newState.getDomain().get(i).get(j).contains(c2.toLowerCase())) {
                    newState.removeIndexDomain(i, j , c2.toLowerCase());
                    newState.limit++;
                    if (newState.getDomain().get(i).get(j ).isEmpty())
                        return null;
                }
            }
        }
        //column
        for (int j = 0; j < n; j++) {
            for (int i = 0; i < n-2; i++) {
                ArrayList<String> row = cBoard.get(i);
                String c1 = cBoard.get(i).get(j).toUpperCase();
                String c2 = cBoard.get(i+1).get(j).toUpperCase();
                String c3 = cBoard.get(i+2).get(j).toUpperCase();
                if (c1.equalsIgnoreCase(c2) && c2.equalsIgnoreCase(c3) && !c1.equalsIgnoreCase("E")) {
                    return null;
                }
                else if ( c1.equalsIgnoreCase(c2) && c3.equals("E")
                        && newState.getDomain().get(i + 2).get(j).contains(c1.toLowerCase())) {
                    newState.removeIndexDomain(i + 2, j, c1.toLowerCase());
                    newState.limit++;
                    if (newState.getDomain().get(i + 2).get(j).isEmpty())
                        return null;
                }

                else if (c1.equalsIgnoreCase(c3) && c2.equals("E")
                        && newState.getDomain().get(i + 1).get(j).contains(c1.toLowerCase())) {
                    newState.removeIndexDomain(i + 1, j, c1.toLowerCase());
                    newState.limit++;
                    if (newState.getDomain().get(i + 1).get(j).isEmpty())
                        return null;
                }

                else if (c2.equalsIgnoreCase(c3) && c1.equals("E")
                        && newState.getDomain().get(i).get(j).contains(c2.toLowerCase())) {
                    newState.removeIndexDomain(i, j, c2.toLowerCase());
                    newState.limit++;
                    if (newState.getDomain().get(i).get(j).isEmpty())
                        return null;
                }
            }
        }

        return newState;
    }

    private State checkIfUnique (State state, int l) {
        ArrayList<ArrayList<String>> cBoard = state.getBoard();
        State newState = state.copy();
        newState.limit = l;

        //check if two rows are duplicated
        for (int i = 0; i < n-1; i++) {
            for (int j = i+1; j < n; j++) {
                int count = 0;
                int temp=-1;
                for (int k = 0; k < n; k++) {
                    String a = cBoard.get(i).get(k);
                    String b = cBoard.get(j).get(k);
                    if (a.equalsIgnoreCase(b) && !a.equals("E")) {
                        count++;
                    }
                    else if (!a.equalsIgnoreCase(b) && (a.equals("E") || b.equals("E")))
                        temp = k;
                }
                if (count == n) {
                    return null;
                }

                if (count == n - 1 && temp!=-1) {
                    String a = cBoard.get(i).get(temp);
                    String b = cBoard.get(j).get(temp);
                    if ( a.equals("E")) {
                        if(newState.getDomain().get(i).get(temp).contains(b)) {
                            newState.removeIndexDomain(i, temp, b);
                            newState.limit++;
                            if (newState.getDomain().get(i).get(temp).isEmpty())
                                return null;
                        }
                    } else {
                        if (newState.getDomain().get(j).get(temp).contains(a)) {
                            newState.removeIndexDomain(j, temp, a);
                            newState.limit++;
                            if (newState.getDomain().get(j).get(temp).isEmpty())
                                return null;
                        }
                    }
                }
            }
        }

        //check if two columns are duplicated

        for (int j = 0; j < n-1; j++) {
            for (int k = j+1; k < n; k++) {
                int count = 0;
                int temp = -1;
                for (int i = 0; i < n; i++) {
                    String a = cBoard.get(i).get(j);
                    String b = cBoard.get(i).get(k);
                    if (cBoard.get(i).get(j).equals(cBoard.get(i).get(k))) {
                        count++;
                    }
                    else if (!a.equalsIgnoreCase(b) && (a.equals("E") || b.equals("E")))
                        temp = k;
                }
                if (count == n) {
                    return null;
                }
                if (count == n - 1 && temp!=-1) {
                    String a = cBoard.get(temp).get(j);
                    String b = cBoard.get(temp).get(k);
                    if (a.equals("E")) {
                        if(newState.getDomain().get(temp).get(j).contains(b)) {
                            newState.removeIndexDomain(temp, j, b);
                            newState.limit++;
                            if (newState.getDomain().get(temp).get(j).isEmpty())
                                return null;
                        }
                    } else {
                        if (newState.getDomain().get(temp).get(k).contains(a)) {
                            newState.removeIndexDomain(temp, k, a);
                            newState.limit++;
                            if (newState.getDomain().get(temp).get(k).isEmpty())
                                return null;
                        }
                    }
                }
            }
        }

        return newState;
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

    private State isConsistent(State state) {
        State newState = checkNumberOfCircles(state);
        if (newState == null) {
            return null;
        } else {
            newState = checkAdjacency(newState, newState.limit);
            if (newState == null) {
                return null;
            } else {
                newState = checkIfUnique(newState, newState.limit);
                return newState;
            }
        }
    }

    private void drawLine() {
        for (int i = 0; i < n*2; i++) {
            System.out.print("\u23E4\u23E4");
        }
        System.out.println();
    }
}
