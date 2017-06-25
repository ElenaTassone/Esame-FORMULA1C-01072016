package it.polito.tdp.formulaone.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.formulaone.db.FormulaOneDAO;

public class Model {
	
	private FormulaOneDAO dao ;
	private List<Constructor> constructors;
	private SimpleWeightedGraph<Driverr, DefaultWeightedEdge> grafo ;
	private List<Driverr> used ;

	
	public Model(){
		this.dao = new FormulaOneDAO () ;
	}
	public List<Constructor> getConstructors() {
		if(this.constructors == null)
			this.constructors = dao.getAllConstructors() ;
		return this.constructors;
	}
	public Driverr getMax(Constructor c) {
		
		this.getGrafo(c);
		int max = 0 ;
		Driverr best = null;
		for(Driverr d : grafo.vertexSet()){
			Set<DefaultWeightedEdge> archi = grafo.edgesOf(d);
			int tot = 0 ;
			for(DefaultWeightedEdge a : archi){
				tot+=grafo.getEdgeWeight(a);
			}
			if(tot>max){
				max = tot ;
				best = d;
			}
		}
		return best;
	}
	private void getGrafo(Constructor c) {
		List<Driverr> drivers = dao.getDriversCon(c);
		grafo = new SimpleWeightedGraph<Driverr, DefaultWeightedEdge> (DefaultWeightedEdge.class) ;
		Graphs.addAllVertices(grafo, drivers) ;
		for(Driverr d1 : drivers){
			for(Driverr d2 : drivers){
				if(!d1.equals(d2)){
					int gare = dao.getGareInsieme(d1, d2);
					if(gare>0){
						Graphs.addEdge(grafo, d1, d2, gare);
//						DefaultWeightedEdge arco = grafo.addEdge(d1, d2);
//						grafo.setEdgeWeight(arco, gare);
					}
				}
			}
		}
		
	}
	public List<Driverr> getDreamTeam(int k, Constructor c) {
//		if(this.grafo == null)
			this.getGrafo(c);
		// se non funziona metterle fuori
		List<Driverr> best = new ArrayList<Driverr> () ;
		List<Driverr> parziale = new ArrayList<Driverr> () ;
		used =  new ArrayList<Driverr> () ;
		this.recursive(best, parziale, k);
		return best;
	}
	private void recursive(List<Driverr> best, List<Driverr> parziale, int k) {
		//cond terminazione
		if(parziale.size()==k){
			//se è la prima volta
			if(best.size()==0){
				best.addAll(parziale);
				return ;
			}
			if(this.valore(parziale)>this.valore(best)){
				best.clear();
				best.addAll(parziale);
				return ;
 			}
		}
		
		
		for(Driverr d :grafo.vertexSet()){
			if(!used.contains(d)){
				parziale.add(d);
				used.add(d);
				this.recursive(best, parziale, k);
				parziale.remove(d);
			}
		}
		
	}
	private int valore(List<Driverr> list) {
		List<Integer> gare = dao.getGareIns(list.get(0), list.get(1));
		for(Driverr d : list){
			if(!d.equals(list.get(0)) && !d.equals(list.get(1))){
				for(Driverr d1 : list){
					if(!d1.equals(list.get(0)) && !d1.equals(list.get(1))){
						List<Integer> gare2 = dao.getGareIns(list.get(0), list.get(1));
						for(Integer i : gare){
							if(!gare2.contains(i)){
								gare.remove(i);
							}
						}
					}
				}
			}
		}
		return gare.size();
	}


}
