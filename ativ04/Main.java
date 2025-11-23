package ativ04;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/// codigo base em https://www.geeksforgeeks.org/dsa/find-edge-disjoint-paths-two-vertices

// Classe para representar uma aresta de fluxo
class FlowEdge {
    int to;
    int rev; // Índice da aresta reversa na lista de adjacência do destino
    int cap; // Capacidade (será sempre 1 para caminhos disjuntos)
    int flow; // Fluxo atual passando pela aresta

    public FlowEdge(int to, int rev, int cap) {
        this.to = to;
        this.rev = rev;
        this.cap = cap;
        this.flow = 0;
    }
}

// Classe para Fluxo Máximo
class FlowGraph {
    int V;
    List<List<FlowEdge>> adj;

    public FlowGraph(int V) {
        this.V = V;
        adj = new ArrayList<>(V + 1);
        for (int i = 0; i <= V; i++) {
            adj.add(new ArrayList<>());
        }
    }

    // adiciona aresta direcionada com capacidade 1 (para contar caminhos)
    public void addEdge(int u, int v) {
        // aresta "Forward": u -> v (Capacidade 1)
        FlowEdge a = new FlowEdge(v, adj.get(v).size(), 1);
        // aresta "Backward": v -> u (Capacidade 0)
        FlowEdge b = new FlowEdge(u, adj.get(u).size(), 0);

        adj.get(u).add(a);
        adj.get(v).add(b);
    }

    // algoritmo de Edmonds-Karp (BFS para encontrar fluxo máximo)
    // lógica baseada no GeeksforGeeks, mas adaptada para Listas de Adjacência por conta de memória
    public int maxFlow(int s, int t) {
        int flow = 0;
        int[] parent = new int[V + 1];
        int[] edgeIndex = new int[V + 1]; // para saber qual aresta da lista foi usada

        while (true) {
            Arrays.fill(parent, -1);
            Queue<Integer> q = new LinkedList<>();
            q.add(s);
            parent[s] = s; // marca origem como visitada

            while (!q.isEmpty() && parent[t] == -1) {
                int u = q.poll();
                
                // itera sobre as arestas de u
                for (int i = 0; i < adj.get(u).size(); i++) {
                    FlowEdge e = adj.get(u).get(i);
                    // se o destino não foi visitado e há capacidade residual
                    if (parent[e.to] == -1 && e.cap - e.flow > 0) {
                        parent[e.to] = u;
                        edgeIndex[e.to] = i; // guarda índice da aresta usada
                        q.add(e.to);
                    }
                }
            }

            // se não chegamos ao destino, não há mais caminhos aumentantes
            if (parent[t] == -1) break;

            // como a capacidade é sempre 1 para caminhos disjuntos, o fluxo do caminho é 1
            int push = 1; 
            flow += push;

            int curr = t;
            while (curr != s) {
                int prev = parent[curr];
                int idx = edgeIndex[curr];
                
                FlowEdge e = adj.get(prev).get(idx);
                e.flow += push;

                adj.get(curr).get(e.rev).flow -= push;

                curr = prev;
            }
        }
        return flow;
    }

    // metodo extra para recuperar e imprimir os caminhos encontrados
    // usa uma DFS simples seguindo arestas onde flow == 1
    public void printDisjointPaths(int s, int t) {
        int pathCount = 0;
        
        // enquanto tenta achar um caminho de s a t usando arestas com flow=1
        while (true) {
            List<Integer> path = new ArrayList<>();
            if (!dfsFindPath(s, t, path, new boolean[V + 1])) {
                break;
            }
            pathCount++;
            System.out.print("Path " + pathCount + ": ");
            for (int i = 0; i < path.size(); i++) {
                System.out.print(path.get(i) + (i < path.size() - 1 ? " -> " : ""));
            }
            System.out.println();
        }
    }

