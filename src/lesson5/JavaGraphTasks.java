package lesson5;

import kotlin.NotImplementedError;
import lesson5.impl.GraphBuilder;

import java.util.*;

@SuppressWarnings("unused")
public class    JavaGraphTasks {
    /**
     * Эйлеров цикл.
     * Средняя
     *
     * Дан граф (получатель). Найти по нему любой Эйлеров цикл.
     * Если в графе нет Эйлеровых циклов, вернуть пустой список.
     * Соседние дуги в списке-результате должны быть инцидентны друг другу,
     * а первая дуга в списке инцидентна последней.
     * Длина списка, если он не пуст, должна быть равна количеству дуг в графе.
     * Веса дуг никак не учитываются.
     *
     * Пример:
     *
     *      G -- H
     *      |    |
     * A -- B -- C -- D
     * |    |    |    |
     * E    F -- I    |
     * |              |
     * J ------------ K
     *
     * Вариант ответа: A, E, J, K, D, C, H, G, B, C, I, F, B, A
     *
     * Справка: Эйлеров цикл -- это цикл, проходящий через все рёбра
     * связного графа ровно по одному разу
     *
     *
     * O(n), n - edges
     */
    public static List<Graph.Edge> findEulerLoop(Graph graph)
    {
        ArrayList<Graph.Edge> result = new ArrayList<>(Collections.emptyList());
        Set<Graph.Edge> edges = graph.getEdges();
        int visitedCount = 0;

        if (edges.size() > 2)
        {
            Stack<Graph.Vertex> vStack = new Stack<>();
            Stack<Graph.Edge> eStack = new Stack<>();

            vStack.push(edges.iterator().next().getBegin());
            Map<Graph.Edge, Boolean> visited = new HashMap<>();

            for (Graph.Edge e : edges)
            {
                visited.put(e, false);
            }

            Graph.Vertex v;

            while (!vStack.isEmpty())
            {
                v = vStack.peek();
                Map<Graph.Vertex, Graph.Edge> connections = graph.getConnections(v);

                for (Map.Entry<Graph.Vertex, Graph.Edge> entry : connections.entrySet())
                {
                    Graph.Edge edge = entry.getValue();

                    if (!visited.get(edge))
                    {
                        vStack.push(entry.getKey());
                        eStack.push(edge);
                        visited.put(edge, true);
                        visitedCount++;
                        break;
                    }
                }

                if (v.equals(vStack.peek()))
                {
                    if (!eStack.isEmpty())
                    {
                        Graph.Edge popped = eStack.pop();
                        if (result.isEmpty() || connected(popped, result.get(result.size() - 1)))
                            result.add(popped);
                        else
                            break;
                    }
                    vStack.pop();
                }
            }

            if (visitedCount != edges.size() || !connected(result.get(0), result.get(result.size() - 1)))
            {
                result.clear();
            }
        }
        return result;
    }

    private static boolean connected(Graph.Edge first, Graph.Edge second)
    {
        Graph.Vertex firstBegin = first.getBegin();
        Graph.Vertex firstEnd = first.getEnd();
        Graph.Vertex secondBegin = second.getBegin();
        Graph.Vertex secondEnd = second.getEnd();

        return firstBegin.equals(secondBegin) || firstBegin.equals(secondEnd) ||
                firstEnd.equals(secondEnd) || firstEnd.equals(secondBegin);
    }

    /**
     * Минимальное остовное дерево.
     * Средняя
     *
     * Дан граф (получатель). Найти по нему минимальное остовное дерево.
     * Если есть несколько минимальных остовных деревьев с одинаковым числом дуг,
     * вернуть любое из них. Веса дуг не учитывать.
     *
     * Пример:
     *
     *      G -- H
     *      |    |
     * A -- B -- C -- D
     * |    |    |    |
     * E    F -- I    |
     * |              |
     * J ------------ K
     *
     * Ответ:
     *
     *      G    H
     *      |    |
     * A -- B -- C -- D
     * |    |    |
     * E    F    I
     * |
     * J ------------ K
     *
     *
     * O(n), n - vertices
     */
    public static Graph minimumSpanningTree(Graph graph)
    {
        Set<Graph.Vertex> notVisited = new HashSet<>(graph.getVertices());
        GraphBuilder gb = new GraphBuilder();

        for (Graph.Vertex v: graph.getVertices())
        {
            if (notVisited.isEmpty())
                continue;

            gb.addVertex(v.getName());
            notVisited.remove(v);
            for (Graph.Vertex neighbour: graph.getNeighbors(v))
            {
                if ((notVisited.contains(neighbour) || graph.getConnections(v).size() == 1)
                        && graph.getConnection(v, neighbour) != null && !v.equals(neighbour))
                {
                    gb.addVertex(neighbour.getName());
                    gb.addConnection(v, neighbour, 1);
                    notVisited.remove(neighbour);
                }
            }

        }
        return gb.build();
    }

