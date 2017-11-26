/**
 * Created by Kalvin on 5/21/2016.
 */
import java.util.*;

public class Test {
    public static void main(String[] args) {
        Random r = new Random();
        List<Vertex> vertices = new ArrayList<Vertex>();
        Set<Edge> edges = new HashSet<Edge>();

        Vertex a = new Vertex("A");
        Vertex b = new Vertex("B");
        Vertex c = new Vertex("C");
        Vertex d = new Vertex("D");
        Vertex e = new Vertex("E");
        Vertex f = new Vertex("F");
        Edge ab = new Edge(a, b, 2);
        Edge ad = new Edge(a, d, 1);
        Edge ae = new Edge(a, e, 3);
        Edge bc = new Edge(b, c, 1);
        Edge be = new Edge(b, e, 0);
        Edge cd = new Edge(c, d, 5);
        Edge ef = new Edge(e, f, 10);
        Edge cf = new Edge(c, f, 10);
        vertices.add(a);
        vertices.add(b);
        vertices.add(c);
        vertices.add(d);
        vertices.add(e);
        vertices.add(f);
        edges.add(ab);
        edges.add(ad);
        edges.add(ad);
        edges.add(bc);
        edges.add(be);
        edges.add(cd);
        edges.add(ef);
        edges.add(cf);

        Scanner console = new Scanner(System.in);

        MyGraph g = new MyGraph(vertices, edges);
       /* while (true) {
            System.out.println("Enter from vertex (A - F)");
            Vertex from = new Vertex(console.next());
            System.out.println("Enter to vertex (A - F)");
            Vertex to = new Vertex(console.next());
            Path p = g.shortestPath(from, to);
        }

        */
        Set<Edge> mintree = g.minSpanTree();

    }
}