package ch.fhnw.sna.twitter;

import ch.fhnw.sna.twitter.model.NewsportalGraph;
import ch.fhnw.sna.twitter.model.TwitterUser;

import it.uniroma1.dis.wsngroup.gexf4j.core.Edge;
import it.uniroma1.dis.wsngroup.gexf4j.core.EdgeType;
import it.uniroma1.dis.wsngroup.gexf4j.core.Gexf;
import it.uniroma1.dis.wsngroup.gexf4j.core.Graph;
import it.uniroma1.dis.wsngroup.gexf4j.core.Mode;
import it.uniroma1.dis.wsngroup.gexf4j.core.Node;
import it.uniroma1.dis.wsngroup.gexf4j.core.data.Attribute;
import it.uniroma1.dis.wsngroup.gexf4j.core.data.AttributeClass;
import it.uniroma1.dis.wsngroup.gexf4j.core.data.AttributeList;
import it.uniroma1.dis.wsngroup.gexf4j.core.data.AttributeType;
import it.uniroma1.dis.wsngroup.gexf4j.core.impl.GexfImpl;
import it.uniroma1.dis.wsngroup.gexf4j.core.impl.StaxGraphWriter;
import it.uniroma1.dis.wsngroup.gexf4j.core.impl.data.AttributeListImpl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Exports the newsportal graph to Gephi
 *
 */
public class NewsportalGephiExport {
    private static final Logger LOG = LoggerFactory.getLogger(NewsportalGephiExport.class);
    private static final String OUTPUT_FOLDER = "output/";
    private final String OUTPUT_FILE;
    private final int MIN_NEWSPORTAL_FOLLOWINGS = 2;

    // Node attributes
    AttributeList attrList = new AttributeListImpl(AttributeClass.NODE);
    Attribute attFollowersCount = attrList.createAttribute(AttributeType.INTEGER, "followersCount");
    Attribute attFollowingsCount = attrList.createAttribute(AttributeType.INTEGER, "followingsCount");
    Attribute attTweetsCount = attrList.createAttribute(AttributeType.INTEGER, "tweetsCount");
    Attribute attDescription = attrList.createAttribute(AttributeType.STRING, "description");
    Attribute attScreenName = attrList.createAttribute(AttributeType.STRING, "screenName");
    Attribute attName = attrList.createAttribute(AttributeType.STRING, "name");
    Attribute attLocation = attrList.createAttribute(AttributeType.STRING, "location");
    Attribute attLang = attrList.createAttribute(AttributeType.STRING, "lang");
    Attribute attType = attrList.createAttribute(AttributeType.STRING, "type");

    public NewsportalGephiExport(String outputFile) {
        this.OUTPUT_FILE = outputFile;
    }

    public void export(NewsportalGraph newsGraph) {
        Gexf gexf = initializeGexf();
        Graph graph = gexf.getGraph();
        LOG.info("Creating gephi Graph");
        createGraph(graph, newsGraph);
        writeGraphToFile(gexf);
        LOG.info("Finished Gephi export");
    }

    private void writeGraphToFile(Gexf gexf) {
        StaxGraphWriter graphWriter = new StaxGraphWriter();
        File f = new File(OUTPUT_FOLDER + OUTPUT_FILE);
        Writer out;
        try {
            out = new FileWriter(f, false);
            graphWriter.writeToStream(gexf, out, "UTF-8");
            LOG.info("Stored graph in file: " +f.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createGraph(Graph graph, NewsportalGraph newsGraph) {
        LOG.info("Creating nodes");
        Map<Long, Node> nodeMap = createNodes(newsGraph, graph);
        LOG.info("Creating edges");
        createEdges(newsGraph, graph, nodeMap);
    }


    private Map<Long, Node> createNodes(NewsportalGraph users, Graph graph) {
        Map<Long, Node> nodeMap = new HashMap<>();

        for (Long id : users.getHumans().keySet()) {
            if (!nodeMap.containsKey(id) && users.getAssociations().get(id).size() >= MIN_NEWSPORTAL_FOLLOWINGS) {
                nodeMap.put(id, createSingleNode(graph, users.getHumans().get(id)));
            }
        }
        for (Long id : users.getNewsportals().keySet()) {
            if (!nodeMap.containsKey(id)) {
                nodeMap.put(id, createSingleNode(graph, users.getNewsportals().get(id)));
            }
        }

        return nodeMap;
    }

    private Node createSingleNode(Graph graph, TwitterUser user) {
        Node node = graph.createNode(user.getScreenName()).setLabel(user.getScreenName());

        node.getAttributeValues().addValue(attFollowersCount, String.valueOf(user.getFollowersCount()));
        node.getAttributeValues().addValue(attFollowingsCount, String.valueOf(user.getFollowingsCount()));
        node.getAttributeValues().addValue(attTweetsCount, String.valueOf(user.getTweetsCount()));
        node.getAttributeValues().addValue(attScreenName, user.getScreenName());
        node.getAttributeValues().addValue(attType, user.getType());

        if (user.getName().length() != 0) {
            node.getAttributeValues().addValue(attName,
                    String.valueOf(user.getName()));
        }
        if (user.getLocation().length() != 0) {
            node.getAttributeValues().addValue(attLocation,
                    String.valueOf(user.getLocation()));
        }
        if (user.getLang().length() != 0) {
            node.getAttributeValues().addValue(attLang,
                    String.valueOf(user.getLang()));
        }

        return node;
    }

    private void createEdges(NewsportalGraph newsportalGraph, Graph graph, Map<Long, Node> nodeMap) {
        for (Map.Entry<Long, Set<Long>> from : newsportalGraph.getAssociations().entrySet()) {
            for (Long to : from.getValue()) {
                createOrUpdateEdge(graph, nodeMap, from.getKey(), to);
            }
        }
    }

    private void createOrUpdateEdge(Graph graph, Map<Long, Node> nodeMap, Long from, Long to) {
        Node source = nodeMap.get(from);
        Node target = nodeMap.get(to);
        if (source == null || target == null) return;

        source.connectTo(target).setWeight(1f);
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
        Graph graph = gexf.getGraph();

        gexf.getMetadata().setLastModified(Calendar.getInstance().getTime()).setCreator("SNA Newsportal Graph");
        gexf.setVisualization(true);

        graph.setDefaultEdgeType(EdgeType.DIRECTED);
        graph.setMode(Mode.STATIC);
        graph.getAttributeLists().add(attrList);

        return gexf;
    }
}
