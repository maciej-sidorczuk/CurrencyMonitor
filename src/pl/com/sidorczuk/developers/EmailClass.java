package pl.com.sidorczuk.developers;

import java.util.ArrayList;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class EmailClass {
	public static void sendEmail(String email, String subject, String messageText, ArrayList<String> files) {
		// Recipient's email ID needs to be mentioned.
		String to = email;

		// Sender's email ID needs to be mentioned
		String from = Configuration.config.getFromEmail();

		String host;
		if (Configuration.config.isExternalSmtp()) {
			host = Configuration.config.getSmtpHost();
		} else {
			host = "localhost";
		}

		// Get system properties
		Properties properties = System.getProperties();

		// Setup mail server
		properties.setProperty("mail.smtp.host", host);
		Session session;
		if (Configuration.config.isExternalSmtp()) {
			properties.setProperty("mail.smtp.auth", "true");
			properties.setProperty("mail.smtp.port", Configuration.config.getSmtpPort());
			if (Configuration.config.getIsSSL()) {
				properties.setProperty("mail.smtp.starttls.enable", "true");
			}
			session = Session.getInstance(properties, new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(Configuration.config.getSmtpLogin(),
							Configuration.config.getSmtpPassword());
				}
			});
		} else {
			// Get the default Session object.
			session = Session.getDefaultInstance(properties);
		}

		try {
			// Create a default MimeMessage object.
			MimeMessage message = new MimeMessage(session);

			// Set From: header field of the header.
			message.setFrom(new InternetAddress(from));

			// Set To: header field of the header.
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

			// Set Subject: header field
			message.setSubject(subject);

			// Now set the actual message
			message.setText(messageText);

			MimeBodyPart textBodyPart = new MimeBodyPart();
			textBodyPart.setText(messageText);

			// attach file if there is any
			MimeBodyPart messageBodyPart = new MimeBodyPart();
			Multipart multipart = new MimeMultipart();
			if (files != null) {
				multipart.addBodyPart(textBodyPart);
				for (String file : files) {
					messageBodyPart = new MimeBodyPart();
					DataSource source = new FileDataSource(file);
					messageBodyPart.setDataHandler(new DataHandler(source));
					messageBodyPart.setFileName(file);
					multipart.addBodyPart(messageBodyPart);
				}
				message.setContent(multipart);
			}

			// Send message
			Transport.send(message);
			System.out.println("Sent message successfully....");
		} catch (MessagingException mex) {
			System.out.println("There was problem with sending e-mail. Check your e-mail configuation and try again.");
			CurrencyMonitor.logErrors(mex);
		}
	}

}
