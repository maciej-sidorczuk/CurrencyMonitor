package pl.com.sidorczuk.developers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;
import java.util.StringTokenizer;

public class Configuration {

	private String currencyXmlSource;
	private String xPathToCurrency;
	private String currencySymbolNodeName;
	private String currencySellNodeName;
	private String currencyBuyNodeName;
	private String smtpHost;
	private String smtpLogin;
	private String smtpPassword;
	private String smtpPort;
	private int[] daysToCompare;
	private boolean isSSL;
	private boolean isExternalSmtp;
	private String fromEmail;
	public static Configuration config = new Configuration();

	private Configuration() {
		String configFileName = "config.txt";
		File configFile = new File(configFileName);

		if (configFile.exists()) {
			try {
				createConfiguration(configFile);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				System.out.println(
						"There was a problem with read/save configuration file. Please check if there is permission to read/write config.txt and if it has correct values");
				e.printStackTrace();
				CurrencyMonitor.logErrors(e);
				System.exit(1);
			}
		} else {
			FileWriter fw;
			try {
				fw = new FileWriter(configFileName);
				BufferedWriter bw = new BufferedWriter(fw);
				Scanner scan = new Scanner(System.in);
				String scanValue = "";
				System.out.println("Please enter url for xml file which contains currency values: ");
				scanValue = scan.nextLine();
				if (scanValue != "") {
					bw.write("currency_xml_source;" + scanValue);
				} else {
					bw.write("currency_xml_source;http://api.nbp.pl/api/exchangerates/tables/c?format=xml");
				}
				bw.newLine();
				scanValue = "";
				System.out.println("Please enter xPath for xml file which points to currency node: ");
				scanValue = scan.nextLine();
				if (scanValue != "") {
					bw.write("xpath_to_currency;" + scanValue);
				} else {
					bw.write("xpath_to_currency;/ArrayOfExchangeRatesTable/ExchangeRatesTable/Rates/Rate");
				}
				bw.newLine();
				scanValue = "";
				System.out.println("Please enter node's name for currency symbol: ");
				scanValue = scan.nextLine();
				if (scanValue != "") {
					bw.write("currency_symbol_node_name;" + scanValue);
				} else {
					bw.write("currency_symbol_node_name;Code");
				}
				bw.newLine();
				scanValue = "";
				System.out.println("Please enter node's name for currency sell: ");
				scanValue = scan.nextLine();
				if (scanValue != "") {
					bw.write("currency_sell_node_name;" + scanValue);
				} else {
					bw.write("currency_sell_node_name;Ask");
				}
				bw.newLine();
				scanValue = "";
				System.out.println("Please enter node's name for currency buy: ");
				scanValue = scan.nextLine();
				if (scanValue != "") {
					bw.write("currency_buy_node_name;" + scanValue);
				} else {
					bw.write("currency_buy_node_name;Bid");
				}
				bw.newLine();
				scanValue = "";
				System.out.println(
						"Please enter days separated by comma which are time reference in values comparsion: ");
				scanValue = scan.nextLine();
				if (scanValue != "") {
					bw.write("days_to_compare;" + scanValue);
				} else {
					bw.write("days_to_compare;1825,1460,1095,730,365,180,90,30,14,7,3");
				}
				bw.newLine();
				scanValue = "";
				System.out.println("Please enter an email which will be field 'From e-mail' during sending e-mails: ");
				scanValue = scan.nextLine();
				if (scanValue != "") {
					bw.write("from_email;" + scanValue);
				} else {
					bw.write("from_email;currencyMonitor@raspberrypi");
				}
				bw.newLine();
				scanValue = "";
				System.out.println("Will you use external smtp server? If yes please type 1, otherwise 0: ");
				scanValue = scan.nextLine();
				if (scanValue != "") {
					bw.write("external_smtp_server;" + scanValue);
				} else {
					bw.write("external_smtp_server;0");
				}
				bw.newLine();
				scanValue = "";
				System.out.println("Please enter smtp host. Default: 'localhost' : ");
				scanValue = scan.nextLine();
				if (scanValue != "") {
					bw.write("external_smtp_host;" + scanValue);
				} else {
					bw.write("external_smtp_host;localhost");
				}
				bw.newLine();
				scanValue = "";
				System.out.println("Please enter smtp login: ");
				scanValue = scan.nextLine();
				if (scanValue != "") {
					bw.write("smtp_login;" + scanValue);
				} else {
					bw.write("smtp_login;currencyMonitor@raspberrypi");
				}
				bw.newLine();
				scanValue = "";
				System.out.println("Please enter smtp password: ");
				scanValue = scan.nextLine();
				if (scanValue != "") {
					bw.write("smtp_password;" + scanValue);
				} else {
					bw.write("smtp_password;password");
				}
				bw.newLine();
				scanValue = "";
				System.out.println("Please enter smtp port: ");
				scanValue = scan.nextLine();
				if (scanValue != "") {
					bw.write("smtp_port;" + scanValue);
				} else {
					bw.write("smtp_port;587");
				}
				bw.newLine();
				scanValue = "";
				System.out.println("Will you use STARTTLS? If yes type 1, otherwise 0 : ");
				scanValue = scan.nextLine();
				if (scanValue != "") {
					bw.write("startls;" + scanValue);
				} else {
					bw.write("startls;0");
				}
				bw.newLine();
				bw.close();
				scan.close();
				createConfiguration(configFile);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println(
						"There was a problem with read/save configuration file. Please check if there is permission to read/write config.txt and if it has correct values");
				e.printStackTrace();
				CurrencyMonitor.logErrors(e);
				System.exit(1);
			}

		}
	}

	public String getCurrencyXmlSource() {
		return this.currencyXmlSource;
	}

