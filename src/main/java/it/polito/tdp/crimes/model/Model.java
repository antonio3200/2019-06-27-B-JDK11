package it.polito.tdp.crimes.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.crimes.db.EventsDao;

public class Model {

	private EventsDao dao;
	private List<String> vertici;
	private SimpleWeightedGraph<String,DefaultWeightedEdge> grafo;
	
	public Model() {
		this.dao= new EventsDao();
		this.vertici= new ArrayList<>();
	}
	
	public List<String> getCategorie(){
		return this.dao.getCategorie();
	}
	
	public List<Integer> getMesi(){
		return this.dao.getMese();
	}
	
	public void creaGrafo(String categoria,int mese) {
		this.grafo= new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		this.vertici=this.dao.getVertici(categoria, mese);
		//aggiungo vertici
		Graphs.addAllVertices(this.grafo, this.vertici);
		//aggiungo archi
		List<Arco> archi= this.dao.getArchi(vertici, categoria, mese);
		for(Arco a : archi) {
			Graphs.addEdgeWithVertices(this.grafo, a.getS1(), a.getS2(), a.getPeso());
		}
	}
	
	public int nVertici() {
		return this.grafo.vertexSet().size();
	}
	
	public int nArchi() {
		return this.grafo.edgeSet().size();
	}
	
	public List<String> getVertici(){
		return this.vertici;
	}
	
	public List<Arco> getMigliori(){
		int somma=0;
		double media=0.0;
		List<Arco> result= new ArrayList<>();
		for(DefaultWeightedEdge e : this.grafo.edgeSet()) {
			somma+=this.grafo.getEdgeWeight(e);
		}
		media=somma/this.nArchi();
		for(DefaultWeightedEdge e : this.grafo.edgeSet()) {
			if(this.grafo.getEdgeWeight(e)>media) {
				Arco a = new Arco(this.grafo.getEdgeSource(e),this.grafo.getEdgeTarget(e),(int)this.grafo.getEdgeWeight(e));
				result.add(a);
			}
		}
		Collections.sort(result);
		return result;
	}
	
}
