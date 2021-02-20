import java.util.*;

/**
 * A-Star Algorithm
 *
 * Adapter from work by Marcelo Surriabre
 */

public class AStar {
	private int bufferLength = 5;	//Distance from edge of the map to the goalNode
	private int xOffset = 0;	//Amount to shift the origin rightward within the searchArea list
	private int yOffset = 0;	//Amount to shift the origin upward within the searchArea list
    private static int DEFAULT_HV_COST = 10; // Horizontal - Vertical Cost
    private static int DEFAULT_DIAGONAL_COST = 14;
    private int hvCost;
    private int diagonalCost;
//    private Node[][] searchArea;
    private ArrayList<ArrayList<Node>> searchArea = new ArrayList<ArrayList<Node>>();	//2D ArrayList; .get() is x-axis, .get().get() is y-axis
    private PriorityQueue<Node> openList;
    private Set<Node> closedSet;
    private Node initialNode;
    private Node finalNode;
    
    public AStar(Node initialNode, Node finalNode) {
        this.hvCost = DEFAULT_HV_COST;
        this.diagonalCost = DEFAULT_DIAGONAL_COST;
        setInitialNode(initialNode);
        setFinalNode(finalNode);
//        this.searchArea = new Node[rows][cols];
        this.openList = new PriorityQueue<Node>(new Comparator<Node>() {
            @Override
            public int compare(Node node0, Node node1) {
                return Integer.compare(node0.getF(), node1.getF());
            }
        });
        
        //Setting initial map boundaries based on initial and final node coordinates
        if (initialNode.getRow() < finalNode.getRow()) {	//finalNode is rightward
        	if (initialNode.getCol() < finalNode.getCol()) {	//finalNode is upwards (pos. Y)
        		setNodes(finalNode.getRow() + bufferLength, finalNode.getCol() + bufferLength);
        	}
        	else {	//finalNode is downwards (neg. Y)
        		setNodes(finalNode.getRow() + bufferLength, initialNode.getCol() + bufferLength);
        	}
        }
        else {	//finalNode is leftward
    	if (initialNode.getCol() < finalNode.getCol()) {	//finalNode is upwards (pos. Y)
    			setNodes(initialNode.getRow() + bufferLength, finalNode.getCol() + bufferLength);
        	}
        	else {	//finalNode is downwards (neg. Y)
        		setNodes(initialNode.getRow() + bufferLength, initialNode.getCol() + bufferLength);
        	}
        }
        
      //Setting offsets based on initial and final node coordinates
        setXOffset(Math.max(Math.max(bufferLength-initialNode.getRow(), bufferLength-finalNode.getRow()), 0));	//First choose the larger offset between initial and final nodes, then, if the offset is negative, ignore it. Then feed this offset amount to setOffset()
        setYOffset(Math.max(Math.max(bufferLength-initialNode.getCol(), bufferLength-finalNode.getCol()), 0));	//First choose the larger offset between initial and final nodes, then, if the offset is negative, ignore it. Then feed this offset amount to setOffset()
        System.out.println("xOffset: " + xOffset);
        System.out.println("yOffset: " + yOffset);
        
        this.closedSet = new HashSet<>();
    }
    
//    public AStar(Node initialNode, Node finalNode) {
//        this.hvCost = DEFAULT_HV_COST;
//        this.diagonalCost = DEFAULT_DIAGONAL_COST;
//        setInitialNode(initialNode);
//        setFinalNode(finalNode);
////        this.searchArea = new Node[rows][cols];
//        this.openList = new PriorityQueue<Node>(new Comparator<Node>() {
//            @Override
//            public int compare(Node node0, Node node1) {
//                return Integer.compare(node0.getF(), node1.getF());
//            }
//        });
//        //Setting initial map boundaries based on initial and final node coordinates
//        if (initialNode.getRow() < finalNode.getRow()) {	//finalNode is rightward
//        	if (initialNode.getCol() < finalNode.getCol()) {	//finalNode is upwards (pos. Y)
//        		setNodes(finalNode.getRow() + bufferLength, finalNode.getCol() + bufferLength);
//        	}
//        	else {	//finalNode is downwards (neg. Y)
//        		setNodes(finalNode.getRow() + bufferLength, initialNode.getCol() + bufferLength);
//        	}
//        }
//        else {	//finalNode is leftward
//    	if (initialNode.getCol() < finalNode.getCol()) {	//finalNode is upwards (pos. Y)
//    			setNodes(initialNode.getRow() + bufferLength, finalNode.getCol() + bufferLength);
//        	}
//        	else {	//finalNode is downwards (neg. Y)
//        		setNodes(initialNode.getRow() + bufferLength, initialNode.getCol() + bufferLength);
//        	}
//        }
//        this.closedSet = new HashSet<>();
//    }
    
