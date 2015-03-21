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
 * A carpet solitaire game.
 * Game state can be saved and loaded from a file.
 * Save file also holds statistical information on games played.
 * Unlimited undo functionality for moves prior to shuffling remaining cards.
 * 
 * <p>
 * <b>Caveats:</b> There is an unwanted boarder on the right and bottom sides of
 * the window. This appears to be a side effect of window.setResizable(false)
 * <p>
 * Some code adapted from Project 2 Solution by Jeffrey Van Baalen
 * 
 * @author Stephen Belden
 * @version 2015-03-21
 */
public class Main {
	/**
	 * The number of pixels that a single card .gif image takes up horizontally.
	 * Should always match the actual dimensions of the images being used.
	 */
	public static final int CARD_WIDTH = 73;
	/**
	 * The number of pixels that a single card .gif image takes up vertically.
	 * Should always match the actual dimensions of the images being used.
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
	 * Background color. (Red, Green, Blue)
	 */
	public static final Color paleGreen = new Color(100, 200, 100);
	
	// frequently used numbers
	public static final int suitsInOneDeck = 4;
	public static final int cardsInOneSuit = 13;
	
	// data fields that are accessed from more than one function
	public static final List<CardImage> Deck = new ArrayList<CardImage>();
	public static final List<CardImage> playGrid = new ArrayList<CardImage>();
	public static JPanel playArea = new JPanel();
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
			if (getCard(i).getNumber() - 1 == i % (cardsInOneSuit + 1)) {
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
		playArea.removeAll();
		for (int i = 0; i < playGrid.size(); i++) {
			playArea.add(playGrid.get(i));
		}
		playArea.revalidate();
		playArea.repaint();
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
		// for clarity, here are some values
		int suitOfGrayCard = 0;
		int valueOfGrayCard = 14;
		boolean grayCardDraggable = false;
		
		// clean up from the last game
		playGrid.clear();
		
		// initialize the playing cards in random order
		Collections.shuffle(Deck);
		for (int i = 0; i < suitsInOneDeck; i++) {
			// add a gray card at the start of each row
			playGrid.add(new CardImage("cardImages/gray.gif", suitOfGrayCard,
					valueOfGrayCard, grayCardDraggable));
			for (int j = 0; j < cardsInOneSuit; j++) {
				playGrid.add(Deck.get(j + (cardsInOneSuit * i)));
			}
		}
		
		// add all 56 cardImage objects to the playArea
		for (int i = 0; i < playGrid.size(); i++) {
			playArea.add(playGrid.get(i));
		}
	}

	/**
	 * Builds and displays the card game window.
	 * 
	 * @param args is ignored.
	 */
	public static void main(String[] args) {
		// define names used for loading
		final String[] SUITS = { "Spades", "Hearts", "Clubs",
		"Diamonds" };
		final String[] NUMBERS = { "ace", "two", "three", "four",
		"five", "six", "seven", "eight", "nine", "ten", "jack", "queen",
		"king" };

		// load images
		for (int i = 0; i < SUITS.length; i++) {
			for (int j = 0; j < NUMBERS.length; j++) {
				Deck.add(new CardImage("cardImages/" + NUMBERS[j] + SUITS[i]
						+ ".gif", i + 1, j + 1, true));
			}
		}
		
		// initialize background gray rectangles
		// setup an ArrayList of 56 gray square images
		final List<CardImage> grays = new ArrayList<CardImage>();
		for (int i = 0; i < 56; i++) {
			grays.add(new CardImage("cardImages/gray.gif", 0, 0, false));
		}
		
		// setup JPanel to hold gray cards (4 by 14 grid of cardImage objects)
		JPanel grayCards = new JPanel();
		grayCards.setLayout(new GridLayout(suitsInOneDeck, cardsInOneSuit + 1, CARD_GAP, CARD_GAP));
		grayCards.setBorder(BorderFactory.createEmptyBorder(BORDER, BORDER, BORDER, BORDER));
		grayCards.setLocation(0, 0);
		grayCards.setSize(((CARD_WIDTH + CARD_GAP) * (cardsInOneSuit + 1)) + BORDER + BORDER,
				((CARD_HEIGHT + CARD_GAP) * suitsInOneDeck) + BORDER + BORDER);
		grayCards.setOpaque(false);
		
		//add 56 gray cards to JPanel
		for (int i = 0; i < 56; i++) {
			grayCards.add(grays.get(i));
		}
		
		// initialize playing cards
		playArea.setLayout(new GridLayout(suitsInOneDeck, cardsInOneSuit, CARD_GAP, CARD_GAP));
		initCards();
		
		// ensure that cards are always visible over the gray rectangles
		JLayeredPane playLayers = new JLayeredPane();
		playLayers.add(grayCards, new Integer(0));
		playLayers.add(playArea, new Integer(1));
		playLayers.setPreferredSize(new Dimension(grayCards.getWidth(), grayCards.getHeight()));
		playLayers.setOpaque(false);
		
		// setup menubar
		JMenuBar menubar = new JMenuBar();
		
		JMenu fileMenu = new JMenu("File");
		menubar.add(fileMenu);
		
		JMenu editMenu = new JMenu("Edit");
		menubar.add(editMenu);
		
		// create and display the window
		playArea.setBorder(BorderFactory.createEmptyBorder(BORDER, BORDER, BORDER, BORDER));
		playArea.setLocation(0, 0);
		playArea.setSize(((CARD_WIDTH + CARD_GAP) * (cardsInOneSuit + 1)) + BORDER + BORDER,
				((CARD_HEIGHT + CARD_GAP) * suitsInOneDeck) + BORDER + BORDER);
		playArea.setOpaque(false);
		window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		window.setLocationByPlatform(true);
		window.getContentPane().setBackground(paleGreen);
		window.setJMenuBar(menubar);
		window.add(playLayers);
		window.pack();
		window.setVisible(true);
		window.setResizable(false);
	}
}