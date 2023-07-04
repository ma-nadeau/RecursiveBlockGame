package assignment3;

import java.awt.Color;
import java.util.ArrayList;

public class BlobGoal extends Goal{

	public BlobGoal(Color c) {
		super(c);
	}

	@Override
	public int score(Block board) {

		int score = 0;
		Color[][] flatten = board.flatten();
		boolean[][] visited = new boolean[flatten.length][flatten[0].length];

		for (int i = 0; i < flatten.length; i++){
			for (int j = 0; j < flatten[0].length; j++){
				visited[i][j] = false;
			}
		}
		for (int i = 0; i < flatten.length; i++){
			for (int j = 0; j < flatten[0].length; j++){
				int blob_size = undiscoveredBlobSize(i, j, flatten, visited);
				if(blob_size > score){
					score = blob_size;
				}
			}
		}
		return score;
	}


	@Override
	public String description() {
		return "Create the largest connected blob of " + GameColors.colorToString(targetGoal) 
		+ " blocks, anywhere within the block";
	}

	public int undiscoveredBlobSize(int i, int j, Color[][] unitCells, boolean[][] visited) {

		if(i < 0 || i > unitCells.length - 1 )

		{
			return 0;
			//throw new IllegalArgumentException("i coordinate is not in the bounds");
		}

		if(j < 0 || j > unitCells[0].length - 1 )

		{
			return 0;
			//throw new IllegalArgumentException("j coordinate is not in the bounds");
		}
		//checks if it has been visited (i.e. value is true). Then is returns 0;
		if(visited[i][j])

		{
			return 0;
		}
		int initial_size = 1;

		visited[i][j] = true;

		if(unitCells[i][j] == targetGoal){

			int up_size;
			up_size = undiscoveredBlobSize( i + 1 , j , unitCells , visited);

			int down_size;
			down_size = undiscoveredBlobSize( i - 1 , j , unitCells , visited);

			int left_size;
			left_size = undiscoveredBlobSize( i , j + 1 , unitCells , visited);

			int right_size;
			right_size = undiscoveredBlobSize( i , j - 1, unitCells , visited);

			int total_size;
			total_size = initial_size + up_size + down_size + left_size + right_size;

			return total_size;}
		else
		{
			return 0;
		}}

}
