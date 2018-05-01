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
import java.util.Arrays;
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

import com.ec.polarion.utilities.EmailUtil;
import com.polarion.alm.ws.client.WebServiceFactory;
import com.polarion.alm.ws.client.projects.ProjectWebService;
import com.polarion.alm.ws.client.session.SessionWebService;
import com.polarion.alm.ws.client.tracker.TrackerWebService;
import com.polarion.alm.ws.client.types.Text;
import com.polarion.alm.ws.client.types.projects.Project;
import com.polarion.alm.ws.client.types.tracker.Custom;
import com.polarion.alm.ws.client.types.tracker.EnumOptionId;
import com.polarion.alm.ws.client.types.tracker.Module;
import com.polarion.alm.ws.client.types.tracker.WorkItem;

public class Agco {
	private static Logger logger = LogManager.getLogger(com.ec.polarion.agco.Agco.class.getName());
	private static Map<String, String> propertiesMap = new LinkedHashMap<String, String>();
	private static int lineNum;
	private EnumOptionId type;
	private WebServiceFactory factory;
	private SessionWebService sessionService;
	private TrackerWebService trackerService;
	private ProjectWebService projectService;
	private Project project;
	private Module module;
	private static String project_id = "";
	private static String hyperLink = "";
	private static String title = "";
	private static String desc2 = "";
	private static String settingp = null;
	private static String base = null;
	private static String commiter = "";
	private static String date = "";
	private static boolean format = false;
	private static int trackerFiles;
	private static String wiUri;
	private static WorkItem upWI;
	private static boolean addWorkItem =true;
	private static List<WorkItem> wl  =new ArrayList<WorkItem>();


	private void setUpPolarionAddress()
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
		buf.append(propertiesMap.get("polarion_server_address")).append(":")
				.append(propertiesMap.get("polarion_server_port")).append("/polarion/ws/services/");

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

	private void setUpPolarionWebServices() throws ServiceException {
		this.sessionService = this.factory.getSessionService();
		this.trackerService = this.factory.getTrackerService();
		this.projectService = this.factory.getProjectService();
	}

	public boolean updateWorkItem(String file) throws Exception {
		// Boolean debug = Boolean.valueOf(false);

		try {

			setUpPolarionAddress();
			setUpPolarionWebServices();

			this.sessionService.logIn(propertiesMap.get("user"), propertiesMap.get("passwd"));
			
			
			
			
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
					WorkItem wi = getWorkItemFromLine(lines, propertiesMap.get("item_delimiter"));
					//SQL STRING 
					//title = "B U G-42";
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
					List<Custom> cust3 = Arrays.asList(upWI.getCustomFields());
					//lambda function
					cust3.forEach((custom -> {
						if(custom.getKey().equals("cf_hasBeenCompleted")) {
							if (custom.getValue().equals(true)) {
								addWorkItem=false;
								logger.debug("This WorkItem"+upWI.getTitle()+"-"+upWI.getId()+"will not be added");
							}else {
								custom.setValue(true);
							}
						}
						logger.debug("[_INF] Length of cust. array is: " + cust2.length + "\n");

						String cust_str = "";

						cust_str = custom.getKey().toString();
						logger.debug("Custom Field Key:  " + cust_str);

						cust_str = custom.getKey().toString();
						logger.debug("Custom Fields Value: " + cust_str); 
					}));
					if(addWorkItem) {
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
					wTemp =upWI;
					logger.debug("[_LINK]: '" + hyperLink + "'");
					logger.debug("[_Comment] :" + newComment.getContent());
					wl.add(wTemp);
					
					}
					
					
					//increase number for our attahcment files based on the number of lines we have in our parser file
					
					}
					trackerFiles++;
					addWorkItem = true;
					

				} catch (RemoteException e2) {
					System.out.println(e2.getMessage());
					logger.error("Error occured during the execution of a remote method call.  " + e2.getMessage());

				} catch (IOException ex) {
					logger.error("Error occured during parsing input file.  " + ex.getMessage());
				} catch (NullPointerException ex) {
					logger.error(ex.getMessage());
//					if (format == true) {
//						try {
//							sendMail("Please see previous commit that was made on:  " + date
//									+ "  Your fromat match the criteria to be updated to Polarion but your workitem id is incorrect: "
//									+ title.toString(), this.projectService.getUser(commiter).getEmail());
//						} catch (RemoteException e) {
//							logger.error(e.getMessage());
//						}
//					} else {
//						logger.debug("Commit was not meant to be added to workitem");
//					}
				}
				}
				wl.forEach(x->{
					try {
						this.trackerService.updateWorkItem(x);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						logger.error(e.getMessage());
					}
				});
			});
			//close file Reader
			fr.close();
			// close buffer reader
			br.close();
			
		}finally {
			
				
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

	

	public static void main(String[] args) {

		Boolean debug = Boolean.valueOf(true);
		if (args.length == 0) {
			logger.error("No Parameters were properly set");
			return;
		}
		logger.debug("EF-ALM_connector mod. for AGCO corp.");
		if (debug.booleanValue()) {
			for (int i = 0; i < args.length; i++) {
				System.out.print("arg_" + Integer.toString(i) + ":" + args[i] + "\n");
			}
		}

		settingp = args[1];
		base = args[2];
		//project_id = args[3];
		Agco importer = new Agco();

		try {

			if (importer.updateWorkItem(args[0])) {
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

	

}