    public AStar(int rows, int cols, Node initialNode, Node finalNode, int hvCost, int diagonalCost) {
        this.hvCost = hvCost;
        this.diagonalCost = diagonalCost;
        setInitialNode(initialNode);
        setFinalNode(finalNode);
//        this.searchArea = new Node[rows][cols];
        this.openList = new PriorityQueue<Node>(new Comparator<Node>() {
            @Override
            public int compare(Node node0, Node node1) {
                return Integer.compare(node0.getF(), node1.getF());
            }
        });
        setNodes(rows, cols);
        this.closedSet = new HashSet<>();
    }

    public AStar(int rows, int cols, Node initialNode, Node finalNode) {
        this(rows, cols, initialNode, finalNode, DEFAULT_HV_COST, DEFAULT_DIAGONAL_COST);
    }

//    private void setNodes() {
//        for (int i = 0; i < searchArea.length; i++) {
//            for (int j = 0; j < searchArea[0].length; j++) {
//                Node node = new Node(i, j);
//                node.calculateHeuristic(getFinalNode());
//                this.searchArea[i][j] = node;
//            }
//        }
//    }
    
    private void setNodes(int rows, int cols) {
    	System.out.println("pos. rows: " + rows);
    	System.out.println("pos. cols: " + cols);
        for (int i = 0; i < rows; i++) {
        	ArrayList<Node> newRow = new ArrayList<Node>();
        	this.searchArea.add(newRow);
            for (int j = 0; j < cols; j++) {
                Node node = new Node(i, j);
                node.calculateHeuristic(getFinalNode());
                this.searchArea.get(i).add(node);
            }
        }
    }

//    public void setObstacles(int[][] obstaclesArray) {
//        for (int i = 0; i < obstaclesArray.length; i++) {
//            int row = obstaclesArray[i][0];
//            int col = obstaclesArray[i][1];
//            setObstacle(row, col);
//        }
//    }
    
    public void setObstacles(ArrayList<ArrayList<Integer>> obstacles) {
        for (int i = 0; i < obstacles.size(); i++) {
        	//the 0 and 1 might be flipped
            int row = obstacles.get(i).get(0);
            int col = obstacles.get(i).get(1);
            setObstacle(row, col);
        }
    }
    
    private void setObstacle(int row, int col) {
    	//Parameter: row and col of the actual Node
    	this.searchArea.get(row+xOffset).get(col+yOffset).setObstacle(true);
//    	this.searchArea.get(row).get(col).setObstacle(true);
//        this.searchArea[row][col].setObstacle(true);
    }

    public List<Node> findPath() {
        openList.add(initialNode);
        while (!isEmpty(openList)) {
            Node currentNode = openList.poll();
            closedSet.add(currentNode);
            if (isFinalNode(currentNode)) {
                return getPath(currentNode);
            } else {
                addAdjacentNodes(currentNode);
            }
        }
        return new ArrayList<Node>();
    }

    private List<Node> getPath(Node currentNode) {
        List<Node> path = new ArrayList<Node>();
        path.add(currentNode);
        Node parent;
        while ((parent = currentNode.getParent()) != null) {
            path.add(0, parent);
            currentNode = parent;
        }
        return path;
    }

    private void addAdjacentNodes(Node currentNode) {
        addAdjacentUpperRow(currentNode);
        addAdjacentMiddleRow(currentNode);
        addAdjacentLowerRow(currentNode);
    }

    private void addAdjacentLowerRow(Node currentNode) {
        int row = currentNode.getRow();
        int col = currentNode.getCol();
        int lowerRow = row + 1;
        if (lowerRow < getSearchArea().size()-xOffset) {
            if (col - 1 >= -yOffset) {
                checkNode(currentNode, col - 1, lowerRow, getDiagonalCost()); // Comment this line if diagonal movements are not allowed
            }
            if (col + 1 < getSearchArea().get(0).size()-yOffset) {
                checkNode(currentNode, col + 1, lowerRow, getDiagonalCost()); // Comment this line if diagonal movements are not allowed
            }
            checkNode(currentNode, col, lowerRow, getHvCost());
        }
    }

    private void addAdjacentMiddleRow(Node currentNode) {
        int row = currentNode.getRow();
        int col = currentNode.getCol();
        int middleRow = row;
        if (col - 1 >= -yOffset) {
            checkNode(currentNode, col - 1, middleRow, getHvCost());
        }
        if (col + 1 < getSearchArea().get(0).size()-yOffset) {
            checkNode(currentNode, col + 1, middleRow, getHvCost());
        }
    }

    private void addAdjacentUpperRow(Node currentNode) {
        int row = currentNode.getRow();
        int col = currentNode.getCol();
        int upperRow = row - 1;
        if (upperRow >= -xOffset) {
            if (col - 1 >= -yOffset) {
                checkNode(currentNode, col - 1, upperRow, getDiagonalCost()); // Comment this if diagonal movements are not allowed
            }
            if (col + 1 < getSearchArea().get(0).size()-yOffset) {
                checkNode(currentNode, col + 1, upperRow, getDiagonalCost()); // Comment this if diagonal movements are not allowed
            }
            checkNode(currentNode, col, upperRow, getHvCost());
        }
    }

