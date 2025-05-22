package stratgame.game.util;

import java.util.PriorityQueue;
import org.joml.Vector3f;

import stratgame.game.GameManager;

import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

class Node implements Comparable<Node> {
    int x, y;
    int f, g, h;
    Node parent;

    public Node(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public int compareTo(Node other) {
        return Integer.compare(this.f, other.f);
    }
    @Override
    public String toString() {
        return "x: " + x + "y: " + y + "f: " + f + "g: " + g + "h: " + h;
    }
}

public class Pathfinder {
    private static Node temp1 = new Node(0, 0);
    private static Node temp2 = new Node(0, 0);
    public static int[][] findPath(Vector3f start, Vector3f end){
        temp1.x = (int)(start.x/GameManager.getMGS());
        temp1.y = (int)(start.z/GameManager.getMGS());
        temp2.x = (int)(end.x/GameManager.getMGS());
        temp2.y = (int)(end.z/GameManager.getMGS());
        List<Node> pathList = findPath(GameManager.getMS(), temp1, temp2);
        if(pathList == null){
            return new int[][] {{(int)start.x,(int)start.z}};
        }
        int[][] path = new int[pathList.size()][2];
        for (int i = 0; i < path.length; i++) {
            path[i][0] = pathList.get(i).x;
            path[i][1] = pathList.get(i).y;
        }
        return path;
    }

    public static List<Node> findPath(double[][] grid, Node start, Node end) {
        PriorityQueue<Node> openSet = new PriorityQueue<>();
        HashSet<Node> closedSet = new HashSet<>();

        start.g = 0;
        start.h = heuristic(start, end, grid);
        start.f = start.g + start.h;

        openSet.add(start);

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();

            if (current.x == end.x && current.y == end.y) {
                return reconstructPath(current);
            }

            closedSet.add(current);

            for (Node neighbor : getNeighbors(grid, current)) {
                if (closedSet.contains(neighbor)) {
                    continue;
                }

                int tentativeG = current.g + 1;

                if (tentativeG < neighbor.g || !openSet.contains(neighbor)) {
                    neighbor.g = tentativeG;
                    neighbor.h = heuristic(neighbor, end, grid);
                    neighbor.f = neighbor.g + neighbor.h;
                    neighbor.parent = current;

                    if (!openSet.contains(neighbor)) {
                        openSet.add(neighbor);
                    }
                }
            }
        }

        return null;
    }

    private static List<Node> reconstructPath(Node current) {
        List<Node> path = new ArrayList<>();
        while (current != null) {
            path.add(current);
            current = current.parent;
        }
        Collections.reverse(path);
        return path;
    }

    private static int heuristic(Node a, Node b, double[][] grid) {
         return (int)((Math.abs(a.x - b.x) + Math.abs(a.y - b.y)));//*grid[a.x][a.y]); // Manhattan distance
    }

    private static int[][] directions = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}, {1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
    private static List<Node> getNeighbors(double[][] grid, Node node) {
        List<Node> neighbors = new ArrayList<>();

        for (int[] dir : directions) {
            int newX = node.x + dir[0];
            int newY = node.y + dir[1];

            if (newX >= 0 && newX < grid.length && newY >= 0 && newY < grid[0].length) {
                neighbors.add(new Node(newX, newY));
            }
        }
        return neighbors;
    }
}