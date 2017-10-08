package dylan.tide_api.api;

import static graphql.Scalars.GraphQLInt;
import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;

import dylan.tide_api.data.ActiveFeature;
import dylan.tide_api.data.Version;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLObjectType;

public class GraphObjects {

    public GraphQLObjectType activeFeature = null;
    public GraphQLObjectType verion	   = null;

    protected GraphObjects() {

	create();

    }

    private void create() {

	verion = newObject().name("version")
			    .field(newFieldDefinition().name("verionNum")
						       .type(GraphQLInt)
						       .dataFetcher(d -> {
							   return ((Version) d.getSource()).getVersion();
						       }))
			    .field(newFieldDefinition().name("release_date")
						       .type(GraphQLInt)
						       .dataFetcher(d -> {
							   return ((Version) d.getSource()).getReleaseDate();
						       }))
			    .field(newFieldDefinition().name("valid")
						       .type(GraphQLString)
						       .dataFetcher(d -> {
							   return ((Version) d.getSource()).isValid() ? "True" : "False";
						       }))
			    .build();

	activeFeature = newObject().name("active_feature")
				   .field(newFieldDefinition().name("name")
							      .type(GraphQLString)
							      .dataFetcher(d -> {
								  return ((ActiveFeature) d.getSource()).getName();
							      }))
				   .field(newFieldDefinition().name("enabled")
							      .type(GraphQLString)
							      .dataFetcher(d -> {
								  return ((ActiveFeature) d.getSource()).isEnabled() ? "True" : "False";
							      }))
				   .field(newFieldDefinition().name("versions")
							      .type(new GraphQLList(verion))
							      .dataFetcher(d -> {
								  return ((ActiveFeature) d.getSource()).getVersions();
							      }))
				   .build();

    }

}
