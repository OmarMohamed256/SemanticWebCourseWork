package createAndQuery;


import java.io.FileInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

public class QueryOntology {
	
	 public static void main(String[] args) throws IOException {
		 
		 String prefix = 
				 "Prefix owl: <http://www.w3.org/2002/07/owl#>"+
				 "Prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>"+
				 "Prefix xsd: <http://www.w3.org/2001/XMLSchema#>"+
				 "Prefix ns0: <http://www.semanticweb.org/omarm/ontologies/2021/11/untitled-ontology-23#>";
		 
		 String queryString = null;
		 
		 Scanner sc= new Scanner(System.in);
		 System.out.println("Choose A Number");
		 System.out.println("1-Find top 10 goal scorers");
		 System.out.println("2-List all players given particular nationality.");
		 System.out.println("3-List all players who play in a specific team");
		 System.out.println("4-List all matches played with a specific referee");
		 System.out.println("5-List all team matches with the result given particular team.");
		 int expression= sc.nextInt();

		switch(expression) {
		  case 1:
			  queryString = prefix+ "SELECT ?player ?goals_overall\r\n"
				 		+ "WHERE { ?player ns0:goals_overall ?goals_overall.\r\n"
				 		+ "}\r\n"
				 		+ "ORDER BYDESC(?goals_overall)\r\n"
				 		+ "Limit 10";
		    break;
		  case 2:
			  String Nationality = sc.next();
		    queryString = prefix+ "SELECT ?player ?birthCountry\r\n"
		    		+ "WHERE { \r\n"
		    		+ "  ?player ns0:nationality ?birthCountry.\r\n"
		    		+ "  FILTER (?birthCountry = \""+Nationality+"\"^^xsd:string)\r\n"
		    		+ " \r\n"
		    		+ "}";
		    break;
		  case 3:
			  sc.nextLine();
			  String team = sc.nextLine();
			  queryString = prefix+ "SELECT ?player ?currentTeam\r\n"
			  		+ "WHERE { \r\n"
			  		+ "  ?player ns0:Current_Club ?currentTeam.\r\n"
			  		+ "  FILTER (?currentTeam = \""+team+"\"^^xsd:string)\r\n"
			  		+ " \r\n"
			  		+ "}";
			    break;
		  case 4:
			  sc.nextLine();
			  String refree = sc.nextLine();
			  queryString = prefix+ "SELECT ?Match ?referee\r\n"
			  		+ "WHERE { \r\n"
			  		+ "  ?Match ns0:referee ?referee.\r\n"
			  		+ "  FILTER (?referee = \""+refree+"\"^^xsd:string)\r\n"
			  		+ " \r\n"
			  		+ "}";
			    break;
		  case 5:
			  sc.nextLine();
			  team = sc.nextLine();
			  team = team.replaceAll("\\s+","_");
			  System.out.println(team);
			  queryString = prefix+ "SELECT * WHERE\r\n"
			  		+ "{ \r\n"
			  		+ "{\r\n"
			  		+ "    SELECT ?match ?totalGoalCounts\r\n"
			  		+ "WHERE { \r\n"
			  		+ "     ?match ns0:hasHomeTeam <http://www.semanticweb.org/omarm/ontologies/2021/11/untitled-ontology-23#"+team+"> .\r\n"
			  		+ "      ?match ns0:total_goal_counts ?totalGoalCounts.\r\n"
			  		+ "}\r\n"
			  		+ "}\r\n"
			  		+ "UNION\r\n"
			  		+ "{\r\n"
			  		+ "    SELECT ?match ?totalGoalCounts\r\n"
			  		+ "WHERE { \r\n"
			  		+ "     ?match ns0:hasAwayTeam <http://www.semanticweb.org/omarm/ontologies/2021/11/untitled-ontology-23#"+team+"> 	.\r\n"
			  		+ "      ?match ns0:total_goal_counts ?totalGoalCounts.\r\n"
			  		+ "}\r\n"
			  		+ "}\r\n"
			  		+ "}";
			    break;	    
		  default:
		    // code block
		}
		 
		 Model model = ModelFactory.createDefaultModel();
		 
		 InputStream in = new FileInputStream("D:\\project solution\\SematicWebProject\\src\\FootballOntology.owl");
		 model.read(in, null);
		 in.close();
		 
		 Query query = QueryFactory.create(queryString) ;
		 try(QueryExecution qexec = QueryExecutionFactory.create(query,
		 model)) {
		 ResultSet rs = qexec.execSelect() ;
		 ResultSetFormatter.out(rs) ;
		 }
	 }
	    	

}
