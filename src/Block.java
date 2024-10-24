package src;

import java.util.ArrayList;
import java.util.Random;
import java.awt.Color;

public class Block {

	private int xCoord;
	private int yCoord;
	private int size; // height/width of the square
	private int level; // the root (outer most block) is at level 0
	private int maxDepth;
	private Color color;
	private Block[] children; // {UR, UL, LL, LR}

	public static Random gen = new Random(2);

	/*
	 * These two constructors are here for testing purposes.
	 */
	public Block() {
	}

	public Block(int x, int y, int size, int lvl, int maxD, Color c, Block[] subBlocks) {
		this.xCoord = x;
		this.yCoord = y;
		this.size = size;
		this.level = lvl;
		this.maxDepth = maxD;
		this.color = c;
		this.children = subBlocks;
	}

	/*
	 * Creates a random block given its level and a max depth.
	 * 
	 * xCoord, yCoord, size, and highlighted should not be initialized
	 * (i.e. they will all be initialized by default)
	 */
	public Block(int lvl, int maxDepth) {
		if (maxDepth <= 0) {
			throw new IllegalArgumentException("Negative or 0 MaxDepth");
		}
		if (lvl < 0) {
			throw new IllegalArgumentException("Negative or lvl");
		}
		if (lvl > maxDepth) {
			throw new IllegalArgumentException("lvl > maxDepth");
		}
		// if(children.length != 4 && children.length != 0 ){
		// throw new IllegalArgumentException("your children doesn't have 0 or 4");
		// }

		// if(size <= 0){
		// throw new IllegalArgumentException("size <= 0");
		// }

		level = lvl;
		this.maxDepth = maxDepth;
		if (level < maxDepth) {
			// bound of one
			if (gen.nextDouble(1) < Math.exp(-0.25 * level)) {
				this.children = new Block[4];
				for (int i = 0; i < 4; i++) {
					this.children[i] = new Block(level + 1, maxDepth);
				}
			} else {
				this.children = new Block[0];
				this.color = GameColors.BLOCK_COLORS[gen.nextInt(GameColors.BLOCK_COLORS.length)];
			}
		} else {
			this.children = new Block[0];
			this.color = GameColors.BLOCK_COLORS[gen.nextInt(GameColors.BLOCK_COLORS.length)];
		}
	}

	/*
	 * Updates size and position for the block and all of its sub-blocks, while
	 * ensuring consistency between the attributes and the relationship of the
	 * blocks.
	 * 
	 * The size is the height and width of the block. (xCoord, yCoord) are the
	 * coordinates of the top left corner of the block.
	 */
	public void updateSizeAndPosition(int size, int xCoord, int yCoord) {
		if (size <= 0) {
			throw new IllegalArgumentException("Size <= 0");
		}
		if (size % 2 != 0 && level < maxDepth) {
			throw new IllegalArgumentException("Size must be divisible by 2.");
		}
		if (size % Math.pow(2, maxDepth - level) != 0) {
			throw new IllegalArgumentException("size not divisible by 2**maxDepth");
		}
		// if(size > xCoord * yCoord ){
		// throw new IllegalArgumentException("size and coordinates ");
		// }

		this.size = size;
		this.xCoord = xCoord;
		this.yCoord = yCoord;

		if (children.length == 4) {
			int half = size / 2;
			children[0].updateSizeAndPosition(half, xCoord + half, yCoord);
			children[1].updateSizeAndPosition(half, xCoord, yCoord);
			children[2].updateSizeAndPosition(half, xCoord, yCoord + half);
			children[3].updateSizeAndPosition(half, xCoord + half, yCoord + half);
		}
	}

	/*
	 * Returns a List of blocks to be drawn to get a graphical representation of
	 * this block.
	 * 
	 * This includes, for each undivided Block:
	 * - one BlockToDraw in the color of the block
	 * - another one in the FRAME_COLOR and stroke thickness 3
	 * 
	 * Note that a stroke thickness equal to 0 indicates that the block should be
	 * filled with its color.
	 * 
	 * The order in which the blocks to draw appear in the list does NOT matter.
	 */

