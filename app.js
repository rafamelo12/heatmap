/*jshint node:true*/

// app.js

var express = require('express');
var ibmdb = require('ibm_db');
var fs = require('fs');
var path = require('path');

// setup middleware
var app = express();
app.use(express.bodyParser());
// app.use(bodyParser.urlencoded({ extended: true}));
// app.use(bodyParser.json());
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
app.get('/test', function(req, res){
	ibmdb.open(connString, function(err, conn){
		if(err){
			res.send('Error: '+ err)
		}else{
			var query = 'SELECT * FROM TESTDATA';
			conn.query(query, function(err, tables, moreResultsSets){
				var data = [];
				console.log(tables.length);
				var filePath = path.join(__dirname, '/public/js/ourData.js');
				var wstream = fs.createWriteStream(filePath);
				wstream.write("var heat1=[\n");
				if(!err){
					for(var j = 0; j < tables.length; j++){
						if(j < parseInt(tables.length/3)){
							if(j == ((tables.length/3)-1)){
								data[j] = '['+tables[j]['X']+','+tables[j]['Y']+']\n';
								wstream.write(data[j]);	
								wstream.write('];\n');
								wstream.write('var heat2=[\n');
							}else{
								data[j] = '['+tables[j]['X']+','+tables[j]['Y']+'],\n';
								wstream.write(data[j]);
								
							}	
						}else{
							if(j < parseInt((tables.length/3)*2)){
								
								if(j == (((tables.length/3)*2)-1)){
									data[j] = '['+tables[j]['X']+','+tables[j]['Y']+']\n';
									wstream.write(data[j]);	
									wstream.write('];\n');
									wstream.write('var heat3=[\n');
								}else{
									data[j] = '['+tables[j]['X']+','+tables[j]['Y']+'],\n';
									wstream.write(data[j]);
									
								}
							}else{
								if(j >= parseInt((tables.length/3)*2)){
									
									if(j == (tables.length-1)){
										data[j] = '['+tables[j]['X']+','+tables[j]['Y']+']\n';
										wstream.write(data[j]);	
									}else{
										data[j] = '['+tables[j]['X']+','+tables[j]['Y']+'],\n';
										wstream.write(data[j]);
									}
								}
							}
						}


						// if(i == 0){
						// 	for(var j = 0; j < parseInt(tables.length/3); j++){
						// 		if(j == ((tables.length/3)-1)){
						// 			data[j] = '['+tables[j]['X']+','+tables[j]['Y']+']\n';
						// 			wstream.write(data[j]);	
						// 		}else{
						// 			data[j] = '['+tables[j]['X']+','+tables[j]['Y']+'],\n';
						// 			wstream.write(data[j]);
						// 		}
						// 	}
						// }else{
						// 	if(i == 1){
						// 		wstream.write('];\n');
						// 		wstream.write('var heat2=[\n');
						// 		for(var k = 0; k < parseInt((tables.length/3)*2); k++){
						// 			if(k == (((tables.length/3)*2)-1)){
						// 				data[k] = '['+tables[k]['X']+','+tables[k]['Y']+']\n';
						// 				wstream.write(data[k]);	
						// 			}else{
						// 				data[k] = '['+tables[k]['X']+','+tables[k]['Y']+'],\n';
						// 				wstream.write(data[k]);
						// 			}
						// 		}
						// 	}else{
						// 		if(i == 2){
						// 			wstream.write('];\n');
						// 			wstream.write('var heat3=[\n');
						// 			for(var l = 0; l < tables.length; l++){
						// 				if(l == (tables.length-1)){
						// 					data[l] = '['+tables[l]['X']+','+tables[l]['Y']+']\n';
						// 					wstream.write(data[l]);	
						// 				}else{
						// 					data[l] = '['+tables[l]['X']+','+tables[l]['Y']+'],\n';
						// 					wstream.write(data[l]);
						// 				}
										
						// 			}
						// 		}
						// 	}
						// }
					}
					wstream.write('];');
					wstream.end(function(){
						res.render('admin.ejs');
					});
				}else{
					res.send('error: '+err);
				}
			});
		}
	});
});
app.get('/admin', function(req, res){
	ibmdb.open(connString, function(err, conn){
		if(err){
			res.send("Error occurred: "+ err.message);
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
app.post('/collectData', function(req, res){
	var x = req.body['x'];
	var y = req.body['y'];
	var query = "INSERT INTO FAKEDATA (x, y) VALUES("+x+","+y+")";
	ibmdb.open(connString, function(err, conn){
		if(err){
			res.send("Error occurred: ", err.message);
		}else{
			conn.query(query, function(err, tables, moreResultsSets){
				if(err){
					res.send("Error occured: ", err.message);
				}else{
					res.send('YAY! INSERTED INTO THE DB2 :) ');  
				}
			});
		}
		conn.close(function(){
			console.log('Connection closed!');
		})
	});
});
// The IP address of the Cloud Foundry DEA (Droplet Execution Agent) that hosts this application:
var host = (process.env.VCAP_APP_HOST || 'localhost');
// The port on the DEA for communication with the application:
var port = (process.env.VCAP_APP_PORT || 3000);
// Start server
app.listen(port, host);
console.log('App started on port ' + port);