	public String getxPathToCurrency() {
		return this.xPathToCurrency;
	}

	public String getCurrencySymbolNodeName() {
		return this.currencySymbolNodeName;
	}

	public String getCurrencySellNodeName() {
		return this.currencySellNodeName;
	}

	public String getCurrencyBuyNodeName() {
		return this.currencyBuyNodeName;
	}

	public String getSmtpHost() {
		return this.smtpHost;
	}

	public String getSmtpLogin() {
		return this.smtpLogin;
	}

	public String getSmtpPassword() {
		return this.smtpPassword;
	}

	public String getSmtpPort() {
		return this.smtpPort;
	}

	public int[] getDaysToCompare() {
		return this.daysToCompare;
	}

	public boolean getIsSSL() {
		return this.isSSL;
	}

	public boolean isExternalSmtp() {
		return this.isExternalSmtp;
	}

	public String getFromEmail() {
		return this.fromEmail;
	}

	public void setCurrencyXmlSource(String value) {
		this.currencyXmlSource = value;
	}

	public void setxPathToCurrency(String value) {
		this.xPathToCurrency = value;
	}

	public void setCurrencySymbolNodeName(String value) {
		this.currencySymbolNodeName = value;
	}

	public void setCurrencySellNodeName(String value) {
		this.currencySellNodeName = value;
	}

	public void setCurrencyBuyNodeName(String value) {
		this.currencyBuyNodeName = value;
	}

	public void setSmtpHost(String value) {
		this.smtpHost = value;
	}

	public void setSmtpLogin(String value) {
		this.smtpLogin = value;
	}

	public void setSmtpPassword(String value) {
		this.smtpPassword = value;
	}

	public void setSmtpPort(String value) {
		this.smtpPort = value;
	}

	public void setFromEmail(String value) {
		this.fromEmail = value;
	}

	public void setIsSSL(String value) {
		if (value.equals("0")) {
			this.isSSL = false;
		} else {
			this.isSSL = true;
		}
	}

	public void setIsExternalSmtp(String value) {
		if (value.equals("0")) {
			this.isExternalSmtp = false;
		} else {
			this.isExternalSmtp = true;
		}
	}

	public void setDaysToCompare(String value) {
		String[] values = value.split(",");
		int[] days = new int[values.length];
		ArrayList<Integer> daysList = new ArrayList<>();
		for (int i = 0; i < values.length; i++) {
			daysList.add(Integer.parseInt(values[i]));
		}
		Collections.sort(daysList);
		Collections.reverse(daysList);
		for (int i = 0; i < daysList.size(); i++) {
			days[i] = daysList.get(i);
		}
		this.daysToCompare = days;
	}

	public void createConfiguration(File configFile) throws FileNotFoundException {
		HashMap<String, String> settingsMap = new HashMap<>();
		Scanner scan = new Scanner(configFile);
		ArrayList<String> configurationKeys = new ArrayList<>();
		configurationKeys.add("currency_xml_source");
		configurationKeys.add("xpath_to_currency");
		configurationKeys.add("currency_symbol_node_name");
		configurationKeys.add("currency_sell_node_name");
		configurationKeys.add("currency_buy_node_name");
		configurationKeys.add("external_smtp_host");
		configurationKeys.add("smtp_login");
		configurationKeys.add("smtp_password");
		configurationKeys.add("smtp_port");
		configurationKeys.add("days_to_compare");
		configurationKeys.add("external_smtp_server");
		configurationKeys.add("startls");
		configurationKeys.add("from_email");
		int parametersCounter = 0;
		while (scan.hasNextLine()) {
			StringTokenizer tokenizer = new StringTokenizer(scan.nextLine(), ";");
			String key_setting = tokenizer.nextToken();
			if(!configurationKeys.contains(key_setting)) {
				String errorText = "You have wrong parameters in config.txt. Please check the config.txt and run program again.";
				System.out.println(errorText);
				CurrencyMonitor.logErrors(errorText);
				System.exit(1);
			}
			if(!tokenizer.hasMoreTokens()) {
				String errorText = "You have incorrect structure of configuration file. Please correct config.txt and run program again.";
				System.out.println(errorText);
				CurrencyMonitor.logErrors(errorText);
				System.exit(1);
			}
			String value_setting = tokenizer.nextToken();
			settingsMap.put(key_setting, value_setting);
			parametersCounter++;
		}
		scan.close();
		if(parametersCounter != configurationKeys.size()) {
			String errorText = "You have insufficient parameters in config.txt. Please check the config.txt and run program again.";
			System.out.println(errorText);
			CurrencyMonitor.logErrors(errorText);
			System.exit(1);		
		}
		setCurrencyXmlSource(settingsMap.get(configurationKeys.get(0)));
		setxPathToCurrency(settingsMap.get(configurationKeys.get(1)));
		setCurrencySymbolNodeName(settingsMap.get(configurationKeys.get(2)));
		setCurrencySellNodeName(settingsMap.get(configurationKeys.get(3)));
		setCurrencyBuyNodeName(settingsMap.get(configurationKeys.get(4)));
		setSmtpHost(settingsMap.get(configurationKeys.get(5)));
		setSmtpLogin(settingsMap.get(configurationKeys.get(6)));
		setSmtpPassword(settingsMap.get(configurationKeys.get(7)));
		setSmtpPort(settingsMap.get(configurationKeys.get(8)));
		setDaysToCompare(settingsMap.get(configurationKeys.get(9)));
		setIsExternalSmtp(settingsMap.get(configurationKeys.get(10)));
		setIsSSL(settingsMap.get(configurationKeys.get(11)));
		setFromEmail(settingsMap.get(configurationKeys.get(12)));
	}

}
