package com.FHTW.tourplanner_swen.service;

import com.FHTW.tourplanner_swen.service.dtos.TourDto;
import com.FHTW.tourplanner_swen.service.dtos.TourLogDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;

@Component
@Slf4j
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

    public void generatePdfFromTourDto(TourDto tourDto, List<TourLogDto> tourLogDtos) throws Exception {
        String htmlContent = parseThymeleafTemplateTourDetails(tourDto, tourLogDtos);
        generatePdfFromHtml(htmlContent);

        log.info("Successfully generated Pdf");
    }

    private String parseThymeleafTemplateTourDetails(TourDto tourDto, List<TourLogDto> tourLogDtos) {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);

        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);

        Context context = new Context();
        context.setVariable("tour", tourDto);
        context.setVariable("tourLogs", tourLogDtos);

        return templateEngine.process("templates/thymeleaf/tour_details", context);
    }

    private void generatePdfFromHtml(String html) throws Exception {
        String output = "tour_report.pdf";
        try (OutputStream outputStream = new FileOutputStream(output)) {
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(html);
            renderer.layout();
            renderer.createPDF(outputStream);
        }
    }
}
