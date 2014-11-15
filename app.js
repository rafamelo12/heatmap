/*jshint node:true*/

// app.js

var express = require('express');
var ibmdb = require('ibm_db');
var fs = require('fs');
var path = require('path')
// setup middleware
var app = express();
app.use(app.router);
app.use(express.errorHandler());
app.use(express.static(__dirname + '/public')); //setup static public directory
app.set('view engine', 'ejs');
app.set('views', __dirname + '/views'); //optional since express defaults to CWD/views

// There are many useful environment variables available in process.env.
// VCAP_APPLICATION contains useful information about a deployed application.
var appInfo = JSON.parse(process.env.VCAP_APPLICATION || "{}");
// TODO: Get application information and use it in your app.

// VCAP_SERVICES contains all the credentials of services bound to
// this application. For details of its content, please refer to
// the document or sample of each service.
var services = JSON.parse(process.env.VCAP_SERVICES || "{}");
// TODO: Get service credentials and communicate with bluemix services.
if(process.env.VCAP_SERVICES){
	var env = JSON.parse(process.env.VCAP_SERVICES);
	var db2 = env['sqldb'][0].credentials;
}
var connString = "DRIVER={DB2};DATABASE=" + db2.db + ";UID=" + db2.username + ";PWD=" + db2.password + ";HOSTNAME=" + db2.hostname + ";port=" + db2.port;
// ROUTES
app.get('/', function(req, res){
	res.render('index.ejs');
});
app.get('/admin', function(req, res){
	ibmdb.open(connString, function(err, conn){
		if(err){
			res.send("Error occurred: ", err.message);
		}else{
			var query = 'SELECT * FROM TESTDATA';
			conn.query(query, function(err, tables, moreResultsSets){
				if(!err){
					var data = [];
					var count = 0;
					var key2;
					var filePath = path.join(__dirname, '/public/js/ourData.js');
					var wstream = fs.createWriteStream(filePath);
					wstream.write('var quakePoints = [\n');
					for (var key in tables){
						key2 = key;
						key2++;
						if(!tables[key2]){
							data[count] = '['+tables[key]['X']+','+tables[key]['Y']+','+tables[key]['INTENSITY']+']\n';
							wstream.write(data[count]);
						}else{
							data[count] = '['+tables[key]['X']+','+tables[key]['Y']+','+tables[key]['INTENSITY']+'],\n';
							wstream.write(data[count]);	
						}
						count++;
					}
					wstream.write('];');
					wstream.end(function (){
						res.render('admin.ejs');
					});
				}else{
					res.send("Error occurred: ", err.message);		
				}
				conn.close(function(){
					console.log("Connection closed");
				});
			});
		}
	});
});
// The IP address of the Cloud Foundry DEA (Droplet Execution Agent) that hosts this application:
var host = (process.env.VCAP_APP_HOST || 'localhost');
// The port on the DEA for communication with the application:
var port = (process.env.VCAP_APP_PORT || 3000);
// Start server
app.listen(port, host);
console.log('App started on port ' + port);

