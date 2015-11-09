package ch.fhnw.sna.examples.wiki;

import it.uniroma1.dis.wsngroup.gexf4j.core.Edge;
import it.uniroma1.dis.wsngroup.gexf4j.core.EdgeType;
import it.uniroma1.dis.wsngroup.gexf4j.core.Gexf;
import it.uniroma1.dis.wsngroup.gexf4j.core.Graph;
import it.uniroma1.dis.wsngroup.gexf4j.core.Mode;
import it.uniroma1.dis.wsngroup.gexf4j.core.Node;
import it.uniroma1.dis.wsngroup.gexf4j.core.impl.GexfImpl;
import it.uniroma1.dis.wsngroup.gexf4j.core.impl.StaxGraphWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Exports
 * 
 * @author Michael Henninger
 */
public class WikiGephiPeopleExport {

	private static final String OUTPUT_FOLDER = "output/";
	private String outputFile;

	public WikiGephiPeopleExport(String outputFile) {
		this.outputFile = outputFile;
	}

	private void writeGraphToFile(Gexf gexf) {
		StaxGraphWriter graphWriter = new StaxGraphWriter();
		File f = new File(OUTPUT_FOLDER + outputFile);
		Writer out;
		try {
			out = new FileWriter(f, false);
			graphWriter.writeToStream(gexf, out, "UTF-8");
			System.out.println("GENERATING GRAPH FILE AT: "
					+ f.getAbsolutePath());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void createGraph(Graph graph,
			Map<String, Set<String>> referencedPeople) {
		Map<String, Node> nodeMap = createNodes(referencedPeople, graph);
		createEdges(referencedPeople, graph, nodeMap);
	}

	private Map<String, Node> createNodes(
			Map<String, Set<String>> referencedPeople, Graph graph) {
		Map<String, Node> nodeMap = new HashMap<>(referencedPeople.size() * 2);
		for (Set<String> people : referencedPeople.values()) {
			for (String person : people) {
				if (!nodeMap.containsKey(person)) {
					nodeMap.put(person,
							graph.createNode(person).setLabel(person));
				}
			}
		}
		return nodeMap;
	}

	private void createEdges(Map<String, Set<String>> referencedPeople,
			Graph graph, Map<String, Node> nodeMap) {
		for (Set<String> group : referencedPeople.values()) {
			String[] groupArray = group.toArray(new String[] {});
			for (int i = 0; i < groupArray.length - 1; i++) {
				for (int j = i + 1; j < groupArray.length; j++) {
					createOrUpdateEdge(graph, nodeMap, groupArray[i],
							groupArray[j]);
				}
			}
		}
	}

	private void createOrUpdateEdge(Graph graph, Map<String, Node> nodeMap,
			String from, String to) {
		Node source = nodeMap.get(from);
		Node target = nodeMap.get(to);
		if (source == null || target == null)
			throw new IllegalStateException(
					"Source and Target must not be null. Every node must be added to network as a node before using it as a edge.");
		if (source.hasEdgeTo(to)) {
			Edge edge = getEdgeBetween(source, target);
			edge.setWeight(edge.getWeight() + 1f);

		} else if (target.hasEdgeTo(from)) {
			Edge edge = getEdgeBetween(target, source);
			edge.setWeight(edge.getWeight() + 1f);

		} else {
			source.connectTo(target).setWeight(1f);
		}

	}

	private Edge getEdgeBetween(Node source, Node target) {
		for (Edge edge : source.getEdges()) {
			if (edge.getTarget().equals(target)) {
				return edge;
			}
		}
		return null;
	}

	private Gexf initializeGexf() {
		Gexf gexf = new GexfImpl();
		gexf.getMetadata().setLastModified(Calendar.getInstance().getTime())
				.setCreator("SNA Wikipedia Graph (FHNW)");
		gexf.setVisualization(true);

		Graph graph = gexf.getGraph();
		graph.setDefaultEdgeType(EdgeType.UNDIRECTED);
		graph.setMode(Mode.STATIC);
		return gexf;
	}

	public void export(Map<String, Set<String>> peoplePerArticle) {
		Gexf gexf = initializeGexf();
		Graph graph = gexf.getGraph();
		createGraph(graph, peoplePerArticle);
		writeGraphToFile(gexf);
	}
}
