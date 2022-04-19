package game.utility.sprites;

import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

import javax.imageio.ImageIO;

import game.utility.other.Pair;

/**
 * The <code>Texture</code> class is used for loading and draw images that
 * are used for representing objects visible on the game environment.
 * 
 * If the image cannot be loaded or any path isn't specified, a rectangle of
 * the color set by <code>Texture.placeHolder</code> will be drawn.
 * 
 * @author Daniel Pellanda
 */
public class Sprite {
	
	/**
	 * The type of file separator the system uses.
	 */
	private static final String separator = System.getProperty("file.separator");
	/**
	 * Default directory where are all sprites are located.
	 */
	public static final String defaultDir = System.getProperty("user.dir") + separator + "res" + separator + "game" + separator + "sprites" + separator;
	
	/**
	 * The image that will be drawn as sprite.
	 */
	private Optional<BufferedImage> image = Optional.empty();
	/**
	 * The color of the rectangle drawn as place holder.
	 */
	private final Color placeHolder;
	
	private String name = "unknown";
	
	/**
	 * Constructor that creates a <code>Sprite</code> object 
	 * without any image path specified.
	 * 
	 * @param name the sprite name identifier
	 * @param placeHolder the color of the rectangle drawn as place holder
	 */
	public Sprite(final String name, final Color placeHolder) {
		this.name = name;
		this.placeHolder = placeHolder;
	}
	
	/**
	 * Constructor that creates a <code>Sprite</code> object 
	 * with a image path specified.
	 * 
	 * @param name the sprite name identifier
	 * @param placeHolder the color of the rectangle drawn as place holder
	 * @param path the path of the image to load
	 */
	public Sprite(final String name, final Color placeHolder, final String path) {
		this(name, placeHolder);
		image = Optional.ofNullable(Sprite.load(defaultDir + path));
	}
	
	/**
	 * Loads a <code>BufferedImage</code> of a sprite and returns it.
	 * 
	 * @param path the image path
	 * @return a <code>BufferedImage</code> containing the sprite
	 */
	public static BufferedImage load(final String path) {
		BufferedImage loaded = null;
		try {
			loaded = ImageIO.read(new File(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return loaded;
	}
	
	/**
	 * @return the sprite name identifier
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Draws the loaded image or a rectangle place holder if the
	 * image hasn't been loaded.
	 * 
	 * @param g the graphics drawer
	 * @param pos the position to draw the image
	 * @param size the length of a side of the squared image
	 */
	public void draw(final Graphics2D g, final Pair<Double,Double> pos, final int size) {
		if(this.image.isPresent()) {
			g.drawImage(image.get(), (int)Math.round(pos.getX()), (int)Math.round(pos.getY()), size, size, null);
		} else {
			g.setColor(placeHolder);
			g.fillRect((int)Math.round(pos.getX()), (int)Math.round(pos.getY()), size, size);
		}
	}
}