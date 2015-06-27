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
var PORT = 40003;

app.get('/', function(req, res)
{
	res.end("CouponSystem Start Port " + PORT);
});

//?�도 경도 Tilt �?보내�?그에 맞는 쿠폰 리스?��? 보내준??
app.get('/backgroundCouponGetList', function(req, res)
{
	var parsedUrl = url.parse(req.url, true);
	var queryAsObject = parsedUrl.query;
	var jsonData = JSON.parse(JSON.stringify(queryAsObject));

	var result = new Array();

	if(jsonData.session == "" || jsonData.lat == "" || jsonData.lng == "" || jsonData.tilt == ""||
		jsonData.session == null || jsonData.lat == null || jsonData.lng == null || jsonData.tilt == null)
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
		db.createCollection('userinfo', function(err, collection)
		{
			assert.equal(null, err);
			collection.findOne({session:jsonData.session, api:{$in: ['basic','facebook']} },function(err, doc)
			{
				assert.equal(null, err);
				if(doc != null)
				{

					db.createCollection('coupon', function(err, collection)
					{
						assert.equal(null, err);
						collection.find({active:true}).toArray(function(err, coupons)
						{
							assert.equal(null, err);
							var docs = new Array();
							for(var i in coupons)
							{
								var v = coupons[i];
								if(v.tilt == jsonData.tilt)
								{
									console.log(v);
									switch(v.category)
									{
										case 'gps':
										{

											if(getDistanceGPS(parseFloat(v.lat), parseFloat(v.lng), parseFloat(jsonData.lat), parseFloat(jsonData.lng)) <= 50)
											{
												docs.push(v);
											}
											break;
										}
										case 'time':
										{
											var now = new Date(0, 0, 0, new Date().getHours(), new Date().getMinutes());
											var beginDate = new Date(0, 0, 0, parseInt(v.beginT.substring(0,2),10), parseInt(v.beginT.substring(3,5),10)),
											endDate = new Date(0, 0, 0, parseInt(v.endT.substring(0,2),10), parseInt(v.endT.substring(3,5),10));
											if(beginDate <= now && endDate >= now)
											{
												docs.push(v);
											}
											break;
										}
										default:
										{

										}
									}
								}
							}
							if(docs.length == 0)
							{
								result = {
									code 	: -2,
									message : "Not match coupon"
								}
								res.end(JSON.stringify(result));
							}
							else
							{
								result = {
									code 	: 1,
									message : "Success return coupons",
									coupons : docs
								}
								res.end(JSON.stringify(result));
							}
						});
					});
				}
				else
				{
					result = {
						code 	: -3,
						message : "Invalidate session"
					}
					res.end(JSON.stringify(result));
					db.close();
				}
			});
		});
	});
});

var rad = function(val)
{
	return val * Math.PI / 180;
};

var getDistanceGPS = function(lat1, lng1, lat2, lng2)
{
	var R = 6378137; // Earth?�s mean radius in meter

	var dLat = rad(lat2 - lat1);
	var dLong = rad(lng2 - lng1);

	var a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
	Math.cos(rad(lat1)) * Math.cos(rad(lat2)) *
	Math.sin(dLong / 2) * Math.sin(dLong / 2);

	var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

	var d = R * c;
	return d; // returns the distance in meter
};

http.createServer(app).listen(PORT,function()
{
	console.log('Server Running ' + PORT);
});
