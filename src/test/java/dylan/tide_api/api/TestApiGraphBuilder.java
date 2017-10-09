package dylan.tide_api.api;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import dylan.tide_api.Engine;
import dylan.tide_api.core.ConfigReader;
import dylan.tide_api.data.DataManager;
import graphql.GraphQLException;

public class TestApiGraphBuilder {

    @Test
    public void testCheckGlobalArgsUserPass() throws Exception {

	final Engine engine = createMockEngine();

	final Map<String, Object> args = new HashMap<>();
	args.put(ApiGraphBuilder.GLOBAL_ARGUMENT_USER, "Test1");

	// Failure if exception not thrown

	boolean hasThrown = false;

	try {
	    ApiGraphBuilder.checkGlobalArgs(args, engine);
	} catch (GraphQLException e) {
	    hasThrown = true;
	}

	assertTrue(!hasThrown);

    }

    @Test
    public void testCheckGlobalArgsUserFail() throws Exception {

	final Engine engine = createMockEngine();

	final Map<String, Object> args = new HashMap<>();
	args.put(ApiGraphBuilder.GLOBAL_ARGUMENT_USER, "Fail");

	// Failure if exception thrown

	boolean hasThrown = false;

	try {
	    ApiGraphBuilder.checkGlobalArgs(args, engine);
	} catch (GraphQLException e) {
	    hasThrown = true;
	}

	assertTrue(hasThrown);

    }

    private Engine createMockEngine() throws Exception {

	final Engine engine = new Engine() {

	    @Override
	    public void init(ConfigReader config) throws Exception {

		this.dataManager = new MockDataManager();
	    }

	};

	engine.init(null);

	return engine;

    }

    private final class MockDataManager extends DataManager {

	public MockDataManager() throws Exception {
	    super(null);
	}

	@Override
	protected void init(Engine engine) throws IOException, Exception {

	    allowedUsers = Arrays.asList("Test1", "Test2");

	}

    }

}
