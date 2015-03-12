/*
 * Defines methods necessary to properly display a card image.
 * Encapsulates required JPanel functions in a class, so that it can be easily created and accessed from an array.
 * @author Stephen Belden
 * @version 2015.01.30
 */

import javax.swing.JPanel;
import javax.swing.ImageIcon;
import java.awt.Graphics;

public class cardPanel extends JPanel {
	ImageIcon card;
	
	//Overrides the JPanel constructor
	public cardPanel(ImageIcon card) {
		this.card = card;
	}
	
	//Called automatically when this object needs to be redrawn
	public void paintComponent(Graphics g) {
        super.paintComponent(g);
        this.setOpaque(false);
        card.paintIcon(this, g, 0, 0);
    }

}
