import java.util.ArrayList;

public class DyckTriangulation {
	private String dyckWord = ""; //A dyckWord in Parentheses
	private ArrayList<Edge> edgeList;
	
	public static void main(String[] args){
		DyckTriangulation test1 = new DyckTriangulation("((()))");
		System.out.println(test1);
		DyckTriangulation test2 = new DyckTriangulation(test1.edgeList);
		System.out.println(test2);
		System.out.println("Report: \ntest1 == test2: " + (test1.equals(test2)));
	}
	
	public DyckTriangulation(ArrayList<Edge> edgeList){
		ArrayList<Edge> edgeListCopy = new ArrayList<Edge>();
		for (Edge edge:edgeList) edgeListCopy.add(edge);
		this.edgeList = edgeList;
		int min = 0,max = 0;
		Edge base = edgeListCopy.get(0);
		for (Edge edge:edgeListCopy){
			if (min > edge.v2.index) min = edge.v1.index;
			if (max < edge.v1.index) max = edge.v1.index;
			if (edge.v1.index == max && edge.v2.index == min) base = edge;
		}
		Triangle root = (Triangle)treeHelper(edgeListCopy, base, base);
		dyckWord = wordBuilder(root).trim();
	}
	
	public DyckTriangulation(String dyckWord){
		this.dyckWord = dyckWord.trim();
		int numV = dyckWord.length()/2 + 2;
		int indexV = 1;
		edgeList = new ArrayList<Edge>();
		Edge base = new Edge(numV, indexV, null);
		edgeList.add(base);
		Node treeStump = new Node(base);
		Node curr = treeStump;
		for (int i = 0; i < dyckWord.length(); i++){
			if (dyckWord.charAt(i) == '('){
				if (curr.left != null) curr = curr.left;
				if (dyckWord.charAt((i+1)) == ')'){
					curr.left = new Edge(indexV, indexV+1, curr);
					indexV++;
					edgeList.add((Edge)curr.left);
				}
				else curr.left = new Node(curr);
			}
			else if (dyckWord.charAt(i) == ')'){
				while(curr.left == null || curr.right != null) curr = curr.prev;
				if (i == (dyckWord.length()-1) || dyckWord.charAt((i+1)) == ')'){
					curr.right = new Edge(indexV, indexV+1, curr);
					indexV++;
					edgeList.add((Edge)curr.right);
				}
				else curr.right = new Node(curr);
				curr = curr.right;
			}
		}
		Triangle root = new Triangle(base, triangleHelper(((Node)treeStump).left, edgeList),
				triangleHelper(((Node)treeStump).right, edgeList));
	}
	
	public String toString(){
		return dyckWord + " : " + edgeList; 
	}
	
	@Override
	public boolean equals(Object o){
		if (o instanceof DyckTriangulation) return (edgeList.equals(((DyckTriangulation) o).edgeList))&&(dyckWord.equals(((DyckTriangulation) o).dyckWord));
		return false;
	}
	
	/*Private Classes*/
	private String wordBuilder(Node curr){
		if (curr == null) return "";
		return curr.value + wordBuilder(curr.left) + wordBuilder(curr.right);
	}
	
