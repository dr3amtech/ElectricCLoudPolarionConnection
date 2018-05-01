package com.agco.utilities;

import java.util.Date;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailUtil {
	
	/**
	 * Utility method to send simple HTML email
	 * @param session
	 * @param toEmail
	 * @param subject
	 * @param body
	 */
	
	
	public static void sendMail(Session session,String toEmail, String subject, String body ) {
MimeMessage msg = new MimeMessage(session);
try {
msg.addHeader("content-type","text/HTML; charset=UTF-8");
msg.addHeader("format","flowed");
msg.addHeader("content-Transfer-Encoding","8bit");


msg.setFrom(new InternetAddress("ElectricCloud@No-Reply.com","Electric Cloud"));

msg.setReplyTo(InternetAddress.parse("ElectricCloud@No-Reply.com", false));

msg.setSubject(subject,"UTF-8");
msg.setText(body, "UTF-8");
msg.setSentDate(new Date());

msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail,false));
Transport.send(msg);


}catch(Exception ex) {
	ex.printStackTrace();
}
	}
	
	
	
}
