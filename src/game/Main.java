package game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.PrintWriter;

/**
 * A carpet solitaire game.
 * Game state can be saved and loaded from a file.
 * Unlimited undo functionality, redo is allowed until a new move is made
 * 
 * http://git.io/hy6V
 * 
 * @author Stephen Belden
 * @version 4.0.0
 */
public class Main {
	/**
	 * Set to true to print debug values to the console.
	 */
	public static final boolean debug = true;
	/**
	 * The number of pixels a single card .gif image takes up horizontally.
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
	
	// data fields that are accessed from more than one function
	public static final String[] SUITS = { "Spades", "Hearts", "Clubs",
	"Diamonds" };
	public static final String[] NUMBERS = { "ace", "two", "three", "four",
	"five", "six", "seven", "eight", "nine", "ten", "jack", "queen",
	"king" };
	public static List<CardImage> Deck = new ArrayList<CardImage>();
	public static List<CardImage> playGrid = new ArrayList<CardImage>();
	public static JPanel playArea = new JPanel();
	public static JFrame window = new JFrame("Cards");
	public static File filepath = new File("");
	
	// these menu items need to be enabled and disabled from a variety of
	// places, so they are declared here
	public static JMenuItem undoItem = new JMenuItem("Undo");
	public static JMenuItem redoItem = new JMenuItem("Redo");
		
	// frequently used numbers
	public static final int suitsInOneDeck = 4;
	public static final int cardsInOneSuit = 13;
	
	// fields needed for keeping track of the game state
	public static short shufflesRemaining;
	public static Stack<List<CardImage>> gameStates =
			new Stack<List<CardImage>>();
	public static int currentState;
	public static int gamesPlayed;
	public static int gamesWon;
	
	//=========================================================================
	// Functions
	//=========================================================================
	
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
	 * @return the number of cards that are in the correct positions,
	 * 		56 for a win.
	 */
	public static int checkWin() {
		int correct = 0;
		
		// look at every card in the grid, test if they are ordered.
		// tests involving the suit are unnecessary, because the only way for
		// all four rows of cards to be correctly ordered via legal moves is
		// for every card in a row to be of the same suit.
		for (int i = 0; i < playGrid.size(); i++) {
			if (getCard(i).getNumber() - 1 == i % (cardsInOneSuit + 1)) {
				correct++;
			}
		}
		
		// display a win message, update statistics, and begin a new game
		if (correct == playGrid.size()) {
			JOptionPane.showMessageDialog(window,
					"You have won Carpet Solitaire!");
			gamesPlayed++;
			gamesWon++;
			initCards();
			redrawInPlace();
		}
		
		// return
		if(debug) {
			System.out.println("In checkWin, correct = " + correct);
			System.out.println();
			}
		return correct;
	}
	
	/**
	 * Checks to see if an undo/redo operation is possible, and disables the
	 * undo/redo menu item if it is not.
	 */
	public static void checkUndo(){
		if(currentState > 0){
			undoItem.setEnabled(true);
		} else {
			undoItem.setEnabled(false);
		}
		
		if(gameStates.size() > currentState + 1){
			redoItem.setEnabled(true);
		} else {
			redoItem.setEnabled(false);
		}
	}

	/**
	 * Restores all cards to their indexed locations without changing order
	 */
	public static void redrawInPlace() {
		// this is a brute-force solution: clear every object from the grid,
		// then add them all back again in the desired order and repaint
		playArea.removeAll();
		for (int i = 0; i < playGrid.size(); i++) {
			playArea.add(playGrid.get(i));
		}
		playArea.revalidate();
		playArea.repaint();
		checkUndo();
	}

	/**
	 * Swaps the location of two cards and makes that change visible
	 * 
	 * @param a is the first card, a CardImage
	 * @param b the second card, referenced by its location in playArea
	 */
	public static void swapCards(CardImage a, CardImage b) {
		recordMove();
		
		// swap
		Collections.swap(playGrid, getCardIndex(a), getCardIndex(b));
		
		if(debug){
			System.out.println("In swapCards, after the swap:");
			System.out.print("gameStates.size() = " + gameStates.size());
			System.out.println(", currentState = " + currentState);
			System.out.println();
		}

		// make changes visible
		redrawInPlace();
	}

