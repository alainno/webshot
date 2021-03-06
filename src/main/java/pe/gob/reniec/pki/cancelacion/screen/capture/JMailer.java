/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.reniec.pki.cancelacion.screen.capture;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.Message.RecipientType;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * a java mail client is like phpmailer author:freecode-freecode.blogspot.com
 */
public class JMailer {
	// ///////////////////////////////////////////////
	// PUBLIC VARIABLES
	// ///////////////////////////////////////////////

	/**
	 * Email priority (1 = High, 3 = Normal, 5 = low).
	 */
	public int priority = 3;

	/**
	 * Sets the CharSet of the message.
	 */
	public String charSet = "iso-8859-1";

	/**
	 * Sets the Content-type of the message.
	 */
	public String contentType = "text/plain";

	/**
	 * Sets the Encoding of the message. Options for this are "8bit", "7bit",
	 * "binary", "base64", and "quoted-printable". Content-Transfer-Encoding
	 */
	public String encoding = "8bit";

	/**
	 * Sets the From email address for the message.
	 */
	public String from = "root@localhost";

	/**
	 * Sets the Sender email (Return-Path) of the message. If not empty, will be
	 * sent via -f to sendmail or as 'MAIL FROM' in smtp mode.
	 */
	public String sender = "";

	/**
	 * Sets the Subject of the message.
	 */
	public String subject = "";

	/**
	 * Sets the Body of the message. This can be either an HTML or text body. If
	 * HTML then run IsHTML(true).
	 */
	public String body = "";

	// ///////////////////////////////////////////////
	// SMTP VARIABLES
	// ///////////////////////////////////////////////
	/**
	 * Sets the SMTP hosts. All hosts must be separated by a semicolon. You can
	 * also specify a different port for each host by using this format:
	 * [hostname:port] (e.g. "smtp1.example.com:25;smtp2.example.com"). Hosts
	 * will be tried in order.
	 */
	public String host = "localhost";

	/**
	 * Sets the default SMTP server port.(25)
	 */
	public int port = 25;

	/**
	 * Sets the SMTP HELO of the message ($Hostname).
	 */
	public String helo = "";

	/**
	 * Sets SMTP authentication. Utilizes the Username and Password variables.
	 * (false)
	 */
	public boolean smtpAuth = false;

	/**
	 * Sets SMTP username.
	 */
	public String username = "";

	/**
	 * Sets SMTP password.
	 */
	public String password = "";

	// private
	// $smtp = NULL;
	private StringBuffer to = new StringBuffer();
	private StringBuffer cc = new StringBuffer();
	private StringBuffer bcc = new StringBuffer();
	private StringBuffer replyTo = new StringBuffer();
	// 
	private ArrayList attachment = new ArrayList(0);
	private ArrayList<String> cids = new ArrayList(0);
	private Date sendDate = new Date();

	private Hashtable _customHeader = new Hashtable();
	private String _message_type = "plain";

	private static String COMMA = ",";

	public JMailer() {
	}

	/**
	 * Sets message type to HTML.
	 *
	 * @param isHTML
	 */
	public void setHTML(boolean isHTML) {
		if (isHTML) {
			contentType = "text/html";
		} else {
			contentType = "text/plain";
		}
	}

	/**
	 * Adds a "To" address.
	 *
	 * @param address
	 */
	public void addAddress(String address) {
		to.append(address).append(COMMA);
	}

	/**
	 * Adds a "Cc" address.
	 *
	 * @param address
	 */
	public void addCC(String address) {
		cc.append(address).append(COMMA);
	}

	/**
	 * Adds a "Bcc" address.
	 *
	 * @param address
	 */
	public void addBCC(String address) {
		bcc.append(address).append(COMMA);
	}

	/**
	 * Adds a "Reply-to" address
	 *
	 * @param address
	 */
	public void addReplyTo(String address) {
		replyTo.append(address).append(COMMA);
	}

	/**
	 * Adds a custom header.
	 *
	 * @param name
	 * @param value
	 */
	public void addHeader(String name, String value) {
		_customHeader.put(name, value);
	}

	public void removeHeader(String name) {
		_customHeader.remove(name);
	}

