package game;

import java.awt.Point;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 * Stores information about a card
 * 
 * @author Stephen Belden
 * @version 3.1.7.b
 */
public class CardImage extends JLabel {
	/**
	 * The number of pixels that a single card image takes up horizontally.
	 * Should always match the actual dimensions of the images being used.
	 * Should always match the value of the same name in Main.
	 */
	public static final int CARD_WIDTH = 73;
	/**
	 * The number of pixels that a single card .gif image takes up vertically.
	 * Should always match the actual dimensions of the images being used.
	 * Should always match the value of the same name in Main.
	 */
	public static final int CARD_HEIGHT = 97;
	
	// fields
	private boolean isDraggable;
	private int suit;
	private int number;

	/**
	 * Constructs a card with all the necessary information.
	 * 
	 * @param path is the filepath to this card's image
	 * @param suit is the number of the suit, 1-4, for Spades, Hearts, Clubs,
	 *  and Diamonds respectively. Suit of a blank card is 0.
	 * @param number is the face value of the card 1-13, for numbers 1-10,
	 * 	Jack, Queen, and King respectively. Number of a blank card is 14.
	 * @param draggable determines if the user is allowed to drag this card
	 */
	public CardImage(String path, int suit, int number, boolean draggable) {
		super(new ImageIcon(path));
		this.suit = suit;
		this.number = number;
		isDraggable = draggable;
		Drag mouseListenInst = new Drag(this);
		addMouseListener(mouseListenInst);
		addMouseMotionListener(mouseListenInst);
		// this.setSize(73, 97);
	}

	/**
	 * @return the center of this card as a Point
	 */
	public Point getCenter() {
		return new Point(this.getX() + (CARD_WIDTH / 2),
				this.getY() + (CARD_HEIGHT / 2));
	}

	/**
	 * @return a boolean value determining whether the user is allowed to drag
	 *         this card
	 */
	public boolean isDraggable() {
		return isDraggable;
	}

	/**
	 * @return the int representing the suit of this card,
	 *  1-4 for suits, 0 for blank
	 */
	public int getSuit() {
		return suit;
	}

	/**
	 * @return the int representing the number of this card,
	 *  1-13 for cards, 14 for blank
	 */
	public int getNumber() {
		return number;
	}
	
	/**
	 * @return the card closest to this card,
	 *  measured from the center of each card
	 */
	public CardImage getNearest(){
		// must be executed in Main,
		// since this function requires access to the playGrid
		return Main.getNearest(this);
	}
	
	/**
	 * @return the position of this card in the playGrid
	 */
	public int getIndex(){
		// must be executed in Main,
		// since this function requires access to the playGrid
		return Main.getCardIndex(this);
	}
	
	/**
	 * Swaps this card with another
	 * 
	 * @param b is the other card
	 */
	public void swap(CardImage b){
		// must be executed in Main,
		// since this function requires access to the playGrid
		Main.swapCards(this, b);
	}
}