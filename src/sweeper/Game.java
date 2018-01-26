package sweeper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Game {

    private Bomb bomb;
    private Flag flag;
    private GameState state;
    private Boolean isStarted;
    private static final Logger log = LogManager.getLogger(Bomb.class);

    public Game(int cols, int rows, int bombs) {
        Ranges.setSize(new Coord(cols, rows));
        bomb = new Bomb(bombs);
        flag = new Flag();
    }

    public void start() {
        state = GameState.PLAYED;
        flag.start();
        isStarted = false;
    }

    public Box getBox(Coord coord) {
        if (flag.get(coord) == Box.OPENED)
            return bomb.get(coord);
        return flag.get(coord);
    }

    public void pressLeftButton(Coord coord) {
        if (!isStarted) {
            bomb.start(coord);
            isStarted = true;
        }
        if (isGameOver()) return;
        openBox(coord);
        checkWinner();
    }

    public void pressRightButton(Coord coord) {
        if (isGameOver()) return;
        flag.toggleFlagedToBox(coord);
    }

    private boolean isGameOver() {
        if (state != GameState.PLAYED) {
            start();
            return true;
        }
        return false;
    }

    private void checkWinner() {
        if (state == GameState.PLAYED)
            if (flag.getTotalClosed() == bomb.getTotalBomb()) {
                state = GameState.WINNER;
                flag.setFlagedToLastClosedBoxes();
            }
    }

    public GameState getState() {
        return state;
    }

    public int getTotalBomb() {
        return bomb.getTotalBomb();
    }

    public int getTotalFlaged() {
        return flag.getTotalFlaged();
    }

    private void openBox(Coord coord) {
        switch (flag.get(coord)) {
            case OPENED:
                setOpenedToCloseBoxAroundNumber(coord);
                break;
            case FLAGED:
                break;
            case CLOSED:
                switch (bomb.get(coord)) {
                    case ZERO:
                        openBoxesAroundZero(coord);
                        break;
                    case BOMB:
                        openBombs(coord);
                        break;
                    default:
                        flag.setOpenedToBox(coord);
                        break;
                }
        }
    }

    private void setOpenedToCloseBoxAroundNumber(Coord coord) {
        if (Box.BOMB != bomb.get(coord))
            if (bomb.get(coord).getNumber() == flag.getCountOfFlagedBoxesAround(coord))
                for (Coord around : Ranges.getCoordsAround(coord)) {
                    if (flag.get(around) == Box.CLOSED)
                        openBox(around);
                }
    }

    private void openBombs(Coord bombedCoord) {
        flag.setBombedToBox(bombedCoord);
        for (Coord coord : Ranges.getAllCoords())
            if (bomb.get(coord) == Box.BOMB)
                flag.setOpenedToClosedBox(coord);
            else
                flag.setNobombToFlagedBox(coord);
        state = GameState.BOMBED;
    }

    private void openBoxesAroundZero(Coord coord) {
        flag.setOpenedToBox(coord);
        for (Coord around : Ranges.getCoordsAround(coord))
            openBox(around);
    }
}
