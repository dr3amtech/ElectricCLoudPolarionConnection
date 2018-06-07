package com.ec.polarion.agco;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class AgcoServiceParser {

	
	  private static  int y=0;
	  private static int t =0;
	  private static int x=0;
	  private static String temp =""; 
	  private static String argument = null;
	  private static  Attributes holder=null;
	  private static  BufferedWriter writer = null;
    private static BufferedWriter writer2=null;
    private static List<String> tempHolder = new ArrayList<String>();
    private static  List<String> dataCollectionMap = new ArrayList<String>();
    private static Logger logger = LogManager.getLogger(com.ec.polarion.agco.AgcoServiceParser.class.getName());
    private static List<String> collectionPlate= new ArrayList<String>();
    private static List<String> pathCollection = new ArrayList<String>();
    private static boolean listOfPath = false;
    private static int tracker =0;
    
	  
	public static void agcoParserMain(String commitTest,String connectionFilePath,String svnLocation) {
		logger.debug("XMLParsing begining");
		try {

			logger.debug("Collecting File");
			//args[0]
            File file = new File(commitTest);
			//File file = new File("C:\\Users\\josephjo\\Documents\\Polarion2\\Polarion\\bundled\\updated\\EC_ALM_connector\\CommitTest.txt");
			InputStream inputStream= new FileInputStream(file);
			Reader reader = new InputStreamReader(inputStream,"UTF-8");
			
			InputSource is = new InputSource(reader);
			is.setEncoding("UTF-8");
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			
			//args[1]
			argument = connectionFilePath;
			
			
			
			dataCollectionMap.add("author");
	        dataCollectionMap.add("DATE");
	        dataCollectionMap.add("msg");
	        dataCollectionMap.add("logentry");
	        dataCollectionMap.add("path");
	        
			
			 DefaultHandler handler = new DefaultHandler() {
				
				    boolean author = false;
	    	        boolean date = false;
	    	        boolean msg = false;
	    	        boolean log = false;
	    	        boolean path = false;
	    	        
	    	        
	    	       
	    	        
				  public void startElement(String uri, String localName,
		    	            String qName, Attributes attributes)
		    	            throws SAXException {
					 
					  dataCollectionMap.forEach(k->{
						 // System.out.println(k);
						//System.out.println(qName);
		    	        	if(qName.equalsIgnoreCase(k.toString())) 
		    	        	{
		    	        		if(k.equalsIgnoreCase("author")) {
		    	        			x++;
		    	        			 author = true;
		    	        		}if(k.equalsIgnoreCase("date")) {
		    	        			 date = true;
		    	        		}if(k.equalsIgnoreCase("msg")){
		    	        			msg = true;
		    	        		}if(k.equalsIgnoreCase("logentry")){
		    	        			log = true;
			    	        	  holder = attributes;
		    	        		}if(k.equalsIgnoreCase("path")) {
		    	        			 path= true;
		    	        		}		    	        
		    	        		}
		    	        });
		    	         System.out.println("Start Element :" + qName);


		    	        }

		    	        public void endElement(String uri, String localName,
		    	                String qName)
		    	                throws SAXException {

		    	              System.out.println("End Element :" + qName);

		    	        }

		    	        public void characters(char ch[], int start, int length)
		    	            throws SAXException {
		    	        	
		    	          //System.out.println(new String(ch, start, length));

		    	          if (author) {
		    	        	logger.debug("USER : "+ new String(ch, start, length));
		    	            collectionPlate.add(new String(ch, start, length));
		    	            author = false;
		    	            tracker++;
		    	          }

		    	          if (date) {
		    	        	  logger.debug("DATE : "+ new String(ch, start, length));
		    	              collectionPlate.add(new String(ch, start, length));
		    	              date = false;
		    	              tracker++;
		    	           }
		    	          if (log) {
			    	             for(int i =0; i<holder.getLength(); i++) {
				    	        	String attrValue = holder.getValue(i);
				    	        	collectionPlate.add(attrValue);
				    	        	tracker++;
			    	             }
			    	            log=false;
					    	           }
		    	          if (msg) {
		    	        	  logger.debug("DATA : "+ new String(ch, start, length));
		    	        	  System.out.println("DATA : "+ new String(ch, start, length));
		    	        	 String[] tempString = new String(ch, start, length).split("]\\[");
		    	        	 for(int y =0; y<tempString.length; y++) {
		    	        		 collectionPlate.add(tempString[y].replaceAll("\\[", "").replace("]", ""));
		    	        	 }
		    	              //collectionPlate.add(new String(ch, start, length));
		    	              msg = false;
		    	             
		    	              tracker++;
				         
		    	        } 
		    	          if(path) {
		    	        	  //args[2]
		    	        	  logger.debug("DETAIL DATA : "+ svnLocation+new String(ch, start, length));
		    	        	  	 pathCollection.add(svnLocation+new String(ch, start, length));
		    	        	  	//if(!listOfPath) {
		    	        		  collectionPlate.add(svnLocation+new String(ch, start, length));
		    	        		  listOfPath=true;
		    	        	  	// }
		    	        	  	 path =false;
		    	        	  	tracker++;
		    	        		 
				    	        }
		    	          
		    	          if(tracker==5) {
		    	          pathCollection.forEach(x->{
	    	            	  
	    	            	  for (int y =0; y<collectionPlate.size();y++) {
	    	            		  if(!(y==collectionPlate.size()-1)) {
	    	            		  temp = temp+collectionPlate.get(y)+";";
	    	            		  }else {
	    	            			  temp = temp+collectionPlate.get(y);
	    	            		  }
	    	            	  }
	    	            	  
//	    	              for(int y =0; y<collectionPlate.size();y++) {
//				            	 if(!(y==collectionPlate.size()-1)&y!=3) 
//				            	 {
//				            		 temp=temp+collectionPlate.get(y)+";";
//				            	 }
//				            	 else if(y==4){
//				            		 temp = temp+collectionPlate.get(y);
//				            		 }else {
//				            			 temp = temp+x+";";
//				            		 }
//				        
//				            	  }
	    	              tempHolder.add(temp);
				          temp="";
	    	              });
		    	          
		    	          
		    	          
		    	          collectionPlate.clear();
		    	          pathCollection.clear();
		    	          tracker = 0;
		    	          }
		    	        		 
		    	          }
		    	          
		    	   
		    	       
		    	      
		    	        
			 };
			 //listOfPath=false;
			 
			saxParser.parse(is, handler);
			
			

	}catch(Exception ex) {
		ex.printStackTrace();
	}
		
		   String batp = ".bat";
		
		  //args[1]
          File fileName = new File(connectionFilePath+"\\Commitdata.txt");
         
			try {
				writer = new BufferedWriter(new FileWriter(fileName));
				writer2 = new BufferedWriter(new FileWriter(connectionFilePath+"\\UserCollection"+batp));
			} catch (IOException e1) {
				
				logger.error(e1.getMessage());
			}

//			//Joshua
//         if (tempHolder.size()<5) {
//        	 System.exit(0);
//         }
        tempHolder.forEach(var->{
          	String version ="";
              String commiter ="";
              String date = "";
              String title ="";
              String hyperLink="";
              String desc ="";
              
          	try {
          	writer.write(var.toString());
          	writer.newLine();
				StringTokenizer st = new StringTokenizer(var, ";");
				int i =1;
				
				
				
				 while (st.hasMoreTokens()) {
				      switch (i)
				      {	
				      case 1:
				    	  version = st.nextToken();
				    	  i++;
				    	  break;
				      case 2: 
				    	  commiter = st.nextToken();
				          i++;
				          break;
				      case 3: 
				    	  date = "Date: "+st.nextToken()+"";
				          i++;
				          break;
				      case 4: 
				    	hyperLink=st.nextToken();
				        i++;
				        break;
				      case 5: 
				    	  title = st.nextToken();
				         i++;
				         break;
				      default:
				    	  desc =  st.nextToken();
				    	  i++;
				    	  break;
			} 
				 } 
				 logger.debug("Commited Data: "+version+" : "+commiter+" ; "+date+" ; "+hyperLink+" ; "+title+" ; "+desc );
				 logger.debug("Batch Data: svn log --search "+commiter+" -r HEAD "+hyperLink+" --diff >> "+argument+"\\Attachment"+t+".txt");
				 writer2.write("svn log --search "+commiter+" -r HEAD "+hyperLink+" --diff --non-interactive --trust-server-cert --username=udgaxa --password=AMTeam2018!b >> "+argument+"DataFolder\\Attachment"+t+".txt");
				 writer2.newLine();
				 t++;
          	}catch (IOException e) {
  				logger.debug(e.getMessage());
  				e.printStackTrace();
  			}
          	
          });
   try {
        writer.close();
    	writer2.close();
   }catch(IOException ex) {
  	logger.error(ex.getMessage());
  	ex.printStackTrace();
   }
   	
		//logger.info("TOTAL WORKITEMS UPUDATED FOR "+args[3]+" :"+x);
	}



}
