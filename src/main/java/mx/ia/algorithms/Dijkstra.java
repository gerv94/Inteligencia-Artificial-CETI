package mx.ia.algorithms;

import java.util.HashMap;
import java.util.Map;

import mx.ia.graph.Edge;
import mx.ia.graph.Graph;
import mx.ia.graph.Vertex;

public class Dijkstra {
	private Graph graphG;
	private Graph graphT = new Graph();
	private Map<String, DijkstraElement> dijkstraElements = new HashMap<String, DijkstraElement>();
//	private static LogPrinter log = new LogPrinter("src/main/resources/log_Dijkstra.txt");

	public Dijkstra(Graph g) {
		System.out.println("Dijkstra Controller - Started");
		graphG = g;
		System.out.println("G: {\n" + g.toString() + "\n}\n" + " Vertices: " + graphG.getV().size() + "\n" + " Edges: "
				+ graphG.getE().size());
	}

	@SuppressWarnings("unlikely-arg-type")
	public Graph resolve(String from, String to) {
		boolean fail = false;
		NewString newFrom = new NewString();
		NewString newTo = new NewString();
		newFrom.str = from;
		newTo.str = to;
		System.out.println("Resolve: from " + newFrom + " to " + newTo);
		DijkstraElement element = null;

		if (!graphG.getV().contains(newFrom)) {
			System.err.println("Can not find the vertex: " + newFrom);
			return null;
		}
		if (!graphG.getV().contains(newTo)) {
			System.err.println("Can not find the vertex: " + newTo);
			return null;
		}

		for (Vertex s : graphG.getV())
			dijkstraElements.put(s.getCode(), new DijkstraElement(s));

		for (Edge e : graphG.getE())
			dijkstraElements.get(e.getFrom()).neighbors.put(e.getTo(), e.getDistance());
		dijkstraElements.get(from).setWeight(0.0, dijkstraElements.get(from));

		while (!dijkstraElements.get(to).isPermanent()) {
			element = getMin();
			if (element == null) {
				fail = true;
				break;
			}
			element.setPermanent();
			for (Map.Entry<String, Double> neighbour : element.neighbors.entrySet())
				dijkstraElements.get(neighbour.getKey()).setWeight(neighbour.getValue() + element.getWeight(), element);
			System.out.println(dijkstraElements.toString());
		}
		if (fail) {
			System.out.println("Failed to resolve");
			return null;
		}
		System.out.println(element.printTrace());
		do {
			graphT.getV().add(element.vertex);
			graphT.getE().add(new Edge(element.previous.vertex.getCode(), element.vertex.getCode(),
					element.getWeight() - element.previous.getWeight()));
			element = element.previous;
		} while (element.previous != element);
		graphT.getV().add(element.vertex);

		System.out.println(graphT.toString());
//		System.out.save();
		return graphT;
	}

	private DijkstraElement getMin() {
		DijkstraElement minDijkstraElement = new DijkstraElement(null);
		for (Map.Entry<String, DijkstraElement> dijkstraElement : dijkstraElements.entrySet())
			if (dijkstraElement.getValue().getWeight() < minDijkstraElement.getWeight()
					&& !dijkstraElement.getValue().isPermanent())
				minDijkstraElement = dijkstraElement.getValue();
		return minDijkstraElement.vertex != null ? minDijkstraElement : null;
	}
}

//------------------------------------------------------------------------------------------------------------------------------------
class DijkstraElement {
	Vertex vertex;
	private double weight = Double.POSITIVE_INFINITY;
	DijkstraElement previous = null;
	private boolean permanent = false;
	Map<String, Double> neighbors = new HashMap<String, Double>();

	DijkstraElement(Vertex vertex) {
		this.vertex = vertex;
	}

	void setWeight(double w, DijkstraElement prev) {
		if (this.weight > w) {
			this.weight = w;
			this.previous = prev;
		}
	}

	void setPermanent() {
		permanent = true;
	}

	boolean isPermanent() {
		return permanent;
	}

	double getWeight() {
		return weight;
	}

	public String toString() {
		String res = permanent ? "*" : "-";
		String previosName = previous != null ? previous.vertex.getCode() : "-";
		return res + vertex + "(" + weight + ", " + previosName + ")";
	}

	String printTrace() {
		String res = this != this.previous ? previous.printTrace() + " > " : "";
		return res + vertex.getCode() + "(" + weight + ", " + previous.vertex.getCode() + ")";
	}
}

class NewString {
	public String str;

	@Override
	public boolean equals(Object o) {
		return o.equals(str);
	}

	public String toString() {
		return str;
	}
}