    // DFS auxiliar para encontrar um caminho único no grafo de fluxo
    private boolean dfsFindPath(int u, int t, List<Integer> path, boolean[] visited) {
        path.add(u);
        if (u == t) return true;
        visited[u] = true;

        for (FlowEdge e : adj.get(u)) {
            // se tem fluxo (foi usada no maxFlow), não é residual (cap > 0) e o destino não foi visitado nesse caminho
            if (e.flow == 1 && e.cap == 1 && !visited[e.to]) {
                if (dfsFindPath(e.to, t, path, visited)) {
                    e.flow = 0; 
                    return true;
                }
            }
        }

        path.remove(path.size() - 1);
        return false;
    }
}

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        int option = 0;
        String fileName = "";

        do {
            System.out.println("\n--- MENU ---");
            System.out.println("1 - Layered graph (1000 vertices)");
            System.out.println("2 - Layered graph (10000 vertices)");
            System.out.println("3 - Layered graph (100000 vertices)");
            System.out.println("4 - Layered graph (500000 vertices)");
            System.out.println("5 - Euler graph (1000 vertices)");
            System.out.println("6 - Euler graph (10000 vertices)");
            System.out.println("7 - Euler graph (100000 vertices)");
            System.out.println("8 - Euler graph (500000 vertices)");
            System.out.println("9 - Custom graph");
            System.out.println("0 - Exit");
            System.out.print("Choose an option: ");
            
            try {
                 String line = sc.nextLine();
                 if(line.isEmpty()) continue;
                 option = Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("Invalid option. Try again.");
                continue;
            }

            switch (option) {
                case 1: fileName = "ativ04/graphs/layered_1000.txt"; break;
                case 2: fileName = "ativ04/graphs/layered_10000.txt"; break;
                case 3: fileName = "ativ04/graphs/layered_100000.txt"; break;
                case 4: fileName = "ativ04/graphs/layered_500000.txt"; break;
                case 5: fileName = "ativ04/graphs/euler_1000.txt"; break;
                case 6: fileName = "ativ04/graphs/euler_10000.txt"; break;
                case 7: fileName = "ativ04/graphs/euler_100000.txt"; break;
                case 8: fileName = "ativ04/graphs/euler_500000.txt"; break;
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

            FlowGraph graph = readGraph(fileName);

            if (graph == null) continue;

            System.out.print("Select a source vertice: ");
            int source = sc.nextInt();
            System.out.print("Select a destination vertice: ");
            int destination = sc.nextInt();
            sc.nextLine(); // Consumir quebra de linha

            System.out.println("\nCalculando caminhos disjuntos...");
            long startTime = System.nanoTime();

            int maxPaths = graph.maxFlow(source, destination);
            
            long endTime = System.nanoTime();
            long totalNanoTime = (endTime - startTime);
            double totalMilliTime = totalNanoTime / 1_000_000.0;

            System.out.println("----------------------------------------");
            System.out.println("--- RESULTADOS ---");
            System.out.println("Total de Caminhos Disjuntos em Arestas: " + maxPaths);

            if (maxPaths > 0) {
                System.out.println("\nListagem dos caminhos:");
                graph.printDisjointPaths(source, destination);
            } else {
                System.out.println("Nenhum caminho encontrado entre " + source + " e " + destination);
            }

            System.out.println("\n--- EFICIÊNCIA ---");
            System.out.println("Vertices no Grafo: " + graph.V);
            System.out.println("Tempo de Execução: " + String.format("%.4f", totalMilliTime) + " ms");
            System.out.println("----------------------------------------\n");

        } while (option != 0);

        sc.close();
    }

    public static FlowGraph readGraph(String fileName) {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String firstLine = reader.readLine();
            if (firstLine == null) return null;
            
            String[] parts = firstLine.trim().split("\\s+");
            int n = Integer.parseInt(parts[0]); // vértices
            int m = Integer.parseInt(parts[1]); // arestas

            FlowGraph graph = new FlowGraph(n);

            for (int i = 0; i < m; i++) {
                String edgeLine = reader.readLine();
                if (edgeLine == null) break;
                if (edgeLine.trim().isEmpty()) { i--; continue; }

                String[] edgeParts = edgeLine.trim().split("\\s+");
                int o = Integer.parseInt(edgeParts[0]); // origem
                int d = Integer.parseInt(edgeParts[1]); // destino

                // Adiciona aresta com capacidade 1 (padrão para caminhos disjuntos)
                graph.addEdge(o, d);
            }
            return graph;

        } catch (IOException e) {
            System.err.println("Erro ao ler arquivo: " + e.getMessage());
            return null;
        } catch (NumberFormatException e) {
            System.err.println("Erro de formato no arquivo: " + e.getMessage());
            return null;
        }
    }
}