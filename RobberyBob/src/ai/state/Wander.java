package ai.state;

import ai.AITransition;
import core.Movement;
import core.Position;
import entity.GameObject;
import game.Game;
import tile.pathfinder.pathfinder;

import java.util.ArrayList;
import java.util.List;

public class Wander extends AIState {
    private List<Position> targets;
    private final pathfinder pf;
    private int currentPathIndex = 1;
    Position targetPosition;

    String state = "Stand";

    private boolean seen = false;

    public Wander() {
        super();
        targets = new ArrayList<>();
        this.pf = new pathfinder();
    }

    @Override
    protected AITransition initializeTransition() {
        return state == "Pursue" ? new AITransition("Pursue", ((game, entity) -> seen)) : new AITransition("Stand", ((game, entity) -> arrived(entity)));
    }

    @Override
    public void update(Game game, GameObject entity) {
        if (targets.isEmpty()) {
            System.out.println("Find Target again NIGGA");
            getRandomPosition(game, entity);
        }

        move(game, entity);

        if (arrived(entity)) {
            entity.movement.stop();
        }
    }

    private boolean seeing(Game game, GameObject entity) {
        boolean track = entity.los.LineOfSight(entity.getPosition().getX(), entity.getPosition().getY(), game.getPlayer().getPosition().getX(), game.getPlayer().getPosition().getY(), game.getMap());

        if (track){
            System.out.println("Tracking!");
        } else {
            System.out.println("Not tracking!");
        }
        return track;
    }

    //#region AI Movement
    private void getRandomPosition(Game game, GameObject entity) {
        int x, y;
        do {
            x = 1 + (int) (Math.random() * 78);
            y = 1 + (int) (Math.random() * 38);
            System.out.println("Random Position: " + x + ", " + y);
            targetPosition = new Position(x * 40, y * 40);
        } while ((game.getMap().map[y][x] == 0 || game.getMap().map[y][x] == 2)
                || !(targetPosition.getfX() > 50 && targetPosition.getfX() < 3150)
                || !(targetPosition.getfY() > 50 && targetPosition.getfY() < 1500));
        //
        Position startPosition = entity.getPosition();

        //System.out.println("Current Position: " + entity.getPosition().getfX() + ", " + entity.getPosition().getfY());

        //System.out.println("Target Position: " + targetPosition.getfX() + ", " + targetPosition.getfY());

        pf.printInfo = 0;
        targets = pathfinder.findPath(startPosition, targetPosition, game.getMap());
        System.out.println("Path: " + targets.get(0).getfX() + ", " + targets.get(0).getfY());
        currentPathIndex = 1;

        if (targets.size() <= 1) {
            targets.clear();
            targets = pathfinder.findPath(startPosition, targetPosition, game.getMap());
        }

        System.out.println("CurrentPathIndex: " + currentPathIndex);
        System.out.println("target size: " + targets.size());
        move(game, entity);
    }

    private void move(Game game, GameObject entity) {
        if (currentPathIndex < targets.size()) {
            System.out.println("Moving...");
            Position start = entity.getPosition();
            Position target = targets.get(currentPathIndex);
            entity.movement.MoveTowards(start, target);
            if (entity.getPosition().getfX() == target.getfX() && entity.getPosition().getfY() == target.getfY()) {
                System.out.println("Target reached");
                currentPathIndex++;
            }
        }
    }
    //#endregion

    private boolean arrived(GameObject entity) {
        if (entity.getPosition().isInRangeOf(targetPosition)) {
            System.out.println("Arrived at target");
        }
        return entity.getPosition().isInRangeOf(targetPosition);
    }
}