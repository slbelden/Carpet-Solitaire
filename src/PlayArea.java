import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JLayeredPane;

/**
 * Provides all of the storage and methods needed to manage the interactive area of the Cards window.
 * Card order is randomized, not including the left-most four gray blanks.
 * Initial layout of the cards is performed using a GridLayout,
 * with dimensions specified at object creation.
 * Dragged cards appear above all other cards, both while and after dragging.
 * 
 * @author Stephen Belden
 * @version 2015-02-13
 */
public class PlayArea extends JLayeredPane {
	/**
	 * Holds all of the necessary image files.
	 * 52 cards plus 1 blank gray square.
	 */
	private ImageIcon[] deck = new ImageIcon[53];
	/**
	 * Holds all of the CardPanel instances in use in the play area.
	 * One for each playing card, and 4 for the gray blanks on the left
	 * of the play area.
	 */
	private CardPanel[] grid = new CardPanel[56];
	
	/**
	 * Creates a playing area full of draggable cards.
	 * Cards will not be displayed if the correctly named images are not present
	 * in the /cardImages directory. No errors will be thrown if this occurs.
	 * The initial layout of the cards will be incorrect if playWidth and playHeight
	 * are not valid values.
	 * 
	 * <p>playWidth should be greater than or equal to the number of cards to be
	 * displayed horizontally multiplied by the width of one card.
	 * playHeight should be greater than or equal to the number of cards to be
	 * displayed vertically multiplied by the height of one card.
	 * 
	 * @param playWidth The int number of pixels needed for the playing area horizontally.
	 * @param playHeight The int number of pixels needed for the playing area vertically.
	 */
	public PlayArea(final int playWidth, final int playHeight) {
		this.setOpaque(false);
        this.setPreferredSize(new Dimension(playWidth, playHeight));
        this.setLayout(new GridLayout(4, 14));
        loadImages();
        initGrid();
	}

	/**
	 * Places each of the cards in the deck in a random location in the initial grid.
	 */
	private void initGrid() {
		//remove the blank card
		ImageIcon[] randomizedDeck = new ImageIcon[52];
		for(int i = 0; i < 52; i++) {
			randomizedDeck[i] = deck[i + 1];
		}
		
		//randomize the remaining 52 cards
		Random rnd = new Random();
	    for (int i = randomizedDeck.length - 1; i > 0; i--) {
	      int index = rnd.nextInt(i + 1);
	      ImageIcon temp = randomizedDeck[index];
	      randomizedDeck[index] = randomizedDeck[i];
	      randomizedDeck[i] = temp;
	    }
	    
	    //add cards to the grid
		int cardType = 0;
		for(int i = 0; i < 56; i++) {
        	if(i % 14 == 0){
        		//gray rectangles
        		grid[i] = new CardPanel(deck[0], this);
        		grid[i].setDraggable(false);
        	} else {
        		//cards
        		grid[i] = new CardPanel(randomizedDeck[cardType], this);
        		cardType++;
        	}
        }
		
		//place each CardPanel in the appropriate position
		for(int i = 0; i < 4; i++) {
			for(int j = 0; j < 13; j++) {
				this.add(grid[(i * 14) + j], new Integer(0));
			}
		}
	}

	/**
	 * Loads card image files into the deck array.
	 */
	private void loadImages() {
		//load blank gray card as first card
		deck[0] = new ImageIcon("cardImages/gray.gif");
		
		//load card images
		final String filepathStub = "cardImages/";
		final String fileExtention = ".gif";
		final String[] numbers = {"ace", "two", "three", "four", "five", "six",
					"seven", "eight", "nine", "ten", "jack", "queen", "king"};
		final String[] suits = {"Spades", "Hearts", "Diamonds", "Clubs"};
		
		for(int i = 0; i < 4; i++) {
			for(int j = 0; j < 13; j++) {
				String path = ((filepathStub.concat(numbers[j])).concat(suits[i])).concat(fileExtention);
				deck[1 + (13 * i) + j] = new ImageIcon(path);
			}
		}
	}
	
	
}
