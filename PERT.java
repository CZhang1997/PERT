package cxz173430;

/** Driver code for PERT algorithm (Project 4)
 * @author 		Churong Zhang 
 * 				cxz173430
 * 				October 20 2018
 * 				Dr. Raghavachari
 * 				This class is for Project 4
 * 				PERT
 */

import cxz173430.Graph;
import cxz173430.Graph.Vertex;
import cxz173430.PERT.PERTVertex.colors;
import cxz173430.Graph.Edge;
import cxz173430.Graph.GraphAlgorithm;
import cxz173430.Graph.Factory;


import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Scanner;

public class PERT extends GraphAlgorithm<PERT.PERTVertex> {
	LinkedList<Vertex> finishList;
	int topNum;
	int time;
	boolean acyclic;
	int criticalPath;
	int numCritical;
	public static class PERTVertex implements Factory {
		enum colors{
			white,
			gray,
			black
		}
		// Add fields to represent attributes of vertices here
		colors color;
		int dis;
		int fin;
		int top;
		Vertex parent;
		int duration;
		int ec;
		int lc;
		int slack;
		
		public PERTVertex(Vertex u) {
		}
		public PERTVertex make(Vertex u) { return new PERTVertex(u); }
	}

	// Constructor for PERT is private. Create PERT instances with static method pert().

	private PERT(Graph g) {
		super(g, new PERTVertex(null));
	}
	/**
	 * set the duration of the vertex to d
	 * @param u	the vertex
	 * @param d	the duration
	 */
	public void setDuration(Vertex u, int d) {
		get(u).duration = d;
		
	}

	// Implement the PERT algorithm. Returns false if the graph g is not a DAG.
	public boolean pert() throws Exception {
		try
		{
			topologicalOrder(); // do topologicalOrder to change the finishList
		}
		catch(Exception e)
		{
			System.out.println(e);	
			return false;	// false if it topologicalOrder throw exceptions
		}
		for(Vertex u: g)
		{
			get(u).ec = get(u).duration;	// assign all vertex with their duration
		}
		for(Vertex u : finishList)
		{
			for(Edge e : g.outEdges(u))
			{
				Vertex v = e.to; // go to every vertex from small to big, fix their currect ec time
				if(get(v).ec < get(u).ec + get(v).duration) {
					get(v).ec = get(u).ec + get(v).duration;
				}
			}
		}
		criticalPath = 0;	// assign criticalPath to 0
		for(Vertex u: g)
		{
			if(get(u).ec > criticalPath)
				criticalPath = get(u).ec; 	// find the largest ec = criticalPath
		}
		for(Vertex u: g)
		{
			get(u).lc = criticalPath;		// set all vertex to the criticalPath
		}
		
		Iterator<Vertex> ite = finishList.descendingIterator();
		while(ite.hasNext())	// loop in descending order
		{
			Vertex v = ite.next();
			for(Edge e: g.inEdges(v))
			{
				Vertex u = e.from;		// fix the lc by doing lc - their duration, first time lc = criticalPath
				if(get(u).lc > get(v).lc - get(v).duration)
					get(u).lc = get(v).lc - get(v).duration;
			}
			get(v).slack = get(v).lc - get(v).ec;	// change the slack after found lc and ec 
		}
		numCritical = 0;
		for(Vertex u: finishList)
		{
			if(critical(u))		// check number of vertex that is critical
				numCritical++;	// add one
		}
			return true;
	}

