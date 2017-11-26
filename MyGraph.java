import java.util.*;

/**
 * A representation of a graph.
 * Assumes that we do not have negative cost edges in the graph.
 * @author Evan Gordon
 * @author Kalvin Suting
 *
 * Last modified 5/31/2016
 */
public class MyGraph implements Graph {
    private static final int HASH_CONST = 90001; // Size of vertex hash.
    private Map<Vertex, Set<Edge>> adjMap; // Mapping of Vertices to their corresponding Edges.
    private Vertex[] vertexHash; // Hash table of all Vertices in graph.

    /**
     * A MyGraph object is instantiated.
     * @param vertices The collection of vertices to add to the graph.
     * @param edges The collection of edges between the vertices of the graph.
     * @throws IllegalArgumentException If passed a non-existent or negatively weighted edge.
     *         Also throws if a redundant edge is passed in, provided the redundant edge has a
     *         different weight from the one already in the graph. Does not throw if redundant
     *         edge with same weight is passed.
     */
    public MyGraph(Collection<Vertex> vertices, Collection<Edge> edges) {
        adjMap = new HashMap<Vertex, Set<Edge>>();
        vertexHash = new Vertex[HASH_CONST];

        // Hash each vertex parameter and add to adjMap.
        for (Vertex v : vertices) {
            vertexHash[v.hashCode()] = v;
            adjMap.put(v, new HashSet<Edge>());
        }

        // Check each possible edge for exceptions.
        for (Edge possibleEdge : edges) {
            Vertex from = vertexHash[possibleEdge.getSource().hashCode()];
            Vertex to = vertexHash[possibleEdge.getDestination().hashCode()];

            // Ensure destination and source vertices are in graph and weight is not negative.
            if (to == null || from == null || possibleEdge.getWeight() < 0) {
                throw new IllegalArgumentException("Invalid edge detected.");
            }

            // Ensure edge is not redundant with same weight.
            for (Edge graphEdge : adjMap.get(from)) {
                boolean sameSource = graphEdge.getSource().equals(possibleEdge.getSource());
                boolean sameDestination = graphEdge.getDestination().equals(possibleEdge.getDestination());
                boolean differentWeight = graphEdge.getWeight() != possibleEdge.getWeight();

                if (sameSource && sameDestination && differentWeight) {
                    throw new IllegalArgumentException("Redundant edge detected.");
                }
            }

            // Edge passed all checks; add to adjMap.
            adjMap.get(from).add(possibleEdge);
        }
    }

    /**
     * Return the collection of vertices of this graph
     * @return the vertices as a collection (which is anything iterable)
     */
    public Collection<Vertex> vertices() {
        return adjMap.keySet();
    }

    /**
     * Return the collection of edges of this graph
     * @return the edges as a collection (which is anything iterable)
     */
    public Collection<Edge> edges() {
        Set<Edge> edges = new HashSet<Edge>();
        for (Set<Edge> s : adjMap.values()) {
            edges.addAll(s);
        }
        return edges;
    }

    /**
     * Return a collection of vertices adjacent to a given vertex v.
     *   i.e., the set of all vertices w where edges v -> w exist in the graph.
     * Return an empty collection if there are no adjacent vertices.
     * @param v one of the vertices in the graph
     * @return an iterable collection of vertices adjacent to v in the graph
     * @throws IllegalArgumentException if v does not exist.
     */
    public Collection<Vertex> adjacentVertices(Vertex v) {
        if(vertexHash[v.hashCode()] == null){
            throw new IllegalArgumentException();
        }
        Set<Vertex> adjVert = new HashSet<Vertex>();
        v = vertexHash[v.hashCode()];
        for (Edge e : vertexEdges(v)) {
            Vertex adj = vertexHash[e.getDestination().hashCode()];
            adjVert.add(adj);
        }
        return adjVert;
    }

    /**
     *
     * @param a the vertex that this method will find edges for.
     * @return returns the edges out of vertex a.
     */
    public Set<Edge> vertexEdges(Vertex a){
        return adjMap.get(vertexHash[a.hashCode()]);
    }

