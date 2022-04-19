package game.logics.entities.obstacles.generic;

import game.logics.entities.generic.Entity;
import game.logics.interactions.SpeedHandler;

/**
 * An Interface for accessing <code>ObstacleInstance</code> methods.
 * 
 * <p>
 * The abstract class <code>ObstacleInstance</code> is used to define the common parts of each obstacle.
 * </p>
 * 
 * @author Daniel Pellanda
 */
public abstract interface Obstacle extends Entity{

	/**
	 * @return the movement information of the obstacle
	 */
	SpeedHandler getSpeedHandler();
	
	/**
	 * @return <code>true</code> if the obstacle's position is on the "clear area", <code>false</code> if not
	 */
	boolean isOnClearArea();
	
	/**
	 * @return <code>true</code> if the obstacle's position is on the "spawn area", <code>false</code> if not
	 */
	boolean isOnSpawnArea();
}