package labyrinth.state;

import javafx.beans.property.ReadOnlyObjectWrapper;

import java.util.Arrays;

public class LabyrinthState {

    /**
     * The size of the board.
     */
    public static final int BOARD_SIZE = 7;

    private static final char LEFT_WALL = 'L';
    private static final char TOP_WALL = 'T';
    private static final char RIGHT_WALL = 'R';
    private static final char BALL = '*';
    private static final char EMPTY = '-';

    private char initial_board[][] = new char[BOARD_SIZE][BOARD_SIZE];

    public LabyrinthState() {
        initial_board = new char[][]{
            {'-', 'L', '-', '-', 'L', '-', '-'},
            {'-', '-', 'T', '-', '*', '-', 'T'},
            {'-', '-', '-', 'L', '-', '-', 'L'},
            {'-', 'T', '-', '-', 'L', 'L', '-'},
            {'-', '-', '-', 'T', '-', '-', 'T'},
            {'T', 'R', '-', 'L', 'T', '-', '-'},
            {'-', '-', 'T', '-', 'L', '-', 'L'}
        };
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (char[] row : initial_board) {
            for (char c : row) {
                sb.append(c).append(' ');
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    // TODO: Implement Move functionality and checking for walls

    public static void main(String[] args) {
        LabyrinthState labyrinthState = new LabyrinthState();
        System.out.println(labyrinthState);
    }
}
