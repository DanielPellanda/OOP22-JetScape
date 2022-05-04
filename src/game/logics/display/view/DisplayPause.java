package game.logics.display.view;

import java.awt.Color;
import java.awt.Graphics2D;

import game.utility.other.MenuOption;
import game.utility.screen.Screen;

public class DisplayPause extends Display {
	static final int titleTile = 2;
	static final int titleShift = 3;
	static final int textShift = 2;
	static final String title = "Paused";

	public DisplayPause(final Screen gScreen) {
		super(gScreen);
		this.options.add(MenuOption.RESUME);
		this.options.add(MenuOption.MENU);
	}

	public void drawScreen(final Graphics2D g, final MenuOption selected) {
		super.selectedOption = selected;		
		//TITLE SHADOW
		g.setColor(Color.black);
		g.setFont(titleFont);
		int x = super.getCenteredX(gScreen, g, title);
		g.drawString(title, x + titleShift, gScreen.getTileSize() * titleTile);
		
		//TITLE
		g.setColor(Color.white);
		g.drawString(title, x, gScreen.getTileSize() * titleTile);
		
		//OPTIONS SHADOW
		g.setColor(Color.black);
		super.drawText(g, textShift);
		
		//OPTIONS
		g.setColor(Color.white);
		super.drawText(g,0);
	}	
}
