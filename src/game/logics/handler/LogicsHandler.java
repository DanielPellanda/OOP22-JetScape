package game.logics.handler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.awt.Color;
import java.awt.Graphics2D;

import java.lang.Runnable;

import game.logics.entities.basic.*;
import game.logics.entities.obstacles.Obstacle;
import game.logics.entities.obstacles.ZapperBaseInstance;
import game.logics.entities.obstacles.ZapperRayInstance;
import game.logics.entities.player.PlayerInstance;
import game.logics.interactions.Generator;
import game.logics.interactions.SpeedHandler;
import game.logics.interactions.TileGenerator;
import game.utility.debug.Debugger;
import game.utility.input.keyboard.KeyHandler;
import game.utility.screen.Screen;

/**
 * The <code>LogicsHandler</code> class helps <class>GameWindow</class> to update
 * and draw logical parts of the game like the Interface, Entities, Collisions, etc....
 * 
 * @author Daniel Pellanda
 */
public class LogicsHandler implements Logics{
	
	/**
	 * Contains the current active entities on the game environment.
	 */
	private final Map<String, Set<Entity>> entities = new HashMap<>();
	
	/**
	 * Generates sets of obstacles on the environment.
	 */
	private final Generator spawner;
	private final Screen screen;
	private final KeyHandler keyH;
	
	/**
	 * Keeps the game timer.
	 */
	private long updateTimer = System.nanoTime();
	/**
	 * Defines how many seconds have to pass for the spawner to generate
	 * another set of obstacles.
	 */
	private int spawnInterval = 3;
	/**
	 * Defines the interval of each check for entities to clean.
	 */
	private int cleanInterval = 1;
	
	private Debugger debugger;
	
	/**
	 * Constructor that gets the screen information, the keyboard listener and the debugger, 
	 * initialize each entity category on the entities map and initialize the obstacle spawner.
	 * 
	 * @param screen the screen information of the game window
	 * @param keyH the keyboard listener linked to the game window
	 * @param debugger the debugger used
	 */
	public LogicsHandler(final Screen screen, final KeyHandler keyH, final Debugger debugger) {
		this.screen = screen;
		this.keyH = keyH;
		this.debugger = debugger;
		
		entities.put("player", new HashSet<>());
		entities.put("zappers", new HashSet<>());
		
		entities.get("player").add(new PlayerInstance(this));
		
		spawner = new TileGenerator(entities, spawnInterval);
		spawner.setZapperBaseCreator(p -> new ZapperBaseInstance(this, p, new SpeedHandler()));
		spawner.setZapperRayCreator((b,p) -> new ZapperRayInstance(this, p, b.getX(), b.getY()));
		
		spawner.initialize();
		spawner.pause();
	}

/*
	private void beginGame() {
		entities.get("player").add(new PlayerInstance(this));
		spawner.resume();
	}
	
	private void endGame() {
		spawner.pause();
		entities.forEach((s, se) -> {
			se.forEach(e -> e.resetPosition());
			se.clear();
		});
	}
	
	private void pauseGame() {
		spawner.pause();
	}
	
	private void resumeGame() {
		spawner.resume();
	}
*/

	/**
	 * Method for test enabling and disabling entity spawner
	 */
	private void checkSpawner() {
		if(keyH.input.get("c")) {
			if(spawner.isRunning()) {
				spawner.resume();
			} else {
				spawner.start();
			}
		} else if(keyH.input.get("v")) {
			spawner.pause();
		}
	}

	
	/**
	 * Handles the enabling and disabling of the Debug Mode 
	 * by using Z (enable) and X (disable).
	 */
	private void checkDebugMode() {
		if(keyH.input.get("z")) {
			debugger.setDebugMode(true);
		} else if(keyH.input.get("x")) {
			debugger.setDebugMode(false);
		}
	}
	
	/**
	 * Removes all entities that are on the "clear area" [x < -tile size].
	 */
	private void cleanEntities() {
		entities.get("zappers").removeIf(e -> {
			Obstacle o = (Obstacle)e;
			if(o.isOnClearArea()) {
				o.resetPosition();
				if(debugger.isFeatureEnabled("log: entities cleaner working")) {
					System.out.println("reset");
				}
			}
			return o.isOnClearArea();
		});
	}
	
	/**
	 * Utility function for running a certain block of code every given interval of time.
	 * 
	 * @param interval the interval in nanoseconds that has to pass after each execution
	 * @param timeStart the system time from when the last execution happened
	 * @param r the block of the code to execute
	 * @return <code>true</code> if given code has been executed, <code>false</code> if not
	 */
	private boolean updateEachInterval(final long interval, final long timeStart, final Runnable r) {
		long timePassed = System.nanoTime() - timeStart;
		if(timePassed >= interval) {
			r.run();
			return true;
		}
		return false;
	}
	
	public Screen getScreenInfo() {
		return screen;
	}
	
	public KeyHandler getKeyHandler() {
		return keyH;
	}
	
	public Debugger getDebugger() {
		return debugger;
	}
	
	public void updateAll() {
		if(updateEachInterval(cleanInterval * 1000000000, updateTimer, () -> cleanEntities())) {
			updateTimer = System.nanoTime();
			if(debugger.isFeatureEnabled("log: entities cleaner check")) {
				System.out.println("clean");
			}
		}
		checkDebugMode();
		checkSpawner();
		
		synchronized(entities) {
			entities.forEach((s, se) -> se.forEach(e -> e.update()));
		}
	}
	
	public void drawAll(final Graphics2D g) {
		synchronized(entities) {
			entities.forEach((s, se) -> se.forEach(e -> e.draw(g)));
			if(debugger.isFeatureEnabled("entity coordinates")) {
				entities.forEach((s, se) -> se.stream().filter(e -> e.isVisible()).collect(Collectors.toSet()).forEach(e -> {
					g.setColor(Color.white);
					g.setFont(Debugger.debugFont);
					g.drawString("X:" + Math.round(e.getX()), Math.round(e.getX()) + Math.round(screen.getTileSize()) + Math.round(screen.getTileSize() / (8 * Screen.tileScaling)), Math.round(e.getY()) + Math.round(screen.getTileSize()) +  Math.round(screen.getTileSize() / (4 * Screen.tileScaling)));
					g.drawString("Y:" + Math.round(e.getY()), Math.round(e.getX()) + Math.round(screen.getTileSize()) + Math.round(screen.getTileSize() / (8 * Screen.tileScaling)), 10 + Math.round(e.getY()) + Math.round(screen.getTileSize()) +  Math.round(screen.getTileSize() / (4 * Screen.tileScaling)));
				}));
			}
		}
	}
}
