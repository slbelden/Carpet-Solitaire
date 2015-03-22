package game;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

/**
 * Listens for mouse presses and movements, and performs repositioning on
 * the object to which it is attached.
 * 
 * @author Stephen
 * @version 3.1.7b
 */
public class Drag implements MouseListener, MouseMotionListener {
	/**
	 * Stores a reference to the object to which this object is attached.
	 */
	CardImage it;
	/**
	 * Stores the X and Y coordinates of mouse when first pressed.
	 */
	Point difference;

	/**
	 * @param it should match the object to which this listener is attached.
	 */
	public Drag(CardImage it) {
		this.it = it;
	}

	/**
	 * Activates when the mouse is first pressed down.
	 * If the card is draggable, it is moved to the top layer,
	 * and the position of the mouse within the card is recorded.
	 */
	public void mousePressed(MouseEvent e) {
		if (it.isDraggable()) {
			e.getComponent().getParent()
					.setComponentZOrder(e.getComponent(), 0);
			difference = e.getPoint();
		}
	}

	/**
	 * Continually active as long as the mouse is still moving.
	 * Sets the position of the dragged card to a new location that matches the
	 * mouse drag motion.
	 */
	public void mouseDragged(MouseEvent e) {
		if (it.isDraggable()) {
			// store relevant information
			Point mouse = e.getPoint();
			Point origin = it.getLocation();

			// cast Points as ints, and move the card
			int newX = (int)(origin.getX() + mouse.getX() - difference.getX());
			int newY = (int)(origin.getY() + mouse.getY() - difference.getY());
			it.setLocation(newX, newY);
		}
	}

	/**
	 * All game logic takes place here, after the card is dropped.
	 */
	public void mouseReleased(MouseEvent e) {
		CardImage nearestCard = it.getNearest();
		
		// options for a single move, initialized to false
		boolean grayCardAtStart = false;
		boolean thisIsAnAce = false;
		boolean legalMove = false;
		
		// test if this card has been dropped on one of the opening gray spaces
		if ((nearestCard.getNumber() == 14)
				&& (nearestCard.getIndex() % 14 == 0)) {
			grayCardAtStart = true;
		}
		
		// test if this is an ace
		if (it.getNumber() == 1) {
			thisIsAnAce = true;
		}
		
		// test if this was a standard legal move
		// these two tests must happen first to prevent exceptions
		if (!grayCardAtStart && !thisIsAnAce
				&& Main.getCard(nearestCard.getIndex() - 1).getNumber()
				== it.getNumber() - 1
				&& Main.getCard(nearestCard.getIndex() - 1).getSuit()
				== it.getSuit()) {
			legalMove = true;
		}
		
		// swap if this move is placing an ace at the start of a row,
		// or is a standard legal move otherwise,
		// beep and put the card back where it was
		if ((grayCardAtStart && thisIsAnAce) || legalMove) {
			it.swap(it.getNearest());
		} else {
			java.awt.Toolkit.getDefaultToolkit().beep();
			Main.redrawInPlace();
		}

		Main.checkWin();
	}

	// these methods are not used, but must be defined from the superclass.
	public void mouseMoved(MouseEvent arg0) {}
	public void mouseClicked(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
}
