##Version
Current Version: **0.10.1**

See change log:

https://github.com/apigee/usergrid-node-module/master/changelog.md

##Overview
This Node.js module, which simplifies the process of making API calls to App Services from within Node.js, is provided by [Apigee](http://apigee.com) and is available as an open-source project on github.  We welcome your contributions and suggestions. The repository is located here:

<https://github.com/apigee/usergrid-node-module>

To find out more about Apigee App Services, see:

<http://apigee.com/about/developers>

To view the Apigee App Services documentation, see:

<http://apigee.com/docs/usergrid/>


##Installing
Use npm:

	$ npm install usergrid



##Getting started
Include the module:

	var usergrid = require('usergrid');

Then create a new client:

	var client = new usergrid.client({
		orgName:'yourorgname',
		appName:'sandbox',
		logging: true, //optional - turn on logging, off by default
	});

The preceding example shows how to use the "Sandbox" testing app, which does not require any authentication.  The "Sandbox" comes with all new App Services accounts.

If you are ready to use authentication, then create your client this way:

	var client = new usergrid.client({
		orgName:'yourorgname',
		appName:'yourappname',
		authType:usergrid.AUTH_CLIENT_ID,
		clientId:'<your client id>',
		clientSecret:'<your client secret>',
		logging: false, //optional - turn on logging, off by default
		buildCurl: false //optional - turn on curl commands, off by default
	});

The last two items are optional. The **logging** option will enable console.log output from the client.  The **buildCurl** option will cause cURL equivalent commands of all calls to the API to be displayed in the console.log output.

**Note:** you can find your client secret and client id on the "Properties" page of the [Admin Portal](http://apigee.com/usergrid).

You are now ready to use the usergrid handle to make calls against the API.

##About the samples
All of the samples provided in this readme file come from unit tests in the test.js which is located in the root of this project.

To run this file, first do the following:

1. Change the org-name and app-name to point to your Usergrid account.  Log into the [Admin Portal](http://apigee.com/usergrid) to see this information.
2. Change the client secret and client id

Then run the code:

	$ node test.js

Read through the samples in this file as they show many examples of how to use this module.

##Make some calls
This Usergrid module uses the [request](https://github.com/mikeal/request) module by [mikeal](https://github.com/mikeal).  We expose a similar request function and a subset of the options available. This allows you to make basic calls against the API using this format:

	client.request(options, callback);

This client.request method uses similar syntax although only the subset of options that is relevant to making calls against the App Services API. For example, to get a list of users:

	var options = {
		method:'GET',
		endpoint:'users'
	};
	client.request(options, function (err, data) {
		if (err) {
			error('GET failed');
		} else {
			//data will contain raw results from API call
			success('GET worked');
		}
	});

Or, to create a new user:

	var options = {
		method:'POST',
		endpoint:'users',
		body:{ username:'fred', password:'secret' }
	};
	client.request(options, function (err, data) {
		if (err) {
			error('POST failed');
		} else {
			//data will contain raw results from API call
			success('POST worked');
		}
	});

Or, to update the new user:

	var options = {
		method:'PUT',
		endpoint:'users/fred',
		body:{ newkey:'newvalue' }
	};
	client.request(options, function (err, data) {
		if (err) {
			error('PUT failed');
		} else {
			//data will contain raw results from API call
			success('PUT worked');
		}
	});

Or to delete the new user:

	var options = {
		method:'DELETE',
		endpoint:'users/fred'
	};
	client.request(options, function (err, data) {
		if (err) {
			error('DELETE failed');
		} else {
			//data will contain raw results from API call
			success('DELETE worked');
		}
	});


The Options Object for the client.request fuction:

* `method` - http method (GET, POST, PUT, or DELETE), defaults to GET
* `qs` - object containing querystring values to be appended to the uri
* `body` - object containing entity body for POST and PUT requests
* `endpoint` - API endpoint, for example "users/fred"
* `mQuery` - boolean, set to true if running management query, defaults to false
* `buildCurl` - boolean, set to true if you want to see equivalent curl commands in console.log, defaults to false

You can make any call to the API using the format above.  However, in practice using the higher level Entity and Collection objects will make life easier as they take care of much of the heavy lifting.


##Entities and Collections
Usergrid stores its data as "Entities" in "Collections".  Entities are essentially JSON objects and Collections are just like folders for storing these objects. You can learn more about Entities and Collections in the App Services docs:

<http://apigee.com/docs/usergrid/content/data-model>


##Entities
This module provides an easy way to make new entities. Here is a simple example that shows how to create a new object of type "dogs":

	var options = {
		type:'dogs',
		name:'Dino'
	}
	client.createEntity(options, function (err, dog) {
		if (err) {
			error('dog not created');
		} else {
			success('dog is created');

			//once the dog is created, you can set single properties:
			dog.set('breed','Dinosaur');

			//or a JSON object:
			var data = {
				master:'Fred',
				state:'hungry'
			}
			//set is additive, so previously set properties are not overwritten
			dog.set(data);

			//finally, call save on the object to save it back to the database
			dog.save(function(err){
				if (err){
					error('dog not saved');
				} else {
					success('new dog is saved');
				}
			});
		}
	});

**note:** all calls to the API will be executed asynchronously, so it is important that you use a callback.


You can also refresh the object from the database if needed (in case the data has been updated by a different client or device):

	//call fetch to refresh the data from the server
	dog.fetch(function(err){
		if (err){
			error('dog not refreshed from database');
		} else {
			//dog has been refreshed from the database
			//will only work if the UUID for the entity is in the dog object
			success('dog entity refreshed from database');
		}
	});

To remove the entity from the database:

	//the destroy method will delete the entity from the database
	dog.destroy(function(err){
		if (err){
			error('dog not removed from database');
		} else {
			success('dog removed from database'); // no real dogs were harmed!
			dog = null; //no real dogs were harmed!
		}
	});


##The Collection object
The Collection object models Collections in the database.  Once you start programming your app, you will likely find that this is the most useful method of interacting with the database.  Creating a collection will automatically populate the object with entities from the collection. The following example shows how to create a Collection object, then how to use entities once the Collection has been populated with entities from the server:

	//options object needs to have the type (which is the collection type)
	var options = {
		type:'dogs',
		qs:{ql:'order by index'}
	}

	client.createCollection(options, function (err, dogs) {
		if (err) {
			error('could not make collection');
		} else {

			success('new Collection worked');

			//we got the dogs, now display the Entities:
			while(dogs.hasNextEntity()) {
				//get a reference to the dog
				dog = dogs.getNextEntity();
				var name = dog.get('name');
				notice('dog is called ' + name);
			}

			success('looped through dogs');

		}
	});


You can also add a new entity of the same type to the collection:

	//create a new dog and add it to the collection
	var options = {
		name:'extra-dog',
		fur:'shedding'
	}
	//just pass the options to the addEntity method
	//to the collection and it is saved automatically
	dogs.addEntity(options, function(err, dog, data) {
		if (err) {
			error('extra dog not saved or added to collection');
		} else {
			success('extra dog saved and added to collection');
		}
	});


##Collection iteration and paging
The Collection object works in Pages of data.  This means that at any given time, the Collection object will have one page of data loaded.  You can iterate across all the entities in the current page of data by using the following pattern:

	//we got the dogs, now display the Entities:
	while(dogs.hasNextEntity()) {
		//get a reference to the dog
		dog = dogs.getNextEntity();
		var name = dog.get('name');
		notice('dog is called ' + name);
	}

To get the next page of data from the server, use the following pattern:

	if (dogs.hasNextPage()) {
		//there is a next page, so get it from the server
		dogs.getNextPage(function(err){
			if (err) {
				error('could not get next page of dogs');
			} else {
				success('got next page of dogs');
				//we got the dogs, now display the Entities:
				while(dogs.hasNextEntity()) {
					//get a reference to the dog
					dog = dogs.getNextEntity();
					var name = dog.get('name');
					notice('dog is called ' + name);
				}
				success('looped through dogs');
			}
		});
	}

You can use the same pattern to get a previous page of data:

	if (dogs.hasPreviousPage()) {
		//there is a previous page, so get it from the server
		dogs.getPreviousPage(function(err){
			if(err) {
				error('could not get previous page of dogs');
			} else {
				success('got next page of dogs');
				//we got the dogs, now display the Entities:
				while(dogs.hasNextEntity()) {
					//get a reference to the dog
					dog = dogs.getNextEntity();
					var name = dog.get('name');
					notice('dog is called ' + name);
				}
				success('looped through dogs');
			}
		});
	}

By default, the database will return 10 entities per page.  You can change that amount by setting a limit:


	var options = {
		type:'dogs',
		qs:{limit:50} //limit statement set to 50
	}

	client.createCollection(options, function (err, dogs) {
		if (err) {
			error('could not get all dogs');
		} else {
			success('got at most 50 dogs');
		}
	}

Several other convenience methods exist to make working with pages of data easier:

* getFirstEntity - gets the first entity of a page
* getLastEntity - gets the last entity of a page
* resetEntityPointer - sets the internal pointer back to the first element of the page
* getEntityByUUID - returns the entity if it is in the current page


###Custom Queries
A custom query allows you to tell the API that you want your results filtered or altered in some way.  To specify that the query results should be ordered by creation date, add the qs parameter to the options object:

	var options = {
		type:'dogs',
		qs:{ql:'order by created DESC'}
	};

You may find that you need to change the query on an existing object.  Simply access the qs property directly:

	dogs.qs = {ql:'order by created DESC'};


If you also wanted to get more entities in the result set than the default 10, say 100, you can specify a query similar to the following (the limit can be a maximum of 999):

	dogs.qs = {ql:'order by created DESC',limit:'100'};

**Note**: there are many cases where expanding the result set is useful.  But be careful - the more results you get back in a single call, the longer it will take to transmit the data back to your app.

Another common requirement is to limit the results to a specific query.  For example, to get all brown dogs, use the following syntax:

	dogs.qs = {ql:"select * where color='brown'"};

You can also limit the results returned such that only the fields you specify are returned:

	dogs.qs = {'ql':"select name, age where color='brown'"};

**Note:** in the two preceding examples that we put single quotes around 'brown', so it will be searched as a string.

You can find more information on custom queries here:

<http://apigee.com/docs/usergrid/content/queries-and-parameters>


##Modeling users with the Entity object
There is no specific User object in the module.  Instead, you simply need to use the Entity object, specifying a type of "users".  Here is an example:

	//type is 'users', set additional paramaters as needed
	var options = {
		type:'users',
		username:'marty',
		password:'mysecurepassword',
		name:'Marty McFly',
		city:'Hill Valley'
	}

  	client.createEntity(options, function (err, marty) {
		if (err){
			error('user not saved');
		} else {
			success('user saved');
		}
	});


If the user is modified, just call save on the user again:

	//add properties cumulatively
	marty.set('state', 'California');
	marty.set("girlfriend","Jennifer");
	marty.save(function(err){
		if (err){
			error('user not updated');
		} else {
			success('user updated');
		}
	});

To refresh the user's information in the database:

	marty.fetch(function(err){
		if (err){
			error('not refreshed');
		} else {
			success('user refreshed');
		}
	});

If you no longer need the object, call the delete() method and the object will be deleted from database:

	marty.destroy(function(err){
		if (err){
			error('user not deleted from database');
		} else {
			success('user deleted from database');
			marty = null; //blow away the local object
		}
	});


###To log a user in
Up to this point, we have shown how you can use the client secret / client id combination to authenticate your calls against the API.  For a server-side Node.js app, this may be all you need.  However, if you do find that your app requires that you authenticate an individual user, this section shows you how.

Logging a user in means sending the user's username and password to the server, and getting back an access (OAuth) token.  You can then use this token to make calls to the API on the User's behalf. The following example shows how to log a user in, then how to make a new client object to use for the app user's calls:

	username = 'marty';
	password = 'mysecurepassword';
	client.login(username, password,
		function (err) {
			if (err) {
				error('could not log user in');
			} else {
				//once a user has logged in, thier user object is stored
				//in the client and you can access it this way:
				var user = client.user;

				//so you can then get their username (or other info):
				var username = user.get('username');

				//you can also detect if the user is logged in:
				if (client.isLoggedIn()) {
					success('user has been logged in');
				}

				//the login call will return an OAuth token, which is saved
				//in the client object for later use.  Access it this way:
				var token = client.token;

				//then make a new client just for the app user
				var appUserClient = new usergrid.client({
					orgName:'yourorgname',
					appName:'yourappname',
					authType:usergrid.APP_USER,
					token:token
				});

				//then use this client to make calls against the API

				//to log the user out, call the logout() method
				appUserClient.logout();
				client.logout();

				//verify the logout worked
				if (client.isLoggedIn()) {
					error('logout failed');
				} else {
					success('user has been logged out');
				}

			}
		}
	);


Another way to make calls for the app user is to simply change the auth method on the existing client object:

	client.authType = usergrid.APP_USER;

After this statement is called, all calls will use the user token instead of the client secret / id combo (application level).  To switch back, just enable the client secret/id combo instead:

	client.authType = usergrid.AUTH_CLIENT_ID;

**Note:** you can also set the client to use no authentication.  You would use this method if you are using the default Sandbox app that was automatically created when your account was set up:

	client.authType = usergrid.AUTH_NONE;

With this setting enabled, no authentication will be provided to the database. You will likely only use this setting if you are testing with the Sandbox app.

To recap, once a user has been logged in, and an OAuth token has been acquired, use one of the two methods to make calls to the API: 1.) use the same client object and change auth types before each call, or, 2.) make a new client object for user calls.  Either method will work.


###To log a user out
To log the user out, call:

	client.logout();

Or, if you made a new client object specifically for the app user:

	appUserClient.logout();

This destroys the token and user object in the client object, effectively logging the user out.


##Samples / Tests
There is significant coverage of this Usergrid module in the test directory. These files are coded to run under the mocha.

For runnable samples, please see the test.js file in the root of the project.  This file covers all the sample code in this readme file and should be run under node:

	$node test.js



## Contributing
We welcome your enhancements!

Like [Usergrid](https://github.com/apigee/usergrid-node-module), the Usergrid Node module is open source and licensed under the Apache License, Version 2.0.

1. Fork it
2. Create your feature branch (`git checkout -b my-new-feature`)
3. Commit your changes (`git commit -am 'Added some feature'`)
4. Push your changes to the upstream branch (`git push origin my-new-feature`)
5. Create new Pull Request (make sure you describe what you did and why your mod is needed)

##More information
For more information on Apigee App Services, visit <http://apigee.com/about/developers>.

## Copyright
Copyright 2012 Apigee Corporation

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

<http://www.apache.org/licenses/LICENSE-2.0>

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.