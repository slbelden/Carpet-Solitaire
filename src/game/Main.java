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
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

/**
 * Displays a deck of draggable cards on a pale green background. Cards are
 * initially laid out in a grid of 13 by 4 cards, in random order.
 * Cards can only be dragged to blank gray spaces, and only if the card to the
 * left of the blank space is of matching suit and has a face value of one less
 * than the moved card.
 * <p>
 * <b>Caveats:</b> There is an unwanted boarder on the right and bottom sides of
 * the window. This appears to be a side effect of window.setResizable(false)
 * <p>
 * Some code adapted from Project 2 Solution by Jeffrey Van Baalen
 * 
 * @author Stephen Belden
 * @version 2015-03-13
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
	 * The desired gap (in pixels) between each card displayed in the initial
	 * grid. Should always be a nonnegative value.
	 */
	public static final int CARD_GAP = 5;
	/**
	 * The desired width of the blank boarder surrounding the initial grid of
	 * cards. Should always be a nonnegative value.
	 */
	public static final int BORDER = 5;
	/**
	 * Background color.
	 */
	public static final Color paleGreen = new Color(100, 200, 100);
	
	// non editable fields
	public static final String[] SUITS = { "Spades", "Hearts", "Clubs",
			"Diamonds" };
	public static final String[] NUMBERS = { "ace", "two", "three", "four",
			"five", "six", "seven", "eight", "nine", "ten", "jack", "queen",
			"king" };
	public static final List<CardImage> deck = new ArrayList<CardImage>();
	public static final List<CardImage> grays = new ArrayList<CardImage>();
	public static final List<CardImage> playGrid = new ArrayList<CardImage>();
	public static JPanel back = new JPanel();
	public static JPanel play = new JPanel();
	public static JLayeredPane playArea = new JLayeredPane();
	public static JFrame window = new JFrame("Cards");

	/**
	 * Returns the index of a card in playArea.
	 * 
	 * @param card a CardImage object in playArea
	 * @return the index of card in playArea
	 */
	public static int getCardIndex(CardImage card) {
		return playGrid.indexOf(card);
	}

	/**
	 * @param index of desired card
	 * @return the desired CardImage
	 */
	public static CardImage getCard(int index) {
		return playGrid.get(index);
	}

	/**
	 * @return whether or not the game has been completed
	 */
	public static void checkWin() {
		int correct = 0;
		for (int i = 0; i < playGrid.size(); i++) {
			if (getCard(i).getNumber() - 1 == i % 14) {
				correct++;
			}
		}
		if (correct == playGrid.size()) {
			JOptionPane.showMessageDialog(window,
					"You have won Carpet Solitaire!");
			initCards();
			redrawInPlace();
		}
	}

	/**
	 * Restores all cards to their indexed locations without changing card order
	 */
	public static void redrawInPlace() {
		play.removeAll();
		for (int i = 0; i < playGrid.size(); i++) {
			play.add(playGrid.get(i));
		}
		play.revalidate();
		play.repaint();
	}

	/**
	 * Swaps a card with a card at another index
	 * 
	 * @param a is the first card, a CardImage
	 * @param b the second card, referenced by its location in playArea
	 */
	public static void swapCards(CardImage a, CardImage b) {
		Collections.swap(playGrid, getCardIndex(a), getCardIndex(b));
		redrawInPlace();
	}

	/**
	 * Returns the index of the card closest to it, not including it
	 * 
	 * @param it CardImage to compare against
	 * @return the index of the card nearest to the point p
	 */
	public static CardImage getNearest(CardImage it) {
		double Nearest = 9999.9;
		// failsafe, if for some reason a nearest card isn't found, swap it with
		// itself
		int indexOfNearest = getCardIndex(it);
		
		// look at all cards, pick the one closest to the center of it
		for (int i = 0; i < playGrid.size(); i++) {
			if (getCardIndex(it) != getCardIndex(playGrid.get(i))) {
				double current = (it.getCenter().distance(playGrid.get(i)
						.getCenter()));
				if (current < Nearest) {
					Nearest = current;
					indexOfNearest = playGrid.indexOf(playGrid.get(i));
				}
			}
		}
		return playGrid.get(indexOfNearest);
	}

	/**
	 * Sets the cards up in random order, with the gray blanks on the left
	 */
	private static void initCards() {
		// clean up from the last game
		playGrid.clear();
		
		// initialize the playing cards in random order
		play.setLayout(new GridLayout(4, 13, CARD_GAP, CARD_GAP));
		Collections.shuffle(deck);
		for (int i = 0; i < SUITS.length; i++) {
			playGrid.add(new CardImage("cardImages/gray.gif", 0, 14, false));
			for (int j = 0; j < NUMBERS.length; j++) {
				playGrid.add(deck.get(j + (NUMBERS.length * i)));
			}
		}
		for (int i = 0; i < playGrid.size(); i++) {
			play.add(playGrid.get(i));
		}
	}

	/**
	 * Builds and displays the card game window.
	 * 
	 * @param args is ignored.
	 */
	public static void main(String[] args) {
		// load images
		for (int i = 0; i < SUITS.length; i++) {
			for (int j = 0; j < NUMBERS.length; j++) {
				deck.add(new CardImage("cardImages/" + NUMBERS[j] + SUITS[i]
						+ ".gif", i + 1, j + 1, true));
			}
		}
		
		// initialize background gray rectangles
		back.setLayout(new GridLayout(4, 14, CARD_GAP, CARD_GAP));
		for (int i = 0; i < 56; i++) {
			grays.add(new CardImage("cardImages/gray.gif", 0, 0, false));
		}
		for (int i = 0; i < (deck.size() + 4); i++) {
			back.add(grays.get(i));
		}
		back.setBorder(BorderFactory.createEmptyBorder(BORDER, BORDER, BORDER,
				BORDER));
		back.setLocation(0, 0);
		back.setSize((CARD_WIDTH + CARD_GAP) * 14 + BORDER + BORDER,
				(CARD_HEIGHT + CARD_GAP) * 4 + BORDER + BORDER);
		back.setOpaque(false);
		
		// initialize cards
		initCards();
		play.setBorder(BorderFactory.createEmptyBorder(BORDER, BORDER, BORDER,
				BORDER));
		play.setLocation(0, 0);
		play.setSize((CARD_WIDTH + CARD_GAP) * 14 + BORDER + BORDER,
				(CARD_HEIGHT + CARD_GAP) * 4 + BORDER + BORDER);
		play.setOpaque(false);
		
		// ensure that cards are always visible over the gray rectangles
		playArea.add(back, new Integer(0));
		playArea.add(play, new Integer(1));
		playArea.setPreferredSize(new Dimension(back.getWidth(), back
				.getHeight()));
		playArea.setOpaque(false);
		
		//setup menubar
		JMenuBar menubar = new JMenuBar();
		
		JMenu fileMenu = new JMenu("File");
		menubar.add(fileMenu);
		
		JMenu editMenu = new JMenu("Edit");
		menubar.add(editMenu);
		
		// create and display the window
		window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		window.setLocationByPlatform(true);
		window.getContentPane().setBackground(paleGreen);
		window.setJMenuBar(menubar);
		window.add(playArea);
		window.pack();
		window.setVisible(true);
		window.setResizable(false);
	}
}