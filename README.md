# tide_api

This is a Graphql API ontop of REST service. What this will allow you to do is query the API with a JSON request.
This api will show you the available packages (bitcoin, ether, etc), and the available historical versions. Your
app in this scenario would simply compare it's version to see if it can be used.

To run the application please just download the git repository, and do a maven update to get the dependancies.
The main method is inside the Engine class, with the arguments required commented above it.

In a production environment the data source would be a database, but for the ease of running I've used csv files
which are included in the repo.

To query the data please use a tool called GraphIQL Feen which can be found on the chrome app store. You can then 
query the api as shown below:

The api supports username authentication as a required argument in the query. The error could be handled better
in future updates, as currently it will throw a runtime exception if not authenticated.

Future Updates:

  1. Containerization
  2. Remove accidentally added files that should be ignored.
  
 Scalabilty:
 
 This API will realistically handle huge number of connections. However if it needed to scale, simply run up mulitple
 versions of the application using the same data source.
