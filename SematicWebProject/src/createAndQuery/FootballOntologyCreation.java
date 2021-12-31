package createAndQuery;
import org.apache.jena.ontology.*;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.vocabulary.XSD;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;
public class FootballOntologyCreation {
	
	static ArrayList<String[]> ReadCsvFiles(String CsvFilePath) {
		String line = "";  
    	String splitBy = ",";  
    	ArrayList<String[]> data = new ArrayList<String[]>();
    	try   
    	{  
    	//parsing a CSV file into BufferedReader class constructor  
    	BufferedReader br = new BufferedReader(new FileReader(CsvFilePath));
    	
    	String[] column;
    	while ((line = br.readLine()) != null)   //returns a Boolean value  
    	{  
    		column = line.split(splitBy);    // use comma as separator  
    	 data.add(column);
    	}  
    	}   
    	catch (IOException e)   
    	{  
    	e.printStackTrace();  
    	}
		return data;  
	  }
	// initialize instances
	static public Vector<Individual> CreateInstances(ArrayList<String[]> matchesData, OntModel model, String uri, OntClass dataClass)
	{
		Vector<Individual> instances = new Vector<Individual>();
		for(int i=1; i<matchesData.size(); i++)
        {

			
            Individual individual;
            if(dataClass.getLocalName().contains("match"))
            	individual = dataClass.createIndividual(uri + matchesData.get(i)[4].replaceAll("\\s+","_") + "-" + matchesData.get(i)[5].replaceAll("\\s+","_"));
            else
            	individual = dataClass.createIndividual(uri + matchesData.get(i)[0].replaceAll("\\s+","_"));


            instances.add(individual);
            
            for(int j=0; j<matchesData.get(0).length; j++)
            {
            	// add data  properties values to instances
            	AddDataPropertyToIndividual(individual,model,uri,dataClass,matchesData.get(0)[j],matchesData.get(i)[j]);

            }
            
        }
		return instances;
		
	}
	
	// add data  properties to instances
	
	static public void AddDataPropertyToIndividual(Individual instance, OntModel model, String uri, OntClass dataClass,String dataPropertyTitle,String dataPropertyValue)
	{

			DatatypeProperty dataProperty = model.createDatatypeProperty(uri + dataPropertyTitle);
			dataProperty.addDomain(dataClass);

            if(dataPropertyTitle.contains("overall") || dataPropertyTitle.contains("counts"))
            	dataProperty.addRange(XSD.integer);
            else if(dataPropertyTitle.equalsIgnoreCase("points_per_game"))
            	dataProperty.addRange(XSD.xdouble);
            else
            	dataProperty.addRange(XSD.xstring);
            
            if(dataProperty.hasRange(XSD.xstring))
            {
            	instance.addLiteral(dataProperty, dataPropertyValue);
            }else if(dataProperty.hasRange(XSD.xdouble)) {
            	
            	instance.addLiteral(dataProperty, Double.parseDouble(dataPropertyValue));
            	
            }else if(dataProperty.hasRange(XSD.integer)) {
            	
            	instance.addLiteral(dataProperty, Integer.parseInt(dataPropertyValue));
            	
            }


    }


    static public void FeedObjectProprties(Vector<Individual> domainInstances, Vector<Individual> rangeInstances, ArrayList<String[]> playersData,
                                              DatatypeProperty domainProperty, DatatypeProperty rangeProperty, ObjectProperty objectPropertyToAdd)
    {
        for(int i=0; i<domainInstances.size(); i++)
        {

            for(int j = 0; j < domainInstances.size(); j++) {
                if(  domainInstances.get(i).getPropertyValue(domainProperty).equals(rangeInstances.get(j).getPropertyValue(rangeProperty))  ) {
                	domainInstances.get(i).addProperty(objectPropertyToAdd, rangeInstances.get(j));
                    break;
                }
            }

        }
    }

    public static void main(String[] args) throws IOException {
    	
        ArrayList<String[]> matchesData = ReadCsvFiles("src\\matches.csv");
        ArrayList<String[]> playersData = ReadCsvFiles("src\\players.csv");
        ArrayList<String[]> teamsData = ReadCsvFiles("src\\teams.csv");


        String baseURI = "http://www.semanticweb.org/omarm/ontologies/2021/11/untitled-ontology-23#";
        OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_RDFS_INF);

        // create ontology classes
        
        OntClass matchClass =  model.createClass(baseURI + "match");
        OntClass playerClass = model.createClass(baseURI + "player");
        OntClass teamClass = model.createClass(baseURI + "team");



        // Creating data properties and individuals, then adding these data property values to individuals
        Vector<Individual> matchIndividuals =  CreateInstances(matchesData, model, baseURI, matchClass);
        //for players with the full_name
        Vector<Individual> playerIndividuals =  CreateInstances(playersData, model, baseURI, playerClass);
        //for teams with the team_name
        Vector<Individual> teamIndividuals =  CreateInstances(teamsData, model, baseURI, teamClass);
        
        
        
     // create object Properties
        ObjectProperty playsFor = model.createObjectProperty( baseURI + "PlaysFor" );
        playsFor.addDomain(playerClass);
        playsFor.addRange(teamClass);

        ObjectProperty hasHomeTeam = model.createObjectProperty( baseURI + "hasHomeTeam" );
        hasHomeTeam.addDomain(matchClass);
        hasHomeTeam.addRange(teamClass);

        ObjectProperty hasAwayTeam = model.createObjectProperty( baseURI + "hasAwayTeam" );
        hasAwayTeam.addDomain(matchClass);
        hasAwayTeam.addRange(teamClass);


        DatatypeProperty commonNameProperty = model.getDatatypeProperty(baseURI + teamsData.get(0)[1]);
        DatatypeProperty currentClubProperty = model.getDatatypeProperty(baseURI + playersData.get(0)[5]);

        DatatypeProperty homeTeamNameProperty = model.getDatatypeProperty(baseURI + matchesData.get(0)[4]);
        DatatypeProperty awayTeamNameProperty = model.getDatatypeProperty(baseURI + matchesData.get(0)[5]);
        
        
        



        // feeding object properties with instances
        // player playsFor team
        FeedObjectProprties(playerIndividuals, teamIndividuals, playersData, currentClubProperty, commonNameProperty, playsFor);
        // match  homeTeam Team
        FeedObjectProprties(matchIndividuals, teamIndividuals, matchesData, homeTeamNameProperty, commonNameProperty, hasHomeTeam);
        // match  hasAway Team
        FeedObjectProprties(matchIndividuals, teamIndividuals, matchesData, awayTeamNameProperty, commonNameProperty, hasAwayTeam);
        FileWriter out = null;
        
        out = new FileWriter( "D:\\project solution\\SematicWebProject\\src\\FootballOntology.owl" );
        model.write( out, "RDF/XML-ABBREV" );

        model.write(System.out,"RDF/XML-ABBREV");
    }


}
