package game.logics.entities.player;

import java.awt.Color;

import game.frame.GameWindow;
import game.logics.entities.generic.EntityInstance;
import game.logics.handler.Logics;
import game.utility.input.keyboard.KeyHandler;
import game.utility.other.Pair;

/**
 * The <code>PlayerInstance</code> class represents the player's entity in
 * the game environment.
 * 
 * @author Daniel Pellanda
 */
public class PlayerInstance extends EntityInstance implements Player{
	
	/**
	 * Specifies the path within the texture folder [specified in <code>Texture</code> class]
	 * where <code>PlayerInstance</code> textures can be found.
	 */
	private static final String texturePath = "player" + System.getProperty("file.separator");
	/**
	 * If textures are missing, they will be replace by a rectangle of the color specified in
	 * <code>PlayerInstance.placeH</code>.
	 */
	private static final Color placeH = Color.white;
	/**
	 * Determines how fast textures change.
	 */
	private static final double animationSpeed = 6;
	
	/**
	 * The current jump speed of the player.
	 */
	private final double jumpSpeed;
	/**
	 * The current fall speed of the player.
	 */
	private final double fallSpeed;

	/**
	 * The current multiplier applied to the speed jump.
	 */
	private double jumpMultiplier = initialJumpMultiplier;
	/**
	 * The current multiplier applied to the speed fall.
	 */
	private double fallMultiplier = initialFallMultiplier;
	
	private final KeyHandler keyH;
	
	/**
	 * A string describing the current action of the player.
	 * It can either "<code>idle</code>", "<code>jump</code>" (jumping) and "<code>fall</code>" (falling).
	 */
	private String action;
	
	/**
	 * A flag indicating if player has changed his current action.
	 */
	private boolean actionChanged = false;
	/**
	 * A flag indicating if player is going from a "fall" action to an "idle" action.
	 */
	private boolean landing = false;
	/**
	 * Decides which texture should be displayed.
	 */
	private int textureSwitcher = 1;
	/**
	 * How many frames have passed since between a second and another.
	 */
	private int frameTime = 0;
	
	/**
	 * Constructor used for initializing basic parts of the player entity.
	 * 
	 * @param l the logics handler which the entity is linked to
	 */
	public PlayerInstance(final Logics l) {
		super(l);
		this.keyH = l.getKeyHandler();
		
		fallSpeed = baseFallSpeed / maximumFPS;
		jumpSpeed = baseJumpSpeed / maximumFPS;

		position = new Pair<>(xPosition, yGround);
		action = "idle";
		entityTag = "player";
		
		textureMgr.setPlaceH(placeH);
		textureMgr.addTexture("walk1", texturePath + "barrywalk1.png");
		textureMgr.addTexture("walk2", texturePath + "barrywalk2.png");
		textureMgr.addTexture("walk3", texturePath + "barrywalk3.png");
		textureMgr.addTexture("walk4", texturePath + "barrywalk4.png");
		textureMgr.addTexture("jump1", texturePath + "barryjump1.png");
		textureMgr.addTexture("jump2", texturePath + "barryjump2.png");
		textureMgr.addTexture("fall1", texturePath + "barryfall1.png");
		textureMgr.addTexture("fall2", texturePath + "barryfall2.png");
		textureMgr.addTexture("land1", texturePath + "barryland1.png");
		textureMgr.addTexture("land2", texturePath + "barryland2.png");
		textureMgr.addTexture("land3", texturePath + "barryland3.png");
		textureMgr.addTexture("land4", texturePath + "barryland4.png");
		textureMgr.setAnimator(() -> {
			String s = "";
			switch(action) {
				case "idle":
					s = "walk" + textureSwitcher;
					break;
				case "land":
					s = "land" + textureSwitcher;
					break;
				case "jump":
					s = "jump" + (textureSwitcher % 2 + 1);
					break;
				case "fall":
					s = "fall" + (textureSwitcher % 2 + 1);
					break;
			}
			updateTexture();
			return s;
		});
	}
	
	private void jump() {
		position.setY(position.getY() - jumpSpeed * jumpMultiplier > yRoof ? position.getY() - jumpSpeed * jumpMultiplier : yRoof);
		setAction("jump");
	}
	
	private void fall() {
		if(position.getY() + fallSpeed * fallMultiplier < yGround) {
			position.setY(position.getY() + fallSpeed * fallMultiplier);
			setAction("fall");
		} else {
			position.setY(yGround);
			setAction("land");
		}
	}

	/**
	 * Sets the current player's action.
	 * 
	 * @param newAction the new action
	 */
	private void setAction(final String newAction) {
		if(action != newAction) {
			actionChanged = true;
			landing = newAction == "land";
		}
		action = newAction;
	}
	
	/**
	 * Updates the texture that should be display during the animation.
	 */
	private void updateTexture() {
		if(this.actionChanged) {
			frameTime = 0;
			textureSwitcher = 1;
			this.actionChanged = false;
		}
		else if(frameTime >= GameWindow.fpsLimit / animationSpeed) {
			if(this.landing && textureSwitcher == 4) {
				setAction("idle");
			}
			frameTime = 0;
			textureSwitcher = textureSwitcher >= 4 ? 1 : textureSwitcher + 1;
		}
		frameTime++;
	}
	
	@Override
	public void update() {
		super.update();
		if(keyH.input.get("spacebar")) {
			jump();
			jumpMultiplier += jumpMultiplierIncrease;
			fallMultiplier = initialFallMultiplier;
		} else if(action != "idle") {
			fall();
			fallMultiplier += fallMultiplierIncrease;
			jumpMultiplier = initialJumpMultiplier;
		}
	}

}