	/**
	 * Sets send date.
	 *
	 * @param d
	 */
	public void setSendDate(Date d) {
		sendDate = d;
	}

	public void addAttachment(File f) {
		if (f != null && f.isFile()) {
			attachment.add(f);
		}
	}

	public void AddAttachment(String file) {
		File f = new File(file);
		if (f.isFile()) {
			attachment.add(f);
			cids.add("");
		}
	}
	
	public void AddAttachment(String file, String cid) {
		File f = new File(file);
		if (f.isFile()) {
			attachment.add(f);
			cids.add(cid);
		}		
	}

	/**
	 * Send message.
	 *
	 * @return
	 */
	public boolean send() {
		boolean ok = false;

		Session session = createSession();

		final MimeMessage msg = new MimeMessage(session);
		try {
			msg.setRecipients(RecipientType.TO, parseAddress(to));

			Address[] ccAddress = parseAddress(cc);
			if (ccAddress != null) {
				msg.setRecipients(RecipientType.CC, ccAddress);
			}
			Address[] bccAddress = parseAddress(bcc);
			if (bccAddress != null) {
				msg.setRecipients(RecipientType.BCC, bccAddress);
			}
			Address[] replyToAddress = parseAddress(replyTo);
			if (replyToAddress != null) {
				msg.setReplyTo(replyToAddress);
			}
			if (!"".equals(from)) {
				if (!"".equals(sender)) {
					msg.setFrom(new InternetAddress(from, sender));
				} else {
					msg.setFrom(new InternetAddress(from));
				}
			}

			msg.setSentDate(sendDate);

			msg.setSubject(subject, charSet);
			msg.addHeaderLine("Content-Transfer-Encoding: " + encoding);
			msg.addHeaderLine("X-Priority: " + priority);

			msg.addHeaderLine("Content-Type: " + contentType + "; charset=" + charSet);
			// add custom header
			Set keyset = _customHeader.keySet();
			for (Iterator keys = keyset.iterator(); keys.hasNext();) {
				String name = (String) keys.next();
				String value = (String) _customHeader.get(name);
				msg.addHeader(name, value);
			}

			// attach the file to the message
			if (attachment.size() > 0) {

				// create and fill the message body
				MimeBodyPart body1 = new MimeBodyPart();
				body1.setContent(body, contentType + "; charset=" + charSet);

				// create the Multipart and its parts to it
				Multipart mp = new MimeMultipart();
				mp.addBodyPart(body1);

				for (int i = 0; i < attachment.size(); i++) {
					//FileDataSource fds = new FileDataSource((File) attachment.get(i));
					MimeBodyPart body2 = new MimeBodyPart();
					//body2.setDataHandler(new DataHandler(fds));
					//body2.setFileName(fds.getName());
					String cid = cids.get(i);
					System.out.println("********** CID: " + cid);
					if(!cid.isEmpty()){
						//body2.setContentID(cid);
                                                body2.setHeader("Content-ID", "<" + cid + ">");
                                                body2.setDisposition(MimeBodyPart.INLINE);
					}
					
                                        body2.attachFile((File)attachment.get(i));
                                        
					mp.addBodyPart(body2);
				}

				msg.setContent(mp);
			} else {
				msg.setContent(body, contentType + "; charset=" + charSet);
			}
			Transport.send(msg);
			ok = true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return ok;
	}

	// create a session
	private Session createSession() {
		Authenticator authenticator = null;
		Properties props = new Properties();
		props.put("mail.smtp.host", host);

		if (smtpAuth) {
			props.put("mail.smtp.auth", "true");// 身份验证
			authenticator = new Authenticator() {
				public PasswordAuthentication getPasswordAuthentication() {

					return new PasswordAuthentication(username, password);
				}
			};
		}
		return Session.getInstance(props, authenticator);
	}

	private Address[] parseAddress(StringBuffer bufAddress)
			throws AddressException {
		String addresses = bufAddress.toString();

		if ("".equals(addresses)) {
			return null;
		}

		if (addresses.endsWith(",")) {
			addresses = addresses.substring(0, addresses.length() - 1);
		}

		return InternetAddress.parse(addresses);
	}
}
