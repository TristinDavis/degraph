package de.schauderhaft.degraph.gui;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import de.schauderhaft.degraph.java.JavaHierarchicGraph;
import de.schauderhaft.degraph.model.Node;

/**
 * Controller for the main window which includes 0-n nodeViews
 * 
 */
/*
 * tests I'd consider writing:
 * 
 * 
 * @FXML does some dependency injection. Does it work? are all dependencies set,
 * when wired to a matching configuration file?
 * 
 * Is the layout working? are all nodes displayed? How do I test a node for
 * being displayed? Do they meet the requirement of not overlapping? Most of
 * this stuff does not belong in the controller, but in a 'Layouter'
 */
public class MainViewController extends ScrollPane {
	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private ScrollPane scrollPane;

	private final Map<String, Object> node4Controller = new HashMap<>();

	private final NodeLabelConverter converter = new NodeLabelConverter();

	private final JavaHierarchicGraph graph;

	public MainViewController(JavaHierarchicGraph graph) {
		// there is a lot of stuff in the constructor ... especially the loading
		// of the configuration file is a rather strong dependenncy
		// maybe this shoudl go into a separate class
		this.graph = graph;
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
				"MainView.fxml"));
		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);

		try {
			fxmlLoader.load();
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}
	}

	// drop this method, it is just debugging code
	@FXML
	void onMouseClicked(MouseEvent event) {
		System.out.println("KLickMainView");
	}

	@FXML
	void initialize() {
		// lose null checks, write a test instead. If this breaks nobody will
		// check anything, it's just broken
		assert scrollPane != null : "fx:id=\"mainView\" was not injected: check your FXML file 'MainView.fxml'.";

		Set<Node> topNodes = graph.topNodes();
		// lose null checks
		assert topNodes != null : "no data";

		// weak method name, a void method with side effects is hard to test.
		// Make this a function and move it in a separate class.
		organizeNodes(topNodes);

	}

	private void organizeNodes(Set<Node> topNodes) {

		// TODO: make dynamic linebreak depends an sum of node
		final int LINEBREAK = 800;
		final int NODESPACE = 200;
		int placeX = 0;
		int placeY = 30;
		AnchorPane pane = new AnchorPane();

		for (Node node : topNodes) {

			NodeController nodeController = createController(placeX, placeY,
					node);

			addControllerToPane(pane, nodeController);

			placeX += NODESPACE;
			if (placeX > LINEBREAK) {
				placeX = 0;
				placeY = +NODESPACE;
			}

			organizeDependencies(node);

			node4Controller.put(converter.getNodeName(node), nodeController);
		}
		addNodesPaneToScrollPane(pane);
	}

	private void organizeDependencies(Node node) {
		// TODO: have to be implemented
	}

	private void addNodesPaneToScrollPane(AnchorPane pane) {
		scrollPane.setContent(pane);
	}

	private void addControllerToPane(AnchorPane pane,
			NodeController nodeController) {
		pane.getChildren().add(nodeController);
	}

	private NodeController createController(int placeX, int placeY, Node node) {
		NodeController nodeController = new NodeController(node);
		nodeController.setLayoutX(placeX);
		nodeController.setLayoutY(placeY);
		return nodeController;
	}
}
