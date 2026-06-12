/* Summer 26
COP 3503C Assignment 3
This program is written by: Richard Magiday */

import java.util.Scanner;

public class Main {

    static class DisjointSet {

        private final int[] parent;
        private final int[] rank;
        private final int[] size;
        long connectivity;    // current sum of size^2 across all components

        DisjointSet(int n) {
            parent = new int[n]; // 0-indexed nodes
            rank   = new int[n];
            size   = new int[n];

            for (int i = 0; i < n; i++) {
                parent[i] = i;
                rank[i]   = 0;
                size[i]   = 1;
            }

            // n singletons, each contributes 1^2 = 1
            connectivity = (long) n;
        }

        // Find with path compression
        int find(int id) {
            if (id == parent[id])
                return id;

            int root = find(parent[id]);
            parent[id] = root; // path compression
            return root;
        }

        // Union by rank, updates connectivity if components merge
        void union(int u, int v) {
            int root1 = find(u);
            int root2 = find(v);

            if (root1 == root2)
                return; // already in same component, connectivity unchanged

            // Remove the two old component contributions from connectivity
            connectivity -= (long) size[root1] * size[root1];
            connectivity -= (long) size[root2] * size[root2];

            // Attach smaller rank tree under larger-rank root
            if (rank[root1] > rank[root2]) {
                parent[root2] = root1;
                size[root1] += size[root2];
                connectivity += (long) size[root1] * size[root1];
            } else if (rank[root2] > rank[root1]) {
                parent[root1] = root2;
                size[root2] += size[root1];
                connectivity += (long) size[root2] * size[root2];
            } else {
                // Equal ranks: attach root2 under root1, increase root1's rank
                parent[root2] = root1;
                size[root1] += size[root2];
                rank[root1]++;
                connectivity += (long) size[root1] * size[root1];
            }
        }
    }

    static class Edge {
        int u, v;
        Edge(int u, int v) {
            this.u = u;
            this.v = v;
        }
    }

    public static void main(String[] args) {
        try (Scanner sc = new Scanner(System.in)) {

        int n = sc.nextInt(); // number of computers
        int m = sc.nextInt(); // number of connections
        int d = sc.nextInt(); // number of connections to destroy

        // Store all m edges (0-indexed: edges[0] .. edges[m-1])
        Edge[] edges = new Edge[m];
        for (int i = 0; i < m; i++) {
            int u = sc.nextInt() - 1; // convert 1-based node to 0-based
            int v = sc.nextInt() - 1;
            edges[i] = new Edge(u, v);
        }

        // Read the d destruction steps, mark which edges will be deleted
        int[] deleteList = new int[d];    // 0-based edge index destroyed at step i
        boolean[] deleted    = new boolean[m]; // true if edge i is in deleteList

        for (int i = 0; i < d; i++) {
            deleteList[i]          = sc.nextInt() - 1; // convert 1-based edge index to 0-based
            deleted[deleteList[i]] = true;
        }

        DisjointSet ds = new DisjointSet(n);

        // Union all edges that survive all d deletions to build the final state
        for (int i = 0; i < m; i++) {
            if (!deleted[i]) {
                ds.union(edges[i].u, edges[i].v);
            }
        }

        // results[i] = connectivity of the network after the i-th deletion
        // results[0] = connectivity before ANY deletion (initial state)
        long[] results = new long[d + 1];

        // State after all d deletions
        results[d] = ds.connectivity;

        // Replay deletions in reverse to fill results[d-1] down to results[0]
        for (int i = d - 1; i >= 0; i--) {
            int edgeIdx = deleteList[i];
            ds.union(edges[edgeIdx].u, edges[edgeIdx].v);
            results[i] = ds.connectivity;
        }

        // Print all d+1 results: initial connectivity, then after each deletion
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i <= d; i++) {
            sb.append(results[i]).append('\n');
        }
        System.out.print(sb);
        } // end try-with-resources
    }
}