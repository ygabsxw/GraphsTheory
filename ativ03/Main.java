package ativ03;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Stack;


class Edge {
    int destination;
    int weight;

    public Edge(int destination, int weight) {
        this.destination = destination;
        this.weight = weight;
    }
}

/**
 * Classe para a Fila de Prioridade (PriorityQueue) do Dijkstra.
 * Armazena o vértice e a distância (peso) acumulada até ele.
 * Implementa Comparable para que a PriorityQueue funcione como um Min-Heap.
 */
class Node implements Comparable<Node> {
    int vertex;
    long distance; // Usar long para evitar estouro com somas de pesos

    public Node(int vertex, long distance) {
        this.vertex = vertex;
        this.distance = distance;
    }

    @Override
    public int compareTo(Node other) {
        // Compara baseado na menor distância (peso)
        return Long.compare(this.distance, other.distance);
    }
}

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        int option = 0;
        String fileName = "";

        do {
            System.out.println("1 - Tree graph (1000 vertices)");
            System.out.println("2 - Tree graph (10000 vertices)");
            System.out.println("3 - Tree graph (100000 vertices)");
            System.out.println("4 - Tree graph (500000 vertices)");
            System.out.println("5 - Euler graph (1000 vertices)");
            System.out.println("6 - Euler graph (10000 vertices)");
            System.out.println("7 - Euler graph (100000 vertices)");
            System.out.println("8 - Euler graph (500000 vertices)");
            System.out.println("9 - Custom graph");
            System.out.println("0 - Exit");
            System.out.print("Choose an option: ");
            
            try {
                 option = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid option. Try again.");
                continue;
            }

            switch (option) {
                case 1:
                    fileName = "ativ03/graphs/tree_1000.txt"; 
                    break;
                case 2:
                    fileName = "ativ03/graphs/tree_10000.txt";
                    break;
                case 3:
                    fileName = "ativ03/graphs/tree_100000.txt"; 
                    break;
                case 4:
                    fileName = "ativ03/graphs/tree_500000.txt";
                    break;
                case 5:
                    fileName = "ativ03/graphs/euler_1000.txt"; 
                    break;
                case 6:
                    fileName = "ativ03/graphs/euler_10000.txt";
                    break;
                case 7:
                    fileName = "ativ03/graphs/euler_100000.txt";
                    break;
                case 8:
                    fileName = "ativ03/graphs/euler_500000.txt";
                    break;
                case 9:
                    System.out.print("Write the name or path of the file: ");
                    fileName = sc.nextLine();
                    break;
                case 0:
                    System.out.println("Exiting...");
                    continue; 
                default:
                    System.out.println("Invalid option.");
                    continue; 
            }

            Graph graph = readGraph(fileName);

            System.out.print("Select a source vertice: ");
            int source = sc.nextInt();
            System.out.print("Select a destination vertice: ");
            int destination = sc.nextInt();
            sc.nextLine();

            long startTime = System.nanoTime();

            dijkstra(graph, source, destination, true);
            
            long endTime = System.nanoTime();
            long totalNanoTime = (endTime - startTime);
            double totalMilliTime = totalNanoTime / 1_000_000.0; 

            System.out.println("--- EFFICIENCY RESULTS ---");
            System.out.println("Graph Size (Vertices): " + graph.size());
            System.out.println("Execution Time (milliseconds): " + String.format("%.6f", totalMilliTime) + " ms");
            System.out.println("Execution Time (seconds): " + String.format("%.6f", totalMilliTime / 1000.0) + " s");
            System.out.println("----------------------------------------\n");

        } while (option != 0);

        sc.close();
    }

    public static Graph readGraph(String fileName) {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {

            String firstLine = reader.readLine();
            String[] parts = firstLine.trim().split("\\s+");
            int n = Integer.parseInt(parts[0]); // vértices
            int m = Integer.parseInt(parts[1]); // arestas

            Graph graph = new Graph(n);

            for (int i = 0; i < m; i++) {
                String edgeLine = reader.readLine();
                if (edgeLine == null || edgeLine.trim().isEmpty()) {
                    i--;
                    continue;
                }

                String[] edgeParts = edgeLine.trim().split("\\s+");
                int o = Integer.parseInt(edgeParts[0]); // origem
                int d = Integer.parseInt(edgeParts[1]); // destino
                int w = Integer.parseInt(edgeParts[2]); // peso

                graph.addEdge(o, d, w);
            }

            return graph;

        } catch (IOException e) {
            System.err.println("Error to read the file '" + fileName + "': " + e.getMessage());
            return null;
        } catch (NumberFormatException e) {
            System.err.println("Error parsing number in file: " + e.getMessage());
            return null;
        }
    }

    /**
     * Implementação do Algoritmo de Dijkstra (baseado em GeeksforGeeks) 
     * modificado para encontrar o caminho com menor número de arestas em
     * caso de empate no peso total. 
     * https://www.geeksforgeeks.org/dsa/dijkstras-shortest-path-algorithm-greedy-algo-7
     *
     * @param graph       O grafo
     * @param source      Vértice de origem
     * @param destination Vértice de destino
     * @param printResults Controla se o resultado deve ser impresso no console
     */
    public static void dijkstra(Graph graph, int source, int destination, boolean printResults) {
        int n = graph.size();

        // dist[i] = menor PESO (comprimento) do caminho de source até i
        long[] dist = new long[n + 1];
        // numEdges[i] = menor NÚMERO DE ARESTAS para o caminho em dist[i]
        int[] numEdges = new int[n + 1];
        // parent[i] = predecessor de i no caminho mínimo
        int[] parent = new int[n + 1];

        Arrays.fill(dist, Long.MAX_VALUE);
        Arrays.fill(numEdges, Integer.MAX_VALUE);
        Arrays.fill(parent, -1);

        dist[source] = 0;
        numEdges[source] = 0;

        PriorityQueue<Node> pq = new PriorityQueue<>();
        pq.add(new Node(source, 0));

        while (!pq.isEmpty()) {
            Node currentNode = pq.poll();
            int u = currentNode.vertex;
            long d = currentNode.distance;

            // Otimização: se já encontramos um caminho melhor para 'u',
            if (d > dist[u]) {
                continue;
            }

            // Se chegamos ao destino, podemos parar (otimização)
            // if (u == destination) break; // Funciona se não houver arestas de peso 0

            // Itera sobre todos os vizinhos 'v' de 'u'
            for (Edge edge : graph.getSucessors(u)) {
                int v = edge.destination;
                int weight = edge.weight;

                long newDist = dist[u] + weight;
                int newEdges = numEdges[u] + 1;

                // Encontramos um caminho com PESO menor.
                if (newDist < dist[v]) {
                    dist[v] = newDist;
                    numEdges[v] = newEdges; // Atualiza o num de arestas
                    parent[v] = u;          // Atualiza o predecessor
                    pq.add(new Node(v, newDist)); // Adiciona à fila para explorar
                }
                // O peso é IGUAL, mas o num de ARESTAS é MENOR.
                else if (newDist == dist[v] && newEdges < numEdges[v]) {
                    numEdges[v] = newEdges; // Atualiza o num de arestas
                    parent[v] = u;          // Atualiza o predecessor
                    // Não precisa adicionar 'v' na fila de novo,
                    // pois sua distância não mudou.
                }
            }
        }

        if (printResults) {
            System.out.println("----------------------------------------");
            System.out.println("--- EFFICACY RESULTS ---");
            if (dist[destination] == Long.MAX_VALUE) {
                System.out.println("No path found from " + source + " to " + destination + ".");
            } else {
                System.out.println("Shortest path from " + source + " to " + destination + ":");
                System.out.println("Total Weight (comprimento): " + dist[destination]);
                System.out.println("Total Edges: " + numEdges[destination]);

                Stack<Integer> path = new Stack<>();
                int current = destination;
                while (current != -1) {
                    path.push(current);
                    if (parent[current] == -1 && current != source) {
                        break;
                    }
                    current = parent[current];
                }

                System.out.print("Path: ");
                while (!path.isEmpty()) {
                    System.out.print(path.pop());
                    if (!path.isEmpty()) {
                        System.out.print(" -> ");
                    }
                }
                System.out.println();
            }
        }
    }
}

class Graph {
    private int n; // num vertices
    private List<List<Edge>> adj; // Lista de sucessores (com pesos)

    public Graph(int n) {
        this.n = n;
        adj = new ArrayList<>(n + 1);

        for (int i = 0; i <= n; i++) {
            adj.add(new ArrayList<>());
        }
    }

    public void addEdge(int o, int d, int w) {
        // Adiciona uma nova aresta (destino, peso) à lista do vértice de origem 'o'
        adj.get(o).add(new Edge(d, w));
    }

    public List<Edge> getSucessors(int v) {
        if (v > n || v < 0) {
            throw new IllegalArgumentException("Vertice invalido: " + v);
        }
        return adj.get(v);
    }

    public int size() {
        return n;
    }

}