	public ArrayList<BlockToDraw> getBlocksToDraw() {
		/*
		 * ADD YOUR CODE HERE
		 */
		ArrayList<BlockToDraw> BlockToDraw = new ArrayList<BlockToDraw>();
		// BlockToDraw OuterBox2 = new
		// BlockToDraw(GameColors.FRAME_COLOR,this.xCoord,this.yCoord,this.size, 3);
		// BlockToDraw.add(OuterBox2);
		if (children.length == 4) {
			for (Block smaller_block : children) {
				// using recursion to add all child block to the biggerr arraylist
				ArrayList<BlockToDraw> ChildToDraw = smaller_block.getBlocksToDraw();
				BlockToDraw.addAll(ChildToDraw);
			}
		} else if (children.length == 0) {
			BlockToDraw Box = new BlockToDraw(color, xCoord, yCoord, size, 0);
			BlockToDraw.add(Box);

			BlockToDraw OuterBox = new BlockToDraw(GameColors.FRAME_COLOR, xCoord, yCoord, size, 3);
			BlockToDraw.add(OuterBox);
		} else {
			throw new IllegalArgumentException("Children not 0 or 4");
		}

		return BlockToDraw;
	}

	/*
	 * This method is provided and you should NOT modify it.
	 */
	public BlockToDraw getHighlightedFrame() {
		return new BlockToDraw(GameColors.HIGHLIGHT_COLOR, this.xCoord, this.yCoord, this.size, 5);
	}

	/*
	 * Return the Block within this Block that includes the given location
	 * and is at the given level. If the level specified is lower than
	 * the lowest block at the specified location, then return the block
	 * at the location with the closest level value.
	 * 
	 * The location is specified by its (x, y) coordinates. The lvl indicates
	 * the level of the desired Block. Note that if a Block includes the location
	 * (x, y), and that Block is subdivided, then one of its sub-Blocks will
	 * contain the location (x, y) too. This is why we need lvl to identify
	 * which Block should be returned.
	 * 
	 * Input validation:
	 * - this.level <= lvl <= maxDepth (if not throw exception)
	 * - if (x,y) is not within this Block, return null.
	 */
	private int getSelectedBlockIndex(int x, int y) {
		// we have four possibilities either
		// index = 0 - > Up Right
		// index = 1 -> Up Left
		// index = 2 -> Low Left
		// index = 3 -> Low Right
		// {UR, UL, LL, LR}

		// finding centers
		int cenX = xCoord + size / 2;
		int cenY = yCoord + size / 2;
		if (y < cenY) {
			if (x >= cenX) {
				return 0;
			} else {
				return 1;
			}
		} else {
			if (x < cenX) {
				return 2;
			} else {
				return 3;
			}
		}
	}

	public Block getSelectedBlock(int x, int y, int lvl) {
		/*
		 * ADD YOUR CODE HERE
		 */

		if (lvl > maxDepth || lvl < level) {
			throw new IllegalArgumentException("Your input lvl is wrong");
		}

		if (x < xCoord || x >= xCoord + size) {
			return null;
		}

		if (y < yCoord || y >= yCoord + size) {
			return null;
		}

		if (level == lvl || children.length == 0) {
			return this;
		}

		if (children.length != 4 && children.length != 0) {
			throw new IllegalArgumentException("children.length not 4 or 0");
		}

		Block chosenB = children[getSelectedBlockIndex(x, y)].getSelectedBlock(x, y, lvl);

		return chosenB;

	}

	/*
	 * Swaps the child Blocks of this Block.
	 * If input is 1, swap vertically. If 0, swap horizontally.
	 * If this Block has no children, do nothing. The swap
	 * should be propagate, effectively implementing a reflection
	 * over the x-axis or over the y-axis.
	 * 
	 */
	public void reflect(int direction) {

		if (direction != 0 && direction != 1) {
			throw new IllegalArgumentException("direction is not 1 or 0");
		}

		if (this.children.length == 4) {
			if (direction == 1) {
				Block tmp_block1 = children[0];
				Block tmp_block2 = children[2];
				this.children[0] = this.children[1];
				this.children[1] = tmp_block1;
				this.children[2] = this.children[3];
				this.children[3] = tmp_block2;

				this.updateSizeAndPosition(size, xCoord, yCoord);

				if (this.level <= maxDepth - 1) {
					for (Block subBlock : children) {
						subBlock.reflect(direction);
					}
				}
			}

			else {
				Block tmp_block0 = children[0];
				Block tmp_block1 = children[1];
				Block tmp_block2 = children[2];
				Block tmp_block3 = children[3];

				this.children[0] = tmp_block3;
				this.children[1] = tmp_block2;
				this.children[2] = tmp_block1;
				this.children[3] = tmp_block0;

				this.updateSizeAndPosition(size, xCoord, yCoord);

				if (this.level <= maxDepth - 1) {
					for (Block subBlock : children) {
						subBlock.reflect(direction);
					}
				}
			}
		}

		else if (children.length == 0) {
			this.updateSizeAndPosition(this.size, xCoord, yCoord);
			return;
		} else {
			throw new IllegalArgumentException(" children !=4 and children != 1");
		}
	}

