package br.inatel.pos.dm111.vfu.api.promo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.inatel.pos.dm111.vfu.api.core.ApiException;
import br.inatel.pos.dm111.vfu.api.promo.PromotionRequest;
import br.inatel.pos.dm111.vfu.api.promo.PromotionResponse;
import br.inatel.pos.dm111.vfu.api.promo.service.PromotionService;

@RestController
@RequestMapping("/valefood/promotions")
public class PromotionController {

    private static final Logger log = LoggerFactory.getLogger(PromotionController.class);

    private final PromotionService service;

    public PromotionController(PromotionService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<PromotionResponse> createPromotion(@RequestBody PromotionRequest request) throws ApiException {
        log.info("Received request to create a new promotion into the cache: {}", request);
        
        var response = service.createPromotion(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}