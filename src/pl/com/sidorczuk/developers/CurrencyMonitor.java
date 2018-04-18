package pl.com.sidorczuk.developers;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class CurrencyMonitor {

	static String email = "";

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String mode = "collect";
		String currency = "";
		String parameters = "";
		if (args.length == 0) {
			System.out.println(
					"Currency was not provided. Please run program with --currency parameter. Example: --currency=eur,usd");
			System.out.println("Program will be closed.");
			System.exit(1);
		} else {
			for (int i = 0; i < args.length; i++) {
				parameters += args[i] + " ";
			}
			Pattern p = Pattern.compile(".*--currency=([^-\\s]*).*");
			Matcher matcher = p.matcher(parameters);
			boolean isMatching = matcher.matches();
			if (isMatching) {
				int n = matcher.groupCount();
				if (n != 1) {
					System.out.println("Wrong parameters. Program will be terminated.");
					System.exit(1);
				} else {
					currency = matcher.group(1).trim();
				}
			} else {
				System.out.println(
						"Currency was not provided. Please run program with --currency parameter. Example: --currency=eur,usd");
				System.out.println("Program will be closed!.");
				System.exit(1);
			}

			p = Pattern.compile(".*--mode=([^-\\s]*).*");
			matcher = p.matcher(parameters);
			isMatching = matcher.matches();
			if (isMatching) {
				int n = matcher.groupCount();
				if (n != 1) {
					System.out.println("Wrong parameters. Program will be terminated.");
					System.exit(1);
				} else {
					mode = matcher.group(1).trim().toLowerCase();
				}
			}
			p = Pattern.compile(".*--email=([^-\\s]*).*");
			matcher = p.matcher(parameters);
			isMatching = matcher.matches();
			if (isMatching) {
				int n = matcher.groupCount();
				if (n != 1) {
					System.out.println("Wrong parameters. Program will be terminated.");
					System.exit(1);
				} else {
					email = matcher.group(1).trim().toLowerCase();
				}
			}
			String[] currency_array = currency.toUpperCase().split(",");
			switch (mode) {
			case "collect":
				collectMode(currency_array);
				break;
			case "analyze":
				analyzeMode(currency_array);
				break;
			}

		}

	}

	public static void collectCurrency(String[] currency_symbols)
			throws ParserConfigurationException, IOException, SAXException, XPathExpressionException {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		String date_as_string = dateFormat.format(date);
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder domBuilder = factory.newDocumentBuilder();
		URL url = new URL(Configuration.config.getCurrencyXmlSource());
		InputStream streamXML;
		FileOutputStream outputXML;
		streamXML = url.openStream();
		outputXML = new FileOutputStream("data.xml");
		int c;
		while ((c = streamXML.read()) != -1) {
			outputXML.write(c);
		}
		streamXML.close();
		outputXML.close();
		Document dom = domBuilder.parse(new File("data.xml"));

		XPathFactory xPathfactory = XPathFactory.newInstance();
		XPath xpath = xPathfactory.newXPath();
		XPathExpression expr;

		expr = xpath.compile(Configuration.config.getxPathToCurrency());
		String nodeCurrency = Configuration.config.getCurrencySymbolNodeName();
		String nodeSell = Configuration.config.getCurrencySellNodeName();
		String nodeBuy = Configuration.config.getCurrencyBuyNodeName();
		List<String> list = Arrays.asList(currency_symbols);
		NodeList nl = (NodeList) expr.evaluate(dom, XPathConstants.NODESET);
		for (int i = 0; i < nl.getLength(); i++) {
			NodeList childNodes = nl.item(i).getChildNodes();
			boolean isCurrencyToAdd = false;
			String currencySymbol = "";
			String sellValue = "";
			String buyValue = "";
			for (int j = 0; j < childNodes.getLength(); j++) {
				Node itemNode = childNodes.item(j);
				String currentNode = itemNode.getNodeName();
				if ((currentNode.toLowerCase().trim()).equals(nodeCurrency.toLowerCase().trim())) {
					currencySymbol = itemNode.getTextContent();
					if (list.contains(currencySymbol.toUpperCase())) {
						isCurrencyToAdd = true;
					}
				}
				if (currentNode.equals(nodeSell)) {
					sellValue = itemNode.getTextContent();
				}
				if (currentNode.equals(nodeBuy)) {
					buyValue = itemNode.getTextContent();
				}
			}
			if (isCurrencyToAdd) {
				FileWriter fw = new FileWriter("collected_data.csv", true);
				BufferedWriter bw = new BufferedWriter(fw);
				bw.write(currencySymbol + ";" + date_as_string + ";" + buyValue + ";" + sellValue);
				bw.newLine();
				bw.close();
			}
		}

	}

	public static void collectMode(String[] currency_array) {
		try {
			collectCurrency(currency_array);
		} catch (ParserConfigurationException | IOException | SAXException | XPathExpressionException e) {
			// TODO Auto-generated catch block
			System.out.println("Some error ocurrs. Details below: ");
			e.printStackTrace();
			logErrors(e);
		}
	}

	public static void analyzeMode(String[] currency_array) {
		ArrayList<Currency> listOfCurrencyToOperate = new ArrayList<>();
		for (int i = 0; i < currency_array.length; i++) {
			try {
				listOfCurrencyToOperate.add(new Currency(currency_array[i]));
			} catch (FileNotFoundException e) {
				System.out.println("File csv with currency data was not found. Data will not be process.");
			} catch (StructureException e) {
				System.out.println(
						"Structure of file csv is invalid. Currency " + currency_array[i] + " will be skipped.");
			} catch (ParseException e) {
				System.out.println("Csv file with currency data has invalid values. Currency " + currency_array[i]
						+ " will be skipped.");
			} catch (Exception e) {
				System.out.println("Some error ocurrs. Details below: ");
				e.printStackTrace();
				logErrors(e);
			}
		}
		ArrayList<String> fileList;
		try {
			fileList = prepareChart(listOfCurrencyToOperate);
			try {
				String output = generateOutputForEmail(listOfCurrencyToOperate);
				System.out.println(output);
				EmailClass.sendEmail(email, "Currency exchange details", output, fileList);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				logErrors(e);
			}
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			logErrors(e1);
		}
	}

	public static String generateOutputForEmail(ArrayList<Currency> currencyList) throws Exception {
		String output = "";
		for (int i = 0; i < currencyList.size(); i++) {
			output += "Currency: " + currencyList.get(i).getCurrencyName() + "\n";
			Date currentTime = new Date();
			Double sellValue = 0.0;
			Double buyValue = 0.0;
			String currencyTime = "";
			String key;
			HashMap<String, Double> recordSell = currencyList.get(i).findClosestDateRecord("sell", currentTime);
			key = recordSell.keySet().iterator().next();
			currencyTime = key;
			sellValue = recordSell.get(key);
			HashMap<String, Double> recordBuy = currencyList.get(i).findClosestDateRecord("buy", currentTime);
			key = recordBuy.keySet().iterator().next();
			buyValue = recordBuy.get(key);
			output += "Recent currency data from day: " + currencyTime + "\n";
			output += "Currency sell: " + sellValue + "\n";
			output += "Currency buy: " + buyValue + "\n";
			if (currencyList.get(i).isMaxValue("sell")) {
				output += "Currency has reached new maximum sell value!" + "\n";
			}
			if (currencyList.get(i).isMaxValue("buy")) {
				output += "Currency has reached new maximum buy value!" + "\n";
			}
			if (currencyList.get(i).isMinValue("sell")) {
				output += "Currency has reached new minimum sell value!" + "\n";
			}
			if (currencyList.get(i).isMinValue("buy")) {
				output += "Currency has reached new minimum buy value!" + "\n";
			}
			Set<String> sellTimeRange = currencyList.get(i).getSellValues().keySet();
			Set<String> buyTimeRange = currencyList.get(i).getBuyValues().keySet();
			int maxSellDays = 0;
			for (String sellTime : sellTimeRange) {
				Date iter_time = Currency.parseToDate(sellTime);
				long diff = Math.abs(currentTime.getTime() - iter_time.getTime());
				int diffDays = (int) diff / (24 * 60 * 60 * 1000);
				if (diffDays > maxSellDays) {
					maxSellDays = diffDays;
				}
			}
			int maxBuyDays = 0;
			for (String buyTime : buyTimeRange) {
				Date iter_time = Currency.parseToDate(buyTime);
				long diff = Math.abs(currentTime.getTime() - iter_time.getTime());
				int diffDays = (int) diff / (24 * 60 * 60 * 1000);
				if (diffDays > maxBuyDays) {
					maxBuyDays = diffDays;
				}
			}
			int[] days = Configuration.config.getDaysToCompare();

			for (int j = 0; j < days.length; j++) {
				if (currencyList.get(i).isMaxValue("sell", days[j])) {
					int daysToDisplay;
					if (days[j] > maxSellDays) {
						daysToDisplay = maxSellDays;
					} else {
						daysToDisplay = days[j];
					}
					output += "Currency has maximum sell value since " + daysToDisplay + " days" + "\n";
					break;
				}
			}

			for (int j = 0; j < days.length; j++) {
				if (currencyList.get(i).isMaxValue("buy", days[j])) {
					int daysToDisplay;
					if (days[j] > maxBuyDays) {
						daysToDisplay = maxBuyDays;
					} else {
						daysToDisplay = days[j];
					}
					output += "Currency has maximum buy value since " + daysToDisplay + " days" + "\n";
					break;
				}
			}

			for (int j = 0; j < days.length; j++) {
				if (currencyList.get(i).isMinValue("sell", days[j])) {
					int daysToDisplay;
					if (days[j] > maxSellDays) {
						daysToDisplay = maxSellDays;
					} else {
						daysToDisplay = days[j];
					}
					output += "Currency has minimum sell value since " + daysToDisplay + " days" + "\n";
					break;
				}

			}

			for (int j = 0; j < days.length; j++) {
				if (currencyList.get(i).isMinValue("buy", days[j])) {
					int daysToDisplay;
					if (days[j] > maxBuyDays) {
						daysToDisplay = maxBuyDays;
					} else {
						daysToDisplay = days[j];
					}
					output += "Currency has minimum buy value since " + daysToDisplay + " days" + "\n";
					break;
				}
			}
			output += "-----------------------------------" + "\n";
		}
		return output;
	}

	public static Date getBeginDate(int numberOfDays) {
		GregorianCalendar then = new GregorianCalendar();
		then.add(Calendar.DAY_OF_MONTH, -numberOfDays);
		return then.getTime();

	}

	public static ArrayList<String> prepareChart(ArrayList<Currency> listOfCurrencyToOperate) throws ParseException {
		ArrayList<String> chartFilesList = new ArrayList<>();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		for (Currency currency : listOfCurrencyToOperate) {
			HashMap<String, Double> buyValues = currency.getBuyValues();
			HashMap<String, Double> sellValues = currency.getSellValues();
			TimeSeries buyTimeSeries = new TimeSeries(currency.getCurrencyName() + " buy currency");
			for (String stringDate : buyValues.keySet()) {
				Date currencyDate = df.parse(stringDate);
				Double currencyValue = buyValues.get(stringDate);
				buyTimeSeries.add(new Second(currencyDate), currencyValue);
			}
			TimeSeriesCollection butTimeDataSet = new TimeSeriesCollection(buyTimeSeries);
			JFreeChart buyChart = ChartFactory.createTimeSeriesChart(currency.getCurrencyName() + " buy currency chart",
					"Time", "CurrencyValue", butTimeDataSet, false, false, false);
			BufferedImage buyImage = buyChart.createBufferedImage(500, 500);
			String buyFileName = "buy_" + currency.getCurrencyName() + "_chart.jpg";
			File outputBuyFileImage = new File(buyFileName);
			try {
				ImageIO.write(buyImage, "jpg", outputBuyFileImage);
				chartFilesList.add(buyFileName);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("Can't save " + buyFileName + " file");
				e.printStackTrace();
				logErrors(e);
			}
			TimeSeries sellTimeSeries = new TimeSeries(currency.getCurrencyName() + " sell currency");
			for (String stringDate : sellValues.keySet()) {
				Date currencyDate = df.parse(stringDate);
				Double currencyValue = sellValues.get(stringDate);
				sellTimeSeries.add(new Second(currencyDate), currencyValue);
			}
			TimeSeriesCollection sellTimeDataSet = new TimeSeriesCollection(sellTimeSeries);
			JFreeChart sellChart = ChartFactory.createTimeSeriesChart(
					currency.getCurrencyName() + " sell currency chart", "Time", "CurrencyValue", sellTimeDataSet,
					false, false, false);
			BufferedImage sellImage = sellChart.createBufferedImage(500, 500);
			String sellFileName = "sell_" + currency.getCurrencyName() + "_chart.jpg";
			File outputSellFileImage = new File(sellFileName);
			try {
				ImageIO.write(sellImage, "jpg", outputSellFileImage);
				chartFilesList.add(sellFileName);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("Can't save " + sellFileName + " file");
				e.printStackTrace();
				logErrors(e);
			}
		}
		return chartFilesList;
	}
	
	public static void logErrors(Exception error) {
		// TODO Auto-generated method stub
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date currDate = new Date();
		PrintStream errorStream;
		try {
			errorStream = new PrintStream(new FileOutputStream("error.log", true));
			errorStream.println(dateFormat.format(currDate));
			error.printStackTrace(errorStream);
			errorStream.println("----------");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("Can't read/write to error.log file. Please check the permissions.");
			e.printStackTrace();
		}
		
	}
	
	public static void logErrors(String error) {
		// TODO Auto-generated method stub
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date currDate = new Date();
		PrintStream errorStream;
		try {
			errorStream = new PrintStream(new FileOutputStream("error.log", true));
			errorStream.println(dateFormat.format(currDate));
			errorStream.println(error);
			errorStream.println("----------");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("Can't read/write to error.log file. Please check the permissions.");
			e.printStackTrace();
		}
		
	}

}
