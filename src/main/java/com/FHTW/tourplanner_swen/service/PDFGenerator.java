package com.FHTW.tourplanner_swen.service;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.FileOutputStream;
import java.io.OutputStream;

public class PDFGenerator {
    private String parseThymeleafTemplateHelloWorld() {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);

        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);

        Context context = new Context();
        context.setVariable("to", "Workers");

        return templateEngine.process("templates/thymeleaf/hello_world", context);
    }

    private void generatePdfFromHtml(String html) throws Exception {
        String output = "report.pdf";
        OutputStream outputStream = new FileOutputStream(output);
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(html);
        renderer.layout();
        renderer.createPDF(outputStream);

        outputStream.close();
    }

    public void startDemo() throws Exception {
        generatePdfFromHtml(parseThymeleafTemplateHelloWorld());
    }

    public static void main(String[] args) throws Exception {
        new PDFGenerator().startDemo();
    }
}
