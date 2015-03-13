package game;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * Listens for mouse presses and movements, and performs repositioning on
 * the object to which it is attached.
 * 
 * @author Stephen
 * @version 2015-02-13
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
		if(it.isDraggable()){
			e.getComponent().getParent().setComponentZOrder(e.getComponent(), 0); 
			difference = e.getPoint();
		}
	}
	
	/**
	 * Continually active as long as the mouse is still moving.
	 * Sets the position of the dragged card to a new location that matches the
	 * mouse drag motion.
	 */
	public void mouseDragged(MouseEvent e) {
		if(it.isDraggable()){
			//store relevant information
			Point mouse = e.getPoint();
			Point origin = it.getLocation();
			
			//cast Points as ints, and move the card
			int newX = (int) (origin.getX()+mouse.getX()-difference.getX());
			int newY = (int) (origin.getY()+mouse.getY()-difference.getY());
			it.setLocation(newX, newY);
		}
	}

	public void mouseReleased(MouseEvent e){
		
	}
	
	//these methods are not used, but must be defined from the abstract superclass.
	public void mouseMoved(MouseEvent arg0) {}
	public void mouseClicked(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
}
