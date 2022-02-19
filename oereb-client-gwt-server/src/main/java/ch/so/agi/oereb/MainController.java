package ch.so.agi.oereb;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    Settings settings;
    
    @Autowired
    HttpServletRequest request;
    
    @Autowired
    HttpClient httpClient;
    
    @GetMapping("/settings")
    public ResponseEntity<?> getSettings() {
        return ResponseEntity.ok().body(settings);
    }
    
    @Cacheable(value = "wmsGetMapCache", key = "#request.getQueryString")
    @GetMapping(value = "/wms", produces = MediaType.IMAGE_PNG_VALUE)
    public @ResponseBody byte[] wms(HttpServletRequest request) throws URISyntaxException, IOException, InterruptedException {        
        String wmsRequest = "https://geodienste.ch/db/av_situationsplan_oereb_0?" + request.getQueryString();
        //String wmsRequest = "https://geo.so.ch/ows/somap?" + request.getQueryString();
        
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(new URI(wmsRequest))
                .GET()
                .build();
        
        HttpResponse<InputStream> response = httpClient
                .send(httpRequest, HttpResponse.BodyHandlers.ofInputStream());

        return IOUtils.toByteArray(response.body());
    }
}
