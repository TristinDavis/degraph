package de.schauderhaft.degraph.gui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import de.schauderhaft.degraph.model.Node;

public class OrganizeNodesTest {

	private OrganizeNodes underTest = null;
	private final int nodeSize = 200;
	private final int lineBreak = 800;

	@Before
	public void init() {
		underTest = new OrganizeNodes(nodeSize, lineBreak);
	}

	@Test
	public void getNotNull() {
		assertNotNull(underTest.getOrganizedNodes(null));
	}

	@Test
	public void shouldGetEmptyCollection() {
		Set<VisualizeNode> organizedNodes = underTest.getOrganizedNodes(null);
		assertTrue(organizedNodes.isEmpty());
	}

	@Test
	public void shouldBeSameSize() {
		Set<Node> nodes = getSampleData();
		Set<VisualizeNode> vNodes = underTest.getOrganizedNodes(nodes);
		assertEquals(nodes.size(), vNodes.size());
	}

	@Test
	public void shouldIncludeNode() {
		Set<Node> nodes = getSampleData();
		Set<VisualizeNode> vNodes = underTest.getOrganizedNodes(nodes);
		for (VisualizeNode vNode : vNodes) {
			assertTrue(nodes.contains(vNode.getNode()));
		}

	}

	public void vNodeShouldHavePosition() {

	}

	private Set<Node> getSampleData() {
		return SampleGraph.getChessgraph().topNodes();
	}
}
