import javax.swing.JPanel;
import javax.swing.ImageIcon;

import java.awt.Graphics;

/**
 * Defines methods necessary to properly display a card image.
 * This class encapsulates the JPanel functions necessary for use in
 * displaying cards. This encapsulation allows pre-configured CardPanel
 * objects to be easily created and accessed from an array.
 * Any information about the state of the card used for interaction is
 * stored here as well.
 * 
 * @author Stephen Belden
 * @version 2015-02-13
 */

public class CardPanel extends JPanel {
	/**
	 * A reference to the ImageIcon that stores this CardPanel's image.
	 */
	private ImageIcon card;
	/**
	 * Whether or not this card is allowed to be moved by the user.
	 */
	private boolean draggable;
	/**
	 * A reference to the container that this object was added to.
	 */
	private PlayArea reference;
	
	/**
	 * Wraps a single card image in a custom JPanel.
	 * 
	 * @param card must be a valid ImageIcon.
	 * @param ref must be the object that created this object.
	 */
	public CardPanel(ImageIcon card, PlayArea ref) {
		this.card = card;
		reference = ref;
		draggable = true;
		this.setVisible(true);
		Drag mouseListenInst = new Drag(this);
		addMouseListener(mouseListenInst);
		addMouseMotionListener(mouseListenInst);
	}
	
	/**
	 * Called automatically when this component needs to be redrawn.
	 * 
	 * @param g a Graphics object.
	 */
	public void paintComponent(Graphics g) {
        super.paintComponent(g);
        this.setOpaque(false);
        card.paintIcon(this, g, 0, 0);
    }
	
	/**
	 * Forces this card to appear over all other cards.
	 * 
	 * <p><b>Caveats:</b> Since the highestLayer is increased by one every
	 * time a card is moved, and the layer is stored as an integer,
	 * no more than 2,147,483,647 moves can be made.
	 */
	public void makeTop(){
		reference.setLayer(this, reference.highestLayer() + 1);
	}
	
	/**
	 * @param b is the new boolean value of draggable
	 */
	public void setDraggable(boolean b) {
		draggable = b;
	}
	
	/**
	 * @return a boolean representing whether or not the user can drag this card.
	 */
	public boolean getDraggable() {
		return draggable;
	}
}