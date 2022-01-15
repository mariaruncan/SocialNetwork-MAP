package socialnetwork.domain.utils;

import java.util.*;

/**
 * Graph class
 */
public class Graph {
    private final int V; // No. of vertices in graph.

    private final LinkedList<Integer>[] adj; // Adjacency List
    // representation

    ArrayList<ArrayList<Integer> > components
            = new ArrayList<>();

    /**
     * constructor
     * @param v - int
     */
    @SuppressWarnings("unchecked")
    public Graph(int v)
    {
        V = v;
        adj = new LinkedList[v+1];

        for (int i = 1; i <= v; i++)
            adj[i] = new LinkedList();
    }

    /**
     * adds an edge
     * @param u - int
     * @param w - int
     */
    public void addEdge(int u, int w)
    {
        adj[u].add(w);
        adj[w].add(u); // Undirected Graph.
    }

    /**
     *
     * @param v - int
     * @param visited - boolean[]
     * @param al - ArrayList<Integer>
     */
    void DFSUtil(int v, boolean[] visited,
                 ArrayList<Integer> al)
    {
        visited[v] = true;
        al.add(v);
        Iterator<Integer> it = adj[v].iterator();

        while (it.hasNext()) {
            int n = it.next();
            if (!visited[n])
                DFSUtil(n, visited, al);
        }
    }

    /***
     * Depth-first search
     */
    public void DFS()
    {
        boolean[] visited = new boolean[V+1];

        for (int i = 1; i <= V; i++) {
            ArrayList<Integer> al = new ArrayList<>();
            if (!visited[i]) {
                DFSUtil(i, visited, al);
                components.add(al);
            }
        }
    }

    /**
     * returns the number of connected components in graph
     * @return int
     */
    public int connectedComponents() { return components.size(); }

    /***
     * returns list of connected components
     * @return List<List<Integer>>
     */
    public ArrayList<ArrayList<Integer> > returnComponents() { return components;}
}
