package game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

/**
 * Displays a deck of draggable cards on a pale green background.
 * Uses a standard 52 card deck.
 * Cards are initially laid out in a grid of 13 by 4 cards, in random order.
 * 
 * Cards can only be dragged to blank gray spaces, and only if the card to the
 * left of the blank space is of matching suit and has a face value of one
 * less than the moved card.
 * 
 * <p><b>Caveats:</b> There is an unwanted boarder on the right and bottom sides
 * of the window. This appears to be a side effect of window.setResizable(false)
 * 
 * Some code adapted from Project 2 Solution by Jeffrey Van Baalen
 * 
 * @author Stephen Belden
 * @version 2015-02-27
 */
public class Main {
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
	/** 
     * The desired gap (in pixels) between each card displayed in the initial grid.
     * Should always be a nonnegative value.
     */
	public static final int CARD_GAP = 5;
	/** 
	 * The desired width of the blank boarder surrounding the initial grid of cards.
	 * Should always be a nonnegative value.
	 */
	public static final int BORDER = 5;
	/**
	 * Background color.
	 */
	public static final Color paleGreen = new Color(100,200,100);
	
	//non editable fields
	public static final String[] SUITS = {"Spades", "Hearts", "Clubs", "Diamonds"};
	public static final String[] NUMBERS = {"ace", "two", "three", "four", "five",
				"six", "seven", "eight", "nine", "ten", "jack", "queen", "king"};
	public static final List<CardImage> deck = new ArrayList<CardImage>();
	public static final List<CardImage> grays = new ArrayList<CardImage>();
	
	/**
	 * Builds and displays the card game window.
	 * 
	 * @param args is ignored.
	 */
	public static void main(String[] args) {
		//Load images
		for(int i = 0; i < SUITS.length; i++) {                                            
            for(int j = 0; j < NUMBERS.length; j++) {
                deck.add(new CardImage("cardImages/" + NUMBERS[j] + SUITS[i] + ".gif", i+1, j+1, true));
            }
        }
		
		//initialize background gray rectangles
		JPanel back = new JPanel(new GridLayout(4, 14, CARD_GAP, CARD_GAP));
		for(int i = 0; i < 56; i++){ grays.add(new CardImage("cardImages/gray.gif", 0, 0, false)); }
		for(int i = 0; i < (deck.size() + 4); i++) { back.add(grays.get(i)); }
		back.setBorder(BorderFactory.createEmptyBorder(BORDER, BORDER, BORDER, BORDER));
		back.setLocation(0, 0);
		back.setSize((CARD_WIDTH + CARD_GAP) * 14 + BORDER + BORDER, (CARD_HEIGHT + CARD_GAP) * 4 + BORDER + BORDER);
		back.setOpaque(false);
		
		//initialize the playing cards in random order
		JPanel play = new JPanel(new GridLayout(4, 13, CARD_GAP, CARD_GAP));
		Collections.shuffle(deck);
		for(int i = 0; i < deck.size(); i++) { play.add(deck.get(i)); }
		play.setBorder(BorderFactory.createEmptyBorder(BORDER, BORDER + CARD_WIDTH + CARD_GAP, BORDER, BORDER));
		play.setLocation(0, 0);
		play.setSize((CARD_WIDTH + CARD_GAP) * 14 + BORDER + BORDER, (CARD_HEIGHT + CARD_GAP) * 4 + BORDER + BORDER);
		play.setOpaque(false);
		
		//ensure that cards are always visible over the gray rectangles
		JLayeredPane playArea = new JLayeredPane();
		playArea.add(back, new Integer(0));
		playArea.add(play, new Integer(1));
		playArea.setPreferredSize(new Dimension(back.getWidth(), back.getHeight()));
		playArea.setOpaque(false);
		
		//create and display the window
		JFrame window = new JFrame("Cards");
		window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		window.setLocationByPlatform(true);
		window.getContentPane().setBackground(paleGreen);
		window.add(playArea);
		window.pack();
		window.setVisible(true);
		window.setResizable(false);
	}
}