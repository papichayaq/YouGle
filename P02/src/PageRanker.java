/*
 * Jidapa		Sumanotham		5888043	Sec. 1
 * Papichaya	Quengdaeng		5888146 Sec. 1
 * Intukorn		Limpachaveng	5888261 Sec. 1
 */
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

/**
 * This class implements PageRank algorithm on simple graph structure.
 * Put your name(s), ID(s), and section here.
 *
 */
class Page{
	
	private int pid;
	private ArrayList<Page> inLink;	//List of pages link to this page
	private ArrayList<Page> outLink; //List of pages this page links to
	private Double pageRank;
	private Double newPageRank;	//Keep newly calculated page rank for update later
	

	public Page(int pid) {
		this.pid = pid;
		inLink = new ArrayList<Page>();
		outLink = new ArrayList<Page>();
		pageRank = 0.0;
		newPageRank = 0.0;
	}
	
	public int getPid() {
		return pid;
	}
	
	public void setPid(int pid) {
		this.pid = pid;
	}
	
	public ArrayList<Page> getInLink() {
		return inLink;
	}
	
	public void setInLink(ArrayList<Page> list) {
		this.inLink = list;
	}
	
	public ArrayList<Page> getOutLink() {
		return outLink;
	}

	public void setOutLink(ArrayList<Page> outLink) {
		this.outLink = outLink;
	}
	
	public Double getPageRank() {
		return pageRank;
	}

	public void setPageRank(Double pageRank) {
		this.pageRank = pageRank;
	}

	public void setNewPageRank(Double newPageRank) {
		this.newPageRank = newPageRank;
	}
	
	public void updatePageRank(){
		this.pageRank = this.newPageRank;
	}
}

public class PageRanker {
	
	/**
	 * This class reads the direct graph stored in the file "inputLinkFilename" into memory.
	 * Each line in the input file should have the following format:
	 * <pid_1> <pid_2> <pid_3> .. <pid_n>
	 * 
	 * Where pid_1, pid_2, ..., pid_n are the page IDs of the page having links to page pid_1. 
	 * You can assume that a page ID is an integer.
	 * @throws Exception 
	 */
	Map<Integer, Page> pages = new HashMap<Integer, Page>();	//Map of all pages
	Map<Integer, Page> sink = new HashMap<Integer, Page>();		//Map of all sink nodes
	Double d = 0.85;
	ArrayList<Double> perplexity = new ArrayList<Double>();		//List of perplexity in each iterations
	
	public void loadData(String inputLinkFilename) throws Exception{
		File f = new File(inputLinkFilename);
		Scanner input = new Scanner(f);
		int pid, qid;
		Page p, q;
		
		while(input.hasNextLine())
		{	
			String line = input.nextLine();
			String[] tokens = line.trim().split(" ");
			pid = Integer.parseInt(tokens[0]);
			//If the page already exists, point to the page, otherwise create a new one and put in the map
			if(pages.containsKey(pid)){
				p = pages.get(pid);
			}
			else{
				p = new Page(pid);
				pages.put(pid, p);
			}
				for(int i=1; i<tokens.length; i++){
					String token = tokens[i];
					qid = Integer.parseInt(token);
					//If the page already exists, point to the page, otherwise create a new one and put in the map
					if(pages.containsKey(qid)){
						q = pages.get(qid);
					}
					else{
						q = new Page(qid);
						pages.put(qid, q);
					}
					//If the page doesn't exist in the lists, put it in
					if(!q.getOutLink().contains(p))
						q.getOutLink().add(p);
					if(!p.getInLink().contains(q))
						p.getInLink().add(q);
				}
		}
		input.close();

		
	}
	

	/**
	 * This method will be called after the graph is loaded into the memory.
	 * This method initialize the parameters for the PageRank algorithm including
	 * setting an initial weight to each page.
	 */
	public void initialize()
	{
		Double n = (double) pages.size();
		for(Page p : pages.values()){
			//Put the sink nodes into sink map
			if(p.getOutLink().isEmpty()){
				sink.put(p.getPid(), p);
			}
			//initialize page rank
			p.setPageRank(1/n);
		}
	}
	
	/**
	 * Computes the perplexity of the current state of the graph. The definition
	 * of perplexity is given in the project specs.
	 */
	public double getPerplexity()
	{
		Double h = (double) 0;
		Double perp;
		for(Page p : pages.values()){
			h += p.getPageRank()*(Math.log(p.getPageRank())/Math.log(2));
		}
		perp = Math.pow(2, -h);
		return perp;
	}
	
