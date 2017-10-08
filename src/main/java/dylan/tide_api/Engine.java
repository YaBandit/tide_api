package dylan.tide_api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dylan.tide_api.api.ApiGraphBuilder;
import dylan.tide_api.api.GraphQLServer;
import dylan.tide_api.core.ConfigReader;
import dylan.tide_api.core.Utils;
import dylan.tide_api.data.DataManager;

public class Engine {

    private final Logger log = LoggerFactory.getLogger(Engine.class);

    private String	 componentId;

    private ConfigReader config;

    private DataManager	 dataManager;

    private void init(ConfigReader config) throws Exception {

	this.config = config;

	componentId = config.getComponentId();

	log.info("Starting initilisation for engine with component id" + componentId);

	dataManager = new DataManager(this);

	GraphQLServer.create(this, ApiGraphBuilder.build(this));

	log.info("Initialisation complete");

    }

    public ConfigReader getConfig() {
	return config;
    }

    public DataManager getDataManager() {
	return dataManager;
    }

    /*
     * TIDE START
     *  
     * componentId=tide
     * config=config.txt
     * 
     */
    public static void main(String[] args) throws Exception {

	final ConfigReader config = Utils.handleArgs(args);

	final Engine engine = new Engine();

	engine.init(config);

    }

}
