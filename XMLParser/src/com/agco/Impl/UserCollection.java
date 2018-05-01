package com.agco.Impl;

import org.xml.sax.Attributes;

public interface UserCollection {

//	public static boolean collect(String qName) {
//		
//		 if (qName.equalsIgnoreCase("author")) {
//       	  author = true;
//         }
//
//         if (qName.equalsIgnoreCase("DATE")) {
//       	  date = true;
//         }
//
//         if (qName.equalsIgnoreCase("MSG")) {
//       	  MSG = true;
//         }
//
//         if (qName.equalsIgnoreCase("logentry")) {
//       	  log = true;
//       	  holder = attributes;
//         }
//         if(qName.equals("path")){
//       	  path= true;
//       	
//         }
	
         public static void startElement(String uri, String localName,
 	            String qName, Attributes attributes) {};
	
	
	
}
