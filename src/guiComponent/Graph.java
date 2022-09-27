package guiComponent;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.awt.geom.Ellipse2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.DefaultDrawingSupplier;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;

import preprocess.Split;

public class Graph extends JScrollPane {
	private static final long serialVersionUID = -2566210811220596765L;
	private Logger logger = Logger.getLogger("Graph");
	private JFreeChart chart;
	private String topic = null; // �Ϫ����D
	private ArrayList<String> datalabel = new ArrayList<String>(); // ����
	private ArrayList<String> num = new ArrayList<String>(); // �ƶq
	private int dataStart = 0;
	private int dataFinal = 0;
	private String xname = "";
	
	public void update(File file, String graph,int x1,int x2) {
		datalabel.clear();
		num.clear();
		dataStart = x1;
		dataFinal = x2;
		try {
			setViewportView(createDemoPanel(file, graph));
		} catch (IOException e) {
			logger.info(e.getMessage());
		}
		repaint();
	}
	public void clear() {
		setViewportView(null);
	}
	public JPanel createDemoPanel(File file, String graph) throws IOException {
		JFreeChart chart = createChart(file, graph);
		return new ChartPanel(chart);
	}
	// ����
	private PieDataset createDataset1(File file) throws IOException {
		DefaultPieDataset dataset = new DefaultPieDataset();
		ArrayList<String> inputSplit;
		String line;
		int labelname = 0;
		String[] title;
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String fileName = file.getName();
			topic = fileName.substring(0, fileName.length() - 4);	//.csv����
			line = br.readLine();
			title = line.split(","); // �p�⦳�h�֭Ӽ��D

			for (int i = 0, length = title.length; i < length; i++) {
				if (title[i].equals("�����ɶ�")) {
					xname = "�����ɶ�";
					labelname = i;
				} else if (title[i].equals("�H����")) {
					xname = "�H����";
					labelname = i;
				}
			}
			String dept = null;
			int cnt = 1;
			if (topic.contains("�U����v�O����N�����ɶ�")) {		//�p�G���Ѧ�����d�򪺸ܡA�N�|�X���A�ݭn�Q��k
				while ((line = br.readLine()) != null) {
					inputSplit = Split.withQuotes(line);
					
					if (!inputSplit.get(0).isEmpty()) {		//���ǯʺ|��O
						dept = inputSplit.get(0);
					}
					if(dataStart == 0 && dataFinal == 0) {
						for (int i = 0; i < inputSplit.size(); i++) {
							datalabel.add(dept + " " + inputSplit.get(1));
							num.add(inputSplit.get(labelname));
						}
					}
					if(dataStart!=0 && (cnt >= dataStart) && (cnt <= dataFinal)) {
						for (int i = 0; i < inputSplit.size(); i++) {
							datalabel.add(dept + " " + inputSplit.get(1));
							num.add(inputSplit.get(labelname));
						}
					}
					cnt++;
				}
			} else {
				if(dataStart == 0 && dataFinal == 0) {			//���`�������
					while ((line = br.readLine()) != null) {
						inputSplit = Split.withQuotes(line);
						for (int i = 0; i < inputSplit.size(); i++) {
							datalabel.add(inputSplit.get(0));
							num.add(inputSplit.get(labelname));
						}
					}
				}else {											//���ӳ]�w�������
					while ((line = br.readLine()) != null) {
						if ((cnt >= dataStart) && (cnt <= dataFinal)) { // �ھڰ_�l�P�̲צ�m�N��Ƽg�J
							inputSplit = Split.withQuotes(line);
							for (int i = 0; i < inputSplit.size(); i++) {
								datalabel.add(inputSplit.get(0));
								num.add(inputSplit.get(labelname));
							}
						}
						cnt++;
					}
				}
			}
			for (int i = 0; i < datalabel.size(); i++) {
				dataset.setValue(datalabel.get(i), Double.valueOf(num.get(i)));
			}
		}
		return dataset;
	}

	// �����ϡB��u��
	private DefaultCategoryDataset createDataset2(File file) throws IOException {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		ArrayList<String> inputSplit;
		String line;
		int labelname = 0;
		String[] title;
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String fileName = file.getName();
			topic = fileName.substring(0, fileName.length() - 4);
			line = br.readLine();
			title = line.split(","); // �p�⦳�h�֭Ӽ��D

			for (int i = 0, length = title.length; i < length; i++) {
				if (title[i].equals("�����ɶ�")) {
					xname = "�����ɶ�";
					labelname = i;
				} else if (title[i].equals("�H����")) {
					xname = "�H����";
					labelname = i;
				}
			}
			String dept = null;
			int cnt = 1;
			if (topic.contains("�U����v�O����N�����ɶ�")) {
				while ((line = br.readLine()) != null) {
					inputSplit = Split.withQuotes(line);
					
					if (!inputSplit.get(0).isEmpty()) {		//���ǯʺ|��O
						dept = inputSplit.get(0);
					}
					if(dataStart == 0 && dataFinal == 0) {
						for (int i = 0; i < inputSplit.size(); i++) {
							datalabel.add(dept + " " + inputSplit.get(1));
							num.add(inputSplit.get(labelname));
						}
					}
					if(dataStart!=0 && (cnt >= dataStart) && (cnt <= dataFinal)) {
						for (int i = 0; i < inputSplit.size(); i++) {
							datalabel.add(dept + " " + inputSplit.get(1));
							num.add(inputSplit.get(labelname));
						}
					}
					cnt++;
				}
			} else {
				if(dataStart == 0 && dataFinal == 0) {			//���`�������
					while ((line = br.readLine()) != null) {
						inputSplit = Split.withQuotes(line);
						for (int i = 0; i < inputSplit.size(); i++) {
							datalabel.add(inputSplit.get(0));
							num.add(inputSplit.get(labelname));
						}
					}
				}else {											//���ӳ]�w�������
					while ((line = br.readLine()) != null) {
						if ((cnt >= dataStart) && (cnt <= dataFinal)) { // �ھڰ_�l�P�̲צ�m�N��Ƽg�J
							inputSplit = Split.withQuotes(line);
							for (int i = 0; i < inputSplit.size(); i++) {
								datalabel.add(inputSplit.get(0));
								num.add(inputSplit.get(labelname));
							}
						}
						cnt++;
					}
				}
			}
			for (int i = 0; i < datalabel.size(); i++) {
				dataset.addValue(Double.valueOf(num.get(i)), title[labelname], datalabel.get(i));
			}
		}
		return dataset;
	}

	private JFreeChart createChart(File file, String graph) throws IOException {
		// �إߥD�D�˦�
		StandardChartTheme standardChartTheme = new StandardChartTheme("CN");
		standardChartTheme.setRegularFont(new Font("�L�n������", Font.PLAIN, 15)); // �]�w�ϨҪ��r��
		ChartFactory.setChartTheme(standardChartTheme);
		// �����ϩΧ�u�Ϫ��C��հt
//		GradientPaint gradientpaint = new GradientPaint(0.0F, 0.0F, new Color(5, 108, 242), 0.0F, 0.0F,new Color(3, 49, 140));
		GradientPaint gradientpaint = new GradientPaint(0.0F, 0.0F, new Color(220, 230, 255), 0.0F, 10.0F,new Color(76,80,156));     

		if (graph.equals("����")) {
			PieDataset dataset = createDataset1(file);
			chart = ChartFactory.createPieChart(topic, dataset, true, true, false);
			PiePlot localPiePlot = (PiePlot) chart.getPlot(); // ���ø�ϰϪ���
			localPiePlot.setLabelBackgroundPaint(new Color(220, 230, 255));
			localPiePlot.setDrawingSupplier(new ChartDrawingSupplier());		//�ϥΦۭq�C��
			localPiePlot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0} ({2}) ",
					NumberFormat.getNumberInstance(), new DecimalFormat("0.00%")));
			localPiePlot.setCircular(true);
			localPiePlot.setBackgroundPaint(Color.WHITE);
		} else if (graph.equals("������")) {
			CategoryDataset dataset = createDataset2(file);
			chart = ChartFactory.createBarChart(topic, "", xname, dataset, PlotOrientation.VERTICAL, false, true, false);

			CategoryPlot categoryplot = (CategoryPlot) chart.getPlot();
			NumberAxis numberaxis = (NumberAxis) categoryplot.getRangeAxis();
			numberaxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
			BarRenderer barrenderer = (BarRenderer) categoryplot.getRenderer();
			barrenderer.setDrawBarOutline(false);
			barrenderer.setItemMargin(0.0);
			barrenderer.setSeriesPaint(0, gradientpaint);
			
			CategoryPlot plot = chart.getCategoryPlot();
			plot.getRangeAxis().setLabelFont(new Font("�L�n������", Font.PLAIN, 20)); // y�b���D
			plot.setBackgroundPaint(Color.WHITE);
			plot.setRangeGridlinePaint(Color.BLACK);
			plot.setRangeGridlineStroke(new BasicStroke(0));
			CategoryAxis axisDomain = plot.getDomainAxis();
			ValueAxis axisRange = plot.getRangeAxis();

			Font font1 = new Font("�L�n������", Font.PLAIN, 11);
			axisDomain.setTickLabelFont(font1); // x�b
			axisRange.setTickLabelFont(font1); // y�b
		} else {
			CategoryDataset dataset = createDataset2(file);
			chart = ChartFactory.createLineChart(topic, "", xname, dataset, PlotOrientation.VERTICAL, false, true, false);

			CategoryPlot plot = chart.getCategoryPlot();
			LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
			renderer.setBaseShapesVisible(true); 								// series �I�]�Y����I�^�i��
			renderer.setBaseLinesVisible(true); 								// series �I�]�Y����I�^�����s�u�i��
			renderer.setSeriesShape(0, new Ellipse2D.Double(-4d, -4d, 8d, 8d));	//�Ϊ�
			renderer.setUseSeriesOffset(true); 									// �]�w�����q
			renderer.setSeriesStroke(0, new BasicStroke(3));					//�u���ʲ�
			renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
			renderer.setBaseItemLabelsVisible(true);
			renderer.setSeriesPaint(0, gradientpaint);

			plot.getRangeAxis().setLabelFont(new Font("�L�n������", Font.PLAIN, 20)); // y�b���D
			plot.setBackgroundPaint(Color.WHITE);
			plot.setRangeGridlinePaint(Color.BLACK);
			plot.setRangeGridlineStroke(new BasicStroke(0));
			CategoryAxis axisDomain = plot.getDomainAxis();
			ValueAxis axisRange = plot.getRangeAxis();
			Font font1 = new Font("�L�n������", Font.PLAIN, 11);
			axisDomain.setTickLabelFont(font1); // x�b
			axisRange.setTickLabelFont(font1); // y�b
		}
		chart.getTitle().setFont(new Font("�L�n������", Font.PLAIN, 30)); // ������D�r���˦�
		return chart;
	}
	
	private class ChartDrawingSupplier extends DefaultDrawingSupplier  {
		private static final long serialVersionUID = 6291362807061539914L;
	    public int paintIndex;
	    
	    public int fillPaintIndex;
	    public Paint[]  paintSequence = new Paint[]{
	    		//����		
				Color.decode("#EA5783"),Color.decode("#EB6C93"),Color.decode("#ED83A2"),
				Color.decode("#EF97B2"),Color.decode("#F1ACC2"),Color.decode("#F4C1D1"),
		    	//��
		    	Color.decode("#DE3837"),Color.decode("#e84242"),Color.decode("#FF5151"),
		    	Color.decode("#FF7575"),Color.decode("#FF9797"),Color.decode("#FFB5B5"),
				//��    
				Color.decode("#ff902e"),Color.decode("#ff9e49"),Color.decode("#ffb06b"),
				Color.decode("#ffc08a"),Color.decode("#ffcfa5"),
				//��
				Color.decode("#ffba00"),Color.decode("#ffc62b"),Color.decode("#fed35e"),
				Color.decode("#ffde84"),Color.decode("#ffeab0"),Color.decode("#FDF6C0"),
				//��
		    	Color.decode("#57b145"),Color.decode("#66cc51"),Color.decode("#74ea5c"),
		    	Color.decode("#7bfa62"),Color.decode("#a7fe97"),Color.decode("#c5f9bc"),   		
		    	//��
		    	Color.decode("#3a75c4"),Color.decode("#4487e1"),Color.decode("#4b98ff"),
		    	Color.decode("#66a6fc"),Color.decode("#83b8ff"),Color.decode("#a6ccfe"),
		    	Color.decode("#3a408e"),Color.decode("#484fae"),Color.decode("#5b63d3"),
		    	Color.decode("#6a73ef"),Color.decode("#8890fe"),Color.decode("#a8aefe"),
		    	Color.decode("#c3c7fd"),
		    	//��
		    	Color.decode("#a84afd"),Color.decode("#B15BFF"),Color.decode("#BE77FF"),
		    	Color.decode("#CA8EFF"),Color.decode("#d9b8f1"),Color.decode("#f1b8e4"),
		    	
	    };
	    public Paint getNextPaint() {
	        Paint result = paintSequence[paintIndex % paintSequence.length];
	        paintIndex++;
	        return result;
	    }
	    public Paint getNextFillPaint() {
	        Paint result = paintSequence[fillPaintIndex % paintSequence.length];
	        fillPaintIndex++;
	        return result;
	    }   
	}
}