	/**
	 * Adds the current game state to the undo/redo system, making sure that
	 * there are no stored game states after this new move
	 */
	public static void recordMove() {
		// only if one or more undos have happened, clear all moves after the
		// current state, because you should not be able to redo after
		// making a new move
		while(gameStates.size() > currentState){
			gameStates.pop();
		}
		
		if(debug){
			System.out.println("In recordMove():");
			System.out.print("gameStates.size() = " + gameStates.size());
			System.out.println(", currentState = " + currentState);
			System.out.println();
		}

		// record state for undo/redo system
		// state must be recorded prior to the swap
		// the undo/redo functions handle states after swaps if they are called
		gameStates.add(new ArrayList<CardImage>(playGrid));
		currentState++;
	}

	/**
	 * Returns the index of the card closest to it, not including it
	 * 
	 * @param it CardImage to compare against
	 * @return the index of the card nearest to the point p
	 */
	public static CardImage getNearest(CardImage it) {
		double Nearest = 9999.9;
		// failsafe, if for some reason a nearest card isn't found,
		// swap it with itself
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
		gameStates.clear();
		currentState = 0;
		shufflesRemaining = 2;
		
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
		
		if(debug){
			System.out.println("At the end of initCards():");
			System.out.print("gameStates.size() = " + gameStates.size());
			System.out.println(", currentState = " + currentState);
			System.out.println();
		}
	}
	
	/**
	 * Saves the current state of the game at the given filepath
	 * 
	 * @param filepath is the desired save location and filename.
	 * @return true if the save file was created successfully; false otherwise
	 */
	public static boolean save(File filepath){
		DocumentBuilderFactory xmlFactory =
				DocumentBuilderFactory.newInstance();
        DocumentBuilder xmlBuilder;
        try{
        	// setup the document
        	xmlBuilder = xmlFactory.newDocumentBuilder();
            Document saveDoc = xmlBuilder.newDocument();
            
            // setup the initial node
            Element game = saveDoc.createElement("Game");
            saveDoc.appendChild(game);
            
            // append card information
			for (int i = 0; i < 56; i++) {
				Element card = saveDoc.createElement("Card");
				game.appendChild(card);
				card.setAttribute("id", new Integer(i).toString());

				Element suit = saveDoc.createElement("Suit");
				card.appendChild(suit);
				Node suitData =
						saveDoc.createTextNode(new Integer(
								getCard(i).getSuit()).toString());
				suit.appendChild(suitData);

				Element value = saveDoc.createElement("Value");
				card.appendChild(value);
				Node valueData =
						saveDoc.createTextNode(new Integer(
								getCard(i).getNumber()).toString());
				value.appendChild(valueData);
			}
            
            // try to save the document
            TransformerFactory transformerFactory =
            		TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(saveDoc);
            StreamResult streamResult =  new StreamResult(filepath);
            transformer.transform(source, streamResult);
            
            // if we get here before throwing an exception, everything worked
        	return true;
        } catch (Exception e){
        	if(debug) e.printStackTrace();
        	return false;
        }
	}
	
