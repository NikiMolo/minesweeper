package sweeper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

class Bomb {
    private Matrix bombMap;
    private int totalBomb;
    private static final Logger log = LogManager.getLogger(Bomb.class);

    Bomb(int totalBomb) {
        this.totalBomb = totalBomb;
        fixBombsCount();
    }

    public void start(Coord initCoord) {
        bombMap = new Matrix(Box.ZERO);
        for (int i = 0; i < totalBomb; i++)
            placeBomb(initCoord);
        log.info("Bomb map was created");
    }

    Box get(Coord coord) {
        return bombMap.get(coord);
    }

    private void placeBomb(Coord initCoord) {
        while (true) {
            Coord coord = Ranges.getRandomCoord();
            if (Box.BOMB == bombMap.get(coord) || coord.equals(initCoord))
                continue;
            incNumbersAroundBomb(coord);
            bombMap.set(coord, Box.BOMB);
            break;
        }

    }

    private void incNumbersAroundBomb(Coord coord) {
        for (Coord around : Ranges.getCoordsAround(coord))
            if (Box.BOMB != bombMap.get(around))
                bombMap.set(around, bombMap.get(around).nextNumberBox());
    }

    private void fixBombsCount() {
        int maxBombs = Ranges.getSize().x * Ranges.getSize().y /2;
        if (totalBomb>maxBombs)
            totalBomb=maxBombs;
    }

    int getTotalBomb() {
        return totalBomb;
    }
}
