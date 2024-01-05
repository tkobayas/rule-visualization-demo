package org.example.rule.visualization.endpoint;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/rules-visualizer")
public class RulesVisualizerEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(RulesVisualizerEndpoint.class);
    @POST()
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String graphAsJson(String payload) {
        //LOGGER.info("Received payload: {}", payload);
        String result = RulesVisualizerService.INSTANCE.graphAsJson(payload);
        //LOGGER.info("Returning result: {}", result);
        return result;
    }
}
