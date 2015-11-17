package ch.fhnw.sna.twitter;

import ch.fhnw.sna.twitter.model.HumanTwitterUser;
import ch.fhnw.sna.twitter.model.NewsportalGraph;
import ch.fhnw.sna.twitter.model.NewsportalTwitterUser;
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
import java.time.LocalDate;
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
    private final String TYPE_HUMAN = "Human";
    private final String TYPE_NEWSPORTAL = "Newsportal";
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

    protected String outputFile;

    public NewsportalGephiExport(String outputFile) {
        this.OUTPUT_FILE = outputFile;
    }

    //protected String file;

    //public NewsportalGephiExport(String file) {
    //    this.file = file;
    //}

    public void export(NewsportalGraph newsGraph) {
        Gexf gexf = initializeGexf();
        Graph graph = gexf.getGraph();
        LOG.info("Creating gephi Graph");
        createGraph(graph, newsGraph);
        writeGraphToFile(gexf);
        LOG.info("Finished Gephi export");
    }

    //public void export(NewsportalGraph graph) {
    //}

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
        Map<String, Node> nodeMap = createNodes(newsGraph, graph);
        LOG.info("Creating edges");
        createEdges(newsGraph, graph, nodeMap);
    }


    private Map<String, Node> createNodes(NewsportalGraph users, Graph graph) {
        Map<String, Node> nodeMap = new HashMap<>();

        for (String screenName : users.getHumans().keySet()) {
            if (!nodeMap.containsKey(screenName) && users.getAssociations().get(screenName).size() >= MIN_NEWSPORTAL_FOLLOWINGS) {
                nodeMap.put(screenName, createSingleNode(graph, users.getHumans().get(screenName)));
            }
        }
        for (String screenName : users.getNewsportals().keySet()) {
            if (!nodeMap.containsKey(screenName)) {
                nodeMap.put(screenName, createSingleNode(graph, users.getNewsportals().get(screenName)));
            }
        }

        return nodeMap;
    }

    private Node createSingleNode(Graph graph, TwitterUser user) {
        Node node = graph.createNode(user.getScreenName()).setLabel(user.getScreenName());
        node.getAttributeValues().addValue(attFollowersCount, String.valueOf(user.getFollowersCount()));
        //if (user.getFollowersCount() != 0) {
        //    node.getAttributeValues().addValue(attFollowersCount,
        //            String.valueOf(user.getFollowersCout()));
        //}
        node.getAttributeValues().addValue(attFollowingsCount, String.valueOf(user.getFollowingsCount()));
        node.getAttributeValues().addValue(attTweetsCount, String.valueOf(user.getTweetsCount()));
        node.getAttributeValues().addValue(attScreenName, user.getScreenName());
        //node.getAttributeValues().addValue(attName, user.getName());
        if (user.getName().length() != 0) {
            node.getAttributeValues().addValue(attName,
                    String.valueOf(user.getName()));
        }
        //node.getAttributeValues().addValue(attLocation, user.getLocation());
        if (user.getLocation().length() != 0) {
            node.getAttributeValues().addValue(attLocation,
                    String.valueOf(user.getLocation()));
        }
        //node.getAttributeValues().addValue(attLang, user.getLang());
        if (user.getLang().length() != 0) {
            node.getAttributeValues().addValue(attLang,
                    String.valueOf(user.getLang()));
        }

        if (user instanceof HumanTwitterUser){
            node.getAttributeValues().addValue(attType, TYPE_HUMAN);
        }

        if (user instanceof NewsportalTwitterUser){
            node.getAttributeValues().addValue(attType, TYPE_NEWSPORTAL);
        }

        return node;
    }

    private void createEdges(NewsportalGraph newsportalGraph, Graph graph,
                             Map<String, Node> nodeMap) {
        for (Map.Entry<String, Set<String>> from : newsportalGraph.getAssociations()
                .entrySet()) {
            for (String to : from.getValue()) {
                createOrUpdateEdge(graph, nodeMap, from.getKey(), to);
            }
        }
    }

    private void createOrUpdateEdge(Graph graph, Map<String, Node> nodeMap,
                                    String from, String to) {
        Node source = nodeMap.get(from);
        Node target = nodeMap.get(to);
        if (source == null || target == null) return;
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
                .setCreator("SNA Newsportal Graph");
        gexf.setVisualization(true);

        Graph graph = gexf.getGraph();
        graph.setDefaultEdgeType(EdgeType.DIRECTED);
        graph.setMode(Mode.STATIC);
        graph.getAttributeLists().add(attrList);
        return gexf;
    }
}
