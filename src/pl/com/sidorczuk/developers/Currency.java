package pl.com.sidorczuk.developers;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;
import java.util.StringTokenizer;

public class Currency {

	private HashMap<String, Double> sellValues;
	private HashMap<String, Double> buyValues;
	private String name;

	public Currency(String name) throws Exception {
		this.name = name;
		sellValues = new HashMap<>();
		buyValues = new HashMap<>();
		this.setCurrencyValues();
	}

	public void setCurrencyValues() throws Exception {
		Scanner scan = new Scanner(new File("collected_data.csv"));
		recordLoop: while (scan.hasNextLine()) {
			String record = scan.nextLine();
			StringTokenizer tokenizer = new StringTokenizer(record, ";");
			int tokenCounter = 0;
			ArrayList<String> recordList = new ArrayList<>();
			while (tokenizer.hasMoreTokens()) {
				String token = tokenizer.nextToken();
				tokenCounter++;
				if (tokenCounter == 1) {
					if (!(token.equals(name))) {
						continue recordLoop;
					}
				}
				recordList.add(token);
			}
			if (recordList.size() != 4) {
				scan.close();
				throw new StructureException("Wrong structure of csv file");
			}
			String date = recordList.get(1);
			buyValues.put(date, Double.parseDouble(recordList.get(2)));
			sellValues.put(date, Double.parseDouble(recordList.get(3)));
		}
		scan.close();
	}

	public HashMap<String, Double> getSellValues() {
		return this.sellValues;
	}

	public HashMap<String, Double> getBuyValues() {
		return this.buyValues;
	}

	public String getCurrencyName() {
		return this.name;
	}

	public static Date parseToDate(String date) throws ParseException {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return df.parse(date);
	}

	public HashMap<String, Double> getMinValue(String type, Date dateStart, Date dateEnd) throws Exception {
		HashMap<String, Double> values;
		if (type.equals("buy")) {
			values = this.getBuyValues();
		} else if (type.equals("sell")) {
			values = this.getSellValues();
		} else {
			throw new Exception("Wrong first argument. Try 'buy' or 'sell'");
		}
		HashMap<String, Double> valuesWithSpecificTime = new HashMap<>();
		for (String key : values.keySet()) {
			Date currencyTime = parseToDate(key);
			if (currencyTime.compareTo(dateStart) >= 0 && currencyTime.compareTo(dateEnd) < 0) {
				valuesWithSpecificTime.put(key, values.get(key));
			}
		}
		HashMap<String, Double> minValue = new HashMap<>();
		for (String key : valuesWithSpecificTime.keySet()) {
			Double value = valuesWithSpecificTime.get(key);
			if (minValue.isEmpty()) {
				minValue.put(key, value);
				continue;
			}
			for (String minValuekey : minValue.keySet()) {
				Double minValueVal = minValue.get(minValuekey);
				if (value < minValueVal) {
					minValue.clear();
					minValue.put(key, value);
				}
			}
		}
		return minValue;
	}

	public HashMap<String, Double> getMinValue(String type) throws Exception {
		HashMap<String, Double> values;
		if (type.equals("buy")) {
			values = this.getBuyValues();
		} else if (type.equals("sell")) {
			values = this.getSellValues();
		} else {
			throw new Exception("Wrong first argument. Try 'buy' or 'sell'");
		}

		HashMap<String, Double> minValue = new HashMap<>();
		for (String key : values.keySet()) {
			Double value = values.get(key);
			if (minValue.isEmpty()) {
				minValue.put(key, value);
				continue;
			}
			for (String minValuekey : minValue.keySet()) {
				Double minValueVal = minValue.get(minValuekey);
				if (value < minValueVal) {
					minValue.clear();
					minValue.put(key, value);
				}
			}
		}
		return minValue;
	}

	public HashMap<String, Double> getMaxValue(String type, Date dateStart, Date dateEnd) throws Exception {
		HashMap<String, Double> values;
		if (type.equals("buy")) {
			values = this.getBuyValues();
		} else if (type.equals("sell")) {
			values = this.getSellValues();
		} else {
			throw new Exception("Wrong first argument. Try 'buy' or 'sell'");
		}
		HashMap<String, Double> valuesWithSpecificTime = new HashMap<>();
		for (String key : values.keySet()) {
			Date currencyTime = parseToDate(key);
			if (currencyTime.compareTo(dateStart) >= 0 && currencyTime.compareTo(dateEnd) < 0) {
				valuesWithSpecificTime.put(key, values.get(key));
			}
		}
		HashMap<String, Double> maxValue = new HashMap<>();
		for (String key : valuesWithSpecificTime.keySet()) {
			Double value = valuesWithSpecificTime.get(key);
			if (maxValue.isEmpty()) {
				maxValue.put(key, value);
				continue;
			}
			for (String maxValuekey : maxValue.keySet()) {
				Double maxValueVal = maxValue.get(maxValuekey);
				if (value > maxValueVal) {
					maxValue.clear();
					maxValue.put(key, value);
				}
			}
		}
		return maxValue;
	}

