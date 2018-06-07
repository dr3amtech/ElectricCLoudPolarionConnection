package com.ec.polarion.agco;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.mail.Session;
import javax.xml.rpc.ServiceException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.joda.time.LocalDate;

import com.ec.polarion.utilities.EmailUtil;
import com.polarion.alm.ws.client.WebServiceFactory;
import com.polarion.alm.ws.client.projects.ProjectWebService;
import com.polarion.alm.ws.client.session.SessionWebService;
import com.polarion.alm.ws.client.tracker.TrackerWebService;
import com.polarion.alm.ws.client.types.Text;
import com.polarion.alm.ws.client.types.projects.Project;
import com.polarion.alm.ws.client.types.projects.User;
import com.polarion.alm.ws.client.types.tracker.Custom;
import com.polarion.alm.ws.client.types.tracker.EnumOptionId;
import com.polarion.alm.ws.client.types.tracker.Module;
import com.polarion.alm.ws.client.types.tracker.WorkItem;

public class AgcoPolarion {
	private static Logger logger = LogManager.getLogger(com.ec.polarion.agco.AgcoPolarion.class.getName());
	private static Map<String, String> propertiesMap = new LinkedHashMap<String, String>();
	private static int lineNum;
	private EnumOptionId type;
	private  WebServiceFactory factory;
	private  SessionWebService sessionService;
	private TrackerWebService trackerService;
	private  ProjectWebService projectService;
	private  Project project;
	private Module module;
	private static String project_id = "";
	private static String hyperLink = "";
	private static String title = "";
	private static String desc2 = "";
	private static String settingp = null;
	private static String base = null;
	private static String commiter = "";
	private static String date = "";
	private static String workItemType="";
	private static WorkItem parentWorkitem;
	private static WorkItem parent;
	private static boolean format = false;
	private static int trackerFiles;
	private static String wiUri;
	private static WorkItem upWI;
	private static boolean addWorkItem =true;
	private static List<WorkItem> wl  =new ArrayList<WorkItem>();
	private static LocalDate ld = new LocalDate();
	private static Calendar c = Calendar.getInstance();
	