	/*
	 * Rotate this Block and all its descendants.
	 * If the input is 1, rotate clockwise. If 0, rotate
	 * counterclockwise. If this Block has no children, do nothing.
	 */
	public void rotate(int direction) {

		if (direction != 0 && direction != 1) {
			throw new IllegalArgumentException("direction is not 1 or 0");
		}
		// if(this.children.length ==0){
		// return;
		// }
		if (this.children.length == 4) {
			if (direction == 1) {
				Block tmp_block = children[0];
				// Block tmp_block2 = children[2];
				this.children[0] = this.children[1];
				this.children[1] = this.children[2];
				this.children[2] = this.children[3];
				this.children[3] = tmp_block;

				this.updateSizeAndPosition(size, xCoord, yCoord);

				if (this.level <= maxDepth - 1) {
					for (Block subBlock : children) {
						subBlock.rotate(direction);
					}
				}
			} else {
				Block tmp_block0 = children[0];
				Block tmp_block1 = children[1];
				Block tmp_block2 = children[2];
				Block tmp_block3 = children[3];
				this.children[0] = tmp_block3;
				this.children[1] = tmp_block0;
				this.children[2] = tmp_block1;
				this.children[3] = tmp_block2;

				this.updateSizeAndPosition(size, xCoord, yCoord);

				if (this.level <= maxDepth - 1) {
					for (Block subBlock : children) {
						subBlock.rotate(direction);
					}
				}
			}
		} else if (children.length == 0) {
			this.updateSizeAndPosition(this.size, xCoord, yCoord);
			return;
		} else {
			throw new IllegalArgumentException(" children !=4 and children != 1");
		}
	}

	/*
	 * Smash this Block.
	 * 
	 * If this Block can be smashed,
	 * randomly generate four new children Blocks for it.
	 * (If it already had children Blocks, discard them.)
	 * Ensure that the invariants of the Blocks remain satisfied.
	 * 
	 * A Block can be smashed iff it is not the top-level Block
	 * and it is not already at the level of the maximum depth.
	 * 
	 * Return True if this Block was smashed and False otherwise.
	 * 
	 */
	public boolean smash() {

		if (this.level > 0 && this.level < maxDepth) {

			this.children = new Block[4];
			for (int i = 0; i < children.length; i++) {
				children[i] = new Block(this.level + 1, maxDepth);
			}
			updateSizeAndPosition(size, xCoord, yCoord);
			return true;
		}
		return false;
	}

