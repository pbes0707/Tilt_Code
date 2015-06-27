var http = require("http");
var url = require("url");
var fs = require('fs');
var multer  = require('multer')
var events = require('events');
var crypto = require('crypto');
var express = require('express');
var bodyParser = require('body-parser');

var Db = require('mongodb').Db,
MongoClient = require('mongodb').MongoClient,
Server = require('mongodb').Server,
ReplSetServers = require('mongodb').ReplSetServers,
ObjectID = require('mongodb').ObjectID,
Binary = require('mongodb').Binary,
GridStore = require('mongodb').GridStore,
Grid = require('mongodb').Grid,
Code = require('mongodb').Code,
assert = require('assert');


var app = express();

app.use(bodyParser());
app.use( bodyParser.json() );       // to support JSON-encoded bodies
app.use(bodyParser.urlencoded({     // to support URL-encoded bodies
	extended: true
}));
app.use(multer({ dest: './temp'}))
events.EventEmitter.defaultMaxListeners = 500;


var db = new Db('TiltCode', new Server('127.0.0.1', 27017));
var PORT = 40004;

app.get('/', function(req, res)
{
	res.end("Analytics And Setting Start Port " + PORT);
});

// 버전 체크
app.get('/checkVersion', function(req, res)
{
	var result = new Array();

	db.open(function(err, db)
	{
		assert.equal(null, err);
		db.createCollection('setting', function(err, collection)
		{
			assert.equal(null, err);
			collection.findOne({}, function(err, doc)
			{
				assert.equal(null, err);
				result = {
					code 	: 1,
					message : "Success check version",
					version : doc['version']
				}
				res.end(JSON.stringify(result));
				db.close();
			});
		});
	});

});

// 최신 버전 갱신
app.post('/changeVersion', function(req, res)
{
	var param = req.body;

	var result = new Array();

	if(param.version == "" ||
		param.version == null )
	{
		result = {
			code 	: -1,
			message : "You have not written entry"
		}
		res.end(JSON.stringify(result));
		return;
	}
	db.open(function(err, db)
	{
		assert.equal(null, err);
		db.createCollection('setting', function(err, collection)
		{
			assert.equal(null, err);
			collection.update({},{$set:{version:param.version}}, function(err, doc)
			{
				assert.equal(null, err);
				result = {
					code 	: 1,
					message : "Success update version"
				}
				res.end(JSON.stringify(result));
				db.close();
			});
		});
	});

});

http.createServer(app).listen(PORT,function()
{
	console.log('Server Running ' + PORT);
});