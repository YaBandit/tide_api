package dylan.tide_api.api;

import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.threadPool;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dylan.tide_api.Engine;
import graphql.ExecutionResult;
import graphql.GraphQL;
import spark.Request;
import spark.Response;

public class GraphQLServer {

    private static final Logger	log			= LoggerFactory.getLogger(GraphQLServer.class);

    private static final String	KEY_GRAPHQL_PORT	= "graphQl.port";
    private static final String	KEY_GRAPHQL_MIN_THREADS	= "graphQl.minThreads";
    private static final String	KEY_GRAPHQL_MAX_THREADS	= "graphQl.maxThreads";
    private static final String	KEY_GRAPHQL_TIME_OUT	= "graphQl.timeOutMillis";

    private final GraphQL	graphQl;

    private final Engine	engine;

    private GraphQLServer(Engine engine, GraphQL graphQl) {

	this.engine = engine;

	this.graphQl = graphQl;
    }

    private void start() {

	/*
	 * This is the single end-point for the entire API
	 * Likely should be single threaded from this point,
	 * with everything accessible from this point
	 */
	final int port = engine.getConfig()
			       .getIntValue(KEY_GRAPHQL_PORT);

	port(port);

	/*
	 * Thread pooling - There is a question as to whether it should
	 * all be single threaded, or everything should be thread safe
	 * and multiple threads used. I think the desired design will
	 * be multiple threads, so will design with that parameter
	 */

	final int minThreads = engine.getConfig()
				     .isValuePresent(KEY_GRAPHQL_MIN_THREADS) ? engine.getConfig()
										      .getIntValue(KEY_GRAPHQL_MIN_THREADS) : 2;
	final int maxThreads = engine.getConfig()
				     .isValuePresent(KEY_GRAPHQL_MAX_THREADS) ? engine.getConfig()
										      .getIntValue(KEY_GRAPHQL_MAX_THREADS) : 8;
	final int timeout = engine.getConfig()
				  .isValuePresent(KEY_GRAPHQL_TIME_OUT) ? engine.getConfig()
										.getIntValue(KEY_GRAPHQL_TIME_OUT) : 30000;

	threadPool(maxThreads, minThreads, timeout);

	post("/graphql", (req, res) -> {

	    return process(req, res);

	});

    }

    @SuppressWarnings("unchecked")
    private String process(Request request, Response response) throws JsonParseException, JsonMappingException, IOException {

	final Map<String, Object> result = new LinkedHashMap<>();

	try {

	    final Map<String, Object> payload = new ObjectMapper().readValue(request.body(), Map.class);

	    Map<String, Object> variables;
	    if (payload.get("variables") == null || ((String) payload.get("variables")).isEmpty()) {
		variables = Collections.emptyMap();
	    } else {
		variables = (Map<String, Object>) payload.get("variables");
	    }

	    final ExecutionResult executionResult = graphQl.execute(payload.get("query")
									   .toString(), null, null, variables);

	    if (executionResult.getErrors()
			       .size() > 0) {
		result.put("errors", executionResult.getErrors());
	    } else {
		result.put("data", executionResult.getData());
	    }

	    response.type("application/json");

	} catch (Exception e) {

	    log.error("Error for request: " + request.body(), e);

	    result.put("errors", e.getMessage());
	}

	final String mapAsJson = new ObjectMapper().writeValueAsString(result);

	return mapAsJson;
    }

    public static GraphQLServer create(Engine engine, GraphQL graphQl) {

	final GraphQLServer server = new GraphQLServer(engine, graphQl);

	server.start();

	return server;

    }

}