	private Node treeHelper(ArrayList<Edge> edgeList, Edge base, Node parent){
		if (Math.abs(base.v1.index - base.v2.index)==1) return base;
		edgeList.remove(base);
		Vertex v1 = new Vertex(Math.min(base.v1.index, base.v2.index));
		Vertex v2 = new Vertex(Math.max(base.v1.index, base.v2.index));
		ArrayList<Edge> V1 = new ArrayList<Edge>();//Edges radiating from V1
		ArrayList<Edge> V2 = new ArrayList<Edge>();//Edges radiating from V2
		ArrayList<Vertex> e2Candidates = new ArrayList<Vertex>();//Vertices that would form edge E2
		ArrayList<Vertex> e3Candidates = new ArrayList<Vertex>();//Vertices that would form edge E3
		for (Edge edge:edgeList){
			if (edge.contains(v1)){
				V1.add(edge);
				e2Candidates.add((v1.equals(edge.v1))?edge.v2:edge.v1);
			}
			if (edge.contains(v2)){
				V2.add(edge);
				e3Candidates.add((v2.equals(edge.v1))?edge.v2:edge.v1);
			}
		}
		Edge e2=null, e3=null;
		for (Vertex v3:e2Candidates){
			if (e3Candidates.contains(v3)){
				e2 = V1.get(e2Candidates.indexOf(v3));
				e3 = V2.get(e3Candidates.indexOf(v3));
			}
		}
		Triangle temp = new Triangle(base, e2, e3, parent);
		((Node)temp).left = treeHelper(edgeList, e2, temp);
		((Node)temp).left.value = '(';
		((Node)temp).right = treeHelper(edgeList, e3, temp);
		((Node)temp).right.value = ')';
		return temp;
	}
	private Edge triangleHelper(Node curr, ArrayList<Edge> edgeList){
		if (curr instanceof Edge) return (Edge)curr;
		Edge e2 = triangleHelper(curr.left, edgeList);
		Edge e3 = triangleHelper(curr.right, edgeList);
		Triangle temp = new Triangle(e2, e3, curr.prev);
		((Node)temp).left = curr.left;
		((Node)temp).right = curr.right;
		if (curr.equals(curr.prev.left)) curr.prev.left = temp;
		else curr.prev.right = temp;
		edgeList.add((Edge)temp.e1);
		return temp.e1;
	}
	
	private class Node{
		private Node left, right, prev;
		private char value;
		
		public Node(Node prev){
			this.prev = prev;
		}
		
		public Node(Node prev, char value){
			this.prev = prev;
			this.value = value;
		}
	}
	
	private class Triangle extends Node{
		private Vertex v1, v2, v3;
		private Edge e1/*base*/, e2/*Left*/, e3/*Right*/;
		public Triangle(int v1, int v2, int v3, Node prev){
			super(prev);
			this.v1 = new Vertex(v1);
			this.v2 = new Vertex(v2);
			this.v3 = new Vertex(v3);
			e1 = new Edge(v1, v2, this);
			e2 = new Edge(v2, v3, this);
			e3 = new Edge(v3, v1, this);
		}
		
		public Triangle(Edge e1, Edge e2, Edge e3, Node prev){
			super(prev);
			this.e1 = e1;
			this.e2 = e2;
			this.e3 = e3;
			v1 = e1.v1;
			v2 = e2.v1;
			v3 = e3.v1;
		}
		
		public Triangle(Edge e2, Edge e3, Node prev){
			super(prev);
			e1 = new Edge(e2.v1.index, e3.v2.index, (Node) this);
			this.e2 = e2;
			this.e3 = e3;
			v1 = e1.v1;
			v2 = e2.v1;
			v3 = e3.v1;
		}
		
		public String toString(){
			return "Base: " + e1 + " Left Side: " + e2 + "Right Side: " + e3;
		}
	}
	
	private class Edge extends Node{
		private Vertex v1, v2;
		public Edge(int v1, int v2, Node prev){
			super(prev);
			this.v1 = new Vertex(v1);
			this.v2 = new Vertex(v2);
		}
		
		@Override
		public boolean equals(Object o){
			if (o instanceof Edge) return (v1.equals(((Edge)o).v1) && v2.equals(((Edge)o).v2))
					|| (v1.equals(((Edge)o).v2) && v2.equals(((Edge)o).v1));
			return false;
		}
		
		@Override
		public String toString(){
			return "{"+v1+", "+v2+"}";
		}
		
		public boolean contains(Vertex v){
			return v.equals(v1) || v.equals(v2);
		}
	}
	
	private class Vertex{
		private int index;
		public Vertex(int index){
			this.index = index;
		}
		
		@Override
		public boolean equals(Object o){
			if (o instanceof Vertex) return index == ((Vertex)o).index;
			return false;
		}
		
		@Override
		public String toString(){
			return Integer.toString(index);
		}
	}
}