	/**
	 * Loads the current state of the game as the data from filepath
	 * 
	 * @param filepath is the desired save file location and filename.
	 * @return true if the save file was loaded successfully; false otherwise
	 */
	public static boolean load(File filepath){
		try {
			// create a temporary playing grid for operations
			List<CardImage> loadGrid = new ArrayList<CardImage>();

			// open xml file
			DocumentBuilderFactory dbFactory =
					DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document savedGame = dBuilder.parse(filepath);

			// read xml file
			NodeList savedCards = savedGame.getElementsByTagName("Card");
			if(savedCards.getLength() != 56){
				throw new Exception("Invalid number of cards in xml file.");
			}
			for (int i = 0; i < savedCards.getLength(); i++) {
				Node currentCardData = savedCards.item(i);
				Element currentCard = (Element)currentCardData;
				if(i != new Integer(currentCard.getAttribute("id"))){
					throw new Exception("Invalid xml game file.");
				}

				// create new cards, and add them to the loadGrid
				int thisSuit = new Integer(currentCard.getElementsByTagName(
						"Suit").item(0).getTextContent());
				int thisValue = new Integer(currentCard.getElementsByTagName(
						"Value").item(0).getTextContent());
				if(thisSuit == 0){
					loadGrid.add(new CardImage(
							"cardImages/gray.gif", 0, 14, false));
				} else {
					loadGrid.add(new CardImage("cardImages/"
							+ NUMBERS[thisValue - 1] + SUITS[thisSuit - 1] + ".gif",
							thisSuit, thisValue, true));
				}
				if(loadGrid.size() == 56){
					// write changes to the real playGrid only if loading 
					// happened correctly
					playGrid = loadGrid;
				}
			}

			// make changes visible and reset the game state
			redrawInPlace();
			gameStates.clear();
			currentState = 0;

			// if we get here before throwing an exception, everything worked
			return true;
		} catch(Exception e) {
			if(debug) e.printStackTrace();
			return false;
		}
	}
		
	//=========================================================================
	// Main
	//=========================================================================
	
	/**
	 * Builds and displays the card game window.
	 * 
	 * @param args is ignored.
	 */
	public static void main(String[] args) {
		
		//=====================================================================
		// Card Setup
		//=====================================================================

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
			grays.add(new CardImage("cardImages/gray.gif", 0, 14, false));
		}
		
		// setup JPanel to hold gray cards (4 by 14 grid of cardImage objects)
		JPanel grayCards = new JPanel();
		grayCards.setLayout(new GridLayout(suitsInOneDeck, cardsInOneSuit + 1,
				CARD_GAP, CARD_GAP));
		grayCards.setBorder(BorderFactory.createEmptyBorder(BORDER, BORDER,
				BORDER, BORDER));
		grayCards.setLocation(0, 0);
		grayCards.setSize(((CARD_WIDTH + CARD_GAP) * (cardsInOneSuit + 1))
				+ BORDER + BORDER, ((CARD_HEIGHT + CARD_GAP) * suitsInOneDeck)
				+ BORDER + BORDER);
		grayCards.setOpaque(false);
		
		//add 56 gray cards to JPanel
		for (int i = 0; i < 56; i++) {
			grayCards.add(grays.get(i));
		}
		
		// initialize playing cards
		playArea.setLayout(new GridLayout(suitsInOneDeck, cardsInOneSuit,
											CARD_GAP, CARD_GAP));
		initCards();
		
		// ensure that cards are always visible over the gray rectangles
		JLayeredPane playLayers = new JLayeredPane();
		playLayers.add(grayCards, new Integer(0));
		playLayers.add(playArea, new Integer(1));
		playLayers.setPreferredSize(
				new Dimension(grayCards.getWidth(), grayCards.getHeight()));
		playLayers.setOpaque(false);
		
		//initialize statistics
		gamesPlayed = 1;
		gamesWon = 0;
		
		//=====================================================================
		// Menubar Setup
		//=====================================================================
		
		JMenuBar menubar = new JMenuBar();
		
		// setup menubar menus
		JMenu fileMenu = new JMenu("File");
		menubar.add(fileMenu);
		JMenu editMenu = new JMenu("Edit");
		menubar.add(editMenu);
		JMenu helpMenu = new JMenu("Help");
		menubar.add(helpMenu);
		
		// setup menu actions		
		// file menu
		/**
		 * Begins a new game with a newly shuffled deck of cards.
		 */
		final ActionListener newGame = new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				// create new shuffled game
				initCards();
				redrawInPlace();
				gamesPlayed++;
				if(debug){
					System.out.println("After newGame():");
					System.out.print("gameStates.size() = " +
							gameStates.size());
					System.out.println(", currentState = " + currentState);
					System.out.println();
				}
				