	/**
	 * Returns true if the perplexity converges (hence, terminate the PageRank algorithm).
	 * Returns false otherwise (and PageRank algorithm continue to update the page scores). 
	 */
	public boolean isConverge()
	{
		int n = perplexity.size();
		if(perplexity.size()>=4){
			if(Math.floor(perplexity.get(n-2)) != Math.floor(perplexity.get(n-1)))
				return false;
			else if(Math.floor(perplexity.get(n-3)) != Math.floor(perplexity.get(n-1)))
				return false;
			else if(Math.floor(perplexity.get(n-4)) != Math.floor(perplexity.get(n-1)))
				return false;
			else
				return true;
		}
		else{
			return false;
		}
	}
	
	/**
	 * The main method of PageRank algorithm. 
	 * Can assume that initialize() has been called before this method is invoked.
	 * While the algorithm is being run, this method should keep track of the perplexity
	 * after each iteration. 
	 * 
	 * Once the algorithm terminates, the method generates two output files.
	 * [1]	"perplexityOutFilename" lists the perplexity after each iteration on each line. 
	 * 		The output should look something like:
	 *  	
	 *  	183811
	 *  	79669.9
	 *  	86267.7
	 *  	72260.4
	 *  	75132.4
	 *  
	 *  Where, for example,the 183811 is the perplexity after the first iteration.
	 *
	 * [2] "prOutFilename" prints out the score for each page after the algorithm terminate.
	 * 		The output should look something like:
	 * 		
	 * 		1	0.1235
	 * 		2	0.3542
	 * 		3 	0.236
	 * 		
	 * Where, for example, 0.1235 is the PageRank score of page 1.
	 * @throws IOException 
	 * 
	 */
	public void runPageRank(String perplexityOutFilename, String prOutFilename) throws IOException
	{
		Double sinkPR, temp;
		Double n = (double) pages.size();
		while(!isConverge()){
			//Calculate sinkPR
			sinkPR = 0.0;
			for(Page p : sink.values()){
				sinkPR += p.getPageRank();
			}
			//Calculate newPageRank for each page
			for(Page p : pages.values()){
				temp = (1-d)/n;
				temp += d*sinkPR/n;
				for(Page q : p.getInLink()){
					int L = q.getOutLink().size();
					temp += d*q.getPageRank()/L;
				}
				p.setNewPageRank(temp);
			}
			//Update page rank of each page
			for(Page p : pages.values()){
				p.updatePageRank();
			}
			//Keep track of perplexity for each iteration
			perplexity.add(getPerplexity());
		}

		File perpFile = new File(perplexityOutFilename);
		File prFile = new File(prOutFilename);
		BufferedWriter perpWriter = new BufferedWriter(new FileWriter(perpFile));
		BufferedWriter prWriter = new BufferedWriter(new FileWriter(prFile));
		//write perplexity output file
		for(Double i : perplexity){
			perpWriter.write(i + "\n");
		}
		//write page scores output file
		for(Page p : pages.values()){
			prWriter.write(p.getPid() + " " + p.getPageRank() + "\n");
		}
		perpWriter.close();
		prWriter.close();
	}
	
	
	/**
	 * Return the top K page IDs, whose scores are highest.
	 */
	public Integer[] getRankedPages(int K)
	{
		Integer[] rankedPages;
		//Create an array of integer with size of K if K doesn't exceed number of pages, otherwise size is number of pages
		if(K < pages.size()){
			rankedPages = new Integer[K];
		}
		else{
			rankedPages = new Integer[pages.size()];
		}
		//Change map to a list to sort
		List<Entry<Integer, Page>> list = new ArrayList<>(pages.entrySet());
	    Collections.sort(list, new Comparator<Map.Entry<Integer, Page>>() {
	    	@Override
	    	//Override compare method to compare with page rank
	        public int compare(Map.Entry<Integer, Page> o1, Map.Entry<Integer, Page> o2) {
	            return (((Map.Entry<Integer, Page>) (o2)).getValue().getPageRank()).compareTo(((Map.Entry<Integer, Page>) (o1)).getValue().getPageRank());
	        }
	    });
	    for(int i=0; i<K; i++){
	    	if(i >= list.size()){
	    		break;
	    	}
	    	rankedPages[i] = list.get(i).getKey();
	    }
		return rankedPages;
	}
	
	public static void main(String args[]) throws Exception
	{
	long startTime = System.currentTimeMillis();
		PageRanker pageRanker =  new PageRanker();
		pageRanker.loadData("citeseer.dat");
		pageRanker.initialize();
		pageRanker.runPageRank("perplexity.out", "pr_scores.out");
		Integer[] rankedPages = pageRanker.getRankedPages(100);
	double estimatedTime = (double)(System.currentTimeMillis() - startTime)/1000.0;
		
		System.out.println("Top 100 Pages are:\n"+Arrays.toString(rankedPages));
		System.out.println("Proccessing time: "+estimatedTime+" seconds");
	}
}
