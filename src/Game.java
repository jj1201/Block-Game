
import javafx.application.Application;
import javafx.event.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.Random;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import javafx.beans.property.*;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.input.TransferMode;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ButtonBar.ButtonData;
import sun.audio.*;
/**
 * 
 * @author Jiajia Chen
 * 
 * In this game, user can drag the blocks into the grid, if the
 * user can create a vertical line and a horizontal line full of
 * blocks, then this line of blocks will disappear. The game ends 
 * when there is no space to put any of the given blocks.
 *
 */
public class Game extends Application {

   boolean success = false;   //record whether a drop is done successfully
   private int[] record = new int[21];  //record the lines that are full
   private int boardSize = 10;
   private int brickSize = 40;
   private int topPadding = 130;
   private int leftPadding = 100;
   private Color[][] board = new Color[10][10];    // record the color of the grid
   private ArrayList<Color[][]> blockSet ;
   ImageView oldImageView = new ImageView();  //record which view contains which block
   int draggedBlock = -1;    //record which block is dragged now
   int score = 0; 
   HashMap<String, Integer> map = new HashMap<String, Integer>();
   
   @Override
   public void start(Stage applicationStage) throws 
   UnsupportedAudioFileException, IOException, LineUnavailableException {
	   blockSet = new Block().createBlocks();
	   //play the back ground music
	   FileInputStream in = new FileInputStream("sound/bgm1.wav");
	   AudioStream s = new AudioStream(in);     
	   AudioData audiodata = s.getData();
	   ContinuousAudioDataStream loop = new ContinuousAudioDataStream(audiodata);
	   AudioPlayer.player.start(loop);
	   
	  
	       //record the score the user get
	  Label labelScore = new Label();   //this label contains the score
	  labelScore.textProperty().bind(new SimpleIntegerProperty(score).asString());
	  labelScore.setFont(new Font(30.0));  // set the label's font
	  labelScore.setTextFill(Color.WHITE);  //set the label's color
	  labelScore.relocate(290, 50);        //put the label score in the top middle
	  Canvas canvas = new Canvas(400, 400);      // Create a canvas in which to draw
	  canvas.relocate(leftPadding, topPadding);
	  GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
	  Pane pane = new Pane();      
	  Label label = new Label("Score:");
	  label.setFont(new Font(30.0));
	  label.setTextFill(Color.WHITE);
	  label.relocate(250, 10);
                  // Create an empty pane    
      Scene scene = new Scene(pane);             // Create a scene containing the pane
      
      
      // Get the canvas' graphics context to draw
      
    
      //set the back ground color
      Color bkg = Color.BLACK;
      pane.setStyle("-fx-background-color: black");
      //set the block color
      Color block = Color.GRAY;
      graphicsContext.setFill(block); 
      //draw the 10 X 10 grid
      for(int i = 0; i < boardSize; i++) {
    	  for(int j = 0; j < boardSize; j++) {
    		  board[i][j] = Color.GRAY;
    		  graphicsContext.fillRoundRect( brickSize * i ,brickSize * j, 
    				  37, 37, 10, 10);
    		
    	  }
      }
      
     //import the blocks images
      Image[] blocks = new Image[13];
      for(int i = 0; i < 13; i++) {
    	  blocks[i] = new Image("/block" + i + ".png");
      }
      
      ImageView iv0 = new ImageView();
      ImageView iv1 = new ImageView();
      ImageView iv2 = new ImageView();
      
      GridPane gridPane = new GridPane();
      //put randoms blocks in the image view
      int randomInt = random(13);
      iv0.setImage(blocks[randomInt]);
      iv0.setId("0");
      map.put(iv0.getId(), randomInt);
      System.out.println("iv0:"+ iv0.getId());
      iv0.setFitHeight(blocks[randomInt].getHeight()/3);
      iv0.setPreserveRatio(true);
      
      randomInt = random(13);
      iv1.setImage(blocks[randomInt]);
      iv1.setId("1");
      map.put(iv1.getId(), randomInt);
      iv1.setFitHeight(blocks[randomInt].getHeight()/3);
      iv1.setPreserveRatio(true);
      randomInt = random(13);
      iv2.setImage(blocks[randomInt]);
      iv2.setId("2");
      map.put(iv2.getId(), randomInt);
      iv2.setFitHeight(blocks[randomInt].getHeight()/3);
      iv2.setPreserveRatio(true);
      
      
      Resize resize = new Resize();
      //resize is happening when dragged
      iv0.setOnDragDetected(resize);
      iv1.setOnDragDetected(resize);
      iv2.setOnDragDetected(resize);
    
      //when the drag is done
      pane.setOnDragDone(e -> {
    	  /* the drag-and-drop gesture ended */
          System.out.println("onDragDone");
          //if the drop is not successful, play the fail sound
          if(!success) {
        	 dropfail();
        	 System.out.println("drop fail");
        	 oldImageView.setVisible(true);
        	 return;
          }
          //detect which line should disappear
          record = disappear(board);
          if(record[0] != -1) {  //it means there is at least one row or one column that is full
        	  
	       	   for(int i = 1; i < 11; i++) {
	       		   if(record[i] == 1) {
	       			   disappearSound();
	       				//add the score
	       			   score = score + 100;
	       			   labelScore.textProperty().bind(new SimpleIntegerProperty(score).asString());
	       			   //change the color to gray
	       			   for(int j = 0; j < boardSize; j++) {	
	       				   board[i - 1][j] = Color.GRAY;
	       			   }
	       		   }
	       	   }
       	   
       	   for(int i = 11; i < 21; i++) {
       		   if(record[i] == 1) {
       			disappearSound();
       			   score = score + 100;
       			   labelScore.textProperty().bind(new SimpleIntegerProperty(score).asString());
       			   for(int j = 0; j < boardSize; j++) {
       				   board[j][i-11] = Color.GRAY;
       		       }
       		   }
       	   }
          }
       	
       	graphicsContext.clearRect(0, 0, brickSize * 10, brickSize * 10);
       	//redraw the grid from the updated board
         for(int i = 0; i < boardSize; i++) {
	       	  for(int j = 0; j < boardSize; j++) {
	       		  graphicsContext.setFill(board[j][i]); 
	       		  graphicsContext.fillRoundRect(brickSize * i , brickSize * j, 
       				  37, 37, 10, 10);
	       		
	       	  }
         }
     
        int blockNum1 = map.get(iv0.getId()); //block in iv0
        int blockNum2 = map.get(iv1.getId()); //block in iv1
        int blockNum3 = map.get(iv2.getId()); //block in iv2
       
       	if(!keepPlay(blockSet.get(blockNum1), blockSet.get(blockNum2), blockSet.get(blockNum3), 
       			board)) { //if there is no space left for any given blocks, show an alert
       		Alert alert = new Alert(AlertType.CONFIRMATION);
       		alert.setTitle("Oops! Out of moves! ><");
       		alert.setHeaderText("Out of moves! ><");
       		alert.setContentText("Do you want to retry?");

       		ButtonType buttonTypeOne = new ButtonType("Retry");
       		ButtonType buttonTypeTwo = new ButtonType("No",ButtonData.CANCEL_CLOSE);

       		alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo);

       		Optional<ButtonType> result = alert.showAndWait();
       		if (result.get() == buttonTypeOne){  //if the user want to replay the game
       		 //redraw the grid
       		 for(int i = 0; i < boardSize; i++) {
	           	  for(int j = 0; j < boardSize; j++) {
	           		  board[i][j] = Color.GRAY;
	           		  graphicsContext.setFill(Color.GRAY);
	           		  graphicsContext.fillRoundRect( brickSize * i ,brickSize * j, 
	           				  37, 37, 10, 10);
	           		
	           	  }
             }
       		 	//clear the score
       		 	score = 0;
 			   labelScore.textProperty().bind(new SimpleIntegerProperty(score).asString());
       		 
       		} else if (result.get() == buttonTypeTwo) {
       		    
       		} 
       	}
       	  dropdone(); 
       	  if(success)
       		  success = false;
          
          e.consume();
    	  
      });
      // when a block is dropped
      pane.setOnDragDropped(e -> {
    	  /* data dropped */
		 //  ImageView iv = (ImageView)e.getSource();
          System.out.println("onDragDropped");
          /* if there is a image data on dragboard, read it and use it */
          Dragboard db = e.getDragboard();
         
          Image img = db.getImage();
          //to line up the coordinate
          double leftTopX = e.getSceneX() - img.getWidth()/2;
          double leftTopY = e.getSceneY() - img.getHeight()/2;
          double rightBottonX = e.getSceneX() + img.getWidth()/2;
          double rightBottonY = e.getSceneY() + img.getHeight()/2;
          leftTopX = locateX(leftTopX);
          leftTopY = locateY(leftTopY);
          int X = (int)(leftTopX - 100) / 40;
          int Y = (int)(leftTopY - 130) / 40;
          System.out.println("X:" + X);
          System.out.println("Y:" + Y);
          oldImageView.setVisible(true);
          System.out.println(draggedBlock);
          System.out.println(rightX(rightBottonX));
          System.out.println( rightY(rightBottonY) );
          int blockNum = map.get(oldImageView.getId());
          //if the user drop the blocks in the grid
          if (db.hasImage() && !(leftTopX == -1)   && !(leftTopY == -1)
        		  && rightX(rightBottonX) && rightY(rightBottonY) 
        		  &&canadd(blockSet.get(blockNum), X, Y, board)) { 

       	      System.out.println("drop");
       	      //add the block to the board
       	     
       	      Color[][] b = blockSet.get(blockNum);
       	      for(int i = 0; i < b.length; i++) {
       	    	  for(int j = 0; j < b[i].length; j++) {
       	    		if(b[j][i] != Color.GRAY && Y + j < 10 && X + i < 10) {
       	    			board[Y + j][X + i] = b[j][i];
       	    			score = score + 10;
       	    			labelScore.textProperty().bind(new SimpleIntegerProperty(score).asString());
       	    		}
       	    	  }
       	      }
       	      
       	      //draw the new grid
       	      graphicsContext.clearRect(0, 0, brickSize * 10, brickSize * 10);
       	      for(int i = 0; i < boardSize; i++) {
	 	       	  for(int j = 0; j < boardSize; j++) {
	 	       		  graphicsContext.setFill(board[j][i]); 
	 	       		  graphicsContext.fillRoundRect(brickSize * i , brickSize * j, 
	        				  37, 37, 10, 10);		
	 	       	  }
          }
       	   	 //get a new block 
       	   	  int random = random(13);
       	   	  oldImageView.setImage(blocks[random]);
       	   	  System.out.println("oldImageView.getId()" + oldImageView.getId());
       	   	  map.put(oldImageView.getId(), random);
       	   	  
       	   	  oldImageView.setFitHeight(blocks[random].getHeight()/3);
       	   	  oldImageView.setPreserveRatio(true);
       	   	  //pane.getChildren().add(iv);
              success = true;
          }

          /* let the source know whether the string was successfully 
           * transferred and used */
          e.setDropCompleted(success);

          e.consume();
    	  
      });
      pane.setOnDragOver(e -> {
    	 // System.out.println("onDragOver");
    	  e.acceptTransferModes(TransferMode.COPY_OR_MOVE);
      });
    