	private  void setUpPolarionAddress()
	{
		InputStream inputStream = null;
		BufferedReader bf =null;
		StringBuffer buf = new StringBuffer();
		try {
			inputStream = new FileInputStream(settingp);
		
		// Map<String,String> propertiesMap = new LinkedHashMap<String,String>();
		 bf = new BufferedReader(new InputStreamReader(inputStream));

		List<String> bufLam = new ArrayList<String>();
		//Lambda Function collecting data from buffer Reader
		bufLam = bf.lines().collect(Collectors.toList());
		
		//lambda function with Exception looping though list of lines from setting properties
		bufLam.forEach(ThrowingINdexOutOfBOunds(property -> {
			String[] temp = property.split("=");
			propertiesMap.put(temp[0], temp[1]);

		}));
		//close buffer
		bf.close();
		//append String with data for connection 
		buf.append(propertiesMap.get("polarion_server_address ").replaceAll(" ","").replaceAll("\\\\", "")).append(":")
				.append(propertiesMap.get("polarion_server_port ").replaceAll(" ", "")).append("/polarion/ws/services/");

		//instantiate factory for services 
		this.factory = new WebServiceFactory(buf.toString());
		} catch (MalformedURLException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (FileNotFoundException e1) {
			logger.error(e1.getMessage());
		} catch (IOException e) {
			logger.error(e.getMessage());
		}

		logger.debug("Trying to connect to ALM web service...");
	}

	private  void setUpPolarionWebServices() throws ServiceException {
		this.sessionService = this.factory.getSessionService();
		this.trackerService = this.factory.getTrackerService();
		this.projectService = this.factory.getProjectService();
	}

	public boolean updateWorkItem(String file,WorkItem parentWorkitem) throws Exception {
		// Boolean debug = Boolean.valueOf(false);

		try {

			
			
			
//			String projectId = propertiesMap.get("project_id");
//			this.project = this.projectService.getProject(projectId);
//			if (this.project.isUnresolvable()) {
//				printError("Project not found: " + projectId);
//				return false;
//			}
//			String moduleName = propertiesMap.get("module");
//			if (moduleName != null) {
//				String moduleLocation = Utils.encodeRelativeLocation(moduleName, null);
//				this.module = this.trackerService.getModuleByLocation(this.project.getId(), moduleLocation);
//				if ((this.module == null) || (this.module.isUnresolvable())) {
//					printError("Module not found: " + moduleName);
//					return false;
//				}
//			}

			this.type = new EnumOptionId(propertiesMap.get("wi_type"));
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);

			logger.debug("[ALM] breakpoint 1");
			//begin Session 
			this.sessionService.beginTransaction();
		
			trackerFiles = 0;
			List<String> readerLines = br.lines().collect(Collectors.toList());
			readerLines.forEach(lines -> {
				if(!lines.isEmpty()) {
				try {
					
					upWI = new WorkItem();
					// collect Workitem and parse with ; as the delimiter
					WorkItem wi = getWorkItemFromLine(lines, propertiesMap.get("item_delimiter "));
					//SQL STRING 
					//title = "B U G-42";
					title = title.replace("WI:", "");
					if(!title.equals("SKIP007")) {
						
					
					String workitem = "SELECT * FROM WORKITEM inner join PROJECT on WORKITEM.FK_URI_PROJECT = PROJECT.C_URI where true and workitem.C_ID in ('"+title+"')";
				
				//'%"+title+"%'";
					//Query for workitem id
					String[] uriList = this.trackerService.queryWorkItemUrisBySQL(workitem);
					
					for(int h=0;h<uriList.length;h++) {
					
					//Collect workitem uri
					upWI = this.trackerService.getWorkItemByUri(uriList[h]);
					
					//get project 
					Project prj =upWI.getProject();
					
					// Was work item null
					wi.equals(null);

					// collect String from properties
					String args20 = project_id.substring(0).replaceAll("\\s", "");

		
					logger.debug("ARGS20 Project ID: " + args20);



					logger.debug("[_ALM] WI Id: '" + upWI.getId() + "'");

					upWI.getId().equals(null);
					
					Custom[] cust2 = upWI.getCustomFields();
					
					

					// store data array Custom Data into a list
//					List<Custom> cust3 = Arrays.asList(upWI.getCustomFields());
//					//lambda function
//					cust3.forEach((custom -> {
//						if(custom.getKey().equals("cf_hasBeenCompleted")) {
//							if (custom.getValue().equals(true)) {
//								addWorkItem=false;
//								logger.debug("This WorkItem"+upWI.getTitle()+"-"+upWI.getId()+"will not be added");
//							}else {
//								custom.setValue(true);
//							}
//						}
//						logger.debug("[_INF] Length of cust. array is: " + cust2.length + "\n");
//
//						String cust_str = "";
//
//						cust_str = custom.getKey().toString();
//						logger.debug("Custom Field Key:  " + cust_str);
//
//						cust_str = custom.getKey().toString();
//						logger.debug("Custom Fields Value: " + cust_str); 
//					}));
					
					// set Enum Option to for hyperlink
					EnumOptionId url_role = new EnumOptionId("external reference");
					// place value to satisfy method
					String[] mine = { "" };
					// instantiate Text Object
					Text newComment = new Text();
					//set type
					newComment.setType("text/html");
					//set content
					newComment.setContent(desc2);
					
					// adding hyperLink
					this.trackerService.addHyperlink(upWI.getUri(), hyperLink, url_role);
					// this.trackerService.addAssignee(arg0, arg1)
					this.trackerService.createCommentNew(upWI.getUri(), "Update", newComment, mine);
					// Convert file to byte in order provide to attached to work item
					byte[] bfile = Files.readAllBytes(new File(base + trackerFiles + ".txt").toPath());
					//Set Attachment
					this.trackerService.createAttachment(upWI.getUri(), "Attachment", "Revsion", bfile);
					// update work item to this point with data given
					WorkItem wTemp = new WorkItem();
					//retrieve parent workitem for link
					WorkItem parentWorkItem=trackerService.getWorkItemById("AMTBacklog", parentWorkitem.getId());
					//Enum option  work item relation
					EnumOptionId link_role = new EnumOptionId("version");
					//add link work item
					this.trackerService.addLinkedItem(upWI.getUri(), parentWorkItem.getUri(), link_role);
					
					
					wTemp =upWI;
					logger.debug("[_LINK]: '" + hyperLink + "'");
					logger.debug("[_Comment] :" + newComment.getContent());
					wl.add(wTemp);
					
					this.trackerService.updateWorkItem(upWI);
					
					
					//increase number for our attahcment files based on the number of lines we have in our parser file
					
					}
					trackerFiles++;
					addWorkItem = true;
					}else {
						System.out.println("Not WorkItem material");
					}

				} catch (RemoteException e2) {
					System.out.println(e2.getMessage());
					logger.error("Error occured during the execution of a remote method call.  " + e2.getMessage());

				} catch (IOException ex) {
					logger.error("Error occured during parsing input file.  " + ex.getMessage());
				} catch (NullPointerException ex) {
					logger.error(ex.getMessage());

				}
				}
//				wl.forEach(x->{
//					try {
//						this.trackerService.updateWorkItem(x);
//					} catch (RemoteException e) {
//						// TODO Auto-generated catch block
//						logger.error(e.getMessage());
//					}
//				
//				});
			});
			//close file Reader
			fr.close();
			// close buffer reader
			br.close();
			
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		finally {
			
				
				// end session transaction
				this.sessionService.endTransaction(false);
				// End Session
				this.sessionService.endSession();

				logger.debug("[ALM] Done Processing WorkItem data");
			}
		return true;
	}

	

	private WorkItem getWorkItemFromLine(String line, String delimiter) {
	
		Text desc = new Text("text/plain", "", false);
		String severity = "";
		String commiter2 = "";

		String comment = "";
		String version = "";

		try {

			StringTokenizer tok = new StringTokenizer(line, delimiter);
			int i = 1;
			String versionPlaceHolder = "";
			while (tok.hasMoreTokens()) {
				switch (i) {
				case 1:
					versionPlaceHolder = tok.nextToken();
					version = "Version: " + versionPlaceHolder;
					i++;
				case 2:
					commiter = tok.nextToken();
					commiter2 = "Commiter: " + commiter;
					i++;
					break;
				case 3:
					date = "Date: " + tok.nextToken() + "";
					i++;
					break;
				case 4:
					hyperLink = tok.nextToken() + "/?p=" + versionPlaceHolder + "/";
					i++;
					break;
				case 5:
					title = tok.nextToken().toUpperCase().replaceAll("\\s+", "");
					i++;
					break;
				case 6:
					workItemType  = tok.nextToken();
					i++;
					break;
				case 7:
					comment = tok.nextToken();
					desc2 = commiter2 + "-----" + date + "-----" + comment + "----" + version;
					desc.setContent(comment);
					i++;
					format = true;
					break;
				default:
					i++;
					break;
				}
			}
		} catch (NoSuchElementException ex) {
			return null;
		}

		WorkItem wi = new WorkItem();
		wi.setDescription(desc);
		wi.setSeverity(new EnumOptionId(severity.toLowerCase()));
		wi.setProject(this.project);
		if (this.module != null) {
			wi.setModuleURI(this.module.getUri());
		}
		wi.setType(this.type);

		
		return wi;
	}

	public int countParsedLines() {
		return lineNum;
	}
	

	public  void Agcomain(String commitData, String properties,String attachment, String project) {
		
		Boolean debug = Boolean.valueOf(true);
		
		logger.debug("EF-ALM_connector mod. for AGCO corp.");
		
				System.out.print("arg_ :" + ":" +commitData +" : " +properties+" : "+attachment+" : "+project +"\n");
		
		// args[1]
		settingp =properties;
		
		try {
			setUpPolarionAddress();
			setUpPolarionWebServices();
			this.sessionService.logIn(propertiesMap.get("user ").replaceAll(" ", ""), propertiesMap.get("passwd ").replaceAll(" ", ""));
			}catch(ServiceException se) {
				se.printStackTrace();
				
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		
		//if day is Monday(2) start new work item for the weeek else grab workitem that was created for the week.
		if(2==c.get(Calendar.DAY_OF_WEEK)) {
			parent = createParent();
			parentWorkitem = createParentWorkItem(parent);
		}else {
			String parentUri = propertiesMap.get("ParentWorkItem ").replaceAll(" ", "");
			try {
				WorkItem wkit = trackerService.getWorkItemByUri(parentUri.toString().replaceAll("\\\\", ""));
				parentWorkitem = createParentWorkItem(wkit);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		//args[2]
		base = attachment;
		

		try {
			//args[0]
			if (updateWorkItem(commitData,parentWorkitem)) {
				logger.debug(
						"RESULT: operation successful - " + trackerFiles + " lines were parsed");
			} else {
				System.err.println("RESULT: operation unsuccessful (1)");
			}
		} catch (Exception e) {
			System.err.println("RESULT: operation unsuccessful (2)");
			e.printStackTrace();
		}
	}

	

	public void sendMail(String body, String userEmail) {
		String relayHost = "smtpapps.atlanta.agcocorp.com";
		String email = userEmail;
		String subject = "Issue with your last commit";
		Properties props = System.getProperties();
		props.put("mail.smtp.host", relayHost);
		Session session = Session.getInstance(props, null);
		EmailUtil.sendMail(session, email, subject, body);

	}


	public static Consumer<String> ThrowingINdexOutOfBOunds(Consumer<String> consumer) {
		return i -> {
			try {
				consumer.accept(i);
			} catch (IndexOutOfBoundsException ex) {
				System.err.println("Index out of bounds: " + ex.getMessage());
				ex.getMessage();
			}
		};

	}
	public  WorkItem createParent() {
		boolean parentB =false;
		WorkItem localParent = new WorkItem();
		String title ="Sprint:"+ld.now().toString();
		WorkItem wi = new WorkItem();
		WorkItem parentWorkItem = new WorkItem();
		EnumOptionId type;
		
		type = new EnumOptionId("SWproductline");
		//type = new EnumOptionId("developmentTask");
		
		
		String[] mine = { " " };
		Text desc = new Text("text/plain", "", false);
		Text comment = new Text("text/plain","",false);
		comment.setContent("Sprint Increment");
		WorkItem wkit = generalCreation(wi,parentWorkItem,type,mine,desc,comment,parentB,title);
		try {
			PropertiesConfiguration config = new PropertiesConfiguration(settingp);
			config.setProperty("ParentWorkItem", wkit.getUri());
			config.save();
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return wkit ;
	}
	
	public  WorkItem createParentWorkItem(WorkItem parent ) {
		boolean childParent = true;
		String title= "Daily Build :"+ld.now().toString();

		WorkItem wi = new WorkItem();
		//WorkItem parentWorkItem = new WorkItem();
		EnumOptionId type;
		
		type = new EnumOptionId("SoftwareVersion");
		//type = new EnumOptionId("developmentTask");
		
		
		String[] mine = { "" };
		Text desc = new Text("text/plain", "", false);
		Text comment = new Text("text/plain","",false);
		
		
		comment.setContent("Daily Increment");
		
	
		return generalCreation(wi,parent,type,mine,desc,comment,childParent,title);
	}
	
	

	public WorkItem generalCreation(WorkItem wi, WorkItem parentWorkItem2, EnumOptionId type, String[] mine, Text desc,
			Text comment,boolean parentB, String title){
		WorkItem localParent = new WorkItem();
		
		try {
			this.sessionService.beginTransaction();
			//Will need to be the integrator Engineer
			User user = this.projectService.getUser("josephjo");
			desc.setContent("Daily Builds");
			wi.setDescription(desc);
			wi.setProject(projectService.getProject("AMTBacklog"));
			wi.setType(type);  
		    wi.setTitle(title);
		    if(!parentB) {
		    	Date locald = new Date();
		    	c.setTime(locald);
		    	c.add(Calendar.DATE,4);
		    	locald = c.getTime();
		    	wi.setDueDate(locald);
		    }
		    String wiURI = this.trackerService.createWorkItem(wi);
			WorkItem getI = new WorkItem();
			// set Enum Option to for hyperlink
			EnumOptionId url_role = new EnumOptionId("external reference");
			
			trackerService.createCommentNew(wiURI, "Update", comment, mine);
			trackerService.addAssignee(wiURI, user.getName());
			
			
			if(parentB) {
			EnumOptionId link_role = new EnumOptionId("version");
			Custom[] customfields =parentWorkItem2.getCustomFields();
			trackerService.addLinkedItem(wiURI,parentWorkItem2.getUri(), link_role);
			}
			
			localParent =trackerService.getWorkItemByUri(wiURI);
			
			System.out.println("Parent Work Item has been created");
		} catch (RemoteException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			
		}finally {
			// end session transaction
			try {
				this.sessionService.endTransaction(false);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		}
		return localParent;
	}
	

}
