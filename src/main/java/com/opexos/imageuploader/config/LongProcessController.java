package com.opexos.imageuploader.config;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Dummy controller that helps to test gracefully shutdown
 */
@RestController
@Api(tags = "Test")
public class LongProcessController {

    @ApiOperation("Method for testing the correct end of the request when the server is shutting down. " +
            "Returns a simple string.")
    @GetMapping("/long-process")
    public String pause() throws InterruptedException {
        Thread.sleep(10000);
        return "Process finished";
    }

}