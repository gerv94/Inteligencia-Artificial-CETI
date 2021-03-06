package mx.ia.controllers;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Vector;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import mx.ia.graph.Edge;
import mx.ia.graph.Graph;
import mx.ia.graph.Vertex;

public class PoiController {
	private Workbook wb = null;
	private FileOutputStream fileOut = null;
	private Sheet sheet = null;

	public void writeTo(String workbook, String sheet) {
		try {
			fileOut = new FileOutputStream(workbook);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		wb = new XSSFWorkbook();
		this.sheet = wb.createSheet(sheet);
	}

	public void save() throws IOException {
		wb.write(fileOut);
		fileOut.close();
		wb = null;
		fileOut = null;
		sheet = null;
	}

	public void putHeaders(Vertex[] headers) {
		Row rowIata = sheet.createRow(0);// for iataCodes
		Row rowName = sheet.createRow(1);// for names
		Row rowLati = sheet.createRow(2);// for latitude
		Row rowLong = sheet.createRow(3);// for longitude
		Cell cellIata;
		Cell cellName;
		Cell cellLati;
		Cell cellLong;

		for (int i = 0; i < headers.length; i++) {
			cellIata = rowIata.createCell(i);
			cellName = rowName.createCell(i);
			cellLati = rowLati.createCell(i);
			cellLong = rowLong.createCell(i);

			cellIata.setCellValue(headers[i].getCode());
			cellName.setCellValue(headers[i].getName());
			cellLati.setCellValue(headers[i].getPositionY());
			cellLong.setCellValue(headers[i].getPositionX());
		}
	}

	public void putContent(double[][] content) {
		double prom;
		for (int i = 0; i < content.length; i++) {
			for (int j = 0; j < content[i].length; j++) {
				prom = 0;
				if (content[i][j] > 0 && content[j][i] > 0) {
					prom = (content[i][j] + content[j][i]) / 2;
				} else if (content[i][j] > 0 && content[j][i] <= 0) {
					prom = content[i][j] / 2;
				} else if (content[j][i] > 0 && content[i][j] <= 0) {
					prom = content[j][i] / 2;
				}
				content[i][j] = content[j][i] = prom;
			}
		}

		Row row;
		Cell cell;

		for (int i = 0; i < content.length; i++) {
			row = sheet.createRow(i + 4);
			for (int j = 0; j < content[i].length; j++) {
				cell = row.createCell(j);
				cell.setCellValue((Double) (Double.isInfinite(content[i][j]) ? -1 : content[i][j]));
			}
		}
	}

	public Graph readGraph(String file) {
		Row row;
		Row rowName;// for names
		Row positionY;// for positionY
		Row positionX;// for positionX

		Cell cell;
		Cell cellName;
		Cell cellLati;
		Cell cellLong;

		Sheet sheet = null;
		Vector<Vertex> vectorV = new Vector<Vertex>();
		Vector<Edge> vectorE = new Vector<Edge>();
		System.out.println("Reading graph - started");

		try {
			wb = new XSSFWorkbook(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		sheet = wb.getSheetAt(0);
		row = sheet.getRow(0);// for codes
		rowName = sheet.getRow(1);// for names
		positionY = sheet.getRow(2);// for positionY
		positionX = sheet.getRow(3);// for positionX

		for (int i = 0; row.getCell(i) != null; i++) {
			cell = row.getCell(i);
			cellName = rowName.getCell(i);
			cellLati = positionY.getCell(i);
			cellLong = positionX.getCell(i);
			vectorV.add(new Vertex(cell.getStringCellValue(), cellName.getStringCellValue(),
					cellLati.getNumericCellValue(), cellLong.getNumericCellValue()));
		}

		for (int i = 4; sheet.getRow(i) != null; i++) {
			row = sheet.getRow(i);
			for (int j = 0; row.getCell(j) != null; j++) {
				cell = row.getCell(j);
				if (row.getCell(j).getNumericCellValue() >= 0)
					vectorE.add(new Edge(vectorV.get(i - 4).getCode(), vectorV.get(j).getCode(),
							cell.getNumericCellValue()));
			}
		}
		System.out.println("Reading graph - finished");
		Graph graph = new Graph(vectorV, vectorE);
		System.out.println(graph.toString());
		return graph;
	}

}
