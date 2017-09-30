package assn3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

public class StateSpace {
	private Node root;
	private int currentPlayer;

	/**
	 * constructor for the FIRST PLAY
	 * 
	 * @param col
	 * @param player
	 */
	public StateSpace(Node currentGameState, int currentPlayer) {
		this.root = currentGameState;
		this.currentPlayer = currentPlayer;
	}

	public StateSpace() {
		//Sole use is to access some of the public methods for detecting a win
	}
	
	public int getBotMove() {
		Node node = bestFirstSearch();
		if (node == null) {
			System.out.println("Node was null");
			return 0;
		}
		if (Game.TESTING) {
			System.out.println("________________________________________________________________________________________________________________");
			System.out.println("_____________________________________________STATE CHOSEN BY AI_________________________________________________");
			System.out.println("________________________________________________________________________________________________________________");
			printState(node);
		}
		
		return node.getPlayToNode();
	}

	private Node bestFirstSearch() {
		ArrayList<Node> openList = new ArrayList<Node>();
		LinkedList<Node> closedList = new LinkedList<Node>();
		openList.add(this.root);

		while (openList.size() > 0) {
			Node X = openList.get(0);

			if (isGoal(X)) {
				return X;
			} else {
				X.getAndSetChildren();
				if (Game.TESTING) {
					printChildren(X);
				}
				LinkedList<Node> childList= X.getChildrenList();
				Node node = null;
				for (int i = 0; i < childList.size(); i++) {
					if (!openList.contains(childList.get(i)) && !closedList.contains(childList.get(i))) {
						node = childList.get(i);
						node.setHeuristicValue(getHeuristicValue(node));
						openList.add(node);
						if (Game.TESTING) {
							System.out.printf("I got added to the open list. HVal = %d\n", getHeuristicValue(node));
						}
					} else if (openList.contains(childList.get(i))) {
						Node nodeInOpen = openList.get(openList.indexOf(childList.get(i)));
						node = childList.get(i);
						//distances
						int openNode = nodeInOpen.getPathToNode().size();
						int nodeDistance = node.getPathToNode().size();
						if (nodeDistance < openNode) {
							nodeInOpen.setPathToNode(node.getPathToNode());
							if (!Game.TESTING) {
								System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
								System.out.printf("++++++++++++++++++++++++++++++++++++Set Node in open list to a shorter path+++++++++++++++open node%d   ++++++++++++++++++++\n", openNode);
								System.out.printf("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++child node%d   +++++++++++++++++++++\n", nodeDistance);
							}
						}
					} else if (closedList.contains(childList.get(i))){
						Node nodeInClosed = closedList.get(closedList.indexOf(childList.get(i)));
						node = childList.get(i);
						//distances
						int closedNodeDistance = nodeInClosed.getPathToNode().size();
						int nodeDistance = node.getPathToNode().size();
						if (nodeDistance < closedNodeDistance) {
							openList.add(node);
							closedList.remove(node);
							if (!Game.TESTING) {
								System.out.println("================================================================================================================================");
								System.out.println("===================================Shorter Path: Node removed from closed and moved to open=====================================");
								System.out.println("================================================================================================================================");
							}
						}
					} else {
						if (!Game.TESTING) {
							System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
							System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&I HIT THE ELSE BLOCK&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
							System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");

						}
					}
				}
				closedList.add(X);
				if ((openList.size() != 0)) {
					openList.remove(0);
				}
				Collections.sort(openList); 
				if (!Game.TESTING) {
					printList(openList);
				}

			}

		}

		return null;
	}



	private boolean isGoal(Node node) {
		if (winningState(node) == 2) {
			return true;
		}else {
			return false;
		}
	}

	private int getHeuristicValue(Node node) {
		// any input on this Would be great! a higher integer value corresponds to a higher value.
		int heuristicVal = 0;
		if (winningState(node) == 2) { // involved with a bot winning
			heuristicVal = 20;
			if (!Game.TESTING) {
				System.out.println("**********************************FOUND WINNING STATE FOR BOT**************************************");
			}

		}else if (winningState(node) == 1) { //block this move
			heuristicVal =5; 
			if (!Game.TESTING) {
				System.out.println("**********************************FOUND WINNING STATE FOR USER**************************************");
			}
			
		}else { // a value to indicate a move that gets us closer to winning.  IE tiles in a row
			heuristicVal =  (int)(Math.random() * 10);
			
			if (!Game.TESTING) {
				System.out.println("**********************************RANDOM STATE GETTING US CLOSER TO A WIN****************************");
			}
		}
	
		return heuristicVal;
	}
	
	
	/**
	 * Prints all the children's matrixes of the passed node
	 * @param node
	 */
	private void printChildren(Node node) {
		LinkedList<Node> childList = node.getChildrenList();
		for(int k = 0; k < node.getChildrenList().size(); k++) {
			int[][] game = childList.get(k).getGameState();
			for (int i = game.length -1; i >=0 ; i--) {
				for (int j = 0; j < game[i].length; j++) {
					System.out.printf("%d  ", game[i][j]);
				}
				System.out.printf("       \n");
			}
			System.out.println("\n\n");
		}
	}
	
