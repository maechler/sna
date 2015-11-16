package ch.fhnw.sna.twitter;

import ch.fhnw.sna.twitter.model.NewsportalGraph;
import ch.fhnw.sna.twitter.model.HumanTwitterUser;
import ch.fhnw.sna.twitter.model.NewsportalTwitterUser;
import ch.fhnw.sna.twitter.model.TwitterUser;




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



    // Node attributes
    AttributeList attrList = new AttributeListImpl(AttributeClass.NODE);

    Attribute attFollowersCount = attrList.createAttribute(AttributeType.INTEGER, "followersCount");
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
        Map<String, Node> nodeMap = new HashMap<>(
                users.getHumans().size() * 2 + 12);

        for (HumanTwitterUser user : users.getHumans()) {
            if (!nodeMap.containsKey(user.getScreenName())) {
                nodeMap.put(user.getScreenName(), createSingleNode(graph, user));
            }
        }
        for (NewsportalTwitterUser user : users.getNewsportals()) {
            if (!nodeMap.containsKey(user.getScreenName())) {
                nodeMap.put(user.getScreenName(), createSingleNode(graph, user));
            }
        }

        return nodeMap;
    }

    private Node createSingleNode(Graph graph, TwitterUser user) {
        Node node = graph.createNode(user.getScreenName()).setLabel(user.getLabel());
        node.getAttributeValues().addValue(attFollowersCount, user.getFollowersCount());
        //if (user.getFollowersCount() != 0) {
        //    node.getAttributeValues().addValue(attFollowersCount,
        //            String.valueOf(user.getFollowersCout()));
        //}
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

        if (user isinstanceof HumanTwitterUser){
            node.addValue(attType, TYPE_HUMAN);
        }

        if (user isinstanceof NewsportalTwitterUser){
            node.addValue(attType, TYPE_NEWSPORTAL);
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
                .setCreator("SNA Newsportal Graph");
        gexf.setVisualization(true);

        Graph graph = gexf.getGraph();
        graph.setDefaultEdgeType(EdgeType.DIRECTED);
        graph.setMode(Mode.STATIC);
        graph.getAttributeLists().add(attrList);
        return gexf;
    }
}
