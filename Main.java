/* COP 3503C Assignment 3
This program is written by: Richard Magiday */

import java.util.Scanner;

public class Main {

    static class DisjointSet {

        private final int[] parent; // parent[i] = parent of node i (self = root)
        private final int[] rank;   // rank[i] = upper bound on tree height at root i
        private final int[] size;   // size[i]  = component size (only valid at root)
        long connectivity;    // current sum of size^2 across all components

        DisjointSet(int n) {
            parent = new int[n + 1]; // 1-indexed nodes
            rank   = new int[n + 1];
            size   = new int[n + 1];

            for (int i = 1; i <= n; i++) {
                parent[i] = i;
                rank[i]   = 0;
                size[i]   = 1;
            }

            // n singletons → each contributes 1^2 = 1
            connectivity = (long) n;
        }

        // Find with path compression (flattens tree toward root)
        int find(int id) {
            if (id == parent[id])
                return id;

            // TODO: recursively find root and apply path compression
            // (set parent[id] = root so future finds are O(1))
            int root = find(parent[id]);
            parent[id] = root; // path compression
            return root;
        }

        // Union by rank; updates connectivity if components merge
        void union(int u, int v) {
            int root1 = find(u);
            int root2 = find(v);

            if (root1 == root2)
                return; // already in same component — connectivity unchanged

            // TODO: subtract the squared sizes of the two old components from connectivity,
            //       merge by rank (attach smaller-rank tree under larger-rank root),
            //       update the surviving root's size,
            //       then add the squared size of the merged component to connectivity.

            // Remove old contributions
            connectivity -= (long) size[root1] * size[root1];
            connectivity -= (long) size[root2] * size[root2];

            // Union by rank
            if (rank[root1] > rank[root2]) {
                // TODO: attach root2 under root1, update size[root1]
            } else if (rank[root2] > rank[root1]) {
                // TODO: attach root1 under root2, update size[root2]
            } else {
                // TODO: equal rank — attach root2 under root1, increment rank[root1], update size
            }

            // TODO: add new merged component's squared size to connectivity
        }
    }

    // -------------------------------------------------------------------------
    // Edge helper — stores the two endpoints of each connection (1-indexed)
    // -------------------------------------------------------------------------
    static class Edge {
        int u, v;
        Edge(int u, int v) {
            this.u = u;
            this.v = v;
        }
    }

    // -------------------------------------------------------------------------
    // Main
    // -------------------------------------------------------------------------
    public static void main(String[] args) {
        try (Scanner sc = new Scanner(System.in)) {

        int n = sc.nextInt(); // number of computers
        int m = sc.nextInt(); // number of connections
        int d = sc.nextInt(); // number of connections to destroy

        // Store all m edges (1-indexed: edge[1] .. edge[m])
        Edge[] edges = new Edge[m + 1];
        for (int i = 1; i <= m; i++) {
            int u = sc.nextInt();
            int v = sc.nextInt();
            edges[i] = new Edge(u, v);
        }

        // Read the d destruction steps; mark which edges will be deleted
        int[]     deleteList = new int[d];       // deleteList[i] = edge index destroyed at step i
        boolean[] deleted    = new boolean[m + 1]; // deleted[i] = true if edge i is in deleteList

        for (int i = 0; i < d; i++) {
            deleteList[i]            = sc.nextInt();
            deleted[deleteList[i]]   = true;
        }

        // -----------------------------------------------------------------
        // Bottom-up strategy (see Hint 6):
        //   1. Build DS using only the edges that survive ALL d deletions.
        //   2. That gives us the connectivity AFTER the last deletion.
        //   3. Then replay deletions in reverse (add edges back one by one)
        //      to recover the connectivity at each earlier step.
        // -----------------------------------------------------------------

        DisjointSet ds = new DisjointSet(n);

        // Union all edges that survive all d deletions to build the final state
        for (int i = 1; i <= m; i++) {
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
