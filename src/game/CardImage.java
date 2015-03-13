package game;

import java.awt.Point;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 * Stores information about the cards
 * 
 * @author Stephen Belden
 * @version 201115-03-13
 */
public class CardImage extends JLabel {
	/**
	 * The number of pixels that a single card .gif image takes up horizontally.
	 * Should always match the actual dimensions of the images being used.
	 * Should always be a nonnegative value.
	 */
	public static final int CARD_WIDTH = 73;
	/**
	 * The number of pixels that a single card .gif image takes up vertically.
	 * Should always match the actual dimensions of the images being used.
	 * Should always be a nonnegative value.
	 */
	public static final int CARD_HEIGHT = 97;

	private boolean isDraggable;
	private int suit;
	private int number;

	/**
	 * Constructs a card with all the necessary information.
	 * 
	 * @param path is the filepath to this card's image
	 * @param suit is the number of the suit, 1-4, for Spades, Hearts, Clubs,
	 *            and Diamonds respectively
	 * @param number is the face value of the card 1-13, for numbers 1-10, Jack,
	 *            Queen, and Kind respectively
	 * @param draggable determines whether the user is allowed to drag this card
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
		return new Point(this.getX() + (CARD_WIDTH / 2), this.getY()
				+ (CARD_HEIGHT / 2));
	}

	/**
	 * @return a boolean value determining whether the user is allowed to drag
	 *         this card
	 */
	public boolean isDraggable() {
		return isDraggable;
	}

	/**
	 * @param isDraggable sets whether the user is allowed to drag this card
	 */
	public void setDraggable(boolean isDraggable) {
		this.isDraggable = isDraggable;
	}

	/**
	 * @return the int representing the suit of this card, 1-4
	 */
	public int getSuit() {
		return suit;
	}

	/**
	 * @return the int representing the number of this card, 1-13
	 */
	public int getNumber() {
		return number;
	}
}