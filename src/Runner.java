
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public class Runner {

    public static void main(String[] args) {
        Node initialNode = new Node(0, 0);
        Node finalNode = new Node(2, 4);
        int rows = 6;
        int cols = 7;
//        AStar aStar = new AStar(rows, cols, initialNode, finalNode);
        AStar aStar = new AStar(initialNode, finalNode);
//        aStar.setXOffset(3);
//        aStar.setYOffset(5);
//        int[][] obstaclesArray = new int[][]{{1, 3}, {2, 3}, {3, 3}};
        ArrayList<ArrayList<Integer>> obstacles = new ArrayList<>();
//        obstacles.add(new ArrayList(Arrays.asList(3, 0)));
//        obstacles.add(new ArrayList(Arrays.asList(3, 1)));
//        obstacles.add(new ArrayList(Arrays.asList(3, 2)));
//        obstacles.add(new ArrayList(Arrays.asList(3, 3)));
//        obstacles.add(new ArrayList(Arrays.asList(3, 4)));
//        obstacles.add(new ArrayList(Arrays.asList(3, 5)));
        
        obstacles.add(new ArrayList(Arrays.asList(1, -1)));
        obstacles.add(new ArrayList(Arrays.asList(1, 0)));
        obstacles.add(new ArrayList(Arrays.asList(1, 1)));
        obstacles.add(new ArrayList(Arrays.asList(1, 2)));
        obstacles.add(new ArrayList(Arrays.asList(3, 3)));
        obstacles.add(new ArrayList(Arrays.asList(2, 3)));
        obstacles.add(new ArrayList(Arrays.asList(1, 3)));
        obstacles.add(new ArrayList(Arrays.asList(0, 3)));
        obstacles.add(new ArrayList(Arrays.asList(-1, 3)));
        obstacles.add(new ArrayList(Arrays.asList(1, 5)));
        obstacles.add(new ArrayList(Arrays.asList(1, 4)));
        
//        aStar.setObstacles(obstaclesArray);
        aStar.setObstacles(obstacles);
        List<Node> path = aStar.findPath();
        for (Node node : path) {
            System.out.print(node + ";  ");
        }
        System.out.println();
        
//        aStar.printObstacleArea();
        printFinalPath(aStar, path);

        //Search Area
        //      0   1   2   3   4   5   6
        // 0    -   -   -   -   -   -   -
        // 1    -   -   -   B   -   -   -
        // 2    -   I   -   B   -   F   -
        // 3    -   -   -   B   -   -   -
        // 4    -   -   -   -   -   -   -
        // 5    -   -   -   -   -   -   -

        //Expected output with diagonals
        //Node [row=2, col=1]
        //Node [row=1, col=2]
        //Node [row=0, col=3]
        //Node [row=1, col=4]
        //Node [row=2, col=5]

        //Search Path with diagonals
        //      0   1   2   3   4   5   6
        // 0    -   -   -   *   -   -   -
        // 1    -   -   *   B   *   -   -
        // 2    -   I*  -   B   -  *F   -
        // 3    -   -   -   B   -   -   -
        // 4    -   -   -   -   -   -   -
        // 5    -   -   -   -   -   -   -

        //Expected output without diagonals
        //Node [row=2, col=1]
        //Node [row=2, col=2]
        //Node [row=1, col=2]
        //Node [row=0, col=2]
        //Node [row=0, col=3]
        //Node [row=0, col=4]
        //Node [row=1, col=4]
        //Node [row=2, col=4]
        //Node [row=2, col=5]

        //Search Path without diagonals
        //      0   1   2   3   4   5   6
        // 0    -   -   *   *   *   -   -
        // 1    -   -   *   B   *   -   -
        // 2    -   I*  *   B   *  *F   -
        // 3    -   -   -   B   -   -   -
        // 4    -   -   -   -   -   -   -
        // 5    -   -   -   -   -   -   -
    }
    
    public static void printFinalPath(AStar aStar, List<Node> path) {
    	ArrayList<ArrayList<Node>> searchArea = aStar.getSearchArea();
    	int xOffset = aStar.getXOffset();
    	int yOffset = aStar.getYOffset();
    	String result = "\t";
    	//Printing labels above the map
    	for(int j = 0; j < searchArea.size(); j++){
    		if (j-xOffset<0) {	//Account for spacing of the minus sign
    			result += j-xOffset;
    		}
    		else {
    			result += " " + (j-xOffset);
    		}
        }
    	result += "\n";
    	
    	//Printing the actual map
    	boolean printed = false;
        for(int i = 0; i < searchArea.get(0).size(); i++){
        	result += i-yOffset + "\t" + " ";
            for(int j = 0; j < searchArea.size(); j++){
            	
            	for (Node n : path) {
            		if (searchArea.get(j).get(i).getRow() == n.getRow() && searchArea.get(j).get(i).getCol() == n.getCol()) {
            			result += "X ";
            			printed = true;
            		}
            	}
            	if (!printed) {
            		result += searchArea.get(j).get(i).printStatus()+" ";
            	}
            	printed = false;
                
            }
            // System.out.println();
            result += "\n";
        }
        System.out.println(result+"\n");
    }
}