	public HashMap<String, Double> getMaxValue(String type) throws Exception {
		HashMap<String, Double> values;
		if (type.equals("buy")) {
			values = this.getBuyValues();
		} else if (type.equals("sell")) {
			values = this.getSellValues();
		} else {
			throw new Exception("Wrong first argument. Try 'buy' or 'sell'");
		}

		HashMap<String, Double> maxValue = new HashMap<>();
		for (String key : values.keySet()) {
			Double value = values.get(key);
			if (maxValue.isEmpty()) {
				maxValue.put(key, value);
				continue;
			}
			for (String maxValuekey : maxValue.keySet()) {
				Double maxValueVal = maxValue.get(maxValuekey);
				if (value > maxValueVal) {
					maxValue.clear();
					maxValue.put(key, value);
				}
			}
		}
		return maxValue;
	}

	public boolean isMaxValue(String type) throws Exception {
		Date currentTime = new Date();
		HashMap<String, Double> closestRecord = this.findClosestDateRecord(type, currentTime);
		double closestValue = closestRecord.get(closestRecord.keySet().iterator().next());
		HashMap<String, Double> maxValueFromRecord = this.getMaxValue(type);
		double maxValue = maxValueFromRecord.get(maxValueFromRecord.keySet().iterator().next());
		if (closestValue >= maxValue) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isMaxValue(String type, int days) throws Exception {
		Date before = CurrencyMonitor.getBeginDate(days);
		Date currentTime = new Date();
		HashMap<String, Double> closestRecord = this.findClosestDateRecord(type, currentTime);
		double closestValue = closestRecord.get(closestRecord.keySet().iterator().next());
		HashMap<String, Double> maxValueFromRecord = this.getMaxValue(type, before, currentTime);
		double maxValue = maxValueFromRecord.get(maxValueFromRecord.keySet().iterator().next());
		if (closestValue >= maxValue) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isMinValue(String type) throws Exception {
		Date currentTime = new Date();
		HashMap<String, Double> closestRecord = this.findClosestDateRecord(type, currentTime);
		double closestValue = closestRecord.get(closestRecord.keySet().iterator().next());
		HashMap<String, Double> minValueFromRecord = this.getMinValue(type);
		double minValue = minValueFromRecord.get(minValueFromRecord.keySet().iterator().next());
		if (closestValue <= minValue) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isMinValue(String type, int days) throws Exception {
		Date before = CurrencyMonitor.getBeginDate(days);
		Date currentTime = new Date();
		HashMap<String, Double> closestRecord = this.findClosestDateRecord(type, currentTime);
		double closestValue = closestRecord.get(closestRecord.keySet().iterator().next());
		HashMap<String, Double> minValueFromRecord = this.getMinValue(type, before, currentTime);
		double minValue = minValueFromRecord.get(minValueFromRecord.keySet().iterator().next());
		if (closestValue <= minValue) {
			return true;
		} else {
			return false;
		}
	}

	public HashMap<String, Double> findClosestDateRecord(String type, Date currentTime) throws Exception {
		long dateDiff = 0;
		HashMap<String, Double> returnRecord = new HashMap<>();
		int counter = 0;
		HashMap<String, Double> ValuesSet;
		if (type.toLowerCase().trim().equals("sell")) {
			ValuesSet = this.getSellValues();
		} else if (type.toLowerCase().trim().equals("buy")) {
			ValuesSet = this.getBuyValues();
		} else {
			throw new Exception("Wrong argument. Try put 'sell' or 'buy'");
		}
		for (String date : ValuesSet.keySet()) {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date iterDate = df.parse(date);
			long tempDateDiff = currentTime.getTime() - iterDate.getTime();
			if (counter == 0) {
				dateDiff = tempDateDiff;
				returnRecord.put(date, ValuesSet.get(date));
			}
			if (tempDateDiff < dateDiff) {
				dateDiff = tempDateDiff;
				returnRecord.clear();
				returnRecord.put(date, ValuesSet.get(date));
			}
			counter++;
		}
		return returnRecord;
	}

}
