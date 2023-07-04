package assignment3;

import java.awt.Color;

public class PerimeterGoal extends Goal{

	public PerimeterGoal(Color c) {
		super(c);
	}

	private int SizeBlob(){
		return 1;

	}

	@Override
	public int score(Block board) {
		/*
		 * ADD YOUR CODE HERE
		 */

		Color targetC = targetGoal;
		Color[][] flattened = board.flatten();

		int initial_size = 0;
		int sideLength = flattened.length - 1;

		for (int tmp = 0; tmp < flattened.length; tmp++) {
			if(flattened[0][tmp] == targetC){
				initial_size += 1;
			}
			if(flattened[sideLength][tmp] == targetC){
				initial_size += 1;
			}
			if(flattened[tmp][0] == targetC){
				initial_size += 1;
			}
			if(flattened[tmp][sideLength] == targetC){
				initial_size += 1;
			}
		}
		/*
		// check top bound
		for (int j = 0; j < flattened.length; j++) {
			if(flattened[0][j] == targetC){
				initial_size += 1;
			}
		}
		// check bottom bound
		for (int j = 0; j < flattened.length; j++) {
			if(flattened[sideLength][j] == targetC){
				initial_size += 1;
			}
		}
		// check left side
		for (int i = 0; i < flattened.length; i++) {
			if(flattened[i][0] == targetC){
				initial_size += 1;
			}
		}
		// check right side
		for (int i = 0; i < flattened.length; i++) {
			if(flattened[i][sideLength] == targetC){
				initial_size += 1;
			}
		}*/
		return initial_size;
	}

	@Override
	public String description() {
		return "Place the highest number of " + GameColors.colorToString(targetGoal) 
		+ " unit cells along the outer perimeter of the board. Corner cell count twice toward the final score!";
	}

}
