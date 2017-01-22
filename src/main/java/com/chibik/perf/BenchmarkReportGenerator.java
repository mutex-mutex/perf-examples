package com.chibik.perf;

import com.chibik.perf.util.Comment;
import com.chibik.perf.util.Included;
import com.chibik.perf.util.Score;
import com.chibik.perf.util.ScoreTimeUnit;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.CMYKColor;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.openjdk.jmh.annotations.Group;
import org.openjdk.jmh.results.RunResult;

import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

public class BenchmarkReportGenerator {

    private final String fileName;
    private final Collection<RunResult> runResults;

    private Font tableFont = FontFactory.getFont(
            FontFactory.COURIER, 8, Font.NORMAL,	new CMYKColor(0, 0, 0, 255)
    );
    private Font descriptionFont = FontFactory.getFont(
            FontFactory.COURIER, 10, Font.NORMAL,	new CMYKColor(0, 0, 0, 255)
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

                Class<?> benchmarkClass = this.getClass().getClassLoader().loadClass(entry.getKey());

                Included included = benchmarkClass.getAnnotation(Included.class);

                if(included == null) {
                    continue;
                }

                document.add(new Paragraph((counter++) + "." + entry.getKey(), chapterFont));
                document.add(new Paragraph("\n"));

                Comment comment = benchmarkClass.getAnnotation(Comment.class);
                if(comment != null) {
                    document.add(new Paragraph(comment.value(), descriptionFont));
                    document.add(new Paragraph("\n"));
                }

                RunResult first = entry.getValue().get(0);
                List<String> parameterKeys = new ArrayList<>(first.getParams().getParamsKeys());
                boolean usedBatch = first.getParams().getMeasurement().getBatchSize() > 1;

                int columns = 4 + parameterKeys.size() + (usedBatch ? 3 : 0);
                float[] columnWidth = new float[columns];
                columnWidth[0] = 200f;

                float parametersWidthTotal = 600f;
                int parameterStartIndex = 1;
                float parameterColumnWidth = Math.min(parametersWidthTotal/parameterKeys.size(), 110f);

                for(int i = parameterStartIndex; i < parameterStartIndex + parameterKeys.size(); i++) {
                    columnWidth[i] = parameterColumnWidth;
                }

                for(int i = parameterStartIndex + parameterKeys.size(); i < columns - 1; i++) {
                    columnWidth[i] = 90f;
                }
                columnWidth[columns - 1] = 300f;

                PdfPTable table = new PdfPTable(columns);
                table.setTotalWidth(columnWidth);
                table.setLockedWidth(true);
                table.addCell(new PdfPCell(new Phrase("Benchmark", tableFont)));
                for(String param : parameterKeys) {
                    table.addCell(new PdfPCell(new Phrase(param, tableFont)));
                }
                if(usedBatch) {
                    table.addCell(new PdfPCell(new Phrase("Batch size", tableFont)));
                    table.addCell(new PdfPCell(new Phrase("Score(total)", tableFont)));
                    table.addCell(new PdfPCell(new Phrase("Score unit", tableFont)));
                    table.addCell(new PdfPCell(new Phrase("Per op(per call)", tableFont)));
                    table.addCell(new PdfPCell(new Phrase("Per op unit", tableFont)));
                } else {
                    table.addCell(new PdfPCell(new Phrase("Score", tableFont)));
                    table.addCell(new PdfPCell(new Phrase("Score unit", tableFont)));
                }
                table.addCell(new PdfPCell(new Phrase("Description", tableFont)));

                for(RunResult result : entry.getValue()) {

                    table.addCell(new PdfPCell(
                            new Phrase(result.getParams().getBenchmark().replace("com.chibik.perf.", ""), tableFont)
                    ));

                    for(String paramKey : parameterKeys) {
                        table.addCell(new PdfPCell(
                                new Phrase(result.getParams().getParam(paramKey), tableFont)
                        ));
                    }

                    if(usedBatch) {
                        int batchSize = result.getParams().getMeasurement().getBatchSize();
                        table.addCell(new PdfPCell(new Phrase("" + batchSize, tableFont)));
                    }

                    Score score = new Score(result.getPrimaryResult().getScore(), ScoreTimeUnit.byTimeUnit(result.getPrimaryResult().getScoreUnit()));

                    table.addCell(new PdfPCell(new Phrase(String.format("%.3f", score.getVal()), tableFont)));
                    table.addCell(new PdfPCell(new Phrase(score.getUnit().getValue(), tableFont)));

                    if(usedBatch) {
                        int batchSize = result.getParams().getMeasurement().getBatchSize();
                        Score batchPerOpScore = new Score(score.getVal()/batchSize, score.getUnit());
                        Score convertedPerOpScore = ScoreTimeUnit.convertToReadable(batchPerOpScore);

                        table.addCell(new PdfPCell(new Phrase(String.format("%.3f", convertedPerOpScore.getVal()), tableFont)));
                        table.addCell(new PdfPCell(new Phrase(convertedPerOpScore.getUnit().getValue(), tableFont)));
                    }

                    try {
                        String label = result.getPrimaryResult().getLabel();
                        Method benchmarkMethod = null;
                        for(Method method : benchmarkClass.getDeclaredMethods()) {
                            Group gr = method.getAnnotation(Group.class);
                            if(gr!= null) {
                                System.out.println("gr.value=" + gr.value());
                                System.out.println(gr.value().equals(label));
                                System.out.println(label);
                            }
                            if(gr != null && gr.value().equals(label)) {
                                benchmarkMethod = method;
                                break;
                            }
                        }
                        if(benchmarkMethod != null) {
                            Comment methodComment = benchmarkMethod.getAnnotation(Comment.class);
                            if (methodComment != null) {
                                table.addCell(new PdfPCell(new Phrase(methodComment.value(), tableFont)));
                            } else {
                                table.addCell(new PdfPCell(new Phrase("No comment", tableFont)));
                            }
                        } else {
                            table.addCell(new PdfPCell(new Phrase("No method", tableFont)));
                        }

                    } catch(Exception e) {
                        table.addCell(new PdfPCell(new Phrase("Error!!!", tableFont)));
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
