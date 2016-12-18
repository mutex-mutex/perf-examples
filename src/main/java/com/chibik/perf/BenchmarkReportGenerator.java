package com.chibik.perf;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.CMYKColor;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.results.RunResult;

import java.io.FileOutputStream;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static java.lang.String.valueOf;
import static java.util.stream.Collectors.toList;

public class BenchmarkReportGenerator {

    private final String fileName;
    private final Collection<RunResult> runResults;

    private Font tableFont = FontFactory.getFont(
            FontFactory.COURIER, 8, Font.NORMAL,	new CMYKColor(0, 0, 0, 255)
    );
    private Font chapterFont = FontFactory.getFont(
            FontFactory.HELVETICA, 14, Font.BOLD,	new CMYKColor(0, 0, 0, 255)
    );

    public BenchmarkReportGenerator(String fileName, Collection<RunResult> runResults) {
        this.fileName = fileName;
        this.runResults = new ArrayList<>(runResults);
    }

    public void build() {
        try {

            Document document = new Document(new RectangleReadOnly(1100F, 3000F), 20, 20, 20, 20);
            PdfWriter.getInstance(document, new FileOutputStream(fileName));
            document.open();
            document.newPage();

            Map<String, List<RunResult>> aggregatedByClassName =
                    runResults.stream().collect(
                            Collectors.groupingBy(
                                    x -> x.getParams().getBenchmark().substring(0, x.getParams().getBenchmark().lastIndexOf(".")),
                                    toList()
                            )
                    );

            int counter = 0;

            for(Map.Entry<String, List<RunResult>> entry : aggregatedByClassName.entrySet()) {

                document.add(new Paragraph((counter++) + "." + entry.getKey(), chapterFont));

                RunResult first = entry.getValue().get(0);
                List<String> parameterKeys = new ArrayList<>(first.getParams().getParamsKeys());
                boolean usedBatch = first.getParams().getMeasurement().getBatchSize() > 1;

                int columns = 3 + parameterKeys.size() + (usedBatch ? 2 : 0);
                float[] columnWidth = new float[columns];
                columnWidth[0] = 200f;

                float parametersWidthTotal = 600f;
                int parameterStartIndex = 1;
                float parameterColumnWidth = Math.min(parametersWidthTotal/parameterKeys.size(), 110f);

                for(int i = parameterStartIndex; i < parameterStartIndex + parameterKeys.size(); i++) {
                    columnWidth[i] = parameterColumnWidth;
                }

                for(int i = parameterStartIndex + parameterKeys.size(); i < columns; i++) {
                    columnWidth[i] = 90f;
                }

                PdfPTable table = new PdfPTable(columns);
                table.setTotalWidth(columnWidth);
                table.setLockedWidth(true);
                table.addCell(new PdfPCell(new Phrase("Benchmark", tableFont)));
                for(String param : parameterKeys) {
                    table.addCell(new PdfPCell(new Phrase(param, tableFont)));
                }
                if(usedBatch) {
                    table.addCell(new PdfPCell(new Phrase("Score(total)", tableFont)));
                    table.addCell(new PdfPCell(new Phrase("Score unit", tableFont)));
                    table.addCell(new PdfPCell(new Phrase("Per op(per call)", tableFont)));
                    table.addCell(new PdfPCell(new Phrase("Per op unit", tableFont)));
                } else {
                    table.addCell(new PdfPCell(new Phrase("Score", tableFont)));
                    table.addCell(new PdfPCell(new Phrase("Score unit", tableFont)));
                }
                for(RunResult result : entry.getValue()) {
                    table.addCell(new PdfPCell(
                            new Phrase(result.getParams().getBenchmark().replace("com.chibik.perf.", ""), tableFont)
                    ));

                    for(String paramKey : parameterKeys) {
                        table.addCell(new PdfPCell(
                                new Phrase(result.getParams().getParam(paramKey), tableFont)
                        ));
                    }

                    table.addCell(new PdfPCell(
                            new Phrase(String.format("%.2f", result.getPrimaryResult().getScore()), tableFont)
                    ));
                    table.addCell(new PdfPCell(
                            new Phrase(result.getPrimaryResult().getScoreUnit(), tableFont)
                    ));
                    if(usedBatch) {
                        double score = result.getPrimaryResult().getScore();
                        int batchSize = result.getParams().getMeasurement().getBatchSize();

                        table.addCell(new PdfPCell(
                                new Phrase(String.format("%.2f", 1.0*result.getPrimaryResult().getScore()/batchSize), tableFont)
                        ));
                        table.addCell(new PdfPCell(
                                new Phrase(result.getPrimaryResult().getScoreUnit(), tableFont)
                        ));
                    }
                }

                table.setHorizontalAlignment(Element.ALIGN_LEFT);
                document.add(table);
            }

            document.close();
        } catch (Exception e) {

            throw new RuntimeException("Error while generating pdf report", e);
        }
    }
}
