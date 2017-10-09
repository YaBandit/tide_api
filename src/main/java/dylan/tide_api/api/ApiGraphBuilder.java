package dylan.tide_api.api;

import static graphql.Scalars.GraphQLInt;
import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dylan.tide_api.Engine;
import dylan.tide_api.core.Pair;
import graphql.GraphQL;
import graphql.GraphQLException;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLNonNull;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;

public class ApiGraphBuilder {

    private static final Logger	     log		    = LoggerFactory.getLogger(ApiGraphBuilder.class);

    public static final int	     MAX_RETURN_AMOUNT	    = 20;

    public static final String	     GLOBAL_ARGUMENT_USER   = "username";
    public static final String	     GLOBAL_ARGUMENT_FIRST  = "first";

    private static final String	     QUERY_ARGUMENT_FEATURE = "feature";

    private static final Set<String> GLOBAL_ARGS;

    static {
	GLOBAL_ARGS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(GLOBAL_ARGUMENT_FIRST, GLOBAL_ARGUMENT_USER)));
    }

    private final GraphObjects graphObjects;

    private final Engine       engine;

    private ApiGraphBuilder(Engine engine) {

	this.engine = engine;

	graphObjects = new GraphObjects();
    }

    private GraphQL createGraphQl() {

	log.debug("Building root query");

	/*
	 * Below is the root query
	 */
	final GraphQLObjectType query = newObject().name("query")
						   .field(newFieldDefinition().name("Active_Features")
									      .type(new GraphQLList(graphObjects.activeFeature))
									      .argument(new GraphQLArgument(GLOBAL_ARGUMENT_USER, new GraphQLNonNull(GraphQLString)))
									      .argument(new GraphQLArgument(GLOBAL_ARGUMENT_FIRST, GraphQLInt))
									      .argument(new GraphQLArgument("feature", GraphQLString))
									      .dataFetcher(d -> {

										  final Pair<Map<String, Object>, Map<String, Object>> argMap = returnGlobalAndNonGlobalArgs(d.getArguments());

										  checkGlobalArgs(argMap.getA(), engine);

										  final int limit = getReturnLimit(argMap.getA());

										  final Object featureNameFilter = argMap.getB()
															 .get(QUERY_ARGUMENT_FEATURE);

										  if (featureNameFilter != null) {

										      final String featureKey = (String) featureNameFilter;

										      return engine.getDataManager()
												   .getActiveFeatures()
												   .stream()
												   .filter(af -> {
												       return af.getName()
														.contains(featureKey);
												   })
												   .limit(limit)
												   .collect(Collectors.toList());

										  } else {

										      return engine.getDataManager()
												   .getActiveFeatures()
												   .stream()
												   .limit(limit)
												   .collect(Collectors.toList());

										  }

									      }))
						   .build();

	final GraphQLSchema schema = GraphQLSchema.newSchema()
						  .query(query)
						  .build();

	return new GraphQL(schema);

    }

    private static Pair<Map<String, Object>, Map<String, Object>> returnGlobalAndNonGlobalArgs(Map<String, Object> input) {

	final Map<String, Object> globals = new HashMap<>();
	final Map<String, Object> nonGlobals = new HashMap<>();

	input.forEach((k, v) -> {
	    if (GLOBAL_ARGS.contains(k)) {
		globals.put(k, v);
	    } else {
		nonGlobals.put(k, v);
	    }
	});

	return new Pair<Map<String, Object>, Map<String, Object>>(globals, nonGlobals);

    }

    public static void checkGlobalArgs(Map<String, Object> args, Engine engine) {

	args.forEach((k, v) -> {
	    if (k.equals(GLOBAL_ARGUMENT_USER)) {
		if (v != null && !engine.getDataManager()
					.getAllowedUsers()
					.contains(v)) {
		    log.info("User not permitted access to API: " + v);
		    throw new GraphQLException("User Not Permitted");
		}
	    }
	    if (k.equals(GLOBAL_ARGUMENT_FIRST)) {
		if (v != null && ((Integer) v) > MAX_RETURN_AMOUNT) {
		    throw new GraphQLException("Api return limit exceeded");
		}
	    }
	});

    }

    public static int getReturnLimit(Map<String, Object> args) {

	for (Entry<String, Object> e : args.entrySet()) {
	    if (e.getKey()
		 .equals(GLOBAL_ARGUMENT_FIRST)) {
		if (e.getValue() == null) {
		    break;
		}
		return ((Integer) e.getValue());
	    }
	}

	return MAX_RETURN_AMOUNT;

    }

    public static GraphQL build(Engine engine) {

	final ApiGraphBuilder builder = new ApiGraphBuilder(engine);

	return builder.createGraphQl();

    }

}
