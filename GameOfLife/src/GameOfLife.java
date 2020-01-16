import java.util.ArrayList;

public class GameOfLife {
    public static void main(String[] args) throws InterruptedException {
        char[][] map = {
                {'.','.','#','.','.','.','.','.','.','.','.','.','.','.','.','.','.','.','.','.'},
                {'#','.','#','.','.','.','.','.','.','.','.','.','.','.','.','.','.','.','.','.'},
                {'.','#','#','.','.','.','.','.','.','.','.','.','.','#','#','.','.','.','.','.'},
                {'.','.','.','.','.','.','.','.','.','.','.','.','.','#','#','.','.','.','.','.'},
                {'.','.','.','.','.','.','.','.','#','.','.','.','.','.','.','.','.','.','.','.'},
                {'.','.','.','.','.','.','.','.','#','.','.','.','.','.','.','.','.','.','.','.'},
                {'.','.','.','.','.','.','.','.','#','.','.','.','.','.','.','.','.','.','.','.'},
                {'.','.','.','.','.','.','.','.','.','.','.','.','.','.','.','.','.','.','.','.'},
                {'.','.','.','.','#','#','#','.','.','.','#','#','#','#','.','.','.','.','.','.'},
                {'.','.','.','.','.','.','.','.','.','.','.','.','.','.','.','.','.','.','.','.'},
                {'.','.','.','.','.','.','.','.','#','.','.','.','.','.','.','.','.','.','.','.'},
                {'.','.','.','.','.','.','.','.','#','.','.','.','.','.','.','.','.','.','.','.'},
                {'.','.','.','.','.','.','.','.','#','.','.','.','.','.','.','.','.','.','.','.'},
                {'.','.','.','#','.','.','.','.','.','.','.','.','.','.','.','.','.','.','.','.'},
                {'.','.','.','.','#','.','.','.','.','.','.','.','.','.','.','.','.','.','.','.'},
                {'.','.','.','#','.','.','.','.','.','.','.','.','.','.','.','.','.','.','.','.'},
                {'.','.','.','.','.','.','.','.','.','.','.','#','.','.','.','.','.','.','.','.'},
                {'.','.','.','.','.','.','.','.','.','.','.','#','.','.','.','.','#','#','.','.'},
                {'.','.','.','.','.','.','.','.','.','.','.','.','.','.','.','.','#','.','#','.'},
                {'.','.','.','.','.','.','.','.','.','.','.','.','.','.','.','.','#','.','.','.'},};
        ArrayList<int[]> willDie = new ArrayList<int[]>();
        ArrayList<int[]> willSpawn = new ArrayList<int[]>();

        while(true == true){
            //Logic
//            System.out.println("\b");
//            System.out.println("\b");
//            System.out.println("\b");
//            System.out.println("\b");
//            System.out.println("\b");
//            System.out.println("\b");
//            System.out.println("\b");
//            System.out.println("\b");
//            System.out.println("\b");
            System.out.println("\b");
            int i = 0;
            while (i < map.length) {
                for (int f = 0; f < map.length; f++) {
                    //Check How many living cells are around
                    int[] currentPoint = {i, f};
                    int livingNeighbors = 0;
                    try {
                        if (map[i + 1][f + 1] == '#') {
                            livingNeighbors++;
                        }
                    } catch(ArrayIndexOutOfBoundsException ex) {

                    }
                    try {
                        if (map[i + 1][f] == '#') {
                            livingNeighbors++;
                        }
                    }
                    catch(ArrayIndexOutOfBoundsException ex) {

                    }
                    try {
                        if (map[i][f + 1] == '#') {
                            livingNeighbors++;
                        }
                    }
                    catch(ArrayIndexOutOfBoundsException ex) {

                    }
                    try {
                        if (map[i - 1][f - 1] == '#') {
                            livingNeighbors++;
                        }
                    }
                    catch(ArrayIndexOutOfBoundsException ex) {

                    }
                    try {
                        if (map[i][f - 1] == '#') {
                            livingNeighbors++;
                        }
                    }
                    catch(ArrayIndexOutOfBoundsException ex) {

                    }
                    try {
                        if (map[i - 1][f] == '#') {
                            livingNeighbors++;
                        }
                    }
                    catch(ArrayIndexOutOfBoundsException ex) {

                    }
                    try {
                        if (map[i + 1][f - 1] == '#') {
                            livingNeighbors++;
                        }
                    }
                    catch(ArrayIndexOutOfBoundsException ex) {

                    }
                    try {
                        if (map[i - 1][f + 1] == '#') {
                            livingNeighbors++;
                        }
                    }
                    catch(ArrayIndexOutOfBoundsException ex) {

                    }
                    if (map[i][f] == '#') {
                        if (livingNeighbors > 3) {
                            willDie.add(currentPoint);
                        }
                        if (livingNeighbors < 2) {
                            willDie.add(currentPoint);
                        }
                    }
                    if (map[i][f] == '.') {
                        if (livingNeighbors == 3) {
                            willSpawn.add(currentPoint);
                        }
                    }
                    livingNeighbors = 0;
                }
                i++;
            }
            //Change all cell states at once
            for (int k = 0; k < willDie.size(); k++) {
                int[] currentDiePoint = willDie.get(k);
                map[currentDiePoint[0]][currentDiePoint[1]] = '.';
            }
            willDie.clear();
            for (int k = 0; k < willSpawn.size(); k++) {
                int[] currentSpawnPoint = willSpawn.get(k);
                map[currentSpawnPoint[0]][currentSpawnPoint[1]] = '#';
            }
            willSpawn.clear();
            //Print at end
            Thread.sleep(750);
            for (int k = 0; k < map.length; k++) {
//                System.out.println(Arrays.toString(map[k]));
                System.out.println(map[k]);
            }
        }
    }
}
