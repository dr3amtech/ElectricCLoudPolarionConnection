package com.ec.polarion.agco;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Agco {
	private static Logger logger = LogManager.getLogger(com.ec.polarion.agco.Agco.class.getName());
	private static String commitData;
	private static String properties;
	private static String attachment;
	private static String project;
	private static String commitTest;
	private static String connectionFilePath;
	private static String svnLocation;
	
	
	
	public static void main(String...args) {
		
		if (args.length == 0) {
			logger.error("No Parameters were properly set");
			System.exit(1);
		}
		
		commitData = args[0];
		properties = args[1];
		attachment = args[2];
		project = args[3];
		commitTest = args[4];
		connectionFilePath = args[5];
		svnLocation = args[6];
		
		AgcoPolarion ap = new AgcoPolarion();
		AgcoServiceParser.agcoParserMain(commitTest,connectionFilePath,svnLocation);
		ap.Agcomain(commitData,properties,attachment,project);
		
	}

}