	/*
	 * Return a two-dimensional array representing this Block as rows and columns of
	 * unit cells.
	 * 
	 * Return and array arr where, arr[i] represents the unit cells in row i,
	 * arr[i][j] is the color of unit cell in row i and column j.
	 * 
	 * arr[0][0] is the color of the unit cell in the upper left corner of this
	 * Block.
	 */
	public Color[][] flatten() {
		// findiing size at each floor
		int side_length = (int) Math.pow(2, maxDepth - level);
		// creating array
		Color[][] flatten_array = new Color[side_length][side_length];

		if (children.length == 0) {
			for (int i = 0; i < side_length; i++) {
				for (int j = 0; j < side_length; j++) {
					flatten_array[i][j] = color;
				}

			}
		}

		else if (children.length == 4) {
			// {UR, UL, LL, LR}
			Color[][] UR = children[0].flatten();
			Color[][] UL = children[1].flatten();
			Color[][] LL = children[2].flatten();
			Color[][] LR = children[3].flatten();

			int childSideSize = side_length / 2;

			/*
			 * children[0].updateSizeAndPosition(halfSize, xCoord + halfSize , yCoord); UR
			 * children[1].updateSizeAndPosition(halfSize, xCoord, yCoord); UL
			 * children[2].updateSizeAndPosition(halfSize, xCoord, yCoord + halfSize); LL
			 * children[3].updateSizeAndPosition(halfSize, xCoord + halfSize, yCoord +
			 * halfSize); LR
			 */
			// remember that here thing are inversed -> [i] represent row and [j] coloumns
			// this is no a mistake, different from earlier

			// i | i
			// j | j + hs
			// ---------------
			// i + hs | i + hs
			// j | j + hs

			// UL
			for (int i = 0; i < childSideSize; i++) {
				for (int j = 0; j < childSideSize; j++) {

					flatten_array[i][j] = UL[i][j];

				}
			}
			// UR
			for (int a = 0; a < childSideSize; a++) {
				for (int b = 0; b < childSideSize; b++) {
					flatten_array[a][b + childSideSize] = UR[a][b];
				}
			}
			// LL
			for (int c = 0; c < childSideSize; c++) {
				for (int d = 0; d < childSideSize; d++) {
					flatten_array[c + childSideSize][d] = LL[c][d];

				}
			}
			// LR
			for (int e = 0; e < childSideSize; e++) {
				for (int f = 0; f < childSideSize; f++) {
					flatten_array[e + childSideSize][f + childSideSize] = LR[e][f];

				}
			}
		}

		else {
			throw new IllegalArgumentException("children.length != 0 and != 4");
		}
		return flatten_array;
	}

	// These two get methods have been provided. Do NOT modify them.
	public int getMaxDepth() {
		return this.maxDepth;
	}

	public int getLevel() {
		return this.level;
	}

	/*
	 * The next 5 methods are needed to get a text representation of a block.
	 * You can use them for debugging. You can modify these methods if you wish.
	 */
	public String toString() {
		return String.format("pos=(%d,%d), size=%d, level=%d", this.xCoord, this.yCoord, this.size, this.level);
	}

	public void printBlock() {
		this.printBlockIndented(0);
	}

	private void printBlockIndented(int indentation) {
		String indent = "";
		for (int i = 0; i < indentation; i++) {
			indent += "\t";
		}

		if (this.children.length == 0) {
			// it's a leaf. Print the color!
			String colorInfo = GameColors.colorToString(this.color) + ", ";
			System.out.println(indent + colorInfo + this);
		} else {
			System.out.println(indent + this);
			for (Block b : this.children)
				b.printBlockIndented(indentation + 1);
		}
	}

	private static void coloredPrint(String message, Color color) {
		System.out.print(GameColors.colorToANSIColor(color));
		System.out.print(message);
		System.out.print(GameColors.colorToANSIColor(Color.WHITE));
	}

	public void printColoredBlock() {
		Color[][] colorArray = this.flatten();
		for (Color[] colors : colorArray) {
			for (Color value : colors) {
				String colorName = GameColors.colorToString(value).toUpperCase();
				if (colorName.length() == 0) {
					colorName = "\u2588";
				} else {
					colorName = colorName.substring(0, 1);
				}
				coloredPrint(colorName, value);
			}
			System.out.println();
		}
	}
	// public static void main(String arg[]){

	// Block blockDepth3 = new Block(0,2);
	// blockDepth3.printBlock();

	// System.out.println("--------------");

	// Block blockDepth2 = new Block(0,2);
	// blockDepth2.updateSizeAndPosition(16, 0, 0);
	// blockDepth2.printBlock();

	// System.out.println("--------------");
	// Block blockDepth3 = new Block(0,3);
	// blockDepth3.updateSizeAndPosition(16, 0, 0);

	// Block b1 = blockDepth3.getSelectedBlock(2, 15, 1);
	// b1.printBlock();

	// System.out.println("--------------");
	// Block b2 = blockDepth3.getSelectedBlock(3, 5, 2);
	// b2.printBlock();
	// blockDepth3.printBlock();
	// System.out.println("");
	// System.out.println("--------------");
	// System.out.println("");
	// blockDepth3.reflect(1);
	// blockDepth3.printBlock();
	// printColoredBlock();
	// Block block = new Block(0, 4);
	// Color[][] flattened = block.flatten();
	// block.printColoredBlock();
	// }
}
