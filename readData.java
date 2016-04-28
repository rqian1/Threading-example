package processor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Scanner;

public class readData implements Runnable {
	// tree map???
	private String base;
	private ArrayList<String> items;
	private ArrayList<Double> pricesPerItem;
	private ArrayList<Double> totalPricePer;
	private ArrayList<Integer> quant;
	private int num, ID, numItems;
	private double total;
	private String list;
	private String writeInto;

	public readData(String baseName, int numIn, String listOfItems, String into) {
		base = baseName;
		num = numIn;
		list = listOfItems;
		items = new ArrayList<String>();
		pricesPerItem = new ArrayList<Double>();
		totalPricePer = new ArrayList<Double>();
		quant = new ArrayList<Integer>();
		writeInto = into;
	}

	protected ArrayList<Double> getPricePerItem() {
		return pricesPerItem;
	}

	protected ArrayList<Integer> getQuant() {
		return quant;
	}

	protected ArrayList<String> getItems() {
		return items;
	}

	protected double getTotal() {
		return total;
	}

	public String writeInFile() {
		// making the string summary
		String write;
		double tot = 0;
		write = "----- Order details for client with Id: " + ID + " -----\n";// 1003,1001,1002
		for (int i = 0; i < numItems; i++) {
			if (quant.get(i) == 0) {
				// this item wasnt sold, skip
			} else {
				write += "Item's name: "
						+ items.get(i)
						+ ", Cost per item: "
						+ NumberFormat.getCurrencyInstance().format(
								pricesPerItem.get(i))
						+ ", Quantity: "
						+ quant.get(i)
						+ ", Cost: "
						+ NumberFormat.getCurrencyInstance().format(
								totalPricePer.get(i)) + "\n";
				tot += totalPricePer.get(i);
			}
		}
		total = tot;
		write += "Order Total: "
				+ NumberFormat.getCurrencyInstance().format(tot) + "\n";
		// System.out.print("write is\n"+write);
		return write;
	}

	public void calcTotals(String item) {
		for (int i = 0; i < numItems; i++) {
			if (item.compareTo(items.get(i)) == 0) {
				// at this index i, this is the same item
				double price = pricesPerItem.get(i);
				double newPrice = totalPricePer.get(i) + price;
				totalPricePer.set(i, newPrice);
				quant.set(i, quant.get(i) + 1);
			}
		}

	}

	public void setTotalPricesAndQuant() {
		// sets arraylists to 0
		int length = pricesPerItem.size();
		for (int i = 0; i < length; i++) {
			totalPricePer.add((double) 0);
			quant.add(0);
		}
	}

	public void readItemsList() throws Exception {
		// reads from the text file
		FileReader file = new FileReader(list);
		BufferedReader reader2 = new BufferedReader(file);
		String line = reader2.readLine();
		while (line != null) {
			if (items.size() == 0) {
				// first item in the list
				items.add(itemPart(line));// this returns what item
				pricesPerItem.add(price(line));
				line = reader2.readLine();
			} else {
				// need to alphabetize
				for (int i = 0; i < (items.size()); i++) {
					if (itemPart(line).compareTo(items.get(i)) < 0) {
						// insert at this index
						items.add(i, itemPart(line));// this returns what item
						pricesPerItem.add(i, price(line));
						break;
					}
				}
				if (itemPart(line).compareTo(items.get(items.size() - 1)) > 0) {
					// last item in list
					items.add(itemPart(line));// this returns what item
					pricesPerItem.add(price(line));
					line = reader2.readLine();
				}
				line = reader2.readLine();
			}
		}
		reader2.close();
	}

	public String itemPart(String line) {
		// returns the item part of the string
		int space = line.lastIndexOf(" ");
		String important = line.substring(0, space);
		// System.out.println("item in this line is "+important);
		return important;
	}

	public double price(String line) {
		// returns the price part of the string
		int space = line.lastIndexOf(" ");
		String important = line.substring(space);
		important = important.trim();
		// System.out.println("price of item is "+important);
		return Double.parseDouble(important);
	}

	@Override
	public void run() {
		try {
			FileReader file = new FileReader(base + "" + num + ".txt");
			BufferedReader reader = new BufferedReader(file);
			String line = reader.readLine();

			// this gets the ID number
			ID = 1000 + num;

			System.out.println("Reading order for client with id: " + ID);

			// sets up arraylists for items and prices per item
			readItemsList();
			setTotalPricesAndQuant();

			// figuring out how many items there are
			numItems = items.size();
			// System.out.println("this is the number of items"+numItems);
			line = reader.readLine();
			while (line != null) {
				String item = itemPart(line);// this returns what item
				calcTotals(item);
				line = reader.readLine();
			}
			reader.close();
			writeInFile();

		} catch (Exception e) {
			System.out.println("exception thrown in run");
		}
	}
}
