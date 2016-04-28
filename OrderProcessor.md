# Threading-example
package processor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
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

public class OrdersProcessor {
	// public ArrayList<Double> ultimateSales=new ArrayList<Double>();
	double total = 0;

	private static String clientTot(String write, ArrayList<String> items,
			ArrayList<Double> pricesPerItem, ArrayList<ArrayList<Integer>> quant) {
		double totalSale = 0;
		double itemsTotal = 0;
		write += "***** Summary of all orders *****\n";
		for (int i = 0; i < items.size(); i++) {
			itemsTotal = pricesPerItem.get(i) * findTotSold(i, quant);
			write += "Summary - Item's name: "
					+ items.get(i)
					+ ", Cost per item: "
					+ NumberFormat.getCurrencyInstance().format(
							pricesPerItem.get(i)) + ", Number sold: "
					+ findTotSold(i, quant) + ", Item's Total: "
					+ NumberFormat.getCurrencyInstance().format(itemsTotal)
					+ "\n";
			totalSale += pricesPerItem.get(i) * findTotSold(i, quant);
		}
		write += "Summary Grand Total: "
				+ NumberFormat.getCurrencyInstance().format(totalSale);
		return write;
	}

	private static int findTotSold(int index,
			ArrayList<ArrayList<Integer>> quant) {
		// returns the total amt sold
		int total = 0;
		for (int i = 0; i < quant.size(); i++) {
			// from the first arrayList to the last
			total += quant.get(i).get(index);
		}
		return total;
	}

	public static void main(String[] args) {
		ArrayList<String> items;
		ArrayList<Double> pricesPerItem;
		ArrayList<ArrayList<Integer>> quant = new ArrayList<ArrayList<Integer>>();

		Scanner scan = new Scanner(System.in);
		System.out.println("Enter item's data file name: "); // itemsData.txt
		String fileName = scan.next();
		System.out
				.println("Enter 'y' for multiple threads, any other character otherwise: ");
		String multThread = scan.next();
		System.out.println("Enter number of orders to process: ");
		String numOrders = scan.next();
		int num = Integer.parseInt(numOrders);
		System.out.println("Enter order's base filename: ");// example
		String baseName = scan.next();
		System.out.println("Enter result's filename: ");// resultsExample.txt
		String resultName = scan.next();
		scan.close();
		Thread[] threads = new Thread[num];
		String writeInText = "";
		ArrayList<readData> objects = new ArrayList<readData>();
		if (multThread.compareTo("y") == 0) {
			// mult threads
			long startTime = System.currentTimeMillis();
			/* TASK YOU WANT TO TIME */
			for (int i = 0; i < num; i++) {
				// makes the readData obj in array
				readData obj = new readData(baseName, i + 1, fileName,
						resultName);
				objects.add(obj);
				threads[i] = new Thread(obj);
				threads[i].start();
			}
			for (int i = 0; i < num; i++) {
				try {
					// joins all threads
					threads[i].join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			items = objects.get(0).getItems();
			pricesPerItem = objects.get(0).getPricePerItem();
			for (int i = 0; i < num; i++) {
				writeInText += objects.get(i).writeInFile();
				quant.add(objects.get(i).getQuant());
			}

			writeInText = clientTot(writeInText, items, pricesPerItem, quant);
			long endTime = System.currentTimeMillis();
			System.out.println("Processing time (msec): "
					+ (endTime - startTime));
		} else {
			// only create one thread
			long startTime = System.currentTimeMillis();
			readData one = null;
			for (int i = 0; i < num; i++) {
				one = new readData(baseName, i + 1, fileName, resultName);
				one.run();
				items = one.getItems();
				writeInText += one.writeInFile();
				quant.add(one.getQuant());
			}
			items = one.getItems();
			pricesPerItem = one.getPricePerItem();// 3223

			writeInText = clientTot(writeInText, items, pricesPerItem, quant);
			long endTime = System.currentTimeMillis();
			System.out.println("Processing time (msec): "
					+ (endTime - startTime));
		}

		// writing in file;
		File newFile = new File(resultName);// path to write the file
		if (newFile.exists()) {
			newFile.delete();
			try {
				BufferedWriter writer = new BufferedWriter(new FileWriter(
						resultName));
				writer.write(writeInText);
				// do stuff
				writer.close();
			} catch (IOException e) {
				// this says file doesnt exist
			}
		} else {
			// path doesnt exist create new file
			try {
				newFile.createNewFile();
			} catch (Exception e) {
				// this means the file exists but i already checked for it
			}
			try {
				BufferedWriter writer = new BufferedWriter(new FileWriter(
						resultName));
				writer.write(writeInText);
				writer.close();
			} catch (IOException e) {
				// this says file doesnt exist
			}
		}
	}
}