	// Find a topological order of g using DFS
	LinkedList<Vertex> topologicalOrder() throws Exception {
		if(!g.directed){	// if g is not directed
			throw new Exception("Graph is not directed");
		}
		acyclic = true;	// set acyclic
		topNum = g.size(); // topNum = the size of g
		dfs();			// call dfs() 
		if(acyclic)
			return finishList;
		else
			throw new Exception("Graph is not acyclic");
	}
	/**
	 * helper function to topologicalOrder()
	 * set all vertex to white and null parents
	 * then visit all vertex
	 */
	void dfs()
	{
		time = 0;	// set time to 0
		finishList = new LinkedList<Vertex>();
		// initialize 
		for(Vertex u: g)
		{	
			get(u).color = colors.white;	// change all vertex to white
			get(u).parent = null;			// set their parent to null
			
		}
		
		for(Vertex u: g)
		{	// check all vertex, if the vertex is white then visit it
			if(get(u).color == colors.white)
			{
				dfsVisit(u);	// visit it
			}
			
		}
	}
	/**
	 * visit the input vertex
	 * @param u the vertex to visit
	 */
	void dfsVisit(Vertex u) {
		get(u).color = colors.gray;	// set the color to gray
		get(u).dis = ++time;		// increase the time then set it to dis
		
		for(Edge e: g.outEdges(u))
		{
			Vertex v = e.to;
			if(get(v).color == colors.white)	// if the outEdges of u is white
			{							// then make u the parent of v
				get(v).parent = u;
				dfsVisit(v);			// visit v 
			}
			else if(get(v).color == colors.gray)
			{				// if v is gray, that mean acyclic is false
				acyclic = false;
			}
			
		}
		get(u).fin = ++time;	// set the finish time of this vertex is time + 1
		get(u).color = colors.black;	// change the color to black
		get(u).top = topNum;		// set the top to topNum
		topNum--;			// topNum - 1 to show one vertex is done
		finishList.addFirst(u);	// add this vertex to finishList
	}

	// The following methods are called after calling pert().

	/**
	 * Earliest time at which task u can be completed
	 * @param u the input vertex
	 * @return ec of u
	 */
	public int ec(Vertex u) {
		return get(u).ec;
	}

	/**
	 * Latest completion time of u
	 * @param u the input vertex
	 * @return lc of u
	 */
	public int lc(Vertex u) {
		return get(u).lc;
	}

	/**
	 * get the slack of u
	 * @param u the input vertex
	 * @return slack of u
	 */
	public int slack(Vertex u) {
		return get(u).slack;
	}

	/**
	 * Length of a critical path (time taken to complete project)
	 * @return the critical path length
	 */
	public int criticalPath() {
		return criticalPath;
	}

	/**
	 * Is u a critical vertex?
	 * @param u the input vertex
	 * @return	true if the vertex is critical
	 */
	public boolean critical(Vertex u) {
		return get(u).lc == get(u).ec;
	}

	/**
	 * Number of critical vertices of g
	 * @return Number of critical vertices of g
	 */
	public int numCritical() {
		return numCritical;
	}

	/* Create a PERT instance on g, runs the algorithm.
	 * Returns PERT instance if successful. Returns null if G is not a DAG.
	 */
	public static PERT pert(Graph g, int[] duration) throws Exception{
		PERT p = new PERT(g);
		for(Vertex u: g) {
			p.setDuration(u, duration[u.getIndex()]);
		}
		// Run PERT algorithm.  Returns false if g is not a DAG
		if(p.pert()) {
			return p;
		} else {
			return null;
		}
	}

	public static void main(String[] args) throws Exception {
		boolean details = false;
		String graph = "10 13   1 2 1   2 4 1   2 5 1   3 5 1   3 6 1   4 7 1   5 7 1   5 8 1   6 8 1   6 9 1   7 10 1   8 10 1   9 10 1      0 3 2 3 2 1 3 2 4 1";
		//String graph = "10 14	1 2 1	1 3 1	2 4 1	2 5 1	3 5 1	3 6 1	4 7 1	5 7 1	5 8 1	6 8 1	6 9 1	7 10 1	8 10 1	9 10 1		2 3 2 3 2 1 3 2 4 1";
		Scanner in;
		// If there is a command line argument, use it as file from which
		// input is read, otherwise use input from string.
		in = args.length > 0 ? new Scanner(new java.io.File(args[0])) : new Scanner(graph);
		//in = new Scanner(new File("p4-1.txt"));
		if(args.length > 1) { details = true; }
		Graph g = Graph.readDirectedGraph(in);
		int[] duration = new int[g.size()];
		for(int i=0; i<g.size(); i++) {
		    duration[i] = in.nextInt();
		}
		PERT p = PERT.pert(g, duration);
		if(p == null) {
		    System.out.println("Invalid graph: not a DAG");
		} else {
		    System.out.println(p.criticalPath() + " " + p.numCritical());
		    if(g.size() <= 20 || details) {
			System.out.println("u\tDur\tEC\tLC\tSlack\tCritical");
			for(Graph.Vertex u: g) {
			    System.out.println(u + "\t" + duration[u.getIndex()] + "\t" + p.ec(u) + "\t" + p.lc(u) + "\t" + p.slack(u) + "\t" + p.critical(u));
			}
		    }
		}
	    }
}
