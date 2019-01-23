package it.polito.tdp.poweroutages.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.poweroutages.db.PowerOutagesDAO;

public class Model {
	
	private PowerOutagesDAO dao;
	private List<Nerc> nerc;
	private Graph< Nerc ,DefaultWeightedEdge> graph = null;
	private List<Nerc> first;
	private List<Nerc> second;

	

	public Model() {
		this.dao = new PowerOutagesDAO();
		nerc = this.dao.loadAllNercs();
		first = this.dao.loadAllNercsFirst();
	
	}
	public List<Nerc> getAllNerc(){
		return nerc;
	}
	public List<Nerc> getAllFirstNerc(){
		return first;
	}

	public void creaGrafo() {
		graph = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		Graphs.addAllVertices(graph, nerc);
		for(Nerc n : this.first) {
			second = this.dao.loadAllNercsSecond(n);
			for(Nerc nr : second) {
				if(n!=null && nr!=null) {
					Graphs.addEdge(graph, n, nr, (this.dao.getWeight(n, nr).size()-this.dao.getWeightDifferent(n, nr).size()));
				}
					
			}
		}
		System.out.println("Grafo creato!");
		System.out.println("# Vertici: " + graph.vertexSet().size());
		System.out.println("# Archi: " + graph.edgeSet().size());
		for(DefaultWeightedEdge ed : this.graph.edgeSet()) {
			System.out.println("# Archi: " + ed + " " + graph.getEdgeWeight(ed) + "\n");
		}
		
	}
	public List<NeighbourVertex> getNeighbour(Nerc nerc) {
		List<Nerc> neighbour = new ArrayList<>();
		List<NeighbourVertex> neigh = new ArrayList<>();
		int peso = 0;
		neighbour.addAll(Graphs.neighborListOf(graph, nerc));
		
		for(Nerc n : neighbour) {
			peso = (int) graph.getEdgeWeight(graph.getEdge(nerc, n));
			neigh.add(new NeighbourVertex(n, peso));
		}
		Collections.sort(neigh);
		return neigh;
	}
	

}