    private void checkNode(Node currentNode, int col, int row, int cost) {
    	//Parameters: currentNode, the col and row of the node itself (not the col and row of searchArea), and cost
//        Node adjacentNode = getSearchArea()[row][col];
    	System.out.println("currentNode: " + currentNode + " " + currentNode.printStatus());
        Node adjacentNode = getSearchArea().get(row+xOffset).get(col+yOffset);
        System.out.println("adjacentNode: " + adjacentNode + " " + adjacentNode.printStatus());
//        if (!adjacentNode.isBlock() && !getClosedSet().contains(adjacentNode)) {
        if (!adjacentNode.obstacle && !getClosedSet().contains(adjacentNode)) {
            if (!getOpenList().contains(adjacentNode)) {
                adjacentNode.setNodeData(currentNode, cost);
                getOpenList().add(adjacentNode);
            } else {
                boolean changed = adjacentNode.checkBetterPath(currentNode, cost);
                if (changed) {
                    // Remove and Add the changed node, so that the PriorityQueue can sort again its contents with the modified "finalCost" value of the modified node
                    getOpenList().remove(adjacentNode);
                    getOpenList().add(adjacentNode);
                }
            }
        }
    }

    private boolean isFinalNode(Node currentNode) {
        return currentNode.equals(finalNode);
    }

    private boolean isEmpty(PriorityQueue<Node> openList) {
        return openList.size() == 0;
    }

    public Node getInitialNode() {
        return initialNode;
    }

    public void setInitialNode(Node initialNode) {
        this.initialNode = initialNode;
    }

    public Node getFinalNode() {
        return finalNode;
    }

    public void setFinalNode(Node finalNode) {
        this.finalNode = finalNode;
    }

    public ArrayList<ArrayList<Node>> getSearchArea() {
        return searchArea;
    }

    public void setSearchArea(ArrayList<ArrayList<Node>> searchArea) {
        this.searchArea = searchArea;
    }

    public PriorityQueue<Node> getOpenList() {
        return openList;
    }

    public void setOpenList(PriorityQueue<Node> openList) {
        this.openList = openList;
    }

    public Set<Node> getClosedSet() {
        return closedSet;
    }

    public void setClosedSet(Set<Node> closedSet) {
        this.closedSet = closedSet;
    }

    public int getHvCost() {
        return hvCost;
    }

    public void setHvCost(int hvCost) {
        this.hvCost = hvCost;
    }
    
    public int getXOffset() {
    	return xOffset;
    }
    
    public void setXOffset(int newOffset) {
    	if (newOffset > xOffset) {
    		int diff = newOffset-xOffset;
    		for (int i=0; i<diff; i++) {	//Adding diff number of rows
    			ArrayList<Node> newRow = new ArrayList<Node>();
    			for (int j=0; j<searchArea.get(0).size(); j++) {	//Add new nodes to newRow
    				newRow.add(new Node(-xOffset-i-1, j-yOffset));
    			}
    			searchArea.add(0, newRow);
    		}
    		xOffset = newOffset;
    	}
    }
    
    public int getYOffset() {
    	return yOffset;
    }
    
    public void setYOffset(int newOffset) {
    	if (newOffset > yOffset) {
    		int diff = newOffset-yOffset;
    		for (int i=0; i<searchArea.size(); i++) {
    			for (int j=0; j<diff; j++) {
    				searchArea.get(i).add(0, new Node(i-xOffset, -yOffset-j-1));
    			}
    		}
    		yOffset = newOffset;
    	}
    }

    private int getDiagonalCost() {
        return diagonalCost;
    }

    private void setDiagonalCost(int diagonalCost) {
        this.diagonalCost = diagonalCost;
    }
    
    public void printSearchArea() {
    	String result = "";
        for(int i = 0; i < searchArea.size(); i++){
            for(int j = 0; j < searchArea.get(i).size(); j++){
                result += searchArea.get(i).get(j).toString() + "\t";
            }
            // System.out.println();
            result += "\n";
        }
        System.out.println(result+"\n");
    }
    
//    public void printObstacleArea() {
//    	String result = "\t";
//    	//Printing labels above the map
//    	for(int j = 0; j < searchArea.get(0).size(); j++){
//    		if (j-yOffset<0) {	//Account for spacing of the minus sign
//    			result += j-yOffset;
//    		}
//    		else {
//    			result += " " + (j-yOffset);
//    		}
//        }
//    	result += "\n";
//    	
//    	//Printing the actual map
//        for(int i = 0; i < searchArea.size(); i++){
//        	result += i-xOffset + "\t" + " ";
//            for(int j = 0; j < searchArea.get(i).size(); j++){
//                result += searchArea.get(i).get(j).printStatus()+" ";
//            }
//            // System.out.println();
//            result += "\n";
//        }
//        System.out.println(result+"\n");
//    }
    
    public void printObstacleArea() {
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
        for(int i = 0; i < searchArea.get(0).size(); i++){
        	result += i-yOffset + "\t" + " ";
            for(int j = 0; j < searchArea.size(); j++){
                result += searchArea.get(j).get(i).printStatus()+" ";
            }
            // System.out.println();
            result += "\n";
        }
        System.out.println(result+"\n");
    }
    
}