				// to prevent saving over existing games when a new game starts
				filepath = new File("");
				
				redrawInPlace();
			}
		};
		/**
		 * Restarts the current game, keeping the initial card order.
		 */
		final ActionListener replay = new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				// if the game state hasn't changed yet, don't change anything
				if(gameStates.size() > 0){
					playGrid = gameStates.get(0);
					gameStates.clear();
					currentState = 0;
					redrawInPlace();
					if(debug){
						System.out.println("After replay()");
						System.out.print("gameStates.size() = " +
								gameStates.size());
						System.out.println(", currentState = " +
								currentState);
						System.out.println();
					}
				}
				redrawInPlace();
			}
		};
		/**
		 * Shuffles only the cards that are not already in winning positions.
		 */
		final ActionListener shuffle = new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				//if shuffles are allowed, shuffle
				if(shufflesRemaining > 0){
					// for undo/redo system
					recordMove();
					
					// create an array of 56 booleans, representing playGrid
					// (entries are true if a card is in a winning position)
					final boolean[] winCards = new boolean[56];
					for(int i = 0; i < winCards.length; i++){
						if (getCard(i).getNumber() - 1 ==
								i % (cardsInOneSuit + 1)) {
							winCards[i] = true;
							if(debug) {
								System.out.println("Shuffle: Card " + i +
										" is correct");
							}
						} else {
							winCards[i] = false;
							if(debug) {
								System.out.println("Shuffle: Card " + i +
										" is NOT CORRECT");
							}
						}
					}
					
					// create a collection of non-winning cards
					// shuffle this collection
					Stack<CardImage> loserCards = new Stack<CardImage>();
					for(int i = 0; i < winCards.length; i++){
						if(!winCards[i]){
							loserCards.add(playGrid.get(i));
						}
					}
					Collections.shuffle(loserCards);
					if(debug) {
						System.out.println("There are " + loserCards.size() +
								" loserCards");
					}
					
					// extract all of the blank cards from loserCards
					final Stack<CardImage> blankCards = new Stack<CardImage>();
					int j = 0;
					while(j < loserCards.size()){
						if(loserCards.get(j).getSuit() == 0){
							blankCards.add(loserCards.get(j));
							loserCards.remove(j);
						} else {
							// j must only be incremented if a blank card is
							// NOT found! Otherwise some blank cards may be
							// skipped.
							j++;
						}
					}
					
					// create a new temporary playing grid
					// For each spot in the tempGrid, if it's corresponding
					// winCards entry is true, use the card from the playGrid.
					// If it's if it's corresponding winCards entry is
					// false, pull a card from loserCards.
					// If the spot is the end of a row and it is non-winning,
					// pull a card from blankCards
					final List<CardImage> tempGrid = new ArrayList<CardImage>();
					for(int i = 0; i < 56; i++) {
						// card already in winning position
						if(winCards[i]){
							tempGrid.add(playGrid.get(i));
							if(debug) {
								System.out.println("Card " + i +
										" is pulled from winning cards.");
							}
						} else {
							// blank card
							if((i + 1) % 14 == 0) {
								tempGrid.add(new CardImage(
										"cardImages/gray.gif", 0, 14, false));
								if(debug) {
									System.out.println("Card " + i +
											" is a blank card.");
								}
							} else {
								// shuffled loser cards
								tempGrid.add(loserCards.pop());
								if(debug) {
									System.out.println("Card " + i +
											" is pulled from loser cards.");
								}
							}
						}
						if(debug && (i + 1) != tempGrid.size()) {
							System.out.println("ERROR: tempGrid is missing at "
									+ "least one card!");
						}
					}
					
					// restore the main playGrid if nothing bad happened
					if(tempGrid.size() == 56) {
						playGrid = tempGrid;
					} else {
						System.out.println("ERROR: Only " + tempGrid.size() +
								" cards after shuffling.");
					}
					redrawInPlace();
					
					shufflesRemaining--;
				} else {
					// inform the user if they have run out of shuffles
					final String[] options =
						{"New Game", "Restart This Game", "Close"};
					int response = JOptionPane.showOptionDialog(
							window, // root pane 
							"Sorry, you have no shuffles remaining." // text
							+ "\nOnly two shuffles are allowed per game."
							+ "\nWould you like to try this game again?",
							"Can't Shuffle", // window title
							JOptionPane.YES_NO_CANCEL_OPTION, // dialog type
							JOptionPane.INFORMATION_MESSAGE, // icon type
							null, // no custom icon
							options, // button text
							options[1]); // default option
					if(response == 0){ // check for "New Game" button
						newGame.actionPerformed(arg0);
					} else if(response == 1){ // check for "Restart"
						replay.actionPerformed(arg0);
					}
				}
			}
		};
		/**
		 * Asks for the location of a game state file
		 */
		final ActionListener open = new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				// pick a file
				final FileNameExtensionFilter xml =
						new FileNameExtensionFilter("xml files", "xml");
				final JFileChooser fc = new JFileChooser();
				fc.setFileFilter(xml);
				int valid = fc.showOpenDialog(window);
				if(valid == JFileChooser.APPROVE_OPTION) {
					filepath = fc.getSelectedFile();
				}
				
				// if load returns false, something bad happened
				if(!load(filepath)){
					JOptionPane.showMessageDialog(window,
							"There was a problem loading the game."
									+ " Please select a different file"
									+ " and try again.",
									"Game not loaded.",
									JOptionPane.ERROR_MESSAGE);
					// if loading fails, reset the filepath to nothing
					filepath = new File("");
				}
			}
		};
		// order of actions here is not the same as in the menu, but saveAs
		// must be defined before save, since it is used in save
		/**
		 * Asks for a location and filename where a game state file will be
		 * saved.
		 */
		final ActionListener saveAs = new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				// pick a file
				final FileNameExtensionFilter xml =
						new FileNameExtensionFilter("xml files", "xml");
				final JFileChooser fc = new JFileChooser();
				fc.setFileFilter(xml);
				fc.setApproveButtonText("Save");
				fc.setDialogTitle("Save As...");
				int valid = fc.showOpenDialog(window);
				if(valid == JFileChooser.APPROVE_OPTION) {
					filepath = fc.getSelectedFile();
					
					// make sure the filename ends in .xml
					if(!fc.getSelectedFile().getAbsolutePath().endsWith(".xml")){
					    filepath = new File(fc.getSelectedFile() + ".xml");
					}
					
					// if save returns false, something bad happened
					if(!save(filepath)){
						JOptionPane.showMessageDialog(
										window,
										"There was a problem saving the game."
										+ " Please select a different save"
										+ " location and try again.",
										"Game not saved.",
										JOptionPane.ERROR_MESSAGE);
						// if loading saveing, reset the filepath to nothing
						filepath = new File("");
					}
				}
				// if the cancel button was pressed, do nothing
			}
		};
		/**
		 * Saves the game silently if the filepath is already set, otherwise
		 * saveAs is called.
		 */
		final ActionListener save = new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				if(!filepath.exists()){
					saveAs.actionPerformed(arg0);
				// if save returns false, something bad happened
				} else if(!save(filepath)){
					JOptionPane.showMessageDialog(
							window,
							"There was a problem saving the game."
									+ " Please select a different save"
									+ " location and try again.",
									"Game not saved.",
									JOptionPane.ERROR_MESSAGE);
					// if loading fails, reset the filepath to nothing
					// and prompt to save to a new location
					filepath = new File("");
					saveAs.actionPerformed(arg0);
				}
			}
		};
		/**
		 * Asks the user if they want to save the current game, then terminates
		 * the application.
		 */
		final ActionListener quit = new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				final String[] options =
					{"Save", "Close Without Saving", "Cancel"};
				int response = JOptionPane.showOptionDialog(
						window, // root pane 
						"Do you want to save the current game" // text
						+ "before quitting?",
						"Quit", // window title
						JOptionPane.YES_NO_CANCEL_OPTION, // option dialog type
						JOptionPane.QUESTION_MESSAGE, // icon type
						null, // no custom icon
						options, // button text
						options[0]); // default option
				if(response == 0){ // check for "Save" button
					save.actionPerformed(arg0);
					System.exit(0);
				} else if(response == 1){ // check for "Close Without Saving"
					System.exit(0);
				}
			}
		};
		
		// edit menu
		/**
		 * Sets the game state back one move, if a previous state exists.
		 */
		final ActionListener undo = new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				if(debug){
					System.out.println("At the start of undo():");
					System.out.print("gameStates.size() = " + gameStates.size());
					System.out.println(", currentState = " + currentState);
					System.out.println();
				}
				// if this is the first undo, and if at least one move has been
				// made, record the state in case we need to redo later
				if(gameStates.size() == currentState && currentState > 0){
					gameStates.add(new ArrayList<CardImage>(playGrid));
					if(debug){
						System.out.println("After undo state creation:");
						System.out.print("gameStates.size() = " + gameStates.size());
						System.out.println(", currentState = " + currentState);
						System.out.println();
					}
				}
				if(currentState > 0){
					currentState--;
					playGrid = gameStates.get(currentState);
					if(debug){
						System.out.println("After successfull undo:");
						System.out.print("gameStates.size() = " + gameStates.size());
						System.out.println(", currentState = " + currentState);
						System.out.println();
					}
				}
				redrawInPlace();
			}
		};
		/**
		 * Sets the game state forward one move, if a newer state exists.
		 */
		final ActionListener redo = new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				if(debug){
					System.out.println("At the start of redo():");
					System.out.print("gameStates.size() = " + gameStates.size());
					System.out.println(", currentState = " + currentState);
					System.out.println();
				}
				if(gameStates.size() > currentState + 1){
					currentState++;
					playGrid = gameStates.get(currentState);
					if(debug){
						System.out.println("After successfull redo:");
						System.out.print("gameStates.size() = " + gameStates.size());
						System.out.println(", currentState = " + currentState);
						System.out.println();
					}
				}
				redrawInPlace();
			}
		};
		/**
		 * Displays statistics for the current session and allows those
		 * statistics to be reset.
		 */
		final ActionListener stats = new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				// keep displaying this dialog until "Close" is pressed
				final String[] buttonOptions = {"Close",
						"Reset Statistics Now"};
				while(JOptionPane.showOptionDialog(window, // root pane
						"Statistics for this session:\n\n" // text
						+ "Games Played: " + gamesPlayed + "\n"
						+ "Games Won: " + gamesWon + "\n"
						+ "Percent Won: "
						+ (((float)gamesWon) / (gamesPlayed) * 100) + "%\n\n"
						+ "Statistics will be reset when you quit the game.",
						"Statistics", // window title
						JOptionPane.DEFAULT_OPTION, // option dialog type
						JOptionPane.PLAIN_MESSAGE, // icon type
						null, // no custom icon
						buttonOptions, // button text
						buttonOptions[0]) // default option
						== 1){ // test for second button
					gamesPlayed = 1;
					gamesWon = 0;
				}
			}
		};
		
		//help menu
		/**
		 * Displays the rules of Carpet Solitaire.
		 */
		final ActionListener rules = new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				JOptionPane.showMessageDialog(window,
			"The game is won when all 13 cards of each suit are in order"
			+ "\nfrom left to right, with the blank space on the far right."
			+ "\nThe vertical order of the suits does not matter."
			+ "\nCards can only be moved onto blank spaces,"
			+ "\nand only if the move is legal."
			+ "\n\nA legal move consists of one of the following:"
			+ "\nMoving an Ace to the farthest left spot to begin a row."
			+ "\nMoving a card to the blank space directly to the right of"
			+ "\nthe card that has the same suit and a face value one less"
			+ "\nthan the card being moved.\n"
			+ "\nNo card can be moved to the right of a King or a blank space."
			+ "\n\nIf no more legal moves are possible, the cards that are not"
			+ "\nyet in their correct positions can be shuffled by selecting"
			+ "\nShuffle from the File menu."
			+ "\nOnly two shuffles are allowed per game.",
			"Rules", JOptionPane.PLAIN_MESSAGE);
			}
		};
		/**
		 * Displays info about this program.
		 */
		final ActionListener about = new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				JOptionPane.showMessageDialog(window, "Carpet Solitaire"
						+ "\nv4.0.0"
						+ "\n\nStephen Belden"
						+ "\nsbelden@uwyo.edu"
						+ "\nhttp://git.io/hy6V",
						"About", JOptionPane.INFORMATION_MESSAGE);
			}
		};
		
		// setup menu items and keyboard shortcuts
		// file menu items
		JMenuItem newGameItem = new JMenuItem("New Game");
		newGameItem.addActionListener(newGame);
		newGameItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
				ActionEvent.CTRL_MASK));
		JMenuItem replayItem = new JMenuItem("Replay");
		replayItem.addActionListener(replay);
		replayItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R,
				ActionEvent.CTRL_MASK));
		JMenuItem shuffleItem = new JMenuItem("Shuffle");
		shuffleItem.addActionListener(shuffle);
		shuffleItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E,
				ActionEvent.CTRL_MASK));
		JMenuItem openItem = new JMenuItem("Open...");
		openItem.addActionListener(open);
		openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
				ActionEvent.CTRL_MASK));
		JMenuItem saveItem = new JMenuItem("Save");
		saveItem.addActionListener(save);
		saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
				ActionEvent.CTRL_MASK));
		JMenuItem saveAsItem = new JMenuItem("Save As...");
		saveAsItem.addActionListener(saveAs);
		saveAsItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
				ActionEvent.CTRL_MASK + ActionEvent.SHIFT_MASK));
		JMenuItem quitItem = new JMenuItem("Quit");
		quitItem.addActionListener(quit);
		quitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,
				ActionEvent.CTRL_MASK));
		
		// edit menu items
		// undoItem is already declared
		undoItem.addActionListener(undo);
		undoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z,
				ActionEvent.CTRL_MASK));
		// redoItem is already declared
		redoItem.addActionListener(redo);
		redoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y,
				ActionEvent.CTRL_MASK));
		JMenuItem statsItem = new JMenuItem("Statistics...");
		statsItem.addActionListener(stats);
		statsItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T,
				ActionEvent.CTRL_MASK));

		// help menu items
		JMenuItem rulesItem = new JMenuItem("Rules...");
		rulesItem.addActionListener(rules);
		rulesItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H,
				ActionEvent.CTRL_MASK));
		JMenuItem aboutItem = new JMenuItem("About...");
		aboutItem.addActionListener(about);
		aboutItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I,
				ActionEvent.CTRL_MASK));
		
		// add menu items to menus
		fileMenu.add(newGameItem);
		fileMenu.add(replayItem);
		fileMenu.add(shuffleItem);
		fileMenu.add(openItem);
		fileMenu.add(saveItem);
		fileMenu.add(saveAsItem);
		fileMenu.add(quitItem);
		
		editMenu.add(undoItem);
		editMenu.add(redoItem);
		editMenu.add(statsItem);
		
		helpMenu.add(rulesItem);
		helpMenu.add(aboutItem);
		
		//=====================================================================
		// Window Setup
		//=====================================================================
		
		redrawInPlace();
		
		playArea.setBorder(BorderFactory.createEmptyBorder(BORDER, BORDER,
				BORDER, BORDER));
		playArea.setLocation(0, 0);
		playArea.setSize(((CARD_WIDTH + CARD_GAP) * (cardsInOneSuit + 1))
				+ BORDER + BORDER, ((CARD_HEIGHT + CARD_GAP) * suitsInOneDeck)
				+ BORDER + BORDER);
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