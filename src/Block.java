import java.util.ArrayList;
import javafx.scene.paint.Color;

/**
 * 
 * @author jiajiachen
 *This class keeps the color and the shape of the blocks
 */
public class Block {
	 /*
	    * block0  *
	    */
		Color c0 = Color.rgb(255, 204, 204);
		Color b = Color.GRAY;
		Color[][] b0 = 
			{
			 {c0, b},
			 {b, b}
			};
		
		/*
		 * block1 *
		 *        *
		 */
		Color c1 = Color.rgb(255,204,204);
		Color[][] b1 = 
			{
			 {c1, b},
			 {c1, b}
			};
		/*
		 * block2 * *
		 */
		Color c2 = Color.rgb(255,204,204);
		Color[][] b2 =
			{
			 {c2, c2},
			 {b, b},
			};
		/*
		 * block3 *
		 *        *
		 *        *
		 */
		Color c3 = Color.rgb(153,255,153);
		Color[][] b3 =
			{
			 {c3, b, b},
			 {c3, b, b},
			 {c3, b, b}
			};
		/*
		 * block4 * * *
		 */
		Color c4 = Color.rgb(153,255,153);
		Color[][] b4 =
			{
			 {c4, c4, c4},
			 {b, b, b},
			 {b, b, b}
			};
		/*
		 * block5 *
		 *        * *
		 */
		Color c5 = Color.rgb(255,153,153);
		Color[][] b5 =
			{
			 {c5, b},
			 {c5, c5}
			};
		/*
		 * block6 * *
		 *          *
		 */
		Color c6 = Color.rgb(255,153,153);
		Color[][] b6 =
			{
			 {c6, c6},
			 {b, c6}		
			};
		/*
		 * block7 * * * *
		 */
		Color c7 = Color.rgb(204,153,255);
		Color[][] b7 =
			{
			 {c7, c7, c7, c7},
			 {b, b, b, b},
			 {b, b, b, b},
			 {b, b, b, b}
			};
		/*
		 * block8 *
		 *        *
		 *        *
		 *        *
		 */
		Color c8 = Color.rgb(204,153,255);
		Color[][] b8 =
			{
			 {c8, b, b, b},
			 {c8, b, b, b},
			 {c8, b, b, b},
			 {c8, b, b, b}
			};
		/*
		 * block9 * * *
		 *            *
		 *            *
		 */
		Color c9 = Color.rgb(255,153,255);

		Color[][] b9 =
			{
			 {c9, c9, c9},
			 {b, b, c9},
			 {b, b, c9}
			};
		
		Color c10 = Color.rgb(255,153,255);
		/*
		 * block10 *
		 *         *
		 *         * * *
		 */
		Color[][] b10 =
			{
			 {c10, b, b},
			 {c10, b, b},
			 {c10, c10, c10}
			};
		
		Color c11 = Color.rgb(153,204,255);
		/*
		 * block11 * * *
		 *         * * *
		 *         * * *
		 */
		Color[][] b11 =
			{
			 {c11, c11, c11},
			 {c11, c11, c11},
			 {c11, c11, c11}
			};
		/*
		 * block12 * *
		 *         * *
		 */
		Color c12 = Color.rgb(255,255,153);
		Color[][] b12 =
			{
			 {c12, c12},
			 {c12, c12}
			};
	
    int Private;
    
	public ArrayList<Color[][]> createBlocks() {
		 ArrayList<Color[][]> blockSet = new ArrayList<Color[][]>();
		 blockSet.add(b0);
		 blockSet.add(b1);
		 blockSet.add(b2);
		 blockSet.add(b3);
		 blockSet.add(b4);
		 blockSet.add(b5);
		 blockSet.add(b6);
		 blockSet.add(b7);
		 blockSet.add(b8);
		 blockSet.add(b9);
		 blockSet.add(b10);
		 blockSet.add(b11);
		 blockSet.add(b12);
		 
		 return blockSet;
		 
	}

}