      gridPane.add(iv0, 0, 0);
      gridPane.add(iv1, 1, 0);
      gridPane.add(iv2, 2, 0);
      gridPane.relocate(100, 600);
      gridPane.setHgap(150);
      
      pane.getChildren().add(label);
      pane.getChildren().add(labelScore);
      pane.getChildren().add(canvas);
      pane.getChildren().add(gridPane);
      
      applicationStage.setTitle("Game"); // Set window's title
      applicationStage.setOnCloseRequest(e ->{
    	  AudioPlayer.player.stop(loop);
    	  
      });
      applicationStage.setScene(scene);              // Set window's scene
      applicationStage.setHeight(800);
      applicationStage.setWidth(600);
      applicationStage.show();                       // Display window
      
      return;
   }
   
		
		
/**
 * 
 * @param i
 * @return a random number in [0, 13)
 */
   public int random(int i) {
	   Random randomGenerator = new Random();
	   int randomInt = randomGenerator.nextInt(13);
	   return randomInt;
   }
   /**
    * 
    * @param block
    * @param x
    * @param y
    * @param board
    * @return  if we can add block in the board, return true,
    * 			otherwise return false
    */
   public boolean canadd(Color[][] block, int x, int y, Color[][] board) {
		int blockSize = block.length;
		int boardSize = board.length;
		
		for(int m = 0; m < blockSize; m++) {
			for(int n = 0; n < blockSize; n++) {
				if((x + m) < boardSize && (y + n) < boardSize && 
						!block[n][m].equals(Color.GRAY) && !board[y + n][x + m].equals(Color.GRAY)) {
					 System.out.print("x:" + (y + n) + " ");
					 System.out.println("y:" + (x + m));
					return false;
				}
			}
		}
			
		return true;
	}
	/**
	 * 
	 * @param  board
	 * @return a array with 21 entries
	 * This function find rows and columns that are full, and return them
	 * if(ans[0] == -1) then no row nor column is full
	 * otherwise  the ans[1] - ans[10] is 1 if row1 to row10 is full
	 * ans[11] - ans[20] is 1 if co1lumn1 to column10 is full
	 */
	public int[] disappear(Color[][] board) {
		int[] ans = new int[21];
		boolean flag = false;
		for(int i = 0; i < board.length; i++) {
			int sum = 0;
			for(int j = 0; j < board.length; j++) {
				if(board[i][j].equals(Color.GRAY))
					sum++;   
			}
			if(sum == 0) {
				ans[i + 1] = 1;
				flag = true;
			}
		}
		
		for(int i = 0; i < board.length; i++) {
			int sum = 0;
			for(int j = 0; j < board.length; j++) {
				if(board[j][i].equals(Color.GRAY))
					sum++;   
			}
			if(sum == 0) {
				ans[i + 11] = 1;
				flag = true;
			}
		}
		if(!flag) ans[0] = -1;
		else ans[0] = 1;
		return ans;
		
	}
	/**
	 * 
	 * @param b1
	 * @param b2
	 * @param b3
	 * @param board
	 * @return  true if any of b1, b2, b3 can be placed in the board
	 * 			false if all blocks can't find place to fit in the board
	 */
	public boolean keepPlay(Color[][] b1, Color[][] b2, Color[][] b3, Color[][] board) {
		System.out.println("in the keepPlay");
		boolean b1Fit = false;
		boolean b2Fit = false;
		boolean b3Fit = false;
		boolean flag = true;
		for(int i = 0; i < board.length; i++) {
			for(int j = 0; j < board[i].length; j++) {
				flag = true;
				for(int m = 0; m < b1.length; m++) {
					for(int n = 0; n < b1[m].length; n++) {
						if((i + m) >= board.length || 
								(j + n) >= board.length || 
								(b1[m][n] != Color.GRAY && board[i + m][j + n] != Color.GRAY))
							flag = false;
					}
				}
		
				if(flag)
					break;
			}
			if(flag)
				break;
		}
		if(flag) b1Fit = true;
		
		for(int i = 0; i < board.length; i++) {
			for(int j = 0; j < board[i].length; j++) {
				flag = true;
				for(int m = 0; m < b2.length; m++) {
					for(int n = 0; n < b2[m].length; n++) {
						if((i + m) >= board.length ||
								(j + n) >= board.length ||  
								(b2[m][n] != Color.GRAY && board[i + m][j + n] != Color.GRAY))
							flag = false;
					}
				}
			
				if(flag)
					break;
			}
			if(flag)
				break;
		}
		if(flag) b2Fit = true;
		
		for(int i = 0; i < board.length; i++) {
			for(int j = 0; j < board[i].length; j++) {
				flag = true;
				for(int m = 0; m < b3.length; m++) {
					for(int n = 0; n < b3[m].length; n++) {
						if((i + m) >= board.length || 
								(j + n) >= board.length ||  
								(b3[m][n] != Color.GRAY && board[i + m][j + n] != Color.GRAY))
							flag = false;
					}
				}
			
				if(flag)
					break;
			}
			if(flag)
				break;
		}
		if(flag) b3Fit = true;
		System.out.println("b1Fit : " + b1Fit);
		System.out.println("b2Fit : " + b2Fit);
		System.out.println("b3Fit : " + b3Fit);
			//System.out.println("no moves left");
		return b1Fit || b2Fit || b3Fit;
		
	}
	/**
	 * 
	 * @author jiajiachen
	 * It is called when a note is dragged
	 *
	 */
   final class Resize implements EventHandler<MouseEvent>{ 
	   public void handle(MouseEvent e) {
		   ImageView iv = (ImageView)e.getSource();
		   Dragboard db = iv.startDragAndDrop(TransferMode.MOVE);
		   ClipboardContent content = new ClipboardContent();
           content.putImage(iv.getImage());
           db.setContent(content);
           oldImageView = iv;
           iv.setVisible(false);
           drag();
           e.consume();
	   }
	  
	}
   
	/**
	 *    
	 * @param x
	 * @return x is the actual coordinate, this function returns the lined up
	 * coordinate, if x is too far from the grid, return -1;
	 * Example: x = 148, return 140.
	 *          x = 132, return 140;
	 *          x = 10;  return -1;
	 */
	  
	
	   public double locateX(double x) {
		   if(x < leftPadding - brickSize/2 || x > brickSize * boardSize + leftPadding - brickSize/2) 
			   return -1;
		   if(x >= leftPadding - brickSize/2 && x < leftPadding) {
			   return leftPadding;
		   }
		   else if((x - leftPadding)/brickSize - (int)((x - leftPadding)/brickSize) > 0.5 ) {
			   return ((int)((x - leftPadding)/brickSize) + 1) * brickSize + leftPadding;
		   }
		   else if((x - leftPadding)/brickSize - (int)((x - leftPadding)/brickSize) <= 0.5) {
			   return (int)((x - leftPadding)/brickSize) * brickSize + leftPadding;
		   }
		   return -1;
		   
	   }
	   /**
	    * 
	    * @param y
	    * @return y is the actual coordinate, this function returns the lined up
	 * coordinate, if y is too far from the grid, return -1;
	 * Example: y = 132, return 130.
	 *          y = 127, return 130;
	 *          y = 10;  return -1;
	    */
	   public double locateY(double y) {
		   if(y < topPadding - brickSize/2 || y > brickSize * boardSize + topPadding - brickSize/2) 
			   return -1;
		   if(y >= topPadding - brickSize/2 && y < topPadding) {
			   return topPadding;
		   }
		   else if((y - topPadding)/brickSize  - (int)((y - topPadding)/brickSize ) > 0.5 ) {
			   return ((int)((y - topPadding)/brickSize ) + 1) * brickSize  + topPadding;
		   }
		   else if((y - topPadding)/brickSize  - (int)((y - topPadding)/brickSize ) <= 0.5) {
			   return (int)((y - topPadding)/brickSize ) * brickSize  + topPadding;
		   }
		   return -1;
	   }
   /**
    * 
    * @param x
    * @return this function test if the right bottom corner of the block
    * is outside the grid
    */
   public boolean rightX(double x) {
	   if(x > brickSize * boardSize + leftPadding + brickSize/2)
		   return false;
	   return true;
   }
   /**
    * 
    * @param y
    * @return this function test if the right bottom corner of the block
    * is outside the grid
    */
   public boolean rightY(double y) {
	   if(y >  brickSize * boardSize + topPadding + brickSize/2)
		   return false;
	   return true;
   }
   /**
    * play the sound when a drop fails
    */
   public void dropfail() {
	   File dropf = new File("sound/dropFail.wav");
   	   AudioInputStream audioStream = null;
		try {
			audioStream = AudioSystem.getAudioInputStream(dropf);
		} catch (UnsupportedAudioFileException | IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
   	   AudioFormat format = audioStream.getFormat();  
   	   DataLine.Info info = new DataLine.Info(Clip.class, format);
   	   Clip audioClip = null;
		try {
			audioClip = (Clip) AudioSystem.getLine(info);
		} catch (LineUnavailableException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
   	   try {
			audioClip.open(audioStream);
		} catch (LineUnavailableException | IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
   	   audioClip.start();
   }
   /**
    * play a sound when a block is dragged
    */
   public void drag() {
	   File drag = new File("sound/drag.wav");
   	   AudioInputStream audioStream = null;
		try {
			audioStream = AudioSystem.getAudioInputStream(drag);
		} catch (UnsupportedAudioFileException | IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
   	   AudioFormat format = audioStream.getFormat();  
   	   DataLine.Info info = new DataLine.Info(Clip.class, format);
   	   Clip audioClip = null;
		try {
			audioClip = (Clip) AudioSystem.getLine(info);
		} catch (LineUnavailableException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
   	   try {
			audioClip.open(audioStream);
		} catch (LineUnavailableException | IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
   	   audioClip.start();
   }
   /**
    * play a sound when drop is done
    */
   public void dropdone() {
	   File bgm = new File("sound/drop.wav");
   	   AudioInputStream audioStream = null;
		try {
			audioStream = AudioSystem.getAudioInputStream(bgm);
		} catch (UnsupportedAudioFileException | IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
   	   AudioFormat format = audioStream.getFormat();  
   	   DataLine.Info info = new DataLine.Info(Clip.class, format);
   	   Clip audioClip = null;
		try {
			audioClip = (Clip) AudioSystem.getLine(info);
		} catch (LineUnavailableException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
   	   try {
			audioClip.open(audioStream);
		} catch (LineUnavailableException | IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
   	   audioClip.start();
   }
   /**
    * play a sound when one line disappears
    */
   public void disappearSound() {
	   File bgm = new File("sound/disappear.wav");
   	   AudioInputStream audioStream = null;
		try {
			audioStream = AudioSystem.getAudioInputStream(bgm);
		} catch (UnsupportedAudioFileException | IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
   	   AudioFormat format = audioStream.getFormat();  
   	   DataLine.Info info = new DataLine.Info(Clip.class, format);
   	   Clip audioClip = null;
		try {
			audioClip = (Clip) AudioSystem.getLine(info);
		} catch (LineUnavailableException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
   	   try {
			audioClip.open(audioStream);
		} catch (LineUnavailableException | IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
   	   audioClip.start();
   }
   public static void main(String [] args) {
       launch(args); // Launch application
     
       
       return;
       }
}