	/**
	 * Prints the heuristic Values for the open list or any passed list
	 */
	private void printList(ArrayList<Node> list) {
		System.out.println("--------------------------------------------------HEURISTIC VALUES LIST-------------------------------------------------");
		for (int i = 0; i < list.size(); i ++) {
			System.out.printf("%d || ", list.get(i).getHeuristicValue());
		}
		System.out.println("");
	}
	
	/**
	 * Prints the matrix of the passed node
	 * @param node
	 */
	public void printState(Node node) {
		int[][] game = node.getGameState();
		for (int i = game.length -1; i >=0 ; i--) {
			for (int j = 0; j < game[i].length; j++) {
				System.out.printf("%d  ", game[i][j]);
			}
			System.out.println("");
		}
		System.out.println("\n\n");
	}
	
		
	

	/** determine if there is a winning state **/

	public int winningState(Node currentState) {
		int playerWin = 0;

		playerWin += horizontalWinState(currentState);

		if (playerWin > 0) {
			return playerWin;
		}

		playerWin += verticalWinState(currentState);

		if (playerWin != 0) {
			return playerWin;
		}

		playerWin += diagonal1WinState(currentState);

		if (playerWin != 0) {
			return playerWin;
		}

		playerWin += diagonal2WinState(currentState);

		return playerWin;
	}

	/** Check for diagonal win (left to right, positive slope) **/

	private int diagonal1WinState(Node currentState) {

		int[][] gameState = currentState.getGameState();

		int player1Count = 0;
		int player2Count = 0;
		int a = 0; // col
		int slope = 1;
		// checks along the y axis
		for (int b = gameState.length - 1; b > 0; b--) {

			int x = diagonalCheck(gameState, b, a, player1Count, player2Count, slope);

			if (x != 0) {
				return x;
			}

		}

		// checks along the x axis
		int b = 0; // row
		while (a < gameState[0].length) {

			int x = diagonalCheck(gameState, b, a, player1Count, player2Count, slope);

			if (x != 0) {
				return x;
			}

			a++;
		}

		return 0;
	}

	/** Check for diagonal win (right to left, negative slope) **/

	private int diagonal2WinState(Node currentState) {

		int[][] gameState = currentState.getGameState();

		int player1Count = 0;
		int player2Count = 0;
		int a = 6; // col
		int slope = -1;
		// checks along the y axis
		for (int b = gameState.length - 1; b > 0; b--) {

			int x = diagonalCheck(gameState, b, a, player1Count, player2Count, slope);

			if (x != 0) {
				return x;
			}

		}

		// checks along the x axis
		int b = 0; // row
		while (a > 0) {

			int x = diagonalCheck(gameState, b, a, player1Count, player2Count, slope);

			if (x != 0) {
				return x;
			}

			a--;
		}

		return 0;

	}

	/** Check for vertical win **/

	private int verticalWinState(Node currentState) {

		int[][] gameState = currentState.getGameState();

		for (int a = 0; a < gameState.length + 1; a++) {

			int player1Count = 0;
			int player2Count = 0;

			for (int b = 0; b < gameState.length; b++) {

				int player = gameState[b][a];

				int[] playerCounts = addPlayerCount(player, player1Count, player2Count);

				player1Count = playerCounts[0];
				player2Count = playerCounts[1];

				if (player1Count == 4) {
					return 1;
				} else if (player2Count == 4) {
					return 2;
				}

			}

		}
		return 0;

	}

	/** Check for horizontal win **/

	private int horizontalWinState(Node currentState) {

		int[][] gameState = currentState.getGameState();

		for (int a = 0; a < gameState.length; a++) {

			int player1Count = 0;
			int player2Count = 0;

			for (int b = 0; b < gameState[a].length; b++) {
				int player = gameState[a][b];

				int[] playerCounts = addPlayerCount(player, player1Count, player2Count);

				player1Count = playerCounts[0];
				player2Count = playerCounts[1];

				if (player1Count == 4) {
					return 1;
				} else if (player2Count == 4) {
					return 2;
				}
			}

		}
		return 0;

	}

	/** add number of counts to appropriate player **/

	private int[] addPlayerCount(int player, int player1Count, int player2Count) {
		int[] playerCounts = new int[2];

		if (player == 2) {
			player2Count++;
			playerCounts[0] = 0;
			playerCounts[1] = player2Count;

		}

		else if (player == 1) {
			player1Count++;
			playerCounts[0] = player1Count;
			playerCounts[1] = 0;

		} else {
			playerCounts[0] = 0;
			playerCounts[1] = 0;

		}

		return playerCounts;

	}

	/** Check for diagonal win **/

	private int diagonalCheck(int[][] gameState, int row, int col, int player1Count, int player2Count, int slope) {

		while (row < gameState.length && col < gameState[0].length) {

			try {

				int player = gameState[row][col];
				int[] playerCounts = addPlayerCount(player, player1Count, player2Count);
				player1Count = playerCounts[0];
				player2Count = playerCounts[1];

			}

			catch (ArrayIndexOutOfBoundsException e) {
				// catch off board nodes
				return 0;
			}

			if (player1Count == 4) {
				return 1;
			} else if (player2Count == 4) {
				return 2;
			}

			if (slope == -1) {
				row++;
			} else {
				row += slope;
			}
			col += slope;

		}

		return 0;

	}
}
