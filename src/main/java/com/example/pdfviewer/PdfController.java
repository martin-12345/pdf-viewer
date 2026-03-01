package com.example.pdfviewer;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/*
Sample input

JVBERi0xLjQKMSAwIG9iago8PCAvVHlwZSAvQ2F0YWxvZyAvUGFnZXMgMiAwIFIgPj4KZW5kb2Jq
CjIgMCBvYmoKPDwgL1R5cGUgL1BhZ2VzIC9LaWRzIFsgMyAwIFIgXSAvQ291bnQgMSA+PgplbmRv
YmoKMyAwIG9iago8PCAvVHlwZSAvUGFnZSAvUGFyZW50IDIgMCBSIC9NZWRpYUJveCBbMCAwIDYx
MiA3OTJdIC9Db250ZW50cyA0IDAgUiAvUmVzb3VyY2VzIDw8IC9Gb250IDw8IC9GMQA1IDAgUiA+
PiA+PiA+PgplbmRvYmoKNCAwIG9iago8PCAvTGVuZ3RoIDQ0ID4+CnN0cmVhbQpCVAovRjEgMjQg
VGYKMTAwIDcwMCBUZAooSGVsbG8gZnJvbSBCYXNlNjQgUERGKSBUagpFVAplbmRzdHJlYW0KZW5k
b2JqCjUgMCBvYmoKPDwgL1R5cGUgL0ZvbnQgL1N1YnR5cGUgL1R5cGUxIC9CYXNlRm9udCAvSGVs
dmV0aWNhID4+CmVuZG9iagoKeHJlZgowIDYKMDAwMDAwMDAwMCA2NTUzNSBmIAowMDAwMDAwMDEw
IDAwMDAwIG4gCjAwMDAwMDAwNzkgMDAwMDAgbiAKMDAwMDAwMDE3MyAwMDAwMCBuIAowMDAwMDAw
MzA0IDAwMDAwIG4gCjAwMDAwMDA0MDEgMDAwMDAgbiAKdHJhaWxlcgo8PCAvU2l6ZSA2IC9Sb290
IDEgMCBSID4+CnN0YXJ0eHJlZgo1MDQKJSVFT0Y=


 */
@Controller
public class PdfController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/decode")
    public void decodePdfStream(@RequestParam String base64pdf,
                                HttpServletResponse response) throws IOException {

        if (base64pdf == null || base64pdf.isBlank()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        // Remove data URL prefix if present
        if (base64pdf.contains(",")) {
            base64pdf = base64pdf.substring(base64pdf.indexOf(",") + 1);
        }
        if(base64pdf.indexOf('>') < base64pdf.length()-1){
            base64pdf=base64pdf.substring((base64pdf.indexOf('>')+1));
        }
        if(base64pdf.contains("<")){
            base64pdf=base64pdf.substring(0, base64pdf.indexOf("<"));
        }

        // Convert string to InputStream
        byte[] inputBytes = base64pdf.getBytes(StandardCharsets.US_ASCII);
        InputStream byteStream = new ByteArrayInputStream(inputBytes);

        // Wrap with Base64 decoder stream (MIME safe)
        InputStream decodedStream = Base64.getMimeDecoder().wrap(byteStream);

        // Set headers
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=document.pdf");

        // Stream to response
        try (OutputStream out = response.getOutputStream()) {
            decodedStream.transferTo(out);  // Java 9+ streaming method
        } catch (IOException e) {
            // Client closed connection or network error
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