    /**
     * Максимальное независимое множество вершин в графе без циклов.
     * Сложная
     *
     * Дан граф без циклов (получатель), например
     *
     *      G -- H -- J
     *      |
     * A -- B -- D
     * |         |
     * C -- F    I
     * |
     * E
     *
     * Найти в нём самое большое независимое множество вершин и вернуть его.
     * Никакая пара вершин в независимом множестве не должна быть связана ребром.
     *
     * Если самых больших множеств несколько, приоритет имеет то из них,
     * в котором вершины расположены раньше во множестве this.vertices (начиная с первых).
     *
     * В данном случае ответ (A, E, F, D, G, J)
     *
     * Если на входе граф с циклами, бросить IllegalArgumentException
     *
     * Эта задача может быть зачтена за пятый и шестой урок одновременно
     *
     *
     * Bron–Kerbosch algorithm - O(3^(n/3))
     */
    public static Set<Graph.Vertex> largestIndependentVertexSet(Graph graph)
    {
        for (Graph.Vertex vertex: graph.getVertices())
        {
            if (isCyclic(graph, vertex, new HashSet<>(), null))
                throw new IllegalArgumentException();
        }

        List<Set<Graph.Vertex>> compsubs = new ArrayList<>();
        Set<Graph.Vertex> compsub;
        Set<Graph.Vertex> candidates;

        for (Graph.Vertex v: graph.getVertices())
        {
            compsub = new HashSet<>();
            compsub.add(v);
            candidates = new HashSet<>(graph.getVertices());
            candidates.remove(v);
            candidates.removeAll(graph.getNeighbors(v));

            compsubs.add(extend(graph, compsub, candidates));
        }

        Set<Graph.Vertex> result = new HashSet<>();

        for (Set<Graph.Vertex> comp: compsubs)
        {
            if (comp.size() > result.size())
                result = comp;
        }

        return result;
    }

    private static Set<Graph.Vertex> extend(Graph graph,
                                            Set<Graph.Vertex> compsub,
                                            Set<Graph.Vertex> candidates)
    {
        if (candidates.iterator().hasNext())
        {
            Graph.Vertex v = candidates.iterator().next();
            compsub.add(v);
            Set<Graph.Vertex> newCandidates = formNewSet(graph, candidates, v);

            if (newCandidates.isEmpty())
            {
                return compsub;
            }
            else
                return extend(graph, compsub, newCandidates);
        }
        return compsub;
    }

    private static Set<Graph.Vertex> formNewSet(Graph graph,
                                                Set<Graph.Vertex> set,
                                                Graph.Vertex v)
    {

        Set<Graph.Vertex> newSet = new HashSet<>(set);
        newSet.removeAll(graph.getNeighbors(v));
        newSet.remove(v);

        return newSet;
    }

    private static int passed;

    private static boolean isCyclic(Graph graph,
                               Graph.Vertex start,
                               Set<Graph.Vertex> visited,
                               Graph.Vertex parent)
    {
        visited.add(start);
        Set<Graph.Vertex> neighbours = graph.getNeighbors(start);
        for (Graph.Vertex n: neighbours)
        {
            if (!visited.contains(n) || parent == null)
                return isCyclic(graph, n, visited, start);
            else if (!n.equals(parent))
                return true;
        }
        return false;
    }



    /**
     * Наидлиннейший простой путь.
     * Сложная
     *
     * Дан граф (получатель). Найти в нём простой путь, включающий максимальное количество рёбер.
     * Простым считается путь, вершины в котором не повторяются.
     * Если таких путей несколько, вернуть любой из них.
     *
     * Пример:
     *
     *      G -- H
     *      |    |
     * A -- B -- C -- D
     * |    |    |    |
     * E    F -- I    |
     * |              |
     * J ------------ K
     *
     * Ответ: A, E, J, K, D, C, H, G, B, F, I
     */
    public static Path longestSimplePath(Graph graph) {
        throw new NotImplementedError();
    }
}