    /**
     * Test whether vertex b is adjacent to vertex a (i.e. a -> b) in a directed graph.
     * Assumes that we do not have negative cost edges in the graph.
     * @param a one vertex
     * @param b another vertex
     * @return cost of edge if there is a directed edge from a to b in the graph,
     * return -1 otherwise.
     * @throws IllegalArgumentException if a or b do not exist.
     */
    public int edgeCost(Vertex a, Vertex b) {
        if(vertexHash[a.hashCode()] == null || vertexHash[b.hashCode()] == null){
            throw new IllegalArgumentException("Vertices must be in the graph.");
        }
        Vertex from = vertexHash[a.hashCode()];
        Vertex to = vertexHash[b.hashCode()];

        // Search each edge for desired destination.
        for (Edge e : vertexEdges(from)) {
            if (e.getDestination().equals(to)) {
                return e.getWeight();
            }
        }
        // Desired destination not adjacent to desired source.
        return -1;
    }

    /**
     * Returns the shortest path from a to b in the graph, or null if there is
     * no such path.  Assumes all edge weights are non-negative.
     * Uses Dijkstra's algorithm.
     * @param a the starting vertex
     * @param b the destination vertex
     * @return a Path where the vertices indicate the path from a to b in order
     *   and contains a (first) and b (last) and the cost is the cost of
     *   the path. Returns null if b is not reachable from a.
     * @throws IllegalArgumentException if a or b does not exist.
     */
    public Path shortestPath(Vertex a, Vertex b) {
        if (vertexHash[a.hashCode()] == null || vertexHash[b.hashCode()] == null) {
            throw new IllegalArgumentException("Node does not exist.");
        }

        // Initialize each vertex's cost to infinity and parent vertex to null
        for (Vertex v : vertices()) {
            v.setCost(Integer.MAX_VALUE);
            v.setParent(null);
        }
        Vertex currentSource = vertexHash[a.hashCode()];
        currentSource.setCost(0);
        Queue<Vertex> unvisitedVertices = new PriorityQueue<Vertex>();
        unvisitedVertices.addAll(vertices());
        while(!unvisitedVertices.isEmpty()) {
            currentSource = unvisitedVertices.remove(); // Get lowest cost adjacent vertex

            // Examine each adjacent vertex's cost.
            for (Vertex v : adjacentVertices(currentSource)) {

                // Ensure currentSource vertex reachable from original source
                if (currentSource.getCost() != Integer.MAX_VALUE) {
                    int cost = currentSource.getCost() + edgeCost(currentSource, v);
                    if (cost < v.getCost()) { // Found a better route than was previously known
                        v.setCost(cost);
                        unvisitedVertices.remove(v);
                        unvisitedVertices.add(v);
                        v.setParent(currentSource);
                    }
                }
            }
        }

        // Costs have been discovered; Find shortest path now.
        List<Vertex> path = new LinkedList<Vertex>();
        Vertex currentChild = vertexHash[b.hashCode()];
        while (currentChild != null) {
            path.add(0, currentChild);
            currentChild = currentChild.getParent();
        }
        return new Path(path, vertexHash[b.hashCode()].getCost());
    }

    /**
     *
     * @return returns the minimum spanning tree for the graph.
     */
    public Set<Edge> minSpanTree(){
        SortedSet<Edge> sortedEdges = new TreeSet<Edge>(); //set of sorted edges
        Set<Vertex> unvisitedVertices = new HashSet<Vertex>(); //all unvisited vertices.
        Set<Edge> minEdges = new TreeSet<>(); // set of the edges that form minSpanTree
        unvisitedVertices.addAll(vertices());
        sortedEdges.addAll(edges());
        while(!unvisitedVertices.isEmpty()){ // while there are vertices still unvisited
            for(Edge e : sortedEdges){
                Vertex source = e.getSource();
                Vertex destination = e.getDestination();
                if(unvisitedVertices.contains(source)){ //source vertex not in minSpanTree
                    minEdges.add(e); //adds the edge to set
                    unvisitedVertices.remove(source); // vertex now visited
                }
                if(unvisitedVertices.contains(destination)){//destination vertex not in minSpanTree
                    minEdges.add(e); //adds the edge to set
                    unvisitedVertices.remove(destination);
                }
            }
        }
        return minEdges; //returns the set of edges that form the minSpanTree
    }
}