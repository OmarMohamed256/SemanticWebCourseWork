import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;


import java.text.ParseException;
import java.text.SimpleDateFormat;  
import java.util.Date; 
import java.util.ArrayList;
import java.util.Scanner;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.util.FileManager;




public class Main {
	static void printInteraction(ArrayList<String> StartDates,ArrayList<String> EndDates, StmtIterator iterMedication1,org.apache.jena.rdf.model.Property medicationNameProperty,ArrayList<String> medicationNames,int index) {
		
		while(iterMedication1.hasNext())
		 {
			 String interactMedcine = iterMedication1.nextStatement()
       			 .getProperty(medicationNameProperty).getString();
			 if(medicationNames.contains(interactMedcine))
			 {
				 int indexMed2 = medicationNames.indexOf(interactMedcine);
						 Date StartDate1 = null;
						try {
							StartDate1 = new SimpleDateFormat("dd/MM/yyyy").parse(StartDates.get(index));
						} catch (ParseException e3) {
							// TODO Auto-generated catch block
							e3.printStackTrace();
						}
				 		 Date EndDate1 = null;
						try {
							EndDate1 = new SimpleDateFormat("dd/MM/yyyy").parse(EndDates.get(index));
						} catch (ParseException e2) {
							// TODO Auto-generated catch block
							e2.printStackTrace();
						}
				 		Date StartDate2 = null;
						try {
							StartDate2 = new SimpleDateFormat("dd/MM/yyyy").parse(StartDates.get(indexMed2));
						} catch (ParseException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
				 		 Date EndDate2 = null;
						try {
							EndDate2 = new SimpleDateFormat("dd/MM/yyyy").parse(EndDates.get(indexMed2));
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				 		 
				 
						 if((StartDate1.before(EndDate2) ) && (StartDate2.before(EndDate1))) {
							 System.out.println(medicationNames.get(index) + " Major With: " + interactMedcine);
						 }
			 }
            
		 }
		
	  }

	@SuppressWarnings("deprecation")
	public static void main(String[] args) {
		
		Model model = ModelFactory.createDefaultModel();
		// use the FileManager to find the input file
		//InputStream in = FileManager.get().open( "D:\\project solution\\WebSemanticsAssigment1\\src\\BookOntology.owl" );
		InputStream in = FileManager.get().open( "D:\\project solution\\WebSemanticsAssigment1\\src\\DrugsOntology.owl" );
		// read the RDF/XML file
		model.read(in, null);
		
		Scanner sc= new Scanner(System.in);
		
		System.out.println("Enter Patient Name");
		
		String patientName = sc.nextLine();

		String patientNameResourceUrl ="http://www.semanticweb.org/omarm/ontologies/2021/11/untitled-ontology-9#" + patientName;
		
		 String takeMedicationUrl="http://www.semanticweb.org/omarm/ontologies/2021/11/untitled-ontology-9#Take_Medication";
		 
		org.apache.jena.rdf.model.Property takeMedicationProperty = model.createProperty(takeMedicationUrl);
		
		 org.apache.jena.rdf.model.Resource patientNameResource = model.getResource(patientNameResourceUrl);
		 
		 
		 String medicationNameUrl="http://www.semanticweb.org/omarm/ontologies/2021/11/untitled-ontology-9#MedicationName";
		 org.apache.jena.rdf.model.Property medicationNameProperty = model.createProperty(medicationNameUrl);
		 
		 String startDateUrl="http://www.semanticweb.org/omarm/ontologies/2021/11/untitled-ontology-9#Start_Date";
		 org.apache.jena.rdf.model.Property startDateProperty = model.createProperty(startDateUrl);
		 
		 String EndDateUrl="http://www.semanticweb.org/omarm/ontologies/2021/11/untitled-ontology-9#End_Date";
		 org.apache.jena.rdf.model.Property EndDateProperty = model.createProperty(EndDateUrl);

		 
		 StmtIterator iterMedication = patientNameResource.listProperties(takeMedicationProperty);
		 StmtIterator iterStartDates = patientNameResource.listProperties(takeMedicationProperty);
		 StmtIterator iterEndDates = patientNameResource.listProperties(takeMedicationProperty);
		 

		 ArrayList<String> medicationNames = new ArrayList<String>();
		 ArrayList<String> StartDates = new ArrayList<String>();
		 ArrayList<String> EndDates = new ArrayList<String>();

		 
		 while(iterMedication.hasNext())
		 {
             medicationNames.add(iterMedication.nextStatement()
        			 .getProperty(medicationNameProperty).getString());
             StartDates.add(iterStartDates.nextStatement()
        			 .getProperty(startDateProperty).getString());
             EndDates.add(iterEndDates.nextStatement()
        			 .getProperty(EndDateProperty).getString());
             
		 }
		 
		 for (int i = 0; i < medicationNames.size(); i++) {
		      System.out.println(medicationNames.get(i));
		      String Medication = medicationNames.get(i);
		      String MedNameResourceUrl ="http://www.semanticweb.org/omarm/ontologies/2021/11/untitled-ontology-9#" + Medication;
				
				 String MajorUrl = "http://www.semanticweb.org/omarm/ontologies/2021/11/untitled-ontology-9#Major";
				 String MinorUrl = "http://www.semanticweb.org/omarm/ontologies/2021/11/untitled-ontology-9#Minor";
				 String ModerateUrl = "http://www.semanticweb.org/omarm/ontologies/2021/11/untitled-ontology-9#Moderate";
				 
				org.apache.jena.rdf.model.Property MajorProperty = model.createProperty(MajorUrl);
				org.apache.jena.rdf.model.Property MinorProperty = model.createProperty(MinorUrl);
				org.apache.jena.rdf.model.Property ModerateProperty = model.createProperty(ModerateUrl);
				
				 org.apache.jena.rdf.model.Resource MedNameResource = model.getResource(MedNameResourceUrl);
				 
				 
				 StmtIterator iterMedication1 = MedNameResource.listProperties(MajorProperty);
				 StmtIterator iterMedication2 = MedNameResource.listProperties(MinorProperty);
				 StmtIterator iterMedication3 = MedNameResource.listProperties(ModerateProperty);
				 printInteraction(StartDates,EndDates,iterMedication1,medicationNameProperty,medicationNames,i);
				 printInteraction(StartDates,EndDates,iterMedication2,medicationNameProperty,medicationNames,i);
				 printInteraction(StartDates,EndDates,iterMedication3,medicationNameProperty,medicationNames,i);
				
				 

		    }
		 
		 
		 
		 
		 

	}

}
